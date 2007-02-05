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
package org.eclipse.net4j.util.om;

import org.eclipse.net4j.util.lifecycle.Singleton;

import org.eclipse.internal.net4j.bundle.AbstractOMPlatform;

/**
 * @author Eike Stepper
 */
public interface OMPlatform
{
  @Singleton
  public static final OMPlatform INSTANCE = AbstractOMPlatform.createPlatform();

  public OMBundle bundle(String bundleID, Class accessor);

  public boolean isDebugging();

  public void setDebugging(boolean debugging);

  public void addLogHandler(OMLogHandler logHandler);

  public void removeLogHandler(OMLogHandler logHandler);

  public void addTraceHandler(OMTraceHandler traceHandler);

  public void removeTraceHandler(OMTraceHandler traceHandler);
}