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

  private Status handleExceptionInternal(Exception e, Status status) {
    log.error(e.getMessage(), e);
    return status.withDescription(e.getMessage()).withCause(e);
  }

  @GrpcExceptionHandler(PhotoNotFoundException.class)
  public Status handlePhotoNotFoundException(final PhotoNotFoundException e) {
    return handleExceptionInternal(e, Status.NOT_FOUND);
  }

  @GrpcExceptionHandler(IllegalPhotoAccessException.class)
  public Status handleIllegalPhotoAccessException(final IllegalPhotoAccessException e) {
    return handleExceptionInternal(e, Status.PERMISSION_DENIED);
  }

  @GrpcExceptionHandler(StatisticNotFoundException.class)
  public Status handleStatisticNotFoundException(final StatisticNotFoundException e) {
    return handleExceptionInternal(e, Status.NOT_FOUND);
  }

  @GrpcExceptionHandler(Exception.class)
  public Status handleException(final Exception e) {
    return handleExceptionInternal(e, Status.ABORTED);
  }
}
