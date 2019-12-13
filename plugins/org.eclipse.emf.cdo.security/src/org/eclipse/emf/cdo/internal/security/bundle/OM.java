/*
 * Copyright (c) 2013, 2015, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Christian W. Damus (CEA LIST) - bug 399487
 */
package org.eclipse.emf.cdo.internal.security.bundle;

import org.eclipse.net4j.util.WrappedException;
import org.eclipse.net4j.util.om.OMBundle;
import org.eclipse.net4j.util.om.OMPlatform;
import org.eclipse.net4j.util.om.OSGiActivator;
import org.eclipse.net4j.util.om.log.OMLogger;
import org.eclipse.net4j.util.om.trace.OMTracer;

import org.eclipse.emf.common.EMFPlugin;
import org.eclipse.emf.common.util.ResourceLocator;

import org.eclipse.core.runtime.Platform;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.MessageFormat;
import java.util.ResourceBundle;

/**
 * The <em>Operations & Maintenance</em> class of this bundle.
 *
 * @author Eike Stepper
 */
public abstract class OM
{
  public static final String BUNDLE_ID = "org.eclipse.emf.cdo.security"; //$NON-NLS-1$

  public static final OMBundle BUNDLE = OMPlatform.INSTANCE.bundle(BUNDLE_ID, OM.class);

  public static final OMTracer DEBUG = BUNDLE.tracer("debug"); //$NON-NLS-1$

  public static final OMLogger LOG = BUNDLE.logger();

  public static final EMFPlugin EMF_PLUGIN = new EMFPlugin(new ResourceLocator[0])
  {

    @Override
    public ResourceLocator getPluginResourceLocator()
    {
      return Activator.INSTANCE;
    }
  };

  /**
   * @author Eike Stepper
   */
  public static final class Activator extends OSGiActivator implements ResourceLocator
  {
    public static Activator INSTANCE;

    private ResourceBundle resourceBundle;

    public Activator()
    {
      super(BUNDLE);
    }

    @Override
    protected void doStart() throws Exception
    {
      INSTANCE = this;
    }

    @Override
    protected void doStop() throws Exception
    {
      INSTANCE = null;
    }

    @Override
    public URL getBaseURL()
    {
      return getOMBundle().getBaseURL();
    }

    @Override
    public Object getImage(String key)
    {
      try
      {
        return new URL(getBaseURL() + "icons/" + key);
      }
      catch (MalformedURLException e)
      {
        throw WrappedException.wrap(e);
      }
    }

    @Override
    public String getString(String key)
    {
      return getString(key, true);
    }

    @Override
    public String getString(String key, boolean translate)
    {
      if (!translate)
      {
        throw new IllegalArgumentException("translate"); //$NON-NLS-1$
      }

      if (resourceBundle == null)
      {
        resourceBundle = Platform.getResourceBundle(bundleContext.getBundle());
      }

      return resourceBundle.getString(key);
    }

    @Override
    public String getString(String key, Object[] substitutions)
    {
      return getString(key, substitutions, true);
    }

    @Override
    public String getString(String key, Object[] substitutions, boolean translate)
    {
      return MessageFormat.format(getString(key, translate), substitutions);
    }
  }
}
