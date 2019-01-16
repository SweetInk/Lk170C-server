package online.githuboy.codec;


import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;
import online.githuboy.Attributes;
import online.githuboy.domain.dto.DeviceEvent;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.Date;

/**
 * 事件处理器
 *
 * @author suchu
 * @since 2018/11/13 15:46
 */
@Slf4j
public class EventHandler extends ChannelDuplexHandler {
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) {
        try {
            if (evt instanceof DeviceEvent) {
                DeviceEvent e = (DeviceEvent) evt;
            } else if (evt instanceof IdleStateEvent) {
                IdleStateEvent e = (IdleStateEvent) evt;
                log.debug("接受到设备IDLE事件:{}", evt);
                if (e.state() == IdleState.READER_IDLE) {
                    String deviceId = ctx.channel().attr(Attributes.DEVICE_ID).get();
                    log.info("设备[{}]长时间 未发送消息，强制关闭！", !StringUtils.isEmpty(deviceId) ? deviceId : ctx.channel().remoteAddress());
                    if (!StringUtils.isEmpty(deviceId)) {
                        DeviceEvent event = new DeviceEvent();
                        event.setOnline(false);
                        event.setDeviceId(deviceId);
                        event.setEventType("device_offline");
                        event.setTriggerTime(new Date());
                        //TODO processEvent
                    } else {
                        log.error("channel -> {},not present deviceId", ctx.channel().id().asShortText());
                    }
                    ctx.close();

                }
            } else {
                super.userEventTriggered(ctx, evt);
            }
        } catch (Exception e) {
            log.error("Occur a error when process Pipeline event：{}", e.getMessage());
            if (e instanceof IOException) {
                if (!ctx.channel().isActive()) {
                    ctx.close();
                }
            }
        }
    }

}
