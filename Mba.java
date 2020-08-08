import java.io.*;
import java.util.*;

public class Mba extends AprioriTgui
{	

	 static int lff;
	 //static int numCols=119,numRows=8124;
	 //static	int minsup=7311;
	 public static File f1;
	 static  int numCols;
	  static int numRows;
	 static double minsup;
	 
	 public void sendData(File g1,double g2,int n1,int n2) throws Exception
{
		f1=g1;
		minsup=n2*g2/100.0;
		numCols=n1;
		numRows=n2;
		
}

	  public  static void start( ) throws IOException
	  {
	  		String s1;int s2;
	  		//String s="c:\\jdk1.5\\bin\\mushroom.dat";
	  		//File f4=new File(s);
	  		
		   /* File f=convert(f1);
		  	String fname=f.getName();*/
		  	
		  	
		  	Mba Mb=new Mba();
		  	String fname="c:\\tr.txt";
		  
 		 	int i=1;
 		 	
 		 
 		 
 		  	BufferedReader fileInput;
      	 	FileReader file = new FileReader(fname);
	        fileInput = new BufferedReader(file);
	        
		    Integer count[]=new Integer[numCols];
		    Integer freq[]=new Integer[numCols];
		 	Integer prune[]=new Integer[numCols];
		 	Integer B[][]=new Integer[numRows][numCols];
		 	Integer count1[]=new Integer[numCols];
		  
		  
		  //Generating 1-itemfrequent sets
		  String line = fileInput.readLine();
		  
		  
		  for(i=0;i<numCols;i++)
		    {
		    	 count[i]=0;
		    	 count1[i]=0;
		  	}
		  textArea.append("\nSupport="+minsup);	
		  textArea.append("\n"+fname);
		  
		  	Date d = new Date();
    		long r1,r2;
    		
			d = new Date();
			r1 = d.getTime();
			System.out.println(r1);
			
		  int r=0;
		  
		  
		  while (line != null&& r!=numRows) 
  	  		{
  	  			
  	  			i=0;
  	  			StringTokenizer dataLine = new StringTokenizer(line);
  	  			
  	  	    	while(dataLine.hasMoreTokens())
		  			{
		  			s1=dataLine.nextToken();
		    	    s2=Integer.parseInt(s1);	
		  			if(i<numCols)
		  			{	
		  		      if (s2==1)  count[i]++;
		  	        }
		    	    i++;  
		    	    }
		    	  line = fileInput.readLine();
		    	  r++;
		    }
		    
 			
 			
 			
			int j=0,l=0,k=0,m; 			
 			
 			textArea.append("\n\n  1-itemsets:  \n ");
 						
 			for (i=0;i<numCols;i++)
 			{
 				 if (count[i]>=minsup)
 				 {
 					freq[l]=i;
 					l++;
 					textArea.append("\n"+ (i+1) +":" +count[i]);
 					 				 		
 				 }
 				 
 			}
 		
 			int c=0;
 			
 		///make matrix of the transactions containing of more than two frequent itemsets	
 		
 		
 		 	BufferedReader fileInput1;
      	 	FileReader file1 = new FileReader(fname);
	        fileInput1 = new BufferedReader(file1);
	        for (i=0;i<numRows;i++)
 			for (j=0;j<numCols;j++)
 				B[i][j]=0;
	        
 		 	
		  	line = fileInput1.readLine();
		  	i=0;m=0;
		  	while (line != null) 
  	  		{
 	  			StringTokenizer dataLine = new StringTokenizer(line);
  	  			i=0;
  	  	    	while(dataLine.hasMoreTokens())
		  			{
		  				s1=dataLine.nextToken();
		  				s2=Integer.parseInt(s1);
		    	    	if(s2==1)	
		    	    	for (k=0;k<l;k++)
		    	    	{
		    	    		 if(i==freq[k])
		    	    		  c++;
		    	    	}
		    	    	i++;
		    	    }
		    	    	
		    	    if(c>=2)
		    	   {    
		    	   		j=0;
		    	   		dataLine = new StringTokenizer(line);
		    	   		while(dataLine.hasMoreTokens())
		  				{
		  				s1=dataLine.nextToken();
		    	    	s2=Integer.parseInt(s1);
		    	    	B[m][j]=s2;
		    	    	j++;
		    	   		}
		    	    m++;  
		    	    }
		    	    
			    c=0;	    
			    line = fileInput1.readLine();
		  	}
		  	
		System.out.println("hai");
		    	
 			for (i=0;i<numCols;i++)
 			{
 		
 			for (j=0;j<numRows;j++)
 			{
 				if(B[j][i]==1)
 					count1[i]++;
 			}
		 	    	
		}   
		
	
		textArea.append("\n\nafter removing false frequent item sets");
		
			lff=0;
			k=0;
			for (i=0;i<numCols;i++)
 			{
 				 if (count1[i]>=minsup)
 				 {
 					freq[lff]=i;
 					lff++;
 					System.out.println(count1[i]+" ");
 					textArea.append("\n" + (i+1) +":" +count1[i])	 	;
 						
 				 }
 			}
 		textArea.append("\n\n 2-itemsets");
 		Mb.generatelevelN(freq,B,2);
 		
 		d = new Date();
	r2 = d.getTime();
	
	System.out.println(r2);
	textArea.append("\n\nExecution time is: " + (r2 - r1) + " milliseconds.\n\n");
			    	
	}
	
	
	void generatelevelN(Integer[] f,Integer[][] b1,int level)
	{
		
	int count[]=new int[numCols];
		
		for(int i=0;i<numCols;i++)
			count[i]=0;
		
		int m=0;
		int n;
		int x,y;
		int k=0;
		int l1=1;
	
		if(level==2)
		{
		
		while(m!=lff)
		{	x=f[m];
			n=m+1;
			while(n!=lff)	
			{
				y=f[n];
				for(int i=0;i<numRows;i++)
				if(b1[i][x]==1&&b1[i][y]==1)
				  {
				  	 count[k]++;
				  }
	 		if(count[k]>minsup)
			
			 textArea.append("\n ["+ (x+1)+ ","+(y+1) +"]:"+ count[k]);	 
			n++;
			k++;
		}
		
		m++;
		}
		generatelevelN(f,b1,level+1);
		}
		else
		{
			superset(f,b1,level+1);
		}
		
		
	}
	
	void superset(Integer[] f,Integer[][] b1,int level)
	{
	/*	textArea.append("Hai ia m in method");
		for(int i=0;i<lff;i++)
		textArea.append(f[i]);
		*/
		int count[]=new int[numCols];
		
		for(int i=0;i<numCols;i++)
			count[i]=0;
		int q=level;
		int m=0;
		int n,n1;
		int x,y,z;
		int k=0;
		int l1=1;
		textArea.append("\n3-itemsets");
		while(m!=lff)
		{	x=f[m];
			n=m+1;
			n1=n+1;
			while(n!=lff)	
			{
				y=f[n];
				while(n1!=lff)
				{
				
				z=f[n1];
				for(int i=0;i<numRows;i++)
				if(b1[i][x]==1&&b1[i][y]==1&&b1[i][z]==1)
				  {
				  	 count[k]++;
				  	 
				  }
	 		if(count[k]>minsup)
			
			textArea.append("\n"+"["+ (x+1)+ ","+(y+1) +"," +(z+1)+" ]:"+ count[k]);	 
			k++;
			n1++;
			}
			
			n++;
			n1=n+1;
		}
		
		m++;
		}
		
	superset1(f,b1,q+1);	
			
	}
	
 		
 	void superset1(Integer[] f,Integer[][] b1,int level)
	{
	
		int count[]=new int[numCols];
		
		for(int i=0;i<numCols;i++)
			count[i]=0;
	
		int m=0;
		int n,n1,n2;
		int x,y,z,p;
		int k=0;
		int l1=1;
	
		while(m!=lff)
		{	x=f[m];
			n=m+1;
			n1=n+1;
			
			while(n!=lff)	
			{
				y=f[n];
				while(n1!=lff)
				{
				n2=n1+1;
				z=f[n1];
				while(n2!=lff)
				{
				p=f[n2];
				for(int i=0;i<numRows;i++)
				if(b1[i][x]==1&&b1[i][y]==1&&b1[i][z]==1&&b1[i][p]==1)
				  {
				  	 count[k]++;
				  	 
				  }
	 		if(count[k]>minsup)
			{
				textArea.append("\n4-itemsets");
			textArea.append("\n"+"["+ (x+1) +","+(y+1) +"," +(z+1)+ "," +(p+1) +  "]:"+ count[k]);
			}	 
			k++;
			n2++;
			}
			n1++;
			}
			
			n++;
			n1=n+1;
		}
		
		m++;
		}
		
		
			
	}	
 			
 			
	public static File convert(File sname)
	{

		int s=0;
        int Max=119;
		int p=0;
	    File outfile=new File("c:\\tr2.txt");
	
    	try
     	{
      		String fileName=sname.getName();
    //  		outfile
      	    BufferedReader fileInput;
      		FileReader file = new FileReader(fileName);
	  
	        fileInput = new BufferedReader(file);
	  
      		PrintWriter writer   = new PrintWriter(new BufferedWriter(new FileWriter(outfile,true)));

 		    int array[]=new int[numCols+1]	;
 
      		String line = fileInput.readLine();
 
	  		char c=' '; 
	   		int n,s2;
	    	String s1;
      		while (line != null) 
  	  		{
		
			    StringTokenizer dataLine = new StringTokenizer(line);
		    	n=dataLine.countTokens();
		    
		    	while(dataLine.hasMoreTokens())
		    
				{   
			    	p=1;
		        	for(int i=1;i<=n;i++)	   
					{
			    		s1=dataLine.nextToken();
		    	  		s2=Integer.parseInt(s1);
		    	  		s=s2;
		          		if(s2==i)
				  		{ 
				    		array[p]=1;
							writer.print(array[p]);
							writer.print(c);
							p++;
						}
				  		else
				   		{ 
					 
							while(p!=s2)
							{ 
					  			array[p]=0;
				                writer.print(array[p]);
					  			writer.print(c);
					  			p++;
					 		}
					  		array[p]=1;
					  		writer.print(array[p]);
					  		writer.print(c);
					  		p++;
					   }    
		      		}
        		
      		   }
      		
      		int j=Max-s;
      		for(int i=1;i<=j;i++)
      		{
      		          array[p]=0;
					  writer.print(array[p]);
					  writer.print(c);
					  p++;	
      		}
      		  
      		
            writer.println();
       		line = fileInput.readLine();
 		}
		
		writer.close();
		
				}
	catch(Exception e)
	{
		System.out.println(e);
	}
	return outfile;
}
    
} 			