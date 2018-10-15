package com.jkojote.library.persistence.tables;

import com.jkojote.library.domain.model.book.Book;
import com.jkojote.library.persistence.TableProcessor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional
public class BookTableProcessor implements TableProcessor<Book> {
    @Override
    public boolean exists(Book e) {
        return false;
    }

    @Override
    public boolean insert(Book e) {
        return false;
    }

    @Override
    public boolean delete(Book e) {
        return false;
    }

    @Override
    public boolean update(Book e) {
        return false;
    }
}
