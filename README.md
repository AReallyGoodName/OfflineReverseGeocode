An Offline Reverse Geocoding library

Uses KD-Trees for extremely fast placename lookups

Usage:

First download a placenames file from http://download.geonames.org/export/dump/allCountries.zip

Then simply

ReverseGeoCode reverseGeoCode = new ReverseGeoCode("c:\\\\placenames.txt", true);

println(reverseGeoCode.nearestMajorPlaceName(-12.345, 123.456));
