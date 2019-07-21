package geocode;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.zip.ZipInputStream;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Unit tests for reverse-geocoding library
 * @author gdi
 * @since PORTAL R10 (2019-07)
 */
public class ReverseGeoCodeTest {

	private static final Set<String> COUNTRIES_TO_KEEP = new HashSet<String>(Arrays.asList(new String[] {"BE", "DE", "FR", "LU"}));

	private static final int CITIES_LUXEMBOURG_JULY_2019 = 1210;

	@Test
	public void testLoadLuTxtFile_all() throws URISyntaxException, FileNotFoundException, IOException {
		// Get input file
		final File inputTxtFile = new File(ReverseGeoCode.class.getClassLoader().getResource("LU.txt").toURI());
		Assert.assertNotNull(inputTxtFile);
		Assert.assertTrue(inputTxtFile.exists());
		Assert.assertTrue(inputTxtFile.isFile());

		// Init DB
		final ReverseGeoCode reverseGeoCodeUtil = new ReverseGeoCode(new FileInputStream(inputTxtFile), false, COUNTRIES_TO_KEEP);
		Assert.assertNotNull(reverseGeoCodeUtil);
		Assert.assertNotNull(reverseGeoCodeUtil.kdTree);
		Assert.assertTrue(reverseGeoCodeUtil.getNbCitiesLoaded() > 1);
		Assert.assertEquals(CITIES_LUXEMBOURG_JULY_2019, reverseGeoCodeUtil.getNbCitiesLoaded());
		System.out.println("Luxembourg LU.txt file => " + reverseGeoCodeUtil.getNbCitiesLoaded() + " cities loaded in-memory DB");
	}

	@Test
	public void testLoadLuTxtFile_major_countryFilter() throws URISyntaxException, FileNotFoundException, IOException {
		// Get input file
		final File inputTxtFile = new File(ReverseGeoCode.class.getClassLoader().getResource("LU.txt").toURI());
		Assert.assertNotNull(inputTxtFile);
		Assert.assertTrue(inputTxtFile.exists());
		Assert.assertTrue(inputTxtFile.isFile());

		// Init DB
		final ReverseGeoCode reverseGeoCodeUtil = new ReverseGeoCode(new FileInputStream(inputTxtFile), true, COUNTRIES_TO_KEEP);
		Assert.assertNotNull(reverseGeoCodeUtil);
		Assert.assertNotNull(reverseGeoCodeUtil.kdTree);
		Assert.assertTrue(reverseGeoCodeUtil.getNbCitiesLoaded() > 1);
		// Expect all cities because of the country filter
		Assert.assertEquals(CITIES_LUXEMBOURG_JULY_2019, reverseGeoCodeUtil.getNbCitiesLoaded());
		System.out.println("Luxembourg LU.txt file => " + reverseGeoCodeUtil.getNbCitiesLoaded() + " cities loaded in-memory DB");
	}

	@Test
	public void testLoadLuTxtFile_major_noCountry() throws URISyntaxException, FileNotFoundException, IOException {
		// Get input file
		final File inputTxtFile = new File(ReverseGeoCode.class.getClassLoader().getResource("LU.txt").toURI());
		Assert.assertNotNull(inputTxtFile);
		Assert.assertTrue(inputTxtFile.exists());
		Assert.assertTrue(inputTxtFile.isFile());

		// Init DB
		final ReverseGeoCode reverseGeoCodeUtil = new ReverseGeoCode(new FileInputStream(inputTxtFile), true, new HashSet<String>());
		Assert.assertNotNull(reverseGeoCodeUtil);
		Assert.assertNotNull(reverseGeoCodeUtil.kdTree);
		Assert.assertTrue(reverseGeoCodeUtil.getNbCitiesLoaded() > 1);
		// Expect all cities because of the country filter
		Assert.assertTrue(CITIES_LUXEMBOURG_JULY_2019 > reverseGeoCodeUtil.getNbCitiesLoaded());
		System.out.println("Luxembourg LU.txt # majors cities only # file => " + reverseGeoCodeUtil.getNbCitiesLoaded() + " cities loaded in-memory DB");
	}

	@Test
	public void testLoadLuZipFile() throws URISyntaxException, FileNotFoundException, IOException {
		// Get input file
		final File inputZipFile = new File(ReverseGeoCode.class.getClassLoader().getResource("LU.zip").toURI());
		Assert.assertNotNull(inputZipFile);
		Assert.assertTrue(inputZipFile.exists());
		Assert.assertTrue(inputZipFile.isFile());

		// Init DB
		final ReverseGeoCode reverseGeoCodeUtil = new ReverseGeoCode(new ZipInputStream(new FileInputStream(inputZipFile)), false, COUNTRIES_TO_KEEP);
		Assert.assertNotNull(reverseGeoCodeUtil);
		Assert.assertNotNull(reverseGeoCodeUtil.kdTree);
		Assert.assertTrue(reverseGeoCodeUtil.getNbCitiesLoaded() > 1);
		Assert.assertEquals(CITIES_LUXEMBOURG_JULY_2019, reverseGeoCodeUtil.getNbCitiesLoaded());
		System.out.println("Luxembourg LU.zip file => " + reverseGeoCodeUtil.getNbCitiesLoaded() + " cities loaded in-memory DB");
	}

	@Test
	public void testLoadCities1000_majorCities() throws URISyntaxException, FileNotFoundException, IOException {
		// Get input file
		final File inputZipFile = new File(ReverseGeoCode.class.getClassLoader().getResource("cities1000.zip").toURI());
		Assert.assertNotNull(inputZipFile);
		Assert.assertTrue(inputZipFile.exists());
		Assert.assertTrue(inputZipFile.isFile());

		// Init DB
		final ReverseGeoCode reverseGeoCodeUtil = new ReverseGeoCode(new ZipInputStream(new FileInputStream(inputZipFile)), true, COUNTRIES_TO_KEEP);
		Assert.assertNotNull(reverseGeoCodeUtil);
		Assert.assertNotNull(reverseGeoCodeUtil.kdTree);
		Assert.assertTrue(reverseGeoCodeUtil.getNbCitiesLoaded() > 1);
		System.out.println("Cities1000.zip # majors cities only # file => " + reverseGeoCodeUtil.getNbCitiesLoaded() + " cities loaded in-memory DB");
	}

	@Test
	public void testLoadCities1000_allCities() throws URISyntaxException, FileNotFoundException, IOException {
		// Get input file
		final File inputZipFile = new File(ReverseGeoCode.class.getClassLoader().getResource("cities1000.zip").toURI());
		Assert.assertNotNull(inputZipFile);
		Assert.assertTrue(inputZipFile.exists());
		Assert.assertTrue(inputZipFile.isFile());

		// Init DB
		final ReverseGeoCode reverseGeoCodeUtil = new ReverseGeoCode(new ZipInputStream(new FileInputStream(inputZipFile)), false, COUNTRIES_TO_KEEP);
		Assert.assertNotNull(reverseGeoCodeUtil);
		Assert.assertNotNull(reverseGeoCodeUtil.kdTree);
		Assert.assertTrue(reverseGeoCodeUtil.getNbCitiesLoaded() > 1);
		System.out.println("Cities1000.zip # all cities # file => " + reverseGeoCodeUtil.getNbCitiesLoaded() + " cities loaded in-memory DB");
	}

	@Ignore
	@Test
	public void testReverseGeoLocation() {
		// Following coordinate are based on LuxTrust headquarters on 2019-07-22
		// data is coming from LuxTrust mobile app Android v3.0.0
		// TODO
	}
}
