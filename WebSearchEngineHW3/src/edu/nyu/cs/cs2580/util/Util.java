package edu.nyu.cs.cs2580.util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class Util {

	public static Object processDocument(File file) throws IOException{
		final String title;
		final List<String> links = new ArrayList<String>();
		
		Document doc = Jsoup.parse(file, "UTF-8", file.getName());
		
		return new Object(){
			public String _title = title;
			public List<String> _links = links;
		};
	}
	
}
