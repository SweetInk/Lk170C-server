package online.githuboy.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.TooLongFrameException;
import lombok.extern.slf4j.Slf4j;
import online.githuboy.utils.CommonUtils;

import java.util.List;

import static online.githuboy.ServerConstant.*;


/**
 * DataFrame decoder
 * used for L710C device
 *
 * @author suchu
 * @since 2018/11/7 10:47
 */
@Slf4j
public class DataFrameDecoder extends ByteToMessageDecoder {


    /**
     * minimum data length
     */
    private final static int MINIMUM_LENGTH = 0x10;
    boolean receivedPacket;
    /**
     * Last scan position.
     */
    private int offset;
    private int maxLength = 1024;
    private int discardedBytes;
    /**
     * True if we're discarding input because we're already over maxLength.
     */
    private boolean discarding;

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        int length = in.readableBytes();
        if (length < MINIMUM_LENGTH) {
            return;
        }

        in.markReaderIndex();
        String dataType = "";
        boolean hexDump = false;
        byte header = in.readByte();
        ByteBuf frame = null;
        if (header == GPS_HEADER) {
            frame = decodeGpsFrame(ctx, in);
            dataType = "GPS_DATA";
            hexDump = true;
        } else if (header == STATUS_HEADER) {
            frame = decodeStatusFrame(ctx, in);
            dataType = "STATUS_DATA";
        } else {
            in.resetReaderIndex();
            byte[] b = new byte[length];
            in.readBytes(b);
            String str = new String(b);
            log.info("received invalid request data:{}", str);
            ctx.pipeline().writeAndFlush("\r\ninvalid request,close it!\r\n").addListener((ChannelFutureListener) future -> {
                log.warn("Illegal connection:[{}] ,force close it !", future.channel().remoteAddress());
                future.channel().close();
            });
            return;
        }

        if (null != frame) {
            out.add(frame);
            int frameLength = frame.readableBytes();
            byte[] buffer = new byte[frameLength];

            frame.getBytes(0, buffer);
            log.debug("RECEIVED channel->{}  [{}] LENGTH:[{}] : {}", ctx.channel().id().asShortText(), dataType, frameLength, hexDump ? CommonUtils.byteToHex(buffer) : new String(buffer));
        } else {
            in.resetReaderIndex();
        }


    }


    private ByteBuf decodeGpsFrame(ChannelHandlerContext ctx, ByteBuf buffer) {
        final int length = buffer.readableBytes();
        if (length >= 46) {
            final ByteBuf frame;
            buffer.readerIndex(buffer.readerIndex() - 1);
            //buffer.slice(buffer.readerIndex()-1,46);
            frame = buffer.readRetainedSlice(47);
            return frame;
        }
        return null;
    }

    private ByteBuf decodeStatusFrame(ChannelHandlerContext ctx, ByteBuf buffer) throws Exception {
        final int hqEol = findEndOfHQData(buffer);
        if (hqEol > 0) {
            final ByteBuf frame;
            final int length = hqEol - buffer.readerIndex();
            final int delimLength = 1;
            if (length > maxLength) {
                buffer.readerIndex(hqEol + delimLength);
                fail(ctx, length);
                return null;
            }
            buffer.readerIndex(buffer.readerIndex() - 1);
            frame = buffer.readRetainedSlice(length + delimLength + 1);
            return frame;
        } else {
            final int length = buffer.readableBytes();
            if (length > maxLength) {
                discardedBytes = length;
                buffer.readerIndex(buffer.writerIndex());
                discarding = true;
                offset = 0;
                fail(ctx, "over " + discardedBytes);
            }
            return null;
        }
    }

    /**
     * 返回心跳包结尾位置
     * Returns -1 如果在当前buffer中没有找到GPS心跳结尾标记
     */
    private int findEndOfHQData(final ByteBuf buffer) {
        int totalLength = buffer.readableBytes();
        int i = buffer.forEachByte(buffer.readerIndex() + offset, totalLength - offset, (byte value) -> {
            return value != STATUS_END;//STATUS_END flag
        });
        if (i >= 0) {
            offset = 0;
        } else {
            offset = totalLength;
        }
        return i;
    }

    private void fail(final ChannelHandlerContext ctx, int length) {
        fail(ctx, String.valueOf(length));
    }


    private void fail(final ChannelHandlerContext ctx, String length) {
        ctx.fireExceptionCaught(
                new TooLongFrameException(
                        "frame length (" + length + ") exceeds the allowed maximum (" + maxLength + ')'));
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.info("remote client close connection:{}", ctx.channel().remoteAddress());
        super.exceptionCaught(ctx, cause);
    }
}
