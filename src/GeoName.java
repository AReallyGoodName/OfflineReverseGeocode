import static java.lang.Math.cos;
import static java.lang.Math.sin;
import java.util.Comparator;

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
        this.name = names[1];
        if (names[6].equals("P")) {
            this.majorPlace = true;
        } else {
            this.majorPlace = false;
        }
        this.latitude = Double.parseDouble(names[4]);
        this.longitude = Double.parseDouble(names[5]);
    }

    GeoName(Double latitude, Double longitude) {
        name = "Search";
        this.latitude = latitude;
        this.longitude = longitude;
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
        return get3DPoint().x;
    }

    Double getY() {
        return get3DPoint().y;
    }
    
    Double getZ() {
        return get3DPoint().z;
    }

    public class Point3D {
        public Double x,y,z;

        public Point3D(Double x, Double y, Double z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }
    }
    
    Point3D get3DPoint() {
        Double rad = 6378137.0;
        Double f = 1.0/298.257223563;
        Double cosLat = cos(deg2rad(latitude));
        Double sinLat = sin(deg2rad(latitude));
        Double FF     = (1.0-f) * (1.0-f);
        Double C      = 1/Math.sqrt((cosLat*cosLat) + FF * sinLat * sinLat);
        Double S      = C * FF;

        return new Point3D(
                (rad * C + 0)*cosLat * cos(deg2rad(longitude)),
                (rad * C + 0)*cosLat * sin(deg2rad(longitude)),
                (rad * S + 0)*sinLat
                );
    }

    @Override
    public String toString() {
        return name;
    }

    public Double squaredDistance(GeoName location) {
        Double x = getX() - location.getX();
        Double y = getY() - location.getY();
        Double z = getZ() - location.getZ();
        return (x*x) + (y*y) + (z*z);
    }

    Double distance(GeoName location) {
        return Math.sqrt(squaredDistance(location));
    }

    Double splitDistance(int axis, GeoName location) {
        if ( axis == 0 ) {
            return Math.abs(getX() - location.getX());
        } else if ( axis == 1 ) {
            return Math.abs(getY() - location.getY());
        } else {
            return Math.abs(getZ() - location.getZ());
        }
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
