/*
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

import org.eclipse.net4j.internal.tcp.TCPAcceptor;
import org.eclipse.net4j.internal.tcp.TCPConnector;

import java.text.MessageFormat;

/**
 * @author Teerawat Chaiyakijpichet (No Magic Asia Ltd.)
 * @author Caspar De Groot (No Magic Asia Ltd.)
 * @since 4.0
 */
public class SSLAcceptor extends TCPAcceptor
{
  @Override
  public String toString()
  {
    return MessageFormat.format("SSLAcceptor[{0}:{1}]", getAddress(), getPort()); //$NON-NLS-1$
  }

  @Override
  protected TCPConnector createConnector()
  {
    return new SSLServerConnector(this);
  }
}
