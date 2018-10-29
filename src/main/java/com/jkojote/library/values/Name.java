package com.jkojote.library.values;

import java.util.Objects;

import static com.google.common.base.Preconditions.checkNotNull;

public final class Name extends ValueObject {

    public static final Name EMPTY = Name.of("", "", "");

    private String firstName;

    private String lastName;

    private String middleName;

    public static Name of(String firstName, String middleName, String lastName) {
        checkNotNull(firstName);
        checkNotNull(middleName);
        checkNotNull(lastName);
        if (firstName.length() == 0 && middleName.length() == 0 && lastName.length() == 0)
            return EMPTY;
        return new Name(firstName, middleName, lastName);
    }

    public static Name of(String firstName, String lastName) {
        checkNotNull(firstName);
        checkNotNull(lastName);
        return new Name(firstName, "", lastName);
    }

    private Name(String firstName, String middleName, String lastName) {
        this.firstName = firstName;
        this.middleName = middleName;
        this.lastName = lastName;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj instanceof Name) {
            Name that = (Name) obj;
            return firstName.equals(that.firstName) &&
                   middleName.equals(that.middleName) &&
                   lastName.equals(that.lastName);
        }
        return false;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public String getLastName() {
        return lastName;
    }

    @Override
    public int hashCode() {
        return Objects.hash(firstName, middleName, lastName);
    }
}
