package com.jkojote.library.domain.shared.domain;


import com.jkojote.library.clauses.SqlClause;

import java.util.List;
import java.util.function.Predicate;

public interface ViewSelector<View extends ViewObject> {

    List<View> selectAll();

    List<View> select(Predicate<View> predicate);

    List<View> select(SqlClause clause);
}
