package com.jkojote.library.domain.shared;

import java.util.ArrayList;

public class EntityArrayList<T extends DomainEntity> extends ArrayList<T>
implements EntityList<T> {
    @Override
    public boolean isFetched() {
        return true;
    }
}
