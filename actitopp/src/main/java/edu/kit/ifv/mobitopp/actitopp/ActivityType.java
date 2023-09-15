package edu.kit.ifv.mobitopp.actitopp;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

public enum ActivityType {

    WORK('W', 1),
    EDUCATION('E', 3),
    LEISURE('L', 5),
    SHOPPING('S', 4),
    TRANSPORT('T', 11),
    HOME('H', 7),
    UNKNOWN('x', 8);


    public static final Set<ActivityType> OUTOFHOMEACTIVITY;
    public static final Set<ActivityType> FULLSET;

    static {
        OUTOFHOMEACTIVITY = Collections.unmodifiableSet(EnumSet.of(
                ActivityType.WORK,
                ActivityType.EDUCATION,
                ActivityType.LEISURE,
                ActivityType.SHOPPING,
                ActivityType.TRANSPORT
        ));

        FULLSET = Collections.unmodifiableSet(EnumSet.of(
                ActivityType.WORK,
                ActivityType.EDUCATION,
                ActivityType.LEISURE,
                ActivityType.SHOPPING,
                ActivityType.TRANSPORT,
                ActivityType.HOME
        ));
    }


    private char charValue;
    private int code;

    private ActivityType(char charValue, int code) {
        this.charValue = charValue;
        this.code = code;
    }

    public char getTypeasChar() {
        return this.charValue;
    }

    public int getCode() {
        return this.code;
    }

    public static ActivityType getTypeFromChar(char charValue) {
        Character tocompare = Character.toUpperCase(charValue);

        for (ActivityType type : EnumSet.allOf(ActivityType.class)) {
            if (type.getTypeasChar() == tocompare) {
                return type;
            }
        }
        return null;
    }
}
