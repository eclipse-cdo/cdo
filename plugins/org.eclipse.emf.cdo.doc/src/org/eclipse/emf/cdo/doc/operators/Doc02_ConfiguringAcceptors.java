/*
 * Copyright (c) 2015 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.doc.operators;

import org.eclipse.net4j.acceptor.IAcceptor;
import org.eclipse.net4j.jvm.IJVMAcceptor;
import org.eclipse.net4j.tcp.ITCPAcceptor;
import org.eclipse.net4j.util.security.INegotiator;
import org.eclipse.net4j.ws.IWSAcceptor;

/**
 * Configuring Acceptors
 * <p>
 * The acceptors of a CDO Server are configured in the cdo-server.xml file. Here's an example:
 * {@link #cdoServerXML() cdo&#8209;server.xml}
 * <p>
 * The following sections describe the various elements and properties.
 * <p>
 * <b>Table of Contents</b> {@toc}
 *
 * @author Eike Stepper
 */
public class Doc02_ConfiguringAcceptors
{
  /**
   * @snippet xml cdo-server-acceptor.xml
   */
  public void cdoServerXML()
  {
  }

  /**
   * Element acceptor
   * <p>
   * Defines an {@link IAcceptor} instance.
   * Please refer to the Net4j Signalling Platform documentation for details about acceptors and connectors.
   * <p>
   * The <code>type</code> attribute corresponds to the type of an acceptor factory that is contributed via the
   * <code>org.eclipse.net4j.util.factories</code> extension point with a product group of
   * <code>org.eclipse.net4j.acceptors</code>.
   * <p>
   * The remaining attributes depend on the specified <code>type</code> attribute value.
   * The following values are possible with the shipped distribution (subject to user-supplied extension):
   * <ul>
   * <li> <b>tcp</b>: {@link ITCPAcceptor} for fast, new I/O based socket connections. The following additional attributes are recognized:
   *      <ul>
   *      <li> <b>listenAddr</b>: The network address the server socket shall be bound to.
   *           A value of <code>"0.0.0.0"</code> is the default (whole attribute can be omitted) and
   *           tells the socket to listen on <b>all</b> available addresses.
   *      <li> <b>port</b>: The network port the server socket shall be bound to.
   *           A value of <code>2036</code> is the default (whole attribute can be omitted).
   *      </ul>
   * <li> <b>ssl</b>: Similar to tcp but with transport-level security (TLS).
   * <li> <b>ws</b>: {@link IWSAcceptor} for WebSocket-based connections over HTTP.
   * <li> <b>jvm</b>: {@link IJVMAcceptor} for JVM internal (non-socket based ) connections.
   * </ul>
   */
  public class Element_acceptor
  {
  }

  /**
   * Element negotiator
   * <p>
   * Defines an {@link INegotiator} instance to be used by the enclosing {@link Element_acceptor acceptor} element.
   * Please refer to the Net4j Signalling Platform documentation for details about negotiators and the pluggable security concept
   * that can be used for authentication and authorization on the transport-level.
   * <p>
   * The <code>type</code> attribute corresponds to the type of a negotiator factory that is contributed via the
   * <code>org.eclipse.net4j.util.factories</code> extension point with a product group of <code>org.eclipse.net4j.negotiators</code>.
   * <p>
   * The remaining attributes depend on the specified type attribute value.
   * The following values are possible with the shipped distribution (subject to user-supplied extension):
   * <ul>
   * <li> <b>challenge</b>: Negotiator for simple yet effective and cryptographically secure challenge/response-based negotiations.
   *      The following additional attributes are recognized:
   *      <ul>
   *      <li> <b>description</b>: The absolute path to a file in the local file system that contains
   *           the credentials of the users in the form <code>user-id: password</code>.
   *      </ul>
   * </ul>
   */
  public class Element_negotiator
  {
  }
}
