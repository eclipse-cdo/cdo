/*
 * Copyright (c) 2009-2013, 2018, 2025 Eike Stepper (Loehne, Germany) and others.
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

import org.eclipse.emf.cdo.common.revision.CDOList;
import org.eclipse.emf.cdo.common.revision.CDORevisionUtil;

import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EStructuralFeature;

/**
 * If the meaning of this type isn't clear, there really should be more of a description here...
 *
 * @author Simon McDuff
 * @since 2.0
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface InternalCDOList extends CDOList
{
  /**
   * @deprecated As of 4.29 use {@link CDORevisionUtil#UNINITIALIZED} instead.
   */
  @Deprecated
  public static final Object UNINITIALIZED = CDORevisionUtil.UNINITIALIZED;

  public default void setOwner(Owner owner)
  {
    // Do nothing by default.
  }

  public default void loadValue(int index, Object value)
  {
    setWithoutFrozenCheck(index, value);
  }

  /**
   * Returns the server-side index represented by the value at the given current list index.
   * <p>
   * The current index is the index in this list. Implementations that represent unloaded values with a server-indexed
   * proxy override this method to return the source index stored by that representation. For ordinary values, the
   * current index is also the server-side index.
   *
   * @param accessIndex
   *          the current logical index in this list
   * @return the server-side index represented by the value at {@code accessIndex}
   * @since 4.31
   */
  public default int getServerIndexAt(int accessIndex)
  {
    return accessIndex;
  }

  /**
   * Completes the temporary construction phase of this list.
   *
   * @param partial
   *          whether positions that were not read must become unloaded
   */
  public default void finishConstruction(boolean partial)
  {
    // Do nothing by default.
  }

  /**
   * Adjusts references according to the passed adjuster and resynchronizes indexes.
   *
   * @since 4.0
   */
  public boolean adjustReferences(CDOReferenceAdjuster adjuster, EStructuralFeature feature);

  /**
   * Clones the list.
   */
  public InternalCDOList clone(EClassifier classifier);

  /**
   * @since 4.0
   */
  public void freeze();

  /**
   * @since 4.0
   */
  public void setWithoutFrozenCheck(int i, Object value);

  /**
   * Internal owner callbacks used for list/revision loaded-state accounting.
   *
   * @author Eike Stepper
   */
  public interface Owner
  {
    public boolean isFrozen();

    public void unloadedCountChanged(int delta);
  }

  /**
   * A mix-in interface for {@link InternalCDOList} that allows to optimize the speed of equality checks.
   *
   * @author Eike Stepper
   * @since 4.2
   */
  public interface ConfigurableEquality extends InternalCDOList
  {
    public void setUseEquals(boolean useEquals);
  }
}
