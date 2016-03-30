package org.codingdojo.exception;

/**
 * User: JRI <julien.ripault@atos.net>
 * Date: 29/03/2016
 */
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
