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

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDProvider;
import org.eclipse.emf.cdo.common.id.CDOIDTemp;
import org.eclipse.emf.cdo.common.model.CDOClass;
import org.eclipse.emf.cdo.common.model.CDOFeature;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.delta.CDOFeatureDeltaVisitor;
import org.eclipse.emf.cdo.common.revision.delta.CDORemoveFeatureDelta;
import org.eclipse.emf.cdo.spi.common.InternalCDORevision;

import org.eclipse.net4j.util.io.ExtendedDataInput;
import org.eclipse.net4j.util.io.ExtendedDataOutput;

import java.io.IOException;
import java.util.Map;

/**
 * @author Simon McDuff
 */
public class CDORemoveFeatureDeltaImpl extends CDOFeatureDeltaImpl implements CDORemoveFeatureDelta,
    IListIndexAffecting
{
  private int index;

  public CDORemoveFeatureDeltaImpl(CDOFeature feature, int index)
  {
    super(feature);
    this.index = index;
  }

  public CDORemoveFeatureDeltaImpl(ExtendedDataInput in, CDOClass cdoClass) throws IOException
  {
    super(in, cdoClass);
    index = in.readInt();
  }

  public int getIndex()
  {
    return index;
  }

  public Type getType()
  {
    return Type.REMOVE;
  }

  public void apply(CDORevision revision)
  {
    ((InternalCDORevision)revision).getList(getFeature()).remove(index);
  }

  public void accept(CDOFeatureDeltaVisitor visitor)
  {
    visitor.visit(this);
  }

  @Override
  public void write(ExtendedDataOutput out, CDOClass cdoClass, CDOIDProvider idProvider) throws IOException
  {
    super.write(out, cdoClass, idProvider);
    out.writeInt(index);
  }

  public void affectIndices(int[] indices)
  {
    int index = getIndex();
    for (int i = 1; i <= indices[0]; i++)
    {
      if (indices[i] > index)
      {
        --indices[i];
      }
      else if (indices[i] == index)
      {
        int rest = indices[0]-- - i;
        if (rest > 0)
        {
          System.arraycopy(indices, i + 1, indices, i, rest);
          --i;
        }
      }
    }
  }

  @Override
  public void adjustReferences(Map<CDOIDTemp, CDOID> idMappings)
  {
    // do Nothing
  }
}
