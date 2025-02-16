package rangiffler.service;



import io.grpc.Status;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.advice.GrpcAdvice;
import net.devh.boot.grpc.server.advice.GrpcExceptionHandler;
import rangiffler.exception.CountryNotFoundException;

@Slf4j
@GrpcAdvice
public class GrpcErrorHandler {

  @GrpcExceptionHandler(CountryNotFoundException.class)
  public Status handleNoSuchElementException(final CountryNotFoundException e) {
    log.error(e.getMessage(), e);
    return Status.NOT_FOUND.withDescription(e.getMessage()).withCause(e);
  }

  @GrpcExceptionHandler(Exception.class)
  public Status handleException(final Exception e) {
    log.error(e.getMessage(), e);
    return Status.ABORTED.withDescription(e.getMessage()).withCause(e);
  }
}
