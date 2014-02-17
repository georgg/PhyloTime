package edu.bwh.cctm.phylotime;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;

import net.sourceforge.olduvai.treejuxtaposer.TreeParser;
import net.sourceforge.olduvai.treejuxtaposer.drawer.Tree;
import net.sourceforge.olduvai.treejuxtaposer.drawer.TreeNode;

public class Test {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String treePath = "/users/georg/Dropbox/Projects/current/PhyloTime/test data/test.tree";
		String refPath = "/users/georg/Dropbox/Projects/current/PhyloTime/test data/Gut_Mouse_Phylum_1006_seq_info.csv";
		String placePath = "/users/georg/Dropbox/Projects/current/PhyloTime/test data/test_query.csv";
		//String treePath = "/users/georg/Dropbox/Projects/current/PhyloTime/test data/Gut_Mouse_Phylum_1006_FastTree.tre";
		PhyloTree myTree = new PhyloTree();
		myTree.readNewickTree(treePath);
		myTree.readReferenceSequenceInfo(refPath);
		myTree.readPlacements(placePath);
		myTree.recursiveTreePrint(myTree.root);
		myTree.placedNodePrint();
		myTree.calcDistances();
		myTree.outputNodeInfo();
		myTree.printDistances();
		
		/*	Tree myTree;
		BufferedReader r  = null;
		try {
			r = new BufferedReader(new FileReader(treePath));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		TreeParser tp = new TreeParser(r);
		myTree = tp.tokenize(1, "test", null);
        int tree_height = myTree.getHeight();
        System.out.println("largest tree height is: " + tree_height);
        recursive_print(0, 0, myTree); */
	}
	
	static void recursive_print (int currkey, int currdepth, Tree myTree) {
        TreeNode currNode = myTree.getNodeByKey(currkey);
        int numChildren = currNode.numberChildren();
        for (int i = 0; i < numChildren; i++) {
            int childkey = currNode.getChild(i).key;
            TreeNode childnode = myTree.getNodeByKey(childkey);
            System.out.println("child name is: " + childnode.getName()
                                 + " depth is: " + currdepth + " weight:" + childnode.getWeight());
            recursive_print(childkey, currdepth+1, myTree);
        }
	}
	
	

}
