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

import org.eclipse.core.runtime.PlatformObject;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.debug.core.model.IVariable;
import org.eclipse.jdt.debug.core.IJavaType;
import org.eclipse.jdt.debug.core.IJavaValue;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Eike Stepper
 */
public abstract class ContainerValue extends PlatformObject implements IJavaValue
{
  private final Scope scope;

  private IVariable[] variables;

  public ContainerValue(Scope scope)
  {
    this.scope = scope;
  }

  public Scope getScope()
  {
    return scope;
  }

  @Override
  public String getModelIdentifier()
  {
    return scope.getModelIdentifier();
  }

  @Override
  public IDebugTarget getDebugTarget()
  {
    return scope.getDebugTarget();
  }

  @Override
  public ILaunch getLaunch()
  {
    return scope.getLaunch();
  }

  @Override
  public String getReferenceTypeName() throws DebugException
  {
    return "";
  }

  @Override
  public String getValueString()
  {
    return "";
  }

  @Override
  public boolean isAllocated() throws DebugException
  {
    return true;
  }

  @Override
  public String getSignature() throws DebugException
  {
    return "";
  }

  @Override
  public String getGenericSignature() throws DebugException
  {
    return "";
  }

  @Override
  public IJavaType getJavaType() throws DebugException
  {
    return null;
  }

  @Override
  public boolean isNull()
  {
    return false;
  }

  @Override
  public boolean hasVariables() throws DebugException
  {
    return true;
  }

  @Override
  public IVariable[] getVariables() throws DebugException
  {
    if (variables == null)
    {
      Variables creator = new Variables();
      initVariables(creator);

      variables = creator.getArray();
    }

    return variables;
  }

  protected abstract void initVariables(Variables variables) throws DebugException;

  /**
   * @author Eike Stepper
   */
  public final class Variables
  {
    private final List<IVariable> list = new ArrayList<>();

    private IVariable[] getArray()
    {
      return list.toArray(new IVariable[list.size()]);
    }

    public void add(String name, String expression) throws DebugException
    {
      Variable variable = new Variable(name, scope.evaluate(expression));
      list.add(variable);
    }

    public void add(String name, ContainerValue container) throws DebugException
    {
      Variable variable = new Variable.Private(name, container);
      list.add(variable);
    }
  }
}
