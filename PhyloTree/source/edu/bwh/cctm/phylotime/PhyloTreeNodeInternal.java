package edu.bwh.cctm.phylotime;

import java.util.ArrayList;

public class PhyloTreeNodeInternal extends PhyloTreeNodeStandard {
	ArrayList<PhyloTreeEdge> childEdges = new ArrayList<PhyloTreeEdge>();
	
	public PhyloTreeNodeInternal(String n) {
		super(n);
	}

	public String nodeInfo() {
		return name + " [internal]";
	}
}
