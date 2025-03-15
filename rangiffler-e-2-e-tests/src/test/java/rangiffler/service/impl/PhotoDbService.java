package rangiffler.service.impl;



import rangiffler.db.model.PhotoEntity;
import rangiffler.db.repository.CountryRepository;
import rangiffler.db.repository.CountryRepositoryJdbc;
import rangiffler.db.repository.PhotoRepository;
import rangiffler.db.repository.PhotoRepositoryJdbc;
import rangiffler.mapper.PhotoMapper;
import rangiffler.model.testdata.TestPhoto;
import rangiffler.service.PhotoService;

import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public class PhotoDbService implements PhotoService {

  private final PhotoRepository photoRepository = new PhotoRepositoryJdbc();
  private final CountryRepository countryRepository = new CountryRepositoryJdbc();

  @Override
  public TestPhoto createPhoto(UUID userId, String countryCode, String image, String description) {
    var country = countryRepository.findRequiredCountryByCode(countryCode);

    var photoEntity = new PhotoEntity();
    photoEntity.setUserId(userId);
    photoEntity.setCountryId(country.getId());
    photoEntity.setDescription(description);
    photoEntity.setPhoto(getImageAsBase64(image).getBytes(StandardCharsets.UTF_8));
    photoEntity.setCreatedDate(Timestamp.from(Instant.now()));

    photoEntity = photoRepository.createPhoto(photoEntity);
    return PhotoMapper.toTestPhoto(photoEntity);
  }

  @Override
  public void deletePhoto(UUID id) {
    photoRepository.deletePhoto(id);
  }

  @Override
  public void likePhoto(UUID userId, UUID photoId) {
    photoRepository.likePhoto(userId, photoId, LocalDate.now());
  }
}
