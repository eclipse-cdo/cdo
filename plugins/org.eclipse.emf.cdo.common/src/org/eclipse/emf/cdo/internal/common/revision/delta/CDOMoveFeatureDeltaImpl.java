/**
 * Copyright (c) 2004 - 2009 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Simon McDuff - initial API and implementation
 *    Eike Stepper - maintenance
 */
package org.eclipse.emf.cdo.internal.common.revision.delta;

import org.eclipse.emf.cdo.common.io.CDODataInput;
import org.eclipse.emf.cdo.common.io.CDODataOutput;
import org.eclipse.emf.cdo.common.revision.CDOReferenceAdjuster;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.delta.CDOFeatureDeltaVisitor;
import org.eclipse.emf.cdo.common.revision.delta.CDOMoveFeatureDelta;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EStructuralFeature;

import java.io.IOException;

/**
 * @author Simon McDuff
 */
public class CDOMoveFeatureDeltaImpl extends CDOFeatureDeltaImpl implements CDOMoveFeatureDelta, IListIndexAffecting
{
  private int oldPosition;

  private int newPosition;

  public CDOMoveFeatureDeltaImpl(EStructuralFeature feature, int newPosition, int oldPosition)
  {
    super(feature);
    this.newPosition = newPosition;
    this.oldPosition = oldPosition;
  }

  public CDOMoveFeatureDeltaImpl(CDODataInput in, EClass eClass) throws IOException
  {
    super(in, eClass);
    newPosition = in.readInt();
    oldPosition = in.readInt();
  }

  @Override
  public void write(CDODataOutput out, EClass eClass) throws IOException
  {
    super.write(out, eClass);
    out.writeInt(newPosition);
    out.writeInt(oldPosition);
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

  public void affectIndices(IListTargetAdding[] source, int[] indices)
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
  public void adjustReferences(CDOReferenceAdjuster adjuster)
  {
    // Do nothing
  }
}
