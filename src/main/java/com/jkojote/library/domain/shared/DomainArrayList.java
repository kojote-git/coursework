package com.jkojote.library.domain.shared;

import com.jkojote.library.domain.shared.domain.DomainObject;
import com.jkojote.library.domain.shared.domain.DomainList;

import java.util.ArrayList;

public class DomainArrayList<T extends DomainObject> extends ArrayList<T>
implements DomainList<T> {

}
