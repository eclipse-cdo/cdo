/*
 * Copyright (c) 2007, 2008, 2010-2012, 2015 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.tcp;

import org.eclipse.net4j.acceptor.IAcceptor;
import org.eclipse.net4j.internal.tcp.bundle.OM;
import org.eclipse.net4j.tcp.ssl.SSLUtil;

/**
 * An {@link IAcceptor acceptor} that implements non-blocking multiplexed TCP transport, optionally with {@link SSLUtil
 * SSL}.
 *
 * @author Eike Stepper
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface ITCPAcceptor extends IAcceptor
{
  public static final String DEFAULT_ADDRESS = "0.0.0.0"; //$NON-NLS-1$

  /**
   * The value of the <i>org.eclipse.net4j.tcp.port</i> bundle/system property if defined, the value <i>2036</i>
   * otherwise.
   */
  public static final int DEFAULT_PORT = OM.getDefaultPort();

  /**
   * @since 4.0
   */
  public ITCPSelector getSelector();

  public String getAddress();

  public int getPort();
}
