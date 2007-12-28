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

import org.eclipse.net4j.internal.util.bundle.AbstractBundle;
import org.eclipse.net4j.internal.util.bundle.AbstractPlatform;
import org.eclipse.net4j.util.io.IOUtil;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Platform;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

import java.io.IOException;
import java.net.URL;

/**
 * @author Eike Stepper
 */
public class OSGiBundle extends AbstractBundle
{
  public OSGiBundle(AbstractPlatform platform, String bundleID, Class<?> accessor)
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
      URL entry = getBundleContext().getBundle().getEntry("."); //$NON-NLS-1$
      return FileLocator.resolve(entry);
    }
    catch (IOException ex)
    {
      IOUtil.print(ex);
      return null;
    }
  }

  public String getStateLocation()
  {
    Bundle bundle = getBundleContext().getBundle();
    return Platform.getStateLocation(bundle).toString();
  }
}
