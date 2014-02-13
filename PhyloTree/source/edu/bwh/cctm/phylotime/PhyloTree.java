package edu.bwh.cctm.phylotime;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import net.sourceforge.olduvai.treejuxtaposer.TreeParser;
import net.sourceforge.olduvai.treejuxtaposer.drawer.TreeNode;

public class PhyloTree {
	PhyloTreeNodeStandard root = null;
	HashMap<String,PhyloTreeNode> nodes = new HashMap<String,PhyloTreeNode>();
	HashMap<String,PhyloTreeEdge> edges = new HashMap<String,PhyloTreeEdge>();
	HashMap<String,PhyloTreeNodePlace> placements = new HashMap<String,PhyloTreeNodePlace>();
	// map for all nodes (used to build distance matrix for nodes)
	HashMap<PhyloTreeNode,Integer> nodeMap = new HashMap<PhyloTreeNode,Integer>();
	
	public void buildNodeMap() {
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
		Iterator<PhyloTreeNodePlace> iterP = placements.values().iterator();
		
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
					PhyloTreeNodePlace node = null;
					node = placements.get(splitLine[1]);
					if (node == null) {
						node = new PhyloTreeNodePlace(splitLine[1]);
					}
					placements.put(node.name,node);
					PhyloTreeEdge edge = edges.get(splitLine[3]);
					edge.placedNodes.add(node);
					node.edges.add(edge);
					node.edgeLikeWt.put(edge,Double.valueOf(splitLine[4]));
					node.distalLength.put(edge,Double.valueOf(splitLine[8]));
					node.pendantLength.put(edge,Double.valueOf(splitLine[9]));
					line = is.readLine();
				}
			} catch(IOException e) {
				e.printStackTrace();
			}
		}
}
