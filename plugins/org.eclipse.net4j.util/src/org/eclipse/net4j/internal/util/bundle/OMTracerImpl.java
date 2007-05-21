/***************************************************************************
 * Copyright (c) 2004 - 2007 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.net4j.internal.util.bundle;

import org.eclipse.net4j.util.om.OMBundle;
import org.eclipse.net4j.util.om.OMTracer;
import org.eclipse.net4j.util.om.OMTraceHandler.Event;

import java.text.MessageFormat;

/**
 * @author Eike Stepper
 */
public class OMTracerImpl implements OMTracer
{
  private OMBundle bundle;

  private OMTracerImpl parent;

  private String name;

  private String fullName;

  public OMTracerImpl(OMBundle bundle, String name)
  {
    this.bundle = bundle;
    this.name = name;
    fullName = name;
  }

  private OMTracerImpl(OMTracerImpl parent, String name)
  {
    this.bundle = parent.getBundle();
    this.parent = parent;
    this.name = name;
    fullName = parent.getFullName() + "." + name; //$NON-NLS-1$
  }

  public OMBundle getBundle()
  {
    return bundle;
  }

  public OMTracer getParent()
  {
    return parent;
  }

  public String getName()
  {
    return name;
  }

  public String getFullName()
  {
    return fullName;
  }

  public boolean isEnabled()
  {
    return bundle.isDebugging() && bundle.getDebugOption(fullName, false);
  }

  public void setEnabled(boolean enabled)
  {
    bundle.setDebugOption(fullName, enabled);
  }

  public void trace(Event event)
  {
    ((AbstractOMPlatform)bundle.getPlatform()).trace(event);
  }

  public Event trace(Class context, String msg, Throwable t)
  {
    Event event = new OMTraceHandlerEventImpl(this, context, msg, t);
    trace(event);
    return event;
  }

  public Event format(Class context, String pattern, Throwable t, Object... args)
  {
    String msg = MessageFormat.format(pattern, args);
    return trace(context, msg, t);
  }

  public Event format(Class context, String pattern, Object... args)
  {
    return format(context, pattern, (Throwable)null, args);
  }

  public Event trace(Class context, String msg)
  {
    return trace(context, msg, (Throwable)null);
  }

  public Event trace(Class context, Throwable t)
  {
    return trace(context, (String)null, t);
  }

  public OMTracer tracer(String name)
  {
    return new OMTracerImpl(this, name);
  }
}
