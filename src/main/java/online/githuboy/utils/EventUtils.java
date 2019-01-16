package online.githuboy.utils;

import io.netty.channel.ChannelHandlerContext;
import online.githuboy.domain.dto.DeviceEvent;

/**
 * @author suchu
 * @since 2018/11/13 16:08
 */
public class EventUtils {

    public static void publish(ChannelHandlerContext ctx, DeviceEvent event) {
        ctx.fireUserEventTriggered(event);
    }
}
