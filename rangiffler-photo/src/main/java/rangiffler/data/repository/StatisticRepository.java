package rangiffler.data.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import rangiffler.data.GroupedStatistic;
import rangiffler.data.StatisticEntity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface StatisticRepository extends JpaRepository<StatisticEntity, UUID> {

  @Query("select new rangiffler.data.GroupedStatistic(s.countryId, sum(s.count)) "
      + "from StatisticEntity s "
      + "where s.userId in :userIds "
      + "group by s.countryId")
  List<GroupedStatistic> countStatistic(List<UUID> userIds);

  Optional<StatisticEntity> findByUserIdAndCountryId(UUID userId, UUID countryId);
}
