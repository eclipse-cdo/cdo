/**
 * Copyright (c) 2004 - 2011 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.internal.efs.bundle;

import org.eclipse.net4j.util.om.OMBundle;
import org.eclipse.net4j.util.om.OMPlatform;
import org.eclipse.net4j.util.om.OSGiActivator;
import org.eclipse.net4j.util.om.log.OMLogger;
import org.eclipse.net4j.util.om.trace.OMTracer;

import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.ResourcesPlugin;

import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

/**
 * The <em>Operations & Maintenance</em> class of this bundle.
 * 
 * @author Eike Stepper
 */
public abstract class OM
{
  public static final String BUNDLE_ID = "org.eclipse.emf.cdo.efs"; //$NON-NLS-1$

  public static final OMBundle BUNDLE = OMPlatform.INSTANCE.bundle(BUNDLE_ID, OM.class);

  public static final OMLogger LOG = BUNDLE.logger();

  public static final OMTracer DEBUG = BUNDLE.tracer("debug"); //$NON-NLS-1$

  private static Map<URI, String> projectNames = new HashMap<URI, String>();

  public static void associateProjectName(URI uri, String projectName) throws IOException
  {
    projectNames.put(uri, projectName);
  }

  public static String getProjectName(URI uri)
  {
    return projectNames.get(uri);
  }

  /**
   * @author Eike Stepper
   */
  public static final class Activator extends OSGiActivator.WithState implements IResourceChangeListener
  {
    public Activator()
    {
      super(BUNDLE);
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void doStartWithState(Object state) throws Exception
    {
      Map<URI, String> names = (Map<URI, String>)state;
      if (names != null)
      {
        projectNames.putAll(names);
      }

      ResourcesPlugin.getWorkspace().addResourceChangeListener(this);
    }

    @Override
    protected Object doStopWithState() throws Exception
    {
      ResourcesPlugin.getWorkspace().removeResourceChangeListener(this);
      return projectNames;
    }

    public void resourceChanged(IResourceChangeEvent event)
    {
      System.out.println(event);
    }
  }
}
