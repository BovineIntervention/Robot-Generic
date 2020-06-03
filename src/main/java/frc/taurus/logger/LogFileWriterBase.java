package frc.taurus.logger;

import java.io.File;



abstract class LogFileWriterBase {

  private static File logPath = null;


  public static File logPath() {
    if (logPath != null)
      return logPath;

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
}