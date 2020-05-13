package frc.taurus.joystick;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

public class JoystickTest {
    @Test 
    public void handleDeadbandTest() {

        double eps = 1e-9;

        assertEquals(Joystick.handleDeadband( 0.5,  0.05),  0.5, eps);
        assertEquals(Joystick.handleDeadband(-0.5,  0.05), -0.5, eps);
        assertEquals(Joystick.handleDeadband( 0.02, 0.05),  0.0, eps);
        assertEquals(Joystick.handleDeadband(-0.03, 0.05),  0.0, eps);
        assertEquals(Joystick.handleDeadband( 0.0,  0.05),  0.0, eps);

        assertEquals(Joystick.handleDeadband( 0.5,  0.025),  0.5,  eps);
        assertEquals(Joystick.handleDeadband(-0.5,  0.025), -0.5,  eps);
        assertEquals(Joystick.handleDeadband( 0.02, 0.025),  0.0,  eps);
        assertEquals(Joystick.handleDeadband(-0.03, 0.025), -0.03, eps);
        assertEquals(Joystick.handleDeadband( 0.0,  0.025),  0.0,  eps);        
    }
}