package rangiffler.data.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import rangiffler.data.LikeEntity;

import java.util.UUID;

public interface LikeRepository extends JpaRepository<LikeEntity, UUID> {

}
