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
package org.eclipse.internal.net4j.bundle;

import org.eclipse.core.runtime.FileLocator;

import org.osgi.framework.BundleContext;

import java.io.IOException;
import java.net.URL;

/**
 * @author Eike Stepper
 */
public class OSGiBundle extends AbstractOMBundle
{
  public OSGiBundle(AbstractOMPlatform platform, String bundleID, Class accessor)
  {
    super(platform, bundleID, accessor);
  }

  @Override
  public BundleContext getBundleContext()
  {
    return (BundleContext)super.getBundleContext();
  }

  public URL getBaseURL()
  {
    try
    {
      URL entry = getBundleContext().getBundle().getEntry(".");
      return FileLocator.resolve(entry);
    }
    catch (IOException ex)
    {
      ex.printStackTrace();
      return null;
    }
  }
}
