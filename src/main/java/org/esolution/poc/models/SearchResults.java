package org.esolution.poc.models;

import java.util.List;

public class SearchResults {
	
	int current;
    Integer next;
    Integer last;
    
    private List items;
    
    public Integer nextPage() {
    	return next;
    }
    
    public <T> List<T> items() {
    	return items;
    }
	
}
