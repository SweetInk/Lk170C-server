package online.githuboy.parser;


import online.githuboy.dto.BaseMessage;

import java.io.UnsupportedEncodingException;

/**
 * 数据解析
 *
 * @author suchu
 */
public abstract class AbstractParser implements DataParser<BaseMessage> {
    protected byte[] srcData;

    protected String rawData;

    public AbstractParser() {
    }

    public AbstractParser(String data) {
        this.srcData = data.getBytes();
    }

    public AbstractParser(byte[] data) {
        this.srcData = data;
    }

    protected boolean needParse() {
        String text = null;
        try {
            text = new String(srcData, "utf-8");
            this.rawData = text;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return false;
        }
        if (text.startsWith("*") && text.endsWith("#") && text.split(",").length > 5)
            return true;
        else return false;
    }


    public BaseMessage process() throws CannotParseException {
        if (needParse())
            return parse(srcData);
        return null;
    }
}
