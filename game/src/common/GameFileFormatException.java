package common;

public class GameFileFormatException extends Exception {
	public GameFileFormatException(String message) {
		super(message);
	}

	public GameFileFormatException() {
		super("Invalid Syntax - unable to generate game from file");
	}

}
