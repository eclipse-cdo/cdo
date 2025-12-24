/*
 * Copyright (c) 2012, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.admin;

import org.eclipse.emf.cdo.common.admin.CDOAdmin;

import org.eclipse.net4j.connector.IConnector;
import org.eclipse.net4j.util.event.IEvent;

import java.util.Map;

/**
 * A client-side {@link CDOAdmin administrative interface}.
 * <p>
 * It can fire the following events:
 * <ul>
 * <li> {@link ConnectionStateChangedEvent} after the connection {@link #isConnected() state} has changed.
 * </ul>
 *
 * @author Eike Stepper
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface CDOAdminClient extends CDOAdmin
{
  public String getURL();

  public boolean isConnected();

  public IConnector getConnector();

  @Override
  public CDOAdminClientRepository[] getRepositories();

  @Override
  public CDOAdminClientRepository getRepository(String name);

  @Override
  public CDOAdminClientRepository createRepository(String name, String type, Map<String, Object> properties);

  @Override
  public CDOAdminClientRepository waitForRepository(String name);

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
    @Override
    public CDOAdminClient getSource();

    public boolean isConnected();
  }
}
