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
    public Status handleCountryNotFoundException(final CountryNotFoundException e) {
        log.warn("Страна не найдена: {}", e.getMessage());
        return Status.NOT_FOUND.withDescription(e.getMessage());
    }

    @GrpcExceptionHandler(Exception.class)
    public Status handleGeneralException(final Exception e) {
        log.error("Внутренняя ошибка сервера: {}", e.getMessage(), e);
        return Status.INTERNAL.withDescription("Внутренняя ошибка сервера");
    }
}
