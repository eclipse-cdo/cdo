/*
 * Copyright (c) 2011, 2012, 2016, 2019, 2020 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.internal.cdo.object;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.CDOObjectReference;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDReference;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;

/**
 * @author Eike Stepper
 * @since 4.0
 */
public class CDOObjectReferenceImpl implements CDOObjectReference
{
  private CDOView view;

  private CDOIDReference delegate;

  public CDOObjectReferenceImpl(CDOView view, CDOIDReference delegate)
  {
    this.view = view;
    this.delegate = delegate;
  }

  @Override
  public CDOObject getTargetObject()
  {
    return view.getObject(getTargetID());
  }

  @Override
  public CDOID getTargetID()
  {
    return delegate.getTargetObject();
  }

  @Override
  public CDOObject getSourceObject()
  {
    return view.getObject(getSourceID());
  }

  @Override
  public CDOID getSourceID()
  {
    return delegate.getSourceObject();
  }

  @Deprecated
  @Override
  public EStructuralFeature getSourceFeature()
  {
    return delegate.getSourceFeature();
  }

  @Override
  public EReference getSourceReference()
  {
    return delegate.getSourceReference();
  }

  @Override
  public int getSourceIndex()
  {
    return delegate.getSourceIndex();
  }

  @Override
  public String toString()
  {
    StringBuilder builder = new StringBuilder();
    builder.append(getSourceObject());
    builder.append(".");
    builder.append(getSourceFeature().getName());
    int sourceIndex = getSourceIndex();
    if (sourceIndex != NO_INDEX)
    {
      builder.append("[");
      builder.append(sourceIndex);
      builder.append("]");
    }

    builder.append(" --> ");
    builder.append(getTargetObject());
    return builder.toString();
  }
}
