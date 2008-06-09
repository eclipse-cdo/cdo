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
import org.eclipse.emf.cdo.common.revision.CDORevisionUtil;
import org.eclipse.emf.cdo.common.revision.delta.CDOFeatureDelta;

import org.eclipse.net4j.util.io.ExtendedDataInput;
import org.eclipse.net4j.util.io.ExtendedDataOutput;

import java.io.IOException;
import java.util.Map;

/**
 * @author Simon McDuff
 */
public abstract class CDOSingleValueFeatureDeltaImpl extends CDOFeatureDeltaImpl implements CDOFeatureDelta
{
  private int index;

  private Object newValue;

  public CDOSingleValueFeatureDeltaImpl(CDOFeature feature, int index, Object value)
  {
    super(feature);
    this.index = index;
    newValue = value;
  }

  public CDOSingleValueFeatureDeltaImpl(ExtendedDataInput in, CDOClass cdoClass) throws IOException
  {
    super(in, cdoClass);
    index = in.readInt();
    newValue = getFeature().getType().readValue(in, cdoClass.getPackageManager().getCDOIDObjectFactory());
  }

  public int getIndex()
  {
    return index;
  }

  public Object getValue()
  {
    return newValue;
  }

  @Override
  public void write(ExtendedDataOutput out, CDOClass cdoClass, CDOIDProvider idProvider) throws IOException
  {
    super.write(out, cdoClass, idProvider);
    out.writeInt(index);
    if (newValue != null && getFeature().isReference())
    {
      newValue = idProvider.provideCDOID(newValue);
    }

    getFeature().getType().writeValue(out, newValue);
  }

  @Override
  public void adjustReferences(Map<CDOIDTemp, CDOID> idMappings)
  {
    if (newValue instanceof CDOID)
    {
      newValue = CDORevisionUtil.remapID(newValue, idMappings);
    }
  }
}
