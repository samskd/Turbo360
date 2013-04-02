package edu.nyu.cs.cs2580.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class Util {

	public static void writeTempGraphToFile(
			Map<Integer, List<Integer>> incomingLinks, String tempFile) 
					throws IOException{

		FileWriter fileWriter = new FileWriter(tempFile);
		BufferedWriter bufferWriter = new BufferedWriter(fileWriter);

		try{

			List<Integer> pageIDs = new ArrayList<Integer>(incomingLinks.keySet());
			Collections.sort(pageIDs);


			StringBuilder temp = new StringBuilder();

			for(int pageID : pageIDs){
				temp.append(pageID+" ");
				List<Integer> links = incomingLinks.get(pageID);
				for(int link : links){
					temp.append(link+" "); 
				}

				bufferWriter.write(temp+"\n");
				//empty the stringbuilder
				temp.delete(0, temp.length());
			}

		}finally{
			bufferWriter.close();
			fileWriter.close();
		}

	}


	public static void mergeGraphFiles(String folderName, 
			String outGoingLinksCountFiles) throws IOException {

		BufferedReader[] readers = null;

		File file = new File("data/corpus.graph");
		// if file doesnt exists, then create it
		if (!file.exists()) {
			file.createNewFile();
		}

		FileWriter fileWriter = new FileWriter(file);
		BufferedWriter bufferWriter = new BufferedWriter(fileWriter);

		FileReader fileReader = new FileReader(outGoingLinksCountFiles);
		BufferedReader outGoingLinksCountReader = new BufferedReader(fileReader);

		try{

			File directory = new File(folderName);
			File[] tempFiles = new File[directory.list().length];

			if(directory.exists() && directory.isDirectory()){
				tempFiles = directory.listFiles();
			}

			readers = new BufferedReader[tempFiles.length];
			String[] currentLines = new String[tempFiles.length];

			for(int i = 0; i<tempFiles.length; i++){
				readers[i] = new BufferedReader(new FileReader(tempFiles[i]));
				String temp = readers[i].readLine();
				if(temp == null){
					currentLines[i] = null;
				}else{
					currentLines[i] = temp.trim();
				}
			}

			int endOfFiles = 0;
			int currentPageID = 1;
			StringBuilder incomingLinksList = new StringBuilder();

			do{
				String start;
				if((start = outGoingLinksCountReader.readLine()) == null){
					break;
				}
				incomingLinksList.append(start.trim());

				for(int i = 0; i < tempFiles.length; i++){

					if(currentLines[i] != null && !currentLines[i].isEmpty() &&
							currentLines[i].startsWith(Integer.toString(currentPageID))){

						incomingLinksList.append(" "+currentLines[i].substring(
								currentLines[i].indexOf(" ")+1, currentLines[i].length()));

						String temp = readers[i].readLine();
						if(temp == null){
							currentLines[i] = null;
							endOfFiles++;
							readers[i].close();
						}else{
							currentLines[i] = temp.trim();
						}
					}
				}

				bufferWriter.write(incomingLinksList + "\n");
				incomingLinksList.delete(0, incomingLinksList.length());
				currentPageID++;

			}while(endOfFiles < tempFiles.length);

			bufferWriter.close();

			//Delete all temporary files
			for(File tFile : tempFiles){
				tFile.delete();
			}

		}catch(Exception e){
			e.printStackTrace();
		}finally{
			if(bufferWriter != null)
				bufferWriter.close();
			if(fileWriter != null)
				fileWriter.close();
			if(readers != null){
				for(BufferedReader reader : readers){
					if(reader != null)
						reader.close();
				}
			}
		}

	}

}
