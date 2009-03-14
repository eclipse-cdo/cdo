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
import org.eclipse.emf.cdo.common.revision.delta.CDOFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDOFeatureDeltaVisitor;
import org.eclipse.emf.cdo.common.revision.delta.CDOListFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDORemoveFeatureDelta;

import org.eclipse.net4j.util.collection.Pair;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Simon McDuff
 */
public class CDOListFeatureDeltaImpl extends CDOFeatureDeltaImpl implements CDOListFeatureDelta
{
  private List<CDOFeatureDelta> featureDeltas = new ArrayList<CDOFeatureDelta>();

  private transient int[] cacheIndices;

  private transient IListTargetAdding[] cacheSources;

  private transient List<CDOFeatureDelta> notProcessedFeatureDelta;

  public CDOListFeatureDeltaImpl(EStructuralFeature feature)
  {
    super(feature);
  }

  public CDOListFeatureDeltaImpl(CDODataInput in, EClass eClass) throws IOException
  {
    super(in, eClass);
    int size = in.readInt();
    for (int i = 0; i < size; i++)
    {
      featureDeltas.add(in.readCDOFeatureDelta(eClass));
    }
  }

  @Override
  public CDOListFeatureDelta copy()
  {
    CDOListFeatureDeltaImpl list = new CDOListFeatureDeltaImpl(getFeature());
    for (CDOFeatureDelta delta : featureDeltas)
    {
      list.add(((InternalCDOFeatureDelta)delta).copy());
    }

    return list;
  }

  @Override
  public void write(CDODataOutput out, EClass eClass) throws IOException
  {
    super.write(out, eClass);
    out.writeInt(featureDeltas.size());
    for (CDOFeatureDelta featureDelta : featureDeltas)
    {
      out.writeCDOFeatureDelta(featureDelta, eClass);
    }
  }

  public Type getType()
  {
    return Type.LIST;
  }

  public List<CDOFeatureDelta> getListChanges()
  {
    return featureDeltas;
  }

  /**
   * Returns the number of indices as the first element of the array.
   * 
   * @return never <code>null</code>.
   */
  public Pair<IListTargetAdding[], int[]> reconstructAddedIndices()
  {
    reconstructAddedIndicesWithNoCopy();
    return new Pair<IListTargetAdding[], int[]>(copyOf(cacheSources, cacheSources.length, cacheSources.getClass()),
        copyOf(cacheIndices, cacheIndices.length));
  }

  private void reconstructAddedIndicesWithNoCopy()
  {
    if (cacheIndices == null || notProcessedFeatureDelta != null)
    {
      if (cacheIndices == null)
      {
        cacheIndices = new int[1 + featureDeltas.size()];
      }
      else if (cacheIndices.length <= 1 + featureDeltas.size())
      {
        int newCapacity = Math.max(10, cacheIndices.length * 3 / 2 + 1);
        int[] newElements = new int[newCapacity];
        System.arraycopy(cacheIndices, 0, newElements, 0, cacheIndices.length);
        cacheIndices = newElements;
      }

      if (cacheSources == null)
      {
        cacheSources = new IListTargetAdding[1 + featureDeltas.size()];
      }
      else if (cacheSources.length <= 1 + featureDeltas.size())
      {
        int newCapacity = Math.max(10, cacheSources.length * 3 / 2 + 1);
        IListTargetAdding[] newElements = new IListTargetAdding[newCapacity];
        System.arraycopy(cacheSources, 0, newElements, 0, cacheSources.length);
        cacheSources = newElements;
      }

      List<CDOFeatureDelta> featureDeltasToBeProcess = notProcessedFeatureDelta == null ? featureDeltas
          : notProcessedFeatureDelta;

      for (CDOFeatureDelta featureDelta : featureDeltasToBeProcess)
      {
        if (featureDelta instanceof IListIndexAffecting)
        {
          IListIndexAffecting affecting = (IListIndexAffecting)featureDelta;
          affecting.affectIndices(cacheSources, cacheIndices);
        }

        if (featureDelta instanceof IListTargetAdding)
        {
          cacheIndices[++cacheIndices[0]] = ((IListTargetAdding)featureDelta).getIndex();
          cacheSources[cacheIndices[0]] = (IListTargetAdding)featureDelta;
        }
      }

      notProcessedFeatureDelta = null;
    }
  }

  private void cleanupWithNewDelta(CDOFeatureDelta featureDelta)
  {
    EStructuralFeature feature = getFeature();
    if (feature instanceof EReference && featureDelta instanceof CDORemoveFeatureDelta)
    {
      int indexToRemove = ((CDORemoveFeatureDelta)featureDelta).getIndex();
      reconstructAddedIndicesWithNoCopy();

      for (int i = 1; i <= cacheIndices[0]; i++)
      {
        int index = cacheIndices[i];
        if (indexToRemove == index)
        {
          cacheSources[i].clear();
          break;
        }
      }
    }

    if (cacheIndices != null)
    {
      if (notProcessedFeatureDelta == null)
      {
        notProcessedFeatureDelta = new ArrayList<CDOFeatureDelta>();
      }

      notProcessedFeatureDelta.add(featureDelta);
    }
  }

  public void add(CDOFeatureDelta featureDelta)
  {
    cleanupWithNewDelta(featureDelta);
    featureDeltas.add(featureDelta);
  }

  public void apply(CDORevision revision)
  {
    for (CDOFeatureDelta featureDelta : featureDeltas)
    {
      ((CDOFeatureDeltaImpl)featureDelta).apply(revision);
    }
  }

  @Override
  public void adjustReferences(CDOReferenceAdjuster adjuster)
  {
    for (CDOFeatureDelta featureDelta : featureDeltas)
    {
      ((CDOFeatureDeltaImpl)featureDelta).adjustReferences(adjuster);
    }
  }

  public void accept(CDOFeatureDeltaVisitor visitor)
  {
    visitor.visit(this);
  }

  /**
   * Copied from JAVA 1.6 {@link Arrays Arrays.copyOf}.
   */
  @SuppressWarnings("unchecked")
  private static <T, U> T[] copyOf(U[] original, int newLength, Class<? extends T[]> newType)
  {
    T[] copy = (Object)newType == (Object)Object[].class ? (T[])new Object[newLength] : (T[])Array.newInstance(newType
        .getComponentType(), newLength);
    System.arraycopy(original, 0, copy, 0, Math.min(original.length, newLength));
    return copy;
  }

  /**
   * Copied from JAVA 1.6 {@link Arrays Arrays.copyOf}.
   */
  private static int[] copyOf(int[] original, int newLength)
  {
    int[] copy = new int[newLength];
    System.arraycopy(original, 0, copy, 0, Math.min(original.length, newLength));
    return copy;
  }
}
