package rangiffler.service;

import io.grpc.Status;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.advice.GrpcAdvice;
import net.devh.boot.grpc.server.advice.GrpcExceptionHandler;
import rangiffler.exception.CountryNotFoundException;

/**
 * Централизованный обработчик ошибок для gRPC сервиса.
 * Обрабатывает исключения, выбрасываемые в сервисе, и возвращает соответствующие статусы.
 */
@Slf4j
@GrpcAdvice
public class GrpcErrorHandler {

    /**
     * Обработчик для исключений CountryNotFoundException.
     * Возвращает статус NOT_FOUND с описанием ошибки.
     *
     * @param e Исключение типа CountryNotFoundException
     * @return Статус NOT_FOUND с описанием ошибки
     */
    @GrpcExceptionHandler(CountryNotFoundException.class)
    public Status handleCountryNotFoundException(final CountryNotFoundException e) {
        log.warn("Страна не найдена: {}", e.getMessage());  // Логирование предупреждения
        return Status.NOT_FOUND.withDescription(e.getMessage());  // Ответ с кодом 404
    }

    /**
     * Обработчик для всех других исключений.
     * Возвращает статус INTERNAL с описанием ошибки.
     *
     * @param e Общее исключение
     * @return Статус INTERNAL с сообщением о внутренней ошибке сервера
     */
    @GrpcExceptionHandler(Exception.class)
    public Status handleGeneralException(final Exception e) {
        log.error("Внутренняя ошибка сервера: {}", e.getMessage(), e);  // Логирование ошибки с подробностями
        return Status.INTERNAL.withDescription("Внутренняя ошибка сервера");  // Ответ с кодом 500
    }
}
