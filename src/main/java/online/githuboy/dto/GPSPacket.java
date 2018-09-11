package online.githuboy.dto;


import online.githuboy.enums.MessageTypeEnum;
import online.githuboy.utils.CommonUtils;
import org.junit.Test;

/**
 * GPS 数据
 * @author suchu
 */
public class GPSPacket extends BaseMessage {

    public GPSPacket() {
        super(MessageTypeEnum.GPS_DATA);
    }

    //12
    public byte header;
    public byte deviceId[] = new byte[5];
    public byte date[] = new byte[3];
    public byte time[] = new byte[3];
    //13
    public byte lat[] = new byte[4];//纬度
    public byte bat;
    public byte lng[] = new byte[5];//经度
    public double _lat;
    public double _lng;
    public byte speed[] = new byte[3];
    //15
    public byte vec[] = new byte[5];
    public byte flag[] = new byte[2];
    public byte gpsSign;
    public byte gpsDistance[] = new byte[4];
    public byte n_code[] = new byte[2];
    public byte spi;
    // 8
    public byte bs[] = new byte[2];
    public byte area[] = new byte[2];
    public byte voltage[] = new byte[2];
    public byte recordId;

    int convertByteToHexInt(byte b) {
        return Integer.parseInt(Integer.toHexString(b & 0xFF));
    }

    /**
     * <3>纬度ddmm.mmmm（度分）格式（前面的0也将被传输） <4> 纬度半球N（北半球）或S（南半球）lat
     * <5>经度dddmm.mmmm（度分）格式（前面的0也将被传输） <6> 经度半球E（东经）或W（西经） lng
     *
     * @return
     */
    public double get_lat() {
        String lat_s = CommonUtils.byteToHex(lat).replace("0x", "");
        Double dd = Double.parseDouble(lat_s.substring(0, 2));
        Double mm = Double.parseDouble(lat_s.substring(2, 4) + "." + lat_s.substring(4)) / 60.;
        return dd + mm;
    }

    public boolean isValid(){
        byte flag = lng[4];
        return (flag >> 1 & 0x01) == 1;
    }

    public String moreData() {
        byte flag = lng[4];
        String f1 = (flag >> 3 & 0x01) == 1 ? "东经" : "西经";
        String f2 = (flag >> 2 & 0x01) == 1 ? "北纬" : "南纬";
        String f3 =  this.isValid() ? "有效定位" : "无效定位";
        return f1 + "," + f2 + "," + f3;
    }

    public double get_lng() {
        String lng_s = CommonUtils.byteToHex(lng).replace("0x", "");
        Double dd = Double.parseDouble(lng_s.substring(0, 3));
        Double mm = Double.parseDouble(lng_s.substring(3, 5) + "." + lng_s.substring(5, lng_s.length() - 1)) / 60;
        return dd + mm;
    }

    public void test() {
        GPSPacket packet = new GPSPacket();
        // packet.lat = new byte[]{48, 52, 35, 132};
        System.out.println(packet.get_lat());
    }

    public void test2() {
        GPSPacket packet = new GPSPacket();
        packet.lat = new byte[]{30, 34, 23, 84};
        packet.lng = new byte[]{0x10, 0x40, 0x37, 25, 0x0c};
        System.out.println(packet.get_lng());
    }

    @Test
    public void test3() {
        byte b = 0xC;
        String f1 = (b >> 3 & 0x01) == 1 ? "东经" : "西经";
        String f2 = (b >> 2 & 0x01) == 1 ? "北纬" : "南纬";
        String f3 = (b >> 1 & 0x01) == 1 ? "有效定位" : "无效定位";
        System.out.println(f1 + "," + f2 + "," + f3);

    }

    @Override
    public String toString() {
        return "GpsPacket{" +
                "more_data=" + moreData() + "," +
                "header=" + Integer.toHexString(header & 0xFF) +
                ", deviceId=" + CommonUtils.byteToHex(deviceId) +
                ", date=" + CommonUtils.byteToHex(date) +
                ", time=" + CommonUtils.byteToHex(time) +
                ", lat=" + CommonUtils.byteToHex(lat) + "->" + get_lat() +
                ", bat=" + Integer.toHexString(bat & 0xFF) +
                ", lng=" + CommonUtils.byteToHex(lng) + "->" + get_lng() +
                ", speed=" + CommonUtils.byteToHex(speed) +
                ", vec=" + CommonUtils.byteToHex(vec) +
                ", flag=" + CommonUtils.byteToHex(flag) +
                ", gpsSign=" + Integer.toHexString(gpsSign & 0xFF) +
                ", gpsDistance=" + CommonUtils.byteToHex(gpsDistance) +
                ", n_code=" + CommonUtils.byteToHex(n_code) +
                ", spi=" + Integer.toHexString(spi & 0xFF) +
                ", bs=" + CommonUtils.byteToHex(bs) +
                ", area=" + CommonUtils.byteToHex(area) +
                ", voltage=" + CommonUtils.byteToHex(voltage) +
                ", recordId=" + Integer.toHexString(recordId & 0xFF) +
                '}';
    }
}