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
import org.eclipse.emf.cdo.security.SecurityItemContainer;
import org.eclipse.emf.cdo.server.IRepository;

/**
 * Protects a given {@link IRepository repository}.
 *
 * @noimplement This interface is not intended to be implemented by clients.
 * @noextend This interface is not intended to be extended by clients.
 * @see SecurityManagerUtil#createSecurityManager(String)
 * @author Eike Stepper
 */
public interface ISecurityManager extends SecurityItemContainer
{
  public IRepository getRepository();

  public Realm getRealm();

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
