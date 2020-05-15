package frc.taurus.joystick;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

import org.junit.Test;

import edu.wpi.first.wpilibj.Joystick;

public class PovButtonTest {
    @Test 
    public void povButtonTest() {

        Joystick mockJoystick = mock(Joystick.class);
        Controller controller = new Controller(mockJoystick);
        int id = 0;
        PovButton povButtonNorth = controller.addPovButton(id, -45, 45);
        PovButton povButtonEast  = controller.addPovButton(id, 90, 90);
        PovButton povButtonSouth = controller.addPovButton(id, 135, 225);
        PovButton povButtonWest  = controller.addPovButton(id, 270, 270);

        // use the when().thenReturn() functions to have the mock controller
        // return fake POV values

        // not pressed
        when(mockJoystick.getPOV(id)).thenReturn(-1);
        controller.update();
        assertFalse( povButtonNorth.getButton() );
        assertFalse( povButtonNorth.getButtonPressed() );
        assertFalse( povButtonNorth.getButtonReleased() );
        assertFalse( povButtonEast.getButton() );
        assertFalse( povButtonEast.getButtonPressed() );
        assertFalse( povButtonEast.getButtonReleased() );
        assertFalse( povButtonSouth.getButton() );
        assertFalse( povButtonSouth.getButtonPressed() );
        assertFalse( povButtonSouth.getButtonReleased() );
        assertFalse( povButtonWest.getButton() );
        assertFalse( povButtonWest.getButtonPressed() );
        assertFalse( povButtonWest.getButtonReleased() );

        // North
        when(mockJoystick.getPOV(id)).thenReturn(0);
        controller.update();
        assertTrue( povButtonNorth.getButton() );
        assertTrue( povButtonNorth.getButtonPressed() );
        assertFalse( povButtonNorth.getButtonReleased() );
        assertFalse( povButtonEast.getButton() );
        assertFalse( povButtonEast.getButtonPressed() );
        assertFalse( povButtonEast.getButtonReleased() );
        assertFalse( povButtonSouth.getButton() );
        assertFalse( povButtonSouth.getButtonPressed() );
        assertFalse( povButtonSouth.getButtonReleased() );
        assertFalse( povButtonWest.getButton() );
        assertFalse( povButtonWest.getButtonPressed() );
        assertFalse( povButtonWest.getButtonReleased() );

        // Northeast
        when(mockJoystick.getPOV(id)).thenReturn(45);
        controller.update();
        assertTrue( povButtonNorth.getButton() );
        assertFalse( povButtonNorth.getButtonPressed() );
        assertFalse( povButtonNorth.getButtonReleased() );
        assertFalse( povButtonEast.getButton() );
        assertFalse( povButtonEast.getButtonPressed() );
        assertFalse( povButtonEast.getButtonReleased() );
        assertFalse( povButtonSouth.getButton() );
        assertFalse( povButtonSouth.getButtonPressed() );
        assertFalse( povButtonSouth.getButtonReleased() );
        assertFalse( povButtonWest.getButton() );
        assertFalse( povButtonWest.getButtonPressed() );
        assertFalse( povButtonWest.getButtonReleased() );

        // East
        when(mockJoystick.getPOV(id)).thenReturn(90);
        controller.update();
        assertFalse( povButtonNorth.getButton() );
        assertFalse( povButtonNorth.getButtonPressed() );
        assertTrue( povButtonNorth.getButtonReleased() );
        assertTrue( povButtonEast.getButton() );
        assertTrue( povButtonEast.getButtonPressed() );
        assertFalse( povButtonEast.getButtonReleased() );
        assertFalse( povButtonSouth.getButton() );
        assertFalse( povButtonSouth.getButtonPressed() );
        assertFalse( povButtonSouth.getButtonReleased() );
        assertFalse( povButtonWest.getButton() );
        assertFalse( povButtonWest.getButtonPressed() );
        assertFalse( povButtonWest.getButtonReleased() );        

        // Southeast
        when(mockJoystick.getPOV(id)).thenReturn(135);
        controller.update();
        assertFalse( povButtonNorth.getButton() );
        assertFalse( povButtonNorth.getButtonPressed() );
        assertFalse( povButtonNorth.getButtonReleased() );
        assertFalse( povButtonEast.getButton() );
        assertFalse( povButtonEast.getButtonPressed() );
        assertTrue( povButtonEast.getButtonReleased() );
        assertTrue( povButtonSouth.getButton() );
        assertTrue( povButtonSouth.getButtonPressed() );
        assertFalse( povButtonSouth.getButtonReleased() );
        assertFalse( povButtonWest.getButton() );
        assertFalse( povButtonWest.getButtonPressed() );
        assertFalse( povButtonWest.getButtonReleased() );             

        // South
        when(mockJoystick.getPOV(id)).thenReturn(180);
        controller.update();
        assertFalse( povButtonNorth.getButton() );
        assertFalse( povButtonNorth.getButtonPressed() );
        assertFalse( povButtonNorth.getButtonReleased() );
        assertFalse( povButtonEast.getButton() );
        assertFalse( povButtonEast.getButtonPressed() );
        assertFalse( povButtonEast.getButtonReleased() );
        assertTrue( povButtonSouth.getButton() );
        assertFalse( povButtonSouth.getButtonPressed() );
        assertFalse( povButtonSouth.getButtonReleased() );
        assertFalse( povButtonWest.getButton() );
        assertFalse( povButtonWest.getButtonPressed() );
        assertFalse( povButtonWest.getButtonReleased() );             
        
        // Southwest
        when(mockJoystick.getPOV(id)).thenReturn(225);
        controller.update();
        assertFalse( povButtonNorth.getButton() );
        assertFalse( povButtonNorth.getButtonPressed() );
        assertFalse( povButtonNorth.getButtonReleased() );
        assertFalse( povButtonEast.getButton() );
        assertFalse( povButtonEast.getButtonPressed() );
        assertFalse( povButtonEast.getButtonReleased() );
        assertTrue( povButtonSouth.getButton() );
        assertFalse( povButtonSouth.getButtonPressed() );
        assertFalse( povButtonSouth.getButtonReleased() );
        assertFalse( povButtonWest.getButton() );
        assertFalse( povButtonWest.getButtonPressed() );
        assertFalse( povButtonWest.getButtonReleased() );             

        // West
        when(mockJoystick.getPOV(id)).thenReturn(270);
        controller.update();
        assertFalse( povButtonNorth.getButton() );
        assertFalse( povButtonNorth.getButtonPressed() );
        assertFalse( povButtonNorth.getButtonReleased() );
        assertFalse( povButtonEast.getButton() );
        assertFalse( povButtonEast.getButtonPressed() );
        assertFalse( povButtonEast.getButtonReleased() );
        assertFalse( povButtonSouth.getButton() );
        assertFalse( povButtonSouth.getButtonPressed() );
        assertTrue( povButtonSouth.getButtonReleased() );
        assertTrue( povButtonWest.getButton() );
        assertTrue( povButtonWest.getButtonPressed() );
        assertFalse( povButtonWest.getButtonReleased() );   
        
        // Northwest
        when(mockJoystick.getPOV(id)).thenReturn(315);
        controller.update();
        assertTrue( povButtonNorth.getButton() );
        assertTrue( povButtonNorth.getButtonPressed() );
        assertFalse( povButtonNorth.getButtonReleased() );
        assertFalse( povButtonEast.getButton() );
        assertFalse( povButtonEast.getButtonPressed() );
        assertFalse( povButtonEast.getButtonReleased() );
        assertFalse( povButtonSouth.getButton() );
        assertFalse( povButtonSouth.getButtonPressed() );
        assertFalse( povButtonSouth.getButtonReleased() );
        assertFalse( povButtonWest.getButton() );
        assertFalse( povButtonWest.getButtonPressed() );
        assertTrue( povButtonWest.getButtonReleased() );      
        
        // Unpressed
        when(mockJoystick.getPOV(id)).thenReturn(-1);
        controller.update();
        assertFalse( povButtonNorth.getButton() );
        assertFalse( povButtonNorth.getButtonPressed() );
        assertTrue( povButtonNorth.getButtonReleased() );
        assertFalse( povButtonEast.getButton() );
        assertFalse( povButtonEast.getButtonPressed() );
        assertFalse( povButtonEast.getButtonReleased() );
        assertFalse( povButtonSouth.getButton() );
        assertFalse( povButtonSouth.getButtonPressed() );
        assertFalse( povButtonSouth.getButtonReleased() );
        assertFalse( povButtonWest.getButton() );
        assertFalse( povButtonWest.getButtonPressed() );
        assertFalse( povButtonWest.getButtonReleased() );         
    }
}