import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.*;
import uk.ac.warwick.dcs.maze.logic.Maze;
import uk.ac.warwick.dcs.maze.generators.PrimGenerator;
import uk.ac.warwick.dcs.maze.logic.IRobot;
import uk.ac.warwick.dcs.maze.logic.RobotImpl;
import java.awt.Point;

/*
    This class contains unit tests for the HomingController class.
*/
public class HomingControllerTest {
    // the dimensions of the test maze
    private int columns = 7;
    private int rows = 7;
    // the maze used for testing
    private Maze maze;
    // the robot used for testing
    private RobotImpl robot;
    // the controller used for testing
    private HomingController controller;

    /*
        This method is run before all tests.
    */
    @Before
    public void setupTests() {
        // generate a maze with the test dimensions
        this.maze = new Maze(this.columns, this.rows);

        // fill the maze with passages
        for (int i=0; i<this.columns; i++) {
            for (int j=0; j<this.rows; j++) {
                this.maze.setCellType(i, j, Maze.PASSAGE);
            }
        }

        // set the starting point somewhere near the middle
        this.maze.setStart(2,2);
        this.maze.setFinish(0,0);

        // initialise the robot
        this.robot = new RobotImpl();
        this.robot.setMaze(this.maze);

        // initialise the random robot controller
        this.controller = new HomingController();
        this.controller.setRobot(this.robot);
    }
	
	

    /*
        Tests whether the homing controller's isTargetNorth
        method works as specified.
    */
    @Test(timeout=10000)
    public void isTargetNorthTest() {
        // move the target to some cells north of the robot and
        // test whether isTargetNorth correctly identifies this
        for(int i=0; i<this.columns; i++) {
            this.robot.setTargetLocation(new Point(i,0));

            assertTrue(
                "HomingController doesn't think the target is north!",
                this.controller.isTargetNorth() == 1);
        }

        // move the target to some cells south of the robot and
        // test whether isTargetNorth correctly identifies this
        for(int i=0; i<this.columns; i++) {
            this.robot.setTargetLocation(new Point(i,4));

            assertTrue(
                "HomingController doesn't think the target is south!",
                this.controller.isTargetNorth() == -1);
        }

        // move the target to some cells on the same y-level as the
        // robot and test whether isTargetNorth correctly identifies this
        for(int i=0; i<this.columns; i++) {
            this.robot.setTargetLocation(new Point(i,2));

            assertTrue(
                "HomingController doesn't think the target is on the same level!",
                this.controller.isTargetNorth() == 0);
        }
    }

    /*
        Tests whether the homing controller's isTargetEast
        method works as specified.
    */
    @Test(timeout=10000)
    public void isTargetEastTest() {
        // move the target to some cells east of the robot and
        // test whether isTargetEast correctly identifies this
        for(int i=0; i<this.columns; i++) {
            this.robot.setTargetLocation(new Point(4,i));

            assertTrue(
                "HomingController doesn't think the target is east!",
                this.controller.isTargetEast() == 1);
        }

        // move the target to some cells west of the robot and
        // test whether isTargetEast correctly identifies this
        for(int i=0; i<this.columns; i++) {
            this.robot.setTargetLocation(new Point(0,i));

            assertTrue(
                "HomingController doesn't think the target is west!",
                this.controller.isTargetEast() == -1);
        }

        // move the target to some cells on the same x-level as the
        // robot and test whether isTargetEast correctly identifies this
        for(int i=0; i<this.columns; i++) {
            this.robot.setTargetLocation(new Point(2,i));

            assertTrue(
                "HomingController doesn't think the target is on the same level!",
                this.controller.isTargetEast() == 0);
        }
    }

    /*
        Tests whether the homing controller's lookHeading method
        works correctly.
    */
    @Test(timeout=10000)
    public void lookHeadingTest() {
        // add some walls to the maze
        this.maze.setCellType(2, 1, Maze.WALL);
        this.maze.setCellType(2, 3, Maze.WALL);

        // test lookHeading for when the robot is facing north
        this.robot.setHeading(IRobot.NORTH);
        assertTrue(
            "HomingController doesn't see a wall in the north!",
            this.controller.lookHeading(IRobot.NORTH) == IRobot.WALL);
        assertTrue(
            "HomingController doesn't see a passage in the east!",
            this.controller.lookHeading(IRobot.EAST) == IRobot.PASSAGE);
        assertTrue(
            "HomingController doesn't see a wall in the south!",
            this.controller.lookHeading(IRobot.SOUTH) == IRobot.WALL);
        assertTrue(
            "HomingController doesn't see a passage in the west!",
            this.controller.lookHeading(IRobot.WEST) == IRobot.PASSAGE);

        // test lookHeading for when the robot is facing east
        this.robot.setHeading(IRobot.EAST);
        assertTrue(
            "HomingController doesn't see a wall in the north!",
            this.controller.lookHeading(IRobot.NORTH) == IRobot.WALL);
        assertTrue(
            "HomingController doesn't see a passage in the east!",
            this.controller.lookHeading(IRobot.EAST) == IRobot.PASSAGE);
        assertTrue(
            "HomingController doesn't see a wall in the south!",
            this.controller.lookHeading(IRobot.SOUTH) == IRobot.WALL);
        assertTrue(
            "HomingController doesn't see a passage in the west!",
            this.controller.lookHeading(IRobot.WEST) == IRobot.PASSAGE);

        // test lookHeading for when the robot is facing south
        this.robot.setHeading(IRobot.SOUTH);
        assertTrue(
            "HomingController doesn't see a wall in the north!",
            this.controller.lookHeading(IRobot.NORTH) == IRobot.WALL);
        assertTrue(
            "HomingController doesn't see a passage in the east!",
            this.controller.lookHeading(IRobot.EAST) == IRobot.PASSAGE);
        assertTrue(
            "HomingController doesn't see a wall in the south!",
            this.controller.lookHeading(IRobot.SOUTH) == IRobot.WALL);
        assertTrue(
            "HomingController doesn't see a passage in the west!",
            this.controller.lookHeading(IRobot.WEST) == IRobot.PASSAGE);

        // test lookHeading for when the robot is facing west
        this.robot.setHeading(IRobot.WEST);
        assertTrue(
            "HomingController doesn't see a wall in the north!",
            this.controller.lookHeading(IRobot.NORTH) == IRobot.WALL);
        assertTrue(
            "HomingController doesn't see a passage in the east!",
            this.controller.lookHeading(IRobot.EAST) == IRobot.PASSAGE);
        assertTrue(
            "HomingController doesn't see a wall in the south!",
            this.controller.lookHeading(IRobot.SOUTH) == IRobot.WALL);
        assertTrue(
            "HomingController doesn't see a passage in the west!",
            this.controller.lookHeading(IRobot.WEST) == IRobot.PASSAGE);
    }
	
	/*
        Tests whether the homing controller's determineHeading
        method works as specified.
    */
	@Test(timeout=10000)
	public void determineHeadingTest() {
        // move the target to some cells south of the robot and
		//checks the method over a number of iterations
        for(int i=3; i<this.columns; i++) {
            this.robot.setTargetLocation(new Point(2,i));

            assertTrue(
                "HomingController wants to go South!",
                this.controller.determineHeading(this.robot) == IRobot.SOUTH);
        }

         // move the target to some cells north of the robot and
		//checks the method over a number of iterations
        for(int i=1; i<0; i--) {
            this.robot.setTargetLocation(new Point(2,i));

            assertTrue(
                "HomingController wants to go North!",
                this.controller.determineHeading(this.robot) == IRobot.NORTH);
        }
		// move the target to some cells east of the robot and
		//checks the method over a number of iterations
        for(int i=3; i<this.columns; i++) {
            this.robot.setTargetLocation(new Point(i,2));

            assertTrue(
                "HomingController wants to go East!",
                this.controller.determineHeading(this.robot) == IRobot.EAST);
        }
		// move the target to some cells west of the robot and
		//checks the method over a number of iterations
        for(int i=1; i<0; i--) {
            this.robot.setTargetLocation(new Point(i,2));

            assertTrue(
                 "HomingController wants to go West!",
                this.controller.determineHeading(this.robot) == IRobot.WEST);
        }
    }
	
	/*
        Tests whether the homing controller doesnt move
		when enclosed in walls aka stuck.
    */	
	@Test(timeout=10000)
	public void stuckTest() {
        // move the target to some cells east of the robot and
        // test whether isTargetEast correctly identifies this
       this.maze.setCellType(2, 1, Maze.WALL);
       this.maze.setCellType(2, 3, Maze.WALL);
	   this.maze.setCellType(1, 2, Maze.WALL);
       this.maze.setCellType(3, 2, Maze.WALL);
	   
			assertTrue(
				"HomingController is stuck between 4 walls!",
                this.controller.getCounter() == 0);
    }
	
	@Test(timeout=10000)
	public void comepletesBlankMazeTest() {
		
		this.maze.setStart(1,1);
        this.maze.setFinish(6,6);
		
		assertTrue(
				"HomingController completes blank maze!",
               (!this.robot.getLocation().equals(this.robot.getTargetLocation())));
    }
	
	/*
        Tests whether the homing controller runs 
		into any walls while running on a random prims
		maze.
    */	
	@Test(timeout=10000)
	public void doesNotRunIntoWallsTest() {
		//Generate a new prims maze
		this.maze = (new PrimGenerator()).generateMaze();
		
        // test whether the robot walked into walls during this run
        assertTrue(
            "HomingController walks into walls!",
            robot.getCollisions() == 0);
    }

	
}