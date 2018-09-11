package online.githuboy.dto;


import online.githuboy.enums.MessageTypeEnum;

/**
 * 多基站协议包
 */
public class HqNbr extends BaseMessage {

    public HqNbr() {
        super(MessageTypeEnum.BS_NBR);
    }
}
