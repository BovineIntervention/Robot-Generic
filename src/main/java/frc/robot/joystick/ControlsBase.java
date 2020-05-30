package frc.robot.joystick;

import java.util.ArrayList;

import frc.taurus.joystick.Controller;

/**
 * ControlsBase keeps a static list of all unique controllers.
 */

public abstract class ControlsBase {

    static ArrayList<Controller> controllers;

    public ControlsBase() {
        controllers = new ArrayList<>();
    }

    static Controller addController(Controller controller) {
        if (!controllers.contains(controller)) {
            controllers.add(controller);
        }
        return controller;
    }

    // update button pressed / released for all buttons
    // including PovButtons and AxisButtons
    void update() {
        for (var controller : controllers) {
            controller.update();
        }
    }
}