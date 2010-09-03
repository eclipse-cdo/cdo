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

import org.eclipse.net4j.util.io.IOUtil;
import org.eclipse.net4j.util.om.OMBundle;
import org.eclipse.net4j.util.om.OMPlatform;
import org.eclipse.net4j.util.om.OSGiActivator;
import org.eclipse.net4j.util.om.log.OMLogger;
import org.eclipse.net4j.util.om.trace.OMTracer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
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
  public static final class Activator extends OSGiActivator
  {
    public Activator()
    {
      super(BUNDLE);
    }

    @Override
    protected void doStart() throws Exception
    {
      FileInputStream fis = null;

      try
      {
        File configFile = OM.BUNDLE.getConfigFile();
        if (configFile.exists())
        {
          fis = new FileInputStream(configFile);
          ObjectInputStream ois = new ObjectInputStream(fis);

          @SuppressWarnings("unchecked")
          Map<URI, String> map = (Map<URI, String>)ois.readObject();
          projectNames.putAll(map);
          IOUtil.close(ois);
        }
      }
      catch (Exception ex)
      {
        LOG.error(ex);
      }
      finally
      {
        IOUtil.close(fis);
      }
    }

    @Override
    protected void doStop() throws Exception
    {
      FileOutputStream fos = null;

      try
      {
        File configFile = OM.BUNDLE.getConfigFile();
        fos = new FileOutputStream(configFile);
        ObjectOutputStream oos = new ObjectOutputStream(fos);
        oos.writeObject(projectNames);
        IOUtil.close(oos);
      }
      catch (Exception ex)
      {
        LOG.error(ex);
      }
      finally
      {
        IOUtil.close(fos);
      }
    }
  }
}
