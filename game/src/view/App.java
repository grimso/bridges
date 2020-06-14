package view;

import model.GameModel;
import model.GameModelnterface;
/**
 * This class puts the pieces of the Bridge game together into a MVC and starts the application.
 * A new GameModel is created and glued together with the view inside the controller
 * 
 * 
 * @author grimm
 *
 */
public class App {
	/**
	 * Create the GUI and show it. For thread safety, this method should be invoked
	 * from the event-dispatching thread.
	 */

	public static void main(String[] args) {
		// Schedule a job for the event-dispatching thread:
		// creating and showing this application's GUI.
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				// GameView game = new GameView();
				GameModelnterface model = new GameModel();
				ControllerInterface controller = new GameController(model);

			}
		});
	}
}
