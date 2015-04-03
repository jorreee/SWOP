package taskMan.view;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import taskMan.resource.Resource;

public class ResourceView {

	private final Resource resource;
	
	public ResourceView(Resource resource) {
		this.resource = resource;
	}
	
	public String getName() {
		return resource.getName();
	}
	
	public List<ResourceView> getRequiredResources() {
		List<ResourceView> reqRes = new ArrayList<>();
		for(Resource res : resource.getRequiredResources()) {
			reqRes.add(new ResourceView(res));
		}
		return reqRes;
	}
	
	public List<ResourceView> getConflictingResources() {
		List<ResourceView> conRes = new ArrayList<>();
		for(Resource res : resource.getConflictingResources()) {
			conRes.add(new ResourceView(res));
		}
		return conRes;
	}
	
	public boolean isDailyAvailable() { 
		return resource.isDailyAvailable();
	}
	
	public LocalTime getDailyAvailabilityStartTime() {
		return resource.getDailyAvailabilityStartTime();
	}
	
	public LocalTime getDailyAvailabilityEndTime() {
		return resource.getDailyAvailabilityEndTime();
	}
	
	public boolean hasAsResource(Resource otherResource) {
		return resource == otherResource;
	}
}
