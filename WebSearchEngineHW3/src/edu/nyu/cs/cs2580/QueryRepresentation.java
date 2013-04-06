package edu.nyu.cs.cs2580;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import com.google.code.externalsorting.ExternalSort;

import edu.nyu.cs.cs2580.util.Util;

public class QueryRepresentation {
	
	private Ranker _ranker;
	private Indexer _indexer;
	
	public QueryRepresentation(Ranker ranker, Indexer indexer){
		this._ranker = ranker;
		this._indexer = indexer;
	}

	public String represent(Query query, int numberOfDocumentsToBeConsidered, int numberOfTerms) throws IOException{

		FileWriter fileWrite = null;
		BufferedWriter bufferedWriter = null;
		
		FileReader fileReader = null;
		BufferedReader bufferedReader = null;
		
		StringBuilder output = new StringBuilder();
		
		try{
			
			String inputFile = "tempProbabilities";
			String outputFile = "probabilities";
			String resultFile = "data/prf/qr_"+query._query+"_" +
					numberOfDocumentsToBeConsidered+"_"+numberOfTerms;

			Vector<ScoredDocument> documents = _ranker.runQuery(query, numberOfDocumentsToBeConsidered);
			long totalWords = 0;

			int temp = numberOfDocumentsToBeConsidered;
			ScoredDocument doc = documents.firstElement();
			Set<Integer> allTerms = new HashSet<Integer>();

			for(int i=0; i<temp; i++, doc=documents.get(i)){
				DocumentIndexed d = (DocumentIndexed)doc.getDocument();
				Vector<Integer> documentVector = d.getDocumentTokens();
				for(Integer term : documentVector){
					allTerms.add(term);
				}
				
				totalWords += documentVector.size();
			}

			fileWrite = new FileWriter(inputFile);
			bufferedWriter = new BufferedWriter(fileWrite);

			double probabilitySum = 0;
			
			Iterator<Integer> terms = allTerms.iterator();
			while(terms.hasNext()){
				int term = terms.next();
				long termTotal = 0;
				doc = documents.firstElement();
				for(int i=0;i<temp; doc=documents.get(++i)){
					DocumentIndexed d = (DocumentIndexed)doc.getDocument();
					termTotal += Util.getTermCount(d.getDocumentTokens(), term);
				}
				double prob = (double)termTotal/totalWords;
				probabilitySum += prob;
				bufferedWriter.write(term +"\t"+ prob + "\n");
			}

			
			//External sort the probability file
			Comparator<String> comparator = new Comparator<String>() {

				public int compare(String r1, String r2){
					String[] t1 = r1.split("\\s+");
					Double prob1 = Double.parseDouble(t1[1]);

					String[] t2 = r2.split("\\s+");
					Double prob2 = Double.parseDouble(t2[1]);
					//sort descending
					return prob2.compareTo(prob1);}
			};

			File input = new File(inputFile);
			List<File> l = ExternalSort.sortInBatch(input, comparator) ;
			ExternalSort.mergeSortedFiles(l, new File(outputFile), comparator);

			fileReader = new FileReader(outputFile);
			bufferedReader = new BufferedReader(fileReader);

			temp = numberOfTerms;
			
			String line = null;
			while((line = bufferedReader.readLine()) != null){
				String[] entry = line.split("\\s+");
				//normalized value
				double prob = Double.parseDouble(entry[1])/probabilitySum;
				output.append(entry[0]+"\t"+prob+"\n");
				if(temp==0) break;
				else --temp;
			}
			
			input.delete();
			
			fileWrite = new FileWriter(resultFile);
			bufferedWriter = new BufferedWriter(fileWrite);
			bufferedWriter.write(output.toString());

		}catch(Exception e){
			e.printStackTrace();
		}finally{
			if(fileWrite != null) fileWrite.close();
			if(fileReader != null) fileReader.close();
			if(bufferedReader != null) bufferedReader.close();
			if(bufferedWriter != null) bufferedWriter.close();
		}

		return output.toString();
	}
	
}
