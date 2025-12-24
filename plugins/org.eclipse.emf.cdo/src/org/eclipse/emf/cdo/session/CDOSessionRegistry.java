/*
 * Copyright (c) 2022 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.session;

import org.eclipse.net4j.util.container.IContainer;

/**
 * A global registry of all open {@link CDOSession sessions}.
 *
 * @author Eike Stepper
 * @since 4.17
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface CDOSessionRegistry extends IContainer<CDOSessionRegistry.Registration>
{
  /**
   * The singleton instance of the {@link CDOSessionRegistry session registry}.
   */
  public static final CDOSessionRegistry INSTANCE = org.eclipse.emf.internal.cdo.session.CDOSessionRegistryImpl.INSTANCE;

  /**
   * A symbolic constant returned from {@link #getID(CDOSession)} if the session is not registered.
   */
  public static final int NOT_REGISTERED = 0;

  /**
   * Returns the {@link Registration#getID() IDs} of all registered {@link CDOSession sessions}.
   */
  public int[] getIDs();

  /**
   * Returns all registered {@link CDOSession sessions}.
   */
  public CDOSession[] getSessions();

  /**
   * Returns the {@link Registration#getID() ID} of the given {@link CDOSession session} if it is registered, {@value #NOT_REGISTERED} otherwise.
   */
  public int getID(CDOSession session);

  /**
   * Returns the {@link CDOSession session} with the given {@link Registration#getID() ID} if it is registered, <code>null</code> otherwise.
   */
  public CDOSession getSession(int id);

  /**
   * A bidirectional mapping between a registered {@link CDOSession session} and its assigned {@link #getID() ID}.
   *
   * @author Eike Stepper
   */
  public interface Registration
  {
    /**
     * Returns the ID of this registration.
     */
    public int getID();

    /**
     * Returns the {@link CDOSession} of this registration.
     */
    public CDOSession getSession();
  }
}
