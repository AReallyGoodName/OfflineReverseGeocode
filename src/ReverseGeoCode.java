import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

/**
 * Created by Daniel Glasson on 18/05/2014.
 * Uses kd-trees to quickly find the nearest point
 */
public class ReverseGeoCode {
    private KDNode<GeoName> root;
    
    // Get placenames from http://download.geonames.org/export/dump/
    public void loadPlaceNames(String placeNamesFile) throws IOException {
        ArrayList<GeoName> arPlaceNames;
        arPlaceNames = new ArrayList<GeoName>();
        // Read the geonames file in the directory
        BufferedReader in = new BufferedReader(new FileReader(placeNamesFile));
        String str;
        while ((str = in.readLine()) != null) {
            arPlaceNames.add(new GeoName(str));
        }
        in.close();
        GeoName[] placeNames = arPlaceNames.toArray(new GeoName[arPlaceNames.size()]);
        root = createKDTree(placeNames, 0, placeNames.length, 0);
    }

    public class KDNode<T> {
        KDNode left;
        KDNode right;
        T location;

        public KDNode(KDNode left, KDNode right, T location) {
            this.left = left;
            this.right = right;
            this.location = location;
        }
    }

    public KDNode<GeoName> createKDTree( GeoName[] placeNames, int start, int end, int depth ) {
        if ( start >= end ) {
            return null;
        }
        Arrays.sort(placeNames, start, end, GeoName.GeoNameComparator.get(depth % 3));
        int currentIndex = start + ((end-start)/2);
        return new KDNode(createKDTree(placeNames, start, currentIndex, depth+1), createKDTree(placeNames, currentIndex+1, end, depth+1), placeNames[currentIndex]);
    }


        /*
    1. Starting with the root node, the algorithm moves down the tree recursively, in the same way that it would if the search point were being inserted (i.e. it goes left or right depending on whether the point is less than or greater than the current node in the split dimension).
    2. Once the algorithm reaches a leaf node, it saves that node point as the "current best"
    The algorithm unwinds the recursion of the tree, performing the following steps at each node:
    3. If the current node is closer than the current best, then it becomes the current best.
    
    * The algorithm checks whether there could be any points on the other side of the splitting plane
    * that are closer to the search point than the current best. 
    * In concept, this is done by intersecting the splitting hyperplane with a hypersphere 
    * around the search point that has a radius equal to the current nearest distance. 
    * Since the hyperplanes are all axis-aligned this is implemented as a simple comparison to see 
    * whether the difference between the splitting coordinate of the search point and current node is 
    * less than the distance (overall coordinates) from the search point to the current best.
    * 
    * If the hypersphere crosses the plane, there could be nearer points on the other side of the plane, 
    * so the algorithm must move down the other branch of the tree from the current node looking for closer points, 
    * following the same recursive process as the entire search.
    * If the hypersphere doesn't intersect the splitting plane, then the algorithm continues walking up the tree, 
    * and the entire branch on the other side of that node is eliminated.
    * When the algorithm finishes this process for the root node, then the search is complete.
    */

    public KDNode<GeoName> findNearest(KDNode<GeoName> currentNode, GeoName search, int depth) {
        int direction = GeoName.GeoNameComparator.get(depth % 3).compare( search, (GeoName)currentNode.location );
        KDNode<GeoName> next = (direction < 0) ? currentNode.left : currentNode.right;
        KDNode<GeoName> best = (next == null) ? currentNode : findNearest(next, search, depth + 1);
        if ( currentNode.location.squaredDistance(search) < best.location.squaredDistance(search) ) {
            best = currentNode;
        } 
        KDNode<GeoName> other = (direction < 0) ? currentNode.right : currentNode.left;
        if ( other != null && other.location.splitDistance(depth % 3, best.location) > best.location.distance(search) ) {
            KDNode<GeoName> possibleBest = findNearest( other, search, depth + 1 );
            if (  possibleBest.location.squaredDistance(search) < best.location.squaredDistance(search) ) {
                best = possibleBest;
            }
        }
        return best;
    }

    // Brute force version
    public GeoName findNearestPlaceBruteForce( GeoName[] placeNames, Double latitude, Double longitude) {
        GeoName nearest = null;
        // Find the element in the array
        for (int i = 0; i < placeNames.length; ++i) {
            if ( nearest == null ||
                 (placeNames[i].distance(latitude, longitude) < nearest.distance(latitude, longitude) &&
                 placeNames[i].majorPlace))
            {
                nearest = placeNames[i];
            }
        }
        return nearest;
    }
    
    public static void main(String [] args) throws IOException
    {
        System.out.println("Loading placenames");
        ReverseGeoCode reverseGeoCode = new ReverseGeoCode();
        reverseGeoCode.loadPlaceNames("C:\\placenames.txt");
        System.out.println("Done loading placenames");
        GeoName search = new GeoName(34.0522, -118.40806);
        System.out.println("Looking for nearest to " + search.latitude + ", " + search.longitude);
        System.out.println("Which is " + search.getX() + ", " + search.getY() + ", " + search.getZ() );
        KDNode<GeoName> nearest = reverseGeoCode.findNearest(reverseGeoCode.root, search, 0);
        System.out.println("Nearest is " + nearest.location.name + " at " + nearest.location.latitude + ", " + nearest.location.longitude);
        System.out.println("Which is " + nearest.location.getX() + ", " + nearest.location.getY() + ", " + nearest.location.getZ() );
    }
}
