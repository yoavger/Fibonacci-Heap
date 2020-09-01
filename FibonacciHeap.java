
/**
 * FibonacciHeap
 *
 * An implementation of fibonacci heap over integers.
 */
public class FibonacciHeap
{
	public HeapNode firstRoot;
	public HeapNode min;
	public int numTrees;
	public int size;
	public int numMarkNodes;
	public static int numLinks;
	public static int numCuts;
	
	
	// Constructor 
	public FibonacciHeap(){
		
		this.firstRoot = null;
		this.min = null;
		this.numTrees = 0;
		this.size = 0;
		this.numMarkNodes = 0;	
	}
	
   /**
    * public boolean isEmpty()
    *
    * precondition: none
    * 
    * The method returns true if and only if the heap
    * is empty.
    *   
    * Complexity --> o(1)
    */
    public boolean isEmpty()
    {
    	if (size == 0) {
    		return true; 
    	}
    	return false;
    }
		
   /**
    * public HeapNode insert(int key)
    *
    * Creates a node (of type HeapNode) which contains the given key, and inserts it into the heap. 
    * 
    * Complexity --> O(1)
    */
    public HeapNode insert(int key)
    {   
    	//Create a new node
    	HeapNode node = new HeapNode(key);
    	add(node);
    	//increment size of total heap
    	size++; 
    	return node;
    }
    /**
     * private HeapNode add(HeapNode node) 
     *
     * Inserts a given node or tree into the heap. 
     * 
     * Complexity --> O(1)
     */
    private HeapNode add(HeapNode node) {
    	
    	// Heap is empty, insert first node\tree
    	if (numTrees == 0) { 
    		min = node;
    	}
    	//concatenate node to root-list 
    	else {
    		node.next = firstRoot;
        	node.prev = firstRoot.prev;
        	firstRoot.prev.next = node;
        	firstRoot.prev  = node;
    	}
    	// update min if needed
    	if (node.key < min.key) { 
    		min = node;
    	}
    	// update numMarkNodes
    	if (node.mark) {
    		node.mark = false;
    		numMarkNodes--;
    	}
    	// update firstRoot pointer
    	firstRoot = node;
    	//increment size of roots
    	numTrees++;
    	
    	return node; 
    }

   /**
    * public void deleteMin()
    *
    * Delete the node containing the minimum key.
    *
    * Complexity --> O(n) W.C, o(log(n)) Amortized
    */
    public void deleteMin()
    {
    	HeapNode nodeMin = min;
    	
    	//heap is not empty
    	if (nodeMin != null) {
    		
    		HeapNode nodechild = nodeMin.child;
    		HeapNode nodeMinNext = nodeMin.next;
    		
    		// for each child of min-node add to the root-list
    		for (int i = 0 ; i < nodeMin.rank; i++) {
    			
    			nodechild.parent = null;
    			nodeMin.child = nodechild.next;
    			
    			// un-mark node 
    			if (nodechild.mark) {
    				nodechild.mark = false;
    				numMarkNodes--;
    			}
    			
    			// add child to the root list next to min
    			nodeMin.next = nodechild;
    			nodechild.prev = nodeMin;
    			nodechild.next = nodeMinNext;
    			nodeMinNext.prev = nodechild;
    			
    			numTrees++;		
    			
    			nodeMinNext = nodechild;
    			nodechild = nodeMin.child;
    			
    			/*
    			nodechild.parent = null;
    			node.child = nodechild.next;
    			add(nodechild);
    			nodechild = node.child;
    			*/
    		}
    		
    		// min-node is the only tree in the heap
    		if (nodeMin.next == nodeMin) {
    			firstRoot = null;
        		min = null;
        		numTrees = 0;
        		size = 0;
        	}
        	
    		else {
    			if (firstRoot == min) {
    				firstRoot = nodeMin.next;
    			}
        		min = nodeMin.next;
        		// remove min-node from root-list
        		nodeMin.next.prev = nodeMin.prev;
        		nodeMin.prev.next = nodeMin.next;
        		numTrees--;
        		size--;
        		consolidate();
        	}
    	}
    		
    }
    /**
     * private void consolidate()
     *
     * utility func
     * preforms consolidation post minimum deletion
     * 
     * Complexity --> O(n) W.C, o(log(n)) Amortized
     * 
     */
    
    private void consolidate() {
    
    	//calculating maximal degree of tree in fibonacci-heap with n items
     	int numCells = (int) Math.floor(1.4404*(1.0/Math.log(2))*Math.log(size))+2; 
    	HeapNode[] cells = new HeapNode[numCells]; 
    	//for each tree in root list
    	for (int i=0; i<numTrees; i++) { 
    		HeapNode x = firstRoot;
    		firstRoot = x.next;
    		x.next = x;
    		x.prev = x;
    		int d = x.rank;
    		//merging trees with same degrees already in cells
    		while (cells[d]!=null) {
    			HeapNode y = cells[d];	
    			x = link(x, y);	
    			cells[d] = null;
    			d++;
    			}
    		cells[d] = x;
    	}
    	//initializing tree but keeping count of size and markednodes count
      	firstRoot = null;
      	min = null;
      	numTrees = 0;
      	//inserting to heap each tree, now with unique degree
      	for (int i=0; i<numCells; i++) {
      		if (cells[numCells-1-i] != null) {
      			add(cells[numCells-1-i]);
      		}
      	}
    	
    }
    /**
     * private HeapNode link(HeapNode x , HeapNode y)
     *
     * Link two trees with the same degree, to one tree of degree +1
     *
     * Complexity --> O(1)
     */
    private HeapNode link(HeapNode x , HeapNode y) {
    	
    	// exchange keys of needed, to maintain heap property 
    	if(x.key > y.key) {
    		HeapNode temp = x;
    		x = y;
    		y = temp;
    	}
    	// add y as x child
    	if (x.child != null) {
    		y.next  = x.child;
    		y.prev = x.child.prev;
    		x.child.prev.next = y;
    		x.child.prev = y;
    	}
    	x.child = y;
    	y.parent = x;
    	
    	// increment rank of x 
    	x.rank++;
    	numLinks++;
    	return x;
    }
    
   /**
    * public HeapNode findMin()
    *
    * Return the node of the heap whose key is minimal. 
    *
    * Complexity --> O(1)
    */
    public HeapNode findMin()
    {
    	return min;
    } 
    
   /**
    * public void meld (FibonacciHeap heap2)
    *
    * Meld the heap with heap2
    * 
    * Complexity --> O(1)
    */
    public void meld (FibonacciHeap heap2)
    {
    	// heap2 is empty, no need to meld
    	if (heap2.isEmpty()) {
    		return;
    	}
    	
    	// concatenate root list of heap 1 and heap2
    	firstRoot.prev.next = heap2.firstRoot;
    	heap2.firstRoot.prev.next = firstRoot;
    	HeapNode x =  heap2.firstRoot.prev;
    	heap2.firstRoot.prev = firstRoot.prev;
    	firstRoot.prev = x;
    	
    	// update min if needed
    	if (min.key > heap2.min.key) {
    		min = heap2.min;
    	}
    	//update heap size, treeSize and count of mark nodes
    	numTrees += heap2.numTrees;
    	size += heap2.size;
    	numMarkNodes += heap2.numMarkNodes;	
    }

   /**
    * public int size()
    *
    * Return the number of elements in the heap
    *   
    * Complexity --> O(1)
    */
    public int size()
    {
    	return size; 
    }
    	
    /**
    * public int[] countersRep()
    *
    * Return a counters array, where the value of the i-th entry is the number of trees of order i in the heap. 
    * 
    */
    public int[] countersRep()
    {
    	int[] countersArray = new int[(int) Math.floor(1.4404*(1.0/Math.log(2))*Math.log(size))+1];
    	
    	HeapNode node = firstRoot;
    	
    	while (firstRoot != node.next) {
    		countersArray[node.rank]++;
    		node = node.next;
    	}
    	countersArray[node.rank]++;
    	
        return countersArray; 
    }
	
   /**
    * public void delete(HeapNode x)
    *
    * Deletes the node x from the heap. 
    * Performs Delete, by calling decreaseKey and delete min.
    * 
    * Complexity --> Amortized O(log(n)), WC o(n)
    */
    public void delete(HeapNode x) 
    {   
    	decreaseKey(x,(x.key+(-min.key+1)));
    	deleteMin();
    }

   /**
    * public void decreaseKey(HeapNode x, int delta)
    *
    * The function decreases the key of the node x by delta. The structure of the heap should be updated
    * to reflect this change (for example, the cascading cuts procedure should be applied if needed).
    * 
    * Complexity --> Amortized O(1)
    */
    public void decreaseKey(HeapNode x, int delta)
    {   
    	//decrease key 
    	x.key  = x.key-delta;
    	
    	HeapNode y = x.parent; 
    	//cheek if heap property maintain
    	if (y != null && x.key < y.key) {
    		cut(x,y);
    		cascadingCut(y);
    	}
    	if (x.key < min.key) {
    		min = x;
    	}
    }
    
    /**
     * private void cut(HeapNode x,HeapNode y)
     *
     * Utility function, performs cut
     * 
     * Complexity --> O(1)
     */
    private void cut(HeapNode x,HeapNode y) {
    	
    	//if y has only child
    	if (y.rank == 1) {
    		y.rank = 0;
    		y.child = null;
    		x.parent = null;
    		add(x);
    	}
    	//if y has more then one child, updating pointers
    	else { 
    		y.child = x.next;
    		x.next.prev = x.prev;
    		x.prev.next = x.next;
    		x.prev = x;
    		x.next = x;
    		x.parent = null;
    		y.rank--;
    		add(x);
    	}
    	numCuts++;
    }
    /**
     * private void cascadingCut (HeapNode y)
     *
     * Performs cascading cut
     * 
     * Complexity --> Amortized O(1)
     */
    private void cascadingCut(HeapNode y) {
    	
    	HeapNode z = y.parent;
    	
    	if (z != null) {
    		if (!y.mark) {
    			y.mark = true;
    			numMarkNodes++;
    		}
    		else {
    			cut(y,z);
    			cascadingCut(z);
    		}
    	}
    }

   /**
    * public int potential() 
    *
    * This function returns the current potential of the heap, which is:
    * Potential = #trees + 2*#marked
    * The potential equals to the number of trees in the heap plus twice the number of marked nodes in the heap. 
    * 
    * Complexity --> O(1)
    */
    public int potential() 
    {    
    	return numTrees + (2 * numMarkNodes);
    }

   /**
    * public static int totalLinks() 
    *
    * This static function returns the total number of link operations made during the run-time of the program.
    * A link operation is the operation which gets as input two trees of the same rank, and generates a tree of 
    * rank bigger by one, by hanging the tree which has larger value in its root on the tree which has smaller value 
    * in its root.
    * 
    * Complexity --> O(1)
    */
    public static int totalLinks()
    {    
    	return numLinks;
    }

   /**
    * public static int totalCuts() 
    *
    * This static function returns the total number of cut operations made during the run-time of the program.
    * A cut operation is the operation which diconnects a subtree from its parent (during decreaseKey/delete methods).
    * 
    *  Complexity --> O(1)
    */
    public static int totalCuts()
    {    
    	return numCuts; 
    }

     /**
    * public static int[] kMin(FibonacciHeap H, int k) 
    *
    * This static function returns the k minimal elements in a binomial tree H.
    * The function should run in O(k(logk + deg(H)). 
    */
    public static int[] kMin(FibonacciHeap H, int k)
    {    
    	int [] arrkMin = new int[k];
    	
    	if (H.size == 0) {
    		return arrkMin;
    	}
        
        FibonacciHeap kMinHeap = new FibonacciHeap();
        
        //insert first key
        arrkMin[0] = H.min.key;
        HeapNode x = H.min;
        HeapNode y = x.child;
       
        for (int i = 1 ; i < k ; i ++) {
        	// find the next min.
        	for (int j = 0 ; j < x.rank ; j++) {
        		kMinHeap.insert(y.key).pointerkMin = y;
        		y = y.next;
        	}
        	arrkMin[i] = kMinHeap.min.key;
        	x = kMinHeap.min.pointerkMin;
        	y = x.child;
        	kMinHeap.deleteMin();
        }
        return arrkMin;
    }

   /**
    * public class HeapNode
    * 
    * If you wish to implement classes other than FibonacciHeap
    * (for example HeapNode), do it in this file, not in 
    * another file 
    *  
    */
    public class HeapNode{

	public int key;
	public int rank;
	public Boolean mark;
	public HeapNode next;
	public HeapNode prev;
	public HeapNode parent;
	public HeapNode child;
	public HeapNode pointerkMin;// pointer for kMin application
		

  	public HeapNode(int key) {
	   
  		this.key = key;
	    this.rank = 0;
	    this.mark = false;
	    this.child = null;
	    this.next = this;
	    this.prev = this;
	    this.parent = null;  
	    this.pointerkMin = null;
  	}
  	public int getKey() {
	    return this.key;
      }

   }  
  }

    


