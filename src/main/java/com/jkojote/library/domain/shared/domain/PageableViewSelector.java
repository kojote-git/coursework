package com.jkojote.library.domain.shared.domain;

import java.util.List;
import java.util.function.Predicate;

public interface PageableViewSelector<View extends ViewObject> extends ViewSelector<View> {

    List<View> findAll(int page, int pageSize);

    List<View> findAll(SqlPageSpecification pageSpecification);

    List<View> findAll(int page, int pageSize, Predicate<View> predicate);

}
