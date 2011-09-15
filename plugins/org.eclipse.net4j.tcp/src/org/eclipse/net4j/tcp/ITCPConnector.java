/*
 * Copyright (c) 2004 - 2011 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.tcp;

import org.eclipse.net4j.connector.IConnector;
import org.eclipse.net4j.tcp.ssl.SSLUtil;

/**
 * A {@link IConnector connector} that implements non-blocking multiplexed TCP transport, optionally with
 * {@link SSLUtil SSL}.
 * 
 * @author Eike Stepper
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface ITCPConnector extends IConnector
{
  public static final int DEFAULT_PORT = ITCPAcceptor.DEFAULT_PORT;

  /**
   * @since 4.0
   */
  public ITCPSelector getSelector();

  public String getHost();

  public int getPort();
}
