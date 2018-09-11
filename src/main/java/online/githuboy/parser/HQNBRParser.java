package online.githuboy.parser;


import online.githuboy.dto.BaseMessage;
import online.githuboy.dto.HqNbr;

/**
 * 多基站解析器
 *
 * @author suchu
 */
public class HQNBRParser extends AbstractParser {

    public HQNBRParser(String data) {
        super(data);
    }

    public BaseMessage parse(byte[] data) throws CannotParseException {
        HqNbr nbr = new HqNbr();
        nbr.setRawData(this.rawData);
        //TODO 解析NBR数据包
        return nbr;
    }
}
