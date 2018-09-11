package online.githuboy.parser;

/**
 * 数据解析接口
 *
 * @param <T>
 * @author suchu
 */
public interface DataParser<T> {
    T parse(byte[] data) throws CannotParseException;
}
