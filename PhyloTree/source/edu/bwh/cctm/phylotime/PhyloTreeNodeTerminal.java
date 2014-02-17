package edu.bwh.cctm.phylotime;

public class PhyloTreeNodeTerminal extends PhyloTreeNodeStandard {
	String species_name;
	String tax_id;
	
	public PhyloTreeNodeTerminal(String n) {
		super(n);
	}
	
	public String nodeInfo() {
		return name + "[ref=" + species_name + "]";
	}

}
