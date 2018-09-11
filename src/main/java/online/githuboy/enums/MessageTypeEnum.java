package online.githuboy.enums;

/**
 * 协议类型 枚举
 *
 * @author suchu
 * @since 2018/9/11
 */
public enum MessageTypeEnum {
    GPS_DATA,//GPS 二进制数据
    BS_V1,//基站协议
    WIFI_V5,//WIFI定位协议
    BS_NBR,//多基站协议
    SIM_V19,//SIM卡协议
    V4//平台应答协议
}
