package org.esolution.poc.models;

import java.util.List;

import lombok.Data;

@Data
public class SearchResults {
	
	int current;
    Integer next;
    Integer last;    
    private List items;
	
}
