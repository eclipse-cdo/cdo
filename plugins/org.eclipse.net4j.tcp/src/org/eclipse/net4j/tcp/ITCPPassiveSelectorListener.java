/*
 * Copyright (c) 2008, 2011, 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
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
