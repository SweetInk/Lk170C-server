package online.githuboy.domain.dto;

import lombok.Data;

import java.util.Date;

@Data
public class DeviceEvent {

    private String deviceId;

    private Double lat;

    private Double lng;

    private String eventType;

    /**
     *
     */
    private String dateStr;

    /**
     * 触发时间
     */

    private Date triggerTime;

    /**
     * 是否同步到过到数据库中
     */
    private boolean synced = false;
    /**
     * 是否在线
     */
    private boolean online = true;


    public DeviceEvent deviceId(String id) {
        this.deviceId = id;
        return this;
    }

    public DeviceEvent lat(Double lat) {
        this.lat = lat;
        return this;
    }

    public DeviceEvent lng(Double lng) {
        this.lng = lng;
        return this;
    }

    public DeviceEvent event(String evt) {
        this.eventType = evt;
        return this;
    }

    public DeviceEvent dateStr(String dateStr) {
        this.dateStr = dateStr;
        return this;
    }

    public DeviceEvent triggerTime(Date triggerTime) {
        this.triggerTime = triggerTime;
        return this;
    }
}