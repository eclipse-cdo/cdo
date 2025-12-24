/*
 * Copyright (c) 2022 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *   Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.internal.cdo.session;

import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.session.CDOSessionRegistry;

import org.eclipse.emf.internal.cdo.util.AbstractRegistry;

/**
 * @author Eike Stepper
 * @since 4.2
 */
public class CDOSessionRegistryImpl extends AbstractRegistry<CDOSession, CDOSessionRegistry.Registration> implements CDOSessionRegistry
{
  public static final CDOSessionRegistryImpl INSTANCE = new CDOSessionRegistryImpl();

  public CDOSessionRegistryImpl()
  {
  }

  @Override
  public CDOSession[] getSessions()
  {
    return getRegisteredElements();
  }

  @Override
  public CDOSession getSession(int id)
  {
    return getElement(id);
  }

  @Override
  protected CDOSession[] newArray(int size)
  {
    return new CDOSession[size];
  }

  @Override
  protected Registration[] newRegistrationArray(int size)
  {
    return new Registration[size];
  }

  @Override
  protected Registration newRegistration(int id, CDOSession session)
  {
    return new RegistrationImpl(id, session);
  }

  @Override
  protected int getRegisteredID(Registration registration)
  {
    return registration.getID();
  }

  @Override
  protected CDOSession getRegisteredElement(Registration registration)
  {
    return registration.getSession();
  }

  void register(CDOSession session)
  {
    registerElement(session);
  }

  void deregister(CDOSession session)
  {
    deregisterElement(session);
  }

  /**
   * @author Eike Stepper
   */
  private static final class RegistrationImpl implements Registration
  {
    private final int id;

    private final CDOSession session;

    public RegistrationImpl(int id, CDOSession session)
    {
      this.id = id;
      this.session = session;
    }

    @Override
    public int getID()
    {
      return id;
    }

    @Override
    public CDOSession getSession()
    {
      return session;
    }
  }
}
