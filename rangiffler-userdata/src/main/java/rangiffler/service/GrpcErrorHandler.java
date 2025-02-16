package rangiffler.service;



import io.grpc.Status;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.advice.GrpcAdvice;
import net.devh.boot.grpc.server.advice.GrpcExceptionHandler;
import rangiffler.exception.FriendshipNotFoundException;
import rangiffler.exception.FriendshipRequestNotFoundException;
import rangiffler.exception.UserNotFoundException;

@Slf4j
@GrpcAdvice
public class GrpcErrorHandler {

  @GrpcExceptionHandler(FriendshipNotFoundException.class)
  public Status handleFriendshipNotFoundException(final FriendshipNotFoundException e) {
    log.error(e.getMessage(), e);
    return Status.NOT_FOUND.withDescription(e.getMessage()).withCause(e);
  }

  @GrpcExceptionHandler(FriendshipRequestNotFoundException.class)
  public Status handleFriendshipRequestNotFoundException(final FriendshipRequestNotFoundException e) {
    log.error(e.getMessage(), e);
    return Status.NOT_FOUND.withDescription(e.getMessage()).withCause(e);
  }

  @GrpcExceptionHandler(UserNotFoundException.class)
  public Status handleUserNotFoundException(final UserNotFoundException e) {
    log.error(e.getMessage(), e);
    return Status.NOT_FOUND.withDescription(e.getMessage()).withCause(e);
  }

  @GrpcExceptionHandler(Exception.class)
  public Status handleException(final Exception e) {
    log.error(e.getMessage(), e);
    return Status.ABORTED.withDescription(e.getMessage()).withCause(e);
  }
}
