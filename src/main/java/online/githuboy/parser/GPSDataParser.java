package online.githuboy.parser;


import online.githuboy.dto.BaseMessage;
import online.githuboy.dto.GPSPacket;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

public class GPSDataParser extends AbstractParser {

    public GPSDataParser(byte[] data) {
        super(data);
    }


    @Override
    protected boolean needParse() {
        return true;
    }

    public BaseMessage parse(byte[] data) throws CannotParseException {
        GPSPacket packet = new GPSPacket();
        ByteArrayInputStream inputStream = new ByteArrayInputStream(data);
        DataInputStream in = new DataInputStream(inputStream);
        try {
            packet.setRawData(rawData);
            packet.header = in.readByte();
            in.read(packet.deviceId);
            in.read(packet.date);
            in.read(packet.time);
            in.read(packet.lat);
            packet.bat = in.readByte();
            in.read(packet.lng);
            in.read(packet.speed);
            in.read(packet.vec);
            in.read(packet.flag);
            packet.gpsSign = in.readByte();
            in.read(packet.gpsDistance);
            in.read(packet.n_code);
            packet.spi = in.readByte();
            in.read(packet.bs);
            in.read(packet.area);
            in.read(packet.voltage);
            packet.recordId = in.readByte();
            return packet;
        } catch (IOException e) {
            e.printStackTrace();
            throw new CannotParseException(e.getMessage());
        }
    }
}
