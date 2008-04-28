/***************************************************************************
 * Copyright (c) 2004 - 2008 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.net4j.util.om;

import org.eclipse.net4j.internal.util.bundle.AbstractPlatform;
import org.eclipse.net4j.util.om.log.OMLogHandler;
import org.eclipse.net4j.util.om.trace.OMTraceHandler;

import java.io.File;
import java.util.Properties;

/**
 * @author Eike Stepper
 */
public interface OMPlatform
{
  // @Singleton
  public static final OMPlatform INSTANCE = AbstractPlatform.createPlatform();

  public OMBundle bundle(String bundleID, Class<?> accessor);

  public boolean isOSGiRunning();

  public boolean isDebugging();

  public void setDebugging(boolean debugging);

  public void addLogHandler(OMLogHandler logHandler);

  public void removeLogHandler(OMLogHandler logHandler);

  public void addTraceHandler(OMTraceHandler traceHandler);

  public void removeTraceHandler(OMTraceHandler traceHandler);

  public File getStateFolder();

  public File getConfigFolder();

  public File getConfigFile(String name);

  public Properties getConfigProperties(String name);
}
