package online.githuboy.utils;


import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.SimpleTimeZone;
import java.util.TimeZone;

@Slf4j
public class CommonUtils {
    private CommonUtils() {
    }

    public static String byteToHex(byte[] buffer) {
        return byteToHex(buffer, true);
    }

    public static String byteToHex(byte[] buffer, boolean prefix) {
        StringBuilder sb = new StringBuilder(128);
        if (prefix) {
            sb.append("0x");
        }
        for (int i = 0; i < buffer.length; i++) {
            String hv = Integer.toHexString(buffer[i] & 0xFF).toUpperCase();
            if (hv.length() < 2)
                sb.append("0");
            sb.append(hv);
        }
        return sb.toString();
    }

    public static int bytesToInteger(byte[] bytes) {
        return (bytes[0] & 0xFF) << 24 |
                (bytes[1] & 0xFF) << 16 |
                (bytes[2] & 0xFF) << 8 |
                (bytes[3] & 0xFF);
    }

    public static String parseDate(byte[] bytes) {
        int hour = Integer.valueOf(Integer.toHexString(bytes[0]));
        int minute = Integer.valueOf(Integer.toHexString(bytes[1]));
        int seconds = Integer.valueOf(Integer.toHexString(bytes[2]));
        int day = Integer.valueOf(Integer.toHexString(bytes[3]));
        int month = Integer.valueOf(Integer.toHexString(bytes[4]));
        int year = Integer.valueOf(Integer.toHexString(bytes[5]));
        String dateStr = String.format("20%02d-%02d-%02d %02d:%02d:%02d", year, month, day, hour, minute, seconds);
        return timeZoneTransfer(dateStr, "yyyy-MM-dd HH:mm:ss", "0", "+8");

    }

    /**
     * 获取偏移时区时间
     *
     * @param timeZoneOffset
     * @return
     */
    public static String getFormatedDateString(float timeZoneOffset) {
        if (timeZoneOffset > 13 || timeZoneOffset < -12) {
            timeZoneOffset = 0;
        }

        int newTime = (int) (timeZoneOffset * 60 * 60 * 1000);
        TimeZone timeZone;
        String[] ids = TimeZone.getAvailableIDs(newTime);
        if (ids.length == 0) {
            timeZone = TimeZone.getDefault();
        } else {
            timeZone = new SimpleTimeZone(newTime, ids[0]);
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        sdf.setTimeZone(timeZone);
        return sdf.format(new Date());
    }

    /**
     * 构造设备回复数据
     *
     * @param deviceId 设备id
     * @param type     消息类型
     * @return
     */
    public static String buildResponseData(String deviceId, String type) {
        return "*HQ," + deviceId + ",V4," + type + "," + getFormatedDateString(0) + "#";
    }


    /**
     * 时区转换
     *
     * @param time           时间字符串
     * @param pattern        格式 "yyyy-MM-dd HH:mm"
     * @param nowTimeZone    eg:+8，0，+9，-1 等等
     * @param targetTimeZone 同nowTimeZone
     * @return
     */
    public static String timeZoneTransfer(String time, String pattern, String nowTimeZone, String targetTimeZone) {
        if (StringUtils.isBlank(time)) {
            return "";
        }
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT" + nowTimeZone));
        Date date;
        try {
            date = simpleDateFormat.parse(time);
        } catch (ParseException e) {
            log.error("时间转换出错。", e);
            return "";
        }
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT" + targetTimeZone));
        return simpleDateFormat.format(date);
    }

    @Test
    public void testCvt() {
        System.out.println(timeZoneTransfer("2018-11-13 23:36:54", "yyyy-MM-dd HH:mm:ss", "0", "+8")
        );
    }
}
