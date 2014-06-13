package geocode.kdtree;

import java.util.Arrays;

/**
 *
 * @author Daniel Glasson
 * A KD-Tree implementation to quickly find nearest points
 * Currently implements createKDTree and findNearest as that's all that's required here
 */
public class KDTree<T extends KDNodeComparator<T>> {
    private KDNode<T> root;

    public KDTree( T[] items ) {
        root = createKDTree(items, 0, items.length, 0);
    }
    
    public T findNearest( T search ) {
        return findNearest(root, search, 0).location;
    }
        
    // Only ever goes to log2(items.length) depth so lack of tail recursion is a non-issue
    private KDNode<T> createKDTree( T[] items, int start, int end, int depth ) {
        if ( start >= end ) {
            return null;
        }
        Arrays.sort(items, start, end, items[0].getComparator(depth % 3));
        int currentIndex = start + ((end-start)/2);
        return new KDNode(createKDTree(items, start, currentIndex, depth+1), createKDTree(items, currentIndex+1, end, depth+1), items[currentIndex]);
    }

    // At least 2*log2(N) complexity - 
    // It starts by going down to a leaf and then works its way back up (2*log2(N)) 
    // It goes down other potential branches if the best is not yet found 
    // Good time complexity though
    private KDNode<T> findNearest(KDNode<T> currentNode, T search, int depth) {
        int direction = search.getComparator(depth % 3).compare( search, (T)currentNode.location );
        KDNode<T> next = (direction < 0) ? currentNode.left : currentNode.right;
        KDNode<T> other = (direction < 0) ? currentNode.right : currentNode.left;
        KDNode<T> best = (next == null) ? currentNode : findNearest(next, search, depth + 1); // Go to a leaf
        if ( currentNode.location.squaredDistance(search) < best.location.squaredDistance(search) ) {
            best = currentNode;
        } 
        if ( other != null ) {
            if ( currentNode.location.axisSquaredDistance(best.location, depth % 3) < best.location.squaredDistance(search) ) {
                KDNode<T> possibleBest = findNearest( other, search, depth + 1 );
                if (  possibleBest.location.squaredDistance(search) < best.location.squaredDistance(search) ) {
                    best = possibleBest;
                }
            }
        }
        return best; // Work back up
    }
}
