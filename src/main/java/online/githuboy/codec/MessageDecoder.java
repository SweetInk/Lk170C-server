package online.githuboy.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import lombok.extern.slf4j.Slf4j;
import online.githuboy.Attributes;
import online.githuboy.ServerConstant;
import online.githuboy.domain.packet.DataPacket;
import online.githuboy.parser.ParseUtils;

import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * ByteBuf to Message
 *
 * @author suchu
 * @since 2018/11/7 10:48
 */
@Slf4j
public class MessageDecoder extends MessageToMessageDecoder<ByteBuf> {
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf msg, List<Object> out) throws Exception {
        byte header = msg.getByte(0);
        DataPacket packet = parseData(msg, header);
        if (null != packet) {
            String deviceId = packet.getDeviceId();
            String result = ctx.channel().attr(Attributes.DEVICE_ID).setIfAbsent(deviceId);
            //设备第一次发送心跳包
            if (null == result) {
                log.info("[{}]:设备:[{}] 登陆上线", ctx.channel().id().asShortText(), deviceId);
            }
            out.add(packet);
        }
    }


    private DataPacket parseData(ByteBuf msg, byte header) {
        byte[] data = new byte[msg.readableBytes()];
        msg.getBytes(0, data);
        String rawData = null;
        rawData = new String(data, StandardCharsets.UTF_8);
        DataPacket packet = null;
        if (ServerConstant.GPS_HEADER == header) {
            packet = ParseUtils.parseGpsData(data);
        } else if (ServerConstant.STATUS_HEADER == header) {
            packet = ParseUtils.parseStatusData(rawData);
        }
        if (null != packet) {
            packet.setRawData(rawData);
        }
        return packet;
    }
}
