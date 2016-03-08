Pruning-Optimization-Tool
=======

Reads a text file containing a qualitative description of spatial obects and their relations. Based on rules regarding the relations between the objects and graph analysis the optimal objects for grounding are choosen. Variables of these objects are replaced with constant values.

## Usage
```java -jar opt-tool inputFile.txt outputFile.txt```

## Spatial Ontology
Following objects and relations can be used in the input file.

### Objects
    point
    line
    circle

### Relations

    centre			: point, circle  
    
    coincident 		: point, point  
    not_coincident 	: point, point  
    coincident 		: point, line  
    not_coincident 	: point, line  
    coincident 		: point, circle  
    not_coincident 	: point, circle  
    
    equal      		: point, point  
    not_equal      	: point, point  
    equal      		: line, line  
    not_equal      	: line, line  
    equal      		: circle, circle  
    not_equal      	: circle, circle  
    
    start_point 	: point, line   
    end_point 		: point, line  
    end_points 		: line, point, point 
    
    collinear		: point, line  
    parallel 		: line, line  
    perpendicular 	: line, line  
    
    radius_equal 	: circle, circle  
    radius_bigger 	: circle, circle  
    radius_smaller 	: circle, circle  
    length_equal 	: line, line  
    length_longer 	: line, line   
    length_shorter 	: line, line   

## Example Input

    \# Objects
    
    p1 point  
    p2 point   
    p3 point  
    
    c1 circle   
    c2 circle  
    
    l12 line  
    l13 line  
    l23 line  
    
    \# Relations
    
    end_points(p1,p2, l12)  
    end_points(p1,p3, l13)  
    end_points(p2,p3, l23)  
    
    not_coincident(p1,p2)  
    
    centre(p1,c1)
    centre(p2,c2)  
    
    coincident(p1,c2)  
    coincident(p2,c1)  
    coincident(p3,c1)  
    coincident(p3,c2)  
    
    %tests
    length_equal(l12, l13)  
    length_equal(l12, l23)  
    length_equal(l13, l23)   
