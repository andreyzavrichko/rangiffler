package rangiffler.service;

import io.grpc.Status;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import rangiffler.exception.IllegalPhotoAccessException;
import rangiffler.exception.PhotoNotFoundException;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class ErrorHandlingTest {

    @Test
    void shouldHandlePhotoNotFound() {
        GrpcErrorHandler handler = new GrpcErrorHandler();
        Status status = handler.handlePhotoNotFoundException(
                new PhotoNotFoundException("test-id")
        );

        assertEquals(Status.NOT_FOUND.getCode(), status.getCode());
    }

    @Test
    void shouldHandlePermissionDenied() {
        GrpcErrorHandler handler = new GrpcErrorHandler();
        Status status = handler.handleIllegalPhotoAccessException(
                new IllegalPhotoAccessException("photo-id", "user-id")
        );

        assertEquals(Status.PERMISSION_DENIED.getCode(), status.getCode());
    }
}