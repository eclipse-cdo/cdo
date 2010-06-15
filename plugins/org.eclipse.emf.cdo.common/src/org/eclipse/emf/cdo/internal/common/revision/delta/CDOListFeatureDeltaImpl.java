/**
 * Copyright (c) 2004 - 2010 Eike Stepper (Berlin, Germany) and others.
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
import org.eclipse.emf.ecore.util.FeatureMapUtil;

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

  private transient int[] cachedIndices;

  private transient IListTargetAdding[] cachedSources;

  private transient List<CDOFeatureDelta> unprocessedFeatureDeltas;

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
      out.writeCDOFeatureDelta(eClass, featureDelta);
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
    return new Pair<IListTargetAdding[], int[]>(copyOf(cachedSources, cachedSources.length, cachedSources.getClass()),
        copyOf(cachedIndices, cachedIndices.length));
  }

  private void reconstructAddedIndicesWithNoCopy()
  {
    if (cachedIndices == null || unprocessedFeatureDeltas != null)
    {
      List<CDOFeatureDelta> featureDeltasToBeProcessed = unprocessedFeatureDeltas == null ? featureDeltas
          : unprocessedFeatureDeltas;

      // Actually the required capacity is the number of ListTargetAdding instances in the
      // featureDeltasToBeProcessed.. so this is an overestimate
      int requiredCapacity = featureDeltasToBeProcessed.size() + 1;

      if (cachedIndices == null)
      {
        cachedIndices = new int[1 + featureDeltas.size()];
      }
      else if (cachedIndices.length < requiredCapacity)
      {
        int newCapacity = Math.max(requiredCapacity, cachedIndices.length * 3 / 2);
        int[] newElements = new int[newCapacity];
        System.arraycopy(cachedIndices, 0, newElements, 0, cachedIndices.length);
        cachedIndices = newElements;
      }

      if (cachedSources == null)
      {
        cachedSources = new IListTargetAdding[requiredCapacity];
      }
      else if (cachedSources.length <= requiredCapacity)
      {
        int newCapacity = Math.max(requiredCapacity, cachedSources.length * 3 / 2);
        IListTargetAdding[] newElements = new IListTargetAdding[newCapacity];
        System.arraycopy(cachedSources, 0, newElements, 0, cachedSources.length);
        cachedSources = newElements;
      }

      for (CDOFeatureDelta featureDelta : featureDeltasToBeProcessed)
      {
        if (featureDelta instanceof IListIndexAffecting)
        {
          IListIndexAffecting affecting = (IListIndexAffecting)featureDelta;
          affecting.affectIndices(cachedSources, cachedIndices);
        }

        if (featureDelta instanceof IListTargetAdding)
        {
          cachedIndices[++cachedIndices[0]] = ((IListTargetAdding)featureDelta).getIndex();
          cachedSources[cachedIndices[0]] = (IListTargetAdding)featureDelta;
        }
      }

      unprocessedFeatureDeltas = null;
    }
  }

  private void cleanupWithNewDelta(CDOFeatureDelta featureDelta)
  {
    EStructuralFeature feature = getFeature();
    if ((feature instanceof EReference || FeatureMapUtil.isFeatureMap(feature))
        && featureDelta instanceof CDORemoveFeatureDelta)
    {
      int indexToRemove = ((CDORemoveFeatureDelta)featureDelta).getIndex();
      reconstructAddedIndicesWithNoCopy();

      for (int i = 1; i <= cachedIndices[0]; i++)
      {
        int index = cachedIndices[i];
        if (indexToRemove == index)
        {
          cachedSources[i].clear();
          break;
        }
      }
    }

    if (cachedIndices != null)
    {
      if (unprocessedFeatureDeltas == null)
      {
        unprocessedFeatureDeltas = new ArrayList<CDOFeatureDelta>();
      }

      unprocessedFeatureDeltas.add(featureDelta);
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

  @Override
  protected String toStringAdditional()
  {
    return "list=" + featureDeltas;
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
