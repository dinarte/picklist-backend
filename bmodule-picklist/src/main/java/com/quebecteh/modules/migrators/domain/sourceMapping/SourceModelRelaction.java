package com.quebecteh.modules.migrators.domain.sourceMapping;

import java.util.ArrayList;
import java.util.List;

import com.quebecteh.modules.migrators.domain.SourceModel;

import ch.qos.logback.core.model.Model;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class SourceModelRelaction {

	private final SourceModel model;
	
	private List<Join> joins;
	
	List<SourceModelRelaction> relacteds;
	
	List<SourceModelRelaction> collecions;
	
	
	
	
	public static SourceModelRelaction buildWith(SourceModel model) {
		SourceModelRelaction relaction = new SourceModelRelaction(model);
		return relaction;
	}
	
	public SourceModelRelaction addRelacted(Model model, List<Join> joins) {
		
		if (relacteds == null) {
			relacteds = new ArrayList<SourceModelRelaction>();
			//SourceModelRelaction relaction = new SourceModelRelaction(model);
		}
		
		return this;
	}
	

}
