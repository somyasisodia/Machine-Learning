package ID3;

import java.util.ArrayList;

public class DecisionTreeNode {
	private int nodeId;
	private int changeLoc;
	private int[] atbSelectionChecker;
	private int classId = -1;// -1 means non-leaf node. 0/1 means leaf node.
	private boolean pure = false;
	private DecisionTreeNode leftChild=null;
	private DecisionTreeNode rightChild=null;
	private DecisionTreeNode parent = null;
	private ArrayList<int[]> dataSet;
	
	
	public DecisionTreeNode() {
		super();
	}

	public DecisionTreeNode(int idCounter,int size) {
		super();
		nodeId = idCounter;
		atbSelectionChecker=new int[size-1];
		for(int i=0;i<atbSelectionChecker.length;i++)
			atbSelectionChecker[i]=-1;
	}

	public DecisionTreeNode(int idCounter,int[] aSC, DecisionTreeNode parent, ArrayList<int[]> dataSet) {
		nodeId = idCounter;
		atbSelectionChecker=aSC;
		this.parent = parent;
		this.dataSet = dataSet;
	}
	
	public int getNodeId() {
		return nodeId;
	}

	public void setNodeId(int nodeId) {
		this.nodeId = nodeId;
	}

	public boolean isPure() {
		return pure;
	}

	public void setPure(boolean pure) {
		this.pure = pure;
	}
	
	public int getChangeLoc() {
		return changeLoc;
	}

	public void setChangeLoc(int changeLoc) {
		this.changeLoc = changeLoc;
	}

	public int getClassId() {
		return classId;
	}
	
	public void setClassId(int classId) {
		this.classId = classId;
	}

	public DecisionTreeNode getLeftChild() {
		return leftChild;
	}

	public void setLeftChild(DecisionTreeNode leftChild) {
		this.leftChild = leftChild;
	}
	
	public int[] getAtbSelectionChecker() {
		return atbSelectionChecker;
	}

	public void setAtbSelectionChecker(int[] atbSelectionChecker) {
		this.atbSelectionChecker = atbSelectionChecker;
	}

	public DecisionTreeNode getParent() {
		return parent;
	}

	public void setParent(DecisionTreeNode parent) {
		this.parent = parent;
	}

	public DecisionTreeNode getRightChild() {
		return rightChild;
	}

	public void setRightChild(DecisionTreeNode rightChild) {
		this.rightChild = rightChild;
	}

	public ArrayList<int[]> getDataSet() {
		return dataSet;
	}

	public void setDataSet(ArrayList<int[]> dataSet) {
		this.dataSet = dataSet;
	}

	public void setAtbSelectionChecker(int bestAttributeLoc) {
		atbSelectionChecker[bestAttributeLoc]=0;
		changeLoc=bestAttributeLoc;
	}

	
	
	
	
	
	
}
