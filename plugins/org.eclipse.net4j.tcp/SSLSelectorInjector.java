/**
 * Copyright (c) 2004 - 2011 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Teerawat Chaiyakijpichet (No Magic Asia Ltd.) - initial API and implementation
 *    Caspar De Groot (No Magic Asia Ltd.) - initial API and implementation
 */
package org.eclipse.net4j.internal.tcp.ssl;

import org.eclipse.net4j.internal.tcp.TCPConnector;
import org.eclipse.net4j.util.container.IElementProcessor;
import org.eclipse.net4j.util.container.IManagedContainer;

/**
 * SSLSelectorInjector responses to set the SSLSelector into SSLAcceptor for server side and SSLSelector into
 * SSLClientConnector for client side.
 * 
 * @author Teerawat Chaiyakijpichet (No Magic Asia Ltd.)
 * @author Caspar De Groot (No Magic Asia Ltd.)
 * @since 4.0
 */
public class SSLSelectorInjector implements IElementProcessor
{
  public static final String TYPE = null;

  public SSLSelectorInjector()
  {
  }

  public Object process(IManagedContainer container, String productGroup, String factoryType, String description,
      Object element)
  {
    if (element instanceof SSLAcceptor)
    {
      SSLAcceptor acceptor = (SSLAcceptor)element;
      if (acceptor.getSelector() == null)
      {
        acceptor.setSelector(getSelector(container));
      }
    }
    else if (element instanceof TCPConnector)
    {
      TCPConnector connector = (TCPConnector)element;
      if (connector.getSelector() == null)
      {
        connector.setSelector(getSelector(container));
      }
    }

    return element;
  }

  protected SSLSelector getSelector(IManagedContainer container)
  {
    return SSLSelectorFactory.get(container, null);
  }
}
