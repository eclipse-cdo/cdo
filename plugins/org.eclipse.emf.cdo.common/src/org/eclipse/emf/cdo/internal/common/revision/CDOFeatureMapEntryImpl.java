/**
 * Copyright (c) 2004 - 2010 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.internal.common.revision;

import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.util.FeatureMap;

import java.text.MessageFormat;

/**
 * @author Eike Stepper
 */
public class CDOFeatureMapEntryImpl implements FeatureMap.Entry
{
  private EStructuralFeature feature;

  private Object value;

  public CDOFeatureMapEntryImpl(EStructuralFeature feature, Object value)
  {
    this.feature = feature;
    this.value = value;
  }

  public EStructuralFeature getEStructuralFeature()
  {
    return feature;
  }

  public Object getValue()
  {
    return value;
  }

  @Override
  public String toString()
  {
    return MessageFormat.format("CDOFeatureMapEntry({0}, {1})", feature.getName(), value); //$NON-NLS-1$
  }
}
