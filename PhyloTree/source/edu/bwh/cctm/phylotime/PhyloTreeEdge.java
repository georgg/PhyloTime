package edu.bwh.cctm.phylotime;

import java.util.ArrayList;

public class PhyloTreeEdge {
	// edge number (based on depth-first traversal)
	int edgeNum = 0;
	// length of the edge
	double edgeLength = 0.0;
	
	// node proximal to root
	PhyloTreeNode proximalNode = null;
	// node distal to root
	PhyloTreeNode distalNode = null;
	// nodes added via phylogenetic placement
	ArrayList<PhyloTreeNodePlace> placedNodes = new ArrayList<PhyloTreeNodePlace>();
	
	public PhyloTreeEdge(PhyloTreeNode prox,PhyloTreeNode dist,int en) {
		proximalNode = prox;
		distalNode = dist;
		edgeNum = en;
	}
}
