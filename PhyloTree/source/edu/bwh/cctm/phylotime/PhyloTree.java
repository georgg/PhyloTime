package edu.bwh.cctm.phylotime;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;

import net.sourceforge.olduvai.treejuxtaposer.TreeParser;
import net.sourceforge.olduvai.treejuxtaposer.drawer.TreeNode;

public class PhyloTree {
	PhyloTreeNodeStandard root = null;
	LinkedHashMap<String,PhyloTreeNode> nodes = new LinkedHashMap<String,PhyloTreeNode>();
	LinkedHashMap<String,PhyloTreeEdge> edges = new LinkedHashMap<String,PhyloTreeEdge>();
	LinkedHashMap<String,PhyloTreeNodePlace> placements = new LinkedHashMap<String,PhyloTreeNodePlace>();
	LinkedHashMap<String,OTU> OTUs = new LinkedHashMap<String,OTU>();
	// map for all nodes (used to build distance matrix for nodes)
	LinkedHashMap<PhyloTreeNode,Integer> nodeMap = new LinkedHashMap<PhyloTreeNode,Integer>();
	
	// calculate distances from placements to all nodes
	public void calcDistances() {
		buildNodeMap();
		
		Iterator<OTU> oIter = OTUs.values().iterator();
		while(oIter.hasNext()) {
			OTU o = oIter.next();
			o.calcDistanceUp(this);
		}
	}
	
	public void outputNodeInfo() {
		Iterator<PhyloTreeNode> iter = nodeMap.keySet().iterator();
		int i = 0;
		while (iter.hasNext()) {
			System.out.println(i + " " + iter.next().nodeInfo());
			i++;
		}
	}
	
	public void printDistances() {
		Iterator<OTU> iter = OTUs.values().iterator();
		while (iter.hasNext()) {
			iter.next().printDistances();
		}
	}
	
	private void buildNodeMap() {
		int i = 0;
		Iterator<PhyloTreeNode> iterN = nodes.values().iterator();
		while (iterN.hasNext()) {
			nodeMap.put(iterN.next(),i);
			i++;
		}
		Iterator<PhyloTreeNodePlace> iterI = placements.values().iterator();
		while (iterI.hasNext()) {
			nodeMap.put(iterI.next(),i);
			i++;
		}
	}
	
	// method loads a Newick formatted tree
	public void readNewickTree(String fileName) {
		BufferedReader r = null;
		try {
			r = new BufferedReader(new FileReader(fileName));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		TreeParser tp = new TreeParser(r);
		net.sourceforge.olduvai.treejuxtaposer.drawer.Tree myTree = tp.tokenize(1, "tree", null);
        
		root = new PhyloTreeNodeInternal("root");
		addNode(root);
		TreeNode rootParseNode = myTree.getNodeByKey(0);
		recursiveTreeBuild(0,root,rootParseNode,myTree);
	}
	
	private int recursiveTreeBuild(int ec, PhyloTreeNodeStandard parentNode, TreeNode parentParseNode, net.sourceforge.olduvai.treejuxtaposer.drawer.Tree myTree) {
		int numChildren = parentParseNode.numberChildren();
		int numEdge = ec;
		
        for (int i = 0; i < numChildren; i++) {
            int childkey = parentParseNode.getChild(i).key;
            TreeNode childnode = myTree.getNodeByKey(childkey);
            PhyloTreeNodeStandard newNode = null;
            String childName = childnode.getName();
            // is node terminal?
            if (childnode.isLeaf()) {
            	newNode = new PhyloTreeNodeTerminal(childName);
            } else {
            	newNode = new PhyloTreeNodeInternal(childName);
            }
            addNode(newNode);
            numEdge = recursiveTreeBuild(numEdge,newNode,childnode,myTree);
            
            PhyloTreeEdge edge = new PhyloTreeEdge(parentNode,newNode,numEdge);
            edges.put(Integer.toString(edge.edgeNum), edge);
            edge.edgeLength = childnode.getWeight();
            edge.distalNode = newNode;
            edge.proximalNode = parentNode;
            newNode.parentEdge = edge;
            ((PhyloTreeNodeInternal) parentNode).childEdges.add(edge);
            numEdge++;
        }
        
        return numEdge;
	}
	
	public void placedNodePrint() {
		Iterator<OTU> iterP = OTUs.values().iterator();
		
		while (iterP.hasNext()) {
			System.out.println((iterP.next()).placementInfo());
		}
	}
	
	public void recursiveTreePrint(PhyloTreeNode node) {
		if (!(node instanceof PhyloTreeNodeTerminal)) {
			System.out.println("node = " + node.name);
			ArrayList<PhyloTreeEdge> edges = ((PhyloTreeNodeInternal) node).childEdges;
			for (int i = 0; i < edges.size(); i++) {
				PhyloTreeEdge e = edges.get(i);
				System.out.println("edge {" + e.edgeNum + "} " + e.proximalNode.name + " -> " + e.distalNode.name + " length=" + e.edgeLength);
				recursiveTreePrint(e.distalNode);
			}
		} else {
			System.out.println("seq = " + node.name + " [" + ((PhyloTreeNodeTerminal) node).species_name + "]");
		}
	}
	
	public boolean addNode(PhyloTreeNode n) {
		boolean c = nodes.containsKey(n.name);
		if (c == false) {
			nodes.put(n.name, n);
		}
		return c;
	}
	
	// read taxonomic info for reference sequences
	public void readReferenceSequenceInfo(String fileName) {
		BufferedReader is = null;
		String line;
		String[] splitLine = null;
		
		try {
			is = new BufferedReader(new FileReader(fileName));
			// first line is column headers
			is.readLine();
			line = is.readLine();
			while (line != null) {
				splitLine = line.split(",");
				PhyloTreeNode node = nodes.get(splitLine[0]);
				if (node != null) {
					((PhyloTreeNodeTerminal) node).tax_id = splitLine[1];
					((PhyloTreeNodeTerminal) node).species_name = splitLine[2];
				}
				line = is.readLine();
			}
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	// read phylogenetically placed reads
		public void readPlacements(String fileName) {
			BufferedReader is = null;
			String line;
			String[] splitLine = null;
			
			try {
				is = new BufferedReader(new FileReader(fileName));
				// first line is column headers
				is.readLine();
				line = is.readLine();
				while (line != null) {
					splitLine = line.split(",");
					OTU myOTU = OTUs.get(splitLine[1]);
					if (myOTU == null) {
						myOTU = new OTU(splitLine[1]);
						OTUs.put(myOTU.name, myOTU);
					}
					PhyloTreeEdge edge = edges.get(splitLine[3]);
					PhyloTreeNodePlace node = new PhyloTreeNodePlace(splitLine[1]+"_"+edge.edgeNum);
					placements.put(node.name,node);
					myOTU.placements.add(node);
					edge.placedNodes.add(node);
					node.edge = edge;
					node.edgeLikeWt = Double.valueOf(splitLine[4]);
					node.distalLength = Double.valueOf(splitLine[8]);
					node.pendantLength = Double.valueOf(splitLine[9]);
					line = is.readLine();
				}
			} catch(IOException e) {
				e.printStackTrace();
			}
		}
}
