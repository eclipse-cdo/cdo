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
import org.eclipse.emf.cdo.common.revision.delta.CDOFeatureDelta;

import java.io.IOException;

/**
 * @author Simon McDuff
 */
public abstract class CDOFeatureDeltaImpl implements InternalCDOFeatureDelta
{
  public static final int NO_INDEX = -1;

  private CDOFeature feature;

  protected CDOFeatureDeltaImpl(CDOFeature feature)
  {
    this.feature = feature;
  }

  public CDOFeatureDeltaImpl(CDODataInput in, CDOClass cdoClass) throws IOException
  {
    int featureID = in.readInt();
    feature = cdoClass.getAllFeatures()[featureID];
  }

  public void write(CDODataOutput out, CDOClass cdoClass) throws IOException
  {
    out.writeInt(getType().ordinal());
    out.writeInt(cdoClass.getFeatureID(feature));
  }

  public CDOFeature getFeature()
  {
    return feature;
  }

  public CDOFeatureDelta copy()
  {
    return this;
  }

  public abstract void adjustReferences(CDOReferenceAdjuster referenceAdjuster);
}
