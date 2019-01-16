package online.githuboy.parser;


import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import online.githuboy.domain.packet.DataPacket;
import online.githuboy.domain.packet.GpsPacket;
import online.githuboy.domain.packet.HeartBeatPacket;
import online.githuboy.domain.packet.NBRPacket;
import online.githuboy.utils.CommonUtils;
import org.junit.Test;

/**
 * 数据解析工具
 *
 * @author suchu
 * @since 2018/11/7 11:37
 */
public class ParseUtils {

    private final static byte POS_VALID_MASK = 0x2;
    private final static byte LAT_DIR_MASK = 0x2 << 1;
    private final static byte LNG_DIR_MASK = 0x2 << 2;

    /**
     * 解析GPS数据包<br/>
     * eg:<code>0x2447100653490902100711183034196804104036219E000000FFE7FBFFFF001E750000000001CC0000000000000101</code>
     *
     * @param dataContent byteArray
     * @return
     */
    public static GpsPacket parseGpsData(byte[] dataContent) {

        ByteBuf byteBuf = Unpooled.copiedBuffer(dataContent);
        GpsPacket packet = new GpsPacket();
        //read header
        byteBuf.readByte();//1

        //read  deviceId
        byte[] deviceBytes = new byte[5];
        byteBuf.readBytes(deviceBytes);
        String deviceId = CommonUtils.byteToHex(deviceBytes, false);
        byte[] dateBytes = new byte[6];

        //read date time
        byteBuf.readBytes(dateBytes);
        String dateStr = CommonUtils.parseDate(dateBytes);

        //read lat
        byte[] latBytes = new byte[4];
        byteBuf.readBytes(latBytes);//1

        //read battery Level
        int batteryLevel = byteBuf.readByte() & 0xFF;

        //read lng
        byte[] lngBytes = new byte[5];
        byteBuf.readBytes(lngBytes);
        double lat = parseLat(latBytes);
        double lng = parseLng(lngBytes);
        String latDir = latDir(lngBytes[4]);
        String lngDir = lngDir(lngBytes[4]);
        boolean isValid = (lngBytes[4] & POS_VALID_MASK) == POS_VALID_MASK;

        //read speed;
        byte[] speedBytes = new byte[3];
        byteBuf.readBytes(speedBytes);
        double speed = getSpeed(speedBytes);//3
        double dirDegree = getDirectionDegree(speedBytes);
        // read vehicle_status
        byte[] vehicle_status = new byte[4];
        byteBuf.readBytes(vehicle_status);

        //Usr_alarm_flag
        byte[] Usr_alarm_flag = new byte[2];
        byteBuf.readBytes(Usr_alarm_flag);

        //GSM signal
        byte gmsSign = byteBuf.readByte();

        //GPS signal
        byte gpsSignal = byteBuf.readByte();

        byteBuf.skipBytes(3);//SKIP 3

        //nation code
        int nationCode = byteBuf.readShort();

        //operator code
        byte operatorCode = byteBuf.readByte();

        //baseId
        short baseId = byteBuf.readShort();

        //areaId
        short areaId = byteBuf.readShort();

        //voltageLevel
        double voltageLevel = byteBuf.readShort() * 0.1;

        //seq number
        int seqNumber = byteBuf.readByte() & 0xFF;

        packet.setBatteryLevel(batteryLevel);
        packet.setLat(lat);
        packet.setLng(lng);
        packet.setSpeed(speed);
        packet.setLatDir(latDir);
        packet.setLngDir(lngDir);
        packet.setVoltage(voltageLevel);
        packet.setValid(isValid);
        packet.setDeviceId(deviceId);
        packet.setSeqNumber(seqNumber);
        packet.setDateStr(dateStr);
        packet.setDirDegree(dirDegree);
        byteBuf.release();
        return packet;
    }


    public static DataPacket parseStatusData(String rawData) {
        String fragments[] = rawData.split(",");

        if (fragments.length >= 5) {
            String type = fragments[2];
            switch (type) {
                case "V1":
                    return parseHeartBeatData(fragments);
                case "NBR":
                    return parseNBRData(fragments);
                default:
                    return null;
            }
        }
        //TODO 其他状态数据包
        return null;
    }

    /**
     * 解析心跳包<br/>
     * eg:<code>*HQ,4209809058,V1,064709,v,2233.9355,N,11351.7442,E,000.00,000,231215,FFFFFBFF,460,00,0,0,6#</code>
     *
     * @param fragments
     * @return
     */
    public static HeartBeatPacket parseHeartBeatData(String[] fragments) {

        if (fragments.length < 15) return null;
        String deviceId = fragments[1];
        boolean isValid = "A".equalsIgnoreCase(fragments[4]);//A 有效，V 无效
        HeartBeatPacket heartBeatPacket = new HeartBeatPacket();
        if (isValid) {
            //解析纬度
            heartBeatPacket.setLat(parseLat(fragments[5]));
            //解析经度
            heartBeatPacket.setLng(parseLng(fragments[7]));
        }
        heartBeatPacket.setDeviceId(deviceId);
        return heartBeatPacket;
    }


    /**
     * 解析多基站协议
     *
     * @param fragments
     * @return
     */
    public static NBRPacket parseNBRData(String[] fragments) {

        String deviceId = fragments[1];
        NBRPacket nbrPacket = new NBRPacket();
        nbrPacket.setDeviceId(deviceId);
        return nbrPacket;
    }


    /**
     * 解析纬度 NMEA-01843 -> WGS84
     *
     * @param lat example:3034.1968
     * @return
     */
    private static double parseLat(String lat) {
        double dd = Double.parseDouble(lat.substring(0, 2));
        double mm = Double.parseDouble(lat.substring(2)) / 60.f;
        return dd + mm;

    }

    /**
     * 解析经度 NMEA-01843 -> WGS84
     *
     * @param lng example:10403.6219
     * @return
     */
    private static double parseLng(String lng) {
        double dd = Double.parseDouble(lng.substring(0, 3));
        double mm = Double.parseDouble(lng.substring(3)) / 60.f;
        return dd + mm;
    }

    /**
     * 0x000099  ox去掉 000表示速度 海里  099 表示正北偏离角度
     * 这个速率值是海里/时，单位是节，要把它转换成千米/时，根据：1海里=1.85公里，把得到的速率乘以1.85。
     *
     * @return
     */
    public static double getSpeed(byte[] data) {
        String str = CommonUtils.byteToHex(data, false);
        return Integer.parseInt(str.substring(0, 3)) * 1.85;
    }

    public static double getDirectionDegree(byte[] data) {
        String str = CommonUtils.byteToHex(data, false);
        return Integer.parseInt(str.substring(3, 6));
    }

    /**
     * 纬度方向
     *
     * @param flag
     * @return
     */
    public static String latDir(byte flag) {
        return ((flag & LAT_DIR_MASK) == LAT_DIR_MASK) ? "北纬" : "南纬";
    }

    /**
     * 经度方向
     *
     * @param flag
     * @return
     */
    public static String lngDir(byte flag) {
        return ((flag & LNG_DIR_MASK) == LNG_DIR_MASK) ? "东经" : "西经";
    }

    /**
     * <3>纬度ddmm.mmmm（度分）格式（前面的0也将被传输） <4> 纬度半球N（北半球）或S（南半球）lat
     *
     * @return
     */
    private static double parseLat(byte[] data) {

        String lat_s = CommonUtils.byteToHex(data, false);
        Double dd = Double.parseDouble(lat_s.substring(0, 2));
        Double mm = Double.parseDouble(lat_s.substring(2, 4) + "." + lat_s.substring(4)) / 60.;
        return dd + mm;

    }

    /**
     * * <5>经度dddmm.mmmm（度分）格式（前面的0也将被传输） <6> 经度半球E（东经）或W（西经） lng
     *
     * @param data
     * @return
     */
    private static double parseLng(byte[] data) {
        String lng_s = CommonUtils.byteToHex(data, false);
        Double dd = Double.parseDouble(lng_s.substring(0, 3));
        Double mm = Double.parseDouble(lng_s.substring(3, 5) + "." + lng_s.substring(5, lng_s.length() - 1)) / 60;
        return dd + mm;
    }

    @Test
    public void testParseLat() {
        System.out.println(parseLat("3034.1968"));
        System.out.println(parseLng("10403.6219"));
    }


}
