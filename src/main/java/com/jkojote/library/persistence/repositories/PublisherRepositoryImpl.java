package com.jkojote.library.persistence.repositories;

import com.jkojote.library.domain.model.publisher.Publisher;
import com.jkojote.library.domain.model.publisher.PublisherRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional
public class PublisherRepositoryImpl implements PublisherRepository {

    @Override
    public Publisher findById(long id) {
        return null;
    }

    @Override
    public List<Publisher> findAll() {
        return null;
    }

    @Override
    public boolean save(Publisher publisher) {
        return false;
    }

    @Override
    public boolean remove(Publisher publisher) {
        return false;
    }

    @Override
    public boolean update(Publisher publisher) {
        return false;
    }

    @Override
    public boolean exists(Publisher publisher) {
        return false;
    }
}
