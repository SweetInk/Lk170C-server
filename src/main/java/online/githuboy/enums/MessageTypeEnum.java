package online.githuboy.enums;

/**
 * 协议类型
 *
 * @author suchu
 */
public enum MessageTypeEnum {
    GPS_DATA,//GPS数据
    BS_V1,//基站协议
    WIFI_V5,//WIFI定位协议
    NBR,//多基站协议
    SIM_V19,//SIM卡协议
    V4,//平台应答协议,
    V1,//心跳包
    INVALID,
}
