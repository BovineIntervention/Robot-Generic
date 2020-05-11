package frc.taurus.joystick;

import java.util.ArrayList;


public class Joystick 
{
    public edu.wpi.first.wpilibj.Joystick wpilibJoystick;
    private ArrayList<Button> buttonList;

    public Joystick(int port) {
        wpilibJoystick = new edu.wpi.first.wpilibj.Joystick(port);
    }

    public void update() {
        for (var button : buttonList) {
            button.update();
            // TODO: logButtons()
        }
    }

    public Button addButton(int buttonId) {
        Button button = new Button(this, buttonId);
        buttonList.add(button);
        return button;
    }
};
