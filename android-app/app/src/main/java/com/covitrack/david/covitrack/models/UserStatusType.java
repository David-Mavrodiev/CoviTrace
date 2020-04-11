package com.covitrack.david.covitrack.models;

public enum UserStatusType {
    CONTACT(1), INFECTED(2), NONE(3);
    private int value;

    UserStatusType(int flag) {
        this.value = flag;
    }

    public int getValue() {
        return value;
    }
}
