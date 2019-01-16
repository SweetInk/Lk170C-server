package online.githuboy.codec;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import lombok.extern.slf4j.Slf4j;
import online.githuboy.domain.dto.DeviceEvent;
import online.githuboy.domain.packet.NBRPacket;
import online.githuboy.enums.MessageTypeEnum;
import online.githuboy.utils.CommonUtils;

import java.util.Date;
import java.util.List;

/**
 * 多基站协议
 *
 * @author suchu
 * @since 2018/11/7 14:27
 */
@Slf4j
public class NBRHandler extends MessageToMessageDecoder<NBRPacket> {
    @Override
    protected void decode(ChannelHandlerContext ctx, NBRPacket msg, List<Object> out) throws Exception {
        String deviceId = msg.getDeviceId();
        String responseData = CommonUtils.buildResponseData(deviceId, msg.getMsgType().toString());
        ctx.pipeline().writeAndFlush(responseData);
        log.debug("回复多基站协议 :{}", responseData);
        DeviceEvent deviceEvent = new DeviceEvent();
        deviceEvent
                .deviceId(msg.getDeviceId())
                .triggerTime(new Date())
                .event(MessageTypeEnum.NBR.toString());
        ctx.fireUserEventTriggered(deviceEvent);
    }
}
