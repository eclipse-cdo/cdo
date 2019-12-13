/*
 * Copyright (c) 2008, 2010-2012, 2014-2016, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.util.om.trace;

import org.eclipse.net4j.internal.util.bundle.AbstractPlatform;
import org.eclipse.net4j.util.om.OMBundle;
import org.eclipse.net4j.util.om.OMBundle.DebugSupport;
import org.eclipse.net4j.util.om.OMPlatform;

import java.text.MessageFormat;

/**
 * The default implementation of a {@link OMTracer tracer}.
 *
 * @author Eike Stepper
 */
public class Tracer implements OMTracer
{
  /**
   * @since 3.0
   */
  public static final String PROP_DISABLE_TRACING = "org.eclipse.net4j.util.om.trace.disable";

  /**
   * @since 3.6
   */
  public static final String PROP_DISABLE_TRACING_OPTIONS = "org.eclipse.net4j.util.om.trace.disable.options";

  private static final boolean IS_GLOBALLY_DISABLED = OMPlatform.INSTANCE.isProperty(PROP_DISABLE_TRACING);

  private OMBundle bundle;

  private Tracer parent;

  private String name;

  private String fullName;

  public Tracer(OMBundle bundle, String name)
  {
    this.bundle = bundle;
    this.name = name;
    fullName = name;
  }

  private Tracer(Tracer parent, String name)
  {
    bundle = parent.getBundle();
    this.parent = parent;
    this.name = name;
    fullName = parent.getFullName() + "." + name; //$NON-NLS-1$
  }

  @Override
  public OMBundle getBundle()
  {
    return bundle;
  }

  @Override
  public OMTracer getParent()
  {
    return parent;
  }

  @Override
  public String getName()
  {
    return name;
  }

  @Override
  public String getFullName()
  {
    return fullName;
  }

  @Override
  public boolean isEnabled()
  {
    if (IS_GLOBALLY_DISABLED)
    {
      return false;
    }

    DebugSupport debugSupport = bundle.getDebugSupport();
    return debugSupport.isDebugging() && debugSupport.getDebugOption(fullName, false);
  }

  @Override
  public void setEnabled(boolean enabled)
  {
    DebugSupport debugSupport = bundle.getDebugSupport();
    debugSupport.setDebugOption(fullName, enabled);
  }

  @Override
  public void trace(OMTraceHandlerEvent event)
  {
    ((AbstractPlatform)bundle.getPlatform()).trace(event);
  }

  @Override
  public OMTraceHandlerEvent trace(Class<?> context, String msg, Throwable t)
  {
    OMTraceHandlerEvent event = new TraceHandlerEvent(this, context, msg, t);
    trace(event);
    return event;
  }

  @Override
  public OMTraceHandlerEvent format(Class<?> context, String pattern, Throwable t, Object... args)
  {
    String msg = MessageFormat.format(pattern, args);
    return trace(context, msg, t);
  }

  @Override
  public OMTraceHandlerEvent format(Class<?> context, String pattern, Object... args)
  {
    return format(context, pattern, (Throwable)null, args);
  }

  @Override
  public OMTraceHandlerEvent trace(Class<?> context, String msg)
  {
    return trace(context, msg, (Throwable)null);
  }

  @Override
  public OMTraceHandlerEvent trace(Class<?> context, Throwable t)
  {
    return trace(context, (String)null, t);
  }

  @Override
  public OMTracer tracer(String name)
  {
    return new Tracer(this, name);
  }

  @Override
  public String toString()
  {
    return "Tracer[" + bundle + "/" + fullName + "]";
  }
}
