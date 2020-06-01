package frc.taurus.logger;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.HashMap;

public class LogFileWriter {

  private static File basePath = null;
  private HashMap<String, BufferedWriter> textOpenFileList = new HashMap<>();
  private HashMap<String, BufferedOutputStream> binaryOpenFileList = new HashMap<>();
  int BUFFER_SIZE = 16 * 1024; // 16 kB

  public LogFileWriter() {
    if (basePath == null) {
      basePath = findBasePath();
      System.out.println("Log directory is " + basePath);
    }
  }

  public File findBasePath() {
    String[] possiblePaths = {"/media/sda1/",   // first check if a thumb drive in the USB port
                              "/home/lvuser/",  // next, check if we are running on a roboRIO
                              "C:/" };          // next, use project folder on host PC (for testing)

    for (var pathName : possiblePaths) {
      File path = new File(pathName);
      String absPathName = path.getAbsolutePath();
      if (path.exists()) {
        File logPath = new File(absPathName + "/" + "logs");
        if (!logPath.exists()) {
          logPath.mkdir();
        }
        return logPath;
      }
    }
    return new File("logs/");
  }

  public String getBasePath() {
    return basePath.getAbsolutePath();
  }

  BufferedWriter getTextFile(String filename) {
    BufferedWriter writer = null;
    try {
      if (textOpenFileList.containsKey(filename)) {
        return textOpenFileList.get(filename);
      }

      // filename not open -- create it
      File file = new File(basePath + "/" + filename);
      file.createNewFile();
      writer = new BufferedWriter(new FileWriter(file), BUFFER_SIZE);

      textOpenFileList.put(filename, writer);

    } catch (IOException e) {
      e.printStackTrace();
    }
    return writer;
  }

  BufferedOutputStream getBinaryFile(String filename) {
    BufferedOutputStream writer = null;
    try {
      if (binaryOpenFileList.containsKey(filename)) {
        return binaryOpenFileList.get(filename);
      }

      // filename not open -- create it
      File file = new File(basePath + "/" + filename);
      file.createNewFile();
      writer = new BufferedOutputStream(new FileOutputStream(file), BUFFER_SIZE);

      binaryOpenFileList.put(filename, writer);

    } catch (IOException e) {
      e.printStackTrace();
    }
    return writer;
  }

  void writeText(String filename, String text) {
    BufferedWriter writer = getTextFile(filename);
    try {
      writer.write(text);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  void writeBytes(String filename, ByteBuffer bb) {
    byte[] b = new byte[bb.remaining()]; // create byte array of the correct size
    bb.get(b); // convert ByteBuffer to byte array
    writeBytes(filename, b);
  }

  void writeBytes(String filename, byte[] b) {
    BufferedOutputStream writer = getBinaryFile(filename);
    try {
      writer.write(b);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public void flush() {
    try {
      for (var writer : textOpenFileList.values()) {
        writer.flush();
      }
      for (var writer : binaryOpenFileList.values()) {
        writer.flush();
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public void close() {
    try {
      for (var writer : textOpenFileList.values()) {
        writer.close();
      }
      for (var writer : binaryOpenFileList.values()) {
        writer.close();
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}