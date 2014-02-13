package edu.bwh.cctm.phylotime;

public abstract class PhyloTreeNodeStandard extends PhyloTreeNode {
	PhyloTreeEdge parentEdge = null;
	public PhyloTreeNodeStandard(String n) {
		super(n);
	}
}
