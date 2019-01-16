package online.githuboy.domain.packet;

import lombok.Data;
import online.githuboy.enums.MessageTypeEnum;

/**
 * GPS数据包数据包
 *
 * @author suchu
 * @since 2018/11/7 11:28
 */
@Data
public class DataPacket {
    /**
     * 消息类型
     */
    protected MessageTypeEnum msgType;


    protected Integer status;

    /**
     * 设备ID
     */
    protected String deviceId;

    /**
     * 原始数据;
     */
    protected String rawData;

    public DataPacket(MessageTypeEnum msgType, String rawData) {
        this.msgType = msgType;
        this.rawData = rawData;
    }

    public DataPacket(MessageTypeEnum msgType) {
        this.msgType = msgType;
    }

    public String getRawData() {
        return rawData;
    }

    public void setRawData(String rawData) {
        this.rawData = rawData;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public MessageTypeEnum getMsgType() {
        return msgType;
    }

    public void setMsgType(MessageTypeEnum msgType) {
        this.msgType = msgType;
    }

}
