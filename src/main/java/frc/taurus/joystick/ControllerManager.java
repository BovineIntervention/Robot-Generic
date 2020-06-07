package frc.taurus.joystick;

import java.util.ArrayList;
import java.util.HashSet;

public class ControllerManager {

  // a Set does not allow duplicates, which is what we want
  HashSet<Controller> controllerSet;

  public ControllerManager() {
    controllerSet = new HashSet<Controller>();
  }

  public int size() { return controllerSet.size(); }
  
  public void register(Controller controller) {
    controllerSet.add(controller);
  }

  /**
   * Register all physical controllers used by driver and operator control schemes
   * Usage:
   *   controllerManager.register( driverControls.getControllersList() );
   *   controllerManager.register( superstructureControls.getControllersList() );
   * @param controllers ArrayList of all controllers used by user control scheme
   */
  public void register(ArrayList<Controller> controllers) {
    controllerSet.addAll(controllers);
  }

  /**
   * Update all Controllers that were registered
   * (Update all axis and button values, then create JoystickStatus message)
   */
  public void update() {
    for (var controller : controllerSet) {
      controller.update();
    }
  }
}