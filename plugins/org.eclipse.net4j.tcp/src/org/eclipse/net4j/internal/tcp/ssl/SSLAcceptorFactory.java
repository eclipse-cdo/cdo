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
import org.eclipse.net4j.internal.tcp.TCPAcceptorFactory;

/**
 * @author Teerawat Chaiyakijpichet (No Magic Asia Ltd.)
 * @author Caspar De Groot (No Magic Asia Ltd.)
 * @since 4.0
 */
public class SSLAcceptorFactory extends TCPAcceptorFactory
{
  public static final String TYPE = "ssl"; //$NON-NLS-1$

  public SSLAcceptorFactory()
  {
    super(TYPE);
  }

  @Override
  protected TCPAcceptor createAcceptor()
  {
    return new SSLAcceptor();
  }
}
