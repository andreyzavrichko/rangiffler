package rangiffler.service;



import io.grpc.Status;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.advice.GrpcAdvice;
import net.devh.boot.grpc.server.advice.GrpcExceptionHandler;
import rangiffler.exception.IllegalPhotoAccessException;
import rangiffler.exception.PhotoNotFoundException;
import rangiffler.exception.StatisticNotFoundException;

@Slf4j
@GrpcAdvice
public class GrpcErrorHandler {

  @GrpcExceptionHandler(PhotoNotFoundException.class)
  public Status handlePhotoNotFoundException(final PhotoNotFoundException e) {
    log.error(e.getMessage(), e);
    return Status.NOT_FOUND.withDescription(e.getMessage()).withCause(e);
  }

  @GrpcExceptionHandler(IllegalPhotoAccessException.class)
  public Status handleIllegalPhotoAccessException(final IllegalPhotoAccessException e) {
    log.error(e.getMessage(), e);
    return Status.PERMISSION_DENIED.withDescription(e.getMessage()).withCause(e);
  }

  @GrpcExceptionHandler(StatisticNotFoundException.class)
  public Status handleStatisticNotFoundException(final StatisticNotFoundException e) {
    log.error(e.getMessage(), e);
    return Status.NOT_FOUND.withDescription(e.getMessage()).withCause(e);
  }

  @GrpcExceptionHandler(Exception.class)
  public Status handleException(final Exception e) {
    log.error(e.getMessage(), e);
    return Status.ABORTED.withDescription(e.getMessage()).withCause(e);
  }
}
