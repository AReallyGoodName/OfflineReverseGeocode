/**
 * Offline reverse geocoding module
 * @author Guillaume Diaz - fork (2019/07), version 1.0.0
 * @author Daniel Glasson - original version 0.1
 * @provides eu.daxiongmao.geocode offline reverse geocoding based on GeoCode database
 * @uses java.base core java features
 * @since v1.0.0
 */
module eu.daxiongmao.geocode {
    // check required dependencies and modules with jdeps
    // jdeps verbose jdkinternals ./target/offline-reverse-geocoding-1.0.0-SNAPSHOT.jar
    requires java.base;

    // class, packages and enum that are for the public
    exports eu.daxiongmao.geocode;
}