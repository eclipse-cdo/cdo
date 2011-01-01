/**
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

import java.nio.channels.ServerSocketChannel;

/**
 * @author Eike Stepper
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface ITCPPassiveSelectorListener
{
  public void handleRegistration(ITCPSelector selector, ServerSocketChannel serverSocketChannel);

  public void handleAccept(ITCPSelector selector, ServerSocketChannel serverSocketChannel);
}
