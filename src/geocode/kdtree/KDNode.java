package geocode.kdtree;

/**
 *
 * @author Daniel Glasson
 */
public class KDNode<T extends KDNodeComparator<T>> {
        KDNode left;
        KDNode right;
        T location;

        public KDNode( KDNode left, KDNode right, T location ) {
            this.left = left;
            this.right = right;
            this.location = location;
        }
    }