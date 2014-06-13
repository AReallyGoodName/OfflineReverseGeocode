package geocode;

import geocode.kdtree.KDNodeComparator;
import static java.lang.Math.cos;
import static java.lang.Math.sin;
import java.util.Comparator;

/**
 * Created by Daniel Glasson on 18/05/2014.
 * This class works with a placenames files from http://download.geonames.org/export/dump/
 */

public class GeoName extends KDNodeComparator<GeoName> {
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
    
    // The following methods are used purely for the KD-Tree
    // They don't convert lat/lon to any particular coordinate system
    Double getX() {
        return cos(deg2rad(latitude)) * cos(deg2rad(longitude));
    }

    Double getY() {
        return cos(deg2rad(latitude)) * sin(deg2rad(longitude));
    }
    
    Double getZ() {
        return sin(deg2rad(latitude));
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    protected Double squaredDistance(Object other) {
        GeoName location = (GeoName)other;
        Double x = getX() - location.getX();
        Double y = getY() - location.getY();
        Double z = getZ() - location.getZ();
        return (x*x) + (y*y) + (z*z);
    }

    @Override
    protected Double axisSquaredDistance(Object other, Integer axis) {
        GeoName location = (GeoName)other;
        Double distance;
        if ( axis == 0 ) {
            distance = getX() - location.getX();
        } else if ( axis == 1 ) {
            distance = getY() - location.getY();
        } else {
            distance = getZ() - location.getZ();
        }
        return distance * distance;
    }

    @Override
    protected Comparator getComparator(Integer axis) {
        return GeoNameComparator.get(axis);
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
