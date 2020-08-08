
import java.io.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;


public class AprioriTgui extends JFrame implements ActionListener{
    
    public BufferedReader fileInput;
    public static JTextArea textArea;
    public  JButton openButton, minSupport, runButton,CompareButton,Restart;
    public  Container container;
    public 	JPanel buttonPanel;
    public String dicm;
    
    protected class TtreeNode {
        protected int support = 0;
	protected TtreeNode[] childRef = null;
	
	
	protected TtreeNode() {
	    }
	
	private TtreeNode(int sup) {
	    support = sup;
	    }
	}
    
    private TtreeNode[] startTtreeRef; 
    private short[][] dataArray = null;
    private static final double MIN_SUPPORT = 0.0;
    private static final double MAX_SUPPORT = 100.0;
    
    dic process1;
    FPgrowthApp fp;
    Mba mba;
    
    private boolean inputFormatOkFlag = true;
    private boolean haveDataFlag = false;
    private boolean hasSupportFlag = false;
    private boolean nextLevelExists = true ;  
    public static File fileName;
    public static int numRows =0;
    public static int numCols =0;
    public static double support = 20.0;
    private double minSupportRows = 1.0;
    public AprioriTgui()
    {
    }
   
    public AprioriTgui(String s) 
    {
      super(s);
    
        container = getContentPane(); 
        container.setBackground(Color.white);
        container.setLayout(new BorderLayout(5,5)); // 5 pixel gaps
	
	  runButton = new JButton("Run");
	  runButton.addActionListener(this);
	  runButton.setEnabled(false);
	
        openButton = new JButton("Open File");
        openButton.addActionListener(this);
	
        minSupport = new JButton("Add Min. Sup.");
        minSupport.addActionListener(this);
	
	 buttonPanel = new JPanel();
	 buttonPanel.setLayout(new GridLayout(1,3));
	buttonPanel.add(openButton);
	buttonPanel.add(minSupport);
	buttonPanel.add(runButton);
	container.add(buttonPanel,BorderLayout.NORTH); 
	
	textArea = new JTextArea(40, 15);
	textArea.setEditable(false);
      container.add(new JScrollPane(textArea),BorderLayout.CENTER);
  	}
    
  
    public void actionPerformed(ActionEvent event) {
        if (event.getActionCommand().equals("Open File")) getFileName();
	if (event.getActionCommand().equals("Read File")) readFile();
        if (event.getActionCommand().equals("Add Min. Sup.")) addSupport();
	if (event.getActionCommand().equals("Run")) showDialog();
	}
	
   
   private void showDialog()
   {
   	Object[] possibleValues = { "WD-FIM" };
      Object selectedValue = JOptionPane.showInputDialog(null,"Choose Algorithm", "Input",JOptionPane.INFORMATION_MESSAGE, null,possibleValues, possibleValues[0]);
   
    try{
    	if (selectedValue.equals("Apriori"))
    					aprioriT(); 
        if (selectedValue.equals("Dynamic Itemset Counting"))
  	  	{
    		 textArea.append("Dic running");
    		 getstepsize();
    		 process1=new dic();
    		 process1.sendData(fileName,support,dicm,numCols,numRows);
    		 process1.dic();					
    		}
    		
    		if (selectedValue.equals("WD-FIM"))
    	{
    		 textArea.append("Weighted FIM Algorithm running");
    		 fp=new FPgrowthApp();
    		 fp.sendData(fileName,support);
    	}
    		
    		if (selectedValue.equals("MBA"))
    	{
    		 textArea.append("MBA running");
    		 mba=new Mba();
    		 mba.sendData(fileName,support,numCols,numRows);
    		 mba.start();
    	}
    	
     	}
    	catch(Exception e)
    	{    	}
    }  
   
   
   public void getstepsize()
   {
   			dicm = JOptionPane.showInputDialog("Input Step length ");
			textArea.append("\n Step length:" +dicm);
   }
   
    
    
    private void aprioriT() 
    {
    	
    		Date d = new Date();
    		long s1,s2;
    		
		d = new Date();
		s1 = d.getTime();
		System.out.println(s1);
		
 	textArea.append("Apriori-T (Minimum support threshold = " + support +
		     "%)\n-----------------------------------------\n" +
		"Generating K=1 large itemsets\n");
        
	minSupportRows = numRows*support/100.0;
	
	createTtreeTopLevel();
	
	generateLevel2();
	
	createTtreeLevelN();
	
	textArea.append("\n");
	outputFrequentSets();
	
	d = new Date();
	s2 = d.getTime();
	
	System.out.println(s2);
	textArea.append("\n\nExecution time is: " + (s2 - s1) + " milliseconds.\n\n");
	
	}

    protected void createTtreeTopLevel() 
	{
    	startTtreeRef = new TtreeNode[numCols+1];
	for (int index=1;index<=numCols;index++) 
	    			startTtreeRef[index] = new TtreeNode();
	createTtreeTopLevel2();
	pruneLevelN(startTtreeRef,1); 
	}
	
    protected void createTtreeTopLevel2() 
	{
	for (int index1=0;index1<dataArray.length;index1++) 
	{
	    if (dataArray[index1] != null) 
		{
    	        for (int index2=0;index2<dataArray[index1].length;index2++) 
		   {
		    startTtreeRef[dataArray[index1][index2]].support++; 
		    }
		}
	    }
	}	
	protected void createTtreeLevelN() 
	{
        int nextLevel=2;
   	  while (nextLevelExists) { 
	    textArea.append("Generating K=" + nextLevel + " large itemsets\n");
	    addSupportToTtreeLevelN(nextLevel);
	    pruneLevelN(startTtreeRef,nextLevel);
	    nextLevelExists=false;
	    generateLevelN(startTtreeRef,1,nextLevel,null); 
	    nextLevel++;
	    }   
	}
 	
protected void addSupportToTtreeLevelN(int level) 
	{
	  for (int index=0;index<dataArray.length;index++) 
	  {
	  if (dataArray[index] != null) 
		{
	        addSupportToTtreeFindLevel(startTtreeRef,level,
		  dataArray[index].length,dataArray[index]);
	        }
	    }
	} 
	
    private void addSupportToTtreeFindLevel(TtreeNode[] linkRef, int level, 
    			int endIndex, short[] itemSet) {
	
	if (level == 1) 
	{
	   for (int index1=0;index1 < endIndex;index1++) {
		if (linkRef[itemSet[index1]] != null) {
		    linkRef[itemSet[index1]].support++; 
		    }
		}
	    }
	
	else {
	    for (int index=0;index<endIndex;index++) {		
		if (linkRef[itemSet[index]] != null) {
		    if (linkRef[itemSet[index]].childRef != null) 
		    	 addSupportToTtreeFindLevel(linkRef[itemSet[index]].childRef,
						level-1,index,itemSet);
		    }
		}
	    }	
	}
	
    protected void pruneLevelN(TtreeNode [] linkRef, int level) {
        int size = linkRef.length;
	
	if (level == 1) {
	    for (int index1=1;index1 < size;index1++) {
	        if (linkRef[index1] != null) {
	            if (linkRef[index1].support < minSupportRows) 
		    		linkRef[index1] = null;
	            }
		}
	    }
	else {
	    for (int index1=1;index1 < size;index1++) {
	        if (linkRef[index1] != null) {		
			    if (linkRef[index1].childRef != null) 
				pruneLevelN(linkRef[index1].childRef,level-1);
		    }
		}
	    }	
	}
	
    protected void generateLevel2() {
	
	nextLevelExists=false;
	for (int index=2;index<startTtreeRef.length;index++) {
	    if (startTtreeRef[index] != null) generateNextLevel(startTtreeRef,
	    				index,realloc2(null,(short) index));		
	    }
	}
	
    protected void generateLevelN(TtreeNode[] linkRef, int level, 
    					int requiredLevel, short[] itemSet) {
	int index1;
	int localSize = linkRef.length;
	if (level == requiredLevel) {
	    for (index1=2;index1<localSize;index1++) {
	    	if (linkRef[index1] != null) generateNextLevel(linkRef,index1,
					realloc2(itemSet,(short) index1));		
	        }
	    }
	else {
	    for (index1=2;index1<localSize;index1++) {
	        if (linkRef[index1] != null) {
		    generateLevelN(linkRef[index1].childRef,level+1,
		    		requiredLevel,realloc2(itemSet,(short) index1));
		    }	
		}
	    }
	}

    protected void generateNextLevel(TtreeNode[] parentRef, int endIndex, 
    			short[] itemSet) {
	parentRef[endIndex].childRef = new TtreeNode[endIndex];	// New level
        short[] newItemSet;	
	TtreeNode currentNode = parentRef[endIndex];
	for (int index=1;index<endIndex;index++) {	
	    if (parentRef[index] != null) {	
	        newItemSet = realloc2(itemSet,(short) index);
		if (testCombinations(newItemSet)) {
		    currentNode.childRef[index] = new TtreeNode();
		    nextLevelExists=true;
		    }
	        else currentNode.childRef[index] = null;
	        }
	    }
	}  
    
    protected boolean testCombinations(short[] currentItemSet) {  
       if (currentItemSet.length < 3) return(true);
	short[] itemSet1 = new short[2];
	itemSet1[0] = currentItemSet[1];
	itemSet1[1] = currentItemSet[0];
	int size = currentItemSet.length-2;
	short[] itemSet2 = removeFirstNelements(currentItemSet,2);
	return(combinations(null,0,2,itemSet1,itemSet2));
	}
    private boolean combinations(short[] sofarSet, int startIndex,
    		    int endIndex, short[] itemSet1, short[] itemSet2) {
	
	if (endIndex > itemSet2.length) {
	    short[] testSet = append(sofarSet,itemSet1);
	    return(findItemSetInTtree(testSet));
	    }
	else {
	    short[] tempSet;
	    for (int index=startIndex;index<endIndex;index++) {
	        tempSet = realloc2(sofarSet,itemSet2[index]);
	        if (!combinations(tempSet,index+1,endIndex+1,itemSet1,
				itemSet2)) return(false);
	        }
	    }						
      return(true);
	}    
    	
    
    private boolean findItemSetInTtree(short[] itemSet) {
		if (startTtreeRef[itemSet[0]] != null) {
    	    int lastIndex = itemSet.length-1;
	    if (lastIndex == 0) return(true);
	    else return(findItemSetInTtree2(itemSet,1,lastIndex,
			startTtreeRef[itemSet[0]].childRef));
	    }	
    	else return(false);
	}
    
    private boolean findItemSetInTtree2(short[] itemSet, int index, 
    			int lastIndex, TtreeNode[] linkRef) {  

  	if (linkRef[itemSet[index]] != null) {
	    if (index == lastIndex) return(true);
	    else return(findItemSetInTtree2(itemSet,index+1,lastIndex,
	    		linkRef[itemSet[index]].childRef));
	    }	
	else return(false);    
    	}
	

    private void addSupport() {
        try{
           while (true) {
               String stNum1 = JOptionPane.showInputDialog("Input minimum " +
	       		" support value between " + MIN_SUPPORT + " and " +
							MAX_SUPPORT);
	       if (stNum1.indexOf('.') > 0) 
	       			support = Double.parseDouble(stNum1);						
               else support = Integer.parseInt(stNum1);
               if (support>=MIN_SUPPORT && support<=MAX_SUPPORT) break;
               JOptionPane.showMessageDialog(null,
	       		"MINIMUM SUPPORT VALUE INPUT ERROR:\n" +
	       		"input = " + support +
	       		"\nminimum support input must be a floating point\n" +
			         "number between " + MIN_SUPPORT + " and " + 
							MAX_SUPPORT);
				 
	       }
	    textArea.append("Minimum support = " + support + "%\n");
	    hasSupportFlag=true;
	    }
        catch(NumberFormatException e) {
            hasSupportFlag=false;
	    runButton.setEnabled(false);
	    }
	if (haveDataFlag && hasSupportFlag) runButton.setEnabled(true);
	}
    private void getFileName() {
	JFileChooser fileChooser = new JFileChooser();
	fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);	
	int result = fileChooser.showOpenDialog(this);
	if (result == JFileChooser.CANCEL_OPTION) return;
	fileName = fileChooser.getSelectedFile();
	if (checkFileName()) {
	    readFile();
	    }
	
	if (inputFormatOkFlag) {
	    if (checkOrdering()) {
	        if (haveDataFlag && hasSupportFlag) runButton.setEnabled(true);
	        outputDataArray();
		textArea.append("Number of records = " + numRows + "\n");
		countNumCols();
		textArea.append("Number of columns = " + numCols + "\n");
		}
	    else {
	        haveDataFlag = false;
	        inputFormatOkFlag = true;
	        textArea.append("Error reading file: " + fileName + "\n\n");		
	        runButton.setEnabled(false);
	        }
	    }
	}	    
    		 
    private boolean checkFileName() {
	if (fileName.exists()) {
	    if (fileName.canRead()) {
		if (fileName.isFile()) return(true);
		else JOptionPane.showMessageDialog(null,
				"FILE ERROR: File is a directory");
		}
	    else JOptionPane.showMessageDialog(null,
	    			"FILE ERROR: Access denied");   
	    } 
	else JOptionPane.showMessageDialog(null,
				"FILE ERROR: No such file!"); 
	
	return(false);
	}

   private void readFile() {
	
	try {
	    inputFormatOkFlag=true;
	    getNumberOfLines();
	    if (inputFormatOkFlag) {
	        dataArray = new short[numRows][];	
	        inputDataSet();	
	        haveDataFlag = true;
		}
	    else {
	        haveDataFlag = false;
		textArea.append("Error reading file: " + fileName + "\n\n");		
		runButton.setEnabled(false);
		}	
	    }
	catch(IOException ioException) {
	    JOptionPane.showMessageDialog(this,"Error reading File", 
			 "Error 5: ",JOptionPane.ERROR_MESSAGE); 
	    closeFile();
	    System.exit(1);
	    }	    
	}
	
private void getNumberOfLines() throws IOException {
        int counter = 0;
	openFile();
	String line = fileInput.readLine();	
	while (line != null) {
	    checkLine(counter+1,line);
	    StringTokenizer dataLine = new StringTokenizer(line);
            int numberOfTokens = dataLine.countTokens();
	    if (numberOfTokens == 0) break;
	    counter++;	 
            line = fileInput.readLine();
	    }
		numRows = counter;
        closeFile();
	}

    private void checkLine(int counter, String str) {
    
        for (int index=0;index <str.length();index++) {
            if (!Character.isDigit(str.charAt(index)) &&
	    			!Character.isWhitespace(str.charAt(index))) {
                JOptionPane.showMessageDialog(null,"FILE INPUT ERROR:\ncharcater " +
		       "on line " + counter + " is not a digit or white space");
	        inputFormatOkFlag = false;
		break;
		}
	    }
	}
    

    public void inputDataSet() throws IOException {  
        int rowIndex=0;
	textArea.append("Reading input file\n" + fileName + "\n");
	
	openFile();
	String line = fileInput.readLine();	
	while (line != null) {
	    StringTokenizer dataLine = new StringTokenizer(line);
            int numberOfTokens = dataLine.countTokens();
	    if (numberOfTokens == 0) break;
	    short[] code = binConversion(dataLine,numberOfTokens);
	    if (code != null) {
		int codeLength = code.length;
		dataArray[rowIndex] = new short[codeLength];
		for (int colIndex=0;colIndex<codeLength;colIndex++)
				dataArray[rowIndex][colIndex] = code[colIndex];
		}
	    else dataArray[rowIndex]= null;
	    rowIndex++;
            line = fileInput.readLine();
	    }
	closeFile();
	}
	
    private short[] binConversion(StringTokenizer dataLine, 
    				int numberOfTokens) {
        short number;
	short[] newItemSet = null;
	for (int tokenCounter=0;tokenCounter < numberOfTokens;tokenCounter++) {
            number = new Short(dataLine.nextToken()).shortValue();
	    newItemSet = realloc1(newItemSet,number);
	    }
return(newItemSet);
	}  


    private boolean checkOrdering() {
        boolean result = true; 
	for(int index=0;index<dataArray.length;index++) {
	    if (!checkLineOrdering(index+1,dataArray[index])) result=false;
	    }
	return(result);
	}
    
    private boolean checkLineOrdering(int lineNum, short[] itemSet) {
        for (int index=0;index<itemSet.length-1;index++) {
	    if (itemSet[index] >= itemSet[index+1]) {
	        JOptionPane.showMessageDialog(null,"FILE FORMAT ERROR:\n" +
	       		"Attribute data in line " + lineNum + 
			" not in numeric order");
		return(false);
		}
	    }    
	return(true);
	}
	
    private void countNumCols() {
        int maxAttribute=0;
  for(int index=0;index<dataArray.length;index++) {
	    int lastIndex = dataArray[index].length-1;
	    if (dataArray[index][lastIndex] > maxAttribute)
	    		maxAttribute = dataArray[index][lastIndex];	    
	    }
	
	numCols = maxAttribute;
	}
     
    public void outputDataArray() {
        for(int index=0;index<dataArray.length;index++) {
	    outputItemSet(dataArray[index]);
	    textArea.append("\n");
	    }
	}

    protected void outputItemSet(short[] itemSet) {
	String itemSetStr = " {";
	int counter = 0;
	for (int index=0;index<itemSet.length;index++) {
	    if (counter != 0) itemSetStr = itemSetStr + ",";
	    counter++;
	    itemSetStr = itemSetStr + itemSet[index];
	    }
	textArea.append(itemSetStr + "}");
	}

    public void outputFrequentSets() {
	int number = 1;
	
	textArea.append("FREQUENT (LARGE) ITEM SETS (with support counts)\n" +
			"------------------------------------------------\n");
	short[] itemSetSofar = new short[1];
	for (int index=1; index <= numCols; index++) {
	    if (startTtreeRef[index] !=null) {
	        if (startTtreeRef[index].support >= minSupportRows) {
	            textArea.append("[" + number + "]  {" + index + "} = " + 
		    			startTtreeRef[index].support + "\n");
	            itemSetSofar[0] = (short) index;
		    number = outputFrequentSets(number+1,itemSetSofar,
		    			index,startTtreeRef[index].childRef);
		    }
		}
	    }    
	textArea.append("\n");
	}
    private int outputFrequentSets(int number, short[] itemSetSofar, int size,
    							TtreeNode[] linkRef) {
	
	if (linkRef == null) return(number);
	for (int index=1; index < size; index++) {
	    if (linkRef[index] != null) {
	        if (linkRef[index].support >= minSupportRows) {
		    short[] newItemSetSofar = realloc2(itemSetSofar,
		    				(short) index);
	            textArea.append("[" + number + "] ");
		    outputItemSet(newItemSetSofar); 
		    textArea.append(" = " + linkRef[index].support + "\n");	            
	            number = outputFrequentSets(number + 1,newItemSetSofar,
		    			index,linkRef[index].childRef); 
	            }
		}
	    }    

	return(number);
	}    

private void openFile() {
	try {
	    FileReader file = new FileReader(fileName);
	    fileInput = new BufferedReader(file);
	    }
	catch(IOException ioException) {
	    JOptionPane.showMessageDialog(this,"Error Opening File", 
			 "Error 4: ",JOptionPane.ERROR_MESSAGE);
	    }
	}
	   
    
    private void closeFile() {
        if (fileInput != null) {
	    try {
	    	fileInput.close();
		}
	    catch (IOException ioException) {
	        JOptionPane.showMessageDialog(this,"Error Opening File", 
			 "Error 4: ",JOptionPane.ERROR_MESSAGE);
	        }
	    }
	}
	  
protected short[] realloc1(short[] oldItemSet, short newElement) {

	if (oldItemSet == null) {
	    short[] newItemSet = {newElement};
	    return(newItemSet);
	    }
	int oldItemSetLength = oldItemSet.length;
	short[] newItemSet = new short[oldItemSetLength+1];
	int index;
	for (index=0;index < oldItemSetLength;index++)
		newItemSet[index] = oldItemSet[index];
	newItemSet[index] = newElement;
return(newItemSet);
	}	
    protected short[] append(short[] itemSet1, short[] itemSet2) {

	if (itemSet1 == null) return(copyItemSet(itemSet2));
	else if (itemSet2 == null) return(copyItemSet(itemSet1));
	short[] newItemSet = new short[itemSet1.length+itemSet2.length];
	int index1;
	for(index1=0;index1<itemSet1.length;index1++) {
	    newItemSet[index1]=itemSet1[index1];
	    }
	for(int index2=0;index2<itemSet2.length;index2++) {
	    newItemSet[index1+index2]=itemSet2[index2];
	    }
	return(newItemSet);	
        }
	
    protected short[] realloc2(short[] oldItemSet, short newElement) {
        
	if (oldItemSet == null) {
	    short[] newItemSet = {newElement};
	    return(newItemSet);
	    }
	int oldItemSetLength = oldItemSet.length;
	short[] newItemSet = new short[oldItemSetLength+1];
	newItemSet[0] = newElement;
	for (int index=0;index < oldItemSetLength;index++)
		newItemSet[index+1] = oldItemSet[index];
	return(newItemSet);
	}
    
    protected short[] removeFirstNelements(short[] oldItemSet, int n) {
        if (oldItemSet.length == n) return(null);
    	else {
	    short[] newItemSet = new short[oldItemSet.length-n];
	    for (int index=0;index<newItemSet.length;index++) {
	        newItemSet[index] = oldItemSet[index+n];
	        }
	    return(newItemSet);
	    }
	}

     
    protected short[] copyItemSet(short[] itemSet) {
	
	if (itemSet == null) return(null);
	short[] newItemSet = new short[itemSet.length];
	for(int index=0;index<itemSet.length;index++) {
	    newItemSet[index] = itemSet[index];
	    }
        return(newItemSet);
	}
	
   
    public static void main(String[] args) throws IOException {
	dicProcess process1;   
	AprioriTgui newFile = new AprioriTgui("Weighted Frequent Itemset Mining Algorithm for Intelligent Decision in Smart Systems");
    	newFile.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	newFile.setSize(500,500);
      newFile.setVisible(true);
    		 
    		         }
    }
