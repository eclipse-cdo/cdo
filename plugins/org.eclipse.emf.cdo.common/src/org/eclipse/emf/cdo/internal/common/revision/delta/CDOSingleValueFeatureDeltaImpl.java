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
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.model.CDOClass;
import org.eclipse.emf.cdo.common.model.CDOFeature;
import org.eclipse.emf.cdo.common.revision.CDOReferenceAdjuster;
import org.eclipse.emf.cdo.common.revision.delta.CDOFeatureDelta;

import java.io.IOException;

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

  public CDOSingleValueFeatureDeltaImpl(CDODataInput in, CDOClass cdoClass) throws IOException
  {
    super(in, cdoClass);
    index = in.readInt();
    newValue = getFeature().getType().readValue(in);
  }

  @Override
  public void write(CDODataOutput out, CDOClass cdoClass) throws IOException
  {
    super.write(out, cdoClass);
    out.writeInt(index);
    Object valueToWrite = newValue;
    if (valueToWrite != null && getFeature().isReference())
    {
      valueToWrite = out.getIDProvider().provideCDOID(newValue);
    }

    getFeature().getType().writeValue(out, valueToWrite);
  }

  public int getIndex()
  {
    return index;
  }

  public Object getValue()
  {
    return newValue;
  }

  protected void setValue(Object value)
  {
    newValue = value;
  }

  @Override
  public void adjustReferences(CDOReferenceAdjuster referenceAdjuster)
  {
    newValue = referenceAdjuster.adjustReference(newValue);
  }

  public void clear()
  {
    setValue(CDOID.NULL);
  }
}
