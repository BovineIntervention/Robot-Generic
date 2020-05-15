package frc.taurus.joystick;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

public class ControllerTest {
    @Test
    public void addButtonTest() {
        // TODO: test if buttons are added to button list
    }

    @Test 
    public void handleDeadbandTest() {

        double eps = 1e-9;

        assertEquals( 0.5, Controller.applyDeadband( 0.5,  0.05), eps);
        assertEquals(-0.5, Controller.applyDeadband(-0.5,  0.05), eps);
        assertEquals( 0.0, Controller.applyDeadband( 0.02, 0.05), eps);
        assertEquals( 0.0, Controller.applyDeadband(-0.03, 0.05), eps);
        assertEquals( 0.0, Controller.applyDeadband( 0.0,  0.05), eps);

        assertEquals( 0.5,  Controller.applyDeadband( 0.5,  0.025), eps);
        assertEquals(-0.5,  Controller.applyDeadband(-0.5,  0.025), eps);
        assertEquals( 0.0,  Controller.applyDeadband( 0.02, 0.025), eps);
        assertEquals(-0.03, Controller.applyDeadband(-0.03, 0.025), eps);
        assertEquals( 0.0,  Controller.applyDeadband( 0.0,  0.025), eps);        
    }
}