package taskMan.view;

import java.time.LocalTime;
import java.util.List;

import taskMan.resource.Resource;

import com.google.common.collect.ImmutableList;

public class ResourceView {

	private final Resource resource;
	
	public ResourceView(Resource resource) {
		this.resource = resource;
	}
	
	public String getName() {
		return resource.getName();
	}
	
	public List<ResourceView> getRequiredResources() {
		ImmutableList.Builder<ResourceView> reqRes = ImmutableList.builder();
		for(Resource res : resource.getRequiredResources()) {
			reqRes.add(new ResourceView(res));
		}
		return reqRes.build();
	}
	
	public List<ResourceView> getConflictingResources() {
		ImmutableList.Builder<ResourceView> conRes = ImmutableList.builder();
		for(Resource res : resource.getConflictingResources()) {
			conRes.add(new ResourceView(res));
		}
		return conRes.build();
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
		if(otherResource == null || resource == null) {
			return false;
		}
		return resource.equals(otherResource);
	}

	public boolean equals(ResourceView otherView) {
		if (this == otherView)
			return true;
		if (otherView == null)
			return false;
		return otherView.hasAsResource(resource);
	}
}
