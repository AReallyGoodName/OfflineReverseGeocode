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
