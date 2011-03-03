/***************************************************************************
 * Copyright (c) 2004 - 2011 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Simon McDuff - maintenance
 *    Victor Roldan Betancort - maintenance
 **************************************************************************/
package org.eclipse.emf.cdo.net4j;

import org.eclipse.emf.cdo.eresource.CDOResourceFactory;
import org.eclipse.emf.cdo.internal.net4j.CDONet4jSessionConfigurationImpl;
import org.eclipse.emf.cdo.internal.net4j.FailoverCDOSessionConfigurationImpl;
import org.eclipse.emf.cdo.internal.net4j.Net4jSessionFactory;
import org.eclipse.emf.cdo.internal.net4j.ReconnectingCDOSessionConfigurationImpl;
import org.eclipse.emf.cdo.internal.net4j.protocol.CDOClientProtocolFactory;
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
 * @since 2.0
 * @author Eike Stepper
 */
public final class CDONet4jUtil
{
  /**
   * @since 4.0
   */
  public static final String PROTOCOL_TCP = "cdo.net4j.tcp";

  /**
   * @since 4.0
   */
  public static final String PROTOCOL_JVM = "cdo.net4j.jvm";

  static
  {
    if (!OMPlatform.INSTANCE.isOSGiRunning())
    {
      CDOUtil.registerResourceFactory(null); // Ensure that the normal resource factory is registered

      Map<String, Object> map = Resource.Factory.Registry.INSTANCE.getProtocolToFactoryMap();
      if (!map.containsKey(PROTOCOL_TCP))
      {
        map.put(PROTOCOL_TCP, CDOResourceFactory.INSTANCE);
      }

      if (!map.containsKey(PROTOCOL_JVM))
      {
        map.put(PROTOCOL_JVM, CDOResourceFactory.INSTANCE);
      }

      int priority = CDOViewProvider.DEFAULT_PRIORITY - 100;
      CDOViewProviderRegistry.INSTANCE.addViewProvider(new CDONet4jViewProvider.TCP(priority));
      CDOViewProviderRegistry.INSTANCE.addViewProvider(new CDONet4jViewProvider.JVM(priority));
    }
  }

  private CDONet4jUtil()
  {
  }

  public static CDOSessionConfiguration createSessionConfiguration()
  {
    return new CDONet4jSessionConfigurationImpl();
  }

  /**
   * @since 4.0
   */
  public static ReconnectingCDOSessionConfiguration createReconnectingSessionConfiguration(String hostAndPort,
      String repoName, IManagedContainer container)
  {
    return new ReconnectingCDOSessionConfigurationImpl(hostAndPort, repoName, container);
  }

  /**
   * @since 4.0
   */
  public static FailoverCDOSessionConfiguration createFailoverSessionConfiguration(String monitorConnectorDescription,
      String repositoryGroup)
  {
    return createFailoverSessionConfiguration(monitorConnectorDescription, repositoryGroup, IPluginContainer.INSTANCE);
  }

  /**
   * @since 4.0
   */
  public static FailoverCDOSessionConfiguration createFailoverSessionConfiguration(String monitorConnectorDescription,
      String repositoryGroup, IManagedContainer container)
  {
    return new FailoverCDOSessionConfigurationImpl(monitorConnectorDescription, repositoryGroup, container);
  }

  public static void prepareContainer(IManagedContainer container)
  {
    container.registerFactory(new CDOClientProtocolFactory());
    container.registerFactory(new Net4jSessionFactory());
  }

  /**
   * @since 4.0
   */
  public static CDOSession getSession(IManagedContainer container, String description)
  {
    return (CDOSession)container.getElement(CDOSessionFactory.PRODUCT_GROUP, Net4jSessionFactory.TYPE, description);
  }
}
