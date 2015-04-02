package initSaveRestore.caretaker;

public class TaskManMemento {
	
	private final String taskman;
	
	public TaskManMemento(String taskmanToSave) {
		this.taskman = taskmanToSave;
	}
	
	public String getMementoAsString() {
		return taskman;
	}

}
