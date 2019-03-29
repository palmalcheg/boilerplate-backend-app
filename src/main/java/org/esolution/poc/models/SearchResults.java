package org.esolution.poc.models;

import java.util.List;
import java.util.Map;

import lombok.Data;

@Data
public class SearchResults {
	
    private Map<String,Integer> navigation;    
    private List<Contributor> items;
    
    public boolean hasNext() {
    	return navigation != null && navigation.containsKey("next");
    }

	public Integer getNext() {
		return hasNext() ? navigation.get("next") : null;
	}
	
}
