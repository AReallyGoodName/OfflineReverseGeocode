/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package geocode.kdtree;
import java.util.Comparator;

/**
 *
 * @author Daniel Glasson
 * Make the user return a comparator for each axis
 * Squared distances should be an optimisation
 */
public abstract class KDNodeComparator<T> { 
    // This should return a comparator for whatever axis is passed in
    protected abstract Comparator<T> getComparator(Integer axis);
    
    // Return squared distance between current and other
    protected abstract <T> Double squaredDistance(T other);
    
    // Return squared distance between one axis only
    protected abstract <T> Double axisSquaredDistance(T other, Integer axis);
}
