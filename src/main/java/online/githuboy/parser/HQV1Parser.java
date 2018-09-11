package online.githuboy.parser;


import online.githuboy.dto.BaseMessage;
import online.githuboy.dto.HqV1;

/**
 * 一般数据解析器
 *
 * @author suchu
 */
public class HQV1Parser extends AbstractParser {

    public HQV1Parser(String data) {
        super(data);
    }

    @Override
    public BaseMessage parse(byte[] data) throws CannotParseException {
        String fragments[] = this.rawData.split(",");
        HqV1 v1 = new HqV1();
        v1.setRawData(this.rawData);
        //TODO 解析V1数据包
        return v1;
    }
}
