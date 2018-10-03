package com.jkojote.library.persistence.entities.work;

import com.jkojote.library.domain.model.work.Work;
import com.jkojote.library.domain.model.work.WorkRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class WorkRepositoryImpl implements WorkRepository {
    @Override
    public Work findById(long id) {
        return null;
    }

    @Override
    public List<Work> findAll() {
        return null;
    }

    @Override
    public boolean exists(Work work) {
        return false;
    }

    @Override
    public long nextId() {
        return 0;
    }

    @Override
    public boolean save(Work work) {
        return false;
    }

    @Override
    public boolean update(Work work) {
        return false;
    }

    @Override
    public boolean delete(Work work) {
        return false;
    }
}
