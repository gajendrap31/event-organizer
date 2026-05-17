package com.eventorganizer.exception;

import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import com.eventorganizer.payload.response.ErrorResponse;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);
   
    private ResponseEntity<ErrorResponse> buildErrorResponse(String message, HttpStatus status, HttpServletRequest request) {

        ErrorResponse errorResponse = new ErrorResponse(
                LocalDateTime.now(),
                status.value(),
                status.getReasonPhrase(),
                message,
                request.getRequestURI()
        );

        return ResponseEntity
                .status(status)
                .body(errorResponse);
    }
    
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGlobalException(Exception e, HttpServletRequest request) {
    	
    	logger.error("{} {}", e.getClass().getCanonicalName(), e.getMessage());
    	   	
        return buildErrorResponse("An unexpected error occurred. Please try again later.",HttpStatus.INTERNAL_SERVER_ERROR , request);
    }
    
    
    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ErrorResponse> handleNoResourceFoundException(NoResourceFoundException e, HttpServletRequest request) {

    	logger.error("{} {}", e.getClass().getCanonicalName(), e.getMessage());
    	
        return buildErrorResponse("Sorry, we couldn't find the resource you were looking for.", HttpStatus.NOT_FOUND, request);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException e, HttpServletRequest request) {
    	
    	logger.error("{} {}", e.getClass().getCanonicalName(), e.getMessage());
    	
    	String errorMessage = e.getBindingResult().getAllErrors().get(0).getDefaultMessage();
    	if(errorMessage.isBlank()) {
    		errorMessage = "Invalid input. Please provide a valid value.";
    	}
    	
    	return buildErrorResponse(errorMessage, HttpStatus.BAD_REQUEST , request);
    }
    
    @ExceptionHandler({
    	ConstraintViolationException.class,
    	MethodArgumentTypeMismatchException.class,
    	HttpMessageNotReadableException.class
    })
    public ResponseEntity<ErrorResponse> handleValidationExceptions(Exception e, HttpServletRequest request) {

    	logger.error("{} {}", e.getClass().getCanonicalName(), e.getMessage());
    	
        return buildErrorResponse("Invalid input. Please provide a valid value.", HttpStatus.BAD_REQUEST, request);
    }
    
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e, HttpServletRequest request) {
    	
    	logger.error("{} {}", e.getClass().getCanonicalName(), e.getMessage());
    	
    	return buildErrorResponse("The request could not be processed.", HttpStatus.METHOD_NOT_ALLOWED, request);
    }
    
    @ExceptionHandler(AuthorizationDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAuthorizationDeniedException(AuthorizationDeniedException e, HttpServletRequest request) {
    	
    	logger.error("{} {}", e.getClass().getCanonicalName(), e.getMessage());
    	
    	return buildErrorResponse("Access Denied.", HttpStatus.FORBIDDEN, request);
    }
    
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ErrorResponse> handleMaxSizeException(MaxUploadSizeExceededException e, HttpServletRequest request) {
    	
    	logger.error("{} {}", e.getClass().getCanonicalName(), e.getMessage());
    	
        return buildErrorResponse("File too large. Max allowed size is 50MB.", HttpStatus.CONTENT_TOO_LARGE, request);
    }
    
    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ErrorResponse> handleBadRequestException(BadRequestException e, HttpServletRequest request) {
    	
    	logger.error("{} {}", e.getClass().getCanonicalName(), e.getMessage());
    	
    	return buildErrorResponse(e.getMessage(), HttpStatus.BAD_REQUEST, request);
    }
    
    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ErrorResponse> handleConflictException(ConflictException e, HttpServletRequest request) {
    	
    	logger.error("{} {}", e.getClass().getCanonicalName(), e.getMessage());
    	
    	return buildErrorResponse(e.getMessage(), HttpStatus.CONFLICT, request);
    }
    
    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<ErrorResponse> handleForbiddenException(ForbiddenException e, HttpServletRequest request) {
    	
    	logger.error("{} {}", e.getClass().getCanonicalName(), e.getMessage());
    	
    	return buildErrorResponse(e.getMessage(), HttpStatus.FORBIDDEN, request);
    }
    
    @ExceptionHandler(InternalServerErrorException.class)
    public ResponseEntity<ErrorResponse> handleInternalServerErrorException(InternalServerErrorException e, HttpServletRequest request) {
    	
    	logger.error("{} {}", e.getClass().getCanonicalName(), e.getMessage());
    	
    	return buildErrorResponse(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR, request);
    }
    
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFoundException(NotFoundException e, HttpServletRequest request) {
    	
    	logger.error("{} {}", e.getClass().getCanonicalName(), e.getMessage());
    	
    	return buildErrorResponse(e.getMessage(), HttpStatus.NOT_FOUND, request);
    }
    
    @ExceptionHandler(TooManyRequestsException.class)
    public ResponseEntity<ErrorResponse> handleTooManyRequestsException(TooManyRequestsException e, HttpServletRequest request) {
    	
    	logger.error("{} {}", e.getClass().getCanonicalName(), e.getMessage());
    	
    	return buildErrorResponse(e.getMessage(), HttpStatus.TOO_MANY_REQUESTS, request);
    }
    
    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ErrorResponse> handleUnauthorizedException(UnauthorizedException e, HttpServletRequest request) {
    	
    	logger.error("{} {}", e.getClass().getCanonicalName(), e.getMessage());
    	
    	return buildErrorResponse(e.getMessage(), HttpStatus.UNAUTHORIZED, request);
    }
    
    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUsernameNotFoundException(UsernameNotFoundException e, HttpServletRequest request) {
    	
    	logger.error("{} {}", e.getClass().getCanonicalName(), e.getMessage());
    	
    	return buildErrorResponse(e.getMessage(), HttpStatus.UNAUTHORIZED, request);
    }
    
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDataIntegrityViolationException(DataIntegrityViolationException e, HttpServletRequest request) {
    	
    	logger.error("{} {}", e.getClass().getCanonicalName(), e.getMessage());
    	
    	return buildErrorResponse("Your request could not be completed due to a data conflict.", HttpStatus.CONFLICT, request);
    }
    
}
