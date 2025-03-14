package rangiffler.jupiter.extension;


import rangiffler.grpc.ChannelProvider;

public class GrpcChannelExtension implements SuiteExtension {

  @Override
  public void afterSuite() {
    ChannelProvider.INSTANCE.closeAllChannels();
  }
}
