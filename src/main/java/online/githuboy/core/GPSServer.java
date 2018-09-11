package online.githuboy.core;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import online.githuboy.NettyConstant;
import online.githuboy.codec.GPsDataDecoder;
import online.githuboy.codec.MessageDataHandler;


/**
 * Desc: Lk170C gps backend server
 *
 * @author suchu
 * @version 1.0
 * @since 2018/9/11
 */
public class GPSServer {

    public void bind() throws InterruptedException {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        ServerBootstrap b = new ServerBootstrap();
        b.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class)
                .option(ChannelOption.SO_BACKLOG, 100)
                .handler(new LoggingHandler(LogLevel.INFO))
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new GPsDataDecoder());
                        ch.pipeline().addLast(new MessageDataHandler());
                    }
                });
        b.bind(NettyConstant.PORT).sync();
        System.out.println("GPS Server start on:" + NettyConstant.PORT);
    }

    public static void main(String[] args) throws InterruptedException {
        new GPSServer().bind();
        ;
    }
}

