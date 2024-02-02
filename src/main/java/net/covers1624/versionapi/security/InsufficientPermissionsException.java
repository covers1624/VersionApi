package net.covers1624.versionapi.security;

/**
 * Created by covers1624 on 2/2/24.
 */
public class InsufficientPermissionsException extends RuntimeException {

    public InsufficientPermissionsException(String message) {
        super(message);
    }

    public InsufficientPermissionsException(String message, Throwable cause) {
        super(message, cause);
    }
}
