package online.githuboy.domain.packet;

import lombok.Data;
import online.githuboy.enums.MessageTypeEnum;

/**
 * @author suchu
 * @since 2018/11/7 14:18
 */
@Data
public class HeartBeatPacket extends DataPacket {

    private boolean isValid;

    private double lat = 0.;

    private double lng = 0.;

    public HeartBeatPacket() {
        super(MessageTypeEnum.V1);
    }
}
