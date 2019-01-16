package online.githuboy.codec;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import lombok.extern.slf4j.Slf4j;
import online.githuboy.domain.dto.DeviceEvent;
import online.githuboy.domain.packet.HeartBeatPacket;
import online.githuboy.enums.MessageTypeEnum;
import online.githuboy.utils.CommonUtils;

import java.util.Date;
import java.util.List;

/**
 * 心跳包处理
 *
 * @author suchu
 * @since 2018/11/7 14:27
 */
@Slf4j
public class HeartBeatHandler extends MessageToMessageDecoder<HeartBeatPacket> {
    @Override
    protected void decode(ChannelHandlerContext ctx, HeartBeatPacket msg, List<Object> out) throws Exception {
        log.info("[{}]:收到设备[{}]心跳包：{}", ctx.channel().id().asShortText(), msg.getDeviceId(), msg.getRawData());
        String deviceId = msg.getDeviceId();
        String responseData = CommonUtils.buildResponseData(deviceId, msg.getMsgType().toString());
        ctx.pipeline().writeAndFlush(responseData);
        log.info("[{}]:回复心跳包 :{}", ctx.channel().id().asShortText(), responseData);
        DeviceEvent deviceEvent = new DeviceEvent();
        Double lat = msg.isValid() ? msg.getLat() : null;
        Double lng = msg.isValid() ? msg.getLng() : null;

        deviceEvent
                .dateStr(null)
                .lat(lat)
                .lng(lng)
                .deviceId(msg.getDeviceId())
                .triggerTime(new Date())
                .event(MessageTypeEnum.V1.toString());
        ctx.fireUserEventTriggered(deviceEvent);
    }
}
