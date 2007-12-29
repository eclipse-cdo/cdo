/***************************************************************************
 * Copyright (c) 2004 - 2008 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.net4j.tcp;

import org.eclipse.net4j.acceptor.IAcceptor;

/**
 * @author Eike Stepper
 */
public interface ITCPAcceptor extends IAcceptor
{
  public static final String DEFAULT_ADDRESS = "0.0.0.0"; //$NON-NLS-1$

  public static final int DEFAULT_PORT = 2036;

  public String getAddress();

  public int getPort();
}
