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
import org.eclipse.jdt.debug.core.IJavaFieldVariable;
import org.eclipse.jdt.debug.core.IJavaObject;
import org.eclipse.jdt.debug.core.IJavaThread;
import org.eclipse.jdt.debug.core.IJavaType;
import org.eclipse.jdt.debug.core.IJavaValue;

import java.util.Objects;

/**
 * @author Eike Stepper
 */
public class DelegatingObject implements IJavaObject
{
  private final IJavaObject delegate;

  private final IVariable[] variables;

  public DelegatingObject(IJavaObject delegate, IVariable[] variables)
  {
    this.delegate = Objects.requireNonNull(delegate);
    this.variables = variables;
  }

  @Override
  public String getLabel() throws DebugException
  {
    return delegate.getLabel();
  }

  @Override
  public void setLabel(String newLabel) throws DebugException
  {
    delegate.setLabel(newLabel);
  }

  @Override
  public IJavaValue sendMessage(String selector, String signature, IJavaValue[] args, IJavaThread thread,
      boolean superSend) throws DebugException
  {
    return delegate.sendMessage(selector, signature, args, thread, superSend);
  }

  @Override
  public IJavaValue sendMessage(String selector, String signature, IJavaValue[] args, IJavaThread thread,
      String typeSignature) throws DebugException
  {
    return delegate.sendMessage(selector, signature, args, thread, typeSignature);
  }

  @Override
  public IJavaFieldVariable getField(String name, boolean superField) throws DebugException
  {
    return delegate.getField(name, superField);
  }

  @Override
  public IJavaFieldVariable getField(String name, String typeSignature) throws DebugException
  {
    return delegate.getField(name, typeSignature);
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
  public String getReferenceTypeName() throws DebugException
  {
    return delegate.getReferenceTypeName();
  }

  @Override
  public String getValueString() throws DebugException
  {
    return delegate.getValueString();
  }

  @Override
  public boolean isAllocated() throws DebugException
  {
    return delegate.isAllocated();
  }

  @Override
  public boolean isNull()
  {
    return delegate.isNull();
  }

  @Override
  public IVariable[] getVariables()
  {
    return variables;
  }

  @Override
  public boolean hasVariables()
  {
    return variables.length > 0;
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
  public ILaunch getLaunch()
  {
    return delegate.getLaunch();
  }

  @Override
  public <T> T getAdapter(Class<T> adapter)
  {
    return delegate.getAdapter(adapter);
  }

  @Override
  public IJavaThread[] getWaitingThreads() throws DebugException
  {
    return delegate.getWaitingThreads();
  }

  @Override
  public IJavaThread getOwningThread() throws DebugException
  {
    return delegate.getOwningThread();
  }

  @Override
  public IJavaObject[] getReferringObjects(long max) throws DebugException
  {
    return delegate.getReferringObjects(max);
  }

  @Override
  public void disableCollection() throws DebugException
  {
    delegate.disableCollection();
  }

  @Override
  public void enableCollection() throws DebugException
  {
    delegate.enableCollection();
  }

  @Override
  public long getUniqueId() throws DebugException
  {
    return delegate.getUniqueId();
  }
}
