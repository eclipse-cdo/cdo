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
import org.eclipse.emf.cdo.common.revision.delta.CDOFeatureDelta;

import org.eclipse.net4j.util.io.ExtendedDataInput;
import org.eclipse.net4j.util.io.ExtendedDataOutput;

import java.io.IOException;
import java.util.Map;

/**
 * @author Simon McDuff
 */
public abstract class CDOFeatureDeltaImpl implements CDOFeatureDelta
{
  public static final int NO_INDEX = -1;

  private CDOFeature feature;

  protected CDOFeatureDeltaImpl(CDOFeature feature)
  {
    this.feature = feature;
  }

  public CDOFeatureDeltaImpl(ExtendedDataInput in, CDOClass cdoClass) throws IOException
  {
    int featureID = in.readInt();
    feature = cdoClass.getAllFeatures()[featureID];
  }

  public CDOFeature getFeature()
  {
    return feature;
  }

  public abstract void adjustReferences(Map<CDOIDTemp, CDOID> idMappings);

  public void write(ExtendedDataOutput out, CDOClass cdoClass, CDOIDProvider idProvider) throws IOException
  {
    out.writeInt(getType().ordinal());
    out.writeInt(cdoClass.getFeatureID(feature));
  }

  public static CDOFeatureDeltaImpl read(ExtendedDataInput in, CDOClass cdoClass) throws IOException
  {
    int typeOrdinal = in.readInt();
    Type type = Type.values()[typeOrdinal];
    switch (type)
    {
    case ADD:
      return new CDOAddFeatureDeltaImpl(in, cdoClass);
    case SET:
      return new CDOSetFeatureDeltaImpl(in, cdoClass);
    case LIST:
      return new CDOListFeatureDeltaImpl(in, cdoClass);
    case MOVE:
      return new CDOMoveFeatureDeltaImpl(in, cdoClass);
    case CLEAR:
      return new CDOClearFeatureDeltaImpl(in, cdoClass);
    case REMOVE:
      return new CDORemoveFeatureDeltaImpl(in, cdoClass);
    case CONTAINER:
      return new CDOContainerFeatureDeltaImpl(in, cdoClass);
    case UNSET:
      return new CDOUnsetFeatureDeltaImpl(in, cdoClass);
    default:
      // TODO Find better exception for signalling errors
      throw new UnsupportedOperationException("Invalid type " + typeOrdinal);
    }
  }
}
