                          
import java.io.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;


/*public class dic
{

	public static void main(String[] args) throws IOException
	{

		dicProcess process1=new dicProcess();
		process1.dicProcesss();
		System.exit(0);

	}
}*/


class dic extends AprioriTgui 
{
	public static File outfile;
    public File f1;
    private final int DC = 1;      // four states of tree node
	private final int DS = 2;
	private final int SC = 3;
	private final int SS = 4;
        int N;                        // total item #
        int M;                       // total transaction #
        int stepm;                  // step increment
        int tid;                   // current line # of transaction
        int k;                    // current processing k-itemset
        int setnum;              // item # in current transaction
	double minsup;
	String dicm;
	hashtreenode root;
	String DSset, DCset, SCset, SSset;

	
    String transafile ;
    String configfile ;
       



	class hashtreenode
	{

                int state;            
                String itemset;      
                int counter;        
                int starting;     
                int startingk;   
 				boolean needcounting;  
                Hashtable ht;         

		public hashtreenode(int state, String itemset, int counter, int starting, int startingk)
		{
			this.state = state;
			if (itemset == null)
				this.itemset = new String();
			else
				this.itemset = new String(itemset);
			this.counter = counter;
			this.starting = starting;
			this.startingk = startingk;
			needcounting = true;
			ht = new Hashtable();
		}

	    public hashtreenode()
		{
			this.state = DC;
			this.itemset = new String();
			this.counter = 0;
			this.starting = 0;
			this.startingk = 0;
			needcounting = true;
			ht = new Hashtable();
		}
	}

	public void getconfig() throws IOException
	{

		FileInputStream file_in;
		DataInputStream data_in;
		String oneline = new String();
		int i = 0;
		InputStreamReader input = new InputStreamReader(System.in);
		BufferedReader reader = new BufferedReader(input);
		String response = "";
	
		File f2=convert(f1);
	    transafile=f2.getName();
	    
		try
		{
			textArea.append("\n\nThe number of attributes :" +N);
			textArea.append("\n\nthe number of transactions :"+M);
			textArea.append("\n\nthe support :"+minsup);
			stepm = Integer.parseInt(dicm);
			textArea.append("\n Step size :"+stepm);
		}
		catch (Exception e)
		{
			System.out.println(e);
		}
	}

public void sendData(File g1,double g2,String g3,int n1,int n2) throws Exception
{
		f1=g1;
		minsup=g2;
		dicm=g3;
		N=n1;
		M=n2;
		
}


public int getitemat(int i, String itemset)
	{

		String str1 = new String(itemset);

		int pos1, pos2;
		pos1 = 0;
		pos2 = 0;
		for (int a = 1; a < i; a++)
		{
			pos1 = itemset.indexOf(" ", pos1);
			pos1++;
		}
		pos2 = itemset.indexOf(" ", pos1 + 1);
		if (pos2 == -1)
			pos2 = itemset.length();
		str1 = itemset.substring(pos1, pos2);

		return (Integer.valueOf(str1.trim()).intValue());

	}


	public int itemsetsize(String itemset)
	{

		StringTokenizer st = new StringTokenizer(itemset);

		return st.countTokens();

	}


boolean dashfound(hashtreenode htn)
	{

		if (htn.state == DS || htn.state == DC)
			return true;
		for (Enumeration e = htn.ht.elements(); e.hasMoreElements(); )
			if (dashfound((hashtreenode)e.nextElement()))
				return true;
		return false;
	}

	public void printhashtree(hashtreenode htn, String transa, int a)
	{
		String state = new String();
		switch (htn.state)
		{
			case DC:
				state = "DC";
				DCset = DCset.concat(htn.itemset);
				DCset = DCset.concat(",");
				break;
			case DS:
				state = "DS";
				DSset = DSset.concat(htn.itemset);
				DSset = DSset.concat(",");
				break;
			case SC:
				state = "SC";
				SCset = SCset.concat(htn.itemset);
				SCset = SCset.concat(",");
				break;
			case SS:
				state = "SS";
				SSset = SSset.concat(htn.itemset);
				SSset = SSset.concat(",");
				break;
		}
		
		if (htn.ht == null)
			return;
		for (int b = a + 1; b <= itemsetsize(transa); b++)
			if (htn.ht.containsKey(Integer.toString(getitemat(b, transa))))
				printhashtree((hashtreenode)htn.ht.get(Integer.toString(getitemat(b, transa))), transa, b);

	}


	
	public void transatrahashtree(String transa, hashtreenode htn, int a)
	{

		if (htn.needcounting)
			htn.counter++;
		if (htn.ht == null)
			return;
		else
			for (int b = a + 1; b <= itemsetsize(transa); b++)
				if (htn.ht.containsKey(Integer.toString(getitemat(b, transa))))
					transatrahashtree(transa, (hashtreenode)htn.ht.get(Integer.toString(getitemat(b, transa))), b);

	}


	public void checkcountedall(hashtreenode htn, String transa, int startfrom)
	{

		if (htn.starting == tid && k != htn.startingk)
		{
			if (htn.state == DS)
			{
				htn.state = SS;
			}
			else if (htn.state == DC)
			{
				htn.state = SC;
			}
			htn.needcounting = false;
		}

		if (htn.ht == null || htn.ht.isEmpty())
			return;

		for (int c = startfrom + 1; c <= N; c++)
			if (htn.ht.containsKey(Integer.toString(getitemat(c, transa))))
				checkcountedall((hashtreenode)htn.ht.get(Integer.toString(getitemat(c, transa))), transa, c);

	}


	public void checkcounter(hashtreenode htn, String transa, int startfrom)
	{

		if (htn.state == DC && htn.counter >= ((minsup * M) / 100))
			htn.state = DS;

		if (htn.ht.isEmpty())
			return;

		for (int c = startfrom + 1; c <= N; c++)
			if (htn.ht.containsKey(Integer.toString(getitemat(c, transa))))
			{
				checkcounter((hashtreenode)htn.ht.get(Integer.toString(getitemat(c, transa))), transa, c);
			}
	}


	public void checkhashtree(hashtreenode htn, String transa, int startfrom)
	{

		String superset = new String();
		String subset = new String();
		StringTokenizer stsuperset, stsubset;
		boolean dcfound;

		if (htn.state == DS)
		{
			superset = gensuperset(htn.itemset);
			if (superset != null)
			{
				stsuperset = new StringTokenizer(superset, ",");
				while (stsuperset.hasMoreTokens())
				{
					String superset1 = new String(stsuperset.nextToken());
					if (htn.ht.containsKey(Integer.toString(getitemat(itemsetsize(superset1), superset1))))
						continue;
					subset = gensubset(superset1);
					stsubset = new StringTokenizer(subset, ",");
					dcfound = false;
					while (stsubset.hasMoreTokens())
						if (circlefound(root, stsubset.nextToken(), 0))
						{
							dcfound = true;
							break;
						}
					if (!dcfound)
					{
						hashtreenode tmphtn = new hashtreenode(DC, superset1, 0, tid, k);
						htn.ht.put(Integer.toString(getitemat(itemsetsize(superset1), superset1)), tmphtn);
					}
				}
			}
		}

		if (htn.ht == null || htn.ht.isEmpty())
			return;

		for (int c = startfrom + 1; c <= N; c++)
			if (htn.ht.containsKey(Integer.toString(getitemat(c, transa))))
				checkhashtree((hashtreenode)htn.ht.get(Integer.toString(getitemat(c, transa))), transa, c);

	} 


	public boolean circlefound(hashtreenode htn, String itemset, int startfrom)
	{

		if (htn.state == DC || htn.state == SC)
			return true;

		for (int c = startfrom + 1; c <= itemsetsize(itemset); c++)
			if (htn.ht.containsKey(Integer.toString(getitemat(c, itemset))))
				if (circlefound((hashtreenode)htn.ht.get(Integer.toString(getitemat(c, itemset))), itemset, c))
					return true;

		return false;
	}


	public String gensubset(String itemset)
	{

		int len = itemsetsize(itemset);
		int i, j;
		String str1;
		String str2 = new String();
		String str3 = new String();

		if (len == 1)
			return null;
		for (i = 1; i <= len; i++)
		{
			StringTokenizer st = new StringTokenizer(itemset);
			str1 = new String();
			for (j = 1; j < i; j++)
			{
				str1 = str1.concat(st.nextToken());
				str1 = str1.concat(" ");
			}
			str2 = st.nextToken();
			for (j = i + 1; j <= len; j++)
			{
				str1 = str1.concat(st.nextToken());
				str1 = str1.concat(" ");
			}
			if (i != 1)
				str3 = str3.concat(",");
			str3 = str3.concat(str1.trim());
		}

		return str3;

	} 


	public String gensuperset(String itemset)
	{

		String str1, str2;
		int c;
		int i1 = itemset.lastIndexOf(" ");

		if (i1 == -1)
			str1 = new String(itemset);
		else
			str1 = new String(itemset.substring(i1 + 1));
		c = Integer.valueOf(str1).intValue();
		if (c == N)
			return null;
		else
		{
			str2 = new String();
			for (int b = c + 1; b < N; b++)
			{
				str2 = str2.concat(itemset);
				str2 = str2.concat(" ");
				str2 = str2.concat(Integer.toString(b));
				str2 = str2.concat(",");
			}
			str2 = str2.concat(itemset);
			str2 = str2.concat(" ");
			str2 = str2.concat(Integer.toString(N));
		}
		return str2;
	}

	public void dic()  throws IOException
	{

		String fullitemset = new String();
		String transa = new String();
		int j;
		String str0;
		FileInputStream file_in;
		DataInputStream data_in;
		String oneline = new String();
		StringTokenizer st;
		int lineprocessed;
		boolean qiaole = false;
		Date d = new Date();
		long s1, s2;
		String ss1, ss2;
		int numRead = 0;
        
      textArea.append("\n\nAlgorithm DIC starting now.....\n");
		
	getconfig();

	root = new hashtreenode(SS, null, 0, 1, 0);
		for (int i = 1; i <= N; i++)
		{
		
			String str = new String(Integer.toString(i));
			
			hashtreenode htn = new hashtreenode(DC, str, 0, 1, 0);
			
				
			if (root.ht == null)
			{
				Hashtable ht = new Hashtable();
				root.ht = ht;
			}
			root.ht.put(str, htn);
		}

		file_in = new FileInputStream(transafile);
		data_in = new DataInputStream(file_in);

		fullitemset = fullitemset.concat("1");
		for (int i = 2; i <= N; i++)
		{
			fullitemset = fullitemset.concat(" ");
			fullitemset = fullitemset.concat(Integer.toString(i));
			
		}
		
		k = 0;
		tid = 1;
		lineprocessed = 0;

		d = new Date();
		s1 = d.getTime();
		
		textArea.append("Processing step M number: ");
		System.out.println("Processing Step M Number:");
		
		while (true)
		{
			k++;
		textArea.append(k + "..");
		System.out.print(k+"  ");
		if (!dashfound(root))
				break;
			lineprocessed = 0;
			while (true)
			{
				oneline = data_in.readLine();
				numRead++;
				if ((oneline == null) || (numRead > M))
				{
					numRead = 0;
					file_in = new FileInputStream(transafile);
					data_in = new DataInputStream(file_in);
					if (qiaole)
					{
						oneline = data_in.readLine();
						tid = 1;
					}
					else
					{
						tid = 1;
						break;
					}
				}
				st = new StringTokenizer(oneline.trim());
				j = 0;
				str0 = new String();
				transa = new String();
				while (st.hasMoreTokens())
				{
					j++;
					str0 = st.nextToken();
					int i = Integer.valueOf(str0).intValue();
					if (i != 0)
					{
						transa = transa.concat(" ");
						transa = transa.concat(Integer.toString(j));
					}
				}
				transa = transa.trim();
				
				transatrahashtree(transa, root, 0);
				lineprocessed++;
				tid++;
				if (lineprocessed >= stepm && tid > M)
					qiaole = true;
				else
					qiaole = false;

				if (tid > M)
					tid = 1;

				if (lineprocessed >= stepm)
					break;
			}

			
			checkcounter(root, fullitemset, 0);			
			checkhashtree(root, fullitemset, 0);
			checkcountedall(root, fullitemset, 0);

		}

		DSset = new String();
		DCset = new String();
		SCset = new String();
		SSset = new String();
		printhashtree(root, fullitemset, 0);
		textArea.append("\n");

		StringTokenizer sss;
		int k=1,l=3;
		int i = 1;
		j = 1;
		boolean found = false, first = true;
		while (true)
		{
		
			first = true;
			sss = new StringTokenizer(SSset, ",");
			found = false;
			while (sss.hasMoreTokens())
			{
				String superset1 = new String(sss.nextToken());
				String s8[];
				s8=superset1.split(" ");
				
				if( (s8.length==i)&&(superset1.length() > 0)	)	
						              
				{  
					if (first)
					{   
						textArea.append("\n Frequent " + j + "-itemsets:"+"\n");
						first = false;
						textArea.append("[" + superset1);
					}
					else
						textArea.append(", " + superset1);
					found = true;
				}
				
			
			}
			if (found == true)
				textArea.append("]");
			textArea.append("\n");
			if (!found)
				break;
				i++;
							
			j++;
		}

		d = new Date();
		s2 = d.getTime();
		textArea.append("Execution time is: " + (s2 - s1) + " milliseconds.\n\n");
		
}

	
	public File convert(File sname)
	{

		int s=0;
        int Max=119;
		int p=0;
	    outfile=new File("tr.txt");
	
    	try
     	{
      		String fileName=sname.getName();
    //  		outfile
      	    BufferedReader fileInput;
      		FileReader file = new FileReader(fileName);
	  
	        fileInput = new BufferedReader(file);
	  
      		PrintWriter writer   = new PrintWriter(new BufferedWriter(new FileWriter(outfile,true)));


 		    int array[]=new int[N+1]	;
 
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

