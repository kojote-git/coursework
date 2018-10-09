package com.jkojote.library.domain.model.publisher;

import java.util.List;

public interface PublisherRepository {

    Publisher findById(long id);

    List<Publisher> findAll();

    boolean save(Publisher publisher);

    boolean remove(Publisher publisher);

    boolean update(Publisher publisher);

    boolean exists(Publisher publisher);
}
