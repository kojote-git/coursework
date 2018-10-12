package com.jkojote.library.persistence.repositories;

import com.jkojote.library.domain.model.author.Author;
import com.jkojote.library.domain.model.work.Subject;
import com.jkojote.library.domain.model.work.Work;
import com.jkojote.library.domain.shared.Utils;
import com.jkojote.library.domain.shared.domain.DomainEventListener;
import com.jkojote.library.domain.shared.domain.DomainRepository;
import com.jkojote.library.persistence.BridgeTableProcessor;
import com.jkojote.library.persistence.LazyList;
import com.jkojote.library.persistence.TableProcessor;
import com.jkojote.library.persistence.listeners.AuthorStateListener;
import com.jkojote.library.persistence.listeners.WorkStateListener;
import com.jkojote.library.persistence.tables.WorkTableProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

@Component
@Transactional
@SuppressWarnings("Duplicates")
class CascadeWorkAuthorPersistence {

    private DomainEventListener<Author> authorStateListener;

    private DomainEventListener<Work> workStateListener;

    private BridgeTableProcessor<Work, Author> workAuthorBridge;

    private BridgeTableProcessor<Work, Subject> workSubjectBridge;

    private TableProcessor<Author> authorTable;

    private TableProcessor<Work> workTable;

    @Autowired
    public CascadeWorkAuthorPersistence(
                @Qualifier("WorkAuthor")
                BridgeTableProcessor<Work, Author> workAuthorBridgeTableProcessor,
                @Qualifier("WorkSubject")
                BridgeTableProcessor<Work, Subject> workSubjectBridgeTableProcessor,
                TableProcessor<Author> authorTableProcessor,
                TableProcessor<Work> workWorkTableProcessor) {
        this.workAuthorBridge = workAuthorBridgeTableProcessor;
        this.workSubjectBridge = workSubjectBridgeTableProcessor;
        this.authorTable = authorTableProcessor;
        this.workTable = workWorkTableProcessor;
    }


    @Autowired
    public void setAuthorStateListener(DomainEventListener<Author> authorStateListener) {
        this.authorStateListener = authorStateListener;
    }

    @Autowired
    public void setWorkStateListener(DomainEventListener<Work> workStateListener) {
        this.workStateListener = workStateListener;
    }

    void saveAuthor(Author author) {
        saveAuthor(author, new HashSet<>(), new HashSet<>());
    }

    void saveWork(Work work) {
        saveWork(work, new HashSet<>(), new HashSet<>());
        work.addEventListener(workStateListener);
    }

    public void updateWork(Work work) {
        updateWork(work, new HashSet<>(), new HashSet<>());
    }

    public void updateAuthor(Author author) {
        updateAuthor(author, new HashSet<>(), new HashSet<>());
    }

    private void updateWork(Work work, Set<Long> updatedAuthors, Set<Long> updatedWorks) {
        if (updatedWorks.contains(work.getId()))
            return;
        workTable.update(work);
        updatedWorks.add(work.getId());
        var authors = work.getAuthors();
        var authorsIsFetched = !(authors instanceof LazyList) || ((LazyList<Author>) authors).isFetched();
        if (!authorsIsFetched)
            return;
        for (var author : authors) {
            if (updatedAuthors.contains(author.getId()))
                continue;
            if (!authorTable.exists(author))
                saveAuthor(author);
            else
                updateAuthor(author, updatedAuthors, updatedWorks);
        }
    }

    private void updateAuthor(Author author, Set<Long> updatedAuthors, Set<Long> updatedWorks) {
        if (updatedAuthors.contains(author.getId()))
            return;
        authorTable.update(author);
        updatedAuthors.add(author.getId());
        var works = author.getWorks();
        var worksIsLazyList = works instanceof LazyList;
        var worksIsFetched = !worksIsLazyList || ((LazyList) works).isFetched();
        if (!worksIsFetched)
            return;
        for (var work : works) {
            if (updatedWorks.contains(work.getId()))
                continue;
            if (!workTable.exists(work))
                saveWork(work);
            else
                updateWork(work, updatedAuthors, updatedWorks);
        }
    }

    private void saveAuthor(Author author, Set<Long> savedAuthors, Set<Long> savedWorks) {
        if (savedAuthors.contains(author.getId()))
            return;
        if (!authorTable.exists(author)) {
            authorTable.insert(author);
            author.addEventListener(authorStateListener);
        }
        else {
            savedAuthors.add(author.getId());
            return;
        }
        savedAuthors.add(author.getId());
        var works = author.getWorks();
        var worksIsLazyList = works instanceof LazyList;
        var worksIsFetched = !worksIsLazyList || ((LazyList) works).isFetched();
        if (!worksIsFetched)
            return;
        for (var work : works) {
            if (savedWorks.contains(work.getId()))
                continue;
            saveWork(work, savedAuthors, savedWorks);
            workAuthorBridge.addRecord(work, author);
        }
    }

    private void saveWork(Work work, Set<Long> savedAuthors, Set<Long> savedWorks) {
        if (savedWorks.contains(work.getId()))
            return;
        if (!workTable.exists(work)) {
            workTable.insert(work);
            work.addEventListener(workStateListener);
        }
        else {
            savedWorks.add(work.getId());
            return;
        }
        savedWorks.add(work.getId());
        var authors = work.getAuthors();
        var authorsIsLazyList = authors instanceof LazyList;
        var authorsIsFetched = !authorsIsLazyList || ((LazyList) authors).isFetched();
        if (authorsIsFetched) {
            for (var author : authors) {
                if (savedAuthors.contains(author.getId()))
                    continue;
                saveAuthor(author, savedAuthors, savedWorks);
                workAuthorBridge.addRecord(work, author);
            }
        }
        saveSubjects(work);
    }

    private void saveSubjects(Work work) {
        var subjects = work.getSubjects();
        var subjectsIsLazyList = subjects instanceof LazyList;
        var subjectsIsFetched = !subjectsIsLazyList || ((LazyList) subjects).isFetched();
        if (!subjectsIsFetched)
            return;
        for (var subject : subjects) {
            workSubjectBridge.addRecord(work, subject);
        }
    }
}
