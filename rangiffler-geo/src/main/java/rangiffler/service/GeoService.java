package rangiffler.service;

import com.google.protobuf.Empty;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.beans.factory.annotation.Autowired;
import rangiffler.data.repository.CountryRepository;
import rangiffler.exception.CountryNotFoundException;
import rangiffler.grpc.*;
import rangiffler.mapper.CountryMapper;

import java.util.UUID;

/**
 * Сервис для работы с географической информацией через gRPC.
 * Предоставляет методы для получения информации о странах по ID, коду и для получения всех стран.
 */
@Slf4j
@GrpcService
public class GeoService extends RangifflerGeoServiceGrpc.RangifflerGeoServiceImplBase {

    private final CountryRepository countryRepository;

    /**
     * Конструктор сервиса для работы с географической информацией.
     *
     * @param countryRepository Репозиторий для работы с сущностями стран.
     */
    @Autowired
    public GeoService(CountryRepository countryRepository) {
        this.countryRepository = countryRepository;
    }

    /**
     * Метод для получения списка всех стран.
     *
     * @param request Пустой запрос.
     * @param responseObserver Наблюдатель для отправки ответа.
     */
    @Override
    public void getAllCountries(Empty request, StreamObserver<AllCountriesResponse> responseObserver) {
        try {
            var responseBuilder = AllCountriesResponse.newBuilder();
            countryRepository.findAll().forEach(country -> responseBuilder.addAllCountries(CountryMapper.toMessage(country)));

            responseObserver.onNext(responseBuilder.build());
            responseObserver.onCompleted();
        } catch (Exception e) {
            log.error("Ошибка при получении всех стран", e);
            responseObserver.onError(Status.INTERNAL.withDescription("Ошибка сервера").withCause(e).asRuntimeException());
        }
    }

    /**
     * Метод для получения информации о стране по её ID.
     *
     * @param request Запрос, содержащий ID страны.
     * @param responseObserver Наблюдатель для отправки ответа.
     */
    @Override
    public void getCountry(GetCountryRequest request, StreamObserver<Country> responseObserver) {
        try {
            UUID countryId = UUID.fromString(request.getId());
            var countryEntity = countryRepository.findById(countryId)
                    .orElseThrow(() -> new CountryNotFoundException(request.getId()));

            responseObserver.onNext(CountryMapper.toMessage(countryEntity));
            responseObserver.onCompleted();
        } catch (IllegalArgumentException e) {
            log.warn("Передан некорректный UUID: {}", request.getId());
            responseObserver.onError(Status.INVALID_ARGUMENT.withDescription("Некорректный формат UUID").asRuntimeException());
        } catch (CountryNotFoundException e) {
            log.warn("Страна не найдена: {}", request.getId());
            responseObserver.onError(Status.NOT_FOUND.withDescription("Страна не найдена").asRuntimeException());
        } catch (Exception e) {
            log.error("Ошибка при получении страны с ID {}", request.getId(), e);
            responseObserver.onError(Status.INTERNAL.withDescription("Ошибка сервера").withCause(e).asRuntimeException());
        }
    }

    /**
     * Метод для получения информации о стране по её коду.
     *
     * @param request Запрос, содержащий код страны.
     * @param responseObserver Наблюдатель для отправки ответа.
     */
    @Override
    public void getCountryByCode(GetCountryByCodeRequest request, StreamObserver<Country> responseObserver) {
        try {
            var countryEntity = countryRepository.findByCode(request.getCode())
                    .orElseThrow(() -> new CountryNotFoundException(request.getCode()));

            responseObserver.onNext(CountryMapper.toMessage(countryEntity));
            responseObserver.onCompleted();
        } catch (CountryNotFoundException e) {
            log.warn("Страна с кодом '{}' не найдена", request.getCode());
            responseObserver.onError(Status.NOT_FOUND.withDescription("Страна не найдена").asRuntimeException());
        } catch (Exception e) {
            log.error("Ошибка при получении страны с кодом {}", request.getCode(), e);
            responseObserver.onError(Status.INTERNAL.withDescription("Ошибка сервера").withCause(e).asRuntimeException());
        }
    }
}
