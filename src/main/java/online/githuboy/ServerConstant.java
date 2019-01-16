package online.githuboy;

/**
 * Desc:
 *
 * @author suchu
 * @version 1.0
 * @since 2018/1/27
 */
public class ServerConstant {


    /**
     * <code>$</code>
     */
    public final static byte GPS_HEADER = 0x24;


    /**
     * device readTimeout value
     */
    public final static int DEVICE_READ_TIMEOUT = 3 * 60;


    /**
     * <code>*</code>
     */
    public final static byte STATUS_HEADER = 0x2a;

    /**
     * <code>#</code>
     */
    public final static byte STATUS_END = 0x23;

    public static final int PORT = 8080;
}
