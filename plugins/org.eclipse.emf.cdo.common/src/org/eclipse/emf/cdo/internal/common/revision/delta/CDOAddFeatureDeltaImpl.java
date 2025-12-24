/*
 * Copyright (c) 2008-2012, 2014-2019 Eike Stepper (Loehne, Germany) and others.
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
package org.eclipse.emf.cdo.internal.common.revision.delta;

import org.eclipse.emf.cdo.common.protocol.CDODataInput;
import org.eclipse.emf.cdo.common.revision.CDOList;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.delta.CDOAddFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDOFeatureDeltaVisitor;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDOFeatureDelta.ListIndexAffecting;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDOFeatureDelta.ListTargetAdding;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EStructuralFeature;

import java.io.IOException;

/**
 * @author Simon McDuff
 */
public class CDOAddFeatureDeltaImpl extends CDOSingleValueFeatureDeltaImpl implements CDOAddFeatureDelta, ListIndexAffecting, ListTargetAdding
{
  public CDOAddFeatureDeltaImpl(EStructuralFeature feature, int index, Object value)
  {
    super(feature, index, value);
  }

  public CDOAddFeatureDeltaImpl(CDODataInput in, EClass eClass) throws IOException
  {
    super(in, eClass);
  }

  @Override
  public Type getType()
  {
    return Type.ADD;
  }

  @Override
  public Object applyTo(CDORevision revision)
  {
    EStructuralFeature feature = getFeature();
    int index = getIndex();
    Object value = getValue();

    InternalCDORevision internalRevision = (InternalCDORevision)revision;
    CDOList list = internalRevision.getOrCreateList(feature);

    if (index < 0)
    {
      index = 0;
    }
    else
    {
      int size = list.size();
      if (index > size)
      {
        index = size;
      }
    }

    list.add(index, value);
    return null;
  }

  @Override
  public void accept(CDOFeatureDeltaVisitor visitor)
  {
    visitor.visit(this);
  }

  @Override
  public CDOAddFeatureDelta copy()
  {
    return new CDOAddFeatureDeltaImpl(getFeature(), getIndex(), getValue());
  }

  @Override
  public void affectIndices(ListTargetAdding[] source, int[] indices)
  {
    int index = getIndex();
    if (index == NO_INDEX)
    {
      return;
    }

    for (int i = 1; i <= indices[0]; i++)
    {
      if (indices[i] >= index)
      {
        ++indices[i];
      }
    }
  }

  @Override
  public int projectIndex(int index)
  {
    if (index >= getIndex())
    {
      --index;
    }

    return index;
  }
}
