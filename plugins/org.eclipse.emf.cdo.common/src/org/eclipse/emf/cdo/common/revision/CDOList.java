/*
 * Copyright (c) 2008, 2010-2012, 2015, 2019, 2025 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Simon McDuff - initial API and implementation
 *    Eike Stepper - maintenance
 */
package org.eclipse.emf.cdo.common.revision;

import org.eclipse.net4j.util.collection.MoveableList;

import org.eclipse.emf.common.util.EList;

/**
 * A {@link MoveableList movable} {@link EList}.
 *
 * @author Simon McDuff
 * @since 2.0
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface CDOList extends MoveableList<Object>, EList<Object>
{
  /**
   * Returns whether the element at {@code index} is loaded.
   * @since 4.29
   */
  public default boolean isLoadedAt(int index)
  {
    Object value = get(index);
    return value != CDORevisionUtil.UNLOADED && !(value instanceof CDOElementProxy);
  }

  /**
   * Returns whether all elements in this list are loaded.
   * <p>
   * An empty list is considered fully loaded. This is an observation-only query and does not load any elements.
   * Implementations with unloaded-state bookkeeping should override this method to provide a constant-time result.
   *
   * @return {@code true} if every position in this list is loaded, or if the list is empty
   * @since 4.29
   */
  public default boolean isFullyLoaded()
  {
    for (int i = 0; i < size(); i++)
    {
      if (!isLoadedAt(i))
      {
        return false;
      }
    }

    return true;
  }

  /**
   * Returns the element at position index of this list and optionally resolves proxies (see CDOElementProxy).
   * <p>
   *
   * @param index
   *          The position of the element to return from this list.
   * @param resolve
   *          A value of <code>false</code> indicates that {@link CDORevisionUtil#UNLOADED} may be returned for
   *          unresolved elements. A value of <code>true</code> indicates that it should behave identical to
   *          {@link CDOList#get(int)}.
   */
  public Object get(int index, boolean resolve);
}
