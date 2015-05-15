package company.taskMan.resource;

import java.time.LocalTime;
import java.util.List;

import com.google.common.collect.ImmutableList;

/**
 * A resourceView is a wrapper for resources. The resourceView only has limited
 * access to the resource and is thus safe to send out to the UI. The UI will
 * hence also only have limited access to the underlying resources and cannot do
 * anything unauthorized.
 * 
 * @author Tim Van den Broecke, Joran Van de Woestijne, Vincent Van Gestel and
 *         Eli Vangrieken
 */
public class ResourceView {

	private final Resource resource;

	/**
	 * Wrap the resource in a new view
	 * 
	 * @param resource
	 *            | The resource to wrap
	 */
	public ResourceView(Resource resource) {
		this.resource = resource;
	}
	
	// TODO unwrap mooier, mooie ;) xxx

	/**
	 * Retrieve the name of the resource
	 * 
	 * @return the resource name
	 */
	public String getName() {
		return resource.getName();
	}

	/**
	 * Retrieve a list of all resources (wrapped in views) required for the
	 * resource contained within this view
	 * 
	 * @return a list of all resources (wrapped in views) required for the
	 *         resource contained within this view
	 */
	public List<ResourceView> getRequiredResources() {
		ImmutableList.Builder<ResourceView> reqRes = ImmutableList.builder();
		for (Resource res : resource.getRequiredResources()) {
			reqRes.add(new ResourceView(res));
		}
		return reqRes.build();
	}

	/**
	 * Retrieve a list of all resources (wrapped in views) conflicting with the
	 * resource contained within this view
	 * 
	 * @return a list of all resources (wrapped in views) conflicting with the
	 *         resource contained within this view
	 */
	public List<ResourceView> getConflictingResources() {
		ImmutableList.Builder<ResourceView> conRes = ImmutableList.builder();
		for (Resource res : resource.getConflictingResources()) {
			conRes.add(new ResourceView(res));
		}
		return conRes.build();
	}

	/**
	 * Check whether or not the resource within this view has an availability
	 * period
	 * 
	 * @return True if the resource has an availability period, false otherwise
	 */
	public boolean isDailyAvailable() {
		return resource.isDailyAvailable();
	}

	/**
	 * A getter to retrieve the start time of the availability period
	 * 
	 * @return the hour and minutes of the day when the availability period
	 *         begins, null if there is no availability period
	 */
	public LocalTime getDailyAvailabilityStartTime() {
		return resource.getDailyAvailabilityStartTime();
	}

	/**
	 * A getter to retrieve the end time of the availability period
	 * 
	 * @return the hour and minutes of the day when the availability period end,
	 *         null if there is no availability period
	 */
	public LocalTime getDailyAvailabilityEndTime() {
		return resource.getDailyAvailabilityEndTime();
	}

	/**
	 * Check whether or not a given resource belongs to this view
	 * 
	 * @param otherResource
	 *            | The resource to check
	 * @return True if this view contains the given resource, false otherwise
	 */
	public boolean hasAsResource(Resource otherResource) {
		if (otherResource == null || resource == null) {
			return false;
		}
		return resource.equals(otherResource);
	}

	/**
	 * Check whether two views are equal
	 * 
	 * @param otherView
	 *            | The other view to compare to
	 * @return True if the other view contains the same resource as this one
	 */
	public boolean equals(Object otherView) {
		if (this == otherView)
			return true;
		if (otherView == null)
			return false;
		try {
			return ((ResourceView) otherView).hasAsResource(resource);	
		} catch(ClassCastException e) {
			return false;
		}
	}
	
	public Resource unwrap(){
		return resource;
	}
}
