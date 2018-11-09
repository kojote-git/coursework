package com.jkojote.library.persistence.mappers;

import com.jkojote.library.domain.model.reader.Download;
import com.jkojote.library.domain.model.reader.Reader;
import com.jkojote.library.persistence.ListFetcher;
import com.jkojote.library.persistence.lazy.LazyListImpl;
import com.jkojote.types.Email;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;

import static com.jkojote.library.domain.model.reader.Reader.ReaderBuilder;

@Component("readerMapper")
class ReaderMapper implements RowMapper<Reader> {

    private ListFetcher<Reader, Download> downloadListFetcher;

    @Qualifier("downloadsFetcher")
    public void setDownloadListFetcher(ListFetcher<Reader, Download> downloadListFetcher) {
        this.downloadListFetcher = downloadListFetcher;
    }

    @Override
    public Reader mapRow(ResultSet rs, int rowNum) throws SQLException {
        long id = rs.getLong("id");
        String password = rs.getString("password");
        Email email = Email.of(rs.getString("email"));
        LazyListImpl<Reader, Download> list = new LazyListImpl<>(downloadListFetcher);
        LocalDateTime registered = rs.getTimestamp("timeRegistered").toLocalDateTime();
        Reader reader = ReaderBuilder.aReader()
                .withId(id)
                .withEncryptedPassword(password)
                .withEmail(email)
                .withDownloads(list)
                .withTimeRegistered(registered)
                .build();
        list.setParentEntity(reader);
        list.seal();
        return reader;
    }
}
