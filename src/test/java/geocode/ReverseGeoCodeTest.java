package geocode;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;

/**
 * Unit tests for reverse-geocoding library
 * @author gdi
 * @since PORTAL R10 (2019-07)
 */
public class ReverseGeoCodeTest {

	private static final Set<String> COUNTRIES_TO_KEEP = new HashSet<String>(Arrays.asList(new String[] {"DE", "FR", "LU"}));

	private static final int CITIES_LUXEMBOURG_JULY_2019 = 1210;

	@Test
	public void testLoadLuTxtFile_all() throws URISyntaxException, FileNotFoundException, IOException {
		// Get input file
		final File inputTxtFile = new File(ReverseGeoCode.class.getClassLoader().getResource("LU.txt").toURI());
		Assert.assertNotNull(inputTxtFile);
		Assert.assertTrue(inputTxtFile.exists());
		Assert.assertTrue(inputTxtFile.isFile());

		// Init DB
		final ReverseGeoCode reverseGeoCodeUtil = new ReverseGeoCode(inputTxtFile.toPath(), false, COUNTRIES_TO_KEEP);
		Assert.assertNotNull(reverseGeoCodeUtil);
		Assert.assertNotNull(reverseGeoCodeUtil.kdTree);
		Assert.assertTrue(reverseGeoCodeUtil.nbCitiesLoaded > 1);
		System.out.println("Luxembourg LU.txt file => " + reverseGeoCodeUtil.nbCitiesLoaded + " cities loaded in-memory DB");
	}

	@Test
	public void testLoadLuTxtFile_major_countryFilter() throws URISyntaxException, FileNotFoundException, IOException {
		// Get input file
		final File inputTxtFile = new File(ReverseGeoCode.class.getClassLoader().getResource("LU.txt").toURI());
		Assert.assertNotNull(inputTxtFile);
		Assert.assertTrue(inputTxtFile.exists());
		Assert.assertTrue(inputTxtFile.isFile());

		// Init DB
		final ReverseGeoCode reverseGeoCodeUtil = new ReverseGeoCode(inputTxtFile.toPath(), true, COUNTRIES_TO_KEEP);
		Assert.assertNotNull(reverseGeoCodeUtil);
		Assert.assertNotNull(reverseGeoCodeUtil.kdTree);
		Assert.assertTrue(reverseGeoCodeUtil.nbCitiesLoaded > 1);
		// Expect all cities because of the country filter
		System.out.println("Luxembourg LU.txt file => " + reverseGeoCodeUtil.nbCitiesLoaded + " cities loaded in-memory DB");
	}

	@Test
	public void testLoadLuTxtFile_major_noCountry() throws URISyntaxException, FileNotFoundException, IOException {
		// Get input file
		final File inputTxtFile = new File(ReverseGeoCode.class.getClassLoader().getResource("LU.txt").toURI());
		Assert.assertNotNull(inputTxtFile);
		Assert.assertTrue(inputTxtFile.exists());
		Assert.assertTrue(inputTxtFile.isFile());

		// Init DB
		final ReverseGeoCode reverseGeoCodeUtil = new ReverseGeoCode(inputTxtFile.toPath(), true, new HashSet<String>());
		Assert.assertNotNull(reverseGeoCodeUtil);
		Assert.assertNotNull(reverseGeoCodeUtil.kdTree);
		Assert.assertTrue(reverseGeoCodeUtil.nbCitiesLoaded > 1);
		// Expect all cities because of the country filter
		Assert.assertTrue(CITIES_LUXEMBOURG_JULY_2019 > reverseGeoCodeUtil.nbCitiesLoaded);
		System.out.println("Luxembourg LU.txt # majors cities only # file => " + reverseGeoCodeUtil.nbCitiesLoaded + " cities loaded in-memory DB");
	}

	@Test
	public void testLoadLuZipFile() throws URISyntaxException, FileNotFoundException, IOException {
		// Get input file
		final File inputZipFile = new File(ReverseGeoCode.class.getClassLoader().getResource("LU.zip").toURI());
		Assert.assertNotNull(inputZipFile);
		Assert.assertTrue(inputZipFile.exists());
		Assert.assertTrue(inputZipFile.isFile());

		// Init DB
		final ReverseGeoCode reverseGeoCodeUtil = new ReverseGeoCode(inputZipFile.toPath(), false, COUNTRIES_TO_KEEP);
		Assert.assertNotNull(reverseGeoCodeUtil);
		Assert.assertNotNull(reverseGeoCodeUtil.kdTree);
		Assert.assertTrue(reverseGeoCodeUtil.nbCitiesLoaded > 1);
		System.out.println("Luxembourg LU.zip file => " + reverseGeoCodeUtil.nbCitiesLoaded + " cities loaded in-memory DB");
	}

	@Test
	public void testLoadCities1000_majorCities() throws URISyntaxException, FileNotFoundException, IOException {
		// Get input file
		final File inputZipFile = new File(ReverseGeoCode.class.getClassLoader().getResource("cities1000.zip").toURI());
		Assert.assertNotNull(inputZipFile);
		Assert.assertTrue(inputZipFile.exists());
		Assert.assertTrue(inputZipFile.isFile());

		// Init DB
		final ReverseGeoCode reverseGeoCodeUtil = new ReverseGeoCode(inputZipFile.toPath(), true, COUNTRIES_TO_KEEP);
		Assert.assertNotNull(reverseGeoCodeUtil);
		Assert.assertNotNull(reverseGeoCodeUtil.kdTree);
		Assert.assertTrue(reverseGeoCodeUtil.nbCitiesLoaded > 1);
		System.out.println("Cities1000.zip # majors cities only # file => " + reverseGeoCodeUtil.nbCitiesLoaded + " cities loaded in-memory DB");
	}

	@Test
	public void testLoadCities1000_allCities() throws URISyntaxException, FileNotFoundException, IOException {
		// Get input file
		final String inputZipFilePath = new File(ReverseGeoCode.class.getClassLoader().getResource("cities1000.zip").toURI()).toString();
		final Path inputZipFile = Paths.get(inputZipFilePath);
		Assert.assertNotNull(inputZipFile);
		Assert.assertTrue(Files.exists(inputZipFile));
		Assert.assertFalse(Files.isDirectory(inputZipFile));

		// Init DB
		final ReverseGeoCode reverseGeoCodeUtil = new ReverseGeoCode(inputZipFile, false, COUNTRIES_TO_KEEP);
		Assert.assertNotNull(reverseGeoCodeUtil);
		Assert.assertNotNull(reverseGeoCodeUtil.kdTree);
		Assert.assertTrue(reverseGeoCodeUtil.nbCitiesLoaded > 1);
		System.out.println("Cities1000.zip # all cities # file => " + reverseGeoCodeUtil.nbCitiesLoaded + " cities loaded in-memory DB");
	}

	@Test
	public void testLoadCities1000_and_Luxembourg_allCities() throws URISyntaxException, FileNotFoundException, IOException {
		// Get input file
		final File cities1000 = new File(ReverseGeoCode.class.getClassLoader().getResource("cities1000.zip").toURI());
		final File luxembourg = new File(ReverseGeoCode.class.getClassLoader().getResource("LU.zip").toURI());
		final List<Path> files = Arrays.asList(cities1000.toPath(), luxembourg.toPath());

		// Init DB
		final ReverseGeoCode reverseGeoCodeUtil = new ReverseGeoCode(files, false, COUNTRIES_TO_KEEP);
		Assert.assertNotNull(reverseGeoCodeUtil);
		Assert.assertNotNull(reverseGeoCodeUtil.kdTree);
		Assert.assertTrue(reverseGeoCodeUtil.nbCitiesLoaded > 1);
		System.out.println("Cities1000.zip + LU.zip # all cities # 2 files => " + reverseGeoCodeUtil.nbCitiesLoaded + " cities loaded in-memory DB");
	}

	@Test
	public void testLoadCities1000_and_Luxembourg_allCities_and_BE_majors() throws URISyntaxException, FileNotFoundException, IOException {
		// Get input file
		final File cities1000 = new File(ReverseGeoCode.class.getClassLoader().getResource("cities1000.zip").toURI());
		final File luxembourg = new File(ReverseGeoCode.class.getClassLoader().getResource("LU.zip").toURI());
		final File belgium = new File(ReverseGeoCode.class.getClassLoader().getResource("BE.zip").toURI());
		final List<Path> files = Arrays.asList(cities1000.toPath(), luxembourg.toPath(), belgium.toPath());

		// Init DB
		final ReverseGeoCode reverseGeoCodeUtil = new ReverseGeoCode(files, true, COUNTRIES_TO_KEEP);
		Assert.assertNotNull(reverseGeoCodeUtil);
		Assert.assertNotNull(reverseGeoCodeUtil.kdTree);
		Assert.assertTrue(reverseGeoCodeUtil.nbCitiesLoaded > 1);
		System.out.println("Cities1000.zip + LU.zip + BE.zip # all cities # 3 files => " + reverseGeoCodeUtil.nbCitiesLoaded + " cities loaded in-memory DB");
	}


	@Test
	public void testReverseGeoLocation() throws URISyntaxException, IOException {
		// Get input file
		final File cities1000 = new File(ReverseGeoCode.class.getClassLoader().getResource("cities1000.zip").toURI());
		final List<Path> files = Arrays.asList(cities1000.toPath());

		// Init DB
		final ReverseGeoCode reverseGeoCodeUtil = new ReverseGeoCode(files, false, COUNTRIES_TO_KEEP);
		Assert.assertNotNull(reverseGeoCodeUtil);
		Assert.assertNotNull(reverseGeoCodeUtil.kdTree);
		Assert.assertTrue(reverseGeoCodeUtil.nbCitiesLoaded > 1);

		// expect: Luxembourg [LU]
		double latitude = 49.615267;
		double longitude = 6.120112;
		GeoName closestCity = reverseGeoCodeUtil.nearestPlace(latitude, longitude);
		Assert.assertNotNull(closestCity);
		Assert.assertEquals("Luxembourg [LU]", closestCity.toString());

		// Following coordinates are LuxTrust headquarters on 2019-07-22
		// expect: Capellen [LU]
		latitude = 49.642314;
		longitude = 6.007225;
		closestCity = reverseGeoCodeUtil.nearestPlace(latitude, longitude);
		Assert.assertNotNull(closestCity);
		Assert.assertEquals("Capellen [LU]", closestCity.toString());

		// Following coordinates are in France
		latitude = 46.033934;
		longitude = 3.917356;
		closestCity = reverseGeoCodeUtil.nearestPlace(latitude, longitude);
		Assert.assertNotNull(closestCity);
		Assert.assertEquals("Renaison [FR]", closestCity.toString());
	}
}
