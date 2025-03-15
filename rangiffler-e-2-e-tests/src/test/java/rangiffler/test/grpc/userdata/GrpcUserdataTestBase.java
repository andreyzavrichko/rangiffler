package rangiffler.test.grpc.userdata;


import org.junit.jupiter.api.BeforeEach;
import rangiffler.config.Config;
import rangiffler.grpc.ChannelProvider;
import rangiffler.grpc.RangifflerUserdataServiceGrpc;
import rangiffler.jupiter.annotation.meta.GrpcTest;

@GrpcTest
public abstract class GrpcUserdataTestBase {

  protected static final Config CFG = Config.getInstance();
  protected RangifflerUserdataServiceGrpc.RangifflerUserdataServiceBlockingStub blockingStub;

  @BeforeEach
  void before() {
    var channel = ChannelProvider.INSTANCE.channel(CFG.userdataHost(), CFG.userdataPort());
    blockingStub = RangifflerUserdataServiceGrpc.newBlockingStub(channel);
  }
}
