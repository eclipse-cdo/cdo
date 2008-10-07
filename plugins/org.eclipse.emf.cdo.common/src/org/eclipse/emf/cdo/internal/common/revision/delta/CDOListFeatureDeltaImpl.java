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

import org.eclipse.emf.cdo.common.CDODataInput;
import org.eclipse.emf.cdo.common.CDODataOutput;
import org.eclipse.emf.cdo.common.model.CDOClass;
import org.eclipse.emf.cdo.common.model.CDOFeature;
import org.eclipse.emf.cdo.common.revision.CDOReferenceAdjuster;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.delta.CDOFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDOFeatureDeltaVisitor;
import org.eclipse.emf.cdo.common.revision.delta.CDOListFeatureDelta;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Simon McDuff
 */
public class CDOListFeatureDeltaImpl extends CDOFeatureDeltaImpl implements CDOListFeatureDelta
{
  private List<CDOFeatureDelta> featureDeltas = new ArrayList<CDOFeatureDelta>();

  public CDOListFeatureDeltaImpl(CDOFeature feature)
  {
    super(feature);
  }

  public CDOListFeatureDeltaImpl(CDODataInput in, CDOClass cdoClass) throws IOException
  {
    super(in, cdoClass);
    int size = in.readInt();
    for (int i = 0; i < size; i++)
    {
      featureDeltas.add(in.readCDOFeatureDelta(cdoClass));
    }
  }

  @Override
  public void write(CDODataOutput out, CDOClass cdoClass) throws IOException
  {
    super.write(out, cdoClass);
    out.writeInt(featureDeltas.size());
    for (CDOFeatureDelta featureDelta : featureDeltas)
    {
      out.writeCDOFeatureDelta(featureDelta, cdoClass);
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
  public int[] reconstructAddedIndices()
  {
    int[] indices = new int[1 + featureDeltas.size()];
    for (CDOFeatureDelta featureDelta : featureDeltas)
    {
      if (featureDelta instanceof IListIndexAffecting)
      {
        IListIndexAffecting affecting = (IListIndexAffecting)featureDelta;
        affecting.affectIndices(indices);
      }

      if (featureDelta instanceof IListTargetAdding)
      {
        indices[++indices[0]] = ((IListTargetAdding)featureDelta).getIndex();
      }
    }

    return indices;
  }

  public void add(CDOFeatureDelta featureDelta)
  {
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
}
