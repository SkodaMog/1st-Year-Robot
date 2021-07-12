import uk.ac.warwick.dcs.maze.logic.*;
import java.awt.Point;

public class Explorer2 implements IRobotController {
    // the robot in the maze
    private IRobot robot;
    // a flag to indicate whether we are looking for a path
    private boolean active = false;
    // a value (in ms) indicating how long we should wait
    // between moves
    private int delay;
    private int pollRun = 0; // Incremented after each pass
    private RobotData2 robotData2; // Data store for junctions
    int direction;

    // this method is called when the "start" button is clicked
    // in the user interface robot
    public void start() {
        this.active = true;
        int modeselect = 0;

        if ((robot.getRuns() == 0) && (pollRun == 0))
            robotData2 = new RobotData2(); //reset the data store

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



            // wait for a while if we are supposed to
            if (delay > 0)
                robot.sleep(delay);
        }
        pollRun++; // pollrun increment
        this.active = false;
    }

    private int explorerMode(IRobot robot) { //When in explorermode this method returns the direction to "explore"

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


    private int backtrackingMode(IRobot robot) { //When in backtrackingmode this method returns the direction to "backtrack"

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
    private int nonwallExits(IRobot robot) { //Method counts the number of non-walls around the robot

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


    private int beenbeforeExits(IRobot robot) { //Method counts the number of beenbefores around the robot

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


    private int randDir(IRobot robot) { //Method that just selects a random direction which isn't a wall.

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

    private int deadEnd(IRobot robot) { //This will find the one passage available if its at a dead end (Can't just choose behind as it doesn't always work at start)
        int deadEndDir = randDir(robot);
        return (deadEndDir);
    }



    private int corridor(IRobot robot) { //This finds a new direction that is a passage at a corner and corridor. It never goes back on it self.
        int corridorDir;
        if (robot.look(IRobot.AHEAD) == IRobot.PASSAGE | robot.look(IRobot.AHEAD) == IRobot.BEENBEFORE)
            corridorDir = IRobot.AHEAD;
        else if (robot.look(IRobot.RIGHT) == IRobot.PASSAGE | robot.look(IRobot.RIGHT) == IRobot.BEENBEFORE)
            corridorDir = IRobot.RIGHT;
        else
            corridorDir = IRobot.LEFT;
        return corridorDir;
    }



    private int junction(IRobot robot) { // Selects a random passage from the available ones. Also stores the junction if it has not been visited before.

        if (beenbeforeExits(robot) <= 1) {
            robotData2.recordJunction(robot);
            robotData2.printJunction(robot);
        }

        int randno;
        int junctionDir = randDir(robot);

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





    private int crossroads(IRobot robot) { // Selects a random passage from the available ones. Also stores the junction if it has not been visited before.
        if (beenbeforeExits(robot) <= 1) {
            robotData2.recordJunction(robot);
            robotData2.printJunction(robot);
        }

        int randno;
        int crossroadDir = randDir(robot);

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

    private int btjunction(IRobot robot) { //This method finds the correct direction to backtrack when at a junction or crossroad, uses stored junctions.    
        int direction = 0;
        int initialheading = robotData2.getInitialHeading(robot);

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
        robotData2.resetJunctionCounter();
        pollRun = 0;
    }

    // sets the reference to the robot
    public void setRobot(IRobot robot) {
        this.robot = robot;
    }
}

class RobotData2 { //Start of robotData class that includes junction infomation.
    private static int junctionCounter = 0;
    private int[] junction = new int[10000]; //The length of the array is 10000, so it cannot store more than that many junctions.

    public void resetJunctionCounter() { //This resests the junction counter
        junctionCounter = 0;
    }


    public void recordJunction(IRobot robot) { //"makes" a junction and adds its into array
        junction[junctionCounter] = robot.getHeading();
        junctionCounter = junctionCounter + 1;
    }

    public void printJunction(IRobot robot) { //We can use this to print out the infomation on the junction just stored to check if correct
        int JunctionNo = junctionCounter;
        int heading = junction[junctionCounter - 1]; //Need junctionCounter - 1 as the junction counter is increased after a new junction is created.
        System.out.print("Junction " + JunctionNo + " heading ");

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

        initialheading = junction[junctionCounter - 1];
        junctionCounter = junctionCounter - 1; // This line will cause the junction just backtracked through will be overwritten as it will no longer be needed (must lead to dead end)

        return initialheading;
    }
}