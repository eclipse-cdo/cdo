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
package org.eclipse.emf.internal.cdo.util;

import org.eclipse.emf.internal.cdo.CDOSessionImpl;

import org.eclipse.net4j.connector.IConnector;
import org.eclipse.net4j.util.StringUtil;
import org.eclipse.net4j.util.container.IElementProcessor;
import org.eclipse.net4j.util.container.IManagedContainer;

import org.eclipse.spi.net4j.ConnectorFactory;

/**
 * @author Eike Stepper
 */
public class ChannelInjector implements IElementProcessor
{
  private static final String SCHEME_SEPARATOR = "://";

  public ChannelInjector()
  {
  }

  public Object process(IManagedContainer container, String productGroup, String factoryType, String description,
      Object element)
  {
    if (element instanceof CDOSessionImpl)
    {
      CDOSessionImpl session = (CDOSessionImpl)element;
      if (session.getConnector() == null)
      {
        session.setConnector(getConnector(container, description));
      }
    }

    return element;
  }

  protected IConnector getConnector(IManagedContainer container, String description)
  {
    int pos = description.indexOf(SCHEME_SEPARATOR);
    if (pos == -1)
    {
      throw new IllegalArgumentException("Invalid URI: " + description);
    }

    String factoryType = description.substring(0, pos);
    if (StringUtil.isEmpty(factoryType))
    {
      throw new IllegalArgumentException("Invalid URI: " + description);
    }

    String connectorDescription = description.substring(pos + SCHEME_SEPARATOR.length());
    if (StringUtil.isEmpty(connectorDescription))
    {
      throw new IllegalArgumentException("Invalid URI: " + description);
    }

    pos = connectorDescription.indexOf('?');
    if (pos != -1)
    {
      connectorDescription = connectorDescription.substring(0, pos);
    }

    return (IConnector)container.getElement(ConnectorFactory.PRODUCT_GROUP, factoryType, connectorDescription);
  }
}
