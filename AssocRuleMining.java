
import java.io.*;
import java.util.*;


import javax.swing.*;



public class AssocRuleMining extends AprioriTgui {

    

    protected class RuleNode {
    	
	protected short[] antecedent;

	protected short[] consequent;

	double confidenceForRule=0.0;
	
	RuleNode next = null;
	
	
	protected RuleNode(short[] ante, short[]cons, double confValue) {
	    antecedent        = ante;
	    consequent        = cons;
	    confidenceForRule = confValue;
	    }
	}
	
    
    protected RuleNode startRulelist = null;	
    
    protected short[][] dataArray = null;
    
    
    protected int[][] conversionArray   = null;
    
    protected short[] reconversionArray = null;
	
    
    
    
    private static final double MIN_SUPPORT = 0.0;
    
    private static final double MAX_SUPPORT = 100.0;
    
    private static final double MIN_CONFIDENCE = 0.0;
    
    private static final double MAX_CONFIDENCE = 100.0;	
    
    protected String  fileName   = null;
    
    protected int     numCols    = 0;
    
    protected int     numRows    = 0;
    
    protected double  support    = 20.0;
    
    protected double  minSupport = 0;
    
    protected double  confidence = 80.0;
    
    protected int numOneItemSets = 0;

    

   
    protected boolean errorFlag  = true;
   
    protected boolean inputFormatOkFlag = true;
   
    private boolean haveDataFlag = false;
   
    protected boolean isOrderedFlag = false;
   
    protected boolean isPrunedFlag = false;

   
    protected BufferedReader fileInput;
   
    protected File filePath = null;

   

    
    public AssocRuleMining(String[] args) 
    {
    	fileName=args[0];
    	support = Double.parseDouble(args[1]);      
       

	

	if (errorFlag) CheckInputArguments();
	else outputMenu();
        }

    protected void CheckInputArguments() {

checkSupportAndConfidence();


	checkFileName();


	if (errorFlag) outputSettings();
	else outputMenu();
	}

   protected void checkSupportAndConfidence() {


	if ((support < MIN_SUPPORT) || (support > MAX_SUPPORT)) {
		System.out.println("INPUT ERROR: Support must be specified " +
		 			"as a percentage (" + MIN_SUPPORT +
		 				" - " + MAX_SUPPORT + ")");
	    errorFlag = false;
	    }

	if ((confidence < MIN_CONFIDENCE) || (confidence > MAX_CONFIDENCE)) {
		System.out.println("INPUT ERROR: Confidence must be " +
		               "specified as a percentage (" + MIN_CONFIDENCE +
					" - " + MAX_CONFIDENCE + ")");
	    errorFlag = false;
	    }
	}

   
    protected void checkFileName() {
	if (fileName == null) {
	    System.out.println("INPUT ERROR: Must specify file name (-F)");
            errorFlag = false;
	    }
	}

  
    public void inputDataSet() {
      
	readFile();

	
	if (inputFormatOkFlag) {
	    if (checkOrdering()) {
                System.out.println("Number of records = " + numRows);
		countNumCols();
		System.out.println("Number of columns = " + numCols);
		minSupport = (numRows * support)/100.0;
        	System.out.println("Min support       = " +
				twoDecPlaces(minSupport) + " (records)");
		}
	    else {
	        System.out.println("Error reading file: " + fileName + "\n");
	        closeFile();
	        System.exit(1);
		}
	    }
	}

   

    protected void readFile() {
        try {
	   
	    inputFormatOkFlag=true;
	    numRows = getNumberOfLines(fileName);
	    if (inputFormatOkFlag) {
	        dataArray = new short[numRows][];
	       
		System.out.println("Reading input file: " + fileName);
	        readInputDataSet();
		}
	    else System.out.println("Error reading file: " + fileName + "\n");
	    }
	catch(IOException ioException) {
	    System.out.println("Error reading File");
	    closeFile();
	    System.exit(1);
	    }
	}

   protected int getNumberOfLines(String nameOfFile) throws IOException {
        int counter = 0;


	if (filePath==null) openFileName(nameOfFile);
	else openFilePath();


	String line = fileInput.readLine();
	while (line != null) {
	    checkLine(counter+1,line);
	    StringTokenizer dataLine = new StringTokenizer(line);
            int numberOfTokens = dataLine.countTokens();
	    if (numberOfTokens == 0) break;
	    counter++;
            line = fileInput.readLine();
	    }

	
        closeFile();
	return(counter);
	}

    
    protected void checkLine(int counter, String str) {

        for (int index=0;index <str.length();index++) {
            if (!Character.isDigit(str.charAt(index)) &&
	    			!Character.isWhitespace(str.charAt(index))) {
		JOptionPane.showMessageDialog(null,"FILE INPUT ERROR:\n" +
					"charcater on line " + counter +
					" is not a digit or white space");
		inputFormatOkFlag = false;
		haveDataFlag = false;
		break;
		}
	    }
	}

     public void readInputDataSet() throws IOException {
        readInputDataSet(fileName);
	}

    
    protected void readInputDataSet(String fName) throws IOException {
	int rowIndex=0;


	if (filePath==null) openFileName(fName);
	else openFilePath();

	String line = fileInput.readLine();


	while (line != null) {
	    
	    if (!processInputLine(line,rowIndex)) break;
	   
	    rowIndex++;
	   
            line = fileInput.readLine();
	    }

	
	closeFile();
	}


    protected void readInputDataSetSeg(String fName, int startRowIndex,
    					int endRowIndex) throws IOException {
	int rowIndex=startRowIndex;


	if (filePath==null) openFileName(fName);
	else openFilePath();


	String line = fileInput.readLine();
	for (int index=startRowIndex;index<endRowIndex;index++) {

	    processInputLine(line,index);

            line = fileInput.readLine();
	    }


	closeFile();
	}


    private boolean processInputLine(String line, int rowIndex) {

	if (line==null) return(false);


	StringTokenizer dataLine = new StringTokenizer(line);
        int numberOfTokens = dataLine.countTokens();


	if (numberOfTokens == 0) return(false);


	short[] code = binConversion(dataLine,numberOfTokens);


	int codeLength = code.length;
	dataArray[rowIndex] = new short[codeLength];

	for (int colIndex=0;colIndex<codeLength;colIndex++)
				dataArray[rowIndex][colIndex] = code[colIndex];


	return(true);
	}


    protected boolean checkOrdering() {
        boolean result = true;


	for(int index=0;index<dataArray.length;index++) {
	    if (!checkLineOrdering(index+1,dataArray[index])) {
		haveDataFlag = false;
		result=false;
		}
	    }


	return(result);
	}

  
    protected boolean checkLineOrdering(int lineNum, short[] itemSet) {
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

  
    protected void countNumCols() {
        int maxAttribute=0;

        for(int index=0;index<dataArray.length;index++) {
	    int lastIndex = dataArray[index].length-1;
	    if (dataArray[index][lastIndex] > maxAttribute)
	    		maxAttribute = dataArray[index][lastIndex];
	    }

	numCols        = maxAttribute;
	numOneItemSets = numCols; 	
	}

   
    protected void openFileName(String nameOfFile) {
	try {
	  
	    FileReader file = new FileReader(nameOfFile);
	    fileInput = new BufferedReader(file);
	    }
	catch(IOException ioException) {
	    JOptionPane.showMessageDialog(this,"Error Opening File",
			 "Error: ",JOptionPane.ERROR_MESSAGE);
	    System.exit(1);
	    }
	}

    
    protected void openFilePath() {
	try {
	   
	    FileReader file = new FileReader(filePath);
	    fileInput = new BufferedReader(file);
	    }
	catch(IOException ioException) {
	    JOptionPane.showMessageDialog(this,"Error Opening File",
			 "Error: ",JOptionPane.ERROR_MESSAGE);
	    System.exit(1);
	    }
	}

    protected void closeFile() {
        if (fileInput != null) {
	    try {
	    	fileInput.close();
		}
	    catch (IOException ioException) {
		JOptionPane.showMessageDialog(this,"Error Closeing File",
			 "Error: ",JOptionPane.ERROR_MESSAGE);
	        System.exit(1);
		}
	    }
	}

   
    protected short[] binConversion(StringTokenizer dataLine,
    				int numberOfTokens) {
        short number;
	short[] newItemSet = null;

	
	for (int tokenCounter=0;tokenCounter < numberOfTokens;tokenCounter++) {
            number = new Short(dataLine.nextToken()).shortValue();
	    newItemSet = realloc1(newItemSet,number);
	    }



	return(newItemSet);
	}

  
    
    public void idInputDataOrdering() {
	
	  
        int[][] countArray = countSingles();
        

	orderCountArray(countArray);
       
     
	defConvertArrays(countArray);
	

	isOrderedFlag = true;
	}
	   
   
    protected int[][] countSingles() {
        

	
	int[][] countArray = new int[numCols+1][2];
	for (int index=0;index<countArray.length;index++) {
	    countArray[index][0] = index;
	    countArray[index][1] = 0;
	    }
	    	    

	for(int rowIndex=0;rowIndex<dataArray.length;rowIndex++) {
	     if (dataArray[rowIndex] != null) {
		for (int colIndex=0;colIndex<dataArray[rowIndex].length;
					colIndex++) 
		    	countArray[dataArray[rowIndex][colIndex]][1]++;
		}
	    }
	

	
	return(countArray);
	}
	
     
    private void orderCountArray(int[][] countArray) {
        int attribute, quantity;	
        boolean isOrdered;
        int index; 
               
        do {
	    isOrdered = true;
            index     = 1; 
            while (index < (countArray.length-1)) {
                if (countArray[index][1] >= countArray[index+1][1]) index++;
	        else {
	            isOrdered=false;
                   
		    attribute              = countArray[index][0];
		    quantity               = countArray[index][1];
	            countArray[index][0]   = countArray[index+1][0];
	            countArray[index][1]   = countArray[index+1][1];
                    countArray[index+1][0] = attribute;
	            countArray[index+1][1] = quantity;
	           
		    index++;  
	            }
	  	}     
	    } while (isOrdered==false);
    	}
    
    
    protected void orderFirstNofCountArray(int[][] countArray, int endIndex) {
        int attribute, quantity;
        boolean isOrdered;
        int index;

        do {
	    isOrdered = true;
            index     = 1;
            while (index < endIndex) {
                if (countArray[index][1] >= countArray[index+1][1]) index++;
	        else {
	            isOrdered=false;
                  
		    attribute              = countArray[index][0];
		    quantity               = countArray[index][1];
	            countArray[index][0]   = countArray[index+1][0];
	            countArray[index][1]   = countArray[index+1][1];
                    countArray[index+1][0] = attribute;
	            countArray[index+1][1] = quantity;
	            
		    index++;
	            }
	  	}
	    } while (isOrdered==false);
    	}

   
    protected void defConvertArrays(int[][] countArray) {


	conversionArray   = new int[numCols+1][2];
        reconversionArray = new short[numCols+1];



	for(int index=1;index<countArray.length;index++) {
            conversionArray[countArray[index][0]][0] = index;
            conversionArray[countArray[index][0]][1] = countArray[index][1];
	    reconversionArray[index] = (short) countArray[index][0];
	    }


	}


    public void recastInputData() {
        short[] itemSet;
	int attribute;



        for(int rowIndex=0;rowIndex<dataArray.length;rowIndex++) {
	    itemSet = new short[dataArray[rowIndex].length];

	    for(int colIndex=0;colIndex<dataArray[rowIndex].length;colIndex++) {
	        attribute = dataArray[rowIndex][colIndex];
		itemSet[colIndex] = (short) conversionArray[attribute][0];
		}
	   
	    sortItemSet(itemSet);
	    dataArray[rowIndex] = itemSet;
	    }
	}

    public void recastInputDataAndPruneUnsupportedAtts() {
        short[] itemSet;
	int attribute;



        for(int rowIndex=0;rowIndex<dataArray.length;rowIndex++) {
	
	    if (dataArray[rowIndex]!= null) {
	        itemSet = null;
	       	for(int colIndex=0;colIndex<dataArray[rowIndex].length;colIndex++) {
	            attribute = dataArray[rowIndex][colIndex];
		    if (conversionArray[attribute][1] >= minSupport) {
		        itemSet = reallocInsert(itemSet,
		    			(short) conversionArray[attribute][0]);
		        }
		    }
	        dataArray[rowIndex] = itemSet;
	 	}
	    }

	
	isPrunedFlag=true;
	
	numOneItemSets = getNumSupOneItemSets();
	}

    
    protected int getNumSupOneItemSets() {
        int counter = 0;


	for (int index=1;index < conversionArray.length;index++) {
	    if (conversionArray[index][1] >= minSupport) counter++;
	    }

	return(counter);
	}

 

    public void resizeInputData(double percentage) {

	numRows = (int) ((double) numRows*(percentage/100.0));
        System.out.println("Recast input data, new num rows = " + numRows);


	short[][] trainingSet = new short[numRows][];
	for (int index=0;index<numRows;index++)
				trainingSet[index] = dataArray[index];


	dataArray = trainingSet;


	minSupport = (numRows * support)/100.0;
	}

   
    protected short[] reconvertItemSet(short[] itemSet) {
 
	if (reconversionArray==null) return(itemSet); 
	

	if (itemSet==null) return(null);
	

	short[] newItemSet = new short[itemSet.length];
	

	for(int index=0;index<newItemSet.length;index++) {
	    newItemSet[index] = reconversionArray[itemSet[index]];
	    }
	

	return(newItemSet);    
        }

    
    protected short reconvertItem(short item) {
       
	if (reconversionArray==null) return(item); 
	
	return(reconversionArray[item]);
	}
	
   

    protected void insertRuleintoRulelist(short[] antecedent,
    				short[] consequent, double confidenceForRule) {


	RuleNode newNode = new RuleNode(antecedent,consequent,
							confidenceForRule);

	
	if (startRulelist == null) {
	    startRulelist = newNode;
	    return;
	    }


	if (confidenceForRule > startRulelist.confidenceForRule) {
	    newNode.next = startRulelist;
	    startRulelist  = newNode;
	    return;
	    }

	RuleNode markerNode = startRulelist;
	RuleNode linkRuleNode = startRulelist.next;
	while (linkRuleNode != null) {
	    if (confidenceForRule > linkRuleNode.confidenceForRule) {
	        markerNode.next = newNode;
		newNode.next    = linkRuleNode;
		return;
		}
	    markerNode = linkRuleNode;
	    linkRuleNode = linkRuleNode.next;
	    }


	markerNode.next = newNode;
	}

  

    protected short[] reallocInsert(short[] oldItemSet, short newElement) {


	if (oldItemSet == null) {
	    short[] newItemSet = {newElement};
	    return(newItemSet);
	    }


	int oldItemSetLength = oldItemSet.length;
	short[] newItemSet = new short[oldItemSetLength+1];


	int index1;
	for (index1=0;index1 < oldItemSetLength;index1++) {
	    if (newElement < oldItemSet[index1]) {
		newItemSet[index1] = newElement;
	
		for(int index2 = index1+1;index2<newItemSet.length;index2++)
				newItemSet[index2] = oldItemSet[index2-1];
		return(newItemSet);
		}
	    else newItemSet[index1] = oldItemSet[index1];
	    }


	newItemSet[newItemSet.length-1] = newElement;

	

	return(newItemSet);
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
	
   
    protected short[] removeElementN(short [] oldItemSet, int n) {
        if (oldItemSet.length <= n) return(oldItemSet);
	else {
	    short[] newItemSet = new short[oldItemSet.length-1];
	    for (int index=0;index<n;index++) newItemSet[index] = 
	    				oldItemSet[index];
	    for (int index=n+1;index<oldItemSet.length;index++) 
	        			newItemSet[index-1] = oldItemSet[index];
	    return(newItemSet);
	    }
	}
	
   
    protected short[] complement(short[] itemSet1, short[] itemSet2) {
        int lengthOfComp = itemSet2.length-itemSet1.length;
	

	if (lengthOfComp<1) return(null);
	
	
	short[] complement  = new short[lengthOfComp];
	int complementIndex = 0;
	for(int index=0;index<itemSet2.length;index++) {
	   
	    if (notMemberOf(itemSet2[index],itemSet1)) {
	    	complement[complementIndex] = itemSet2[index];
		complementIndex++;
		}	
	    }

	return(complement);
	}

   
    protected void sortItemSet(short[] itemSet) {
        short temp;
        boolean isOrdered;
        int index;

        do {
	    isOrdered = true;
            index     = 0;
            while (index < (itemSet.length-1)) {
                if (itemSet[index] <= itemSet[index+1]) index++;
	        else {
	            isOrdered=false;
                   
		    temp = itemSet[index];
	            itemSet[index] = itemSet[index+1];
                    itemSet[index+1] = temp;
	           
		    index++;
	            }
	  	}
	    } while (isOrdered==false);
    	}  

  
    
    protected boolean notMemberOf(short number, short[] itemSet) {
        

	for(int index=0;index<itemSet.length;index++) {
	    if (number < itemSet[index]) return(true);
	    if (number == itemSet[index]) return(false);
	    }
	

	return(true);
	}

   

    protected short[][] combinations(short[] inputSet) {
	if (inputSet == null) return(null);
	else {
	    short[][] outputSet = new short[getCombinations(inputSet)][];
	    combinations(inputSet,0,null,outputSet,0);
	    return(outputSet);
	    }
	}

    
    private int combinations(short[] inputSet, int inputIndex,
    		short[] sofar, short[][] outputSet, int outputIndex) {
    	short[] tempSet;
	int index=inputIndex;

    
	while(index < inputSet.length) {
            tempSet = realloc1(sofar,inputSet[index]);
            outputSet[outputIndex] = tempSet;
	    outputIndex = combinations(inputSet,index+1,
	    		copyItemSet(tempSet),outputSet,outputIndex+1);
    	    index++;
	    }

    
    	return(outputIndex);
    	}   

   
    private int getCombinations(short[] set) {
    	int counter=0, numComb;	

	numComb = (int) Math.pow(2.0,set.length)-1;
	    
    

        return(numComb);
        }

   
    
    protected short[] copyItemSet(short[] itemSet) {
	

	if (itemSet == null) return(null);
	

	short[] newItemSet = new short[itemSet.length];
	for(int index=0;index<itemSet.length;index++) {
	    newItemSet[index] = itemSet[index];
	    }
        
	
	return(newItemSet);
	}

   
    public void outputDataArray() {
  
      for(int index=0;index<dataArray.length;index++) {
	    outputItemSet(dataArray[index]);

	    }
	}

   
    protected void outputDataArray(short[][] dataSet) {
        if (dataSet==null) {
	    System.out.println("null");
	    return;
	    }


        for(int index=0;index<dataSet.length;index++) {
	    outputItemSet(dataSet[index]);
	    System.out.println();
	    }
	}

   
    protected void outputItemSet(short[] itemSet) {

	if (itemSet == null) System.out.print(" null ");
	
	else {
	    
	    short[] tempItemSet = reconvertItemSet(itemSet);
	   
            int counter = 0;
	    for (int index=0;index<tempItemSet.length;index++) {
	        if (counter == 0) {
	    	    counter++;
		  
		    }
	       
		}
	   
	    }
	}

  
    public void outputDataArraySize() {
    	int numRecords = 0;
	int numElements = 0;


	for (int index=0;index<dataArray.length;index++) {
	    if (dataArray[index] != null) {
	        numRecords++;
		numElements = numElements+dataArray[index].length;
	        }
	    }


	textArea.append("Number of records        = " + numRecords);
	textArea.append("Number of elements       = " + numElements);
	double density = (double) numElements/ (numCols*numRecords);
	textArea.append("Data set density   = " + twoDecPlaces(density) +
								"%");
	}

  
    public void outputConversionArrays() {

       
        System.out.println("Conversion Array = ");
	for(int index=1;index<conversionArray.length;index++) {
	    System.out.println("(" + index + ") " + conversionArray[index][0] +
	    			" = " + conversionArray[index][1]);
	    }

      
        System.out.println("Reonversion Array = ");
	for(int index=1;index<reconversionArray.length;index++) {
	    System.out.println("(" + index + ") " + reconversionArray[index]);
	    }
	}

  
    protected void outputMenu() {
        System.out.println();
	System.out.println("-C  = Confidence (default 80%)");
	System.out.println("-F  = File name");
	System.out.println("-N  = Number of classes (Optional)");
	System.out.println("-S  = Support (default 20%)");
	System.out.println();



	System.exit(1);
	}

 
    protected void outputSettings() {
        textArea.append("\nSETTINGS\n--------");
	textArea.append("\nFile name                = " + fileName);
textArea.append("\nSupport (default 20%)    = " + support);

	textArea.append("\n");
        }


    protected void outputSettings2() {
        textArea.append("\nSETTINGS\n--------");
        textArea.append("\nNumber of records        = " + numRows);
	textArea.append("\nNumber of columns        = " + numCols);
	textArea.append("\nSupport (default 20%)    = " + support);

        textArea.append("\nMin support              = " + minSupport +
					" (records)");
	textArea.append("\nNum one itemsets         = " + numOneItemSets);
	}

  

    public void outputSuppAndConf() {
	textArea.append("\nSupport = " + twoDecPlaces(support) + 
			", Confidence = " + twoDecPlaces(confidence));
        }
	
    
    public void outputRules() {
        outputRules(startRulelist);
	}
	
    
    public void outputRules(RuleNode ruleList) {
	
	if (ruleList==null) System.out.println("No rules generated!");
	
	
	int number = 1;
        RuleNode linkRuleNode = ruleList;
	while (linkRuleNode != null) {
	   textArea.append("(" + number + ") ");
	    outputRule(linkRuleNode);
            textArea.append(" " +
	    		twoDecPlaces(linkRuleNode.confidenceForRule) + "%");
	    number++;
	    linkRuleNode = linkRuleNode.next;
	    }
	}

   
    private void outputRule(RuleNode rule) {
        outputItemSet(rule.antecedent);
	textArea.append(" -> ");
        outputItemSet(rule.consequent);
	}

   
    public void outputRulesWithDefault() {
        int number = 1;
        RuleNode linkRuleNode = startRulelist;

	while (linkRuleNode != null) {
	  
	    textArea.append("(" + number + ") ");
	   
	    if (linkRuleNode.next==null) System.out.print("Default -> ");
	    else {
	        outputItemSet(linkRuleNode.antecedent);
	        textArea.append(" -> ");
		}
	   
            outputItemSet(linkRuleNode.consequent);
            textArea.append(" " +twoDecPlaces(linkRuleNode.confidenceForRule) + "%\n");
	   
	    number++;
	    linkRuleNode = linkRuleNode.next;
	    }
	}
			
  
    
    public double outputDuration(double time1, double time2) {
        double duration = (time2-time1)/1000;
textArea.append("\nGeneration time = " + twoDecPlaces(duration) + 
			" seconds (" + twoDecPlaces(duration/60) + " mins)");
        

	return(duration);
	}

   
    
    protected double twoDecPlaces(double number) {
    	int numInt = (int) ((number+0.005)*100.0);
	number = ((double) numInt)/100.0;
	return(number);
	}    
    }
