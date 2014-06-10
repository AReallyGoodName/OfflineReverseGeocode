import javafx.geometry.Point3D;

import java.io.PrintWriter;
import java.lang.*;
import java.net.Socket;
import java.util.Comparator;

import static java.lang.Math.cos;
import static java.lang.Math.sin;

/* 
The main 'geoname' table has the following fields :
---------------------------------------------------
geonameid         : integer id of record in geonames database
name              : name of geographical point (utf8) varchar(200)
asciiname         : name of geographical point in plain ascii characters, varchar(200)
alternatenames    : alternatenames, comma separated varchar(5000)
latitude          : latitude in decimal degrees (wgs84)
longitude         : longitude in decimal degrees (wgs84)
feature class     : see http://www.geonames.org/export/codes.html, char(1)
feature code      : see http://www.geonames.org/export/codes.html, varchar(10)
country code      : ISO-3166 2-letter country code, 2 characters
cc2               : alternate country codes, comma separated, ISO-3166 2-letter country code, 60 characters
admin1 code       : fipscode (subject to change to iso code), see exceptions below, see file admin1Codes.txt for display names of this code; varchar(20)
admin2 code       : code for the second administrative division, a county in the US, see file admin2Codes.txt; varchar(80) 
admin3 code       : code for third level administrative division, varchar(20)
admin4 code       : code for fourth level administrative division, varchar(20)
population        : bigint (8 byte int) 
elevation         : in meters, integer
gtopo30           : average elevation of 30'x30' (ca 900mx900m) area in meters, integer
timezone          : the timezone id (see file timeZone.txt)
modification date : date of last modification in yyyy-MM-dd format
*/

public class GeoName {
    public String name;
    public boolean majorPlace; // Major or minor place
    public Double latitude;
    public Double longitude;

    GeoName(String data) {
        String[] names = data.split("\t");
        name = names[1];
        if (names[6].equals("P")) {
            majorPlace = true;
        } else {
            majorPlace = false;
        }
        latitude = Double.parseDouble(names[4]);
        longitude = Double.parseDouble(names[5]);
    }

    GeoName(Double latitude, Double longitude) {
        name = "";
        latitude = this.latitude;
        longitude = this.longitude;
    }

    private double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    private double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }

    // Haversine formula
    Double distance(double lat1, double lon1) {
        double theta = lon1 - longitude;
        double dist = sin(deg2rad(lat1)) * sin(deg2rad(latitude)) + cos(deg2rad(lat1)) * cos(deg2rad(latitude)) * cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
        dist = dist * 1.609344;
        return new Double(dist);
    }

    Double getX() {
        return cos(latitude) * cos(longitude);
    }

    Double getY() {
        return -sin(latitude);
    }

    Double getZ() {
        return cos(latitude) * sin(longitude);
    }

    Point3D get3DPoint() {
        Double cosLat = cos(latitude);
        Double x = cosLat * cos(longitude);
        Double y = -sin(latitude);
        Double z = cosLat * sin(longitude);
        return new Point3D(x, y, z);
    }

    @Override
    public String toString() {
        return name;
    }

    public Double distance(GeoName search) {
        Double x = getX();
        Double y = getY();
        Double z = getZ();
        return (x*x) + (y*y) + (z*z);
    }

    public static enum GeoNameComparator implements Comparator<GeoName> {
        x {
            @Override
            public int compare(GeoName a, GeoName b) {
                return a.getX().compareTo(b.getX());
            }
        },
        y {
            @Override
            public int compare(GeoName a, GeoName b) {
                return a.getY().compareTo(b.getY());
            }
        },
        z {
            @Override
            public int compare(GeoName a, GeoName b) {
                return a.getZ().compareTo(b.getZ());
            }
        };

        public static GeoNameComparator get(Integer axis) {
            if (axis == 0) {
                return x;
            } else if (axis == 1) {
                return y;
            } else {
                return z;
            }
        }
    }
}
