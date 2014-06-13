/*
This library is free software; you can redistribute it and/or modify it under
the terms of the GNU Lesser General Public License as published by the Free
Software Foundation; either version 2.1 of the License, or (at your option)
any later version.
This library is distributed in the hope that it will be useful, but WITHOUT
ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
details.
You should have received a copy of the GNU Lesser General Public License
along with this library; if not, write to the Free Software Foundation, Inc.,
59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
*/

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
        return new KDNode<T>(createKDTree(items, start, currentIndex, depth+1), createKDTree(items, currentIndex+1, end, depth+1), items[currentIndex]);
    }

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
