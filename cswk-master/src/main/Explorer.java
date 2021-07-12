import uk.ac.warwick.dcs.maze.logic.*;
import java.awt.Point;

public class Explorer implements IRobotController {
    // the robot in the maze
    public IRobot robot;
    // a flag to indicate whether we are looking for a path
    public boolean active = false;
    // a value (in ms) indicating how long we should wait
    // between moves
    public int delay;
	public int counter = 0;
    public int pollRun = 0; // Incremented after each pass
    public RobotData robotData; // Data store for junctions
    int direction;

    // this method is called when the "start" button is clicked
    // in the user interface
    public void start() {
        this.active = true;
        int modeselect = 0;

        if ((robot.getRuns() == 0) && (pollRun == 0))
            robotData = new RobotData(); //reset the data store

        while (!robot.getLocation().equals(robot.getTargetLocation()) && active) {

            if (nonwallExits(robot) == beenbeforeExits(robot)) // Selects value for modeselect based on if there is a passage
                modeselect = 1;
            else
                modeselect = 0;

            switch (modeselect) { // Uses the mode select value to choose between Exploring and backtracking
                case 0:
                    direction = explorerMode(robot);
                    break;
                case 1:
                    direction = backtrackingMode(robot);
                    break;
                default:
                    System.out.println("Robot cannot choose mode!");
            }

            robot.face(direction);
            robot.advance();
			counter = counter++;



            // wait for a while if we are supposed to
            if (delay > 0)
                robot.sleep(delay);
        }
    }

    public int explorerMode(IRobot robot) { //When in explorermode this method returns the direction to "explore"

        if (nonwallExits(robot) == 1) // Robot has differant movement methods depending on number of passages surrounding the robot
            direction = deadEnd(robot);
        else if (nonwallExits(robot) == 2)
            direction = corridor(robot);
        else if (nonwallExits(robot) == 3)
            direction = junction(robot);
        else if (nonwallExits(robot) == 4)
            direction = crossroads(robot);

        return direction;

    }


    public int backtrackingMode(IRobot robot) { //When in backtrackingmode this method returns the direction to "backtrack"

        if (nonwallExits(robot) == 1) // Robot has differant movement methods depending on number of passages
            direction = deadEnd(robot);
        else if (nonwallExits(robot) == 2)
            direction = corridor(robot);
        else if (nonwallExits(robot) >= 3)
            direction = btjunction(robot);
        else
            System.out.println("Robot is stuck!");

        return direction;

    }


    // returns a number indicating how many non-wall exits there
    // are surrounding the robot's current position
    public int nonwallExits(IRobot robot) { //Method counts the number of non-walls around the robot

        int nonwalls = 0;

        if ((robot.look(IRobot.AHEAD) == IRobot.PASSAGE) | (robot.look(IRobot.AHEAD) == IRobot.BEENBEFORE))
            nonwalls += 1;
        if ((robot.look(IRobot.BEHIND) == IRobot.PASSAGE) | (robot.look(IRobot.BEHIND) == IRobot.BEENBEFORE))
            nonwalls += 1;
        if ((robot.look(IRobot.LEFT) == IRobot.PASSAGE) | (robot.look(IRobot.LEFT) == IRobot.BEENBEFORE))
            nonwalls += 1;
        if ((robot.look(IRobot.RIGHT) == IRobot.PASSAGE) | (robot.look(IRobot.RIGHT) == IRobot.BEENBEFORE))
            nonwalls += 1;

        return nonwalls;

    }


    public int beenbeforeExits(IRobot robot) { //Method counts the number of beenbefores around the robot

        int beenbeforeEx = 0;

        if (robot.look(IRobot.AHEAD) == IRobot.BEENBEFORE)
            beenbeforeEx += 1;
        if (robot.look(IRobot.BEHIND) == IRobot.BEENBEFORE)
            beenbeforeEx += 1;
        if (robot.look(IRobot.LEFT) == IRobot.BEENBEFORE)
            beenbeforeEx += 1;
        if (robot.look(IRobot.RIGHT) == IRobot.BEENBEFORE)
            beenbeforeEx += 1;

        return beenbeforeEx;

    }

    public int isTargetNorth(IRobot robot) { //method that determines if the rbot is north of the target, 1 = yes

        int targety = robot.getTargetLocation().y;
        int roboty = robot.getLocation().y;
        int yvalue = (roboty) - (targety);

        if (yvalue > 0)
            yvalue = 1;
        else if (yvalue < 0)
            yvalue = -1;
        else
            yvalue = 0;
        return yvalue;
    }


    public int isTargetEast(IRobot robot) { //method that determines if the robot is east of the target, 1 = yes

        int targetx = robot.getTargetLocation().x;
        int robotx = robot.getLocation().x;
        int xvalue = ((targetx) - (robotx));

        if (xvalue > 0)
            xvalue = 1;
        else if (xvalue < 0)
            xvalue = -1;
        else
            xvalue = 0;
        return xvalue;
    }

    public int vertDist(IRobot robot) { //method that determines vertical distance between target and robot

        int targety = robot.getTargetLocation().y;
        int roboty = robot.getLocation().y;
        int yvalue = (roboty) - (targety);

        return yvalue;
    }


    public int horiDist(IRobot robot) { //method that determines horizontal distance between target and robot

        int targetx = robot.getTargetLocation().x;
        int robotx = robot.getLocation().x;
        int xvalue = ((targetx) - (robotx));

        return xvalue;
    }


    public int randDir(IRobot robot) { //Method that just selects a random direction which isn't a wall.

        int randno = (int) Math.floor(Math.random() * 3);

        if (randno == 0)
            direction = IRobot.LEFT;
        if (randno == 1)
            direction = IRobot.RIGHT;
        if (randno == 2)
            direction = IRobot.BEHIND;

        while (robot.look(direction) == IRobot.WALL) {
            randno = (int) Math.floor(Math.random() * 4);
            if (randno == 0)
                direction = IRobot.LEFT;
            if (randno == 1)
                direction = IRobot.RIGHT;
            if (randno == 2)
                direction = IRobot.BEHIND;
            if (randno == 3)
                direction = IRobot.AHEAD;
        }

        return (direction);

    }

    public int deadEnd(IRobot robot) { //This will find the one passage available if its at a dead end (Can't just choose behind as it doesn't always work at start)
        int deadEndDir = randDir(robot);
        return (deadEndDir);
    }



    public int corridor(IRobot robot) { //This finds a new direction that is a passage at a corner and corridor. It never goes back on it self.
        int corridorDir;
        if (robot.look(IRobot.AHEAD) == IRobot.PASSAGE | robot.look(IRobot.AHEAD) == IRobot.BEENBEFORE)
            corridorDir = IRobot.AHEAD;
        else if (robot.look(IRobot.RIGHT) == IRobot.PASSAGE | robot.look(IRobot.RIGHT) == IRobot.BEENBEFORE)
            corridorDir = IRobot.RIGHT;
        else
            corridorDir = IRobot.LEFT;
        return corridorDir;
    }



    public int junction(IRobot robot) { // Selects a random passage from the available ones. Also stores the junction (it knows it hasn't been to it before if it "explores" into it)

        if (nonwallExits(robot) == 3) {
            robotData.recordJunction(robot);
            robotData.printJunction(robot);
        }

        robot.setHeading(IRobot.NORTH);

        int checkingposy = isTargetNorth(robot);
        int checkingposx = isTargetEast(robot);
        int randno;
        int junctionDir = IRobot.AHEAD;

        if ((vertDist(robot) ^ 2) >= (horiDist(robot) ^ 2)) {
            robot.setHeading(IRobot.NORTH);
            switch (checkingposy) {
                case 1:
                    junctionDir = IRobot.AHEAD;
                    break;
                case -1:
                    junctionDir = IRobot.BEHIND;
                    break;
                case 0:
                    switch (checkingposx) {
                        case 1:
                            junctionDir = IRobot.RIGHT;
                            break;
                        case -1:
                            junctionDir = IRobot.LEFT;
                            break;
                        default:
                            junctionDir = randDir(robot);
                    }
                    break;
                default:
                    junctionDir = randDir(robot);
            }

            if (robot.look(junctionDir) == IRobot.BEENBEFORE || robot.look(junctionDir) == IRobot.WALL) {
                robot.setHeading(IRobot.NORTH);
                switch (checkingposx) {
                    case 1:
                        junctionDir = IRobot.RIGHT;
                        break;
                    case -1:
                        junctionDir = IRobot.LEFT;
                        break;
                    default:
                        junctionDir = randDir(robot);
                }
            }
        } else if ((vertDist(robot) ^ 2) < (horiDist(robot) ^ 2)) {
            robot.setHeading(IRobot.NORTH);
            switch (checkingposx) {
                case 1:
                    junctionDir = IRobot.RIGHT;
                    break;
                case -1:
                    junctionDir = IRobot.LEFT;
                    break;
                case 0:
                    switch (checkingposy) {
                        case 1:
                            junctionDir = IRobot.AHEAD;
                            break;
                        case -1:
                            junctionDir = IRobot.BEHIND;
                            break;
                        default:
                            junctionDir = randDir(robot);
                    }
                    break;
                default:
                    junctionDir = randDir(robot);
            }

            if (robot.look(junctionDir) == IRobot.BEENBEFORE || robot.look(junctionDir) == IRobot.WALL) {
                robot.setHeading(IRobot.NORTH);
                switch (checkingposy) {
                    case 1:
                        junctionDir = IRobot.AHEAD;
                        break;
                    case -1:
                        junctionDir = IRobot.BEHIND;
                        break;

                    default:
                        junctionDir = randDir(robot);
                }
            }
        }



        while (robot.look(junctionDir) == IRobot.BEENBEFORE || robot.look(junctionDir) == IRobot.WALL) {
            randno = (int) Math.floor(Math.random() * 4);
            if (randno == 0)
                junctionDir = IRobot.LEFT;
            if (randno == 1)
                junctionDir = IRobot.RIGHT;
            if (randno == 2)
                junctionDir = IRobot.BEHIND;
            if (randno == 3)
                junctionDir = IRobot.AHEAD;
        }
        return (junctionDir);
    }





    public int crossroads(IRobot robot) { // Selects a random passage from the available ones. Also stores the junction (it knows it hasn't been to it before if it "explores" into it.
        if (nonwallExits(robot) == 4) {
            robotData.recordJunction(robot);
            robotData.printJunction(robot);
        }

        robot.setHeading(IRobot.NORTH);

        int checkingposy = isTargetNorth(robot);
        int checkingposx = isTargetEast(robot);
        int randno;
        int crossroadDir = IRobot.AHEAD;


        if ((vertDist(robot) ^ 2) >= (horiDist(robot) ^ 2)) {
            robot.setHeading(IRobot.NORTH);
            switch (checkingposy) {
                case 1:
                    crossroadDir = IRobot.AHEAD;
                    break;
                case -1:
                    crossroadDir = IRobot.BEHIND;
                    break;
                case 0:
                    switch (checkingposx) {
                        case 1:
                            crossroadDir = IRobot.RIGHT;
                            break;
                        case -1:
                            crossroadDir = IRobot.LEFT;
                            break;
                        default:
                            crossroadDir = randDir(robot);
                    }
                    break;
                default:
                    crossroadDir = randDir(robot);
            }

            if (robot.look(crossroadDir) == IRobot.BEENBEFORE || robot.look(crossroadDir) == IRobot.WALL) {
                robot.setHeading(IRobot.NORTH);
                switch (checkingposx) {
                    case 1:
                        crossroadDir = IRobot.RIGHT;
                        break;
                    case -1:
                        crossroadDir = IRobot.LEFT;
                        break;
                    default:
                        crossroadDir = randDir(robot);
                }
            }
        } else if ((vertDist(robot) ^ 2) < (horiDist(robot) ^ 2)) {
            robot.setHeading(IRobot.NORTH);
            switch (checkingposx) {
                case 1:
                    crossroadDir = IRobot.RIGHT;
                    break;
                case -1:
                    crossroadDir = IRobot.LEFT;
                    break;
                case 0:
                    switch (checkingposy) { //makes sure the robot goes straight to target if level with it
                        case 1:
                            crossroadDir = IRobot.AHEAD;
                            break;
                        case -1:
                            crossroadDir = IRobot.BEHIND;
                            break;
                        default:
                            crossroadDir = randDir(robot);
                    }
                    break;
                default:
                    crossroadDir = randDir(robot);
            }

            if (robot.look(crossroadDir) == IRobot.BEENBEFORE || robot.look(crossroadDir) == IRobot.WALL) {
                robot.setHeading(IRobot.NORTH);
                switch (checkingposy) {
                    case 1:
                        crossroadDir = IRobot.AHEAD;
                        break;
                    case -1:
                        crossroadDir = IRobot.BEHIND;
                        break;

                    default:
                        crossroadDir = randDir(robot);
                }
            }
        }

        while (robot.look(crossroadDir) == IRobot.BEENBEFORE || robot.look(crossroadDir) == IRobot.WALL) {
            randno = (int) Math.floor(Math.random() * 4);
            if (randno == 0)
                crossroadDir = IRobot.LEFT;
            if (randno == 1)
                crossroadDir = IRobot.RIGHT;
            if (randno == 2)
                crossroadDir = IRobot.BEHIND;
            if (randno == 3)
                crossroadDir = IRobot.AHEAD;
        }
        return (crossroadDir);
    }


    public int btjunction(IRobot robot) { //This method finds the correct direction to backtrack when at a junction or crossroad, uses stored junctions.    
        int direction = 0;
        int initialheading = robotData.getInitialHeading(robot);

        switch (initialheading) { // This switch covers all possible combinations of current heading and heading when first encountering the junction
            case IRobot.NORTH:
                if (robot.getHeading() == IRobot.NORTH)
                    direction = IRobot.BEHIND;
                else if (robot.getHeading() == IRobot.SOUTH)
                    direction = IRobot.AHEAD;
                else if (robot.getHeading() == IRobot.EAST)
                    direction = IRobot.RIGHT;
                else
                    direction = IRobot.LEFT;;
                break;
            case IRobot.SOUTH:
                if (robot.getHeading() == IRobot.NORTH)
                    direction = IRobot.AHEAD;
                else if (robot.getHeading() == IRobot.SOUTH)
                    direction = IRobot.BEHIND;
                else if (robot.getHeading() == IRobot.EAST)
                    direction = IRobot.LEFT;
                else
                    direction = IRobot.RIGHT;;
                break;
            case IRobot.EAST:
                if (robot.getHeading() == IRobot.NORTH)
                    direction = IRobot.LEFT;
                else if (robot.getHeading() == IRobot.SOUTH)
                    direction = IRobot.RIGHT;
                else if (robot.getHeading() == IRobot.EAST)
                    direction = IRobot.BEHIND;
                else
                    direction = IRobot.AHEAD;;
                break;

            case IRobot.WEST:
                if (robot.getHeading() == IRobot.NORTH)
                    direction = IRobot.RIGHT;
                else if (robot.getHeading() == IRobot.SOUTH)
                    direction = IRobot.LEFT;
                else if (robot.getHeading() == IRobot.EAST)
                    direction = IRobot.AHEAD;
                else
                    direction = IRobot.BEHIND;;
                break;
        }

        return direction;
    }




    // this method returns a description of this controller
    public String getDescription() {
        return "A controller which explores the maze in a structured way";
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
        active = false;
        robotData.resetJunctionCounter();
        pollRun = 0;
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

class RobotData { //Start of robotData class that includes junction infomation.
    public static int junctionCounter = 0;
    public int[][] junction = new int[50000][3]; //The length of the array is 10000, so it cannot store more than that many junctions.

    public void resetJunctionCounter() { //This resests the junction counter and backtracking counter
        junctionCounter = 0;
    }


    public void recordJunction(IRobot robot) { //"makes" a junction and adds its into array
        junction[junctionCounter][0] = robot.getHeading();
        junction[junctionCounter][1] = robot.getLocation().x;
        junction[junctionCounter][2] = robot.getLocation().y;
        junctionCounter = junctionCounter + 1;
    }

    public void printJunction(IRobot robot) { //We can use this to print out the infomation on the junction just stored to check if correct
        int JunctionNo = junctionCounter;
        int heading = junction[junctionCounter - 1][0]; //Need junctionCounter - 1 as the junction counter is increased after a new junction is created.
        int juncX = junction[junctionCounter - 1][1];
        int juncY = junction[junctionCounter - 1][2];
        System.out.print("Junction " + JunctionNo + " (x=");
        System.out.print(juncX + ", y=" + juncY + ") heading ");

        switch (heading) { // This switch converts the numeric absolute direction into a printed string.
            case IRobot.NORTH:
                System.out.println("NORTH");
                break;
            case IRobot.SOUTH:
                System.out.println("SOUTH");
                break;
            case IRobot.EAST:
                System.out.println("EAST");
                break;
            case IRobot.WEST:
                System.out.println("WEST");
                break;
        }
    }

    public int getInitialHeading(IRobot robot) { //This method finds the heading the robot initially had when encountering the previous junction.
        int initialheading = 0;
        int xc = robot.getLocation().x;
        int yc = robot.getLocation().y;

        for (int i = 0; i < junction.length; i++) { //This loop checks the current position against the array to find what the heading was when it was first encountered
            if (junction[i][1] == xc && junction[i][2] == yc) {
                initialheading = junction[i][0];
                i = junction.length;
            }
        }


        return initialheading;
    }
}