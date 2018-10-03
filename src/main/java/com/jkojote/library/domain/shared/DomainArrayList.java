package com.jkojote.library.domain.shared;

import com.jkojote.library.persistence.DomainList;

import java.util.ArrayList;

public class DomainArrayList<T extends DomainObject> extends ArrayList<T>
implements DomainList<T> {

}
