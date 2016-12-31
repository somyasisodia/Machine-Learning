package ID3;

class NodeCount{
	private int totalNodeCount;
	private int leafNodeCount;
	
	public NodeCount(int totalCount, int leafCount) {
		totalNodeCount = totalCount;
		leafNodeCount = leafCount;
	}
	public int getTotalNodeCount() {
		return totalNodeCount;
	}
	public void setTotalNodeCount(int totalNodeCount) {
		this.totalNodeCount = totalNodeCount;
	}
	public int getLeafNodeCount() {
		return leafNodeCount;
	}
	public void setLeafNodeCount(int leafNodeCount) {
		this.leafNodeCount = leafNodeCount;
	}
	
}