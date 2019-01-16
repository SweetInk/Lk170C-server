package online.githuboy.domain.packet;

import lombok.Data;
import online.githuboy.enums.MessageTypeEnum;

/**
 * 多基站协议
 *
 * @author suchu
 * @since 2018/11/7 14:18
 */
@Data
public class NBRPacket extends DataPacket {
    public NBRPacket() {
        super(MessageTypeEnum.NBR);
    }
}
