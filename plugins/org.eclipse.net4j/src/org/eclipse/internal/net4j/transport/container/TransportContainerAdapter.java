/***************************************************************************
 * Copyright (c) 2004-2007 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.internal.net4j.transport.container;

import org.eclipse.net4j.transport.AcceptorFactory;
import org.eclipse.net4j.transport.ConnectorFactory;
import org.eclipse.net4j.transport.container.Container;

public abstract class TransportContainerAdapter extends AbstractContainerAdapter
{
  private AcceptorFactory acceptorFactory;

  private ConnectorFactory connectorFactory;

  protected TransportContainerAdapter(Container container, String type)
  {
    super(container, type);
    acceptorFactory = createAcceptorFactory();
    connectorFactory = createConnectorFactory();
  }

  @Override
  protected void onActivate() throws Exception
  {
    super.onActivate();
    getContainer().register(acceptorFactory);
    getContainer().register(connectorFactory);
  }

  @Override
  protected void onDeactivate() throws Exception
  {
    getContainer().deregister(connectorFactory);
    getContainer().deregister(acceptorFactory);
    super.onDeactivate();
  }

  protected abstract AcceptorFactory createAcceptorFactory();

  protected abstract ConnectorFactory createConnectorFactory();
}