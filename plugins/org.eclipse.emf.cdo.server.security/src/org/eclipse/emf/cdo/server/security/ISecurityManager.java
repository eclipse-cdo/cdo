/*
 * Copyright (c) 2012, 2013, 2018, 2021, 2023 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.server.security;

import org.eclipse.emf.cdo.common.commit.CDOCommitInfo;
import org.eclipse.emf.cdo.security.Realm;
import org.eclipse.emf.cdo.security.SecurityItemContainer;
import org.eclipse.emf.cdo.server.IRepository;

import org.eclipse.net4j.util.security.IAuthenticator2;

/**
 * Protects a given {@link IRepository repository}.
 *
 * @see SecurityManagerUtil#createSecurityManager(String)
 * @author Eike Stepper
 * @noimplement This interface is not intended to be implemented by clients.
 * @noextend This interface is not intended to be extended by clients.
 */
public interface ISecurityManager extends SecurityItemContainer, IAuthenticator2
{
  /**
   * @since 4.2
   */
  public static final String SYSTEM_USER_ID = IRepository.SYSTEM_USER_ID;

  public IRepository getRepository();

  /**
   * @since 4.6
   */
  public IRepository[] getSecondaryRepositories();

  public Realm getRealm();

  /**
   * @since 4.2
   */
  public void read(RealmOperation operation);

  public void modify(RealmOperation operation);

  /**
   * @since 4.2
   */
  public void modify(RealmOperation operation, boolean waitUntilReadable);

  /**
   * @since 4.4
   */
  public CDOCommitInfo modifyWithInfo(RealmOperation operation, boolean waitUntilReadable);

  /**
   * Modifies a security {@link Realm realm} in a safe transaction.
   *
   * @author Eike Stepper
   */
  @FunctionalInterface
  public interface RealmOperation
  {
    public void execute(Realm realm);
  }
}
