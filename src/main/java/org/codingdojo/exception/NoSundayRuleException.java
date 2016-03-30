package org.codingdojo.exception;

public class NoSundayRuleException extends RuntimeException {
    public NoSundayRuleException() {
        super();
    }

    public NoSundayRuleException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public NoSundayRuleException(final String message) {
        super(message);
    }

    public NoSundayRuleException(final Throwable cause) {
        super(cause);
    }
}
