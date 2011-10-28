package eel.kitchen.jsonschema.v2.schema;

public final class ValidationType
{
    public static final int
        ANY = 1 << 0,
        ALL = 1 << 1,
        NEGATE = 1 << 2;

    private static final int SET = ANY | ALL;
}
