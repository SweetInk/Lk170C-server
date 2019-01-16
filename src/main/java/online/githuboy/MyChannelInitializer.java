package online.githuboy;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.timeout.IdleStateHandler;
import online.githuboy.codec.*;

/**
 * Channel init
 *
 * @author suchu
 * @since 2018/11/13 14:11
 */
public class MyChannelInitializer extends ChannelInitializer<SocketChannel> {
    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ch.pipeline().addLast("idleStateHandler", new IdleStateHandler(ServerConstant.DEVICE_READ_TIMEOUT, 0, 0));
        ch.pipeline().addLast("frameDecoder", new DataFrameDecoder());
        ch.pipeline().addLast("messageDecoder", new MessageDecoder());
        ch.pipeline().addLast("ENCODER", new StringEncoder());
        ch.pipeline().addLast("heartBeatHandler", new HeartBeatHandler());
        ch.pipeline().addLast("nbrHandler", new NBRHandler());
        ch.pipeline().addLast("gpsDataHandler", new GpsDataHandler());
        ch.pipeline().addLast("eventHandler", new EventHandler());
    }
}
