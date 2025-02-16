package rangiffler.data.repository;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import rangiffler.data.PhotoEntity;

import java.util.List;
import java.util.UUID;

public interface PhotoRepository extends JpaRepository<PhotoEntity, UUID> {

  Page<PhotoEntity> findByUserId(UUID userId, Pageable pageable);

  Page<PhotoEntity> findByUserIdIn(List<UUID> userId, Pageable pageable);
}
