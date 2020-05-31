package frc.taurus.logger;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import com.google.flatbuffers.ByteBufferUtil;

import frc.taurus.config.ChannelIntf;
import frc.taurus.config.ChannelManager;

public class LogFileReader {

    RandomAccessFile file;

    public LogFileReader(ChannelIntf channel)  {
System.out.println("LogFileReader " + channel.getNum() + " : " + channel.getName());
        String filename = ChannelManager.getInstance().getLogFilename(channel);
        try {
            file = new RandomAccessFile(new File(filename), "r");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public ByteBuffer getNextTable() {
        byte bytes[] = new byte[0];
        try {
            byte[] prefix = new byte[4];    // prefix is always 4 bytes
            file.readFully(prefix);         // get prefix
            int tableSize = ByteBufferUtil.getSizePrefix(ByteBuffer.wrap(prefix).order(ByteOrder.LITTLE_ENDIAN));
            bytes = new byte[tableSize];
            file.readFully(bytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ByteBuffer.wrap(bytes); 
    }

    public void close() {
        try {
            file.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}