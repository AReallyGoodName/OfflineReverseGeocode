An Offline Reverse Geocoding Java library

Uses KD-Trees for extremely fast placename lookups

Licensed under LGPL 2.1

A C# port by Necrolis is available at https://github.com/Necrolis/GeoSharp

Usage:

First download a placenames file from http://download.geonames.org/export/dump/allCountries.zip

Then simply

ReverseGeoCode reverseGeoCode = new ReverseGeoCode("c:\\\\placenames.txt", true);

println(reverseGeoCode.nearestMajorPlaceName(-12.345, 123.456));
