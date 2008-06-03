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
package org.eclipse.emf.internal.cdo.analyzer;

import org.eclipse.emf.cdo.common.model.CDOClass;
import org.eclipse.emf.cdo.common.model.CDOFeature;

/**
 * @author Simon McDuff
 */
public class CDOClusterOfFetchRule
{
  private CDOAnalyzerFeatureInfo featureInfo = new CDOAnalyzerFeatureInfo();

  private CDOFeature rootFeature;

  private CDOClass rootClass;

  private long lastUpdate;

  public CDOClusterOfFetchRule(CDOClass rootClass, CDOFeature rootFeature)
  {
    this.rootFeature = rootFeature;
    this.rootClass = rootClass;
    lastUpdate = System.currentTimeMillis();
  }

  public CDOFeature getRootFeature()
  {
    return rootFeature;
  }

  public CDOAnalyzerFeatureInfo getFeatureInfo()
  {
    return featureInfo;
  }

  public long getLastUpdate()
  {
    return lastUpdate;
  }

  @Override
  public int hashCode()
  {
    return rootClass.hashCode() ^ rootFeature.hashCode();
  }

  @Override
  public boolean equals(Object obj)
  {
    if (obj == this)
    {
      return true;
    }

    if (obj instanceof CDOClusterOfFetchRule)
    {
      CDOClusterOfFetchRule featureInfo = (CDOClusterOfFetchRule)obj;
      return featureInfo.rootClass == rootClass && featureInfo.rootFeature == rootFeature;
    }

    return false;
  }
}
