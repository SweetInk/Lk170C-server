package online.githuboy;

import io.netty.util.AttributeKey;

import java.util.Date;

/**
 * ATTRIBUTE KEYS
 *
 * @author suchu
 * @since 2018/11/5 14:04
 */
public class Attributes {
    public final static AttributeKey<String> DEVICE_ID = AttributeKey.newInstance("deviceId");
    public final static AttributeKey<Date> LAST_ACTIVE_TIME = AttributeKey.newInstance("last_active_time");
}
