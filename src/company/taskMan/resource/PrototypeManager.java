package company.taskMan.resource;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import userInterface.TaskManException;


public class PrototypeManager {

	private List<ResourcePrototype> prototypes;
	
	public PrototypeManager(){
		this.prototypes = new ArrayList<>();
	}
	
	public List<ResourcePrototype> getPrototypes(){
		return prototypes;
	}
	
	/**
	 * Add resource requirements to a prototype
	 * 
	 * @param reqToAdd
	 *            | The new requirements to add
	 * @param prototype
	 *            | The prototype that the new requirements should be added to
	 * @throws IllegalArgumentException
	 */
	public void addRequirementsToResource(List<ResourceView> reqToAdd, ResourceView prototype) 
			throws IllegalArgumentException {
		// TODO niet meer casten
		ResourcePrototype rprot = (ResourcePrototype) prototype.unwrap();
		for (ResourceView req : reqToAdd) {
			ResourcePrototype unwrapReq = (ResourcePrototype) req.unwrap();
			rprot.addRequiredResource(unwrapReq);
		}
	}
	
	/**
	 * Add resource conflicts to a prototype
	 * 
	 * @param conToAdd
	 *            | The new conflicts to add
	 * @param prototype
	 *            | The prototype that the new conflicts should be added to
	 * @throws IllegalArgumentException
	 */
	public void addConflictsToResource(List<ResourceView> conToAdd, ResourceView prototype) 
			throws IllegalArgumentException {
		// TODO niet meer casten
		ResourcePrototype rprot = (ResourcePrototype) prototype.unwrap();
		for (ResourceView req : conToAdd) {
			ResourcePrototype unwrapReq = (ResourcePrototype) req.unwrap();
			rprot.addConflictingResource(unwrapReq);
		}
	}
	
	public void createResourcePrototype(String name,
			Optional<LocalTime> availabilityStart,
			Optional<LocalTime> availabilityEnd) throws TaskManException{	
		//	currentTaskMan.createResourcePrototype(name,availabilityStart,availabilityEnd);
		if(!isValidPeriod(availabilityStart, availabilityEnd)) {
			throw new TaskManException(new IllegalArgumentException("Invalid time period"));
		}
		if(name == null) {
			throw new TaskManException(new IllegalArgumentException("Null is not allowed as name"));
		}

		// Create resourcePrototype (should happen before applying conflicting resources,
		// since a resource can conflict with itself)
		ResourcePrototype resprot = null;
		if(!availabilityStart.isPresent() && !availabilityEnd.isPresent()) {
			resprot = new ResourcePrototype(name, null);
		} else {
			resprot = new ResourcePrototype(name, new AvailabilityPeriod(availabilityStart.get(), availabilityEnd.get()));
		}
		prototypes.add(resprot);
	}
	
	/**
	 * Check whether the given availability period start and end times are valid
	 * 
	 * @param start
	 *            | the optional startTime that should be valid
	 * @param end
	 *            | the optional endTime that should be valid
	 * @return True if the given timestamps define a valid availability period
	 */
	private boolean isValidPeriod(Optional<LocalTime> start, Optional<LocalTime> end) {
		if(start.isPresent() && !end.isPresent()) {
			return false;
		}
		if(!start.isPresent() && end.isPresent()) {
			return false;
		}
		if(!start.isPresent() && !end.isPresent()) {
			return true;
		}
		return !end.get().isBefore(start.get());
	}
}
