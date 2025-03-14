package rangiffler.test.grpc.photo;


import org.junit.jupiter.api.BeforeEach;
import rangiffler.config.Config;
import rangiffler.grpc.ChannelProvider;
import rangiffler.grpc.RangifflerPhotoServiceGrpc;
import rangiffler.jupiter.annotation.meta.GrpcTest;

@GrpcTest
public abstract class GrpcPhotoTestBase {

  protected static final Config CFG = Config.getInstance();
  protected RangifflerPhotoServiceGrpc.RangifflerPhotoServiceBlockingStub blockingStub;

  @BeforeEach
  void before() {
    var channel = ChannelProvider.INSTANCE.channel(CFG.photoHost(), CFG.photoPort());
    blockingStub = RangifflerPhotoServiceGrpc.newBlockingStub(channel);
  }
}
