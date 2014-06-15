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

import geocode.kdtree.KDNodeComparator;
import static java.lang.Math.cos;
import static java.lang.Math.sin;
import static java.lang.Math.toRadians;

import java.util.Comparator;

/**
 * Created by Daniel Glasson on 18/05/2014.
 * This class works with a placenames files from http://download.geonames.org/export/dump/
 */

public class GeoName extends KDNodeComparator<GeoName> {
    public String name;
    public boolean majorPlace; // Major or minor place
    public double latitude;
    public double longitude;
    public double point[] = new double[3]; // The 3D coordinates of the point
    public String country;

    GeoName(String data) {
        String[] names = data.split("\t");
        name = names[1];
        majorPlace = names[6].equals("P");
        latitude = Double.parseDouble(names[4]);
        longitude = Double.parseDouble(names[5]);
        setPoint();
        country = names[8];
    }

    GeoName(Double latitude, Double longitude) {
        name = country = "Search";
        this.latitude = latitude;
        this.longitude = longitude;
        setPoint();
    }

    private void setPoint() {
        point[0] = cos(toRadians(latitude)) * cos(toRadians(longitude));
        point[1] = cos(toRadians(latitude)) * sin(toRadians(longitude));
        point[2] = sin(toRadians(latitude));
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    protected Double squaredDistance(Object other) {
        GeoName location = (GeoName)other;
        double x = this.point[0] - location.point[0];
        double y = this.point[1] - location.point[1];
        double z = this.point[2] - location.point[2];
        return (x*x) + (y*y) + (z*z);
    }

    @Override
    protected Double axisSquaredDistance(Object other, Integer axis) {
        GeoName location = (GeoName)other;
        Double distance = point[axis] - location.point[axis];
        return distance * distance;
    }

    @Override
    protected Comparator<GeoName> getComparator(Integer axis) {
        return GeoNameComparator.values()[axis];
    }

    protected static enum GeoNameComparator implements Comparator<GeoName> {
        x {
            @Override
            public int compare(GeoName a, GeoName b) {
                return Double.compare(a.point[0], b.point[0]);
            }
        },
        y {
            @Override
            public int compare(GeoName a, GeoName b) {
                return Double.compare(a.point[1], b.point[1]);
            }
        },
        z {
            @Override
            public int compare(GeoName a, GeoName b) {
                return Double.compare(a.point[2], b.point[2]);
            }
        };
    }
}
