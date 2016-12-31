package ID3;

/*
 * ------------------------------------------------------------------------------------------------------------------------------
 * ID3DecisionTree.java
 * package - ID3
 * Program to build the Decision Tree based on ID3 algorithm making its decision to split the tree on different attributes at different levels based on   
 * Entropy and Gain calculations and allows node pruning. Also provides estimation of accuracy for both pre-pruned and post-pruned tree.
 * Author - Partha De, Somya Singh
 *  
 */

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Stack;

public class ID3DecisionTree {
	static int idCounter =0;
	
	//This function reads the input file and prepares the customised ArrayList of data keeping data intact based on its count and arrangements in which it occurs
	static ArrayList<int[]> readTrainData(BufferedReader reader, String[] attributes){
		int n = (int) Math.pow(2,attributes.length-1); //It creates the bit-wise arrangement ArrayList based on the number of attributes 
		ArrayList<int[]> nodeData = new ArrayList<int[]>();
		for(int i=0;i<n;i++){
			int[] rowData = new int[attributes.length+1];
			String a = String.format("%"+(attributes.length-1)+"s", Integer.toBinaryString(i)).replace(" ", "0");
			int j=0;
			for(;j<attributes.length-1;j++)
				rowData[j]=Character.getNumericValue(a.charAt(j));
			rowData[j]=0;
			rowData[j+1]=0;
			nodeData.add(rowData);
		}
		String temp = null;
		String st=null;
		try {
			while ((st=reader.readLine()) != null) { //Reading File
				if(st != null){
					temp = st.replace("\t",""); //removing tabs and spaces to concatenate string
					int classId = Character.getNumericValue(st.charAt(st.length()-1));
					temp = temp.substring(0,temp.length()-1);
					int index = Integer.parseInt(temp,2);
					if(classId == 0)
						nodeData.get(index)[attributes.length-1]++; //Building counts table out of data
					else
						nodeData.get(index)[attributes.length]++;
				}	
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		nodeData = purifyNodeData(nodeData);
		return nodeData;
	}

	//Removing noise from the data, it removes all those rows from the table which has data neither contributing to class 0 output or class1 output
	private static ArrayList<int[]> purifyNodeData(ArrayList<int[]> nodeData) {
		ArrayList<int[]> correcteNodeData = new ArrayList<int[]>();
		for(int i=0;i<nodeData.size();i++){
			if(nodeData.get(i)[nodeData.get(i).length-2]!=0 || nodeData.get(i)[nodeData.get(i).length-1]!=0)
				correcteNodeData.add(nodeData.get(i));
		}
		return correcteNodeData;
	}

	//This function selects the best attribute to partition data based on the maximum information gain, comparing information gain from all the attributes
	private static int getBestAttribute(DecisionTreeNode decisionTree) {
		ArrayList<int[]> finalData = decisionTree.getDataSet();
		int[] atbChecker = decisionTree.getAtbSelectionChecker();
		int bestAttributeLoc = -1;
		double maxGain = 0;
		for(int i=0;i<atbChecker.length;i++){
			if(atbChecker[i]!=0){
				double tempGain = getGain(finalData, i); //Calls for getGain function where information gain is calculated
				if(tempGain>maxGain){
					maxGain = tempGain;
					bestAttributeLoc = i;
				}
			}
		}
		if(maxGain==0)
			return -1;
		else
			return bestAttributeLoc;
	}

	//This function calculates the information gain for various attributes
	private static double getGain(ArrayList<int[]> finalData, int attributeLoc) {
		RetType retVal = getEntropy(finalData,attributeLoc);
		double countPAndN = retVal.getCountP()+retVal.getCountN();
		double ratioP = retVal.getCountP()/countPAndN;
		double ratioN = retVal.getCountN()/countPAndN;
		double gain = retVal.geteS()-(ratioP*retVal.geteSP()+ratioN*retVal.geteSN());
		return gain;
	}

	//This function calculates values like probabilities and counts which are needed to calculate entropy and information gain
	private static RetType getEntropy(ArrayList<int[]> finalData, int attributeLoc) {
		int countNegAtbNegCls = 0,countNegAtbPtvCls = 0,countPtvAtbNegCls = 0,countPtvAtbPtvCls = 0;
		for(int i=0;i<finalData.size();i++){
			if(finalData.get(i)[attributeLoc]==0){ //Calculates counts for all set of records if class output for particular set of value of attributes is 0
				countNegAtbNegCls += finalData.get(i)[finalData.get(i).length-2];
				countNegAtbPtvCls += finalData.get(i)[finalData.get(i).length-1];
			}
			else{
				countPtvAtbNegCls += finalData.get(i)[finalData.get(i).length-2];
				countPtvAtbPtvCls += finalData.get(i)[finalData.get(i).length-1];
			}
		}
		double entropyAtbPtv = calcEntropy(countPtvAtbNegCls,countPtvAtbPtvCls);
		double entropyAtbNeg = calcEntropy(countNegAtbNegCls,countNegAtbPtvCls);
		double entropyCls = calcEntropy(countPtvAtbNegCls+countNegAtbNegCls,countPtvAtbPtvCls+countNegAtbPtvCls);
		RetType retVal = new RetType(entropyCls,entropyAtbPtv,entropyAtbNeg,countNegAtbNegCls+countNegAtbPtvCls,countPtvAtbNegCls+countPtvAtbPtvCls);
		return retVal;
	}
	
//This function calculates entropy value
	private static double calcEntropy(int countNeg, int countPtv) {
		double totalCount = countNeg+countPtv;
		double probPtv = countPtv/totalCount;
		double probNeg = countNeg/totalCount;
		return (-probPtv*Math.log(probPtv)/Math.log(2)-probNeg*Math.log(probNeg)/Math.log(2));
	}

	//This is call to partition the data based on best attribute. It terminates partitioning if we are not left with any attributes further to partition data and set last nodes as leaf nodes
	private static DecisionTreeNode partitionDataAux(DecisionTreeNode decisionTree, int bestAttributeLoc) {
		partitionData(decisionTree,bestAttributeLoc);
		int bestAtbLocLeft = getBestAttribute(decisionTree.getLeftChild());
		if(bestAtbLocLeft != -1)
			partitionDataAux(decisionTree.getLeftChild(),bestAtbLocLeft);
		else
			setAsLeafNode(decisionTree.getLeftChild());
		int bestAtbLocRight = getBestAttribute(decisionTree.getRightChild());
		if(bestAtbLocRight != -1)
			partitionDataAux(decisionTree.getRightChild(),bestAtbLocRight);
		else
			setAsLeafNode(decisionTree.getRightChild());
		return decisionTree;
	}

	
	//This function actually partitions the data on the best attribute and sets half the partition as left child of tree and other set as right child of the tree.
	private static boolean partitionData(DecisionTreeNode decisionTree, int atbLoc) {
		ArrayList<int[]> nodeData = decisionTree.getDataSet();
		ArrayList<int[]> leftChildDataSet = new ArrayList<int[]>();
		ArrayList<int[]> rightChildDataSet = new ArrayList<int[]>();
		decisionTree.setAtbSelectionChecker(atbLoc);
		int[] selCheckerLC = new int[decisionTree.getAtbSelectionChecker().length];
		int[] selCheckerRC = new int[decisionTree.getAtbSelectionChecker().length];
		for(int i=0;i<selCheckerLC.length;i++){
			selCheckerLC[i]=decisionTree.getAtbSelectionChecker()[i];
			selCheckerRC[i]=decisionTree.getAtbSelectionChecker()[i];
		}
				
		for(int i=0;i<nodeData.size();i++){
			if(nodeData.get(i)[atbLoc]==0)
				leftChildDataSet.add(nodeData.get(i));
			else
				rightChildDataSet.add(nodeData.get(i));
		}
		//Set the member variables,
		DecisionTreeNode leftChild = new DecisionTreeNode(idCounter,selCheckerLC,decisionTree,leftChildDataSet);
		idCounter++;
		DecisionTreeNode rightChild = new DecisionTreeNode(idCounter,selCheckerRC,decisionTree,rightChildDataSet);
		idCounter++;
		decisionTree.setLeftChild(leftChild);
		decisionTree.setRightChild(rightChild);
		decisionTree.setDataSet(null);
		return true;	
	}

	//This function sets the output for leaf nodes based on the majority of values in the class, in case they are not pure classes
	private static void setAsLeafNode(DecisionTreeNode node) {
		ArrayList<int[]> nodeData = node.getDataSet();
		int countP=0,countN=0;
		for(int i=0;i<nodeData.size();i++){
			countP+=nodeData.get(i)[nodeData.get(i).length-1];
			countN+=nodeData.get(i)[nodeData.get(i).length-2];
					
		}
		if(countP==0||countN ==0)
			node.setPure(true);
		if(countP>countN)
			node.setClassId(1);
		else
			node.setClassId(0);
	}
	
	
	private static double calculateAccuracy(DecisionTreeNode decisionTree, BufferedReader reader, int length) {
		int errorCount = 0;
		int correctCount = 0;
		String st=null;
		int[] testData = new int[length];
		try {
			while ((st=reader.readLine()) != null) {
				if(st != null){
					recordCount++;
					String[] temp = st.split("	");
					for(int i=0;i<temp.length;i++)
						testData[i]=  Integer.parseInt(temp[i]);
					boolean flag = checkIfCorrectClass(decisionTree,testData);
					if(flag == true)
						correctCount++;
					else{
						errorCount++;
					}
				}
			}
		}catch (IOException e) {
			e.printStackTrace();
		}
		double totalCount = errorCount+correctCount;
		double p = correctCount/totalCount;
		return p;
	}

	private static boolean checkIfCorrectClass(DecisionTreeNode decisionTree, int[] data) {
		DecisionTreeNode temp = decisionTree;
		int classId=-1;
		for(int i=0;i<data.length;i++){
			if(temp.getLeftChild()==null){
				classId = temp.getClassId();
				break;
			}
			else{
			int atbToBreakLoc = temp.getChangeLoc();
			if(data[atbToBreakLoc]==0)
				temp=temp.getLeftChild();
			else
				temp=temp.getRightChild();
			}
		}
		if(classId==data[data.length-1])
			return true;
		else{
			return false;
		}
	}
	
	//This function gives the order of attributes in which tree is partitioned 
	private static ArrayList<DecisionTreeNode> postOrderTraversalOfTree(DecisionTreeNode root) {
		ArrayList<DecisionTreeNode> postOderList = new ArrayList<DecisionTreeNode>();
		if(root == null)
			return null;
		Stack<DecisionTreeNode> st = new Stack<DecisionTreeNode>();
		DecisionTreeNode current = root;
	    while(true){
	    	if(current != null){
	    		if(current.getRightChild() != null)
	    			st.push(current.getRightChild());  
	            st.push(current);  
	            current = current.getLeftChild();  
	            continue;
	            }
	    	if(st.isEmpty())
	    		return postOderList;
	    	current = st.pop();
	    	if(current.getRightChild() != null && !st.isEmpty() && current.getRightChild() == st.peek()){
	    		st.pop(); 
	    		st.push(current);
	    		current = current.getRightChild();
	    		}
	    	else{
	    		postOderList.add(current);
	    		current = null;
	    		}
	    		
	    	}
	    }  
		
	//This function is for pruning the Decision Tree
	private static DecisionTreeNode pruningTree(DecisionTreeNode tree,int numOfNodesToPrune) {
		int deleteCount = 0;
		ArrayList<DecisionTreeNode> postOderList = postOrderTraversalOfTree(tree);
		while(deleteCount < numOfNodesToPrune && postOderList.size()!=0){
			for(int i=0;i<postOderList.size();i++){
				if(deleteCount >= numOfNodesToPrune || postOderList.size()<=3)
					break;
				if(postOderList.get(i).getClassId()!=-1 && postOderList.get(i+1).getClassId()!=-1 && postOderList.get(i).getParent()==postOderList.get(i+1).getParent()){
					pruneLeafNodes(tree,postOderList.get(i),postOderList.get(i+1));
					postOderList.remove(i);
					postOderList.remove(i);
					deleteCount+=2;
				}
			}
		}
		return tree;
	}
	
	//In case of leaf nodes being pruned, the parents left or right child or both n]being set to null and entire data 
	//goes to the immediate parent
	private static void pruneLeafNodes(DecisionTreeNode tree, DecisionTreeNode nodeL, DecisionTreeNode nodeR) {
		DecisionTreeNode parent = nodeL.getParent();
		ArrayList<int[]> dataSet = nodeL.getDataSet();
		ArrayList<int[]> dataSetOther = nodeR.getDataSet();
		dataSet.addAll(dataSetOther);

		parent.setDataSet(dataSet);
		setAsLeafNode(parent);
		parent.setLeftChild(null);
		parent.setRightChild(null);
	}

	//in this part of the program, test data is being executed
	private static void testingMyModel(boolean a,boolean b, String trainDataPath,DecisionTreeNode model) {
		try {
			BufferedReader reader = new BufferedReader(new FileReader(trainDataPath));
			double accuracy = 0;
			int recCount =0;
			String[] attributesTest = null;
			try{
				String firstLine=reader.readLine();
				attributesTest = firstLine.split("	");
				accuracy = calculateAccuracy(model,reader,attributesTest.length)*100;
				recCount = recordCount;
				recordCount = 0;
			}
			catch (IOException e) {
				System.out.println("Error while testing data.");
				e.printStackTrace();
			}
			NodeCount nc = getNodeCount(model);
			
			if(!b){
				treeNodeCount = nc.getTotalNodeCount();
				printSummaryOfData(a,false,recCount,attributesTest.length-1,nc.getTotalNodeCount(),nc.getLeafNodeCount(),accuracy);
			}
			else{
				printSummaryOfData(a,true,recCount,attributesTest.length-1,nc.getTotalNodeCount(),nc.getLeafNodeCount(),accuracy);
				
			}
			} catch (FileNotFoundException e1) {
				e1.printStackTrace();
			}
	}

	//This function is to keep the count of nodes in decision tree. Class NodeCount has been defined
	private static NodeCount getNodeCount(DecisionTreeNode model) {
		NodeCount nc;
		DecisionTreeNode temp = model;
		if(temp.getLeftChild()==null){
			nc = new NodeCount(1,1);
			return nc;
		}
		else{
			NodeCount ncL,ncR;
			ncL = getNodeCount(temp.getLeftChild());
			ncR = getNodeCount(temp.getRightChild());
			nc = new NodeCount(ncL.getTotalNodeCount()+ncR.getTotalNodeCount()+1, ncL.getLeafNodeCount()+ncR.getLeafNodeCount());
			return nc;
		}
	}


	//This function prints the decision ont he console
	public static void printTree(String[] attributes, DecisionTreeNode root,int tabCount) {
		if(root.getLeftChild()==null && root.getRightChild()==null){
			System.out.print(root.getClassId());
			return;
		}
		else{
			if(tabCount!=0)
				System.out.println();
			for(int i=0;i<tabCount;i++)
				System.out.print("|  ");
			System.out.print(attributes[root.getChangeLoc()]+" = 0 : ");
			printTree(attributes,root.getLeftChild(),tabCount+1);
			System.out.println();
			for(int i=0;i<tabCount;i++)
				System.out.print("|  ");
			System.out.print(attributes[root.getChangeLoc()]+" = 1 : ");
			printTree(attributes,root.getRightChild(),tabCount+1);
		}

	}
	
	//This function prints the summary(accuracy and count data) on the console. When true is passed to the function
	//it prints summary for train data and summary of test data in case of false.
	private static void printSummaryOfData(boolean prePruned,boolean testOnTestData, int instanceCount,int atbCount,int nodeCount,int leafCount, double accuracy) {		
		if(!testOnTestData){
			System.out.println("\n");
			if(!prePruned)
				System.out.println("Pre-Pruned Accuracy\n------------------------");
			else
				System.out.println("Post-Pruned Accuracy\n------------------------");
			System.out.println("Number of training instances = "+instanceCount);
			System.out.println("Number of training attributes = "+atbCount);
			System.out.println("Total number of nodes in the tree = "+nodeCount);
			System.out.println("Total number of leaf nodes in the tree = "+leafCount);
			System.out.println("Accuracy of the model on the training dataset = "+accuracy+"%");
			System.out.println();
		}
		else{
			System.out.println("Number of testing instances = "+instanceCount);
			System.out.println("Number of testing attributes = "+atbCount);
			System.out.println("Accuracy of the model on the testing dataset = "+accuracy+"%");
		}
	}
	static int recordCount=0;
	static int treeNodeCount=0;
	
	//Main function. Train and test files are being read here
	public static void main(String[] args) {
		String trainDataPath="/C:/Somya/Semester_1/ML/Assignments/Assignment1/data/train.dat";
		String testDataPath="/C:/Somya/Semester_1/ML/Assignments/Assignment1/data/test.dat";
		int numOfNodesToPrune = -1;
		
		if(args.length==3){ // If 3 arguments are passed, number og nodes to be pruned being third argument, it acts accordingly
			trainDataPath = args[0];
			testDataPath= args[1];
			numOfNodesToPrune=Integer.parseInt(args[2]);
		}
		else if(args.length==1)
			numOfNodesToPrune=Integer.parseInt(args[0]);
		else if(args.length!=0){ // If no arguments are provided, it prompts to provide the data
			System.out.println("Usage:\tjava ID3DecisionTree <Num_Of_Nodes_To_Prune>\n\tjava ID3DecisionTree <Training_Data_Path> <Testing_Data_Path> <Num_Of_Nodes_To_Prune>");
			System.exit(0);
		}
			
		String[] attributes = null;
		DecisionTreeNode decisionTree = null;
		DecisionTreeNode decisionTreeAfterPruning = null;
		String firstLine = null;
		try {
			BufferedReader reader = new BufferedReader(new FileReader(trainDataPath));
			try {
				firstLine=reader.readLine();			
			} catch (IOException e) {
				System.out.println("Error while building the decision tree.");
				e.printStackTrace();
			}
			attributes = firstLine.split("	");
			ArrayList<int[]> finalData = readTrainData(reader,attributes);
			decisionTree = new DecisionTreeNode(idCounter,attributes.length);
			idCounter++;
			decisionTree.setDataSet(finalData);
			
			int bestAttributeLoc = getBestAttribute(decisionTree);
			if(bestAttributeLoc != -1){
				decisionTree=partitionDataAux(decisionTree,bestAttributeLoc);
			}
			System.out.println("Pre-Pruned Decision Tree: \n------------------------");
			printTree(attributes,decisionTree,0);	//0 is the 'tab' count	
			testingMyModel(false,false,trainDataPath,decisionTree);
			if(numOfNodesToPrune==-1)
				numOfNodesToPrune=treeNodeCount/2-1; // By default program prunes (n/2)-1 number of nodes of tree
			testingMyModel(false,true,testDataPath,decisionTree);
			
			System.out.println("\n\nPost-Pruned Decision Tree: \n------------------------");
			decisionTreeAfterPruning = pruningTree(decisionTree,numOfNodesToPrune);
			printTree(attributes,decisionTreeAfterPruning,0); //0 is the 'tab' count
			testingMyModel(true,false,trainDataPath,decisionTreeAfterPruning);
			testingMyModel(true,true,testDataPath,decisionTreeAfterPruning);
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}	
	}
}
