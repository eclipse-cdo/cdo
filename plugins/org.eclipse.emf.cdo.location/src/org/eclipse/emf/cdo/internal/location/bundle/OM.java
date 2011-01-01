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
package org.eclipse.emf.cdo.internal.location.bundle;

import org.eclipse.emf.cdo.internal.location.RepositoryLocationManager;
import org.eclipse.emf.cdo.location.IRepositoryLocation;
import org.eclipse.emf.cdo.location.IRepositoryLocationManager;

import org.eclipse.net4j.util.om.OMBundle;
import org.eclipse.net4j.util.om.OMPlatform;
import org.eclipse.net4j.util.om.OSGiActivator;
import org.eclipse.net4j.util.om.log.OMLogger;
import org.eclipse.net4j.util.om.trace.OMTracer;

import java.util.ArrayList;
import java.util.List;

/**
 * The <em>Operations & Maintenance</em> class of this bundle.
 * 
 * @author Eike Stepper
 */
public abstract class OM
{
  public static final String BUNDLE_ID = "org.eclipse.emf.cdo.location"; //$NON-NLS-1$

  public static final OMBundle BUNDLE = OMPlatform.INSTANCE.bundle(BUNDLE_ID, OM.class);

  public static final OMTracer DEBUG = BUNDLE.tracer("debug"); //$NON-NLS-1$

  public static final OMLogger LOG = BUNDLE.logger();

  private static RepositoryLocationManager repositoryLocationManager = new RepositoryLocationManager();

  public static IRepositoryLocationManager getRepositoryLocationManager()
  {
    return repositoryLocationManager;
  }

  /**
   * @author Eike Stepper
   */
  public static final class Activator extends OSGiActivator.WithState
  {
    public Activator()
    {
      super(BUNDLE);
    }

    @Override
    protected void doStartWithState(Object state) throws Exception
    {
      @SuppressWarnings("unchecked")
      List<List<String>> locations = (List<List<String>>)state;
      if (locations != null)
      {
        for (List<String> location : locations)
        {
          repositoryLocationManager.addRepositoryLocation(location.get(0), location.get(1), location.get(2));
        }
      }
    }

    @Override
    protected Object doStopWithState() throws Exception
    {
      List<List<String>> locations = new ArrayList<List<String>>();
      for (IRepositoryLocation location : repositoryLocationManager.getRepositoryLocations())
      {
        List<String> list = new ArrayList<String>();
        list.add(location.getConnectorType());
        list.add(location.getConnectorDescription());
        list.add(location.getRepositoryName());
        locations.add(list);
      }

      return locations;
    }
  }
}
