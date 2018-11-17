package com.jkojote.library.domain.model;

import com.jkojote.library.clauses.SortOrder;
import com.jkojote.library.clauses.SqlClause;
import com.jkojote.library.clauses.mysql.MySqlClauseBuilder;
import com.jkojote.library.config.tests.ForRepositories;
import com.jkojote.library.domain.model.author.Author;
import com.jkojote.library.domain.shared.SqlPageSpecificationImpl;
import com.jkojote.library.domain.shared.domain.PageableRepository;
import com.jkojote.library.domain.shared.domain.SqlPageSpecification;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Comparator;
import java.util.List;

import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = ForRepositories.class)
@DirtiesContext
public class PageableAuthorRepositoryTest {

    @Autowired
    private PageableRepository<Author> authorRepository;

    @Test
    public void findAll() {
        SqlClause sqlClause = new MySqlClauseBuilder()
                .orderBy("firstName", SortOrder.ASC)
                .thenOrderBy("middleName", SortOrder.ASC)
                .thenOrderBy("lastName", SortOrder.ASC)
                .build();
        SqlPageSpecification page1Spec =
                new SqlPageSpecificationImpl(sqlClause, 3, 1);
        SqlPageSpecification page2Spec =
                new SqlPageSpecificationImpl(sqlClause, 3, 2);
        SqlPageSpecification page3Spec =
                new SqlPageSpecificationImpl(sqlClause, 3, 3);
        Comparator<Author> comparator = Comparator.comparing(a -> a.getName().getFirstName());
        List<Author> page1 = authorRepository.findAll(page1Spec);
        List<Author> page2 = authorRepository.findAll(page2Spec);
        List<Author> page3 = authorRepository.findAll(page3Spec);
        assertSortedAsc(page1, comparator);
        assertSortedAsc(page2, comparator);
        assertSortedAsc(page3, comparator);
    }

    private <T> void assertSortedAsc(List<T> list, Comparator<T> comparator) {
        for (int i = 0; i < list.size() - 1; i++) {
            T first = list.get(i);
            T second = list.get(i + 1);
            assertTrue(comparator.compare(first, second) <= 0);
        }
    }
    private <T> void assertSortedDesc(List<T> list, Comparator<T> comparator) {
        for (int i = 0; i < list.size() - 1; i++) {
            T first = list.get(i);
            T second = list.get(i);
            assertTrue(comparator.compare(first, second) > 0);
        }
    }
}
