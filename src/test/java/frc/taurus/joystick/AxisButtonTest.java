package frc.taurus.joystick;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

import org.junit.Test;

import edu.wpi.first.wpilibj.Joystick;

public class AxisButtonTest {

    @Test 
    public void axisButtonTest() {

        Joystick mockJoystick = mock(Joystick.class);
        Controller controller = new Controller(mockJoystick);
        int id = 0;
        AxisButton axisButton = controller.addAxisButton(id, 0.5);

        // use the when().thenReturn() functions to have the mock controller
        // return fake axis values
        when(mockJoystick.getRawAxis(id)).thenReturn(0.1);
        controller.update();
        assertFalse( axisButton.getButton() );
        assertFalse( axisButton.getButtonPressed() );
        assertFalse( axisButton.getButtonReleased() );

        when(mockJoystick.getRawAxis(id)).thenReturn(0.4);
        controller.update();
        assertFalse( axisButton.getButton() );
        assertFalse( axisButton.getButtonReleased() );
        assertFalse( axisButton.getButtonReleased() );

        // press
        when(mockJoystick.getRawAxis(id)).thenReturn(0.6);
        controller.update();
        assertTrue( axisButton.getButton() );
        assertTrue( axisButton.getButtonPressed() );
        assertFalse( axisButton.getButtonReleased() );

        when(mockJoystick.getRawAxis(id)).thenReturn(1.0);
        controller.update();        
        assertTrue( axisButton.getButton() );
        assertFalse( axisButton.getButtonPressed() );
        assertFalse( axisButton.getButtonReleased() );

        // release
        when(mockJoystick.getRawAxis(id)).thenReturn(0.3);
        controller.update();
        assertFalse( axisButton.getButton() );
        assertFalse( axisButton.getButtonPressed() );
        assertTrue( axisButton.getButtonReleased() );

        when(mockJoystick.getRawAxis(id)).thenReturn(0.3);
        controller.update();
        assertFalse( axisButton.getButton() );
        assertFalse( axisButton.getButtonPressed() );
        assertFalse( axisButton.getButtonReleased() );             
       
    }
}