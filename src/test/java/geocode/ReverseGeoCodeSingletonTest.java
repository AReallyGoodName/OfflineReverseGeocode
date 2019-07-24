package geocode;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class ReverseGeoCodeSingletonTest {

	private static final Set<String> COUNTRIES_TO_KEEP = new HashSet<String>(Arrays.asList(new String[] {"BE", "DE", "FR", "LU"}));

	@BeforeClass
	public static void setup() throws FileNotFoundException, URISyntaxException, Exception {
		final File cities1000 = new File(ReverseGeoCode.class.getClassLoader().getResource("cities1000.zip").toURI());
		new ReverseGeoCode(cities1000.toPath(), false, COUNTRIES_TO_KEEP);
	}

	@Test
	public void testLoadCities1000() throws URISyntaxException, FileNotFoundException, IOException {
		// Check DB
		Assert.assertNotNull(ReverseGeoCode.getInstance());
		Assert.assertNotNull(ReverseGeoCode.getInstance().kdTree);
		Assert.assertTrue(ReverseGeoCode.getInstance().nbCitiesLoaded > 1);
		System.out.println("Cities1000.zip # majors cities only # file => " + ReverseGeoCode.getInstance().nbCitiesLoaded + " cities loaded in-memory DB");
	}

	@Test
	public void testReverseGeoLocation() {
		// expect: Luxembourg [LU]
		double latitude = 49.615267;
		double longitude = 6.120112;
		GeoName closestCity = ReverseGeoCode.getInstance().nearestPlace(latitude, longitude);
		Assert.assertNotNull(closestCity);
		Assert.assertEquals("Luxembourg [LU]", closestCity.toString());

		// Following coordinates are LuxTrust headquarters on 2019-07-22
		// expect: Capellen [LU]
		latitude = 49.642314;
		longitude = 6.007225;
		closestCity = ReverseGeoCode.getInstance().nearestPlace(latitude, longitude);
		Assert.assertNotNull(closestCity);
		Assert.assertEquals("Capellen [LU]", closestCity.toString());

		// Following coordinates are in France
		latitude = 46.033934;
		longitude = 3.917356;
		closestCity = ReverseGeoCode.getInstance().nearestPlace(latitude, longitude);
		Assert.assertNotNull(closestCity);
		Assert.assertEquals("Renaison [FR]", closestCity.toString());
	}
}
