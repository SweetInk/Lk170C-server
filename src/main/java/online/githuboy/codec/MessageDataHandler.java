package online.githuboy.codec;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import online.githuboy.dto.BaseMessage;

import java.util.List;

/**
 * 处理结构化数据包
 *
 * @author suchu
 * @since 2018/9/11
 */
public class MessageDataHandler extends MessageToMessageDecoder<BaseMessage> {

    @Override
    protected void decode(ChannelHandlerContext ctx, BaseMessage msg, List<Object> out) throws Exception {
        System.out.println("object_data:" + msg);
        //TODO persistence data
    }
}
