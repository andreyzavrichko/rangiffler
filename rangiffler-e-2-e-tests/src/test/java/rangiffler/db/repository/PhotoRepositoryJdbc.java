package rangiffler.db.repository;


import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.JdbcTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;
import rangiffler.db.DataSourceProvider;
import rangiffler.db.JdbcUrl;
import rangiffler.db.mapper.PhotoEntityRowMapper;
import rangiffler.db.model.LikeEntity;
import rangiffler.db.model.PhotoEntity;
import rangiffler.db.model.StatisticEntity;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

public class PhotoRepositoryJdbc implements PhotoRepository {

  private final JdbcTemplate photoTemplate;
  private final NamedParameterJdbcTemplate namedPhotoTemplate;
  private final TransactionTemplate photoTxt;

  public PhotoRepositoryJdbc() {
    var photoTm = new JdbcTransactionManager(DataSourceProvider.INSTANCE.dataSource(JdbcUrl.PHOTO));
    this.photoTxt = new TransactionTemplate(photoTm);
    this.photoTemplate = new JdbcTemplate(Objects.requireNonNull(photoTm.getDataSource()));
    this.namedPhotoTemplate = new NamedParameterJdbcTemplate(photoTemplate);
  }

  @Override
  public PhotoEntity createPhoto(PhotoEntity photo) {
    return photoTxt.execute(status -> {
      var kh = new GeneratedKeyHolder();
      photoTemplate.update(con -> {
        PreparedStatement ps = con.prepareStatement(
            "INSERT INTO photo "
                + "(user_id, country_id, description, photo, created_date) "
                + "VALUES(?, ?, ?, ?, ?)",
            PreparedStatement.RETURN_GENERATED_KEYS
        );
        ps.setObject(1, photo.getUserId());
        ps.setObject(2, photo.getCountryId());
        ps.setString(3, photo.getDescription());
        ps.setBytes(4, photo.getPhoto());
        ps.setTimestamp(5, photo.getCreatedDate());
        return ps;
      }, kh);

      photo.setId((UUID) Objects.requireNonNull(kh.getKeys()).get("id"));

      var statistic = findStatisticByUserIdAndCountryId(photo.getUserId(), photo.getCountryId());
      if (statistic.isPresent()) {
        updateStatisticByUserIdAndCountryId(photo.getUserId(), photo.getCountryId(), statistic.get().getCount() + 1);
      } else {
        createStatisticByUserIdAndCountryId(photo.getUserId(), photo.getCountryId(), 1);
      }

      return photo;
    });
  }

  @Override
  public void deletePhoto(UUID id) {
    var optionalPhoto = findPhotoById(id);
    optionalPhoto.ifPresent(photoEntity -> photoTxt.execute(status -> {
          var statistic = findStatisticByUserIdAndCountryId(photoEntity.getUserId(),
              photoEntity.getCountryId()).orElseThrow();

          if (statistic.getCount() - 1 == 0) {
            deleteStatisticByUserIdAndCountryId(photoEntity.getUserId(), photoEntity.getCountryId());
          } else {
            updateStatisticByUserIdAndCountryId(photoEntity.getUserId(), photoEntity.getCountryId(),
                statistic.getCount() - 1);
          }

          var likesIds = findLikesIdsByPhotoId(id);
          photoTemplate.update("DELETE FROM \"photo_like\" WHERE photo_id = ?", id);
          deleteLikesByIds(likesIds);
          photoTemplate.update("DELETE FROM \"photo\" WHERE id = ?", id);
          return null;
        })
    );
  }

  @Override
  public PhotoEntity findRequiredPhotoById(UUID photoId) {
    return photoTemplate.queryForObject("SELECT * FROM \"photo\" WHERE id = ?", new PhotoEntityRowMapper(), photoId);
  }

  @Override
  public Optional<PhotoEntity> findPhotoById(UUID photoId) {
    try {
      return Optional.ofNullable(
          photoTemplate.queryForObject("SELECT * FROM \"photo\" WHERE id = ?", new PhotoEntityRowMapper(), photoId)
      );
    } catch (EmptyResultDataAccessException e) {
      return Optional.empty();
    }
  }

  @Override
  public Boolean isPhotoExists(UUID photoId) {
    return photoTemplate.queryForObject("SELECT EXISTS(SELECT * FROM \"photo\" WHERE id = ?)", Boolean.class, photoId);
  }

  @Override
  public List<PhotoEntity> findByUserId(UUID userId) {
    return photoTemplate.query("SELECT * FROM \"photo\" WHERE user_id = ?", new PhotoEntityRowMapper(), userId);
  }

  @Override
  public Optional<StatisticEntity> findStatisticByUserIdAndCountryId(UUID userId, UUID countryId) {
    try {
      return Optional.ofNullable(
          photoTemplate.queryForObject("SELECT * FROM \"statistic\" WHERE user_id = ? and country_id = ?",
              (ResultSet rs, int rowNum) -> {
                var statisticEntity = new StatisticEntity();
                statisticEntity.setId(rs.getObject("id", UUID.class));
                statisticEntity.setUserId(rs.getObject("user_id", UUID.class));
                statisticEntity.setCountryId(rs.getObject("country_id", UUID.class));
                statisticEntity.setCount(rs.getInt("count"));
                return statisticEntity;
              },
              userId, countryId
          )
      );
    } catch (EmptyResultDataAccessException e) {
      return Optional.empty();
    }
  }

  @Override
  public void updateStatisticByUserIdAndCountryId(UUID userId, UUID countryId, Integer count) {
    photoTemplate.update("UPDATE \"statistic\" SET count = ? WHERE user_id = ? and country_id = ?",
        count, userId, countryId);
  }

  @Override
  public void createStatisticByUserIdAndCountryId(UUID userId, UUID countryId, Integer count) {
    photoTemplate.update("INSERT INTO \"statistic\" (user_id, country_id, count) VALUES(?, ?, ?)",
        userId, countryId, count);
  }

  @Override
  public void deleteStatisticByUserIdAndCountryId(UUID userId, UUID countryId) {
    photoTemplate.update("DELETE FROM \"statistic\" WHERE user_id = ? and country_id = ?", userId, countryId);
  }

  @Override
  public void likePhoto(UUID userId, UUID photoId, LocalDate createdDate) {
    photoTxt.execute(status -> {
      var kh = new GeneratedKeyHolder();
      photoTemplate.update(con -> {
        PreparedStatement ps = con.prepareStatement(
            "INSERT INTO \"like\" (user_id, created_date) VALUES(?, ?)",
            PreparedStatement.RETURN_GENERATED_KEYS
        );
        ps.setObject(1, userId);
        ps.setDate(2, Date.valueOf(createdDate));
        return ps;
      }, kh);

      photoTemplate.update("INSERT INTO \"photo_like\" (photo_id, like_id) VALUES(?, ?)",
          photoId, Objects.requireNonNull(kh.getKeys()).get("id"));

      return null;
    });
  }

  @Override
  public List<UUID> findLikesIdsByPhotoId(UUID photoId) {
    return photoTemplate.queryForList("SELECT like_id FROM \"photo_like\" WHERE photo_id = ?",
        UUID.class,
        photoId
    );
  }

  @Override
  public List<LikeEntity> findLikesByPhotoId(UUID photoId) {
    return photoTemplate.query(
        "SELECT * FROM \"photo_like\" pl JOIN \"like\" l ON l.id = pl.like_id  WHERE photo_id = ?",
        (ResultSet rs, int rowNum) -> {
          var likeEntity = new LikeEntity();
          likeEntity.setId(rs.getObject("id", UUID.class));
          likeEntity.setUserId(rs.getObject("user_id", UUID.class));
          likeEntity.setCreatedDate(rs.getTimestamp("created_date"));
          return likeEntity;
        },
        photoId
    );
  }

  @Override
  public void deleteLikesByIds(List<UUID> likeIds) {
    if (!likeIds.isEmpty()) {
      var parameters = new MapSqlParameterSource("ids", likeIds);
      namedPhotoTemplate.update("DELETE FROM \"like\" WHERE id IN (:ids)", parameters);
    }
  }
}
