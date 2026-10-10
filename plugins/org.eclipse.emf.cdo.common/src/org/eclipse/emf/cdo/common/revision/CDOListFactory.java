/*
 * Copyright (c) 2008, 2011, 2012, 2015, 2019, 2025 Eike Stepper (Loehne, Germany) and others.
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

import org.eclipse.emf.cdo.internal.common.revision.CDOListImpl;
import org.eclipse.emf.cdo.internal.common.revision.CDOListWithEqualsImpl;
import org.eclipse.emf.cdo.internal.common.revision.CDOPCLListImpl;
import org.eclipse.emf.cdo.internal.common.revision.CDOPCLListWithEqualsImpl;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EStructuralFeature;

/**
 * Creates {@link CDOList list} instances.
 *
 * @author Simon McDuff
 * @since 2.0
 */
public interface CDOListFactory
{
  public static final CDOListFactory DEFAULT = new CDOListFactory()
  {
    @Override
    public CDOList createList(int initialCapacity, int size, int initialChunk)
    {
      return new CDOListImpl(initialCapacity, size);
    }

    @Override
    public CDOList createList(EStructuralFeature feature, int initialCapacity, int size, int initialChunk)
    {
      boolean partial = initialChunk != CDORevision.UNCHUNKED && initialChunk < size;
      if (partial)
      {
        return feature != null && feature instanceof EAttribute //
            ? new CDOPCLListWithEqualsImpl(initialCapacity, size) //
            : new CDOPCLListImpl(initialCapacity, size);
      }

      return feature != null && feature instanceof EAttribute //
          ? new CDOListWithEqualsImpl(initialCapacity, size) //
          : new CDOListImpl(initialCapacity, size);
    }
  };

  public CDOList createList(int intitialCapacity, int size, int initialChunk);

  /**
   * Creates a list with equality semantics derived from the feature.
   * @since 4.29
   */
  public default CDOList createList(EStructuralFeature feature, int initialCapacity, int size, int initialChunk)
  {
    return createList(initialCapacity, size, initialChunk);
  }
}
