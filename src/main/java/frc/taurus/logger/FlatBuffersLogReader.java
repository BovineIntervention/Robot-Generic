package frc.taurus.logger;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import com.google.flatbuffers.ByteBufferUtil;

public class FlatBuffersLogReader {

  RandomAccessFile file;

  public FlatBuffersLogReader(final String filename) {
    try {
      String fullPathFilename = LogFileWriterBase.logPath() + File.separator + filename;
      file = new RandomAccessFile(new File(fullPathFilename), "r");
    } catch (final FileNotFoundException e) {
      e.printStackTrace();
    }
  }

  public ByteBuffer getNextTable() {
    byte bytes[] = new byte[0];
    try {
      final byte[] prefix = new byte[4]; // prefix is always 4 bytes
      file.readFully(prefix); // get prefix
      final int tableSize = ByteBufferUtil.getSizePrefix(ByteBuffer.wrap(prefix).order(ByteOrder.LITTLE_ENDIAN));
      bytes = new byte[tableSize];
      file.readFully(bytes);
    } catch (final IOException e) {
      e.printStackTrace();
    }
    return ByteBuffer.wrap(bytes);
  }

  public void close() {
    try {
      file.close();
    } catch (final IOException e) {
      e.printStackTrace();
    }
  }
}