/*
 * Copyright (c) 2010-2013, 2017, 2018, 2025 Eike Stepper (Loehne, Germany) and others.
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
package org.eclipse.emf.cdo.spi.common.revision;

import org.eclipse.emf.cdo.common.revision.delta.CDOFeatureDelta;

/**
 * Internal extension contract for feature changes that need to participate in list-index adjustment during delta
 * application. Its nested roles let list operations update the positions affected by insertions, removals, and moves.
 *
 * @author Simon McDuff
 * @since 3.0
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface InternalCDOFeatureDelta extends CDOFeatureDelta
{
  /**
   * Marks a list feature delta whose operation has a meaningful position in the current list. List edits use this
   * contract to keep their indices aligned as earlier operations are applied.
   *
   * @author Eike Stepper
   * @noextend This interface is not intended to be extended by clients.
   * @noimplement This interface is not intended to be implemented by clients.
   */
  public interface WithIndex
  {
    /**
     * @since 4.6
     */
    public int getIndex();

    public void adjustAfterAddition(int index);

    public void adjustAfterRemoval(int index);

    /**
     * @since 4.6
     */
    public void adjustAfterMove(int oldPosition, int newPosition);
  }

  /**
   * Contract for a list operation that changes the indices of other pending operations. It projects an index through
   * that edit and can update the affected target additions.
   *
   * @author Eike Stepper
   * @noextend This interface is not intended to be extended by clients.
   * @noimplement This interface is not intended to be implemented by clients.
   */
  public interface ListIndexAffecting
  {
    /**
     * Expects the number of indices in the first element of the indices array.
     */
    public void affectIndices(ListTargetAdding source[], int[] indices);

    /**
     * @since 4.6
     */
    public int projectIndex(int index);
  }

  /**
   * Identifies a value being added to a list delta so other edits can adjust or cancel that pending target insertion.
   *
   * @author Eike Stepper
   * @noextend This interface is not intended to be extended by clients.
   * @noimplement This interface is not intended to be implemented by clients.
   */
  public interface ListTargetAdding
  {
    /**
     * @since 4.0
     */
    public Object getValue();

    public int getIndex();

    public void clear();
  }
}
