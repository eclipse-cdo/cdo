/*
 * Copyright (c) 2026 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.emf.cdo.transaction;

import org.eclipse.net4j.util.event.IEvent;

/**
 * Event fired by the real transaction after a scope has closed.
 *
 * @since 4.30
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface CDOTransactionScopeClosedEvent extends IEvent
{
  /**
   * Returns the real transaction that emitted this event.
   *
   * @return the owning transaction.
   */
  @Override
  public CDOTransaction getSource();

  /**
   * Returns the scope whose transition completed.
   *
   * @return the closed scope.
   */
  public CDOTransactionScope getScope();

  /**
   * Returns whether the scope was committed or rolled back.
   *
   * @return the completed transition cause.
   */
  public Cause getCause();

  /**
   * The cause that closed a scope.
   *
   * @author Eike Stepper
   */
  public enum Cause
  {
    /**
     * The scope completed successfully and its changes were accepted.
     */
    COMMITTED,

    /**
     * The scope and its descendants were restored to their boundary state.
     */
    ROLLED_BACK
  }
}
