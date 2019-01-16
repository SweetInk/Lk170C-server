package online.githuboy;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * Test server
 *
 * @author suchu
 * @since 2018/11/7 11:10
 */
@Slf4j
public class ServerBoot {
    private final static int PORT = 8080;

    public static void main(String[] args) throws InterruptedException {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {

            ServerBootstrap b = new ServerBootstrap();

            b.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 100)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new MyChannelInitializer());
            ChannelFuture f = b.bind(PORT).sync(); // (7)
            log.info("GPS Server start on:{}", PORT);

            // Wait until the server socket is closed.
            // In this example, this does not happen, but you can do that to gracefully
            // shut down your server.
            f.channel().closeFuture().sync();
            System.out.println("ready：close server");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            log.info("server has been shutdown");
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
            System.exit(0);
        }
    }
}
