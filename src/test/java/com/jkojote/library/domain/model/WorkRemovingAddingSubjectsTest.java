package com.jkojote.library.domain.model;

import com.jkojote.library.config.tests.ForRepositories;
import com.jkojote.library.domain.model.work.Subject;
import com.jkojote.library.domain.model.work.SubjectTable;
import com.jkojote.library.domain.model.work.Work;
import com.jkojote.library.domain.shared.domain.DomainRepository;
import com.jkojote.library.persistence.BridgeTableProcessor;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = ForRepositories.class)
@DirtiesContext
public class WorkRemovingAddingSubjectsTest {

    @Autowired
    @Qualifier("workRepository")
    private DomainRepository<Work> workRepository;


    @Autowired
    private SubjectTable subjectTable;

    @Autowired
    @Qualifier("workSubjectBridge")
    private BridgeTableProcessor<Work, Subject> workSubjectBridge;

    @Test
    public void addSubject_AddsSubjectAndCorrespondingRecordIsSavedToDatabase() {
        Work w = workRepository.findById(1);
        Subject s = Subject.of("Random");
        w.addSubject(s);
        int subjectId = subjectTable.exists(s);
        assertNotEquals(-1, subjectId);
        assertTrue(workSubjectBridge.exists(w, s));
    }

    @Test
    public void removeSubject_RemovesSubject() {
        Work w = workRepository.findById(1);
        Subject s = Subject.of("Biology");
        int subjectId = subjectTable.exists(s);
        assertNotEquals(-1, subjectId);
        w.removeSubject(s);
        assertFalse(workSubjectBridge.exists(w, s));
    }
}
