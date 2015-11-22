package de.ifgi.optimizer;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import de.ifgi.importer.ParsedInput;

public class Optimizer {
	// object types preserved under various affine transformations
	private HashMap<String, String[]> objectTypes;
	// Qualitative spatial relations preserved under various affine transformations
	private HashMap<String, String[]> spatialRelations;

	
	public Optimizer() {
		objectTypes = new HashMap<String, String[]>();
		spatialRelations = new HashMap<String, String[]>();
		// point, line, ellipse, convex polygon
		this.objectTypes.put("1", new String[] {"translate", "rotate", "scale", "reflect"});
		// circle, square, rectangle
		this.objectTypes.put("2", new String[] {"translate", "rotate", "uniform scale", "reflect"});
		
		// topology, mereology, coincidence, collinearity, line parallelism, packing size
		this.spatialRelations.put("1", new String[] {"translate", "rotate", "scale", "reflect"});
		// distance, length, line perpendicularity
		this.spatialRelations.put("2", new String[] {"translate", "rotate", "uniform scale", "reflect"});
		// relative orientation
		this.spatialRelations.put("3", new String[] {"translate", "rotate", "scale"});
	}
	
	public void optimize(ParsedInput input) {
		Set<String> objectTransforms = new HashSet<String>();
		Set<String> relationTransforms = new HashSet<String>();
		
		if (input.hasCircles()) {
			objectTransforms.addAll(Arrays.asList(objectTypes.get("2")));
		} else {
			objectTransforms.addAll(Arrays.asList(objectTypes.get("1")));
		}
		
		if (input.hasTypeThreeRelations()) {
			relationTransforms.addAll(Arrays.asList(spatialRelations.get("3")));
		} else if (input.hasTypeTwoRelations()) {
			relationTransforms.addAll(Arrays.asList(spatialRelations.get("2")));
		} else {
			relationTransforms.addAll(Arrays.asList(spatialRelations.get("1")));
		}
		
		objectTransforms.retainAll(relationTransforms);
		System.out.println(objectTransforms.toString());
	
		
		
		
	}

}
