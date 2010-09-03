/**
 * Copyright (c) 2004 - 2010 Eike Stepper (Berlin, Germany) and others.
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
  public static final class Activator extends OSGiActivator.WithConfig
  {
    public Activator()
    {
      super(BUNDLE);
    }

    @Override
    protected void doStartWithConfig(Object config) throws Exception
    {
      @SuppressWarnings("unchecked")
      Map<URI, String> map = (Map<URI, String>)config;
      projectNames.putAll(map);
    }

    @Override
    protected Object doStopWithConfig() throws Exception
    {
      return projectNames;
    }
  }
}
