/*
 * Copyright (c) 2007-2012, 2015, 2016, 2021 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.acceptor;

import org.eclipse.net4j.ILocationAware.Location;
import org.eclipse.net4j.connector.IConnector;
import org.eclipse.net4j.util.collection.Closeable;
import org.eclipse.net4j.util.container.IContainer;
import org.eclipse.net4j.util.properties.IPropertiesContainer;

import org.eclipse.spi.net4j.Acceptor;

/**
 * Accepts incoming connection requests from {@link Location#CLIENT client} {@link IConnector connectors} and creates
 * the appropriate {@link Location#SERVER server} connectors.
 * <p>
 * Since the process of accepting connection requests is heavily dependent on the implementation of the respective
 * connectors the only public API is introspection and notification.
 * <p>
 * This interface is <b>not</b> intended to be implemented by clients. Service providers <b>must</b> extend the abstract
 * {@link Acceptor} class.
 *
 * @author Eike Stepper
 * @noimplement This interface is not intended to be implemented by clients.
 * @noextend This interface is not intended to be extended by clients.
 */
public interface IAcceptor extends IContainer<IConnector>, IPropertiesContainer, Closeable
{
  /**
   * Returns an array of the connectors that have been accepted by this acceptor and not been closed since.
   */
  public IConnector[] getAcceptedConnectors();
}
