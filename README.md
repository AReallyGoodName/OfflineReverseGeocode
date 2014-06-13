An Offline Reverse Geocoding Java library

Uses KD-Trees for extremely fast placename lookups

Licensed under LGPL 2.1

A C# port by Necrolis is available at https://github.com/Necrolis/GeoSharp

Usage:

First download a placenames file from http://download.geonames.org/export/dump/

Allcountries.zip from that site is comprehensive however if you're on mobile try the cities1000.zip file. It's 1/80th of the size.

Then simply

ReverseGeoCode reverseGeoCode = new ReverseGeoCode("c:\\\\placenames.txt", true);

println(reverseGeoCode.nearestMajorPlaceName(-12.345, 123.456));
