/***************************************************************************
 * Copyright (c) 2004 - 2007 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.net4j;

/**
 * Provides the ability to execute a negotitation phase between two connectors.
 * 
 * @author Eike Stepper
 * @since 0.8.0
 */
public interface IConnectorNegotiator
{
  /**
   * Executes the negotitation phase between the given connector and ts peer connector.
   * 
   * @return <code>true</code> if the negotiation succeeded, <code>false</code> otherwise.
   */
  public boolean negotiate(IConnector connector);
}
