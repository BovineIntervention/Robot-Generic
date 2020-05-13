package frc.taurus.joystick;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class ButtonTest {
    @Test 
    public void pressTest() {

        Joystick joystick = new Joystick(0);
        Button button = joystick.addButton(0);

        // initially not pressed
        button.update(false);
        assertFalse( button.onPress() );
        assertFalse( button.onRelease() );
        assertFalse( button.isPressed() );

        button.update(false);
        assertFalse( button.onPress() );
        assertFalse( button.onRelease() );
        assertFalse( button.isPressed() );

        // press
        button.update(true);
        assertTrue( button.onPress() );
        assertFalse( button.onRelease() );
        assertTrue( button.isPressed() );

        button.update(true);
        assertFalse( button.onPress() );
        assertFalse( button.onRelease() );
        assertTrue( button.isPressed() );

        // release
        button.update(false);
        assertFalse( button.onPress() );
        assertTrue( button.onRelease() );
        assertFalse( button.isPressed() );

        button.update(false);
        assertFalse( button.onPress() );
        assertFalse( button.onRelease() );
        assertFalse( button.isPressed() );

    }
}