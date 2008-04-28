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
package org.eclipse.net4j.internal.util.om;

import org.eclipse.net4j.internal.util.bundle.AbstractPlatform;
import org.eclipse.net4j.util.om.OMBundle;

import org.eclipse.core.runtime.Platform;
import org.eclipse.osgi.service.debug.DebugOptions;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

/**
 * @author Eike Stepper
 */
public class OSGiPlatform extends AbstractPlatform
{
  BundleContext systemContext;

  public OSGiPlatform(Object systemContext)
  {
    this.systemContext = (BundleContext)systemContext;

    try
    {
      setDebugging(Platform.inDebugMode());
    }
    catch (Throwable ignore)
    {
      ;
    }
  }

  public boolean isOSGiRunning()
  {
    return true;
  }

  @Override
  protected OMBundle createBundle(String bundleID, Class<?> accessor)
  {
    return new OSGiBundle(this, bundleID, accessor);
  }

  @Override
  protected String getDebugOption(String bundleID, String option)
  {
    try
    {
      DebugOptions debugOptions = getDebugOptions();
      return debugOptions.getOption(bundleID + "/" + option); //$NON-NLS-1$
    }
    catch (Exception ex)
    {
      return null;
    }
  }

  @Override
  protected void setDebugOption(String bundleID, String option, String value)
  {
    try
    {
      DebugOptions debugOptions = getDebugOptions();
      debugOptions.setOption(bundleID + "/" + option, value); //$NON-NLS-1$
    }
    catch (Exception ex)
    {
    }
  }

  protected DebugOptions getDebugOptions() throws NoClassDefFoundError, NullPointerException
  {
    ServiceReference ref = systemContext.getServiceReference(DebugOptions.class.getName());
    return (DebugOptions)systemContext.getService(ref);
  }
}
