/*
 * Copyright (c) 2008-2012, 2014-2019 Eike Stepper (Loehne, Germany) and others.
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

import org.eclipse.emf.cdo.common.protocol.CDODataInput;
import org.eclipse.emf.cdo.common.protocol.CDODataOutput;
import org.eclipse.emf.cdo.common.revision.CDOList;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.delta.CDOFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDOFeatureDeltaVisitor;
import org.eclipse.emf.cdo.common.revision.delta.CDOMoveFeatureDelta;
import org.eclipse.emf.cdo.spi.common.revision.CDOReferenceAdjuster;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDOFeatureDelta.ListIndexAffecting;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDOFeatureDelta.WithIndex;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EcorePackage;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Simon McDuff
 */
public class CDOMoveFeatureDeltaImpl extends CDOFeatureDeltaImpl implements CDOMoveFeatureDelta, ListIndexAffecting, WithIndex
{
  private int oldPosition;

  private int newPosition;

  private Object value;

  public CDOMoveFeatureDeltaImpl(EStructuralFeature feature, int newPosition, int oldPosition)
  {
    super(feature);
    this.newPosition = newPosition;
    this.oldPosition = oldPosition;
    value = UNKNOWN_VALUE;
  }

  public CDOMoveFeatureDeltaImpl(CDODataInput in, EClass eClass) throws IOException
  {
    super(in, eClass);
    newPosition = in.readXInt();
    oldPosition = in.readXInt();
    value = UNKNOWN_VALUE;
  }

  @Override
  public void write(CDODataOutput out, EClass eClass) throws IOException
  {
    super.write(out, eClass);
    out.writeXInt(newPosition);
    out.writeXInt(oldPosition);
  }

  @Override
  public int getNewPosition()
  {
    return newPosition;
  }

  @Override
  public int getOldPosition()
  {
    return oldPosition;
  }

  @Override
  public Type getType()
  {
    return Type.MOVE;
  }

  @Override
  public int getIndex()
  {
    return oldPosition;
  }

  @Override
  public Object getValue()
  {
    return value;
  }

  public void setValue(Object value)
  {
    this.value = value;
  }

  public void setOldPosition(int oldPosition)
  {
    this.oldPosition = oldPosition;
  }

  public void setNewPosition(int newPosition)
  {
    this.newPosition = newPosition;
  }

  @Override
  public CDOFeatureDelta copy()
  {
    CDOFeatureDelta copy = new CDOMoveFeatureDeltaImpl(getFeature(), newPosition, oldPosition);
    ((CDOMoveFeatureDeltaImpl)copy).setValue(getValue());
    return copy;
  }

  @Override
  public Object applyTo(CDORevision revision)
  {
    EStructuralFeature feature = getFeature();

    InternalCDORevision internalRevision = (InternalCDORevision)revision;
    CDOList list = internalRevision.getListOrNull(feature);

    if (oldPosition < 0)
    {
      return null;
    }

    int size = list.size();
    if (oldPosition > size)
    {
      return null;
    }

    if (newPosition < 0)
    {
      newPosition = 0;
    }
    else if (newPosition > size)
    {
      newPosition = size;
    }

    return list.move(newPosition, oldPosition);
  }

  @Override
  public void affectIndices(ListTargetAdding[] source, int[] indices)
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

  public static void main(String[] args)
  {
    final EReference feature = EcorePackage.Literals.EPACKAGE__ECLASSIFIERS;

    List<CDOFeatureDelta> deltas = new ArrayList<CDOFeatureDelta>();
    deltas.add(new CDOMoveFeatureDeltaImpl(feature, 7, 1));
    // deltas.add(new CDORemoveFeatureDeltaImpl(feature, 1));
    // deltas.add(new CDORemoveFeatureDeltaImpl(feature, 1));

    for (int i = 0; i < 10; i++)
    {
      projectIndex(deltas, i);
    }
  }

  private static void projectIndex(List<CDOFeatureDelta> deltas, int index)
  {
    for (CDOFeatureDelta delta : deltas)
    {
      if (delta instanceof ListIndexAffecting)
      {
        index = ((ListIndexAffecting)delta).projectIndex(index);
      }
    }

    System.out.println(index);
  }

  @Override
  public int projectIndex(int index)
  {
    if (oldPosition < newPosition)
    {
      // Move to right.
      if (oldPosition <= index && index < newPosition)
      {
        ++index;
      }
      else if (index == newPosition)
      {
        index = oldPosition;
      }
    }
    else
    {
      // Move to left.
      if (newPosition < index && index <= oldPosition)
      {
        --index;
      }
      else if (index == newPosition)
      {
        index = oldPosition;
      }
    }

    return index;
  }

  @Override
  public void accept(CDOFeatureDeltaVisitor visitor)
  {
    visitor.visit(this);
  }

  @Override
  public void adjustAfterAddition(int index)
  {
    if (index <= oldPosition)
    {
      ++oldPosition;
    }

    if (index <= newPosition)
    {
      ++newPosition;
    }
  }

  @Override
  public void adjustAfterRemoval(int index)
  {
    if (index < oldPosition && oldPosition > 0)
    {
      --oldPosition;
    }

    // Index fix for moves from left to right.
    if (oldPosition < newPosition)
    {
      --index;
    }

    if (index < newPosition && newPosition > 0)
    {
      --newPosition;
    }
  }

  @Override
  public void adjustAfterMove(int oldPosition, int newPosition)
  {
    if (this.oldPosition == oldPosition)
    {
      this.oldPosition = newPosition;
    }
    else
    {
      adjustAfterRemoval(oldPosition);
      adjustAfterAddition(newPosition);
    }
  }

  @Override
  public boolean adjustReferences(CDOReferenceAdjuster adjuster)
  {
    return false;
  }

  @Override
  public boolean isStructurallyEqual(Object obj)
  {
    if (!super.isStructurallyEqual(obj))
    {
      return false;
    }

    CDOMoveFeatureDelta that = (CDOMoveFeatureDelta)obj;
    return oldPosition == that.getOldPosition() && newPosition == that.getNewPosition();
  }

  @Override
  protected String toStringAdditional()
  {
    return MessageFormat.format("from={0}, to={1}, value={2}", oldPosition, newPosition, value);
  }

}
