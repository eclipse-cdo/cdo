/***************************************************************************
 * Copyright (c) 2004 - 2008 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.net4j.tcp;

import org.eclipse.net4j.internal.tcp.TCPAcceptorFactory;
import org.eclipse.net4j.internal.tcp.TCPConnectorFactory;
import org.eclipse.net4j.internal.tcp.TCPSelectorFactory;
import org.eclipse.net4j.internal.tcp.TCPSelectorInjector;
import org.eclipse.net4j.util.container.IManagedContainer;

/**
 * @author Eike Stepper
 */
public final class TCPUtil
{
  private TCPUtil()
  {
  }

  public static void prepareContainer(IManagedContainer container)
  {
    container.registerFactory(new TCPSelectorFactory());
    container.registerFactory(new TCPAcceptorFactory());
    container.registerFactory(new TCPConnectorFactory());
    container.addPostProcessor(new TCPSelectorInjector());
  }

  public static ITCPAcceptor getAcceptor(IManagedContainer container, String description)
  {
    return (ITCPAcceptor)container.getElement(TCPAcceptorFactory.PRODUCT_GROUP, TCPAcceptorFactory.TYPE, description);
  }

  public static ITCPConnector getConnector(IManagedContainer container, String description)
  {
    return (ITCPConnector)container
        .getElement(TCPConnectorFactory.PRODUCT_GROUP, TCPConnectorFactory.TYPE, description);
  }
}
