import uk.ac.warwick.dcs.maze.logic.*;
import java.awt.Point;

public class RandomController implements IRobotController {
    // the robot in the maze
    private IRobot robot;
    // a flag to indicate whether we are looking for a path
    private boolean active = false;
    // a value (in ms) indicating how long we should wait
    // between moves
	//a value indicating how many steps the bot has taken
	private int counter = 0;
    
	private int delay;
	//A random number for determining the direction of the bot
	private int rand;
	//A random number to determine when the bot should change direction
	private int randchange;

    // this method is called when the "start" button is clicked
    // in the user interface
    public void start() {
        this.active = true;

        // loop while we haven't found the exit and the agent
        // has not been interrupted
        while(!robot.getLocation().equals(robot.getTargetLocation()) && active) {

			
		randchange = (int)Math.round(Math.random()*6);
			
			while (robot.look(IRobot.AHEAD) == IRobot.WALL || randchange == 1) {  //If the robot is facing a wall it will choose a new direction
				rand = (int)Math.round(Math.random()*4);		//We change the random number here so if the direction chosen does no fufill the while statement
				randchange = (int)Math.round(Math.random()*6);  // it doesnt use the same random numbers again
				switch (rand) { 		//This switch chooses the direction based on the value of the random number (rand)
				case 0: //0-0.4999...
				case 4: //3.5-3.999...
					robot.face(IRobot.AHEAD);
					robot.getLogger().log(IRobot.AHEAD);
					break;
				case 1: //0.5-1.49999...
					robot.face(IRobot.LEFT);
					robot.getLogger().log(IRobot.LEFT);
					break;
				case 2: //1.5-2.4999...
					robot.face(IRobot.RIGHT);
					robot.getLogger().log(IRobot.RIGHT);
					break;
				case 3: //2.5-3.4999...
					robot.face(IRobot.BEHIND);
					robot.getLogger().log(IRobot.BEHIND);
					break;
				}
			}	
            // move one step into the direction the robot is facing
            robot.advance();
			//Adds 1 to counter after it moves
			counter = counter++;

            // wait for a while if we are supposed to
            if (delay > 0)
                robot.sleep(delay);
		}
	}

    // this method returns a description of this controller
    public String getDescription() {
        return "A controller which randomly chooses where to go";
    }

    // sets the delay
    public void setDelay(int millis) {
        delay = millis;
    }

    // gets the current delay
    public int getDelay() {
        return delay;
    }

    // stops the controller and the counter
    public void reset() {
		setCounter(0);
        active = false;
    }

    // sets the reference to the robot
    public void setRobot(IRobot robot) {
        this.robot = robot;
    }
	
	// simple counter for counting how many steps the bot takes.
    public int getCounter() {
        return counter;
    }
	//sets the value of the counter
	public void setCounter(int number) {
        counter = number;
    }
}
