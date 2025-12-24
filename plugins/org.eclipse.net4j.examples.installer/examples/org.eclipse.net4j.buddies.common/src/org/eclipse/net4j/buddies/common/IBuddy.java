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
package org.eclipse.net4j.buddies.common;

import org.eclipse.net4j.util.security.IUserAware;

import org.eclipse.core.runtime.IAdaptable;

import java.util.Collection;
import java.util.Set;

/**
 * @author Eike Stepper
 */
public interface IBuddy extends IUserAware, IMembershipContainer, ICollaborationProvider, IAdaptable
{
  public State getState();

  public IAccount getAccount();

  public ISession getSession();

  public Set<String> getFacilityTypes();

  public IMembership initiate();

  public IMembership initiate(IBuddy buddy);

  public IMembership[] initiate(Collection<IBuddy> buddies);

  public IMembership join(long collaborationID);

  public IMembership join(Object invitationToken);

  /**
   * @author Eike Stepper
   */
  public enum State
  {
    AVAILABLE, LONESOME, AWAY, DO_NOT_DISTURB
  }
}
