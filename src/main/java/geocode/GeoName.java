/*
The MIT License (MIT)
[OSI Approved License]
The MIT License (MIT)

Copyright (c) 2014 Daniel Glasson

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
THE SOFTWARE.
 */

package geocode;

import static java.lang.Math.cos;
import static java.lang.Math.sin;
import static java.lang.Math.toRadians;

import java.util.Comparator;
import java.util.Objects;

import geocode.kdtree.KDNodeComparator;

/**
 * Created by Daniel Glasson on 18/05/2014.
 * This class works with a placenames files from http://download.geonames.org/export/dump/
 * @author Daniel Glasson (18/05/2014)
 * @author Guillaume Diaz (22/07/2019) - add hashCode() + equals()
 */
public class GeoName extends KDNodeComparator<GeoName> {
	public String name;
	public boolean majorPlace; // Major or minor place
	public double latitude;
	public double longitude;
	public double point[] = new double[3]; // The 3D coordinates of the point
	public String country;

	GeoName(final String data) {
		final String[] names = data.split("\t");
		name = names[1];
		majorPlace = names[6].equals("P");
		latitude = Double.parseDouble(names[4]);
		longitude = Double.parseDouble(names[5]);
		setPoint();
		country = names[8];
	}

	GeoName(final Double latitude, final Double longitude) {
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
		return name + " [" + country + "]";
	}

	@Override
	protected double squaredDistance(final GeoName other) {
		final double x = this.point[0] - other.point[0];
		final double y = this.point[1] - other.point[1];
		final double z = this.point[2] - other.point[2];
		return (x*x) + (y*y) + (z*z);
	}

	@Override
	protected double axisSquaredDistance(final GeoName other, final int axis) {
		final double distance = point[axis] - other.point[axis];
		return distance * distance;
	}

	@Override
	protected Comparator<GeoName> getComparator(final int axis) {
		return GeoNameComparator.values()[axis];
	}

	protected static enum GeoNameComparator implements Comparator<GeoName> {
		x {
			@Override
			public int compare(final GeoName a, final GeoName b) {
				return Double.compare(a.point[0], b.point[0]);
			}
		},
		y {
			@Override
			public int compare(final GeoName a, final GeoName b) {
				return Double.compare(a.point[1], b.point[1]);
			}
		},
		z {
			@Override
			public int compare(final GeoName a, final GeoName b) {
				return Double.compare(a.point[2], b.point[2]);
			}
		};
	}

	@Override
	public int hashCode() {
		return Objects.hash(country, latitude, longitude, name);
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof GeoName)) {
			return false;
		}
		final GeoName other = (GeoName) obj;
		return Objects.equals(country, other.country)
				&& Double.doubleToLongBits(latitude) == Double.doubleToLongBits(other.latitude)
				&& Double.doubleToLongBits(longitude) == Double.doubleToLongBits(other.longitude)
				&& Objects.equals(name, other.name);
	}


}
