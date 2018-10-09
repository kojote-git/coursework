package com.jkojote.library.persistence.repositories;

import com.jkojote.library.domain.model.publisher.Publisher;
import com.jkojote.library.domain.shared.domain.DomainRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional
public class PublisherRepository implements DomainRepository<Publisher> {

    @Override
    public Publisher findById(long id) {
        return null;
    }

    @Override
    public List<Publisher> findAll() {
        return null;
    }

    @Override
    public long nextId() {
        return 0;
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
