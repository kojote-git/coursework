package com.jkojote.library.persistence.tables;

import com.jkojote.library.domain.model.reader.Rating;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Iterator;


public class RatingBatchSetter implements BatchPreparedStatementSetter {

    private Collection<Rating> ratings;

    private Iterator<Rating> iterator;

    public RatingBatchSetter(Collection<Rating> ratings) {
        this.ratings = ratings;
        this.iterator = this.ratings.iterator();
    }

    @Override
    public void setValues(PreparedStatement ps, int i) throws SQLException {
        Rating rating = iterator.next();
        ps.setLong(1, rating.getBook().getId());
        ps.setLong(2, rating.getReader().getId());
        ps.setInt(3, rating.getRating());
    }

    @Override
    public int getBatchSize() {
        return ratings.size();
    }
}
