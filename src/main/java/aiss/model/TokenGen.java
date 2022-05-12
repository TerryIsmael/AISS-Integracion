package aiss.model;

import java.util.UUID;

public class TokenGen {
    public static String RandomToken() {
        return UUID.randomUUID().toString();
    }
}
