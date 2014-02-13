package edu.bwh.cctm.phylotime;

import java.util.ArrayList;
import java.util.HashMap;

public class PhyloTreeNodePlace extends PhyloTreeNode {
	// edges on which the node is inserted
	ArrayList<PhyloTreeEdge> edges = new ArrayList<PhyloTreeEdge>();
	// likelihoods weights for edge insertions
	HashMap<PhyloTreeEdge,Double> edgeLikeWt = new HashMap<PhyloTreeEdge,Double>();
	// distal lengths for edge insertions
	HashMap<PhyloTreeEdge,Double> distalLength = new HashMap<PhyloTreeEdge,Double>();
	// pendant lengths for edge insertions
	HashMap<PhyloTreeEdge,Double> pendantLength = new HashMap<PhyloTreeEdge,Double>();
	
	public PhyloTreeNodePlace(String n) {
		super(n);
	}
	
	public String placementInfo() {
		String s = "place " + name;
		
		for (int i = 0; i<edges.size();i++) {
			PhyloTreeEdge edge = edges.get(i);
			s = s + " [{" + edge.edgeNum + "} wt=" + edgeLikeWt.get(edge) + " dlen=" + distalLength.get(edge) + " plen=" + pendantLength.get(edge) + "]";
		}
		
		return s;
	}
}
