package edu.nyu.cs.cs2580.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
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


	public static void mergeGraphFiles(String folderName, String outputFile, 
			int totalPages) throws IOException {

		//		BufferedReader[] readers = null;

		File file = new File(outputFile);
		// if file doesnt exists, then create it
		if (!file.exists()) {
			file.createNewFile();
		}

		FileWriter fileWriter = new FileWriter(file);
		BufferedWriter bufferWriter = new BufferedWriter(fileWriter);

		//writes the total pages on the first line
		bufferWriter.write(totalPages+"\n");

		try{

			File directory = new File(folderName);
			File[] tempFiles = new File[directory.list().length];

			if(directory.exists() && directory.isDirectory()){
				tempFiles = directory.listFiles();
			}

			int currentPageID = 0;

			//combine all the files into one. Temp files doesn't have correlated data, 
			//so just copy and add into new file
			for(File tFile : tempFiles){
				BufferedReader reader = new BufferedReader(new FileReader(tFile));
				try{
					String line = reader.readLine(); 
					while(line != null){
						line = line.trim();

						if(!line.isEmpty() && line.startsWith(Integer.toString(currentPageID))){
							bufferWriter.write(line + "\n");
							line = reader.readLine();
						}else{
							bufferWriter.write(currentPageID + "\n");
						}
						++currentPageID;
					}
					reader.close();
					reader = null;
				}catch(Exception e){
					e.printStackTrace();
				}finally{
					if(reader != null) reader.close();
				}

			}

			//			readers = new BufferedReader[tempFiles.length];
			//			String[] currentLines = new String[tempFiles.length];
			//
			//			for(int i = 0; i<tempFiles.length; i++){
			//				readers[i] = new BufferedReader(new FileReader(tempFiles[i]));
			//				String temp = readers[i].readLine();
			//				if(temp == null){
			//					currentLines[i] = null;
			//				}else{
			//					currentLines[i] = temp.trim();
			//				}
			//			}
			//
			//			int endOfFiles = 0;
			//			int currentPageID = 1;
			//			StringBuilder incomingLinksList = new StringBuilder();
			//
			//			do{
			//				String start = Integer.toString(currentPageID);
			//				incomingLinksList.append(start.trim());
			//
			//				for(int i = 0; i < tempFiles.length; i++){
			//
			//					if(currentLines[i] != null && !currentLines[i].isEmpty() &&
			//							currentLines[i].startsWith(Integer.toString(currentPageID))){
			//
			//						incomingLinksList.append(" "+currentLines[i].substring(
			//								currentLines[i].indexOf(" ")+1, currentLines[i].length()));
			//
			//						String temp = readers[i].readLine();
			//						if(temp == null){
			//							currentLines[i] = null;
			//							endOfFiles++;
			//							readers[i].close();
			//						}else{
			//							currentLines[i] = temp.trim();
			//						}
			//					}
			//				}
			//
			//				bufferWriter.write(incomingLinksList + "\n");
			//				incomingLinksList.delete(0, incomingLinksList.length());
			//				currentPageID++;
			//
			//			}while(endOfFiles < tempFiles.length);

			bufferWriter.close();

			//Delete all temporary files
			for(File tFile : tempFiles){
				tFile.delete();
			}

		}catch(Exception e){
			e.printStackTrace();
		}finally{
			bufferWriter.close();
			fileWriter.close();
			//			if(readers != null){
			//				for(BufferedReader reader : readers){
			//					if(reader != null)
			//						reader.close();
			//				}
			//			}

		}

	}


	public static void writePageRanks(double[] pageRanks, String outputFile) throws IOException{

		FileWriter fileWriter = new FileWriter(outputFile);
		BufferedWriter bufferWriter = new BufferedWriter(fileWriter);
		bufferWriter.write(pageRanks.length + "\n");
		try{
			for(int i=0; i<pageRanks.length; i++){
				bufferWriter.write((i+1) + " " + pageRanks[i] + "\n");
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			bufferWriter.close();
			fileWriter.close();
		}
	}

}
