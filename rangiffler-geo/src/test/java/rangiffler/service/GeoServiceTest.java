package rangiffler.service;

import io.grpc.stub.StreamObserver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import rangiffler.data.CountryEntity;
import rangiffler.data.repository.CountryRepository;
import rangiffler.grpc.AllCountriesResponse;
import rangiffler.grpc.Country;
import rangiffler.grpc.GetCountryByCodeRequest;
import rangiffler.grpc.GetCountryRequest;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GeoServiceTest {

    @InjectMocks
    private GeoService geoService;

    @Mock
    private CountryRepository countryRepository;

    @Mock
    private StreamObserver<AllCountriesResponse> allCountriesResponseObserver;

    @Mock
    private StreamObserver<Country> countryResponseObserver;

    private CountryEntity mockCountryEntity;

    @BeforeEach
    void setup() {
        mockCountryEntity = new CountryEntity();
        mockCountryEntity.setId(UUID.randomUUID());
        mockCountryEntity.setCode("US");
        mockCountryEntity.setName("United States");
        mockCountryEntity.setFlag(new byte[]{1, 2, 3});
    }

    @Test
    void shouldGetAllCountries() {
        when(countryRepository.findAll()).thenReturn(List.of(mockCountryEntity));

        geoService.getAllCountries(null, allCountriesResponseObserver);

        verify(allCountriesResponseObserver).onNext(any(AllCountriesResponse.class));
        verify(allCountriesResponseObserver).onCompleted();
    }

    @Test
    void shouldHandleGetAllCountriesError() {
        when(countryRepository.findAll()).thenThrow(new RuntimeException("Database error"));

        geoService.getAllCountries(null, allCountriesResponseObserver);

        verify(allCountriesResponseObserver).onError(argThat(e -> e instanceof io.grpc.StatusRuntimeException));
    }

    @Test
    void shouldGetCountryById() {
        UUID countryId = mockCountryEntity.getId();
        GetCountryRequest request = GetCountryRequest.newBuilder().setId(countryId.toString()).build();
        when(countryRepository.findById(countryId)).thenReturn(Optional.of(mockCountryEntity));

        geoService.getCountry(request, countryResponseObserver);

        verify(countryResponseObserver).onNext(any(Country.class));
        verify(countryResponseObserver).onCompleted();
    }

    @Test
    void shouldHandleCountryNotFoundById() {
        UUID countryId = UUID.randomUUID();
        GetCountryRequest request = GetCountryRequest.newBuilder().setId(countryId.toString()).build();
        when(countryRepository.findById(countryId)).thenReturn(Optional.empty());

        geoService.getCountry(request, countryResponseObserver);

        verify(countryResponseObserver).onError(argThat(e -> e instanceof io.grpc.StatusRuntimeException));
    }

    @Test
    void shouldGetCountryByCode() {
        String code = "US";
        GetCountryByCodeRequest request = GetCountryByCodeRequest.newBuilder().setCode(code).build();
        when(countryRepository.findByCode(code)).thenReturn(Optional.of(mockCountryEntity));

        geoService.getCountryByCode(request, countryResponseObserver);

        verify(countryResponseObserver).onNext(any(Country.class));
        verify(countryResponseObserver).onCompleted();
    }

    @Test
    void shouldHandleCountryNotFoundByCode() {
        String code = "US";
        GetCountryByCodeRequest request = GetCountryByCodeRequest.newBuilder().setCode(code).build();
        when(countryRepository.findByCode(code)).thenReturn(Optional.empty());

        geoService.getCountryByCode(request, countryResponseObserver);

        verify(countryResponseObserver).onError(argThat(e -> e instanceof io.grpc.StatusRuntimeException));
    }

    @Test
    void shouldHandleInvalidUUIDFormat() {
        String invalidUUID = "invalid-uuid";
        GetCountryRequest request = GetCountryRequest.newBuilder().setId(invalidUUID).build();

        geoService.getCountry(request, countryResponseObserver);

        verify(countryResponseObserver).onError(argThat(e -> e instanceof io.grpc.StatusRuntimeException));
    }
}
