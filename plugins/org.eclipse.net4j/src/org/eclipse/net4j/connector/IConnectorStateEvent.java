/*
 * Copyright (c) 2007, 2008, 2011, 2012, 2015 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.connector;

/**
 * An event that is fired by an {@link IConnector} to indicate that its state has changed.
 *
 * @author Eike Stepper
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface IConnectorStateEvent extends IConnectorEvent
{
  /**
   * The old state of the {@link IConnector} that sent this event.
   */
  public ConnectorState getOldState();

  /**
   * The new state of the {@link IConnector} that sent this event.
   */
  public ConnectorState getNewState();
}
