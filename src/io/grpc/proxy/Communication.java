package io.grpc.proxy;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.ServerBuilder;
import io.grpc.netty.NettyChannelBuilder;
import io.grpc.netty.NettyServerBuilder;
import io.netty.channel.epoll.EpollDomainSocketChannel;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerDomainSocketChannel;
import io.netty.channel.unix.DomainSocketAddress;
import io.netty.util.concurrent.DefaultThreadFactory;

/**
 * TODO: Document this class
 */
public interface Communication {
    Communication DEFAULT = new DomainSocketCommunication();

    ServerBuilder<?> createServerBuilder();

    ManagedChannel createClientChannel();

    String getListeningDescriptor();

    class DomainSocketCommunication implements Communication {
        private static final String DEFAULT_SOCKET_PATH = "./grpc.socket";
        private final String socketPath;

        public DomainSocketCommunication(String socketPath) {
            this.socketPath = socketPath;
        }

        public DomainSocketCommunication() {
            this(DEFAULT_SOCKET_PATH);
        }

        @Override
        public ServerBuilder<?> createServerBuilder() {
            final DefaultThreadFactory threadFactory = new DefaultThreadFactory(EpollEventLoopGroup.class, true);
            return NettyServerBuilder.forAddress(new DomainSocketAddress(socketPath))
                    .bossEventLoopGroup(new EpollEventLoopGroup(0, threadFactory))
                    .workerEventLoopGroup(new EpollEventLoopGroup(0, threadFactory))
                    .channelType(EpollServerDomainSocketChannel.class);
        }

        @Override
        public ManagedChannel createClientChannel() {
            final DefaultThreadFactory threadFactory = new DefaultThreadFactory(EpollEventLoopGroup.class, true);
            return NettyChannelBuilder.forAddress(new DomainSocketAddress("./grpc.socket"))
                    .usePlaintext(true)
                    // need to pass thread factory so that the thread is a daemon so that the client can actually exit
                    .eventLoopGroup(new EpollEventLoopGroup(0, threadFactory))
                    .channelType(EpollDomainSocketChannel.class)
                    .build();
        }

        @Override
        public String getListeningDescriptor() {
            return "unix socket " + socketPath;
        }
    }

    class PortCommunication implements Communication {
        private static final int DEFAULT_PORT = 50053;

        private final int port;

        public PortCommunication(int port) {
            this.port = port;
        }

        public PortCommunication() {
            this(DEFAULT_PORT);
        }

        @Override
        public ServerBuilder<?> createServerBuilder() {
            return ServerBuilder.forPort(port);
        }

        @Override
        public ManagedChannel createClientChannel() {
            return ManagedChannelBuilder.forAddress("localhost", port)
                    .usePlaintext(true)
                    .build();
        }

        @Override
        public String getListeningDescriptor() {
            return "port " + port;
        }
    }
}
