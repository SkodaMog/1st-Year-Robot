import uk.ac.warwick.dcs.maze.logic.*;
import java.awt.Point;

public class GrandFinale implements IRobotController {
    // the robot in the maze
    private IRobot robot;
    // a flag to indicate whether we are looking for a path
    private boolean active = false;
    // a value (in ms) indicating how long we should wait
    // between moves
    private int delay;
    private int pollRun = 0; // Incremented after each pass
    private RobotData4 robotData4; // Data store for junctions
    int direction;

    // this method is called when the "start" button is clicked
    // in the user interface robot
    public void start() {
        this.active = true;
        int modeselect = 0;

        if ((robot.getRuns() == 0) && (pollRun == 0))
            robotData4 = new RobotData4(); //reset the data store

        while (!robot.getLocation().equals(robot.getTargetLocation()) && active) {

            if (robot.getRuns() != 0) // If its on a run that isn't the first, it will always use the shortest route it found in the first run
                modeselect = 2;
            else if (nonwallExits(robot) == beenbeforeExits(robot)) // Selects value for modeselect based on if there is a passage
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
                case 2:
                    direction = shortestMode(robot);
                    break;
                default:
                    System.out.println("Robot cannot choose mode!");
            }

            robotData4.recordMode(modeselect); // Stores the mode selected



            robot.face(direction);

            if ((nonwallExits(robot) >= 3) && (robot.getRuns() == 0)) {
                robotData4.recordLeaveJunction(robot);
                //robotData4.printGFJunction(robot); //Use this if you want to print the grandfinale junction stored
            }

            robot.advance();



            // wait for a while if we are supposed to
            if (delay > 0)
                robot.sleep(delay);
        }
        pollRun++; // pollrun increment



        this.active = false;
    }

    private int explorerMode(IRobot robot) { //When in explorermode this method returns the direction to "explore"

        if (nonwallExits(robot) == 1) { // Robot has differant movement methods depending on number of passages surrounding the robot
            direction = deadEnd(robot);
        } else if (nonwallExits(robot) == 2) {
            direction = corridor(robot);
        } else if (nonwallExits(robot) == 3) {
            direction = junction(robot);
        } else if (nonwallExits(robot) == 4) {
            direction = crossroads(robot);
        }

        return direction;

    }


    private int backtrackingMode(IRobot robot) { //When in backtrackingmode this method returns the direction to "backtrack"

        if (nonwallExits(robot) == 1) // Robot has differant movement methods depending on number of passages
            direction = deadEnd(robot);
        else if (nonwallExits(robot) == 2)
            direction = btcorridor(robot);
        else if (nonwallExits(robot) >= 3) {
            direction = btjunction(robot);
        } else
            System.out.println("Robot is stuck!");

        return direction;

    }


    private int shortestMode(IRobot robot) { //This tells the robot which direction to follow the "shortest" path that it found in the first run.

        if (nonwallExits(robot) == 1) { // Robot has differant movement methods depending on number of passages
            direction = deadEnd(robot);
        } else if (nonwallExits(robot) == 2) {
            direction = corridor(robot);
        } else if (nonwallExits(robot) >= 3) {
            direction = finaljunction(robot);
        } else
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

        if (robotData4.checkFirstHeading(robot) == true) { // First 2 if statments deal with the first move of the robot, because its not a junction its not stored. So can lead to unessaccery moves otherwise
            robot.setHeading(IRobot.SOUTH);
            corridorDir = IRobot.AHEAD;

        } else if ((robotData4.movecounter == 0) & (robotData4.checkFirstHeading(robot) == false)) {
            robot.setHeading(IRobot.SOUTH);
            corridorDir = IRobot.LEFT;
        } else if (robot.look(IRobot.AHEAD) == IRobot.PASSAGE | robot.look(IRobot.AHEAD) == IRobot.BEENBEFORE) {
            corridorDir = IRobot.AHEAD;
        } else if (robot.look(IRobot.RIGHT) == IRobot.PASSAGE | robot.look(IRobot.RIGHT) == IRobot.BEENBEFORE) {
            corridorDir = IRobot.RIGHT;
        } else
            corridorDir = IRobot.LEFT;
        return corridorDir;
    }



    private int junction(IRobot robot) { // Selects a passage based off where the target is in relation too the robot. Robot tries to move "away" from the target

        if (nonwallExits(robot) == 3) {
            robotData4.recordJunction(robot);
        }

        robot.setHeading(IRobot.NORTH);

        int checkingposy = isTargetNorth(robot);
        int checkingposx = isTargetEast(robot);
        int randno;
        int junctionDir = IRobot.AHEAD;

        if ((vertDist(robot) ^ 2) >= (horiDist(robot) ^ 2)) { //Checking position of robot vs the target
            robot.setHeading(IRobot.NORTH);
            switch (checkingposy) {
                case 1:
                    junctionDir = IRobot.BEHIND; //Chooses direction away from target
                    break;
                case -1:
                    junctionDir = IRobot.AHEAD;
                    break;
                case 0:
                    switch (checkingposx) { //makes sure the robot goes straight to target if level with it
                        case 1:
                            junctionDir = IRobot.LEFT;
                            break;
                        case -1:
                            junctionDir = IRobot.RIGHT;
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
                        junctionDir = IRobot.LEFT;
                        break;
                    case -1:
                        junctionDir = IRobot.RIGHT;
                        break;
                    default:
                        junctionDir = randDir(robot);
                }
            }
        } else if ((vertDist(robot) ^ 2) < (horiDist(robot) ^ 2)) { //Checking position of robot vs the target
            robot.setHeading(IRobot.NORTH);
            switch (checkingposx) {
                case 1:
                    junctionDir = IRobot.LEFT; //Chooses direction away from target
                    break;
                case -1:
                    junctionDir = IRobot.RIGHT;
                    break;
                case 0:
                    switch (checkingposy) { //makes sure the robot goes straight to target if level with it
                        case 1:
                            junctionDir = IRobot.BEHIND;
                            break;
                        case -1:
                            junctionDir = IRobot.AHEAD;
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
                        junctionDir = IRobot.BEHIND;
                        break;
                    case -1:
                        junctionDir = IRobot.AHEAD;
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





    private int crossroads(IRobot robot) { // Selects a passage based off where the target is in relation too the robot. Robot tries to move "away" from the target
        if (nonwallExits(robot) == 4) {
            robotData4.recordJunction(robot);
        }

        robot.setHeading(IRobot.NORTH);

        int checkingposy = isTargetNorth(robot);
        int checkingposx = isTargetEast(robot);
        int randno;
        int crossroadDir = IRobot.AHEAD;


        if ((vertDist(robot) ^ 2) >= (horiDist(robot) ^ 2)) { //Checking position of robot vs the target
            robot.setHeading(IRobot.NORTH);
            switch (checkingposy) {
                case 1:
                    crossroadDir = IRobot.BEHIND; //Chooses direction away from target
                    break;
                case -1:
                    crossroadDir = IRobot.AHEAD;
                    break;
                case 0:
                    switch (checkingposx) { //makes sure the robot goes straight to target if level with it
                        case 1:
                            crossroadDir = IRobot.LEFT;
                            break;
                        case -1:
                            crossroadDir = IRobot.RIGHT;
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
                        crossroadDir = IRobot.LEFT;
                        break;
                    case -1:
                        crossroadDir = IRobot.RIGHT;
                        break;
                    default:
                        crossroadDir = randDir(robot);
                }
            }
        } else if ((vertDist(robot) ^ 2) < (horiDist(robot) ^ 2)) { //Checking position of robot vs the target
            robot.setHeading(IRobot.NORTH);
            switch (checkingposx) {
                case 1:
                    crossroadDir = IRobot.LEFT; //Chooses direction away from target
                    break;
                case -1:
                    crossroadDir = IRobot.RIGHT;
                    break;
                case 0:
                    switch (checkingposy) { //makes sure the robot goes straight to target if level with it
                        case 1:
                            crossroadDir = IRobot.BEHIND;
                            break;
                        case -1:
                            crossroadDir = IRobot.AHEAD;
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
                        crossroadDir = IRobot.BEHIND;
                        break;
                    case -1:
                        crossroadDir = IRobot.AHEAD;
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
    private int btcorridor(IRobot robot) { //This method deals with backtracting in a corridor.
        int btcorridorDir;
        if (robotData4.getPreviousMode() == 0) { //This if statement is for when the robot explores and ends up looping back round to an already explored junction(So the corridor just before the junction)
            btcorridorDir = IRobot.BEHIND;
            if (robotData4.junctionAhead(robot) == true) {
                robotData4.recordLeaveJunctionSpecialCase(robot); //Updates the "leaving direction" of a Juction when we backtrack into a now explored junction
            }
        } else {
            if (robot.look(IRobot.AHEAD) == IRobot.BEENBEFORE)
                btcorridorDir = IRobot.AHEAD;
            else if (robot.look(IRobot.RIGHT) == IRobot.BEENBEFORE)
                btcorridorDir = IRobot.RIGHT;
            else
                btcorridorDir = IRobot.LEFT;
        }
        return btcorridorDir;
    }




    private int btjunction(IRobot robot) { //This method finds the correct direction to backtrack when at a junction or crossroad, uses stored junctions.    
        int direction = 0;
        int initialheading = robotData4.getInitialHeading(robot);

        if (robotData4.getPreviousMode() == 0) { //This part is for when a robot explores next to an already fully explored junction. But it has just come from a junction. (special case)
            direction = IRobot.BEHIND;
        } else if ((robotData4.getPreviousMode() == 1) && (nonwallExits(robot) == beenbeforeExits(robot))) { // This is normal backtracking mode if it encounters a junction while backtracking (backtracking for atleast 1 move before)
            switch (initialheading) { // This switch covers all possible combinations of current heading and heading when first encountering the junction (also converts from absolute directions to relative directions)
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
        } else if (nonwallExits(robot) == 3) { // If the robot tries to backtrack into an old junction, it goes into backtracking as it sees the junction as beenbefore. This allows us to still explore it.
            direction = junction(robot);
        } else { //Same as above but for crossroads
            direction = crossroads(robot);
        }

        return direction;
    }

    private int finaljunction(IRobot robot) { //Gets the direction the robot when it last encountered this junction in the previous run
        int direction = 0;



        switch (robotData4.getFinalHeading(robot)) { // This switch covers all possible combinations of current heading and heading when last encountering the junction (also converts from absolute directions to relative directions)
            case IRobot.NORTH:
                if (robot.getHeading() == IRobot.NORTH)
                    direction = IRobot.AHEAD;
                else if (robot.getHeading() == IRobot.SOUTH)
                    direction = IRobot.BEHIND;
                else if (robot.getHeading() == IRobot.EAST)
                    direction = IRobot.LEFT;
                else
                    direction = IRobot.RIGHT;;
                break;
            case IRobot.SOUTH:
                if (robot.getHeading() == IRobot.NORTH)
                    direction = IRobot.BEHIND;
                else if (robot.getHeading() == IRobot.SOUTH)
                    direction = IRobot.AHEAD;
                else if (robot.getHeading() == IRobot.EAST)
                    direction = IRobot.RIGHT;
                else
                    direction = IRobot.LEFT;;
                break;
            case IRobot.EAST:
                if (robot.getHeading() == IRobot.NORTH)
                    direction = IRobot.RIGHT;
                else if (robot.getHeading() == IRobot.SOUTH)
                    direction = IRobot.LEFT;
                else if (robot.getHeading() == IRobot.EAST)
                    direction = IRobot.AHEAD;
                else
                    direction = IRobot.BEHIND;;
                break;

            case IRobot.WEST:
                if (robot.getHeading() == IRobot.NORTH)
                    direction = IRobot.LEFT;
                else if (robot.getHeading() == IRobot.SOUTH)
                    direction = IRobot.RIGHT;
                else if (robot.getHeading() == IRobot.EAST)
                    direction = IRobot.BEHIND;
                else
                    direction = IRobot.AHEAD;;
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
        robotData4.resetJunctionCounter();
        robotData4.resetMoveCounter();
        pollRun = 0;
    }

    // sets the reference to the robot
    public void setRobot(IRobot robot) {
        this.robot = robot;
    }
}

class RobotData4 { //Start of robotData class that includes junction infomation.
    private static int junctionCounter = 0;
    public static int movecounter = 0;
    private int[][] junction = new int[50000][3]; //The length of the array is 50000, so it cannot store more than that many junctions. (Probably wont ever need more than that, if you do just add more)
    private int[][] grandfinale = new int[50000][3];
    private int[] mode = new int[1000000]; //Array of what mode (explore or backtrack) the robot was in

    public void resetJunctionCounter() { //This resets the junction counter
        junctionCounter = 0;
    }

    public void resetMoveCounter() { //This resets the move counter
        movecounter = 0;
    }

    public void recordLeaveJunction(IRobot robot) { //records the final direction the robot took after passing through a junction
        grandfinale[junctionCounter - 1][0] = robot.getHeading();
        grandfinale[junctionCounter - 1][1] = robot.getLocation().x;
        grandfinale[junctionCounter - 1][2] = robot.getLocation().y;
    }

    public boolean junctionAhead(IRobot robot) {
        int xc = robot.getLocation().x;
        int yc = robot.getLocation().y;
        boolean LJSC = false;
        switch (robot.getHeading()) {
            case IRobot.NORTH:
                for (int i = 0; i < grandfinale.length; i++) { //This loop checks the current position against the array to find if there is a juction NORTH of the robot
                    if ((grandfinale[i][1] == xc) && (grandfinale[i][2] == (yc - 1))) {
                        LJSC = true;
                    }
                    if ((grandfinale[i][1]) == 0 && (grandfinale[i][2] == 0)) {
                        i = grandfinale.length;
                    }
                }
                break;
            case IRobot.SOUTH:
                for (int i = 0; i < grandfinale.length; i++) { //This loop checks the current position against the array to find if there is a juction SOUTH of the robot
                    if ((grandfinale[i][1] == xc) && (grandfinale[i][2] == (yc + 1))) {
                        LJSC = true;
                    }
                    if (grandfinale[i][1] == 0 && grandfinale[i][2] == 0) {
                        i = grandfinale.length;
                    }
                }
                break;
            case IRobot.EAST:
                for (int i = 0; i < grandfinale.length; i++) { //This loop checks the current position against the array to find if there is a juction EAST of the robot
                    if ((grandfinale[i][1] == (xc + 1) && grandfinale[i][2] == yc)) {
                        LJSC = true;
                    }
                    if (grandfinale[i][1] == 0 && grandfinale[i][2] == 0) {
                        i = grandfinale.length;
                    }
                }
                break;
            case IRobot.WEST:
                for (int i = 0; i < grandfinale.length; i++) { //This loop checks the current position against the array to find if there is a juction WEST of the robot
                    if ((grandfinale[i][1] == (xc - 1)) && (grandfinale[i][2] == yc)) {
                        LJSC = true;
                    }
                    if ((grandfinale[i][1] == 0) && (grandfinale[i][2] == 0)) {
                        i = grandfinale.length;
                    }
                }
                break;
        }
        return LJSC;
    }


    public void recordLeaveJunctionSpecialCase(IRobot robot) { //records the final direction the robot took after passing through a junction
        int headings = robot.getHeading();

        switch (headings) {
            case IRobot.NORTH:
                grandfinale[junctionCounter][0] = IRobot.SOUTH;
                grandfinale[junctionCounter][1] = robot.getLocation().x;
                grandfinale[junctionCounter][2] = ((robot.getLocation().y) + 1);
                break;
            case IRobot.SOUTH:
                grandfinale[junctionCounter][0] = IRobot.NORTH;
                grandfinale[junctionCounter][1] = robot.getLocation().x;
                grandfinale[junctionCounter][2] = ((robot.getLocation().y) - 1);
                break;
            case IRobot.EAST:
                grandfinale[junctionCounter][0] = IRobot.WEST;
                grandfinale[junctionCounter][1] = ((robot.getLocation().x) + 1);
                grandfinale[junctionCounter][2] = robot.getLocation().y;
                break;
            case IRobot.WEST:
                grandfinale[junctionCounter - 1][0] = IRobot.EAST;
                grandfinale[junctionCounter - 1][1] = ((robot.getLocation().x) - 1);
                grandfinale[junctionCounter - 1][2] = robot.getLocation().y;
                break;
        }
        junctionCounter = junctionCounter + 1;
    }

    public void recordJunction(IRobot robot) { //"makes" a junction and adds its into array
        junction[junctionCounter][0] = robot.getHeading();
        junction[junctionCounter][1] = robot.getLocation().x;
        junction[junctionCounter][2] = robot.getLocation().y;
        junctionCounter = junctionCounter + 1;
    }

    public void recordMode(int modeselect) { //This stores the mode of previous move (explore or backtrack)
        mode[movecounter] = modeselect;
        movecounter = movecounter + 1;
    }

    public void printMode() { //Prints our the data in the mode array
        int MoveNo = movecounter;
        int modevalue = mode[movecounter - 2];
        System.out.print("Move Number: " + MoveNo + " Mode: ");

        switch (modevalue) { // This switch converts the int value to a printed string
            case 0:
                System.out.println("Explorer");
                break;
            case 1:
                System.out.println("Backtrack");
                break;
            case 2:
                System.out.println("Shortest");
                break;
        }
    }

    public int getPreviousMode() { //This can be called to get mode of the previous move
        int previousmode;

        previousmode = mode[movecounter - 1];
        return previousmode;
    }

    public void printJunction(IRobot robot) { //We can use this to print out the information on the junction just stored to check if correct
        int JunctionNo = junctionCounter;
        int heading = junction[junctionCounter][0]; //Need junctionCounter - 1 as the junction counter is increased after a new junction is created.
        int juncX = junction[junctionCounter][1];
        int juncY = junction[junctionCounter][2];
        System.out.println("Junction " + JunctionNo + " (x=");
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

    public void printGFJunction(IRobot robot) { //We can use this to print out the information on the junction just stored to check if correct
        int JunctionNo = junctionCounter;
        int heading = grandfinale[junctionCounter][0]; //Need junctionCounter - 1 as the junction counter is increased after a new junction is created.
        int juncX = grandfinale[junctionCounter][1];
        int juncY = grandfinale[junctionCounter][2];
        System.out.println("GF: " + JunctionNo + " (x=");
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

    public boolean checkFirstHeading(IRobot robot) { //method checks if the first heading was correct or not
        if ((movecounter == 0) && (grandfinale[0][1] == robot.getLocation().x)) {
            return true;
        } else
            return false;
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

    public int getFinalHeading(IRobot robot) { //This method finds the heading the robot had when it left a junction
        int finalheading = 0;
        int xc = robot.getLocation().x;
        int yc = robot.getLocation().y;

        for (int i = 0; i < grandfinale.length; i++) { //This loop checks the current position against the array to find what the heading was when it was last encountered
            if (grandfinale[i][1] == xc && grandfinale[i][2] == yc) {
                finalheading = grandfinale[i][0];
            }
        }
        return finalheading;
    }
}