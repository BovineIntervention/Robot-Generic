package frc.taurus.messages;


public class MessageQueueManager {
    // singleton pattern
    private static MessageQueueManager mInstance = null;
    public static MessageQueueManager getInstance() {
        if (mInstance == null) {
             mInstance = new MessageQueueManager();
        }
        return mInstance;
    }   

    public MessageQueue<JoystickGoal> driveJoystickGoalQueue;
    public MessageQueue<JoystickStatus> driveJoystickStatusQueue;

    public MessageQueue<JoystickGoal> operatorJoystickGoalQueue;
    public MessageQueue<JoystickStatus> operatorJoystickStatusQueue;    

    private MessageQueueManager() {
        driveJoystickGoalQueue = new MessageQueue<JoystickGoal>(JoystickGoal::getRootAsJoystickGoal);
        driveJoystickStatusQueue = new MessageQueue<JoystickStatus>(JoystickStatus::getRootAsJoystickStatus);

        operatorJoystickGoalQueue = new MessageQueue<JoystickGoal>(JoystickGoal::getRootAsJoystickGoal);
        operatorJoystickStatusQueue = new MessageQueue<JoystickStatus>(JoystickStatus::getRootAsJoystickStatus);


    }




}