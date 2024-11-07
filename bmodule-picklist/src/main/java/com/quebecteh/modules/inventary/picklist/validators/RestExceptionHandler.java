package com.quebecteh.modules.inventary.picklist.validators;

import java.util.List;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.quebecteh.commons.rest.ApiErrorResponse;
import com.quebecteh.commons.rest.ApiResponse;

/**
 * Global exception handler for RESTful APIs that extends {@link ResponseEntityExceptionHandler} to handle and customize exception responses.
 *
 * <p>This class intercepts exceptions thrown by controller methods and provides custom responses in a consistent format defined by {@link ApiResponse}.</p>
 *
 * <p>It specifically handles:</p>
 * <ul>
 *   <li>{@link ApiValidationFieldException}: Custom exception for validation errors related to business rules.</li>
 *   <li>{@link MethodArgumentNotValidException}: Thrown when validation on an argument annotated with {@code @Valid} fails.</li>
 * </ul>
 *
 * <p>The responses include detailed validation errors encapsulated in {@code ValidationError} objects.</p>
 */
@ControllerAdvice
public class RestExceptionHandler {

    /**
     * Handles {@link ApiValidationFieldException} exceptions and constructs a {@link ResponseEntity} containing an {@link ApiResponse} with validation errors.
     *
     * @param ex the {@code ApiValidationFieldException} containing validation errors.
     * @return a {@code ResponseEntity} with the {@code ApiResponse} detailing the validation errors and an HTTP BAD_REQUEST status.
     */
    @ExceptionHandler(ApiValidationFieldException.class)
    public ResponseEntity<ApiResponse<List<ValidationError>>> handleValidationExceptions(ApiValidationFieldException ex) {
        List<ValidationError> errors = ex.getErrors();

        ApiResponse<List<ValidationError>> response = new ApiErrorResponse<>(
            HttpStatus.BAD_REQUEST.value(),
            "business-rule-validation-error",
            "Business Rule Validation Error",
            errors
        );
        
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
    
    /**
     * Handles {@link HttpException} exceptions thrown by controller methods and constructs
     * a {@link ResponseEntity} containing an {@link ApiResponse} with detailed exception information.
     *
     * <p>This method is annotated with {@code @ExceptionHandler(HttpException.class)}, indicating
     * that it intercepts any {@code HttpException} thrown within the application. It extracts
     * relevant information from the exception, such as the HTTP status code, message ID, and
     * error message, and wraps it into an {@code ApiResponse} object along with an
     * {@link ExceptionDetail} payload.</p>
     *
     * @param ex the {@code HttpException} instance containing details about the error that occurred
     * @return a {@code ResponseEntity} wrapping an {@code ApiResponse} with exception details and the corresponding HTTP status code
     */
    @ExceptionHandler(HttpException.class)
    public ResponseEntity<ApiResponse<ExceptionDetail>> handleHttpException(HttpException ex) {
        

    	ApiResponse<ExceptionDetail> response = new ApiErrorResponse<>(
            ex.getCode(),
            ex.getMessageId(),
            ex.getMessage(),
            getDeatilMapped(ex)
        );

        return new ResponseEntity<>(response, HttpStatus.valueOf(ex.getCode()));
    }

	
    /**
     * Overrides the default {@code handleMethodArgumentNotValid} method to provide a custom response when validation on a method argument fails.
     *
     * @param ex the {@code MethodArgumentNotValidException} containing validation errors.
     * @param headers the {@code HttpHeaders} for the response.
     * @param status the {@code HttpStatusCode} representing the HTTP status code.
     * @param request the {@code WebRequest} during which the exception occurred.
     * @return a {@code ResponseEntity} with the {@code ApiResponse} detailing the validation errors and an HTTP BAD_REQUEST status.
     */
    //@Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {

        ApiResponse<List<ValidationError>> response = new ApiResponse<>(
                HttpStatus.BAD_REQUEST.value(),
                "field-validation-error",
                "Field validation error",
                getErrorsMapped(ex));

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    /**
     * Converts field errors from a {@link MethodArgumentNotValidException} into a list of {@link ValidationError} objects.
     *
     * @param ex the {@code MethodArgumentNotValidException} containing field errors.
     * @return a list of {@code ValidationError} objects representing the field errors.
     */
    private List<ValidationError> getErrorsMapped(MethodArgumentNotValidException ex) {
        List<ValidationError> errors = JsonMapper
                .builder()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .build()
                .convertValue(
                        ex.getBindingResult().getFieldErrors(),
                        new TypeReference<List<ValidationError>>() {}
                );
        return errors;
    }
    
    /**
     * Converts a {@link HttpException} into an {@link ExceptionDetail} object.
     *
     * @param ex the {@code HttpException} containing exception details to be mapped.
     * @return an {@code ExceptionDetail} object representing the details of the exception.
     */
    private ExceptionDetail getDeatilMapped(HttpException ex) {
		return (ExceptionDetail) JsonMapper
								.builder()
								.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
								.build().
								convertValue(ex,  new TypeReference<ExceptionDetailRecord>(){});
	}

}
