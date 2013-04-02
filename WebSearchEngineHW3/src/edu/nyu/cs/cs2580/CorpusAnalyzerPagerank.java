package edu.nyu.cs.cs2580;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.nyu.cs.cs2580.SearchEngine.Options;
import edu.nyu.cs.cs2580.util.Util;

/**
 * @CS2580: Implement this class for HW3.
 */
public class CorpusAnalyzerPagerank extends CorpusAnalyzer {
	public CorpusAnalyzerPagerank(Options options) {
		super(options);
	}

	int linksBlockSize = 2000;
	String tempFolder = "data/temp/";

	/**
	 * This function processes the corpus as specified inside {@link _options}
	 * and extracts the "internal" graph structure from the pages inside the
	 * corpus. Internal means we only store links between two pages that are both
	 * inside the corpus.
	 * 
	 * Note that you will not be implementing a real crawler. Instead, the corpus
	 * you are processing can be simply read from the disk. All you need to do is
	 * reading the files one by one, parsing them, extracting the links for them,
	 * and computing the graph composed of all and only links that connect two
	 * pages that are both in the corpus.
	 * 
	 * Note that you will need to design the data structure for storing the
	 * resulting graph, which will be used by the {@link compute} function. Since
	 * the graph may be large, it may be necessary to store partial graphs to
	 * disk before producing the final graph.
	 *
	 * @throws IOException
	 */
	@Override
	public void prepare() throws IOException {
		System.out.println("Preparing " + this.getClass().getName());

		//create temporary folders
		File directory = new File(tempFolder);
		if(!directory.exists()){ directory.mkdirs(); }

		directory = new File(tempFolder+"graph");
		if(!directory.exists()){ directory.mkdirs(); }


		String tempOutgoingLinkCountFile = tempFolder+"outgoingLinksCount";
		FileWriter fileWriter = new FileWriter(tempOutgoingLinkCountFile);
		BufferedWriter bufferWriter = new BufferedWriter(fileWriter);

		int blockSize = linksBlockSize;
		int blockNumber = 0;

		try{

			File corpusDirectory = new File(_options._corpusPrefix);

			//returns if the corpus prefix is not a directory
			if(!corpusDirectory.isDirectory()){
				return;
			}

			//Maps page link to ID
			Map<String, Integer> pages = new HashMap<String, Integer>();
			int pageCount = 0;
			for(File page : corpusDirectory.listFiles()){
				pages.put(page.getName(), ++pageCount);
			}

			//Maps Link ID to set of incoming links
			Map<Integer, List<Integer>> incomingLinks = new HashMap<Integer, List<Integer>>();

			for(File page : corpusDirectory.listFiles()){

				HeuristicLinkExtractor extractor = new HeuristicLinkExtractor(page);
				String sourcePageLink = extractor.getLinkSource();
				Integer sourcePageID;

				if((sourcePageID = pages.get(sourcePageLink)) == null){
					System.out.println(page.getName());
					continue;
				}

				//System.out.println("#### Source : "+sourcePageLink+ "-->"+sourcePageID);

				String targetPageLink = null;
				int outgoingLinks = 0;

				while((targetPageLink = extractor.getNextInCorpusLinkTarget())!=null){

					Integer targetLinkID;
					if((targetLinkID = pages.get(targetPageLink)) == null){
						continue;
					}

					List<Integer> links;
					if((links = incomingLinks.get(targetLinkID)) == null){
						links = new ArrayList<Integer>();
						incomingLinks.put(targetLinkID, links);
					}

					links.add(sourcePageID);
					outgoingLinks++;
				}

				blockSize--;
				//write the temp link list to file
				if(blockSize == 0){
					Util.writeTempGraphToFile(incomingLinks, tempFolder+"graph/graph"+blockNumber);
					incomingLinks.clear();
					blockNumber++;
					blockSize = linksBlockSize;
				}

				bufferWriter.write(sourcePageID+" "+outgoingLinks+"\n");
				//outgoingLinksTotal.put(sourcePageID, outgoingLinks);
			}

			//write remaining links
			Util.writeTempGraphToFile(incomingLinks, tempFolder+"graph/graph"+blockNumber);
			incomingLinks.clear();
			blockNumber++;
			blockSize = linksBlockSize;

			//Merge all the temp files
			Util.mergeGraphFiles(tempFolder+"graph/", tempOutgoingLinkCountFile);

		}finally{
			bufferWriter.close();
			fileWriter.close();
		}

		return;
	}

	/**
	 * This function computes the PageRank based on the internal graph generated
	 * by the {@link prepare} function, and stores the PageRank to be used for
	 * ranking.
	 * 
	 * Note that you will have to store the computed PageRank with each document
	 * the same way you do the indexing for HW2. I.e., the PageRank information
	 * becomes part of the index and can be used for ranking in serve mode. Thus,
	 * you should store the whatever is needed inside the same directory as
	 * specified by _indexPrefix inside {@link _options}.
	 *
	 * @throws IOException
	 */
	@Override
	public void compute() throws IOException {
		System.out.println("Computing using " + this.getClass().getName());
		return;
	}

	/**
	 * During indexing mode, this function loads the PageRank values computed
	 * during mining mode to be used by the indexer.
	 *
	 * @throws IOException
	 */
	@Override
	public Object load() throws IOException {
		System.out.println("Loading using " + this.getClass().getName());
		return null;
	}
}
