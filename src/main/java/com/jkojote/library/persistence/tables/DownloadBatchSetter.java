package com.jkojote.library.persistence.tables;

import com.jkojote.library.domain.model.reader.Download;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Iterator;

class DownloadBatchSetter implements BatchPreparedStatementSetter {

    private Collection<Download> downloads;

    private Iterator<Download> iterator;

    public DownloadBatchSetter(Collection<Download> downloads) {
        this.downloads = downloads;
        this.iterator = downloads.iterator();
    }

    @Override
    public void setValues(PreparedStatement ps, int i) throws SQLException {
        Download d = iterator.next();
        ps.setLong(1, d.getReader().getId());
        ps.setLong(2, d.getInstance().getId());
        ps.setInt(3, d.getReaderRating());
    }

    @Override
    public int getBatchSize() {
        return downloads.size();
    }
}
