/*
 * Copyright (c) 2008, 2011, 2012, 2015 Eike Stepper (Loehne, Germany) and others.
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

import java.nio.channels.ServerSocketChannel;

/**
 * Call-back that handles the possible calls from a {@link ITCPSelector selector} to a passive consumer, usually an
 * {@link IAcceptor acceptor}.
 *
 * @author Eike Stepper
 * @noimplement This interface is not intended to be implemented by clients.
 * @noextend This interface is not intended to be extended by clients.
 */
public interface ITCPPassiveSelectorListener
{
  public void handleRegistration(ITCPSelector selector, ServerSocketChannel serverSocketChannel);

  public void handleAccept(ITCPSelector selector, ServerSocketChannel serverSocketChannel);
}
