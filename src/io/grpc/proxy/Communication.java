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
public class Communication {
    private static final boolean USE_UNIX_SOCKET = true;
    private static final int PORT = 50053;
    private static final String DOMAIN_SOCKET = "./grpc.socket";
    public static ServerBuilder<?> createServerBuilder() {
        if (USE_UNIX_SOCKET) {
            final DefaultThreadFactory threadFactory = new DefaultThreadFactory(EpollEventLoopGroup.class, true);
            return NettyServerBuilder.forAddress(new DomainSocketAddress(DOMAIN_SOCKET))
                    .bossEventLoopGroup(new EpollEventLoopGroup(0, threadFactory))
                    .workerEventLoopGroup(new EpollEventLoopGroup(0, threadFactory))
                    .channelType(EpollServerDomainSocketChannel.class);
        } else {
            return ServerBuilder.forPort(PORT);
        }
    }

    public static ManagedChannel createClientChannel() {
        if (USE_UNIX_SOCKET) {
            final DefaultThreadFactory threadFactory = new DefaultThreadFactory(EpollEventLoopGroup.class, true);
            return NettyChannelBuilder.forAddress(new DomainSocketAddress("./grpc.socket"))
                    .usePlaintext(true)
                    // need to pass thread factory so that the thread is a daemon so that the client can actually exit
                    .eventLoopGroup(new EpollEventLoopGroup(0, threadFactory))
                    .channelType(EpollDomainSocketChannel.class)
                    .build();
        } else {
            return ManagedChannelBuilder.forAddress("localhost", PORT)
                    .usePlaintext(true)
                    .build();

        }
    }

    public static String getListeningDescriptor() {
        return USE_UNIX_SOCKET ? "unix socket " + DOMAIN_SOCKET : "port " + PORT;
    }
}
