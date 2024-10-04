/*
 * Copyright (c) 2009-2013, 2015, 2016, 2019, 2020, 2023, 2024 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Simon McDuff - maintenance
 *    Victor Roldan Betancort - maintenance
 *    Maxime Porhel (Obeo) - WSS Support
 */
package org.eclipse.emf.cdo.net4j;

import org.eclipse.emf.cdo.eresource.CDOResourceFactory;
import org.eclipse.emf.cdo.internal.net4j.CDONet4jSessionConfigurationImpl;
import org.eclipse.emf.cdo.internal.net4j.FailoverCDOSessionConfigurationImpl;
import org.eclipse.emf.cdo.internal.net4j.Net4jSessionFactory;
import org.eclipse.emf.cdo.internal.net4j.ReconnectingCDOSessionConfigurationImpl;
import org.eclipse.emf.cdo.internal.net4j.bundle.OM;
import org.eclipse.emf.cdo.util.CDOUtil;
import org.eclipse.emf.cdo.view.CDOViewProvider;
import org.eclipse.emf.cdo.view.CDOViewProviderRegistry;

import org.eclipse.emf.internal.cdo.session.CDOSessionFactory;

import org.eclipse.net4j.util.container.IManagedContainer;
import org.eclipse.net4j.util.container.IPluginContainer;
import org.eclipse.net4j.util.om.OMPlatform;

import org.eclipse.emf.ecore.resource.Resource;

import java.util.Map;

/**
 * Various static methods that may help with Net4j-specific CDO {@link CDONet4jSession sessions}.
 *
 * @since 2.0
 * @author Eike Stepper
 */
public final class CDONet4jUtil
{
  /**
   * @since 4.0
   */
  public static final String PROTOCOL_JVM = "cdo.net4j.jvm";

  /**
   * @since 4.0
   */
  public static final String PROTOCOL_TCP = "cdo.net4j.tcp";

  /**
   * @since 4.0
   */
  public static final String PROTOCOL_SSL = "cdo.net4j.ssl";

  /**
   * @since 4.3
   */
  public static final String PROTOCOL_WS = "cdo.net4j.ws";

  /**
   * @since 4.7
   */
  public static final String PROTOCOL_WSS = "cdo.net4j.wss";

  static
  {
    try
    {
      if (!OMPlatform.INSTANCE.isOSGiRunning())
      {
        CDOUtil.registerResourceFactory(null); // Ensure that the normal resource factory is registered

        Map<String, Object> map = Resource.Factory.Registry.INSTANCE.getProtocolToFactoryMap();
        if (!map.containsKey(PROTOCOL_TCP))
        {
          map.put(PROTOCOL_TCP, CDOResourceFactory.INSTANCE);
        }

        if (!map.containsKey(PROTOCOL_SSL))
        {
          map.put(PROTOCOL_SSL, CDOResourceFactory.INSTANCE);
        }

        if (!map.containsKey(PROTOCOL_WS))
        {
          map.put(PROTOCOL_WS, CDOResourceFactory.INSTANCE);
        }

        if (!map.containsKey(PROTOCOL_WSS))
        {
          map.put(PROTOCOL_WSS, CDOResourceFactory.INSTANCE);
        }

        if (!map.containsKey(PROTOCOL_JVM))
        {
          map.put(PROTOCOL_JVM, CDOResourceFactory.INSTANCE);
        }

        int priority = CDOViewProvider.DEFAULT_PRIORITY - 100;
        CDOViewProviderRegistry.INSTANCE.addViewProvider(new CDONet4jViewProvider.JVM(priority));
        CDOViewProviderRegistry.INSTANCE.addViewProvider(new CDONet4jViewProvider.TCP(priority));
        CDOViewProviderRegistry.INSTANCE.addViewProvider(new CDONet4jViewProvider.SSL(priority));
        CDOViewProviderRegistry.INSTANCE.addViewProvider(new CDONet4jViewProvider.WS(priority));
        CDOViewProviderRegistry.INSTANCE.addViewProvider(new CDONet4jViewProvider.WSS(priority));
      }
    }
    catch (RuntimeException ex)
    {
      ex.printStackTrace();
      throw ex;
    }
    catch (Error ex)
    {
      ex.printStackTrace();
      throw ex;
    }
  }

  private CDONet4jUtil()
  {
  }

  public static void prepareContainer(IManagedContainer container)
  {
    OM.BUNDLE.prepareContainer(container);
  }

  /**
   * @since 4.1
   */
  public static CDONet4jSessionConfiguration createNet4jSessionConfiguration()
  {
    return new CDONet4jSessionConfigurationImpl();
  }

  /**
   * @deprecated Use {@link #createNet4jSessionConfiguration() createNet4jSessionConfiguration()}.
   */
  @Deprecated
  public static CDOSessionConfiguration createSessionConfiguration()
  {
    return (CDOSessionConfiguration)createNet4jSessionConfiguration();
  }

  /**
   * @since 4.0
   */
  public static ReconnectingCDOSessionConfiguration createReconnectingSessionConfiguration(String hostAndPort, String repoName, IManagedContainer container)
  {
    return new ReconnectingCDOSessionConfigurationImpl(hostAndPort, repoName, container);
  }

  /**
   * @since 4.0
   */
  public static FailoverCDOSessionConfiguration createFailoverSessionConfiguration(String monitorConnectorDescription, String repositoryGroup)
  {
    return createFailoverSessionConfiguration(monitorConnectorDescription, repositoryGroup, IPluginContainer.INSTANCE);
  }

  /**
   * @since 4.0
   */
  public static FailoverCDOSessionConfiguration createFailoverSessionConfiguration(String monitorConnectorDescription, String repositoryGroup,
      IManagedContainer container)
  {
    return new FailoverCDOSessionConfigurationImpl(monitorConnectorDescription, repositoryGroup, container);
  }

  /**
   * @since 4.1
   */
  public static CDONet4jSession getNet4jSession(IManagedContainer container, String description)
  {
    return (CDONet4jSession)container.getElement(CDOSessionFactory.PRODUCT_GROUP, Net4jSessionFactory.TYPE, description);
  }

  /**
   * @since 4.0
   * @deprecated Use {@link #getNet4jSession(IManagedContainer, String) getNet4jSession()}.
   */
  @Deprecated
  public static CDOSession getSession(IManagedContainer container, String description)
  {
    return (CDOSession)getNet4jSession(container, description);
  }
}
