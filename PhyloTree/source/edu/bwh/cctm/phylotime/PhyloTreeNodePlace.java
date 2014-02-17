package edu.bwh.cctm.phylotime;

import java.util.ArrayList;
import java.util.HashMap;

public class PhyloTreeNodePlace extends PhyloTreeNode {
	// edge on which the node is inserted
	PhyloTreeEdge edge = null;
	// likelihood weight for the edge insertion
	double edgeLikeWt = 0.0;
	// distal length for edge insertion
	double distalLength = 0.0;
	// pendant length for edge insertion
	double pendantLength = 0.0;
	// distances to all other nodes for each placement
	ArrayList<Double> distances = new ArrayList<Double>();
	
	public PhyloTreeNodePlace(String n) {
		super(n);
	}
	
	public String nodeInfo() {
		return name + " [placed]";
	}
}
