package dev.rogerbertan.cashflow.infra.exception;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import dev.rogerbertan.cashflow.infra.dto.ErrorResponse;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

    @InjectMocks private GlobalExceptionHandler handler;

    @Test
    void handleAICategorizeException_ShouldReturn503_WithErrorMessage() {
        AICategorizeException ex = new AICategorizeException("AI categorize failed");

        ResponseEntity<ErrorResponse> result = handler.handleAICategorizeException(ex);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.SERVICE_UNAVAILABLE);
        assertThat(result.getBody()).isNotNull();
        assertThat(result.getBody().error()).isEqualTo("AI categorize failed");
    }

    @Test
    void handleAIInsightsException_ShouldReturn503_WithErrorMessage() {
        AIInsightsException ex = new AIInsightsException("AI insights failed");

        ResponseEntity<ErrorResponse> result = handler.handleAIInsightsException(ex);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.SERVICE_UNAVAILABLE);
        assertThat(result.getBody()).isNotNull();
        assertThat(result.getBody().error()).isEqualTo("AI insights failed");
    }

    @Test
    void handleBudgetPlannerException_ShouldReturn404_WhenResourceNotFound() {
        ResourceNotFoundException ex = new ResourceNotFoundException("Category", "id 1");

        ResponseEntity<ErrorResponse> result = handler.handleBudgetPlannerException(ex);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(result.getBody()).isNotNull();
        assertThat(result.getBody().error()).contains("Category");
    }

    @Test
    void handleBudgetPlannerException_ShouldReturn400_WhenInvalidTransaction() {
        InvalidTransactionException ex = new InvalidTransactionException("Invalid amount");

        ResponseEntity<ErrorResponse> result = handler.handleBudgetPlannerException(ex);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(result.getBody()).isNotNull();
        assertThat(result.getBody().error()).isEqualTo("Invalid amount");
    }

    @Test
    void handleValidationException_ShouldReturn400_WithFieldErrors() {
        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = mock(BindingResult.class);
        FieldError fieldError = new FieldError("obj", "name", "must not be blank");
        when(ex.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getFieldErrors()).thenReturn(List.of(fieldError));

        ResponseEntity<ErrorResponse> result = handler.handleValidationException(ex);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(result.getBody()).isNotNull();
        assertThat(result.getBody().error()).isEqualTo("name: must not be blank");
    }

    @Test
    void handleValidationException_ShouldFormatMultipleFieldErrors_WhenMultipleViolations() {
        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = mock(BindingResult.class);
        FieldError error1 = new FieldError("obj", "name", "must not be blank");
        FieldError error2 = new FieldError("obj", "type", "must not be null");
        when(ex.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getFieldErrors()).thenReturn(List.of(error1, error2));

        ResponseEntity<ErrorResponse> result = handler.handleValidationException(ex);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(result.getBody()).isNotNull();
        assertThat(result.getBody().error()).contains("name: must not be blank");
        assertThat(result.getBody().error()).contains("type: must not be null");
    }

    @Test
    void handleTypeMismatchException_ShouldReturn400_WithTypeName_WhenRequiredTypeIsNotNull() {
        MethodArgumentTypeMismatchException ex = mock(MethodArgumentTypeMismatchException.class);
        when(ex.getName()).thenReturn("period");
        when(ex.getRequiredType()).thenReturn((Class) String.class);

        ResponseEntity<ErrorResponse> result = handler.handleTypeMismatchException(ex);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(result.getBody()).isNotNull();
        assertThat(result.getBody().error())
                .isEqualTo("Parameter 'period' should be of type String");
    }

    @Test
    void
            handleTypeMismatchException_ShouldReturn400_WithInvalidTypeMessage_WhenRequiredTypeIsNull() {
        MethodArgumentTypeMismatchException ex = mock(MethodArgumentTypeMismatchException.class);
        when(ex.getName()).thenReturn("period");
        when(ex.getRequiredType()).thenReturn(null);

        ResponseEntity<ErrorResponse> result = handler.handleTypeMismatchException(ex);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(result.getBody()).isNotNull();
        assertThat(result.getBody().error()).isEqualTo("Parameter 'period' has an invalid type");
    }

    @Test
    void handleHttpMessageNotReadableException_ShouldReturn400_WithMalformedJsonMessage() {
        HttpMessageNotReadableException ex = mock(HttpMessageNotReadableException.class);

        ResponseEntity<ErrorResponse> result = handler.handleHttpMessageNotReadableException(ex);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(result.getBody()).isNotNull();
        assertThat(result.getBody().error()).isEqualTo("Malformed JSON request");
    }

    @Test
    void
            handleMethodNotSupportedException_ShouldReturn405_WithSupportedMethods_WhenMethodsNotNull() {
        HttpRequestMethodNotSupportedException ex =
                mock(HttpRequestMethodNotSupportedException.class);
        when(ex.getMethod()).thenReturn("DELETE");
        when(ex.getSupportedHttpMethods()).thenReturn(Set.of(HttpMethod.GET, HttpMethod.POST));

        ResponseEntity<ErrorResponse> result = handler.handleMethodNotSupportedException(ex);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.METHOD_NOT_ALLOWED);
        assertThat(result.getBody()).isNotNull();
        assertThat(result.getBody().error()).contains("DELETE");
        assertThat(result.getBody().error()).doesNotContain("none");
    }

    @Test
    void handleMethodNotSupportedException_ShouldReturn405_WithNone_WhenSupportedMethodsIsNull() {
        HttpRequestMethodNotSupportedException ex =
                mock(HttpRequestMethodNotSupportedException.class);
        when(ex.getMethod()).thenReturn("PATCH");
        when(ex.getSupportedHttpMethods()).thenReturn(null);

        ResponseEntity<ErrorResponse> result = handler.handleMethodNotSupportedException(ex);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.METHOD_NOT_ALLOWED);
        assertThat(result.getBody()).isNotNull();
        assertThat(result.getBody().error()).contains("none");
    }

    @Test
    void handleMissingServletRequestParameterException_ShouldReturn400_WithParameterName() {
        MissingServletRequestParameterException ex =
                mock(MissingServletRequestParameterException.class);
        when(ex.getParameterName()).thenReturn("period");

        ResponseEntity<ErrorResponse> result =
                handler.handleMissingServletRequestParameterException(ex);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(result.getBody()).isNotNull();
        assertThat(result.getBody().error()).isEqualTo("Required parameter 'period' is missing");
    }

    @Test
    void handleGenericException_ShouldReturn500_WithUnexpectedErrorMessage() {
        RuntimeException ex = new RuntimeException("boom");

        ResponseEntity<ErrorResponse> result = handler.handleGenericException(ex);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(result.getBody()).isNotNull();
        assertThat(result.getBody().error()).isEqualTo("An unexpected error occurred");
    }
}
