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

As a special exception to the GNU Lesser General Public License version 2.1, you
may convey to a third party an executable file from a Combined Work that links,
statically or dynamically, portions of this Library in the executable file,
conveying the Minimal Corresponding Source but without the need to convey the
Corresponding Application Code under section 6b of the GNU Lesser General Public
License, so long as you are using an unmodified publicly distributed version of
the Library. This exception does not invalidate any other reasons why the
executable file might be covered by the GNU Lesser General Public License or the
GNU General Public License.
*/

package geocode.kdtree;

/**
 *
 * @author Daniel Glasson
 */
public class KDNode<T extends KDNodeComparator<T>> {
    KDNode<T> left;
    KDNode<T> right;
    T location;

    public KDNode( KDNode<T> left, KDNode<T> right, T location ) {
        this.left = left;
        this.right = right;
        this.location = location;
    }
}