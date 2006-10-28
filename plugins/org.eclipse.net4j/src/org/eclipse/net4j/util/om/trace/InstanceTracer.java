/***************************************************************************
 * Copyright (c) 2004, 2005, 2006 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.net4j.util.om.trace;

import org.eclipse.net4j.util.om.OMBundle;
import org.eclipse.net4j.util.om.OMTracer;

/**
 * @author Eike Stepper
 */
public class InstanceTracer
{
  private OMTracer delegate;

  private Class context;

  private Object instance;

  public InstanceTracer(OMTracer delegate, Class context, Object instance)
  {
    this.delegate = delegate;
    this.context = context;
    this.instance = instance;
  }

  public OMBundle getBundle()
  {
    return delegate.getBundle();
  }

  public String getFullName()
  {
    return delegate.getFullName();
  }

  public String getName()
  {
    return delegate.getName();
  }

  public OMTracer getParent()
  {
    return delegate.getParent();
  }

  public boolean isEnabled()
  {
    return delegate.isEnabled();
  }

  public void setEnabled(boolean enabled)
  {
    delegate.setEnabled(enabled);
  }

  public void trace(String pattern, Object... args)
  {
    delegate.trace(context, instance, pattern, args);
  }

  public void trace(String pattern, Throwable t, Object... args)
  {
    delegate.trace(context, instance, pattern, t, args);
  }

  public void trace(String msg, Throwable t)
  {
    delegate.trace(context, instance, msg, t);
  }

  public void trace(String msg)
  {
    delegate.trace(context, instance, msg);
  }

  public void trace(Throwable t)
  {
    delegate.trace(context, instance, t);
  }
}
