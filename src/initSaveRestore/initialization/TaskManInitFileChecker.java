package initSaveRestore.initialization;

import java.io.IOException;
import java.io.Reader;
import java.io.StreamTokenizer;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * A TaskManInitFileChecker will read data from an input stream. The incoming
 * data should be formatted as a TMAN file. The file checker will then
 * instantiate several lists containing the parsed data required to initiate the
 * system.
 * 
 * @author Tim Van Den Broecke, Joran Van de Woestijne, Vincent Van Gestel and
 *         Eli Vangrieken
 */
public class TaskManInitFileChecker extends StreamTokenizer {
	
	  DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
	  DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
	  
	  LocalDateTime systemTime = LocalDateTime.now();
	  String currentUser;
	  
	  List<ProjectCreationData> projectDataList = new ArrayList<>();
	  List<TaskCreationData> taskDataList = new ArrayList<>();
	  List<ResourcePrototypeCreationData> resourcePrototypeDataList = new ArrayList<>();
	  List<ConcreteResourceCreationData> concreteResourceDataList = new ArrayList<>();
	  List<DeveloperCreationData> developerDataList = new ArrayList<>();
	  List<PlanningCreationData> planningDataList = new ArrayList<>();
	  List<ReservationCreationData> reservationDataList = new ArrayList<>();
	  
	  List<LocalTime[]> dailyAvailabilityTime = new ArrayList<LocalTime[]>();

	  public TaskManInitFileChecker(Reader r) {
	    super(r);
	  }

	  public int nextToken() {
	    try {
	      return super.nextToken();
	    } catch (IOException e) {
	      throw new RuntimeException(e);
	    }
	  }

	  void error(String msg) {
	    throw new RuntimeException("Line " + lineno() + ": " + msg);
	  }

	  boolean isWord(String word) {
	    return ttype == TT_WORD && sval.equals(word);
	  }

	  void expectChar(char c) {
	    if (ttype != c)
	      error ("'" + c + "' expected");
	    nextToken();
	  }

	  void expectLabel(String name) {
	    if (!isWord(name))
	      error("Keyword '" + name + "' expected");
	    nextToken();
	    expectChar(':');
	  }

	  String expectStringField(String label) {
	    expectLabel(label);
	    if (ttype != '"')
	      error("String expected");
	    String value = sval;
	    nextToken();
	    return value;
	  }

	  LocalDateTime expectDateField(String label) {
	    String date = expectStringField(label);
	    return LocalDateTime.parse(date, dateTimeFormatter);
	  }

	  LocalTime expectTimeField(String label) {
	    String date = expectStringField(label);
	    return LocalTime.parse(date, timeFormatter);
	  }

	  int expectInt() {
	    if (ttype != TT_NUMBER || nval != (double)(int)nval)
	      error("Integer expected");
	    int value = (int)nval;
	    nextToken();
	    return value;
	  }

	  int expectIntField(String label) {
	    expectLabel(label);
	    return expectInt();
	  }

	  List<Integer> expectIntList() {
	    ArrayList<Integer> list = new ArrayList<>();
	    expectChar('[');
	    while (ttype == TT_NUMBER){
	      list.add(expectInt());
	      if (ttype == ',')
	        expectChar(',');
	      else if (ttype != ']')
	        error("']' (end of list) or ',' (new list item) expected");
	    }
	    expectChar(']');
	    return list;
	  }

	  List<IntPair> expectLabeledPairList(String first, String second) {
	    ArrayList<IntPair> list = new ArrayList<>();
	    expectChar('[');
	    while (ttype == '{'){
	      if (ttype == '{')
	      {
	        expectChar('{');
	        int f = expectIntField(first);
	        expectChar(',');
	        int s = expectIntField(second);
	        expectChar('}');
	        IntPair p = new IntPair();
	        p.first = f;
	        p.second = s;
	        list.add(p);
	      }
	      if (ttype == ',')
	        expectChar(',');
	      else if (ttype != ']')
	        error("']' (end of list) or ',' (new list item) expected");
	    }
	    expectChar(']');
	    return list;
	  }

	  public void checkFile() {
	    slashSlashComments(false);
	    slashStarComments(false);
	    ordinaryChar('/'); // otherwise "//" keeps treated as comments.
	    commentChar('#');

	    nextToken();

	    this.systemTime = expectDateField("systemTime");

	    expectLabel("dailyAvailability");
	    while (ttype == '-') {
	      expectChar('-');
	      dailyAvailabilityTime.add(new LocalTime[]{expectTimeField("startTime"), expectTimeField("endTime")});
	    }
	    
	    expectLabel("resourceTypes");
	    while (ttype == '-') {
	      expectChar('-');
	      String name = expectStringField("name");
	      expectLabel("requires");
	      List<Integer> requirements = expectIntList();
	      expectLabel("conflictsWith");
	      List<Integer> conflicts = expectIntList();
	      expectLabel("dailyAvailability");
	      Integer availabilityIndex = null;
	      if (ttype == TT_NUMBER)
	      {
	        availabilityIndex = expectInt();
	      }
	      resourcePrototypeDataList.add(new ResourcePrototypeCreationData(
	    		  name, requirements,
	    		  conflicts, availabilityIndex));
	    }

	    expectLabel("resources");
	    while (ttype == '-') {
	      expectChar('-');
	      String name = expectStringField("name");
	      expectLabel("type");
	      int typeIndex = expectInt();
	      concreteResourceDataList.add(new ConcreteResourceCreationData(name, typeIndex));
	    }

	    expectLabel("developers");
	    while (ttype == '-') {
	      expectChar('-');
	      String name = expectStringField("name");
	      developerDataList.add(new DeveloperCreationData(name));	      
	    }
	    
	    expectLabel("currentUser");
	    expectChar('-');
	    currentUser = expectStringField("name");
	    

	    expectLabel("projects");
	    while (ttype == '-') {
	      expectChar('-');
	      String name = expectStringField("name");
	      String description = expectStringField("description");
	      LocalDateTime creationTime = expectDateField("creationTime");
	      LocalDateTime dueTime = expectDateField("dueTime");
	      projectDataList.add(new ProjectCreationData(name, description, creationTime, dueTime));
	    }

	    expectLabel("plannings");
	    while (ttype == '-') {
	      expectChar('-');
	      LocalDateTime dueTime = expectDateField("plannedStartTime");
	      expectLabel("developers");
	      List<Integer> developers = expectIntList();
	      planningDataList.add(new PlanningCreationData(dueTime, developers));
	    }

	    expectLabel("tasks");
	    while (ttype == '-') {
	      expectChar('-');
	      int project = expectIntField("project");
	      String description = expectStringField("description");
	      int estimatedDuration = expectIntField("estimatedDuration");
	      int acceptableDeviation = expectIntField("acceptableDeviation");
	      int alternativeFor = -1;
	      expectLabel("alternativeFor");
	      if (ttype == TT_NUMBER)
	        alternativeFor = expectInt();
	      List<Integer> prerequisiteTasks = new ArrayList<>();
	      expectLabel("prerequisiteTasks");
	      if (ttype == '[')
	        prerequisiteTasks = expectIntList();
	      expectLabel("requiredResources");
	      List<IntPair> requiredResources = new ArrayList<>();
	      if (ttype == '[')
		        requiredResources = expectLabeledPairList("type","quantity");
	      expectLabel("planning");
	      Integer planning = null;
	      if (ttype == TT_NUMBER)
	        planning = expectInt();
	      expectLabel("status");
	      TaskStatus status = null;
	      if (isWord("executing")) {
	        nextToken();
	        status = TaskStatus.EXECUTING;
	      } else if (isWord("finished")) {
	        nextToken();
	        status = TaskStatus.FINISHED;
	      } else if (isWord("failed")) {
	        nextToken();
	        status = TaskStatus.FAILED;
	      }
	      LocalDateTime startTime = null;
	      LocalDateTime endTime = null;
	      if (status != null) {
	        startTime = expectDateField("startTime");
	      }
	      if (status != null && status != TaskStatus.EXECUTING) {
	        endTime = expectDateField("endTime");
	      }
	      
	      PlanningCreationData planningData = null;
	      if(planning != null)
	    	  planningData = planningDataList.get(planning);
	      
	      taskDataList.add(new TaskCreationData(project, description, estimatedDuration, acceptableDeviation, alternativeFor, prerequisiteTasks, requiredResources, status, startTime, endTime, planningData));
	    }

	    expectLabel("reservations");
	    while (ttype == '-') {
	      expectChar('-');
	      int resource = expectIntField("resource");
	      int task = expectIntField("task");
	      LocalDateTime startTime = expectDateField("startTime");
	      LocalDateTime endTime = expectDateField("endTime");
	      reservationDataList.add(new ReservationCreationData(resource, task, startTime, endTime));
	    }

	    if (ttype != TT_EOF)
	      error("End of file or '-' expected");
	  }

	public List<TaskCreationData> getTaskDataList() {
		return taskDataList;
	}

	public List<ProjectCreationData> getProjectDataList() {
		return projectDataList;
	}
	
	public LocalDateTime getSystemTime() {
		return systemTime;
	}
	
	public Optional<LocalTime> getDailyAvailabilityStartByIndex(Integer index) {
		if(index == null) return Optional.empty();
		else return Optional.of(dailyAvailabilityTime.get(index)[0]);
	}
	
	public Optional<LocalTime> getDailyAvailabilityEndByIndex(Integer index) {
		if(index == null) return Optional.empty();
		else return Optional.of(dailyAvailabilityTime.get(index)[1]);
	}

	public List<ResourcePrototypeCreationData> getResourcePrototypeDataList() {
		return resourcePrototypeDataList;
	}

	public List<ConcreteResourceCreationData> getConcreteResourceDataList() {
		return concreteResourceDataList;
	}

	public List<DeveloperCreationData> getDeveloperDataList() {
		return developerDataList;
	}
	
	public String getCurrentUser() {
		return currentUser;
	}

	public List<ReservationCreationData> getReservationDataList() {
		return reservationDataList;
	}
}