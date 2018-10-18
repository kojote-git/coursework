package com.jkojote.library.persistence.repositories;

import com.jkojote.library.domain.model.book.instance.BookInstance;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Iterator;

class BookInstancesBatchSetter implements BatchPreparedStatementSetter {

    static final String STATEMENT =
        "INSERT INTO BookInstance (id, isbn13, bookId, format, file) " +
          "VALUES (?, ?, ?, ?, ?)";

    private Iterator<BookInstance> iterator;

    private int size;

    BookInstancesBatchSetter(Collection<BookInstance> bookInstances) {
        this.size = bookInstances.size();
        this.iterator = bookInstances.iterator();
    }

    @Override
    public void setValues(PreparedStatement ps, int i) throws SQLException {
        BookInstance instance = iterator.next();
        ps.setLong(1, instance.getId());
        ps.setString(2, instance.getIsbn13().asString());
        ps.setLong(3, instance.getBook().getId());
        ps.setString(4, instance.getFormat().asString());
        ps.setBlob(5, instance.getFile().asBlob());
    }

    @Override
    public int getBatchSize() {
        return size;
    }
}
