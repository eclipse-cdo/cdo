/***************************************************************************
 * Copyright (c) 2004 - 2008 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Simon McDuff - initial API and implementation
 *    Eike Stepper - maintenance
 **************************************************************************/
package org.eclipse.emf.cdo.internal.common.revision.delta;

import org.eclipse.emf.cdo.common.model.CDOClass;
import org.eclipse.emf.cdo.common.model.CDOFeature;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.delta.CDOAddFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDOFeatureDeltaVisitor;
import org.eclipse.emf.cdo.spi.common.InternalCDORevision;

import org.eclipse.net4j.util.io.ExtendedDataInput;

import java.io.IOException;

/**
 * @author Simon McDuff
 */
public class CDOAddFeatureDeltaImpl extends CDOSingleValueFeatureDeltaImpl implements CDOAddFeatureDelta,
    IListIndexAffecting, IListTargetAdding
{
  public CDOAddFeatureDeltaImpl(CDOFeature feature, int index, Object value)
  {
    super(feature, index, value);
  }

  public CDOAddFeatureDeltaImpl(ExtendedDataInput in, CDOClass cdoClass) throws IOException
  {
    super(in, cdoClass);
  }

  public Type getType()
  {
    return Type.ADD;
  }

  public void apply(CDORevision revision)
  {
    ((InternalCDORevision)revision).getList(getFeature()).add(getIndex(), getValue());
  }

  public void accept(CDOFeatureDeltaVisitor visitor)
  {
    visitor.visit(this);
  }

  public void affectIndices(int[] indices)
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
}
