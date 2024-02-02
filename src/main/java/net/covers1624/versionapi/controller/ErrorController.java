package net.covers1624.versionapi.controller;

import net.covers1624.versionapi.security.InsufficientPermissionsException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Created by covers1624 on 2/2/24.
 */
@RestControllerAdvice
public class ErrorController {

    @ExceptionHandler
    public ResponseEntity<Object> insufficientPermissions(InsufficientPermissionsException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ex.getMessage());
    }
}
