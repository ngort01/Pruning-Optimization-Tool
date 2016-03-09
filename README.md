Pruning-Optimization-Tool
=======

Reads a text file containing a qualitative description of spatial obects and their relations. Based on rules regarding the relations between the objects and graph analysis the optimal objects for grounding are choosen. Variables of these objects are replaced with constant values.

## How it works
* The information in the input file is translated into a weighted graph ```G = (V,E)``` where vertices ```v``` represent spatial objects and edges ```e``` represent relations between objects.
* Weights of the edges are based on the type of relation (e.g. centre, coincindent) they represent. Each vertex gets an score calculated from the weights of its edges.
* If possible the graph is decomposed into independent subgraphs.
* For each subgraph the two vertices with the highest score are grounded by fixing their coordinates.
* Depending on what object was grounded, other related objects may be grounded too. E.g. if point ```p(x,y)``` is coincident with the grounded line ```l``` with start point ```s(0,0)``` and end point ```e(1,0)```, we can also set ```p.y = 0```.

## Usage
```java -jar opt-tool.jar inputFile.txt outputFile.txt customWeights.properties```

```java -jar opt-tool.jar -help```

Arguments

    inputFile.txt               text file containing a qualitative description of spatial obects and their relations
    outputFile.txt              outputFile name
    customWeights.properties    .properties file specifying weights for some or all relations
    
    -help                       prints help
    
## Custom Weights
Edges of the graph get weighted depending on the relation they represent.
The default weights are:

    centre=3.0
    ec=5.0
    coincidentPP=1.0
    coincidentPL=3.0
    coincidentPC=1.0
    
You can override these values by passing your own ```.properties``` file to the tool, specifying weights as in the example above.
Your file may include all relations listed in the spatial ontology below or just a few. Relations that are not included get a weight of ```1.0```.

In case of the ```coincident``` relation you have to differentiate between 3 types:

    coincidentPP:   point, point coincidence
    coincidentPL:   point, line coincidence
    coincidentPC:   point, circle coincidence

## Spatial Ontology
Following objects and relations can be used in the input file.

### Objects
    point
    line
    circle

### Relations

    centre			    : point, circle  
    
    coincident 		    : point, point  
    not_coincident 	    : point, point  
    coincident 		    : point, line  
    not_coincident 	    : point, line  
    coincident 		    : point, circle  
    not_coincident 	    : point, circle  
    
    equal      		    : point, point  
    not_equal      	    : point, point  
    equal      		    : line, line  
    not_equal      	    : line, line  
    equal      		    : circle, circle  
    not_equal      	    : circle, circle  
    
    start_point 	    : point, line   
    end_point 		    : point, line  
    end_points 		    : point, point, line
    
    collinear		    : point, line  
    not_collinear		: point, line  
    parallel 		    : line, line
    not_parallel 		: line, line  
    perpendicular 	    : line, line  
    
    radius_equal 	    : circle, circle  
    radius_bigger 	    : circle, circle  
    radius_smaller 	    : circle, circle 
    
    length_equal 	    : line, line
    not_length_equal    : line, line
    length_longer 	    : line, line   
    length_shorter 	    : line, line   

## Example Input File

    #Objects
    
    p1 point  
    p2 point   
    p3 point  
    
    c1 circle   
    c2 circle  
    
    l12 line  
    l13 line  
    l23 line  
    
    #Relations
    
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
    
    length_equal(l12, l13)  
    length_equal(l12, l23)  
    length_equal(l13, l23)   

## Example Output

    Subgraph 1
    
    OBJECTS p1 p2 p3 c1 c2 l12 l13 l23 
    DELETED length_equal l12 l13
    DELETED length_equal l12 l23
    DELETED length_equal l13 l23
    
    SUBCASE 1
    
    p1 x 0
    p1 y 0
    p2 x 1
    p2 y 0
    c1 x 0
    c1 y 0
    c2 x 1
    c2 y 0
