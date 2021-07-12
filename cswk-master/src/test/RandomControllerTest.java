import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.*;
import uk.ac.warwick.dcs.maze.logic.Maze;
import uk.ac.warwick.dcs.maze.generators.PrimGenerator;
import uk.ac.warwick.dcs.maze.logic.IRobot;
import uk.ac.warwick.dcs.maze.logic.RobotImpl;
import java.awt.Point;

/*
    This class contains unit tests for the RandomController class.
*/
public class RandomControllerTest {
    // the dimensions of the test maze
    private int columns = 5;
    private int rows = 5;
    // the maze used for testing
    private Maze maze;
    // the robot used for testing
    private RobotImpl robot;
    // the controller used for testing
    private RandomController controller;

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
        this.controller = new RandomController();
        this.controller.setRobot(this.robot);
    }
        
	
	
	@Test(timeout=10000)
	public void doesNotRunIntoWallsTest() {
        // generate a random maze
        Maze maze = (new PrimGenerator()).generateMaze();

        // initialise the robot
        RobotImpl robot = new RobotImpl();
        robot.setMaze(maze);

        // initialise the random robot controller
        RandomController controller = new RandomController();
        controller.setRobot(robot);

        // run the controller
        controller.start();

        // test whether the robot walked into walls during this run
        assertTrue(
            "RandomController walks into walls!",
            robot.getCollisions() == 0);
    }
	
	
	
	@Test(timeout=10000)
	public void stuckTest() {
        // surround the robot at (2,2) with walls
       this.maze.setCellType(2, 1, Maze.WALL);
       this.maze.setCellType(2, 3, Maze.WALL);
	   this.maze.setCellType(1, 2, Maze.WALL);
       this.maze.setCellType(3, 2, Maze.WALL);
	   //Test to see if the robot takes any steps while trapped
			assertTrue(
					"HomingController is stuck between 4 walls!",
                this.controller.getCounter() == 0);
    }
	
	
	
	@Test(timeout=10000)
	public void comepletesBlankMazeTest() {
		//Test to see if the robot and solve a blank maze
		this.maze.setStart(1,1);
        this.maze.setFinish(6,6);
		
		assertTrue(
				"HomingController completes blank maze!",
               (!this.robot.getLocation().equals(this.robot.getTargetLocation())));
    }
	
	
	
	@Test(timeout=10000)
	public void completesPrimsMazeTest() {
        // generate a random maze
        Maze maze = (new PrimGenerator()).generateMaze();

        // initialise the robot
        RobotImpl robot = new RobotImpl();
        robot.setMaze(maze);

        // initialise the random robot controller
        RandomController controller = new RandomController();
        controller.setRobot(robot);

        // run the controller
        controller.start();

        // test whether the robot completes this random prims maze
        assertTrue(
            "RandomController completes the prims maze!",
           (!this.robot.getLocation().equals(this.robot.getTargetLocation())));
    }

	
}