package frc.taurus.joystick;

import java.nio.ByteBuffer;
import java.util.HashSet;

import edu.wpi.first.wpilibj.Joystick;
import frc.taurus.messages.MessageQueue;


public class ControllerManager {
  HashSet<Controller> controllerList = new HashSet<Controller>();
  
  public Controller getController(ControllerType type, Joystick joystick, double deadband, 
                                  MessageQueue<ByteBuffer> joystickStausQueue, 
                                  MessageQueue<ByteBuffer> joystickGoalQueue) {

    // check if port is already in use
    for (var controller : controllerList) {
      if (controller.wpilibJoystick.getPort() == joystick.getPort()) {
          // different controllers trying to use the same port ==> error
          throw new RuntimeException("Controller is trying to use a previously reserved port");
      }
    }

    Controller controller;
    switch (type) {
      default:
      case XBOX:
        controller = new XboxController(joystick, deadband, joystickStausQueue, joystickGoalQueue);
        break;
      case THRUSTMASTER:
        controller = new ThrustmasterController(joystick, deadband, joystickStausQueue, joystickGoalQueue);
        break;
      case BUTTON_BOARD:
        controller = new ButtonBoardController(joystick, deadband, joystickStausQueue, joystickGoalQueue);
        break;
    }

    controllerList.add(controller); // keep list of all instantiated controllers
    return controller;
  }

  // update all controllers that were instantiated
  public void update() {
    for (var controller : controllerList) {
      controller.update();
    }
  }
}