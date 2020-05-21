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

    static int queueDepth = 32;

    public MessageQueue<JoystickGoal> driveJoystickGoalQueue;
    public MessageQueue<JoystickStatus> driveJoystickStatusQueue;

    public MessageQueue<JoystickGoal> operatorJoystickGoalQueue;
    public MessageQueue<JoystickStatus> operatorJoystickStatusQueue;    

    private MessageQueueManager() {
        driveJoystickGoalQueue = new MessageQueue<JoystickGoal>(queueDepth, JoystickGoal::getRootAsJoystickGoal);
        driveJoystickStatusQueue = new MessageQueue<JoystickStatus>(queueDepth, JoystickStatus::getRootAsJoystickStatus);

        operatorJoystickGoalQueue = new MessageQueue<JoystickGoal>(queueDepth, JoystickGoal::getRootAsJoystickGoal);
        operatorJoystickStatusQueue = new MessageQueue<JoystickStatus>(queueDepth, JoystickStatus::getRootAsJoystickStatus);


    }




}