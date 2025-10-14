/*
 * Copyright (c) 2007-2009, 2011, 2012, 2014, 2025 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Simon McDuff - initial API and implementation
 *    Eike Stepper - maintenance
 */
package org.eclipse.emf.internal.cdo.analyzer;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EStructuralFeature;

import java.util.Objects;

/**
 * @author Simon McDuff
 */
public class CDOClusterOfFetchRule
{
  private final EClass rootClass;

  private final EStructuralFeature rootFeature;

  private final CDOAnalyzerFeatureInfo featureInfo = new CDOAnalyzerFeatureInfo();

  private long lastUpdate;

  public CDOClusterOfFetchRule(EClass rootClass, EStructuralFeature rootFeature)
  {
    this.rootClass = rootClass;
    this.rootFeature = rootFeature;
    lastUpdate = System.currentTimeMillis();
  }

  public EClass getRootClass()
  {
    return rootClass;
  }

  public EStructuralFeature getRootFeature()
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
    return Objects.hash(rootClass, rootFeature);
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
      CDOClusterOfFetchRule other = (CDOClusterOfFetchRule)obj;
      return other.rootClass == rootClass && other.rootFeature == rootFeature;
    }

    return false;
  }
}
