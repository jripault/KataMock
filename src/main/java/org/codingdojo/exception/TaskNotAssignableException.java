package org.codingdojo.exception;

/**
 * User: JRI <julien.ripault@atos.net>
 * Date: 29/03/2016
 */
public class TaskNotAssignableException extends RuntimeException {
    public TaskNotAssignableException() {
        super();
    }

    public TaskNotAssignableException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public TaskNotAssignableException(final String message) {
        super(message);
    }

    public TaskNotAssignableException(final Throwable cause) {
        super(cause);
    }
}
