package com.jkojote.library.domain.model.work;

public interface SubjectTable {

    /**
     * Check if subject {@code subject} exists in table
     * @param subject
     * @return id of the subject; -1 if such a record doesn't exist yet
     */
    int exists(Subject subject);

    /**
     * Saves subject into database and returns its id
     * @param subject
     * @return id of the subject
     */
    int save(Subject subject);

    void remove(Subject subject);
}
