package frc.taurus.joystick;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.nio.ByteBuffer;
import java.util.ArrayList;

import org.junit.Test;

import edu.wpi.first.wpilibj.Joystick;
import frc.taurus.messages.MessageQueue;

public class ControllerManagerTest {

  // check that the same controller type can be used on different ports
  @Test
  public void sameJoystickTest() {
    var dummyStatusQueue = new MessageQueue<ByteBuffer>();
    var dummyGoalQueue = new MessageQueue<ByteBuffer>();

    XboxController xbox1 = new XboxController(new Joystick(0), dummyStatusQueue, dummyGoalQueue);
    XboxController xbox2 = new XboxController(new Joystick(1), dummyStatusQueue, dummyGoalQueue);
    XboxController xbox3 = new XboxController(new Joystick(2), dummyStatusQueue, dummyGoalQueue);
    XboxController xbox4 = new XboxController(new Joystick(3), dummyStatusQueue, dummyGoalQueue);

    ArrayList<Controller> controllerList = new ArrayList<Controller>();
    controllerList.add(xbox1);
    controllerList.add(xbox2);
    controllerList.add(xbox3);
    controllerList.add(xbox4);
    
    ControllerManager manager = new ControllerManager();
    manager.register(controllerList);
    assertEquals(4, manager.size());
    
    // test that adding duplicate controllers does not increase size of list in ControllerManager
    manager.register(controllerList);
    assertEquals(4, manager.size());
  }

  // check that controllers are updated (mock the joysticks)
  public void updateTest() {
    var dummyStatusQueue = new MessageQueue<ByteBuffer>();
    var dummyGoalQueue = new MessageQueue<ByteBuffer>();
    ControllerManager manager = new ControllerManager();

    Joystick mockJoystick1 = mock(Joystick.class);
    Joystick mockJoystick2 = mock(Joystick.class);
    Joystick mockJoystick3 = mock(Joystick.class);

    // have the mock joysticks report different ports
    when(mockJoystick1.getPort()).thenReturn(1);
    when(mockJoystick2.getPort()).thenReturn(2);
    when(mockJoystick3.getPort()).thenReturn(3);

    // create 3 controllers
    Controller ctrlr1 = new XboxController(mockJoystick1, dummyStatusQueue, dummyGoalQueue);
    Controller ctrlr2 = new ThrustmasterController(mockJoystick2, dummyStatusQueue, dummyGoalQueue);
    Controller ctrlr3 = new ButtonBoardController(mockJoystick3, dummyStatusQueue, dummyGoalQueue);

    int id = 13; // the button we are testing

    // press on 1st controller
    when(mockJoystick1.getRawButton(id)).thenReturn(true);
    when(mockJoystick2.getRawButton(id)).thenReturn(false);
    when(mockJoystick3.getRawButton(id)).thenReturn(false);

    manager.update(); // this is what we are testing

    assertTrue(ctrlr1.getButton(id));
    assertFalse(ctrlr2.getButton(id));
    assertFalse(ctrlr3.getButton(id));

    // press on 2nd controller
    when(mockJoystick1.getRawButton(id)).thenReturn(false);
    when(mockJoystick2.getRawButton(id)).thenReturn(true);
    when(mockJoystick3.getRawButton(id)).thenReturn(false);

    manager.update(); // this is what we are testing

    assertFalse(ctrlr1.getButton(id));
    assertTrue(ctrlr2.getButton(id));
    assertFalse(ctrlr3.getButton(id));

    // press on 3rd controller
    when(mockJoystick1.getRawButton(id)).thenReturn(false);
    when(mockJoystick2.getRawButton(id)).thenReturn(false);
    when(mockJoystick3.getRawButton(id)).thenReturn(true);

    manager.update(); // this is what we are testing

    assertFalse(ctrlr1.getButton(id));
    assertFalse(ctrlr2.getButton(id));
    assertTrue(ctrlr3.getButton(id));

  }

}