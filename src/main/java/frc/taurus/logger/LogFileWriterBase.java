package frc.taurus.logger;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;



abstract class LogFileWriterBase {

  private static File logPath = null;
  private static String logFolder = null;



  public static File logPath() {
    if (logFolder == null) {
      updateLogFolderTimestamp("");
    }
    if (logPath != null) {
      return logPath;
    }

    String[] possiblePaths = {"/media/sda1/",   // first check if a thumb drive in the USB port
                              "/home/lvuser/",  // next, check if we are running on a roboRIO
                              "C:/",            // next, use project folder on host PC (for testing)
                              "logs/"};         // if all else fails, use something relative to java executable

    for (int k=0; k<possiblePaths.length; k++) {
      possiblePaths[k] = possiblePaths[k].replace("/", File.separator);
    }

    for (var pathName : possiblePaths) {
      File path = new File(pathName);
      if (path.exists()) {
        logPath = new File(path.getAbsolutePath() + File.separator + "logs" + File.separator + logFolder);
        if (logPath.exists()) {
          // path already exists (unit test)
          break;
        } else {
          // path doesn't exist yet -- make it
          boolean success = logPath.mkdir();
          if (success) {
            break;
          }
          success = false;  // breakpoint
          // TODO: what to do when fails to create a log folder?
        }
      }
    }
    return logPath;
  }



  // to be called by LoggerManager when we switch into auto, teleop, or test modes
  // creates a subfolder under logs with a timestamp and suffix
  public static void updateLogFolderTimestamp(String suffix) {
    if (suffix.isEmpty()) {
      logFolder = "unit_test" + File.separator;
    } else {
      LocalDateTime date = LocalDateTime.now();
      logFolder = date.format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
      logFolder = logFolder + "_" + suffix + File.separator;
    }
    logPath = null;   // force logPath to make a new folder
    logPath();        // make new folder
  }

}