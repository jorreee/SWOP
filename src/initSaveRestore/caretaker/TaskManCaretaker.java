package initSaveRestore.caretaker;

import initSaveRestore.initialization.TaskManInitFileChecker;

import java.io.StringReader;

public class TaskManCaretaker {

	
	// TODO Alles, maar zo krijgt ge een string (tman rep) als stream in de fileChecker voor parsing
	public boolean loadFromMemento() {
		TaskManInitFileChecker fchecker = new TaskManInitFileChecker(new StringReader("TMANFILE"));
		return true;
	}
}
