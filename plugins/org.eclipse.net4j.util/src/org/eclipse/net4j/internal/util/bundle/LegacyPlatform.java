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

import org.eclipse.net4j.internal.util.om.pref.Preferences;
import org.eclipse.net4j.util.om.OMBundle;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Eike Stepper
 */
public class LegacyPlatform extends AbstractOMPlatform
{
  private Map<String, String> debugOptions = new ConcurrentHashMap(0);

  public LegacyPlatform()
  {
  }

  public synchronized void start()
  {
  }

  public synchronized void stop()
  {
    for (AbstractOMBundle bundle : getBundles().values())
    {
      try
      {
        Preferences preferences = bundle.preferences();
        preferences.save();
      }
      catch (RuntimeException ex)
      {
        OM.LOG.error(ex);
      }
    }
  }

  @Override
  protected OMBundle createBundle(String bundleID, Class accessor)
  {
    return new LegacyBundle(this, bundleID, accessor);
  }

  @Override
  protected String getDebugOption(String bundleID, String option)
  {
    return debugOptions.get(bundleID + "/" + option); //$NON-NLS-1$
  }

  @Override
  protected void setDebugOption(String bundleID, String option, String value)
  {
    debugOptions.put(bundleID + "/" + option, value); //$NON-NLS-1$
  }
}
