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

As a special exception to the GNU Lesser General Public License version 2.1, you
may convey to a third party an executable file from a Combined Work that links,
statically or dynamically, portions of this Library in the executable file,
conveying the Minimal Corresponding Source but without the need to convey the
Corresponding Application Code under section 4d0 of the GNU Lesser General Public
License, so long as you are using an unmodified publicly distributed version of
the Library. This exception does not invalidate any other reasons why the
executable file might be covered by the GNU Lesser General Public License or the
GNU General Public License.
*/

package geocode;

import geocode.kdtree.KDTree;
import java.io.*;
import java.util.ArrayList;

/**
 *
 * Created by Daniel Glasson on 18/05/2014.
 * Uses KD-trees to quickly find the nearest point
 * 
 * ReverseGeoCode reverseGeoCode = new ReverseGeoCode(new FileInputStream("c:\\AU.txt"), true);
 * System.out.println("Nearest to -23.456, 123.456 is " + geocode.nearestPlace(-23.456, 123.456););
 */
public class ReverseGeoCode {
    KDTree<GeoName> kdTree;
    
    // Get placenames from http://download.geonames.org/export/dump/
    public ReverseGeoCode( InputStream placenames, Boolean majorOnly ) throws IOException {
        ArrayList<GeoName> arPlaceNames;
        arPlaceNames = new ArrayList<GeoName>();
        // Read the geonames file in the directory
        BufferedReader in = new BufferedReader(new InputStreamReader(placenames));
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
        kdTree = new KDTree<GeoName>(arPlaceNames);
    }

    public GeoName nearestPlace(double latitude, double longitude) {
        return kdTree.findNearest(new GeoName(latitude,longitude));
    }

    // Simple test
    public static void main(String [] args)
    {
        try {
            ReverseGeoCode geocode = new ReverseGeoCode(new FileInputStream("c:\\allCountries.txt"), true);
            System.out.println(System.nanoTime());
            GeoName ret = geocode.nearestPlace(-33.404644, 150.384833);
            System.out.println(System.nanoTime());
            System.out.println("Place is " + ret + " at " + ret.latitude + ", " + ret.longitude + " dist " + ret.squaredDistance(new GeoName(-33.404644, 150.384833)));

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
