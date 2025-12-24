/*
 * Copyright (c) 2025 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.ecore.logicalstructure.util;

import org.eclipse.core.runtime.PlatformObject;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.debug.core.model.IValue;
import org.eclipse.jdt.debug.core.IJavaModifiers;
import org.eclipse.jdt.debug.core.IJavaType;
import org.eclipse.jdt.debug.core.IJavaValue;
import org.eclipse.jdt.debug.core.IJavaVariable;

/**
 * @author Eike Stepper
 */
public class Variable extends PlatformObject implements IJavaVariable
{
  private final String name;

  private final IValue value;

  public Variable(String name, IValue value)
  {
    this.name = name;
    this.value = value;
  }

  @Override
  public String getName()
  {
    return name;
  }

  @Override
  public IValue getValue()
  {
    return value;
  }

  @Override
  public String getSignature() throws DebugException
  {
    if (value instanceof IJavaValue)
    {
      IJavaValue javaValue = (IJavaValue)value;
      return javaValue.getSignature();
    }

    return null;
  }

  @Override
  public String getGenericSignature() throws DebugException
  {
    if (value instanceof IJavaValue)
    {
      IJavaValue javaValue = (IJavaValue)value;
      return javaValue.getGenericSignature();
    }

    return null;
  }

  @Override
  public IJavaType getJavaType() throws DebugException
  {
    if (value instanceof IJavaValue)
    {
      IJavaValue javaValue = (IJavaValue)value;
      return javaValue.getJavaType();
    }

    return null;
  }

  @Override
  public boolean isLocal()
  {
    return false;
  }

  @Override
  public String getReferenceTypeName() throws DebugException
  {
    if (value instanceof IJavaValue)
    {
      IJavaValue javaValue = (IJavaValue)value;
      return javaValue.getReferenceTypeName();
    }

    return null;
  }

  @Override
  public boolean hasValueChanged()
  {
    return false;
  }

  @Override
  public boolean isPublic()
  {
    return false;
  }

  @Override
  public boolean isPrivate()
  {
    return false;
  }

  @Override
  public boolean isProtected()
  {
    return true;
  }

  @Override
  public boolean isPackagePrivate()
  {
    return false;
  }

  @Override
  public boolean isFinal()
  {
    return false;
  }

  @Override
  public boolean isStatic()
  {
    return false;
  }

  @Override
  public boolean isSynthetic()
  {
    return false;
  }

  @Override
  public String getModelIdentifier()
  {
    return value.getModelIdentifier();
  }

  @Override
  public IDebugTarget getDebugTarget()
  {
    if (value instanceof IJavaValue)
    {
      IJavaValue javaValue = (IJavaValue)value;
      return javaValue.getDebugTarget();
    }

    return null;
  }

  @Override
  public ILaunch getLaunch()
  {
    return value.getLaunch();
  }

  @Override
  public void setValue(String expression)
  {
  }

  @Override
  public void setValue(IValue value)
  {
  }

  @Override
  public boolean supportsValueModification()
  {
    return false;
  }

  @Override
  public boolean verifyValue(String expression)
  {
    return false;
  }

  @Override
  public boolean verifyValue(IValue value)
  {
    return false;
  }

  @Override
  @SuppressWarnings("unchecked")
  public <T> T getAdapter(Class<T> adapter)
  {
    if (IJavaVariable.class.equals(adapter) || IJavaModifiers.class.equals(adapter))
    {
      return (T)this;
    }

    return super.getAdapter(adapter);
  }

  @Override
  public boolean equals(Object obj)
  {
    if (obj instanceof Variable)
    {
      Variable var = (Variable)obj;
      return var.getName().equals(getName()) && var.value.equals(value);
    }

    return false;
  }

  @Override
  public int hashCode()
  {
    return name.hashCode() + value.hashCode();
  }

  /**
   * @author Eike Stepper
   */
  public static class Private extends Variable
  {
    public Private(String name, IValue value)
    {
      super(name, value);
    }

    @Override
    public boolean isPrivate()
    {
      return true;
    }

    @Override
    public boolean isProtected()
    {
      return false;
    }
  }
}
