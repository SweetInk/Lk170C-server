package online.githuboy.dto;


import online.githuboy.enums.MessageTypeEnum;

/**
 * 一般信息包
 *
 * @author suchu
 */

public class HqV1 extends BaseMessage {

    public HqV1() {
        super(MessageTypeEnum.BS_V1);
    }

    public HqV1(String rawData) {
        super(MessageTypeEnum.BS_V1, rawData);
    }

}
