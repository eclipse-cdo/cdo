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
package org.eclipse.emf.cdo.common.analyzer;

import org.eclipse.emf.cdo.common.model.CDOClass;
import org.eclipse.emf.cdo.common.model.CDOClassRef;
import org.eclipse.emf.cdo.common.model.CDOFeature;
import org.eclipse.emf.cdo.common.model.CDOModelUtil;
import org.eclipse.emf.cdo.common.model.CDOPackageManager;

import org.eclipse.net4j.util.io.ExtendedDataInput;
import org.eclipse.net4j.util.io.ExtendedDataOutput;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Simon McDuff
 */
public final class CDOFetchRule
{
  private CDOClass cdoClass;

  private List<CDOFeature> features = new ArrayList<CDOFeature>(0);

  public CDOFetchRule(CDOClass cdoClass)
  {
    this.cdoClass = cdoClass;
  }

  public CDOFetchRule(ExtendedDataInput in, CDOPackageManager packageManager) throws IOException
  {
    CDOClassRef classRef = CDOModelUtil.readClassRef(in);
    cdoClass = classRef.resolve(packageManager);
    int size = in.readInt();
    for (int i = 0; i < size; i++)
    {
      int featureID = in.readInt();
      CDOFeature feature = cdoClass.lookupFeature(featureID);
      features.add(feature);
    }
  }

  public void write(ExtendedDataOutput out) throws IOException
  {
    CDOModelUtil.writeClassRef(out, cdoClass.createClassRef());
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
