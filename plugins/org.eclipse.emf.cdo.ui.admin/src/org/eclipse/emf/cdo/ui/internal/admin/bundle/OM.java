/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Victor Roldan Betancort - maintenance
 */
package org.eclipse.emf.cdo.ui.internal.admin.bundle;

import org.eclipse.emf.cdo.admin.CDOAdminClientManager;
import org.eclipse.emf.cdo.admin.CDOAdminUtil;

import org.eclipse.net4j.util.lifecycle.LifecycleUtil;
import org.eclipse.net4j.util.om.OMBundle;
import org.eclipse.net4j.util.om.OMPlatform;
import org.eclipse.net4j.util.om.log.OMLogger;
import org.eclipse.net4j.util.om.trace.OMTracer;
import org.eclipse.net4j.util.ui.UIActivator;

import org.eclipse.jface.resource.ImageDescriptor;

import java.util.Collection;
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

  private static CDOAdminClientManager adminManager = CDOAdminUtil.createAdminManager();

  public static CDOAdminClientManager getAdminManager()
  {
    return adminManager;
  }

  public static ImageDescriptor getImageDescriptor(String imageFilePath)
  {
    return Activator.imageDescriptorFromPlugin(BUNDLE_ID, imageFilePath);
  }

  /**
   * @author Eike Stepper
   */
  public static final class Activator extends UIActivator.WithState
  {
    public Activator()
    {
      super(BUNDLE);
    }

    @Override
    protected void doStartWithState(Object state) throws Exception
    {
      LifecycleUtil.activate(adminManager);
      if (state instanceof List<?>)
      {
        @SuppressWarnings("unchecked")
        Collection<String> urls = (Collection<String>)state;
        adminManager.addConnections(urls);
      }
    }

    @Override
    protected Object doStopWithState() throws Exception
    {
      List<String> urls = adminManager.getConnectionURLs();
      LifecycleUtil.deactivate(adminManager);
      return urls;
    }
  }
}
