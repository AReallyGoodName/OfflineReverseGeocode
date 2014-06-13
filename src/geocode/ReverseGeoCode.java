/*
This library is free software; you can redistribute it and/or modify it under
the terms of the GNU Lesser General Public License as published by the Free
Software Foundation; either version 2.1 of the License, or (at your option)
any later version.
This library is distributed in the hope that it will be useful, but WITHOUT
ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
details.
You should have received a copy of the GNU Lesser General Public License
along with this library; if not, write to the Free Software Foundation, Inc.,
59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
*/

package geocode;

import geocode.kdtree.KDTree;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

/**
 *
 * Created by Daniel Glasson on 18/05/2014.
 * Uses KD-trees to quickly find the nearest point
 * 
 * ReverseGeoCode reverseGeoCode = new ReverseGeoCode("placenames.txt", true);
 * System.out.println("Nearest to -123.456, 123.456 is " + reverseGeoCode.nearestMajorPlaceName(-123.456, 123.456));
 */
public class ReverseGeoCode {
    KDTree<GeoName> kdTree;
    
    // Get placenames from http://download.geonames.org/export/dump/
    public ReverseGeoCode( String placeNamesFile, Boolean majorOnly ) throws IOException {
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
        in.close();
        kdTree = new KDTree<GeoName>(arPlaceNames.toArray(new GeoName[arPlaceNames.size()]));
    }
    
    public String nearestMajorPlaceName(double latitude, double longitude) {
        return kdTree.findNearest(new GeoName(latitude,longitude)).name;
    }
}
