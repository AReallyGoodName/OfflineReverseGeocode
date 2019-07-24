An Offline Reverse Geocoding Java library

Uses KD-Trees for extremely fast placename lookups

Licensed under The MIT License

--------------------------

[![Build Status](https://travis-ci.org/guihome-diaz/OfflineReverseGeocode.svg?branch=master)](https://travis-ci.org/guihome-diaz/OfflineReverseGeocode)


# Authors
- Created by **Daniel Glasson** on 18/05/2014
- Fork by **Guillaume Diaz** for **LuxTrust** on 22/07/2019 to keep all cities from the following countries: FR / BE / LU / DE 



# Requirements

Download a place-names file from http://download.geonames.org/export/dump/
- Use the file "<code>cities1000.zip</code>" to get cities with at least 1000 inhabitants worldwide


# Usage


**Standard usage (SINGLETON)**

You can init the database (<code>cities1000.zip</code>) once and access it as a Singleton. 

By default it only keep ALL details:

```java
// Retrieve data-file
final Path citiesFile = Paths.get("/home/guillaume/dev/workspace/OfflineReverseGeocode/src/test/resources/cities1000.zip");

// Init DB keep all cities (raises exception if the file does not exists or is not valid)
new ReverseGeoCode(inputZipFile, false, null);
		
// Query
GeoName closestCity = ReverseGeoCode.getInstance().nearestPlace(latitude, longitude);
System.out.println(closestCity.toString());
```


**Keep only major cities**

You can choose to keep only the majors cities by setting the flag as "true" :

```java
// Retrieve data-file
final Path citiesFile = Paths.get("/home/guillaume/dev/workspace/OfflineReverseGeocode/src/test/resources/cities1000.zip");

// Init DB keep only MAJOR cities
new ReverseGeoCode(inputZipFile, true, null);
```


**Multiple files at once**

You can also load multiple files together:

```java
// Retrieve data-files
final Path citiesFile = Paths.get("/home/guillaume/dev/workspace/OfflineReverseGeocode/src/test/resources/cities1000.zip");
final Path luFile = Paths.get("/home/guillaume/dev/workspace/OfflineReverseGeocode/src/test/resources/LU.zip");
final List<Path> files = Arrays.asList(citiesFile, luFile);

// Init DB (raises exception if the file does not exists or is not valid)
new ReverseGeoCode(files, false, null);
```


**Keep country details**

You can choose to keep all details (street, interest point, etc.) about some countries:

```java
// List of countries to keep
final Set<String> countriesToKepp = new HashSet<String>(Arrays.asList(new String[] {"FR", "LU"}));

// Retrieve data-files
final Path citiesFile = Paths.get("/home/guillaume/dev/workspace/OfflineReverseGeocode/src/test/resources/cities1000.zip");
final Path luFile = Paths.get("/home/guillaume/dev/workspace/OfflineReverseGeocode/src/test/resources/LU.zip");
final Path frFile = Paths.get("/home/guillaume/dev/workspace/OfflineReverseGeocode/src/test/resources/FR.zip");
final List<Path> files = Arrays.asList(citiesFile, luFile, frFile);

// Init DB (raises exception if the file does not exists or is not valid)
new ReverseGeoCode(files, true, countriesToKeep);
```


**Reload DB before each usage**

/!\ Careful: this is very bad for memory and performances.

```java
// Load DB before each call
ReverseGeoCode reverseGeoCode = new ReverseGeoCode(new FileInputStream("/opt/portal/data/cities1000.txt"), true);

// Query position
System.out.println("Nearest to -23.456, 123.456 is " + geocode.nearestPlace(-23.456, 123.456));
```
