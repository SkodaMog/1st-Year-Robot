import uk.ac.warwick.dcs.maze.logic.*;
import java.awt.Point;

public class HomingController implements IRobotController {
    // the robot in the maze
    private IRobot robot;
    // a flag to indicate whether we are looking for a path
    private boolean active = false;
    // a value (in ms) indicating how long we should wait
    // between moves
    private int delay;
	//a value indicating how many steps the bot has taken
	private int counter = 0;
	// a random number
	private int rand;

    // this method is called when the "start" button is clicked
    // in the user interface
    public void start() {
        this.active = true;
		//Robot only tops once it reaches the target
		while(!robot.getLocation().equals(robot.getTargetLocation()) && active) {
			
			//This sets our direction to head using our method that uses the "homing" features
			int direction = determineHeading(robot);
			
			robot.setHeading(direction); //This sets our bearing to that which the determineheading method gives us.
			
			while (robot.look(IRobot.AHEAD) == IRobot.WALL) {  //If the robot is facing a wall it will choose a new direction, this is a backup to our "determine heading" method incase it fails (it shouldnt)
				rand = (int)Math.round(Math.random()*4); //We change the random number here so if the direction chosen does no fufill the while statement it doesnt use the same random number again
				switch (rand) {
				case 0: //0-0.4999...
				case 4: //3.5-3.999...
					robot.face(IRobot.AHEAD);
					break;
				case 1: //0.5-1.49999...
					robot.face(IRobot.LEFT);
					break;
				case 2: //1.5-2.4999...
					robot.face(IRobot.RIGHT);
					break;
				case 3: //2.5-3.4999...
					robot.face(IRobot.BEHIND);
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
	
	

    // this method returns 1 if the target is north of the
    // robot, -1 if the target is south of the robot, or
    // 0 if otherwise.
	public byte isTargetNorth() {
	byte yvalue;
		
		if (((robot.getTargetLocation().y) - (robot.getLocation().y)) > 0) //this calculation determines whether the robot or target has a higher y coordinate
			yvalue = -1;
		else if (((robot.getTargetLocation().y) - (robot.getLocation().y))  < 0) //this calculation determines whether the robot or target has a higher y coordinate
			yvalue = 1;
		else 
			yvalue = 0;
	
	return  yvalue;
	}

    // this method returns 1 if the target is east of the
    // robot, -1 if the target is west of the robot, or
    // 0 if otherwise.
	public byte isTargetEast() {
	byte xvalue;
		
		if (((robot.getTargetLocation().x) - (robot.getLocation().x)) > 0) //this calculation determines whether the robot or target has a higher x coordinate
			xvalue = 1;
		else if (((robot.getTargetLocation().x) - (robot.getLocation().x))  < 0) //this calculation determines whether the robot or target has a higher x coordinate
			xvalue = -1;
		else 
			xvalue = 0;
	
	return  xvalue;
	}

    // this method causes the robot to look to the absolute
    // direction that is specified as argument and returns
    // what sort of square there is
	public int lookHeading(int check) {
	int result;
	robot.setHeading(IRobot.NORTH); // We set the heading to North so we always know that behind = south etc...
	
	if (check == IRobot.SOUTH) {  //Starts to check ahead(NORTH), behind(SOUTH), left(WEST), and, right(EAST) individually
		result = robot.look(IRobot.BEHIND);
	}
	else if (check == IRobot.EAST) {
		result = robot.look(IRobot.RIGHT);
	}
	else if (check == IRobot.NORTH) {
		result = robot.look(IRobot.AHEAD);
	}
	else
		result = robot.look(IRobot.LEFT); //We know that the only direction unchecked is west, which is left in this case.
	
	return result;
	}

    // this method determines the heading in which the robot
    // should head next to move closer to the target
    public int determineHeading(IRobot robot) {
	
	int heading = 0;
	int decider = (int) Math.round(Math.random()*2); //This value will determine if the robot will check North/south or east/west first. 
	
	if (decider == 1) { //0.5 1.499... half the time it will do north/south first.
		switch (isTargetNorth()) { // This switch checks if the target is north or south from our "istargetnorth" method
				case -1:
					heading = IRobot.SOUTH; //if target is south we head south
					break;
				case 1:
					heading = IRobot.NORTH; //if target is north we head north
					break;
				default:
					break;
		}
			if (heading == 0 || lookHeading(heading) == IRobot.WALL) { //If the heading chosen is a wall or if no heading was chosen in the switch we need a new direction as we should try go towards the target and cant go into walls .
			
				switch (isTargetEast()) { //This chooses east/west, if niether it wont change the heading. But i
						case -1:
							heading = IRobot.WEST; //if target is west we head west
							break;
						case 1:
							heading = IRobot.EAST; //if target is east we head east
							break;
						default:
							break;
				}
			}
			else 
				return heading;
	}
	else {
		switch (isTargetEast()) { //This is for when the decider isnt = 1, but is the same as above otherwise. (East/west first and north/south second)
						case -1:
							heading = IRobot.WEST; //if target is west we head west
							break;
						case 1:
							heading = IRobot.EAST; //if target is east we head east
							break;
						default:
							break;
		}
			if (heading == 0 || lookHeading(heading) == IRobot.WALL) {
			
				switch (isTargetNorth()) {
						case -1:
							heading = IRobot.SOUTH; //if target is south we head south
							break;
						case 1:
							heading = IRobot.NORTH; //if target is north we head north
							break;
						default:
							break;
				}
			}
			else 
				return heading;
	}
	return heading;
    }

    // this method returns a description of this controller
    public String getDescription() {
        return "A controller which homes in on the target";
    }

    // sets the delay
    public void setDelay(int millis) {
        delay = millis;
    }

    // gets the current delay
    public int getDelay() {
        return delay;
    }

    // stops the controller
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