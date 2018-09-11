package online.githuboy.parser;

/**
 * 无法解析的数据包抛出异常
 *
 * @author suchu
 */
public class CannotParseException extends Exception {
    public CannotParseException(String msg) {
        super("UnSupport data:" + msg);
    }
}

