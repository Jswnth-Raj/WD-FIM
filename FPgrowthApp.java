
import java.io.*;
import java.util.*;


public class FPgrowthApp extends AprioriTgui
{
   File f1;
   double minsup; 
   public void start() throws IOException 
   {
     String fname=f1.getName();   
	String args1[]=new String[10];
	args1[0]=fname;
	args1[1]=Double.toString(minsup);
	
	FPtree newFPtree = new FPtree(args1);
	

	newFPtree.inputDataSet();
	
		Date d = new Date();
    		long s1,s2;
    		
		d = new Date();
		s1 = d.getTime();
        
	
		
	newFPtree.idInputDataOrdering();
	newFPtree.recastInputDataAndPruneUnsupportedAtts(); 
	newFPtree.setNumOneItemSets();
newFPtree.outputDataArray();

      
	
	double time1 = (double) System.currentTimeMillis(); 
	newFPtree.createFPtree();

	newFPtree.outputFPtreeStorage();			
newFPtree.outputFPtree();
newFPtree.outputItemPrefixSubtree();


	newFPtree.startMining();

newFPtree.outputStorage(); 
newFPtree.outputNumFreqSets(); 
newFPtree.outputTtree();

	d = new Date();
	s2 = d.getTime();
	
	System.out.println(s2);
	textArea.append("\n\nExecution time is: " + (s2 - s1) + " milliseconds.\n\n");
	
	}
	
	public void sendData(File g1,double g2)throws IOException
{
		f1=g1;
		minsup=g2;
		start();
			
}
	
    }
    
                                                                                                                                                             