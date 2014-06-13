package geocode;

import geocode.kdtree.KDTree;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

/**
 * Created by Daniel Glasson on 18/05/2014.
 * Uses KD-trees to quickly find the nearest point
 * 
 * ReverseGeoCode reverseGeoCode = new ReverseGeoCode();
 * reverseGeoCode.loadPlaceNames("placenames.txt", true);
 * System.out.println("Nearest to -123.456, 123.456 is " + reverseGeoCode.nearestMajorPlaceName(-123.456, 123.456));
 */
public class ReverseGeoCode {
    KDTree<GeoName> kdTree;
    
    // Get placenames from http://download.geonames.org/export/dump/
    public void loadPlaceNames( String placeNamesFile, Boolean majorOnly ) throws IOException {
        ArrayList<GeoName> arPlaceNames;
        arPlaceNames = new ArrayList<GeoName>();
        // Read the geonames file in the directory
        BufferedReader in = new BufferedReader(new FileReader(placeNamesFile));
        String str;
        try {
            while ((str = in.readLine()) != null) {
                GeoName newPlace = new GeoName(str);
                if ( !majorOnly || newPlace.majorPlace ) {
                    arPlaceNames.add(new GeoName(str));
                }
            }
        } catch (IOException ex) {
            in.close(); 
            throw ex;
        }
        finally {
            in.close();
        }
        
        kdTree = new KDTree<GeoName>(arPlaceNames.toArray(new GeoName[arPlaceNames.size()]));
    }
    
    public String nearestMajorPlaceName(double latitude, double longitude) {
        return kdTree.findNearest(new GeoName(latitude,longitude)).name;
    }
}
