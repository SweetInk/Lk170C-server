package online.githuboy.utils;

import io.netty.buffer.ByteBuf;
import online.githuboy.NettyConstant;
import online.githuboy.dto.BaseMessage;
import online.githuboy.parser.*;

import java.io.UnsupportedEncodingException;

public class CommonUtils {
    public static String byteToHex(byte[] buffer) {
        StringBuilder sb = new StringBuilder(128);
        for (int i = 0; i < buffer.length; i++) {
            String hv = Integer.toHexString(buffer[i] & 0xFF).toUpperCase();
            if (hv.length() < 2)
                sb.append("0");
            sb.append(hv);
        }
        return "0x" + sb.toString();
    }


    public static BaseMessage toBaseMessage(ByteBuf byteBuf, byte header) {
        byte[] data = new byte[byteBuf.readableBytes()];
        byteBuf.getBytes(0, data);
        String rawData = null;
        try {
            rawData = new String(data, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        if (NettyConstant.GPS_DATA_HEADER == header) {
            AbstractParser parser = new GPSDataParser(data);
            try {
                return parser.process();
            } catch (CannotParseException e) {
                e.printStackTrace();
            }
        } else if (NettyConstant.GPS_HQ_HEADER == header) {
            AbstractParser parser = determineParser(rawData);
            if (null != parser) {
                try {
                    return parser.process();
                } catch (CannotParseException e) {
                    e.printStackTrace();
                    return null;
                }
            }
        }
        return null;
    }

    public static AbstractParser determineParser(String data) {
        String fragment[] = data.split(",");
        if (fragment.length >= 5) {
            String type = fragment[2];
            switch (type) {
                case "V1":
                    return new HQV1Parser(data);
                case "NBR":
                    return new HQNBRParser(data);
            }
        }
        return null;
    }
}
