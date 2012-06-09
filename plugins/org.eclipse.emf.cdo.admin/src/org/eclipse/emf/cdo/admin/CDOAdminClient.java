/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.admin;

import org.eclipse.emf.cdo.common.admin.CDOAdmin;

import org.eclipse.net4j.util.event.IEvent;

/**
 * A client-side {@link CDOAdmin administrative interface}.
 * <p>
 * It can fire the following events:
 * <ul>
 * <li> {@link ConnectionStateChangedEvent} after the connection {@link #isConnected() state} has changed.
 * </ul>
 *
 * @author Eike Stepper
 */
public interface CDOAdminClient extends CDOAdmin
{
  public String getURL();

  public boolean isConnected();

  /**
   * An {@link IEvent event} fired from a client-side {@link CDOAdminClient administrative interface}
   * after the connection {@link CDOAdminClient#isConnected() state} has changed.
   *
   * @author Eike Stepper
   * @noextend This interface is not intended to be extended by clients.
   * @noimplement This interface is not intended to be implemented by clients.
   */
  public interface ConnectionStateChangedEvent extends IEvent
  {
    public CDOAdminClient getSource();

    public boolean isConnected();
  }
}
