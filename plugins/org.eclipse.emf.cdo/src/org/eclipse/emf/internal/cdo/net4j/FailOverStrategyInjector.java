/***************************************************************************
 * Copyright (c) 2004 - 2009 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.emf.internal.cdo.net4j;

import org.eclipse.emf.cdo.messages.Messages;

import org.eclipse.net4j.connector.IConnector;
import org.eclipse.net4j.signal.failover.NOOPFailOverStrategy;
import org.eclipse.net4j.util.StringUtil;
import org.eclipse.net4j.util.container.IElementProcessor;
import org.eclipse.net4j.util.container.IManagedContainer;

import org.eclipse.spi.net4j.ConnectorFactory;

import java.text.MessageFormat;

/**
 * @author Eike Stepper
 */
public class FailOverStrategyInjector implements IElementProcessor
{
  private static final String INVALID_URI_MESSAGE = Messages.getString("InvalidURIException.0"); //$NON-NLS-1$

  private static final String SCHEME_SEPARATOR = "://"; //$NON-NLS-1$

  public FailOverStrategyInjector()
  {
  }

  public Object process(IManagedContainer container, String productGroup, String factoryType, String description,
      Object element)
  {
    if (element instanceof org.eclipse.emf.cdo.net4j.CDOSession)
    {
      NOOPFailOverStrategy failOverStrategy = new NOOPFailOverStrategy(getConnector(container, description));
      ((org.eclipse.emf.cdo.net4j.CDOSession)element).options().getProtocol().setFailOverStrategy(failOverStrategy);
    }

    return element;
  }

  protected IConnector getConnector(IManagedContainer container, String description)
  {
    int pos = description.indexOf(SCHEME_SEPARATOR);
    if (pos == -1)
    {
      throw new IllegalArgumentException(MessageFormat.format(INVALID_URI_MESSAGE, description,
          Messages.getString("FailOverStrategyInjector.0"))); //$NON-NLS-1$
    }

    String factoryType = description.substring(0, pos);
    if (StringUtil.isEmpty(factoryType))
    {
      throw new IllegalArgumentException(MessageFormat.format(INVALID_URI_MESSAGE, description,
          Messages.getString("FailOverStrategyInjector.1"))); //$NON-NLS-1$
    }

    String connectorDescription = description.substring(pos + SCHEME_SEPARATOR.length());
    if (StringUtil.isEmpty(connectorDescription))
    {
      throw new IllegalArgumentException(MessageFormat.format(INVALID_URI_MESSAGE, description,
          Messages.getString("FailOverStrategyInjector.2"))); //$NON-NLS-1$
    }

    pos = connectorDescription.indexOf('?');
    if (pos != -1)
    {
      connectorDescription = connectorDescription.substring(0, pos);
    }

    return (IConnector)container.getElement(ConnectorFactory.PRODUCT_GROUP, factoryType, connectorDescription);
  }
}
