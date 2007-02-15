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
package org.eclipse.net4j.transport.container;

import org.eclipse.net4j.transport.Acceptor;
import org.eclipse.net4j.transport.Connector;

/**
 * @author Eike Stepper
 */
public interface ContainerAdapter
{
  public Container getContainer();

  public String getType();

  public void initAcceptor(Acceptor acceptor);

  public void initConnector(Connector connector);
}
