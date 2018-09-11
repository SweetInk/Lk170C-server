package online.githuboy.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.TooLongFrameException;
import online.githuboy.dto.BaseMessage;
import online.githuboy.utils.CommonUtils;

import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.SimpleTimeZone;
import java.util.TimeZone;

/**
 * Desc: LK710C GPS protocol decoder
 *
 * @author suchu
 * @version 1.0
 * @since 2018/9/11
 */
public class GPsDataDecoder extends ByteToMessageDecoder {
    private final byte GPS_HEADER = 0x24; //$
    private final byte HQ_HEADER = 0x2a; //*
    /***
     * <code>
     *     *HQ,[optional_data],[protocol_type],....#
     * </code>
     */
    private final byte HQ_END = 0x23; //#
    volatile int count = 0;
    private final int maxLength;


    /**
     * True if we're discarding input because we're already over maxLength.
     */
    private boolean discarding;

    private int discardedBytes;
    /**
     * Last scan position.
     */
    private int offset;

    public GPsDataDecoder() {
        maxLength = 1024;
    }


    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        int length = in.readableBytes();
        if (length < 16) return;
        System.out.println("marked:" + in.markReaderIndex());//marked the readerIndex
        byte header = in.readByte();
        ByteBuf decoded = null;
        if (header == GPS_HEADER) {
            decoded = (ByteBuf) decodeGpsData(ctx, in);
        } else if (header == HQ_HEADER) {
            decoded = (ByteBuf) decodeHqData(ctx, in);
        } else {
            //    System.out.println("not received header");
        }
        if (decoded != null) {
            BaseMessage baseMessage = CommonUtils.toBaseMessage(decoded, header);
            if (null != baseMessage)
                out.add(baseMessage);
            System.out.print("Length:" + decoded.readableBytes());
            String str = new String(decoded.toString(Charset.forName("utf-8")));
            System.out.println("->" + str);
            if (header == HQ_HEADER) {
                responseHQData(ctx, decoded);
            }
        } else {
            in.resetReaderIndex();//if not decode the data,resetReaderIndex
            return;
        }

    }

    public static String getFormatedDateString(float timeZoneOffset) {
        if (timeZoneOffset > 13 || timeZoneOffset < -12) {
            timeZoneOffset = 0;
        }

        int newTime = (int) (timeZoneOffset * 60 * 60 * 1000);
        TimeZone timeZone;
        String[] ids = TimeZone.getAvailableIDs(newTime);
        if (ids.length == 0) {
            timeZone = TimeZone.getDefault();
        } else {
            timeZone = new SimpleTimeZone(newTime, ids[0]);
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        sdf.setTimeZone(timeZone);
        return sdf.format(new Date());
    }

    public void responseHQData(ChannelHandlerContext ctx, ByteBuf data) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        int length = data.readableBytes();
        byte[] buf = new byte[length];
        data.getBytes(0, buf);
        String hqData = new String(buf, 0, buf.length);
        String fragments[] = hqData.split(",");
        if (fragments.length > 5) {
            String type = fragments[2];
            String deviceId = fragments[1];
            String response = "*HQ," + deviceId + ",V4," + type + "," + getFormatedDateString(0) + "#";
            ctx.writeAndFlush(response.getBytes());
            System.out.println("[" + sdf.format(new Date()) + "][INFO]response_hq[" + response.getBytes().length + "]:" + response);
        }

    }


    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("new connection joined:" + ctx.pipeline().channel().remoteAddress());
        super.channelActive(ctx);
    }

    protected Object decodeGpsData(ChannelHandlerContext ctx, ByteBuf buffer) {
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

    protected Object decodeHqData(ChannelHandlerContext ctx, ByteBuf buffer) throws Exception {
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

    private void fail(final ChannelHandlerContext ctx, int length) {
        fail(ctx, String.valueOf(length));
    }

    private void fail(final ChannelHandlerContext ctx, String length) {
        ctx.fireExceptionCaught(
                new TooLongFrameException(
                        "frame length (" + length + ") exceeds the allowed maximum (" + maxLength + ')'));
    }

    /**
     * 返回心跳包结尾位置
     * Returns -1 如果在当前buffer中没有找到GPS心跳结尾标记
     */
    private int findEndOfHQData(final ByteBuf buffer) {
        int totalLength = buffer.readableBytes();
        int i = buffer.forEachByte(buffer.readerIndex() + offset, totalLength - offset, (byte value) -> {
            return value != HQ_END;//HQ_END flag
        });
        if (i >= 0) {
            offset = 0;
        } else {
            offset = totalLength;
        }
        return i;
    }


    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
        System.out.println("a connection lost:" + ctx.pipeline().channel().remoteAddress());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
        if (ctx.channel() == this) {
            System.out.println("client lose connection:" + ctx.channel().remoteAddress());
        }
    }
}
