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
	
	public QueryRepresentation(Ranker ranker){
		this._ranker = ranker;
	}

	public void represent(Query query, int numberOfDocumentsToBeConsidered, int numberOfTerms) throws IOException{

		try{

			String inputFile = "tempProbabilities";
			String outputFile = "probabilities";

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

			FileWriter fileWrite = new FileWriter(inputFile);
			BufferedWriter bufferedWriter = new BufferedWriter(fileWrite);

			System.out.println(totalWords);
			Iterator<Integer> terms = allTerms.iterator();
			while(terms.hasNext()){
				int term = terms.next();
				long termTotal = 0;
				doc = documents.firstElement();
				for(int i=0;i<temp; doc=documents.get(++i)){
					DocumentIndexed d = (DocumentIndexed)doc.getDocument();
					termTotal += Util.getTermCount(d.getDocumentTokens(), term);
				}

				bufferedWriter.write(term +"\t"+ (double)termTotal/totalWords + "\n");
			}

			bufferedWriter.close();
			fileWrite.close();

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

			FileReader fileReader = new FileReader(outputFile);
			BufferedReader bufferedReader = new BufferedReader(fileReader);

			temp = numberOfTerms;
			StringBuilder str = new StringBuilder();
			String line = null;
			while((line = bufferedReader.readLine()) != null){
				str.append(line+"\n");
				if(temp==0) break;
				else --temp;
			}

			bufferedReader.close();
			fileReader.close();
			
			input.delete();

			System.out.println(str.toString());

		}catch(Exception e){
			e.printStackTrace();
		}

	}
	
}
