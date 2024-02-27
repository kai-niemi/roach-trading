package io.roach.product.web;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.hateoas.mediatype.problem.Problem;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;

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
    @RequestMapping("/error")
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
