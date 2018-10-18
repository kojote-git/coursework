package com.jkojote.library.domain.model;

import com.jkojote.library.config.tests.ForRepositories;
import com.jkojote.library.domain.model.author.Author;
import com.jkojote.library.domain.model.work.Work;
import com.jkojote.library.persistence.ListFetcher;
import com.jkojote.library.persistence.lazy.LazyListImpl;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = ForRepositories.class)
@DirtiesContext
public class RefreshableTest {

    @Autowired
    private ListFetcher<Work, Author> listFetcher;

    @Test
    public void refresh() {
        Work work = mock(Work.class);
        when(work.getId()).thenReturn(1L);
        LazyListImpl list = new LazyListImpl<>(work, listFetcher);
        int size = list.size();
        list.remove(0);
        assertEquals(size - 1, list.size());
        list.refresh();
        assertEquals(size, list.size());
    }
}
