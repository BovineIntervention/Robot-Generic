package frc.taurus.logger;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;


public class BinaryLogFileWriter extends LogFileWriterBase {

  final String filename;
  BufferedOutputStream writer;
  static final int BUFFER_SIZE = 16 * 1024; // 16 kB

  public BinaryLogFileWriter(final String filename) {
    this.filename = filename;
    try {
      File file = new File(logPath() + "/" + filename);
      file.createNewFile();
      writer = new BufferedOutputStream(new FileOutputStream(file), BUFFER_SIZE);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  void write(ByteBuffer bb) {
    byte[] b = new byte[bb.remaining()]; // create byte array of the correct size
    bb.get(b); // convert ByteBuffer to byte array
    write(b);
  }

  void write(byte[] b) {
    try {
      if (writer != null) {
        writer.write(b);
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public void flush() {
    try {
      if (writer != null) {
        writer.flush();
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public void close() {
    try {
      if (writer != null) {
        writer.close();
      }

    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}