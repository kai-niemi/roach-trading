package io.roach.trading.domain.home;

import java.lang.reflect.UndeclaredThrowableException;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;

import org.springframework.beans.TypeMismatchException;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.core.NestedExceptionUtils;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.hateoas.mediatype.problem.Problem;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import org.springframework.web.util.WebUtils;

/**
 * Centralized REST error handler. All HTTP exceptions should be routed to this handler
 * for uniformed processing and status reporting.
 * <p>
 * Error bodies follow RFC-7807 (https://tools.ietf.org/html/rfc7807) using
 * the vnd/problem+json media type.
 */
@RestControllerAdvice
@Controller
public class RestErrorController extends ResponseEntityExceptionHandler implements ErrorController {
    @RequestMapping("/api/error")
    public ResponseEntity<Object> handleError(HttpServletRequest request) {
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        HttpStatus httpStatus;
        if (status != null) {
            httpStatus = HttpStatus.valueOf(Integer.parseInt(status.toString()));
        } else {
            httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        }
        return wrap(Problem.create()
                .withStatus(httpStatus)
                .withTitle(httpStatus.getReasonPhrase()));
    }

    protected ResponseEntity<Object> wrap(Problem problem) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PROBLEM_JSON);
        return ResponseEntity
                .status(problem.getStatus())
                .headers(headers)
                .body(problem);
    }
}
