package org.graphipedia.dataextract;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.compress.compressors.CompressorInputStream;
import org.apache.commons.compress.compressors.CompressorStreamFactory;
import org.graphipedia.GraphipediaSettings;
import org.graphipedia.progress.LoggerFactory;
import org.graphipedia.progress.ProgressCounter;
import org.graphipedia.progress.ReadableTime;
import org.graphipedia.wikipedia.Geotags;

/**
 * This thread extracts the geotags associated to spatial entities in Wikipedia.
 * The input to this thread is the file that contains the geographic coordinates of all the Wikipedia pages
 * describing spatial entities.
 * The output of this thread is an index of the Wikipedia pages that describe spatial entities..
 * The pages are indexed by their wikipedia identifier.  
 */
public class ExtractGeoTags extends Thread {

	/**
	 * The index of the spatial entities.
	 */
	private Map<String, Geotags> geoTags;

	/**
	 * Pattern to extract a set of geotags from the input file.   
	 */
	private static final Pattern GEO_TAGS_PATTERN = Pattern.compile("INSERT INTO `geo_tags` VALUES (.+)");

	/**
	 * Pattern to extract the geotag attached to a spatial entity.   
	 */
	private static final Pattern GEO_TAG_PATTERN = Pattern.compile("\\((.+?),(.+?),'(.*?)',(.+?),(.+?),(.+?),(.+?),(.+?),(.+?),(.+?),(.+?)\\)");

	/**
	 * The logger of this thread.
	 */
	private Logger logger;

	/**
	 * The settings of the import.
	 */
	private GraphipediaSettings settings;

	/**
	 * The code of the language of the Wikipedia edition being imported.
	 */
	private String language;

	/**
	 * Tracks the progress of the import.
	 */
	private ProgressCounter pageCounter;

	/**
	 * Creates a new thread.
	 * @param settings The settings of the import.
	 * @param language The code of the language of the Wikipedia edition being currently imported.
	 */
	public ExtractGeoTags(GraphipediaSettings settings, String language) {
		this.geoTags = new HashMap<String, Geotags>();
		this.settings = settings;
		this.language = language;
		this.logger = LoggerFactory.createLogger("Extract geotags  (" + language.toUpperCase() + ")");
		this.pageCounter = new ProgressCounter(this.logger);
	}

	@Override
	public void run() {
		if ( settings.getGeotagsFile(language) == null )
			return;
		String geotagsFile = settings.getGeotagsFile(language).getAbsolutePath();
		long startTime = System.currentTimeMillis();
		try {
			FileInputStream fin = new FileInputStream(geotagsFile);
			BufferedInputStream bis = new BufferedInputStream(fin);
			CompressorInputStream input = new CompressorStreamFactory().createCompressorInputStream(bis);
			parse(input);
			fin.close();
			bis.close();
			input.close();
		}
		catch(Exception e) {
			logger.severe("Error while reading file " + geotagsFile);
			e.printStackTrace();
			System.exit(-1);
		}
		long elapsed = System.currentTimeMillis() - startTime;
		logger.info(String.format("geotags for %d pages extracted in "+ ReadableTime.readableTime(elapsed) +"\n", pageCounter.getCount()));
	}

	/**
	 * Parses the input file containing the geo tags.
	 * @param inputStream The input file containing the geo tags.
	 * @throws Exception when something goes wrong while reading the geo tag file.
	 */
	private void parse(InputStream inputStream) throws Exception {
		BufferedReader bd  = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
		String line = "";
		while( (line = bd.readLine()) != null ) {
			Matcher matcher = GEO_TAGS_PATTERN.matcher(line);
			if ( matcher.find() ) {
				String geotags = matcher.group(1);
				Matcher matcher1 = GEO_TAG_PATTERN.matcher(geotags);
				while(matcher1.find()) {
					String wikiid = matcher1.group(2);
					String globe = matcher1.group(3);
					int primary = Integer.parseInt(matcher1.group(4));
					String latitude = matcher1.group(5);
					String longitude = matcher1.group(6);
					String type = matcher1.group(8);
					if ( primary != 1 || latitude.equals("NULL") || longitude.equals("NULL") )
						continue;
					if ( globe.length() == 0 )
						globe = null;
					if ( type.equals("NULL") || type.length() == 0)
						type = null;
					else {
						type = type.substring(1, type.length() - 1);
						if ( type.length() == 0 )
							type = null;
					}
					if ( this.geoTags.containsKey(wikiid) )
						continue;
					this.geoTags.put(wikiid, 
							new Geotags(globe, Double.parseDouble(latitude), Double.parseDouble(longitude), type));
					this.pageCounter.increment("Pages");
				}

			}

		}
		inputStream.close();
		bd.close();

	}

	/**
	 * Returns the geotags obtained by this thread.
	 * @return The geotags obtained by this thread.
	 */
	public Map<String, Geotags> getGeoTags() {
		return geoTags.size() > 0 ? this.geoTags : null;
	}

}
