package eu.daxiongmao.geocode;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class ReverseGeoCodeSingletonTest {

	private static final Set<String> COUNTRIES_TO_KEEP = new HashSet<String>(Arrays.asList(new String[] {"BE", "DE", "FR", "LU"}));

	@BeforeAll
	public static void setUp() throws FileNotFoundException, URISyntaxException, Exception {
		final File cities1000 = new File(ReverseGeoCode.class.getClassLoader().getResource("cities1000.zip").toURI());
		new ReverseGeoCode(cities1000.toPath(), false, COUNTRIES_TO_KEEP);
	}

	@Test
	public void testLoadCities1000() throws URISyntaxException, FileNotFoundException, IOException {
		// Check DB
		Assertions.assertNotNull(ReverseGeoCode.getInstance());
		Assertions.assertNotNull(ReverseGeoCode.getInstance().kdTree);
		Assertions.assertTrue(ReverseGeoCode.getInstance().nbCitiesLoaded > 1);
		System.out.println("Cities1000.zip # majors cities only # file => " + ReverseGeoCode.getInstance().nbCitiesLoaded + " cities loaded in-memory DB");
	}

	@Test
	public void testReverseGeoLocation() {
		// expect: Luxembourg [LU]
		double latitude = 49.615267;
		double longitude = 6.120112;
		GeoName closestCity = ReverseGeoCode.getInstance().nearestPlace(latitude, longitude);
		Assertions.assertNotNull(closestCity);
		Assertions.assertEquals("Luxembourg [LU]", closestCity.toString());

		// expect: Capellen [LU]
		latitude = 49.642314;
		longitude = 6.007225;
		closestCity = ReverseGeoCode.getInstance().nearestPlace(latitude, longitude);
		Assertions.assertNotNull(closestCity);
		Assertions.assertEquals("Capellen [LU]", closestCity.toString());

		// Following coordinates are in France
		latitude = 46.033934;
		longitude = 3.917356;
		closestCity = ReverseGeoCode.getInstance().nearestPlace(latitude, longitude);
		Assertions.assertNotNull(closestCity);
		Assertions.assertEquals("Renaison [FR]", closestCity.toString());
	}
}
