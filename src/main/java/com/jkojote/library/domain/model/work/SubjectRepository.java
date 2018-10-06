package com.jkojote.library.domain.model.work;

public interface SubjectRepository {

    int exists(Subject subject);

    int save(Subject subject);

    void remove(Subject subject);
}
