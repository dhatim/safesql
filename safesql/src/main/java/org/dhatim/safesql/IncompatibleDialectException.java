package org.dhatim.safesql;

public class IncompatibleDialectException extends RuntimeException {

    public IncompatibleDialectException() {
        super("Not same dialect");
    }

}
