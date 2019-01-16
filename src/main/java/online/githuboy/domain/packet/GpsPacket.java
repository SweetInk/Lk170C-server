package online.githuboy.domain.packet;

import lombok.Data;
import online.githuboy.enums.MessageTypeEnum;

/**
 * gps 数据包
 *
 * @author suchu
 * @since 2018/11/7 11:29
 */
@Data
public class GpsPacket extends DataPacket {

    /**
     * 纬度
     */
    private double lat;

    /**
     * 经度
     */
    private double lng;

    /**
     * 纬度方向
     */
    private String latDir;

    /**
     * 经度方向
     */
    private String lngDir;
    /**
     * 电池电量
     */

    private int batteryLevel;

    /**
     * 电压
     */
    private double voltage;

    /**
     * 速度（km/h)
     */
    private double speed;

    /**
     * 方向 偏离度
     */
    private double dirDegree;

    private boolean valid;

    private int seqNumber;

    private String dateStr;

    public GpsPacket() {
        super(MessageTypeEnum.GPS_DATA);
    }
}
