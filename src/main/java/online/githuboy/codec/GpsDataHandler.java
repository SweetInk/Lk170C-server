package online.githuboy.codec;


import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import lombok.extern.slf4j.Slf4j;
import online.githuboy.domain.dto.DeviceEvent;
import online.githuboy.domain.packet.GpsPacket;
import online.githuboy.enums.MessageTypeEnum;

import java.util.Date;
import java.util.List;

/**
 * 处理GPS数据包
 *
 * @author suchu
 * @since 2018/11/7 14:16
 */
@Slf4j
public class GpsDataHandler extends MessageToMessageDecoder<GpsPacket> {
    @Override
    protected void decode(ChannelHandlerContext ctx, GpsPacket msg, List<Object> out) throws Exception {
        log.debug("[{}]:收到[{}]: GPS数据包：{}", ctx.channel().id().asShortText(), msg.getDeviceId(), msg);

        //trigger user event
        Double lat = msg.isValid() ? msg.getLat() : null;
        Double lng = msg.isValid() ? msg.getLng() : null;
        DeviceEvent deviceEvent = new DeviceEvent();
        deviceEvent
                .dateStr(msg.getDateStr())
                .lat(lat)
                .lng(lng)
                .deviceId(msg.getDeviceId())
                .triggerTime(new Date())
                .event(MessageTypeEnum.GPS_DATA.toString());
        ctx.fireUserEventTriggered(deviceEvent);

        try {
            if (msg.isValid()) {
                //TODO process GpsDataPacket
            }

        } catch (Exception e) {
            log.error("In the GPS information data persistence encounters an error when processing:", e);
        }
    }
}
