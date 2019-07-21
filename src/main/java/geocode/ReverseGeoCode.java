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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
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
 * Fork by Guillaume Diaz for LuxTrust on 22/07/2019:<br>
 * Keep all cities from some countries: FR / BE / LU / DE 
 */
public class ReverseGeoCode {

	private static ReverseGeoCode instance;

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

	KDTree<GeoName> kdTree;
	int nbCitiesLoaded = 0;

	// Get placenames from http://download.geonames.org/export/dump/
	// 2019-07-22: take "cities1000.zip" to get all cities with 1000 inhabitants or more

	/**
	 * Parse the zipped geonames file.
	 * @param zippedPlacednames a {@link ZipInputStream} zip file downloaded from http://download.geonames.org/export/dump/; can not be null.
	 * @param majorOnly only include major cities in KD-tree.
	 * @param countriesToKeep list of countries code (2 letters) to keep. All cities belong to these countries will be keep in-memory
	 * @throws IOException if there is a problem reading the {@link ZipInputStream}.
	 * @throws NullPointerException if zippedPlacenames is {@code null}.
	 */
	public ReverseGeoCode(final ZipInputStream zippedPlacednames, final boolean majorOnly, final Set<String> countriesToKeep) throws IOException {
		//depending on which zip file is given, country specific zip files have read me files that we should ignore
		ZipEntry entry;
		do {
			entry = zippedPlacednames.getNextEntry();
		} while(entry.getName().equals("readme.txt"));

		createKdTree(zippedPlacednames, majorOnly, countriesToKeep);
	}

	/**
	 * Parse the raw text geonames file.
	 * @param placenames the text file downloaded from http://download.geonames.org/export/dump/; can not be null.
	 * @param majorOnly only include major cities in KD-tree.
	 * @param countriesToKeep list of countries code (2 letters) to keep. All cities belong to these countries will be keep in-memory
	 * 
	 * @throws IOException if there is a problem reading the stream.
	 * @throws NullPointerException if zippedPlacenames is {@code null}.
	 */
	public ReverseGeoCode(final InputStream placenames, final boolean majorOnly, final Set<String> countriesToKeep) throws IOException {
		createKdTree(placenames, majorOnly, countriesToKeep);
	}

	private void createKdTree(final InputStream placenames, final boolean majorOnly, final Set<String> countriesToKeep) throws IOException {
		final ArrayList<GeoName> arPlaceNames = new ArrayList<GeoName>();

		// Read the geonames file in the directory
		int citiesLoaded = 0;
		try (final BufferedReader in = new BufferedReader(new InputStreamReader(placenames))) {
			String str;
			while ((str = in.readLine()) != null) {
				final GeoName newPlace = new GeoName(str);
				// 2019-07-22: LuxTrust fork
				if (addPlaceToInMemoryDatabase(newPlace, majorOnly, countriesToKeep)) {
					arPlaceNames.add(newPlace);
					citiesLoaded++;
				}
			}
		} catch (final IOException ex) {
			throw ex;
		}
		// Bind in-memory DB to local instance
		kdTree = new KDTree<GeoName>(arPlaceNames);
		nbCitiesLoaded = citiesLoaded;
		// Register local instance as the 'big one' => singleton
		setInstance(this);
	}

	/** 
	 * to check if the place should be added or not to the in-memory database.<br>
	 * LuxTrust change - 2019/07/22
	 * @param newPlace place to analyse (= entry from the "cities1000" file)
	 * @param majorOnly GeoName city's flag. 'true' if the city is considered as major
	 * @param countriesToKeep optional list of countries to not filter
	 * @return 'true' if the city should be added to the in-memory database
	 */
	private boolean addPlaceToInMemoryDatabase(final GeoName newPlace, final boolean majorOnly, final Set<String> countriesToKeep) {
		// keep places belonging to particular countries
		if (countriesToKeep != null && newPlace.country!= null && countriesToKeep.contains(newPlace.country)) {
			return true;
		}

		// keep only major places?
		if ( !majorOnly || newPlace.majorPlace ) {
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

	/**
	 * @return size of the in-memory database = number of cities loaded in memory
	 */
	public int getNbCitiesLoaded() {
		return nbCitiesLoaded;
	}
}
