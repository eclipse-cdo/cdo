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
package org.eclipse.net4j.internal.tcp;

import org.eclipse.net4j.util.container.IElementProcessor;
import org.eclipse.net4j.util.container.IManagedContainer;

/**
 * @author Eike Stepper
 */
public class TCPSelectorInjector implements IElementProcessor
{
  public static final String TYPE = null;

  public TCPSelectorInjector()
  {
  }

  public Object process(IManagedContainer container, String productGroup, String factoryType, String description,
      Object element)
  {
    if (element instanceof TCPAcceptor)
    {
      TCPAcceptor acceptor = (TCPAcceptor)element;
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

  protected TCPSelector getSelector(IManagedContainer container)
  {
    return TCPSelectorFactory.get(container, null);
  }
}
