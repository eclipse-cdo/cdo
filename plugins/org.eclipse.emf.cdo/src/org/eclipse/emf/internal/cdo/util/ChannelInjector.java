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

import org.eclipse.net4j.IConnector;
import org.eclipse.net4j.util.StringUtil;
import org.eclipse.net4j.util.container.IElementProcessor;
import org.eclipse.net4j.util.container.IManagedContainer;

import org.eclipse.internal.net4j.ConnectorFactory;

import org.eclipse.emf.common.util.URI;

/**
 * @author Eike Stepper
 */
public class ChannelInjector implements IElementProcessor
{
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
    URI uri = URI.createURI(description);
    String factoryType = uri.scheme();
    if (StringUtil.isEmpty(factoryType))
    {
      throw new IllegalArgumentException("Connector type (scheme) missing: " + description);
    }

    String connectorDescription = uri.authority();
    if (StringUtil.isEmpty(connectorDescription))
    {
      throw new IllegalArgumentException("Illegal connector description: " + description);
    }

    return (IConnector)container.getElement(ConnectorFactory.PRODUCT_GROUP, factoryType, connectorDescription);
  }
}
