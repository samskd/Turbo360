package edu.nyu.cs.cs2580;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class QuerySimilarity {
	
	private String directoryPath;
	File folder = new File(directoryPath);
	private File[] files = folder.listFiles();
	
	
	private String[] createListOfFiles(){
		String[] fileNames = new String[files.length];
		int i=-1;
		for(File f : files){
			if(f.isFile() && f.getName().endsWith(".prf"))
				fileNames[++i] = f.getName();
		}
		return fileNames;
	}
	
	private Map<String,Double> createMap(String f){
		File file = new File(f);
		Map<String,Double> map = new HashMap<String,Double>();
		BufferedReader br;
		String[] temp;
		try {
			br = new BufferedReader(new FileReader(file));
			String line = null;
			while((line = br.readLine()) != null){
				temp = line.split("\\s+");
				if(temp.length != 2)
					continue;
				map.put(temp[0],Double.parseDouble(temp[1]));
			}
			br.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return map;
	}
	
	private List<String> intersection(Map<String,Double> m1, Map<String,Double> m2){
		Set<String> intersectionSet = new HashSet<String>(m1.keySet());
		intersectionSet.retainAll(m2.keySet());
		return new ArrayList<String>(intersectionSet);
	}
	
	public void querySimilarity(){
		String[] files = this.createListOfFiles();
		Map<String,Double> map1;
		Map<String,Double> map2;
		List<String> keys;
		for(int i=0; i<files.length-1; i++){
			for(int j=i+1; j<files.length; j++){
				 map1 = this.createMap(files[i]);
				 map2 = this.createMap(files[j]);
				 keys = this.intersection(map1, map2);
				 double summation = 0d;
				 for(String s : keys){
					 summation += Math.sqrt(map1.get(s)*map2.get(s));
				 }
				 System.out.println(files[i] + "\\t" + files[j] + "\\t" + summation);
			}
		}
		
	}
}
