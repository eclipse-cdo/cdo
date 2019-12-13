/*
 * Copyright (c) 2009, 2011, 2012, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.internal.common.revision;

import org.eclipse.emf.cdo.spi.common.revision.CDOFeatureMapEntry;

import org.eclipse.emf.ecore.EStructuralFeature;

import java.text.MessageFormat;

/**
 * @author Eike Stepper
 */
public class CDOFeatureMapEntryImpl implements CDOFeatureMapEntry
{
  private EStructuralFeature feature;

  private Object value;

  public CDOFeatureMapEntryImpl()
  {
  }

  public CDOFeatureMapEntryImpl(EStructuralFeature feature, Object value)
  {
    this.feature = feature;
    this.value = value;
  }

  @Override
  public EStructuralFeature getEStructuralFeature()
  {
    return feature;
  }

  @Override
  public void setEStructuralFeature(EStructuralFeature feature)
  {
    this.feature = feature;
  }

  @Override
  public Object getValue()
  {
    return value;
  }

  @Override
  public void setValue(Object value)
  {
    this.value = value;
  }

  @Override
  public String toString()
  {
    return MessageFormat.format("CDOFeatureMapEntry({0}, {1})", feature.getName(), value); //$NON-NLS-1$
  }
}
