/*
 * Copyright (c) 2020 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.ws;

import org.eclipse.net4j.connector.IConnector;

import java.net.URI;

/**
 * A {@link IConnector connector} that implements Websockets-based transport.
 *
 * @author Eike Stepper
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface IWSConnector extends IConnector
{
  public static final String ACCEPTOR_NAME_PREFIX = "@";

  public URI getServiceURI();

  public String getAcceptorName();
}
