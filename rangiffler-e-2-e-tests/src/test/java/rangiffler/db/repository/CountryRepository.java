package rangiffler.db.repository;



import rangiffler.db.model.CountryEntity;

import java.util.UUID;

public interface CountryRepository {

  CountryEntity findRequiredCountryById(UUID id);

  CountryEntity findRequiredCountryByCode(String code);

  CountryEntity findRequiredCountryByIdNot(UUID id);

  Integer count();
}
