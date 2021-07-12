import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.*;
import uk.ac.warwick.dcs.maze.logic.Maze;
import uk.ac.warwick.dcs.maze.generators.PrimGenerator;
import uk.ac.warwick.dcs.maze.generators.*;
import uk.ac.warwick.dcs.maze.logic.IRobot;
import uk.ac.warwick.dcs.maze.logic.RobotImpl;
import java.awt.Point;

/*
    This class contains unit tests for the Explorer class.
*/
public class ExplorerTest {
    // the dimensions of the test maze
    private int columns = 7;
    private int rows = 7;
    // the maze used for testing
    private Maze maze;
    // the robot used for testing
    private RobotImpl robot;
    // the controller used for testing
    private Explorer controller;
    //the data store used in testing
    private RobotData robotData;

    /*
        This method is run before all tests.
    */
    @Before
    public void setupTests() {
        // generate a maze with the test dimensions
        this.maze = new Maze(this.columns, this.rows);

        // fill the maze with passages
        for (int i = 0; i < this.columns; i++) {
            for (int j = 0; j < this.rows; j++) {
                this.maze.setCellType(i, j, Maze.PASSAGE);
            }
        }

        // set the starting point somewhere near the middle
        this.maze.setStart(2, 2);
        this.maze.setFinish(0, 0);

        // initialise the robot
        this.robot = new RobotImpl();
        this.robot.setMaze(this.maze);

        // initialise the random robot controller
        this.controller = new Explorer();
        this.controller.setRobot(this.robot);

        //initialise the data store
        this.robotData = new RobotData();
    }



    /*
        Tests whether the Explorer's isTargetNorth
        method works as specified.
    */
    @Test(timeout = 10000)
    public void isTargetNorthTest() {
        // move the target to some cells north of the robot and
        // test whether isTargetNorth correctly identifies this
        for (int i = 0; i < this.columns; i++) {
            this.robot.setTargetLocation(new Point(i, 0));

            assertTrue(
                "Explorer doesn't think the target is north!",
                this.controller.isTargetNorth(robot) == 1);
        }

        // move the target to some cells south of the robot and
        // test whether isTargetNorth correctly identifies this
        for (int i = 0; i < this.columns; i++) {
            this.robot.setTargetLocation(new Point(i, 4));

            assertTrue(
                "Explorer doesn't think the target is south!",
                this.controller.isTargetNorth(robot) == -1);
        }

        // move the target to some cells on the same y-level as the
        // robot and test whether isTargetNorth correctly identifies this
        for (int i = 0; i < this.columns; i++) {
            this.robot.setTargetLocation(new Point(i, 2));

            assertTrue(
                "Explorer doesn't think the target is on the same level!",
                this.controller.isTargetNorth(robot) == 0);
        }
    }

    /*
        Tests whether the Explorer's isTargetEast
        method works as specified.
    */
    @Test(timeout = 10000)
    public void isTargetEastTest() {
        // move the target to some cells east of the robot and
        // test whether isTargetEast correctly identifies this
        for (int i = 0; i < this.columns; i++) {
            this.robot.setTargetLocation(new Point(4, i));

            assertTrue(
                "Explorer doesn't think the target is east!",
                this.controller.isTargetEast(robot) == 1);
        }

        // move the target to some cells west of the robot and
        // test whether isTargetEast correctly identifies this
        for (int i = 0; i < this.columns; i++) {
            this.robot.setTargetLocation(new Point(0, i));

            assertTrue(
                "Explorer doesn't think the target is west!",
                this.controller.isTargetEast(robot) == -1);
        }

        // move the target to some cells on the same x-level as the
        // robot and test whether isTargetEast correctly identifies this
        for (int i = 0; i < this.columns; i++) {
            this.robot.setTargetLocation(new Point(2, i));

            assertTrue(
                "Explorer doesn't think the target is on the same level!",
                this.controller.isTargetEast(robot) == 0);
        }
    }
    /*
        Tests whether the Explorer's determineHeading
        method works as specified.
    */
    @Test(timeout = 10000)
    public void nonwallexitsTest() {
        this.maze.setCellType(2, 1, Maze.WALL);
        assertTrue(
            "Explorer doesnt know its next to 1 wall!",
            this.controller.nonwallExits(robot) == 3);
        this.maze.setCellType(2, 3, Maze.WALL);
        assertTrue(
            "Explorer doesnt know its next to 2 walls!",
            this.controller.nonwallExits(robot) == 2);
        this.maze.setCellType(1, 2, Maze.WALL);
        assertTrue(
            "Explorer doesnt know its next to 3 walls!",
            this.controller.nonwallExits(robot) == 1);
        this.maze.setCellType(3, 2, Maze.WALL);
        assertTrue(
            "Explorer doesnt know its next to 4 walls!",
            this.controller.nonwallExits(robot) == 0);
    }





    /*
        Tests whether the Explorer doesnt move
		when enclosed in walls aka stuck.
    */
    @Test(timeout = 10000)
    public void stuckTest() {
        // move the target to some cells east of the robot and
        // test whether isTargetEast correctly identifies this
        this.maze.setCellType(2, 1, Maze.WALL);
        this.maze.setCellType(2, 3, Maze.WALL);
        this.maze.setCellType(1, 2, Maze.WALL);
        this.maze.setCellType(3, 2, Maze.WALL);

        assertTrue(
            "Explorer is stuck between 4 walls!",
            this.controller.getCounter() == 0);
    }
    /* 
    	Tests whether it can complete a blank maze
    */
    @Test(timeout = 10000)
    public void comepletesBlankMazeTest() {

        this.maze.setStart(1, 1);
        this.maze.setFinish(6, 6);

        assertTrue(
            "Explorer completes blank maze!",
            (!robot.getLocation().equals(robot.getTargetLocation())));
    }

    /*
        Tests whether the Explorer runs 
		into any walls while running on a random prims
		maze.
    */
    @Test(timeout = 10000)
    public void doesNotRunIntoWallsTest() {
        //Generate a new prims maze
        this.maze = (new PrimGenerator()).generateMaze();

        // test whether the robot walked into walls during this run
        assertTrue(
            "Explorer walks into walls!",
            robot.getCollisions() == 0);
    }


    /*
        Tests whether the explorer behaves in a dead end
    */
    @Test(timeout = 10000)
    public void deadEndTest() {
        // move the target to some cells east of the robot and
        // test whether isTargetEast correctly identifies this
        this.maze.setCellType(2, 1, Maze.WALL);
        this.maze.setCellType(2, 3, Maze.WALL);
        this.maze.setCellType(1, 2, Maze.WALL);
        this.maze.setCellType(3, 1, Maze.WALL);
        this.maze.setCellType(4, 1, Maze.WALL);
        this.maze.setCellType(5, 1, Maze.WALL);
        this.maze.setCellType(3, 3, Maze.WALL);
        this.maze.setCellType(4, 3, Maze.WALL);
        this.maze.setCellType(5, 3, Maze.WALL);
        // test whether the robot walked into walls during this run
        assertTrue(
            "Deadend method failed!!",
            robot.getHeading() == IRobot.EAST);
    }


    @Test(timeout = 10000)
    public void completePrimsTest() {
        //Generate a new prims maze
        this.maze = (new PrimGenerator()).generateMaze();

        // test whether the robot got to the end or not
        assertTrue(
            "Explorer completes blank maze!",
            (!robot.getLocation().equals(robot.getTargetLocation())));
    }



}

}