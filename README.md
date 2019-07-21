An Offline Reverse Geocoding Java library

Uses KD-Trees for extremely fast placename lookups

Licensed under The MIT License

--------------------------

# Authors
- Created by **Daniel Glasson** on 18/05/2014
- Fork by **Guillaume Diaz** for **LuxTrust** on 22/07/2019 to keep all cities from the following countries: FR / BE / LU / DE 


# Requirements

Download a place-names file from http://download.geonames.org/export/dump/
- Use the file "cities1000.zip" to get cities with at least 1000 inhabitants worldwide


#Usage


Load in-memory database: 

<code>ReverseGeoCode reverseGeoCode = new ReverseGeoCode(new FileInputStream("/opt/portal/data/cities1000.txt"), true);</code>


Query position: 

<code>System.out.println("Nearest to -23.456, 123.456 is " + geocode.nearestPlace(-23.456, 123.456));</code>

