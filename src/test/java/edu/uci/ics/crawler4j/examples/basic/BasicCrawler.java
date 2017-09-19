/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License. 2013
 */

package test.java.edu.uci.ics.crawler4j.examples.basic;

import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.crawler.WebCrawler;
import edu.uci.ics.crawler4j.parser.HtmlParseData;
import edu.uci.ics.crawler4j.url.WebURL;
import edu.uci.ics.crawler4j.frequency.Frequency;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.StringTokenizer;
import java.util.regex.Pattern;

/**
 * @author Yasser Ganjisaffar <lastname at gmail dot com>
 */
public class BasicCrawler extends WebCrawler {

	private final static Pattern FILTERS = Pattern.compile(".*(\\.(css|js|bmp|gif|jpe?g" + "|png|tiff?|mid|mp2|mp3|mp4"
			+ "|wav|avi|mov|mpeg|ram|m4v|pdf" + "doc|docx|xls|xlsx|ppt|pptx|txt|exe|ps"
			+ "|rm|smil|wmv|swf|wma|zip|rar|gz|tgz))$");

	/**
	 * You should implement this function to specify whether the given url
	 * should be crawled or not (based on your crawling logic).
	 */
	static List<Frequency> mostDis = new ArrayList<Frequency>(); 
    static List<String> stopWords = new ArrayList<String>();
    static List<String> UrltoDocid = new ArrayList<String>();//<===corresponding ID to URL LIST
    static int shut = 0;
    String[] stopWord = { "a", "about", "above", "after", "again", "against", "all", "am",
    		"an", "and", "any", "are", "aren't", "as", "at", "be", "because", "been", "before",
    		"being", "below", "between", "both", "but", "by", "can't", "cannot", "could", "couldn't", 
    		"did", "didn't", "do", "does", "doesn't", "doing", "don't", "down", "during", "each", 
    		"few", "for", "from", "further", "had", "hadn't", "has", "hasn't", "have", "haven't",
    		"having", "he", "he'd", "he'll", "he's", "her", "here", "here's", "hers", "herself", "him", 
    		"himself", "his", "how", "how's","i", "i'd", "i'll", "i'm", "i've", "if", "in", "into", 
    		"is", "isn't", "it", "it's", "its", "itself", "let's", "me", "more", "most", "mustn't", 
    		"my", "myself", "no", "nor", "not", "of", "off", "on", "once", "only", "or", "other", 
    		"ought", "our", "ours", "ourselves", "out", "over", "own", "same", "shan't", "she", "she'd", 
    		"she'll", "she's", "should", "shouldn't" ,"so",	"some",	"such",	"than",	"that",	"that's", "the",
    		"their", "theirs", "them" ,"themselves" ,"then" ,"there" ,"there's" ,"these" ,"they" ,"they'd", 
    		"they'll", "they're", "they've", "this" ,"those", "through" ,"to", "too", "under", "until", 
    		"up", "very", "was", "wasn't", "we", "we'd", "we'll", "we're", "we've", "were", "weren't", 
    		"what", "what's", "when", "when's", "where", "where's", "which", "while", "who", "who's", 
    		"whom", "why", "why's", "with", "won't", "would", "wouldn't", "you", "you'd", "you'll", 
    		"you're", "you've", "your", "yours", "yourself", "yourselves",
    		"b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z","?"};
    {
		for (int i = 0; i < stopWord.length; i++)
		{
			stopWords.add(stopWord[i]);
		}
}
	@Override
	public boolean shouldVisit(WebURL url) {
		String href = url.getURL().toLowerCase();
		 return !FILTERS.matcher(href).matches() && 
					href.contains(".ics.uci.edu/")
					&& !href.contains("pdf/pdf/pdf/")&& !href.contains("archive.ics") && !href.contains("calendar.ics")&& !href.startsWith("http://djp3-pc2.ics.uci.edu");
	}
	
	/**
	 * This function is called when a page is fetched and ready to be processed
	 * by your program.
	 */
	static List<Frequency> subdomain=new ArrayList<Frequency>();
	static List<Frequency> URL=new ArrayList<Frequency>();
	static List<Frequency> f = new ArrayList<Frequency>();
	@Override
	public void visit(Page page) {
		//List<Frequency> subdomain = new ArrayList<Frequency>();
		//List<Frequency> URL = new ArrayList<Frequency>();
		int docid = page.getWebURL().getDocid();
		String url = page.getWebURL().getURL();		
		String anchortext = page.getWebURL().getAnchor();//<=====create an anchor text		
		String domain = page.getWebURL().getDomain();
		String path = page.getWebURL().getPath();
		String subDomain = page.getWebURL().getSubDomain();
		String parentUrl = page.getWebURL().getParentUrl();

		System.out.println("Docid: " + docid);
		System.out.println("URL: " + url);
		System.out.println("Domain: '" + domain + "'");
		System.out.println("Sub-domain: '" + subDomain + "'");
		System.out.println("Path: '" + path + "'");
		System.out.println("Parent page: " + parentUrl);
		
        
		if (page.getParseData() instanceof HtmlParseData) {
			HtmlParseData htmlParseData = (HtmlParseData) page.getParseData();
			String text = htmlParseData.getText();
			String html = htmlParseData.getHtml();
			String title = htmlParseData.getTitle(); // <======GET title
			List<WebURL> links = htmlParseData.getOutgoingUrls();

			OutputData(text,url, domain, subDomain, docid, anchortext, title);//OUTPUTDATA....
			System.out.println("Text length: " + text.length());
			System.out.println("Html length: " + html.length());
			System.out.println("Title : " + title);
			System.out.println("anchortext: " + anchortext);
			System.out.println("Number of outgoing links: " + links.size());
			//System.out.println("Text is : " + text);
			counturL(page.getWebURL(),links.size());
			countsub(page.getWebURL());		
			OutputRecord(subdomain,URL);
		}
		System.out.println("=============");		
	}
	 public static void OutputRecord(List<Frequency> subdomain,List<Frequency> URL){
		 try { 
		 File f=new File("C:\\Users\\Michael\\workspace\\Project2\\Subdomains.txt");
		 BufferedWriter bw=new BufferedWriter(new FileWriter(f));
	    for(int i=0;i<subdomain.size();i++){
	            bw.write(subdomain.get(i).toString());
	            bw.newLine();
	        }
	     bw.write("=============================");
         bw.newLine();
         bw.write("URL: ");
         bw.newLine();
         
         for(int i=0;i<URL.size();i++){
	            bw.write(URL.get(i).toString());
	            bw.newLine();
	        }
	      bw.close();    
		 }
		 catch (Exception e)

	        {System.err.println ("Error writing to file");}		 
		 
	 }
	 public static ArrayList<String> tokenizeFile(File file, String url, String domain, String subDomain, int docid,ArrayList<String> filterwords, 
			 String anchortext, String title) {
	 
	 FileOutputStream out; // declare a file output object
	 PrintStream print; // declare a print stream object   
     ArrayList<String> mostgram = new ArrayList<String>();	
	 try{
		 if(file.isFile() && file.exists())  //to make sure file is exist and correct
         { 
           BufferedReader reader = new BufferedReader(new FileReader(file));  //use bufferedreader to read the content 
           //StringBuffer sb = new StringBuffer();   //create data to browse the content of file(access the content from bufferedreader)
           String data ="";
           while((data =reader.readLine()) != null)  //read the text from input file
           {
  	         boolean flag = false;               
           //System.out.println(data);            	
  	       StringTokenizer token = new StringTokenizer(data,"\\|"+" "+"\b"+"\t"+"\n"+"\f"+"\r"+"—"+"?"+",.!\n\t\"()@#$%^&~<>/[]" +
    	    		 "‘…’’“{}-_*;:=\\+”?+””– - _ ");//doing filtering
             while (token.hasMoreTokens())//if there are more tokens
	         {
  	          //System.out.println(token.nextToken()); 
  	            String save = token.nextToken().toString();
  	          //System.out.println("token is "+save);
  	            String finalstr = save.toLowerCase();
  	            mostgram.add(finalstr);
  	            for(int j = 0; j < stopWords.size(); j++)
  		        {
  			      flag = finalstr.equals(stopWords.get(j));
  			      if(flag == true)
  			      {
  			    	 //System.out.println("Match stop words : "+stopWords.get(j));
  				  break;
  		          }
  		        }  	            
  	           if(flag == false)
  	           {
  	         	filterwords.add(finalstr);  // save to the Arraylist
  	           }
             } //end while
          }//end while read file
            reader.close();   //close bufferedreader
            file.delete();
            
            out = new FileOutputStream("C:\\Users\\Michael\\workspace\\Project2\\final\\"+docid+"uciics.txt");
                       
            String stringValue = Integer.toString(docid); //<====corresponding mapping for Docid to URL
            String stringCom = stringValue+"->"+url;
            UrltoDocid.add(stringCom);
            
          //Connect print stream to the output stream
            print = new PrintStream(out); 
            boolean flag_title = false; 
            boolean flag_anchor = false;
            String finaltitle =""; 
            String finalanchor = "";
            if(title == null)
            {//<====implement Title First line title
               	print.println("");
            }
            else{
            	/*  
                StringTokenizer token_title = new StringTokenizer(title,"\\|"+"\b"+"\t"+"\n"+"\f"+"\r"+"—"+"?"+",.!\n\t\"()@#$%^&~<>/[]" +
       	    		 "‘…’’“{}-_*;:=\\+”+””?– - _ ");//doing filtering for Title
                      while (token_title.hasMoreTokens())//if there are more tokens
             	         {
               	          //System.out.println(token.nextToken()); 
               	            String title_temp = token_title.nextToken().toString();
               	          //System.out.println("token is "+save);
               	          
               	            for(int j = 0; j < stopWords.size(); j++)
               		        {
               			     flag_title = title_temp.equals(stopWords.get(j));
               			        if(flag_title == true)
               			        {
               			    	 //System.out.println("Match stop words : "+stopWords.get(j));
               				     break;
               		            }
               		        }
               			    if(flag_title == false){
                  	        	  finaltitle+=title_temp.toLowerCase();
                  	        	finaltitle+=" ";
                  	           }
               		 }  */
                  	print.println(title);  
            }
            if(anchortext==null)   
            {////<====implement Anchor Text Second line anchor text
               	print.println("");
            }
            else{
            	StringTokenizer token_anchor = new StringTokenizer(anchortext,"\\|"+"\b"+"\t"+"\n"+"\f"+"\r"+"—"+"?"+",.!\n\t\"()@#$%^&~<>/[]" +
          	    		 "‘…’’“{}-_*;:=\\+”+””?– - _ ");//doing filtering for Anchor Text
            	while (token_anchor.hasMoreTokens())//if there are more tokens
    	         {
      	          //System.out.println(token.nextToken()); 
      	            String anchor_temp = token_anchor.nextToken().toString();
      	          //System.out.println("token is "+save);
      	          
      	            for(int j = 0; j < stopWords.size(); j++)
      		        {
      			     flag_anchor = anchor_temp.equals(stopWords.get(j));
      			        if(flag_anchor == true)
      			        {
      			    	 //System.out.println("Match stop words : "+stopWords.get(j));
      				     break;
      		            }
      		        }
      			    if(flag_anchor == false)
      			    {
         	        	  finalanchor+=anchor_temp.toLowerCase();
         	        	 finalanchor+=" ";
         	        }
      		      }//end while  anchor
            	 print.println(finalanchor);
             }
            
            print.println(url);// Line #3 print URL
            
            for(int a =0;a<filterwords.size();a++)
            {
              print.println (filterwords.get(a).toLowerCase());
            }     
            String site = subDomain+"."+domain;
            //print.println(site);
            
             print.close();
             computeTwoGramFrequencies(mostgram);
           }
         }
		 catch (Exception ex) {
             System.out.println("ERROR");}
		return filterwords;
	 }
	 
	
 public static List<Frequency> computeWordFrequencies(List<String> words) {
		// TODO Write body!
		
		List<Frequency> tester = new ArrayList<Frequency>();
		
  int i=0, count;
		while(i<words.size())
  {
  	String a = words.get(i);
  	//tester<Frequency>(a);
  	count = 1;
  	for(int j = i+1; j < words.size(); j++)
  	{
  		String b = words.get(j);
  		int flag;
  		flag = a.compareTo(b);
  		//System.out.println("j is "+j);
  		if(flag == 0)
  		{
  			words.remove(j);
  			count++;
  		}
  	} 
  	tester.add(new Frequency(a, count));
  	i++;
  } 

		Collections.sort(tester,
		new Comparator<Frequency>()
		{
  	public int compare(Frequency o1, Frequency o2)
  	{
  		return o1.getText().compareTo(o2.getText());
      }
  });
		Collections.sort(tester,
		        new Comparator<Frequency>() 
		        {
					public int compare(Frequency o1, Frequency o2) 
		            {
		                return o2.getFrequency()-o1.getFrequency();
		            }
		        }
		);
		//System.out.println(words);
		return tester;
	} 
 
 public static void OutputData(String text, String url, String domain, String subDomain, int num, String anchortext, String title) { 
    	
        FileOutputStream out; // declare a file output object
        PrintStream print; // declare a print stream object 
      
        ArrayList<String> filterwords = new ArrayList<String>();
        try {        	    	  
        	out = new FileOutputStream("C:\\Users\\Michael\\workspace\\Project2\\uciics.txt");
    	 
           // Connect print stream to the output stream
           print = new PrintStream(out);       
          
           print.println (text);               
           print.close();                           
         
           File file=new File("C:\\Users\\Michael\\workspace\\Project2\\uciics.txt");
           if(file.exists())
           {
        	   System.out.println("UCIICS is EXISTed!!!!");
           }
           filterwords= tokenizeFile(file,url,domain, subDomain, num,filterwords, anchortext, title); //Information  
           List<String> notStop = new ArrayList<String>();
       	List<String> tokenStr = new ArrayList<String>();
       	boolean flag = true;
       	String str;
       	for(int i = 0; i<filterwords.size(); i++)
       	{
       		str = filterwords.get(i);
       		for(int j = 0; j < stopWords.size(); j++)
       		{
       			flag = str.equals(stopWords.get(j));
       			if(flag == true)	break;
       		}
       		if(flag == false)	
       		{
       			tokenStr.add(str);
       			notStop.add(str);
       		}
       		if(notStop.size() == 5000)
       		{
       			List<Frequency> temp = computeWordFrequencies(notStop);
       			if(mostDis.size()==0)
       			{
       				for(int m = 0; m<temp.size(); m++)
       					mostDis.add(new Frequency(temp.get(m).getText(),temp.get(m).getFrequency()));
       			}
       			else
       			{
       				for(int m = 0; m<temp.size(); m++)
       				{
       					boolean flag2 = false;
       					for(int n = 0; n<mostDis.size(); n++)
       					{
       						flag2 = temp.get(m).getText().equals(mostDis.get(n).getText());
       						if(flag2 == true)
       						{
       							int number = mostDis.get(n).getFrequency()+temp.get(m).getFrequency();
       							mostDis.set(n, new Frequency(temp.get(m).getText(),number));
                   				break;
       						}
       					}
       					if(flag2 == false && mostDis.size()<5000)
       						mostDis.add(new Frequency(temp.get(m).getText(),temp.get(m).getFrequency()));
       				}
       			}
       			notStop.clear();
       		}
       	}
       	
       	if(notStop.size() > 0)
   		{
   			List<Frequency> temp = computeWordFrequencies(notStop);
   			if(mostDis.size()==0)
   			{
   				for(int m = 0; m<temp.size(); m++)
   					mostDis.add(new Frequency(temp.get(m).getText(),temp.get(m).getFrequency()));
   			}
   			else
   			{
   				for(int m = 0; m<temp.size(); m++)
   				{
   					boolean flag2 = false;
   					for(int n = 0; n<mostDis.size(); n++)
   					{
   						flag2 = temp.get(m).getText().equals(mostDis.get(n).getText());
   						if(flag2 == true)
   						{
   							int number = mostDis.get(n).getFrequency()+temp.get(m).getFrequency();
   							mostDis.set(n, new Frequency(temp.get(m).getText(),number));
               				break;
   						}
   					}
   					if(flag2 == false && mostDis.size()<5000)
   						mostDis.add(new Frequency(temp.get(m).getText(),temp.get(m).getFrequency()));
   				}
   			}
   			notStop.clear();
   		}
       	
       	/*Collections.sort(mostDis,
   		        new Comparator<Frequency>() 
   		        {
   					public int compare(Frequency o1, Frequency o2) 
   		            {
   		                return o2.getFrequency()-o1.getFrequency();
   		            }
   		        }
   		);
       	for(int r=0; r<mostDis.size();r++)
       	{
       		System.out.println(mostDis.get(r).getText()+" "+mostDis.get(r).getFrequency());
       	}*/
       	/*try
       	{
       		FileWriter writer = new FileWriter("CommonWords.txt"); 
       		for(int i=0;i<mostDis.size();i++)
       		{
       			writer.write(mostDis.get(i).getText()+" "+mostDis.get(i).getFrequency()+"\r\n");
       		}
       		writer.close(); 
       	} 
       	catch(IOException ex) { 
       		ex.printStackTrace(); 
       	}*/
       	

       }            
       catch (Exception e)
       {System.err.println ("Error writing to file");}
    }
     
 
  public static void getfrequency(String newstr) {
	try
    {  
		shut++;
		if(shut>5000)
		{
			printgram();
		}
	  boolean flag=false;
      if(f.isEmpty()){
    		f.add(new Frequency(newstr,1));
    	}
    	else{
    	if(f.size()<=5000)
    	{
    		String news = "";	
    		for(int a=0;a<f.size();a++)
    		{
    			news=f.get(a).getText();
    			if(news.equals(newstr))
    			{
    			   flag=true;
    			   //System.out.println("final str is the same as "+news);
    			   f.get(a).incrementFrequency();
    			   break;
    			}	
       		}
    		if(flag==false){    			
    		       f.add(new Frequency(newstr,1));
    		}
    	 }
    	if(f.size()>=5001)
    	{
    		String news = "";	
    		for(int a=0;a<5001;a++)
    		{
    			news=f.get(a).getText();
    			if(news.equals(newstr))
    			{		
    			   f.get(a).incrementFrequency();
    			}	
    		}
    	}
    	}         
	       
	  	} 
    	catch(Exception ex) { 
    		ex.printStackTrace(); 
    	}
    }
   public static void printgram(){
    	
    	try{
           	FileOutputStream out; // declare a file output object
            PrintStream print; // declare a print stream object 
		   // System.out.println("f is "+f.toString());
    	    out = new FileOutputStream("C:\\Users\\Michael\\workspace\\Project2\\Common2Grams.txt");
	        print = new PrintStream(out);
	    	Collections.sort(f,	new Comparator<Frequency>() 
   			{
   				public int compare(Frequency o1, Frequency o2) 
   				{
   					return o2.getFrequency()-o1.getFrequency();
   				}
   			});
	        for(int a =0;a<100;a++)
	        {
	        print.println (f.get(a).toString());
	        }
	        print.close();
	       
	        Collections.sort(mostDis,
	   		        new Comparator<Frequency>() 
	   		        {
	   					public int compare(Frequency o1, Frequency o2) 
	   		            {
	   		                return o2.getFrequency()-o1.getFrequency();
	   		            }
	   		        }
	   		);
	        FileWriter writer = new FileWriter("CommonWords.txt"); 
       		for(int i=0;i<mostDis.size();i++)
       		{
       			writer.write(mostDis.get(i).getText()+" "+mostDis.get(i).getFrequency()+"\r\n");
       		}
       		writer.close();
       		
       		FileWriter writer2 = new FileWriter("UrlToDocid.txt"); //the correspondinf LIST
       		for(int i=0;i<UrltoDocid.size();i++)
    		{
    			writer2.write(UrltoDocid.get(i).toString()+"\r\n");
    		}
    		writer2.close();
    		
    		
       		shut=0;
      	} 
    	catch(IOException ex) { 
    		ex.printStackTrace(); 
    	}
    }
    public static void computeTwoGramFrequencies(ArrayList<String> mostgram) {
		// TODO Write body!
		try{
			List<String>twogram=new ArrayList<String>(mostgram);
			while(!twogram.isEmpty())
			{
		            String newstr1 = "",newstr2 = "";
			        newstr1 = twogram.get(0);
			        newstr2 = twogram.get(1);
			        twogram.remove(0);
		        	StringBuilder finalstr = new StringBuilder();
	            	finalstr.append(newstr1);
	            	finalstr.append(" ");
	            	finalstr.append(newstr2);
	               //System.out.println("The finalStr in compute is "+ finalstr);
		            getfrequency(finalstr.toString());
			}			
		}
		catch (Exception ex) {
            System.out.println("List is Empty");
                            }
	  }
    
    public static void counturL(WebURL url, int num) { 
    	
	   try{
	    	if(URL.isEmpty())
	    	{
	    		URL.add(new Frequency(url.getURL(),num));
	    	}
	    	else{
	    	    int a=0, flag=-1;
	 	       	String newu = "";	
	 			while(a<URL.size())
	 	        {
	 	        	newu= url.getURL();
	 	        	String compareu = URL.get(a).getText();
	 	        	flag = newu.compareTo(compareu);
	 	        	if(flag == 0)
	 	        	{
	 	        		System.out.println("Its duplicate URL!!!!");
	 	        		URL.add(new Frequency("DUPLICATE"+url.getURL(),1));
	 	        		break;
	 	        	}
	 	          	a++;
	 	        }
	 			if(flag!=0)
	 			{
	 			   URL.add(new Frequency(newu,num)); 	        	
	 			}
	 			Collections.sort(URL,
				new Comparator<Frequency>()
				{
		        	public int compare(Frequency o1, Frequency o2)
		        	{
		        		return o1.getText().compareTo(o2.getText());
		            }
		        });
	    	}
	    	    
	        } catch (Exception e)

	        {System.err.println ("Error comparing URL");}
	      
	           
	    }
    public static void countsub( WebURL url) {            
	    	
	     try{
	    	 String news =url.getSubDomain()+"."+url.getDomain();
	    	if(subdomain.isEmpty())
	    	{
	    		subdomain.add(new Frequency(news,1));
	    	}
	    	else{
	    		    int i=0,flag=-1;
	    	        while(i<subdomain.size())
	    	        {
	    	        	String compare = subdomain.get(i).getText();
	    	        	flag = news.compareTo(compare);
	    	        	if(flag == 0)
	    	        	{
	    	        		subdomain.get(i).incrementFrequency();
	    	        		break;
	    	        	}
	    	            	        	
	    	         	i++;
	    	        } 
	    			if(flag!=0){
	    				subdomain.add(new Frequency(news,1));
		        	}
	    			Collections.sort(subdomain,
	        		        new Comparator<Frequency>() 
	        		        {
	        					public int compare(Frequency o1, Frequency o2) 
	        		            {
	        		                return o2.getFrequency()-o1.getFrequency();
	        		            }
	        		        }
	        		);
	    	}    	
	     
	    }
	        catch (Exception e)

	        {System.err.println ("Error comparing subdomain");}
	      
	     }
   
}