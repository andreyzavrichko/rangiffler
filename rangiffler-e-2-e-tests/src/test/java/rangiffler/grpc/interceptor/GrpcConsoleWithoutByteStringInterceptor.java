package rangiffler.grpc.interceptor;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Message;
import com.google.protobuf.Message.Builder;
import com.google.protobuf.MessageOrBuilder;
import com.google.protobuf.util.JsonFormat;
import io.grpc.*;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public class GrpcConsoleWithoutByteStringInterceptor implements ClientInterceptor {

    private static final JsonFormat.Printer JSON_PRINTER = JsonFormat.printer();

    @Override
    public <T, A> ClientCall<T, A> interceptCall(MethodDescriptor<T, A> method,
                                                 CallOptions callOptions,
                                                 Channel next) {
        return new ForwardingClientCall.SimpleForwardingClientCall<>(
                next.newCall(method, callOptions.withoutWaitForReady())) {

            @Override
            public void sendMessage(T message) {
                log.info("Send gRPC request to {}{}", next.authority(), trimGrpcMethodName(method.getFullMethodName()));
                try {
                    log.info("Request body:\n {}", JSON_PRINTER.print((MessageOrBuilder) message));
                } catch (InvalidProtocolBufferException e) {
                    log.error("Unable to transform message -> json");
                }
                super.sendMessage(message);
            }

            @Override
            public void start(Listener<A> responseListener, Metadata headers) {
                final Listener<A> listener = new ForwardingClientCallListener<A>() {
                    @Override
                    protected Listener<A> delegate() {
                        return responseListener;
                    }

                    @Override
                    public void onClose(io.grpc.Status status, Metadata trailers) {
                        super.onClose(status, trailers);
                    }

                    @SneakyThrows
                    @Override
                    public void onMessage(A message) {
                        try {
                            var msgToPrint = getMessageForPrint(((Message) message).toBuilder());
                            log.info("Response body:\n {}", JSON_PRINTER.print(msgToPrint));
                        } catch (Exception e) {
                            log.error("Can't log parsed message to console");
                        }
                        super.onMessage(message);
                    }
                };
                super.start(listener, headers);
            }

            private String trimGrpcMethodName(final String source) {
                return source.substring(source.lastIndexOf('/'));
            }
        };
    }

    public Message getMessageForPrint(Builder messageBuilder) {
        for (var field : messageBuilder.getAllFields().entrySet()) {
            if (!field.getKey().isRepeated()) {
                var valueToPrint = field.getValue() instanceof ByteString && ((ByteString) field.getValue()).size() > 1000
                        ? ByteString.copyFrom(new byte[]{})
                        : field.getValue();
                messageBuilder.setField(field.getKey(), valueToPrint);
            } else {
                var messages = (List<?>) field.getValue();
                for (int i = 0; i < messages.size(); i++) {
                    if (Message.class.isAssignableFrom(messages.getFirst().getClass())) {
                        messageBuilder.setRepeatedField(
                                field.getKey(),
                                i,
                                getMessageForPrint(((Message) messages.get(i)).toBuilder())
                        );
                    } else {
                        messageBuilder.setRepeatedField(field.getKey(), i, messages.get(i));
                    }
                }
            }
        }
        return messageBuilder.build();
    }
}
