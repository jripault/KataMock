package org.codingdojo.exception;

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
