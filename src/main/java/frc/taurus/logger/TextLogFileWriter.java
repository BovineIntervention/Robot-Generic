package frc.taurus.logger;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;


public class TextLogFileWriter extends LogFileWriterBase {

  final String filename;
  BufferedWriter writer;
  int BUFFER_SIZE = 16 * 1024; // 16 kB

  public TextLogFileWriter(final String filename) {
    this.filename = filename;
    try {
      File file = new File(logPath() + File.separator + filename);
      file.createNewFile();
      writer = new BufferedWriter(new FileWriter(file), BUFFER_SIZE);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  void write(final String s) {
  try {
    if (writer != null) {
      writer.write(s);
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