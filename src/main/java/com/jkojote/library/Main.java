package com.jkojote.library;

import com.jkojote.library.config.PersistenceConfig;
import com.jkojote.library.domain.model.author.Author;
import com.jkojote.library.domain.shared.domain.DomainRepository;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class Main {
    @SuppressWarnings("unchecked")
    public static void main(String[] args) {
        AnnotationConfigApplicationContext ctx =
                new AnnotationConfigApplicationContext(PersistenceConfig.class);
        DomainRepository<Author> authorRepository =
                ctx.getBean("authorRepository", DomainRepository.class);
    }
}
