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
package org.eclipse.internal.net4j.util.operation;

import org.eclipse.osgi.service.debug.DebugOptions;

import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;

/**
 * @author Eike Stepper
 */
public class BundleDebugOptions extends AbstractDebugOptions
{
  private String bundleName;

  private ServiceTracker debugTracker;

  public BundleDebugOptions(BundleContext context)
  {
    bundleName = context.getBundle().getSymbolicName();
    debugTracker = new ServiceTracker(context, DebugOptions.class.getName(), null);
    debugTracker.open();
  }

  @Override
  public void dispose()
  {
    debugTracker.close();
    debugTracker = null;
  }

  public String getOption(String option)
  {
    return getDebugService().getOption(getPrefixedOption(option));
  }

  public String getOption(String option, String defaultValue)
  {
    return getDebugService().getOption(getPrefixedOption(option), defaultValue);
  }

  public boolean getBooleanOption(String option, boolean defaultValue)
  {
    return getDebugService().getBooleanOption(getPrefixedOption(option), defaultValue);
  }

  public int getIntegerOption(String option, int defaultValue)
  {
    return getDebugService().getIntegerOption(getPrefixedOption(option), defaultValue);
  }

  public void setOption(String option, String value)
  {
    getDebugService().setOption(getPrefixedOption(option), value);
  }

  private DebugOptions getDebugService()
  {
    DebugOptions debugService = (DebugOptions)debugTracker.getService();
    if (debugService == null)
    {
      throw new IllegalStateException("debugService == null");
    }

    return debugService;
  }

  private String getPrefixedOption(String option)
  {
    return bundleName + "/" + option;
  }
}
