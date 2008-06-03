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
import org.eclipse.emf.cdo.common.revision.delta.CDOMoveFeatureDelta;
import org.eclipse.emf.cdo.spi.common.InternalCDORevision;

import org.eclipse.net4j.util.io.ExtendedDataInput;
import org.eclipse.net4j.util.io.ExtendedDataOutput;

import java.io.IOException;
import java.util.Map;

/**
 * @author Simon McDuff
 */
public class CDOMoveFeatureDeltaImpl extends CDOFeatureDeltaImpl implements CDOMoveFeatureDelta, IListIndexAffecting
{
  private int oldPosition;

  private int newPosition;

  public CDOMoveFeatureDeltaImpl(CDOFeature feature, int newPosition, int oldPosition)
  {
    super(feature);
    this.newPosition = newPosition;
    this.oldPosition = oldPosition;
  }

  public CDOMoveFeatureDeltaImpl(ExtendedDataInput in, CDOClass cdoClass) throws IOException
  {
    super(in, cdoClass);
    newPosition = in.readInt();
    oldPosition = in.readInt();
  }

  public int getNewPosition()
  {
    return newPosition;
  }

  public int getOldPosition()
  {
    return oldPosition;
  }

  public Type getType()
  {
    return Type.MOVE;
  }

  public void apply(CDORevision revision)
  {
    ((InternalCDORevision)revision).getList(getFeature()).move(newPosition, oldPosition);
  }

  @Override
  public void write(ExtendedDataOutput out, CDOClass cdoClass, CDOIDProvider idProvider) throws IOException
  {
    super.write(out, cdoClass, idProvider);
    out.writeInt(newPosition);
    out.writeInt(oldPosition);
  }

  public void affectIndices(int[] indices)
  {
    if (oldPosition < newPosition)
    {
      for (int i = 1; i <= indices[0]; i++)
      {
        if (oldPosition < indices[i] && indices[i] <= newPosition)
        {
          --indices[i];
        }
        else if (indices[i] == oldPosition)
        {
          indices[i] = newPosition;
        }
      }
    }
    else if (newPosition < oldPosition)
    {
      for (int i = 1; i <= indices[0]; i++)
      {
        if (newPosition <= indices[i] && indices[i] < oldPosition)
        {
          ++indices[i];
        }
        else if (indices[i] == oldPosition)
        {
          indices[i] = newPosition;
        }
      }
    }
  }

  public void accept(CDOFeatureDeltaVisitor visitor)
  {
    visitor.visit(this);
  }

  @Override
  public void adjustReferences(Map<CDOIDTemp, CDOID> idMappings)
  {
    // Do nothing
  }
}
