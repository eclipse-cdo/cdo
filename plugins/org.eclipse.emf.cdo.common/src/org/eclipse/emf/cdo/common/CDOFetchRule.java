/***************************************************************************
 * Copyright (c) 2004 - 2009 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Simon McDuff - initial API and implementation
 *    Eike Stepper - maintenance
 **************************************************************************/
package org.eclipse.emf.cdo.common;

import org.eclipse.emf.cdo.common.io.CDODataInput;
import org.eclipse.emf.cdo.common.io.CDODataOutput;
import org.eclipse.emf.cdo.common.model.CDOClass;
import org.eclipse.emf.cdo.common.model.CDOFeature;
import org.eclipse.emf.cdo.common.model.CDOPackageManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Simon McDuff
 * @since 2.0
 */
public final class CDOFetchRule
{
  private CDOClass cdoClass;

  private List<CDOFeature> features = new ArrayList<CDOFeature>(0);

  public CDOFetchRule(CDOClass cdoClass)
  {
    this.cdoClass = cdoClass;
  }

  public CDOFetchRule(CDODataInput in, CDOPackageManager packageManager) throws IOException
  {
    cdoClass = in.readCDOClassRefAndResolve();
    int size = in.readInt();
    for (int i = 0; i < size; i++)
    {
      int featureID = in.readInt();
      CDOFeature feature = cdoClass.lookupFeature(featureID);
      features.add(feature);
    }
  }

  public void write(CDODataOutput out) throws IOException
  {
    out.writeCDOClassRef(cdoClass);
    out.writeInt(features.size());
    for (CDOFeature feature : features)
    {
      out.writeInt(feature.getFeatureID());
    }
  }

  public CDOClass getCDOClass()
  {
    return cdoClass;
  }

  public List<CDOFeature> getFeatures()
  {
    return features;
  }

  public void addFeature(CDOFeature feature)
  {
    features.add(feature);
  }

  public void removeFeature(CDOFeature feature)
  {
    features.remove(feature);

  }

  public boolean isEmpty()
  {
    return features.isEmpty();
  }
}
