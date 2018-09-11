package online.githuboy.dto;


import online.githuboy.enums.MessageTypeEnum;

import java.io.Serializable;

/**
 * 基础消息
 *
 * @author suchu
 */
public class BaseMessage implements Serializable {

    /**
     * 消息类型
     */
    protected MessageTypeEnum msgType;


    /**
     * 原始数据;
     */
    protected String rawData;

    public String getRawData() {
        return rawData;
    }

    public void setRawData(String rawData) {
        this.rawData = rawData;
    }

    public BaseMessage(MessageTypeEnum msgType, String rawData) {
        this.msgType = msgType;
        this.rawData = rawData;
    }

    public BaseMessage(MessageTypeEnum msgType) {
        this.msgType = msgType;
    }


    public MessageTypeEnum getMsgType() {
        return msgType;
    }

    public void setMsgType(MessageTypeEnum msgType) {
        this.msgType = msgType;
    }

    @Override
    public String toString() {
        return "BaseMessage{" +
                "msgType=" + msgType +
                ", rawData='" + rawData + '\'' +
                '}';
    }
}
