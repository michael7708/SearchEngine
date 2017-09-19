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

package test.java.edu.uci.ics.crawler4j.examples.basic;

import edu.uci.ics.crawler4j.crawler.CrawlConfig;
import edu.uci.ics.crawler4j.crawler.CrawlController;
import edu.uci.ics.crawler4j.fetcher.PageFetcher;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtConfig;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtServer;

/**
 * @author Yasser Ganjisaffar <lastname at gmail dot com>
 */
public class BasicCrawlController {
	
        public static void main(String[] args) throws Exception {
                if (args.length != 2) {
                        System.out.println("Needed parameters: ");
                        System.out.println("\t rootFolder (it will contain intermediate crawl data)");
                        System.out.println("\t numberOfCralwers (number of concurrent threads)");
                        return;
                }
                

                /*
                 * crawlStorageFolder is a folder where intermediate crawl data is
                 * stored.
                 */
               String crawlStorageFolder = args[0];

                /*
                 * numberOfCrawlers shows the number of concurrent threads that should
                 * be initiated for crawling.
                 */
                int numberOfCrawlers = Integer.parseInt(args[1]);

                CrawlConfig config = new CrawlConfig();

                config.setCrawlStorageFolder(crawlStorageFolder);

                /*
                 * Be polite: Make sure that we don't send more than 1 request per
                 * second (1000 milliseconds between requests).
                 */
                config.setPolitenessDelay(250);

                /*
                 * You can set the maximum crawl depth here. The default value is -1 for
                 * unlimited depth
                 */
                
                config.setMaxDepthOfCrawling(-1);

                /*
                 * You can set the maximum number of pages to crawl. The default value
                 * is -1 for unlimited number of pages
                 */
                config.setMaxPagesToFetch(-1);

                /*
                 * Do you need to set a proxy? If so, you can use:
                 * config.setProxyHost("proxyserver.example.com");
                 * config.setProxyPort(8080);
                 * 
                 * If your proxy also needs authentication:
                 * config.setProxyUsername(username); config.getProxyPassword(password);
                 */

                /*
                 * This config parameter can be used to set your crawl to be resumable
                 * (meaning that you can resume the crawl from a previously
                 * interrupted/crashed crawl). Note: if you enable resuming feature and
                 * want to start a fresh crawl, you need to delete the contents of
                 * rootFolder manually.
                 */
                config.setResumableCrawling(false);

                /*
                 * Instantiate the controller for this crawl.
                 */
                PageFetcher pageFetcher = new PageFetcher(config);
                RobotstxtConfig robotstxtConfig = new RobotstxtConfig();
                RobotstxtServer robotstxtServer = new RobotstxtServer(robotstxtConfig, pageFetcher);
                CrawlController controller = new CrawlController(config, pageFetcher, robotstxtServer);

                
                /*
                 * For each crawl, you need to add some seed urls. These are the first
                 * URLs that are fetched and then the crawler starts following links
                 * which are found in these pages
                 */

                controller.addSeed("http://mondego.ics.uci.edu/");
                controller.addSeed("http://sdcl.ics.uci.edu/2012/05/calico-for-the-mondego-group/");
                controller.addSeed("http://sdcl.ics.uci.edu/mondego-group/");
                controller.addSeed("http://www.ics.uci.edu/~lopes/");
                controller.addSeed("http://mondego.ics.uci.edu/datasets/wikipedia-events/files/");
                controller.addSeed("http://mondego.ics.uci.edu/datasets/wikipedia-events/");
                //controller.addSeed("http://sdcl.ics.uci.edu/calicoservers/");
                //controller.addSeed("http://www.ics.uci.edu/~djp3/classes/2006_03_30_ICS105/Resources/AnteaterIdol.html");
                controller.addSeed("http://www.ics.uci.edu/~lopes/datasets/");
                controller.addSeed("http://mlearn.ics.uci.edu/MLRepository.html");
                controller.addSeed("http://cml.ics.uci.edu/");
                controller.addSeed("http://www.ics.uci.edu/~qliu1/MLcrowd_ICML_workshop/");
                controller.addSeed("http://mlearn.ics.uci.edu/");
                controller.addSeed("http://sli.ics.uci.edu/Classes/2012F-273a");
                controller.addSeed("http://cml.ics.uci.edu/?page=events&subPage=aiml");
                //controller.addSeed("http://www.ics.uci.edu/~ihler/");
                controller.addSeed("http://kdd.ics.uci.edu/");
                //controller.addSeed("hhttp://mlearn.ics.uci.edu/MLSummary.html");
                controller.addSeed("http://www.ics.uci.edu/prospective/en/degrees/software-engineering/");
                controller.addSeed("http://www.ics.uci.edu/faculty/area/area_software");
                controller.addSeed("http://se.ics.uci.edu/Welcome.html");
                controller.addSeed("http://www.ics.uci.edu/grad/degrees/degree_se.php");
                controller.addSeed("http://www.ics.uci.edu/~emilyo/SimSE/se_rules.html");
                controller.addSeed("http://www.ics.uci.edu/ugrad/degrees/degree_se.php");
                //controller.addSeed("http://www.ics.uci.edu/~ziv/ooad/intro_to_se/tsld008.htm");
                //controller.addSeed("http://www.ics.uci.edu/~taylor/");
                controller.addSeed("http://www.ics.uci.edu/computing/linux/security.php");
                controller.addSeed("http://www.ics.uci.edu/faculty/area/area_security.php");
                controller.addSeed("http://sprout.ics.uci.edu/");
                controller.addSeed("http://www.ics.uci.edu/~gts/");
                controller.addSeed("http://sconce.ics.uci.edu/");
                controller.addSeed("http://www.ics.uci.edu/~goodrich/teach/ics8/");
                //controller.addSeed("http://www.ics.uci.edu/community/events/openhouse/research12.html");
                //controller.addSeed("http://www.ics.uci.edu/~goodrich/teach/ics247/");
                controller.addSeed("http://www.ics.uci.edu/about/search/search_sao.php");
                controller.addSeed("http://www.ics.uci.edu/prospective/en/contact/student-affairs/");
                controller.addSeed("http://www.ics.uci.edu/ugrad/sao/index.php");
                controller.addSeed("http://www.ics.uci.edu/about/annualreport/2005-06/sao.php");
                controller.addSeed("http://www.ics.uci.edu/about/annualreport/2006-07/sao.php");
        		controller.addSeed("http://www.ics.uci.edu/grad/sao/index.php");
        		//controller.addSeed("http://www.ics.uci.edu/about/search/search_dean.php");
        		//controller.addSeed("http://www.ics.uci.edu/about/visit/index.php");
        		controller.addSeed("http://www.ics.uci.edu/grad/");
        		controller.addSeed("http://www.ics.uci.edu/grad/courses/index.php");
        		controller.addSeed("http://www.ics.uci.edu/grad/courses/listing.php?year=2012&level=Graduate&department=CS&program=ALL");
        		controller.addSeed("http://www.ics.uci.edu/grad/courses/listing.php?year=2013&level=Graduate&department=CS&program=ALL");
        		controller.addSeed("http://www.ics.uci.edu/grad/admissions/index.php");
        		controller.addSeed("http://www.ics.uci.edu/grad/degrees/degree_cs.php");
        		//controller.addSeed("http://www.ics.uci.edu/grad/degrees/index.php");
        		controller.addSeed("http://www.ics.uci.edu/");
        		controller.addSeed("http://www.ics.uci.edu/~lopes/");
        		controller.addSeed("http://www.ics.uci.edu/~lopes/publications.html");
        		controller.addSeed("http://www.ics.uci.edu/~lopes/patents.html");
        		controller.addSeed("http://luci.ics.uci.edu/blog/?p=416");
        		controller.addSeed("http://www.ics.uci.edu/~tdebeauv/");
        		controller.addSeed("http://luci.ics.uci.edu/blog/?tag=crista-lopes");
        		controller.addSeed("http://luci.ics.uci.edu/blog/?tag=crista-lopes&paged=2");
        		//controller.addSeed("http://www.ics.uci.edu/faculty/highlights/");
        		controller.addSeed("http://www.ics.uci.edu/~fielding/pubs/dissertation/rest_arch_style.htm");
        		controller.addSeed("http://www.ics.uci.edu/~fielding/pubs/dissertation/top.htm");
        		controller.addSeed("http://www.ics.uci.edu/~fielding/pubs/dissertation/evaluation.htm");
        		controller.addSeed("http://www.ics.uci.edu/~fielding/pubs/dissertation/introduction.htm");
        		//controller.addSeed("http://www.ics.uci.edu/~fielding/pubs/dissertation/conclusions.htm");
        		//controller.addSeed("http://www.ics.uci.edu/~fielding/pubs/dissertation/abstract.htm");
        		controller.addSeed("http://www.ics.uci.edu/~fielding/");
        		controller.addSeed("http://www.ics.uci.edu/~rohit/");
        		controller.addSeed("http://www.ics.uci.edu/ugrad/degrees/degree_cgs.php");
        		controller.addSeed("http://cgvw.ics.uci.edu/");
        		controller.addSeed("http://www.ics.uci.edu/prospective/en/degrees/computer-game-science/");
        		controller.addSeed("http://www.ics.uci.edu/ugrad/degrees/index.php");
        		controller.addSeed("http://www.ics.uci.edu/ugrad/degrees/degree_cgs_curriculum.php");
        		//controller.addSeed("http://cgvw.ics.uci.edu/?author=2");
        		controller.addSeed("http://cgvw.ics.uci.edu/?p=266");
        		controller.addSeed("http://www.ics.uci.edu/~lopes/teaching/cs221W12/index.html");
        		controller.addSeed("http://www.ics.uci.edu/~lopes/teaching/inf141W12/index.html");
        		controller.addSeed("http://www.ics.uci.edu/~lopes/teaching/inf141W11/");
        		controller.addSeed("http://www.ics.uci.edu/~djp3/classes/2010_01_CS221/index.html");
        		controller.addSeed("http://www.ics.uci.edu/~djp3/classes/2009_01_02_INF141/index.html");
        		//controller.addSeed("http://www.ics.uci.edu/~djp3/classes/2009_01_02_INF141/calendar.html");
        		//controller.addSeed("http://www.ics.uci.edu/~djp3/classes/2010_01_CS221/assignmentSchedule.html");
        		controller.addSeed("http://luci.ics.uci.edu/blog/?tag=information-retrieval");
        		//controller.addSeed("http://www.ics.uci.edu/~djp3/classes/2008_09_26_CS221/calendar.html");
               // controller.addSeed("http://www.ics.uci.edu/");
                //controller.addSeed("http://www.ics.uci.edu/~lopes/");
                //controller.addSeed("http://www.ics.uci.edu/~welling/");
                //controller.addSeed("http://mlearn.ics.uci.edu/MLRepository.html");
                //controller.addSeed("http://mlearn.ics.uci.edu/");
                //controller.addSeed("http://cml.ics.uci.edu/");
                //controller.addSeed("http://sli.ics.uci.edu/Classes/2012F-273a");
                //controller.addSeed("http://mlearn.ics.uci.edu/MLRepository.html");
                //controller.addSeed("http://www.ics.uci.edu/~qliu1/MLcrowd_ICML_workshop/");
                //controller.addSeed("http://www.ics.uci.edu/~djp3/classes/2009_01_02_INF141/index.html");
                //controller.addSeed("http://www.ics.uci.edu/~lopes/teaching/cs221W12/index.html");
                //controller.addSeed("http://www.ics.uci.edu/~lopes/teaching/inf141W12/index.html");
                //controller.addSeed("http://www.ics.uci.edu/~djp3/classes/2010_01_CS221/index.html");
                //controller.addSeed("http://www.ics.uci.edu/~lopes/teaching/inf141W11/");
                //controller.addSeed("http://www.ics.uci.edu/prospective/en/degrees/computer-game-science/");
                //controller.addSeed("http://www.ics.uci.edu/grad/courses/listing.php?year=2013&level=Graduate&department=CS&program=ALL");
                /*
                 * Start the crawl. This is a blocking operation, meaning that your code
                 * will reach the line after this only when crawling is finished.
                 */
               if(controller.isFinished()==true)
               {
            	   BasicCrawler.printgram();
               }
                controller.start(BasicCrawler.class, numberOfCrawlers);
              
        }


}
