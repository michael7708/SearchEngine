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
 * limitations under the License.
 */

package edu.uci.ics.crawler4j.crawler;

import edu.uci.ics.crawler4j.fetcher.PageFetchResult;
import edu.uci.ics.crawler4j.fetcher.CustomFetchStatus;
import edu.uci.ics.crawler4j.fetcher.PageFetcher;
import edu.uci.ics.crawler4j.frontier.DocIDServer;
import edu.uci.ics.crawler4j.frontier.Frontier;
import edu.uci.ics.crawler4j.parser.HtmlParseData;
import edu.uci.ics.crawler4j.parser.ParseData;
import edu.uci.ics.crawler4j.parser.Parser;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtServer;
import edu.uci.ics.crawler4j.url.WebURL;

import org.apache.http.HttpStatus;
import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;


/**
 * WebCrawler class in the Runnable class that is executed by each crawler
 * thread.
 * 
 * @author Yasser Ganjisaffar <lastname at gmail dot com>
 */
public class WebCrawler implements Runnable {

	protected static final Logger logger = Logger.getLogger(WebCrawler.class.getName());

	/**
	 * The id associated to the crawler thread running this instance
	 */
	protected int myId;

	/**
	 * The controller instance that has created this crawler thread. This
	 * reference to the controller can be used for getting configurations of the
	 * current crawl or adding new seeds during runtime.
	 */
	protected CrawlController myController;

	/**
	 * The thread within which this crawler instance is running.
	 */
	private Thread myThread;

	/**
	 * The parser that is used by this crawler instance to parse the content of
	 * the fetched pages.
	 */
	private Parser parser;

	/**
	 * The fetcher that is used by this crawler instance to fetch the content of
	 * pages from the web.
	 */
	private PageFetcher pageFetcher;

	/**
	 * The RobotstxtServer instance that is used by this crawler instance to
	 * determine whether the crawler is allowed to crawl the content of each
	 * page.
	 */
	private RobotstxtServer robotstxtServer;

	/**
	 * The DocIDServer that is used by this crawler instance to map each URL to
	 * a unique docid.
	 */
	private DocIDServer docIdServer;

	/**
	 * The Frontier object that manages the crawl queue.
	 */
	private Frontier frontier;

	/**
	 * Is the current crawler instance waiting for new URLs? This field is
	 * mainly used by the controller to detect whether all of the crawler
	 * instances are waiting for new URLs and therefore there is no more work
	 * and crawling can be stopped.
	 */
	private boolean isWaitingForNewURLs;

	/**
	 * Initializes the current instance of the crawler
	 * 
	 * @param myId
	 *            the id of this crawler instance
	 * @param crawlController
	 *            the controller that manages this crawling session
	 */
	public void init(int myId, CrawlController crawlController) {
		this.myId = myId;
		this.pageFetcher = crawlController.getPageFetcher();
		this.robotstxtServer = crawlController.getRobotstxtServer();
		this.docIdServer = crawlController.getDocIdServer();
		this.frontier = crawlController.getFrontier();
		this.parser = new Parser(crawlController.getConfig());
		this.myController = crawlController;
		this.isWaitingForNewURLs = false;
	}

	/**
	 * Get the id of the current crawler instance
	 * 
	 * @return the id of the current crawler instance
	 */
	public int getMyId() {
		return myId;
	}

	public CrawlController getMyController() {
		return myController;
	}

	/**
	 * This function is called just before starting the crawl by this crawler
	 * instance. It can be used for setting up the data structures or
	 * initializations needed by this crawler instance.
	 */
	public void onStart() {
	}

	/**
	 * This function is called just before the termination of the current
	 * crawler instance. It can be used for persisting in-memory data or other
	 * finalization tasks.
	 */
	public void onBeforeExit() {
	}
	
	/**
	 * This function is called once the header of a page is fetched.
	 * It can be overwritten by sub-classes to perform custom logic
	 * for different status codes. For example, 404 pages can be logged, etc.
	 */
	protected void handlePageStatusCode(WebURL webUrl, int statusCode, String statusDescription) {
	}

	/**
	 * The CrawlController instance that has created this crawler instance will
	 * call this function just before terminating this crawler thread. Classes
	 * that extend WebCrawler can override this function to pass their local
	 * data to their controller. The controller then puts these local data in a
	 * List that can then be used for processing the local data of crawlers (if
	 * needed).
	 */
	public Object getMyLocalData() {
		return null;
	}

	public void run() {
		onStart();
		while (true) {
			List<WebURL> assignedURLs = new ArrayList<WebURL>(50);
			isWaitingForNewURLs = true;
			frontier.getNextURLs(50, assignedURLs);
			isWaitingForNewURLs = false;
			if (assignedURLs.size() == 0) {
				if (frontier.isFinished()) {
					return;
				}
				try {
					Thread.sleep(3000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			} else {
				for (WebURL curURL : assignedURLs) {
					if (curURL != null) {
						processPage(curURL);
						frontier.setProcessed(curURL);
					}
					if (myController.isShuttingDown()) {
						logger.info("Exiting because of controller shutdown.");
						return;
					}
				}
			}
		}
	}

	/**
	 * Classes that extends WebCrawler can overwrite this function to tell the
	 * crawler whether the given url should be crawled or not. The following
	 * implementation indicates that all urls should be included in the crawl.
	 * 
	 * @param url
	 *            the url which we are interested to know whether it should be
	 *            included in the crawl or not.
	 * @return if the url should be included in the crawl it returns true,
	 *         otherwise false is returned.
	 */
	public boolean shouldVisit(WebURL url) {
		return true;
	}

	/**
	 * Classes that extends WebCrawler can overwrite this function to process
	 * the content of the fetched and parsed page.
	 * 
	 * @param page
	 *            the page object that is just fetched and parsed.
	 */
	public void visit(Page page) {
	}

	private void processPage(WebURL curURL) {
		if (curURL == null) {
			return;
		}
		PageFetchResult fetchResult = null;
		try {
			fetchResult = pageFetcher.fetchHeader(curURL);
			int statusCode = fetchResult.getStatusCode();
			handlePageStatusCode(curURL, statusCode, CustomFetchStatus.getStatusDescription(statusCode));
			if (statusCode != HttpStatus.SC_OK) {
				if (statusCode == HttpStatus.SC_MOVED_PERMANENTLY || statusCode == HttpStatus.SC_MOVED_TEMPORARILY) {
					if (myController.getConfig().isFollowRedirects()) {
						String movedToUrl = fetchResult.getMovedToUrl();
						if (movedToUrl == null) {
							return;
						}
						int newDocId = docIdServer.getDocId(movedToUrl);
						if (newDocId > 0) {
							// Redirect page is already seen
							try{
							File pagerank_dest = new File ("C:\\Users\\Michael\\workspace\\Project2\\pr\\"+newDocId+"uciics.txt"); 
							
							if(pagerank_dest.exists())
							{		
								System.out.println("FILE exist!!!");
							
							}
							else{
						    System.out.println("FILE not exist!!!");
							FileOutputStream out2; // declare a file output object
    					    PrintStream print2; // declare a print stream object  
    					    out2 = new FileOutputStream("C:\\Users\\Michael\\workspace\\Project2\\pr\\"+newDocId+"uciics.txt");
    			            print2 = new PrintStream(out2);            
    			            
    			            String pagerank= Float.toString(0.0f);
    			            print2.println(pagerank);
    			            print2.close();
							}
							}
							catch(Exception e){
								System.out.println("Pagerank error");
							}
							
							return;
						} else {
							WebURL webURL = new WebURL();
							webURL.setURL(movedToUrl);
							webURL.setParentDocid(curURL.getParentDocid());
							webURL.setParentUrl(curURL.getParentUrl());
							webURL.setDepth(curURL.getDepth());
							webURL.setDocid(-1);
							webURL.setAnchor(curURL.getAnchor());// Set Anchor in Webcrawler
							if (shouldVisit(webURL) && robotstxtServer.allows(webURL)) {
								webURL.setDocid(docIdServer.getNewDocID(movedToUrl));
								frontier.schedule(webURL);
							}
						}
					}
				} else if (fetchResult.getStatusCode() == CustomFetchStatus.PageTooBig) {
					logger.info("Skipping a page which was bigger than max allowed size: " + curURL.getURL());
				}
				return;
			}

			if (!curURL.getURL().equals(fetchResult.getFetchedUrl())) {
				if (docIdServer.isSeenBefore(fetchResult.getFetchedUrl())) {
					// Redirect page is already seen
					return;
				}
				curURL.setURL(fetchResult.getFetchedUrl());
				curURL.setDocid(docIdServer.getNewDocID(fetchResult.getFetchedUrl()));
			}

			Page page = new Page(curURL);
			int docid = curURL.getDocid();
			if (fetchResult.fetchContent(page) && parser.parse(page, curURL.getURL())) {
				ParseData parseData = page.getParseData();
				if (parseData instanceof HtmlParseData) {
					HtmlParseData htmlParseData = (HtmlParseData) parseData;

					List<WebURL> toSchedule = new ArrayList<WebURL>();
					int maxCrawlDepth = myController.getConfig().getMaxDepthOfCrawling();
					for (WebURL webURL : htmlParseData.getOutgoingUrls()) {
						webURL.setParentDocid(docid);
						webURL.setParentUrl(curURL.getURL());
						
						int newdocid = docIdServer.getDocId(webURL.getURL());
						if (newdocid > 0) {
							// This is not the first time that this Url is
							// visited. So, we set the depth to a negative
							// number.
							int line_src =0;
							int line_dest=0;
							
							String pr_src="";
							String pr_dest="";
							String old_income_src= "";
							String old_income_dest= "";
							float srcpr=0.0f;
							float destpr=0.0f;
							List<WebURL> links = htmlParseData.getOutgoingUrls();
							try{
								File pagerank_src = new File ("C:\\Users\\Michael\\workspace\\Project2\\pr\\"+docid+"uciics.txt"); 
	    			            if(!pagerank_src.exists())
								{
									srcpr=1;
									srcpr   =   (float)(Math.round(srcpr*100000))/100000;
									srcpr=srcpr/(links.size());
									srcpr   =   (float)(Math.round(srcpr*100000))/100000;
									//System.out.println("size of link is "+links.size());
								}
								else{
									BufferedReader reader_src = new BufferedReader(new FileReader(pagerank_src));
								while((pr_src =reader_src.readLine()) != null)  //read the text from input file
						        {
									line_src++;
									if(line_src ==1 && pr_src.equals("0")){
										srcpr   =   (float)(Math.round(srcpr*100000))/100000;
										srcpr = 1/(links.size());
										srcpr   =   (float)(Math.round(srcpr*100000))/100000;
										break;
									}
									if(line_src ==1 && !pr_src.equals("0"))
									{
										old_income_src=pr_src;
										//System.out.println("The income links : "+old_income);
										srcpr=Float.parseFloat(old_income_src); // String to float
										srcpr   =   (float)(Math.round(srcpr*100000))/100000;
										srcpr=srcpr/(links.size());
										srcpr   =   (float)(Math.round(srcpr*100000))/100000;
										break;
									}
						        }
								reader_src.close();
								}
							//System.out.println("Visited URL is :"+webURL.toString()+"its DocID is :"+newdocid);//<====Visited Webpage
							File pagerank_dest = new File ("C:\\Users\\Michael\\workspace\\Project2\\pr\\"+newdocid+"uciics.txt"); 
												
							if(pagerank_dest.exists())
							{		
								BufferedReader reader_dest = new BufferedReader(new FileReader(pagerank_dest));		
								System.out.println("FILE exist!!!");
								pr_dest =reader_dest.readLine();
								while(pr_dest!=null)  //read the text from input file
						           {
									//System.out.println("pr_dest is "+pr_dest);
									line_dest++;
									if(line_dest ==1)
									{   
										destpr=Float.parseFloat(pr_dest);
										destpr=  (float)(Math.round(destpr*100000))/100000;
										break;
									}
						           }
								reader_dest.close();
								//System.out.println("The result is "+destpr+srcpr);
								float sum = destpr+srcpr;
								sum   =   (float)(Math.round(sum*100000))/100000;
								String sd_sum = String.format("%f", sum);
								String newtext = old_income_dest.replaceAll(old_income_dest,sd_sum);	
								
					            FileWriter writer = new FileWriter(pagerank_dest);
					            writer.write(newtext);
					            writer.close();
							}
							
							else{
						    System.out.println("FILE not exist!!!");
							FileOutputStream out2; // declare a file output object
    					    PrintStream print2; // declare a print stream object  
    					    out2 = new FileOutputStream("C:\\Users\\Michael\\workspace\\Project2\\pr\\"+newdocid+"uciics.txt");
    			            print2 = new PrintStream(out2);            
    			            srcpr  =   (float)(Math.round(srcpr*100000))/100000;
    			            //String pagerank= Float.toString(srcpr);
    			            String pagerank = String.format("%f",srcpr);
    			            print2.println(pagerank);
    			            print2.close();
							}
							}
							catch(Exception e){
								System.out.println("Pagerank error");
							}
							webURL.setDepth((short) -1);
							webURL.setDocid(newdocid);
						} else {
							webURL.setDocid(-1);
							webURL.setDepth((short) (curURL.getDepth() + 1));
							if (maxCrawlDepth == -1 || curURL.getDepth() < maxCrawlDepth) {
								if (shouldVisit(webURL) && robotstxtServer.allows(webURL)) {
									webURL.setDocid(docIdServer.getNewDocID(webURL.getURL()));
									toSchedule.add(webURL);
								}
							}
						}
					}
					frontier.scheduleAll(toSchedule);
				}
				try {
					  visit(page);
					} catch (Exception e) {
					  logger.error("Exception while running the visit method. Message: '" + e.getMessage() + "' at " + e.getStackTrace()[0]);
					}
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage() + ", while processing: " + curURL.getURL());
		} finally {
			if (fetchResult != null) {
				fetchResult.discardContentIfNotConsumed();
			}
		}
	}

	public Thread getThread() {
		return myThread;
	}

	public void setThread(Thread myThread) {
		this.myThread = myThread;
	}

	public boolean isNotWaitingForNewURLs() {
		return !isWaitingForNewURLs;
	}

}