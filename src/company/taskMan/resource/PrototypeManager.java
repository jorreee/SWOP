package company.taskMan.resource;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import exceptions.UnexpectedViewContentException;


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
		ResourcePrototype rprot = unwrap(prototype);
		for (ResourceView req : reqToAdd) {
			ResourcePrototype unwrapReq = unwrap(req);
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
		ResourcePrototype rprot = unwrap(prototype);
		for (ResourceView con : conToAdd) {
			ResourcePrototype unwrapCon = unwrap(con);
			rprot.addConflictingResource(unwrapCon);
		}
	}
	
	/**
	 * Unwraps the given ResourceView of a Prototype.
	 * @param 	view
	 * 			The given ResourceView
	 * @return	The unwrapped ResourcePrototype
	 * @throws IllegalArgumentException
	 */
	private ResourcePrototype unwrap(ResourceView view) 
			throws IllegalArgumentException {
		if(view == null) {
			throw new IllegalArgumentException("view must not be null");
		}
		ResourcePrototype r;
		try {
			r = (ResourcePrototype) view.unwrap();
			if(!prototypes.contains(r)) {
				throw new UnexpectedViewContentException("The resourceView didn't contain a valid resourcePrototype");
			}
		} catch(ClassCastException e) {
			throw new UnexpectedViewContentException("The resourceView didn't contain a resourcePrototype");
		}
		return r;
	}
	
	/**
	 * Create a new Resource Prototype
	 * @param 	name
	 * 			The name of the resource prototype
	 * @param 	availabilityStart
	 * 			The availability start of the prototype. 
	 * @param 	availabilityEnd
	 * 			The availability end of the prototype.
	 * @throws IllegalArgumentException
	 */
	public void createResourcePrototype(String name,
			Optional<LocalTime> availabilityStart,
			Optional<LocalTime> availabilityEnd) 
					throws IllegalArgumentException {	
		//	currentTaskMan.createResourcePrototype(name,availabilityStart,availabilityEnd);
		if(!isValidPeriod(availabilityStart, availabilityEnd)) {
			throw new IllegalArgumentException("Invalid time period");
		}
		if(name == null) {
			throw new IllegalArgumentException("name must not be null");
		}

		ResourcePrototype resprot;
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
