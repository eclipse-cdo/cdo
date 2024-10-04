/*
 * Copyright (c) 2024 Obeo and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Maxime Porhel (Obeo) - initial API and implementation
 */
package org.eclipse.net4j.wss;

import org.eclipse.net4j.internal.ws.WSAcceptorFactory;
import org.eclipse.net4j.internal.ws.WSConnectorFactory;
import org.eclipse.net4j.internal.wss.WSSAcceptorFactory;
import org.eclipse.net4j.internal.wss.WSSConnectorFactory;
import org.eclipse.net4j.util.container.IManagedContainer;
import org.eclipse.net4j.ws.IWSAcceptor;
import org.eclipse.net4j.ws.IWSConnector;
import org.eclipse.net4j.ws.WSUtil;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * A utility class with static convenience methods.
 *
 * @author mporhel
 * @since 1.3
 */
public final class WSSUtil
{
  public static final String FACTORY_TYPE = "wss"; //$NON-NLS-1$

  private WSSUtil()
  {
  }

  public static void prepareContainer(IManagedContainer container)
  {
    container.registerFactory(new WSSAcceptorFactory());
    container.registerFactory(new WSSConnectorFactory());
  }

  public static IWSAcceptor getAcceptor(IManagedContainer container, String acceptorName)
  {
    return (IWSAcceptor)container.getElement(WSAcceptorFactory.PRODUCT_GROUP, FACTORY_TYPE, acceptorName);
  }

  public static IWSConnector getConnector(IManagedContainer container, String description)
  {
    return (IWSConnector)container.getElement(WSConnectorFactory.PRODUCT_GROUP, FACTORY_TYPE, description);
  }

  public static IWSConnector getConnector(IManagedContainer container, URI serviceURI, String acceptorName, String... arguments)
  {
    String description = getConnectorDescription(serviceURI, acceptorName, arguments);
    return getConnector(container, description);
  }

  public static String getConnectorDescription(String serviceURI, String acceptorName, String... arguments) throws URISyntaxException
  {
    return getConnectorDescription(new URI(serviceURI), acceptorName, arguments);
  }

  public static String getConnectorDescription(URI serviceURI, String acceptorName, String... arguments)
  {
    return WSUtil.getConnectorDescription(serviceURI, acceptorName, arguments);
  }
}
