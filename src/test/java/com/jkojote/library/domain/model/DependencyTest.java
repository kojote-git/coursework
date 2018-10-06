package com.jkojote.library.domain.model;

import com.jkojote.library.config.TestConfig;
import com.jkojote.library.persistence.entities.mappers.AuthorMapper;
import com.jkojote.library.persistence.entities.mappers.WorkMapper;
import com.jkojote.library.persistence.internals.fetchers.LazyAuthorListFetcher;
import com.jkojote.library.persistence.internals.fetchers.LazySubjectListFetcher;
import com.jkojote.library.persistence.internals.fetchers.LazyWorkListFetcher;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
public class DependencyTest {

    @Autowired
    private ApplicationContext ctx;

    @Test
    public void test() {
        LazySubjectListFetcher subjectListFetcher = ctx.getBean(LazySubjectListFetcher.class);
        LazyWorkListFetcher lazyWorkListFetcher  = ctx.getBean(LazyWorkListFetcher.class);
        LazyAuthorListFetcher lazyAuthorListFetcher = ctx.getBean(LazyAuthorListFetcher.class);
        AuthorMapper authorMapper = ctx.getBean(AuthorMapper.class);
        WorkMapper workMapper = ctx.getBean(WorkMapper.class);

        assertEquals(lazyWorkListFetcher.getWorkMapper(), workMapper);
        assertEquals(lazyAuthorListFetcher.getAuthorMapper(), authorMapper);
        assertEquals(workMapper.getLazySubjectListFetcher(), subjectListFetcher);
    }
}