package photo.service;

import com.google.protobuf.Empty;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.beans.factory.annotation.Autowired;
import photo.data.entity.PhotoEntity;
import photo.data.repository.PhotoRepository;
import photo.ex.PhotoNotFoundException;
import photo.service.api.GeoGrpcClient;
import rangiffler.grpc.*;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@GrpcService
public class PhotoGrpcService extends PhotoServiceGrpc.PhotoServiceImplBase {

    private final PhotoRepository photoRepository;
    private final GeoGrpcClient geoGrpcClient;


    @Autowired
    public PhotoGrpcService(PhotoRepository photoRepository,
                            GeoGrpcClient geoGrpcClient) {
        this.photoRepository = photoRepository;
        this.geoGrpcClient = geoGrpcClient;
    }

    @Override
    public void addPhoto(Photo inputPhoto, StreamObserver<Photo> responseObserver) {
        PhotoEntity entity = photoRepository.save(PhotoEntity.fromGrpc(inputPhoto));
        responseObserver.onNext(getCountryAndConvertToPhoto(entity));
        responseObserver.onCompleted();
    }

    @Override
    public void editPhoto(Photo inputPhoto, StreamObserver<Photo> responseObserver) {
        PhotoEntity inputEntity = getExistsPhotoById(inputPhoto.getId())
                .setDescription(inputPhoto.getDescription())
                .setCountryCode(inputPhoto.getCountry().getCode());
        PhotoEntity entity = photoRepository.save(inputEntity);
        responseObserver.onNext(getCountryAndConvertToPhoto(entity));
        responseObserver.onCompleted();
    }

    @Override
    public void deletePhoto(PhotoIdRequest request, StreamObserver<Empty> responseObserver) {
        photoRepository.deleteById(UUID.fromString(request.getId()));
        responseObserver.onNext(Empty.getDefaultInstance());
        responseObserver.onCompleted();
    }

    @Override
    public void getUserPhotos(UsernameRequest request, StreamObserver<Photos> responseObserver) {
        Set<PhotoEntity> photos = photoRepository.findAllByUsername(request.getUsername());
        responseObserver.onNext(convertToPhotos(photos));
        responseObserver.onCompleted();
    }


    private PhotoEntity getExistsPhotoById(String photoId) {
        UUID id = UUID.fromString(photoId);
        Optional<PhotoEntity> entity = photoRepository.findById(id);
        if (entity.isEmpty()) {
            throw new PhotoNotFoundException(id);
        }
        return entity.get();
    }

    private Photo getCountryAndConvertToPhoto(PhotoEntity entity) {
        Country country = geoGrpcClient.getCountryByCode(entity.getCountryCode());
        return entity.toGrpcWithCountry(country);
    }

    private Photos convertToPhotos(Collection<PhotoEntity> entities) {
        Set<Photo> photos = entities.stream().map(this::getCountryAndConvertToPhoto).collect(Collectors.toSet());
        return Photos.newBuilder().addAllPhotos(photos).build();
    }

}