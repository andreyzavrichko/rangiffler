package rangiffler.grpc.interceptor;

import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.MessageOrBuilder;
import com.google.protobuf.util.JsonFormat;
import io.grpc.*;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class GrpcConsoleInterceptor implements ClientInterceptor {

    private static final JsonFormat.Printer JSON_PRINTER = JsonFormat.printer().includingDefaultValueFields().omittingInsignificantWhitespace();

    @Override
    public <T, A> ClientCall<T, A> interceptCall(MethodDescriptor<T, A> method, CallOptions callOptions, Channel next) {
        return new ForwardingClientCall.SimpleForwardingClientCall<>(next.newCall(method, callOptions.withoutWaitForReady())) {

            @Override
            public void sendMessage(T message) {
                log.info("\n---- gRPC Request ----\nTarget: {}\nMethod: {}\n", next.authority(), method.getFullMethodName());
                logMessage("Request body", message);
                super.sendMessage(message);
            }

            @Override
            public void start(Listener<A> responseListener, Metadata headers) {
                log.info("Request headers: {}", headers);
                final Listener<A> listener = new ForwardingClientCallListener<A>() {
                    @Override
                    protected Listener<A> delegate() {
                        return responseListener;
                    }

                    @Override
                    public void onMessage(A message) {
                        logMessage("Response body", message);
                        super.onMessage(message);
                    }

                    @Override
                    public void onClose(io.grpc.Status status, Metadata trailers) {
                        log.info("\n---- gRPC Response ----\nStatus: {}\nResponse headers: {}\n", status, trailers);
                        super.onClose(status, trailers);
                    }
                };
                super.start(listener, headers);
            }
        };
    }

    private void logMessage(String prefix, Object message) {
        if (message instanceof MessageOrBuilder) {
            try {
                log.info("{}:\n{}", prefix, JSON_PRINTER.print((MessageOrBuilder) message));
            } catch (InvalidProtocolBufferException e) {
                log.warn("{} (failed to parse as JSON): {}", prefix, message.toString(), e);
            }
        } else {
            log.warn("{} (not a protobuf message): {}", prefix, message.toString());
        }
    }
}
