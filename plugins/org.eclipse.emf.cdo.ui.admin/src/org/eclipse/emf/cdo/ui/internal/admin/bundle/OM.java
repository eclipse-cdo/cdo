/*
 * Copyright (c) 2012, 2013 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Victor Roldan Betancort - maintenance
 *    Christian W. Damus (CEA LIST) - bug 418454
 */
package org.eclipse.emf.cdo.ui.internal.admin.bundle;

import org.eclipse.emf.cdo.admin.CDOAdminClientManager;
import org.eclipse.emf.cdo.admin.CDOAdminClientUtil;

import org.eclipse.net4j.util.lifecycle.LifecycleUtil;
import org.eclipse.net4j.util.om.OMBundle;
import org.eclipse.net4j.util.om.OMPlatform;
import org.eclipse.net4j.util.om.log.OMLogger;
import org.eclipse.net4j.util.om.trace.OMTracer;
import org.eclipse.net4j.util.ui.UIActivator;

import org.eclipse.jface.resource.ImageDescriptor;

import java.util.List;

/**
 * The <em>Operations & Maintenance</em> class of this bundle.
 *
 * @author Eike Stepper
 */
public abstract class OM
{
  public static final String BUNDLE_ID = "org.eclipse.emf.cdo.ui.admin"; //$NON-NLS-1$

  public static final OMBundle BUNDLE = OMPlatform.INSTANCE.bundle(BUNDLE_ID, OM.class);

  public static final OMLogger LOG = BUNDLE.logger();

  public static final OMTracer DEBUG = BUNDLE.tracer("debug"); //$NON-NLS-1$

  private static CDOAdminClientManager adminManager = CDOAdminClientUtil.createAdminManager();

  public static CDOAdminClientManager getAdminManager()
  {
    return adminManager;
  }

  private static String lastURL;

  public static String getLastURL()
  {
    return lastURL;
  }

  public static void setLastURL(String lastURL)
  {
    OM.lastURL = lastURL;
  }

  @SuppressWarnings("deprecation")
  public static ImageDescriptor getImageDescriptor(String imageFilePath)
  {
    return Activator.imageDescriptorFromPlugin(BUNDLE_ID, imageFilePath);
  }

  /**
   * @author Eike Stepper
   */
  public static final class Activator extends UIActivator.WithState
  {
    public static Activator INSTANCE;

    public Activator()
    {
      super(BUNDLE);
    }

    @Override
    protected void doStartWithState(Object state) throws Exception
    {
      INSTANCE = this;

      LifecycleUtil.activate(adminManager);
      if (state instanceof List<?>)
      {
        @SuppressWarnings("unchecked")
        List<String> urls = (List<String>)state;
        if (!urls.isEmpty())
        {
          lastURL = urls.remove(0);
        }

        adminManager.addConnections(urls);
      }
    }

    @Override
    protected Object doStopWithState() throws Exception
    {
      List<String> urls = adminManager.getConnectionURLs();
      urls.add(0, lastURL);

      LifecycleUtil.deactivate(adminManager);

      INSTANCE = null;

      return urls;
    }
  }
}
