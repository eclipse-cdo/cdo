/*
 * Copyright (c) 2011, 2012, 2015, 2016, 2020 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Victor Roldan Betancort - maintenance
 */
package org.eclipse.emf.cdo.internal.explorer.bundle;

import org.eclipse.emf.cdo.internal.explorer.CDOExplorerURIHandler;
import org.eclipse.emf.cdo.internal.explorer.checkouts.CDOCheckoutManagerImpl;
import org.eclipse.emf.cdo.internal.explorer.repositories.CDORepositoryManagerImpl;

import org.eclipse.net4j.util.lifecycle.LifecycleUtil;
import org.eclipse.net4j.util.om.OMBundle;
import org.eclipse.net4j.util.om.OMPlatform;
import org.eclipse.net4j.util.om.OSGiActivator;
import org.eclipse.net4j.util.om.log.OMLogger;
import org.eclipse.net4j.util.om.trace.OMTracer;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.URIConverter;
import org.eclipse.emf.ecore.resource.impl.BinaryResourceImpl;
import org.eclipse.emf.ecore.resource.impl.ResourceFactoryImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceImpl;

import java.io.File;
import java.util.Map;

/**
 * The <em>Operations & Maintenance</em> class of this bundle.
 *
 * @author Eike Stepper
 */
public abstract class OM
{
  public static final String BUNDLE_ID = "org.eclipse.emf.cdo.explorer"; //$NON-NLS-1$

  public static final OMBundle BUNDLE = OMPlatform.INSTANCE.bundle(BUNDLE_ID, OM.class);

  public static final OMTracer DEBUG = BUNDLE.tracer("debug"); //$NON-NLS-1$

  public static final OMLogger LOG = BUNDLE.logger();

  private static final String STATE_LOCATION = OMPlatform.INSTANCE.getProperty("org.eclipse.emf.cdo.explorer.stateLocation");

  private static final boolean OMIT_CHECKOUT_FILE_URI_HANDLERS = OMPlatform.INSTANCE.isProperty("org.eclipse.emf.cdo.explorer.omitCheckoutFileURIHandlers");

  private static String stateLocation;

  private static CDORepositoryManagerImpl repositoryManager;

  private static CDOCheckoutManagerImpl checkoutManager;

  public static String getStateLocation()
  {
    return stateLocation;
  }

  public static void initializeManagers(File stateLocation)
  {
    disposeManagers();

    if (repositoryManager == null)
    {
      repositoryManager = new CDORepositoryManagerImpl(new File(stateLocation, "rp"));
      Exception exception = LifecycleUtil.activateSilent(repositoryManager);
      if (exception != null)
      {
        LOG.error(exception);
      }
    }

    if (checkoutManager == null)
    {
      checkoutManager = new CDOCheckoutManagerImpl(new File(stateLocation, "co"));
      Exception exception = LifecycleUtil.activateSilent(checkoutManager);
      if (exception != null)
      {
        LOG.error(exception);
      }
    }
  }

  public static void disposeManagers()
  {
    if (checkoutManager != null)
    {
      Exception exception = LifecycleUtil.deactivate(checkoutManager);
      if (exception != null)
      {
        LOG.error(exception);
      }

      checkoutManager = null;
    }

    if (repositoryManager != null)
    {
      Exception exception = LifecycleUtil.deactivate(repositoryManager);
      if (exception != null)
      {
        LOG.error(exception);
      }

      repositoryManager = null;
    }
  }

  public static CDORepositoryManagerImpl getRepositoryManager()
  {
    return repositoryManager;
  }

  public static CDOCheckoutManagerImpl getCheckoutManager()
  {
    return checkoutManager;
  }

  /**
   * @author Eike Stepper
   */
  public static final class Activator extends OSGiActivator
  {
    private static final Resource.Factory TEXT_RESOURCE_FACTORY = new XMIResourceFactoryImpl()
    {
      @Override
      public Resource createResource(URI uri)
      {
        return new XMIResourceImpl(uri)
        {
          private URIConverter uriConverter;

          @Override
          protected URIConverter getURIConverter()
          {
            if (uriConverter == null)
            {
              uriConverter = super.getURIConverter();
              uriConverter.getURIHandlers().add(0, CDOExplorerURIHandler.TEXT);
            }

            return uriConverter;
          }
        };
      }
    };

    private static final Resource.Factory BINARY_RESOURCE_FACTORY = new ResourceFactoryImpl()
    {
      @Override
      public Resource createResource(URI uri)
      {
        return new BinaryResourceImpl(uri)
        {
          private URIConverter uriConverter;

          @Override
          protected URIConverter getURIConverter()
          {
            if (uriConverter == null)
            {
              uriConverter = super.getURIConverter();
              uriConverter.getURIHandlers().add(0, CDOExplorerURIHandler.TEXT);
            }

            return uriConverter;
          }
        };
      }
    };

    public Activator()
    {
      super(BUNDLE);
    }

    @Override
    protected void doStart() throws Exception
    {
      super.doStart();

      if (!OMIT_CHECKOUT_FILE_URI_HANDLERS)
      {
        Map<String, Object> factoryMap = Resource.Factory.Registry.INSTANCE.getProtocolToFactoryMap();
        factoryMap.put(CDOExplorerURIHandler.TEXT.getScheme(), TEXT_RESOURCE_FACTORY);
        factoryMap.put(CDOExplorerURIHandler.BINARY.getScheme(), BINARY_RESOURCE_FACTORY);
      }

      stateLocation = STATE_LOCATION != null ? STATE_LOCATION : BUNDLE.getStateLocation();
      initializeManagers(new File(stateLocation));
    }

    @Override
    protected void doStop() throws Exception
    {
      disposeManagers();
      super.doStop();
    }
  }
}
