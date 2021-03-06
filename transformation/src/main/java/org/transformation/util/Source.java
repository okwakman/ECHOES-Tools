/**
 * 
 */
package org.transformation.util;

import java.util.List;


/**
 * @author amartinez
 *
 */
public interface Source<T> {
    List<T> nextIteration () throws Exception;
    boolean endReached ();
}