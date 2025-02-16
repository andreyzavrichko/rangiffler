package rangiffler.data.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import rangiffler.data.CountryEntity;

import java.util.Optional;
import java.util.UUID;

public interface CountryRepository extends JpaRepository<CountryEntity, UUID> {

  Optional<CountryEntity> findByCode(String code);
}
