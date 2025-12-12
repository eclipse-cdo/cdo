/*
 * Copyright (c) 2025 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.ecore.logicalstructure.util;

import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.debug.core.model.IVariable;
import org.eclipse.jdt.debug.core.IJavaType;
import org.eclipse.jdt.debug.core.IJavaValue;

/**
 * @author Eike Stepper
 */
public class DelegatingValue implements IJavaValue
{
  private final IJavaValue delegate;

  public DelegatingValue(IJavaValue delegate)
  {
    this.delegate = delegate;
  }

  @Override
  public <T> T getAdapter(Class<T> adapter)
  {
    return delegate.getAdapter(adapter);
  }

  @Override
  public String getReferenceTypeName() throws DebugException
  {
    return delegate.getReferenceTypeName();
  }

  @Override
  public String getModelIdentifier()
  {
    return delegate.getModelIdentifier();
  }

  @Override
  public IDebugTarget getDebugTarget()
  {
    return delegate.getDebugTarget();
  }

  @Override
  public String getValueString() throws DebugException
  {
    return delegate.getValueString();
  }

  @Override
  public ILaunch getLaunch()
  {
    return delegate.getLaunch();
  }

  @Override
  public boolean isAllocated() throws DebugException
  {
    return delegate.isAllocated();
  }

  @Override
  public IVariable[] getVariables() throws DebugException
  {
    return delegate.getVariables();
  }

  @Override
  public boolean hasVariables() throws DebugException
  {
    return delegate.hasVariables();
  }

  @Override
  public String getSignature() throws DebugException
  {
    return delegate.getSignature();
  }

  @Override
  public String getGenericSignature() throws DebugException
  {
    return delegate.getGenericSignature();
  }

  @Override
  public IJavaType getJavaType() throws DebugException
  {
    return delegate.getJavaType();
  }

  @Override
  public boolean isNull()
  {
    return delegate.isNull();
  }
}
