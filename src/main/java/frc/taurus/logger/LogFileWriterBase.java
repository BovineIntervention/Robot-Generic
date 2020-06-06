package frc.taurus.logger;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;



abstract class LogFileWriterBase {

  private static File basePath = null;
  private static File logPath = null;
  private static String logFolder = null;



  public static File basePath() {
    if (basePath != null) {
      return basePath;
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
        basePath = new File(path.getAbsolutePath() + File.separator + "logs" + File.separator);
        if (!basePath.exists()) {
          break;
        } else {
          // path doesn't exist yet -- make it
          boolean success = basePath.mkdir();
          if (success) {
            break;
          }
          success = false;  // breakpoint
          // TODO: what to do when fails to create a log folder?
        }
      }
    }
    return basePath;
  }


  public static File logPath() {
    if (logFolder == null) {
      updateLogFolderTimestamp("");
    }

    if (logPath == null) {
      logPath = new File(basePath().getAbsolutePath() + File.separator + logFolder);
      if (!logPath.exists()) {
        // path doesn't exist yet -- make it
        boolean success = logPath.mkdir();
        if (!success) {
          // TODO: what to do when fails to create a log folder?
        }
      }
    }
    return logPath;
  }




  //https://stackoverflow.com/questions/2341943/how-can-i-find-out-if-code-is-running-inside-a-junit-test-or-not
  public static boolean isJUnitTest() {
    StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
    List<StackTraceElement> list = Arrays.asList(stackTrace);
    for (StackTraceElement element : list) {
        if (element.getClassName().startsWith("org.junit.")) {
            return true;
        }           
    }
    return false;
  }  




  // to be called by LoggerManager when we switch into auto, teleop, or test modes
  // creates a subfolder under logs with a timestamp and suffix
  public static void updateLogFolderTimestamp(String suffix) {
    if (LogFileWriterBase.isJUnitTest()) {
      // do not use a timestamp for unit tests
      // (which would create too many folders)
      logFolder = "unit_test" + "_" + suffix + File.separator;
    } else {
      // note: this code will never be unit tested
      LocalDateTime date = LocalDateTime.now();
      logFolder = date.format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
      logFolder = logFolder + "_" + suffix + File.separator;
    }
    logPath = null;   // force logPath to make a new folder
    logPath();        // make new folder now
  }

}