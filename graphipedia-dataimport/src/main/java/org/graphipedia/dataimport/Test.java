package org.graphipedia.dataimport;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.URL;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.graphipedia.dataimport.wikipedia.DisambiguationPages;
import org.graphipedia.dataimport.wikipedia.Infobox;
import org.graphipedia.dataimport.wikipedia.InfoboxTemplates;
import org.graphipedia.dataimport.wikipedia.Link;
import org.graphipedia.dataimport.wikipedia.Namespace;
import org.graphipedia.dataimport.wikipedia.Namespaces;
import org.graphipedia.dataimport.wikipedia.parser.InfoboxParser;
import org.graphipedia.dataimport.wikipedia.parser.WikiTextParser;

public class Test {

	public static void main(String[] args) throws IOException {
		
		testLinks();
		
		/*Test t = new Test();
		t.testResource();
		
		//System.out.println(ReadableTime.readableTime(Long.parseLong("61000")));
		
		
		//System.exit(-1);
		
		String text = "";
		BufferedReader bd = new BufferedReader(new FileReader("article.txt"));
		String line = null;
		while( (line=bd.readLine())!= null )
			text += "\n" + line;
		bd.close();
		System.out.println("stripping references");
		text = stripReferences(text); // problem in stripping references!!!
		//System.out.println("stripped");
		//System.out.println(text);
		//System.exit(-1);
		InfoboxTemplates templates = new InfoboxTemplates(new File("it"));
		System.out.println("Loading the templates");
		templates.load(new File("it", "infobox-templates.txt"));
		//templates.load("it", "Categoria:Template sinottici");

		InfoboxParser infoParser = new InfoboxParser(templates);
		System.out.println("parsing the infobox");
		Infobox info = infoParser.parse(text);
		if ( info == null  )
			System.out.println("infobox does not exist");
		else {
			System.out.println("Start index " + info.startIndex());
			System.out.println(info.text());
			System.out.println("End index " + info.endIndex());
		}*/
	}
	
	private static void testLinks() throws IOException {
		String text = "";
		BufferedReader bd = new BufferedReader(new FileReader("article.txt"));
		String line = null;
		while( (line=bd.readLine())!= null )
			text += "       \n" + line;
		bd.close();
		InfoboxTemplates it = new InfoboxTemplates(new File("it"));
		//System.out.println("Loading the templates");
		it.load(new File("it", "infobox-templates.txt"));
		Namespaces ns = extractNamespaces();
		DisambiguationPages dp = new DisambiguationPages(new File("it"));
		dp.load(new File("it", "disambiguation-pages.txt"));
		WikiTextParser parser = new WikiTextParser(ns, it, dp);
		Set<Link> links = parser.parse("2. Liga", text);
		System.out.println("Extracting links");
		for ( Link l : links )
			if ( !l.isRegularLink() )
				System.out.println(l.targetTitle());
	}
	
	private static Namespaces extractNamespaces() throws IOException {
		Namespaces ns = new Namespaces();
		BufferedReader bd = new BufferedReader(new FileReader(new File("it", "namespaces.txt")));
		String line;
		while( (line = bd.readLine()) != null ) {
			String[] values = line.split("\t");
			ns.add(new Namespace(Integer.parseInt(values[0]), values[1]));
		}
		
		bd.close();
		return ns;
	}
	
	private void testResource() {
		ClassLoader classLoader = getClass().getClassLoader();
		InputStream stream  = classLoader.getResourceAsStream("dp-root-categories.csv");
		if (stream == null)
			System.out.println("Fuck");
		else {
			System.out.println("belin");
		}
	}
	
	private static String stripReferences(String text) {
		int startPos = -1;
		while( (startPos = text.indexOf("<ref")) >= 0 ) {
			int ref = 1;
			int endPos = startPos + "<ref".length();
			while ( endPos < text.length() ) {
				switch(text.charAt(endPos)) {
				case '>' :
					if (text.charAt(endPos-1) == '/')
						ref--;
					endPos++;
					break;
				case '<' :
					if (endPos + 1 < text.length() && endPos + 10 < text.length() 
							&& text.substring(endPos + 1, endPos + 10).equals("ref name=")) {
						ref++;
						endPos += 10;
					}
					else if ( endPos + 1 < text.length() && endPos + 5 < text.length() && 
							text.substring(endPos + 1, endPos + 5).equals("ref>") ) {
						ref ++;
						endPos += 5;
					}
					else if ( endPos + 1 < text.length() && endPos + 6 < text.length() && 
							text.substring(endPos + 1, endPos + 6).equals("/ref>")  ) {
						ref--;
						endPos += 6;
					}
					else
						endPos += 1;
					break;
				default: endPos += 1;
				}
				if ( ref == 0 )
					break;
			} // end while
			String toBeRemoved = "";
			if(endPos+1 >= text.length()) 
				toBeRemoved = text.substring(startPos);
			else
				toBeRemoved = text.substring(startPos, endPos);
			text = text.replace(toBeRemoved, "");
		}
		return text;
	}

	private static String strip(String text) {
		int startPos = -1;
		while( (startPos = text.indexOf("{{")) >=0 ) {
			int bracketCount = 2;
			int endPos = startPos + "{{".length();
			for(; endPos < text.length(); endPos++) {
				switch(text.charAt(endPos)) {
				case '}':
					bracketCount--;
					break;
				case '{':
					bracketCount++;
					break;
				default:
				}
				if(bracketCount == 0) break;
			}
			String toBeRemoved = "";
			if(endPos+1 >= text.length()) 
				toBeRemoved = text.substring(startPos);
			else
				toBeRemoved = text.substring(startPos, endPos+1);
			text = text.replace(toBeRemoved, "");
		}

		return text;
	}

}
