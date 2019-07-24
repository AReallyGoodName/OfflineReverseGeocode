/*
The MIT License (MIT)
[OSI Approved License]
The MIT License (MIT)

Copyright (c) 2014 Daniel Glasson

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
THE SOFTWARE.
 */

package geocode;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import geocode.kdtree.KDTree;

/**
 *
 * Created by Daniel Glasson on 18/05/2014.<br>
 * Uses KD-trees to quickly find the nearest point</p>
 * 
 * <code>
 * ReverseGeoCode reverseGeoCode = new ReverseGeoCode(new FileInputStream("c:\\AU.txt"), true);<br>
 * System.out.println("Nearest to -23.456, 123.456 is " + geocode.nearestPlace(-23.456, 123.456));
 * </code>
 * 
 * <br>
 * Fork by Guillaume Diaz on 22/07/2019:
 * <ul>
 * <li>Keep all cities from some countries (optional param, ex: {FR,BE,LU,DE})</li>
 * <li>Load multiple files at once</li>
 * <li>Singleton pattern, if needed</li>
 * </ul>
 * @author Daniel Glasson (18/05/2014)
 * @author Guillaume Diaz (22/07/2019)
 * @version 2.0
 */
public class ReverseGeoCode {

	private static ReverseGeoCode instance;

	// Requirement: get placenames from http://download.geonames.org/export/dump/
	// 2019-07-22: take "cities1000.zip" + "LU.zip" to get all cities with 1000 inhabitants and Luxembourg country in full

	/**
	 * @return current instance if it has been initialised, else NULL
	 */
	public static ReverseGeoCode getInstance() {
		return instance;
	}

	/**
	 * To set the singleton instance to use
	 * @param instance reference to the instance to use
	 */
	private static synchronized void setInstance(final ReverseGeoCode instance) {
		ReverseGeoCode.instance = instance;
	}

	final KDTree<GeoName> kdTree;
	final int nbCitiesLoaded;

	/**
	 * To parse the geonames file(s).
	 * @param geoFiles Geoname file(s) downloaded from http://download.geonames.org/export/dump/; can not be null.
	 * @param majorOnly only include major cities in KD-tree - except if you ask to keep all cities from particular countries.
	 * @param countriesToKeep list of countries code (2 letters) to keep. All cities belong to these countries will be keep in-memory
	 * @throws IOException if there is a problem reading some file(s)
	 */
	public ReverseGeoCode(final List<Path> geoFiles, final boolean majorOnly, final Set<String> countriesToKeep) throws IOException {
		if (geoFiles == null) {
			throw new IllegalArgumentException("You must provide a valid set of geoname file(s)");
		}

		// Get cities
		final Set<GeoName> cities = new HashSet<>();
		for (final Path file : geoFiles) {
			cities.addAll(loadFileContent(file, majorOnly, countriesToKeep));
		}

		// Populate 3D in-memory DB
		kdTree = new KDTree<GeoName>(new ArrayList<GeoName>(cities));
		nbCitiesLoaded = cities.size();

		// Register local instance as the 'big one' => singleton
		setInstance(this);
	}

	/**
	 * To parse the geonames file.
	 * @param geoFiles Geoname files downloaded from http://download.geonames.org/export/dump/; can not be null.
	 * @param majorOnly only include major cities in KD-tree - except if you ask to keep all cities from particular countries.
	 * @param countriesToKeep list of countries code (2 letters) to keep. All cities belong to these countries will be keep in-memory
	 * @throws IOException if there is a problem reading some file(s)
	 */
	public ReverseGeoCode(final Path geoFile, final boolean majorOnly, final Set<String> countriesToKeep) throws IOException {
		this(Arrays.asList(geoFile), majorOnly, countriesToKeep);
	}

	/**
	 * To load the geoname file content into memory, as a cities dictionary
	 * @param geoFile file to load
	 * @param majorOnly only include major cities in KD-tree - except if you ask to keep all cities from particular countries.
	 * @param countriesToKeep list of countries code (2 letters) to keep. All cities belong to these countries will be keep in-memory
	 * @return cities dictionary
	 * @throws IOException cannot read file
	 */
	private Set<GeoName> loadFileContent(final Path geoFile, final boolean majorOnly, final Set<String> countriesToKeep) throws IOException {
		// arg check
		if (geoFile == null || Files.notExists(geoFile)) {
			throw new IllegalArgumentException("You must provide a valid geofile. Cannot access: " + geoFile);
		}

		// Open the GeoFile
		final boolean isZip = FileUtils.isZipFile(geoFile);
		if (isZip) {
			return parseZipFile(geoFile, majorOnly, countriesToKeep);
		} else {
			try (final InputStream fis = Files.newInputStream(geoFile)) {
				return parseContent(fis , majorOnly, countriesToKeep);
			}
		}
	}

	/**
	 * To parse the content of a Geo ZIP file
	 * @param geoFile ZIP file analyse
	 * @param majorOnly only include major cities in KD-tree.
	 * @param countriesToKeep list of countries code (2 letters) to keep. All cities belong to these countries will be keep in-memory
	 * @return cities description
	 * @throws IOException failed to parse file
	 */
	private Set<GeoName> parseZipFile(final Path geoFile, final boolean majorOnly, final Set<String> countriesToKeep) throws IOException {
		final Set<GeoName> cities = new HashSet<>();

		// Open ZIP archive
		try (final FileInputStream zipFIS = new FileInputStream(geoFile.toFile());
				final BufferedInputStream zipBIS = new BufferedInputStream(zipFIS);
				final ZipInputStream zipIS = new ZipInputStream(zipBIS)) {

			ZipEntry entry;
			// for each item...
			while ((entry = zipIS.getNextEntry()) != null) {
				// Skip readme file
				if (!entry.getName().equalsIgnoreCase("readme.txt")) {
					// Parse file content
					final Set<GeoName> fileCities = parseContent(zipIS, majorOnly, countriesToKeep);
					// Update global list
					cities.addAll(fileCities);
				}
			}
		}
		return cities;
	}

	/**
	 * To parse the content of a Geo file
	 * @param entry TXT file or ZIP entry to analyse
	 * @param majorOnly only include major cities in KD-tree.
	 * @param countriesToKeep list of countries code (2 letters) to keep. All cities belong to these countries will be keep in-memory
	 * @return cities description
	 * @throws IOException failed to parse file
	 */
	private Set<GeoName> parseContent(final InputStream entry, final boolean majorOnly, final Set<String> countriesToKeep) throws IOException {
		final Set<GeoName> places = new HashSet<>();

		// Read file
		// (i) do not close the input stream: this will be done later on by the caller
		final BufferedReader in = new BufferedReader(new InputStreamReader(entry));
		String line;
		while ((line = in.readLine()) != null) {
			// register place, if required
			final GeoName newPlace = new GeoName(line);
			if (shouldAddPlaceToInMemoryDB(newPlace, majorOnly, countriesToKeep)) {
				places.add(newPlace);
			}
		}
		return places;
	}

	/** 
	 * to check if the place should be added or not to the in-memory database
	 * @param newPlace place to analyse (= entry from the TXT or ZIP file)
	 * @param majorOnly GeoName city's flag. 'true' to keep only major cities for countries that are not in the 'countriesToKeep' list
	 * @param countriesToKeep optional list of countries to not filter
	 * @return 'true' if the city should be added to the in-memory database
	 */
	private boolean shouldAddPlaceToInMemoryDB(final GeoName newPlace, final boolean majorOnly, final Set<String> countriesToKeep) {
		// keep places belonging to particular countries
		if (countriesToKeep != null && newPlace.country!= null && countriesToKeep.contains(newPlace.country)) {
			return true;
		}

		// keep only major places?
		if (!majorOnly || newPlace.majorPlace ) {
			return true;
		}
		return false;
	}

	/**
	 * To find the nearest place to the search latitude / longitude
	 * @param latitude latitude
	 * @param longitude longitude
	 * @return closest city
	 */
	public GeoName nearestPlace(final double latitude, final double longitude) {
		return kdTree.findNearest(new GeoName(latitude,longitude));
	}
}
