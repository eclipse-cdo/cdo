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
package org.eclipse.emf.cdo.server.security;

import org.eclipse.emf.cdo.security.Realm;
import org.eclipse.emf.cdo.security.User;
import org.eclipse.emf.cdo.server.IRepository;

import org.eclipse.net4j.util.container.IManagedContainer;

/**
 * Protects a given {@link IRepository repository}.
 *
 * @see SecurityManagerUtil#createSecurityManager(org.eclipse.emf.cdo.server.IRepository, String)
 * @author Eike Stepper
 */
public interface ISecurityManager
{
  public IManagedContainer getContainer();

  public IRepository getRepository();

  public String getRealmPath();

  public Realm getRealm();

  public User getUser(String userID);

  public void modify(RealmOperation operation);

  /**
   * Modifies a security {@link Realm realm} in a safe transaction.
   *
   * @author Eike Stepper
   */
  public interface RealmOperation
  {
    public void execute(Realm realm);
  }
}
