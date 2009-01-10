/***************************************************************************
 * Copyright (c) 2004 - 2008 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Simon McDuff - http://bugs.eclipse.org/201266
 *    Simon McDuff - http://bugs.eclipse.org/212958
 *    Simon McDuff - http://bugs.eclipse.org/213402
 **************************************************************************/
package org.eclipse.emf.cdo.internal.common.revision;

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.io.CDODataInput;
import org.eclipse.emf.cdo.common.model.CDOClass;
import org.eclipse.emf.cdo.common.model.CDOFeature;
import org.eclipse.emf.cdo.common.model.CDOType;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.spi.common.revision.AbstractCDORevision;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDOList;

import java.io.IOException;

/**
 * @author Eike Stepper
 */
public class CDORevisionImpl extends AbstractCDORevision
{
  private Object[] values;

  public CDORevisionImpl(CDOClass cdoClass, CDOID id)
  {
    super(cdoClass, id);
  }

  public CDORevisionImpl(CDODataInput in) throws IOException
  {
    super(in);
  }

  public CDORevisionImpl(CDORevisionImpl source)
  {
    super(source);
    CDOFeature[] features = getCDOClass().getAllFeatures();
    initValues(features.length);
    for (int i = 0; i < features.length; i++)
    {
      CDOFeature feature = features[i];
      CDOType type = feature.getType();
      if (feature.isMany())
      {
        InternalCDOList sourceList = (InternalCDOList)source.values[i];
        if (sourceList != null)
        {
          setValue(i, sourceList.clone(type));
        }
      }
      else
      {
        setValue(i, type.copyValue(source.values[i]));
      }
    }
  }

  public CDORevision copy()
  {
    return new CDORevisionImpl(this);
  }

  @Override
  protected void initValues(int size)
  {
    values = new Object[size];
  }

  @Override
  protected Object getValue(int i)
  {
    return values[i];
  }

  @Override
  protected void setValue(int i, Object value)
  {
    values[i] = value;
  }
}
