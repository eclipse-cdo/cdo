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
package org.eclipse.emf.cdo.server.spi.security;

import org.eclipse.emf.cdo.security.User;
import org.eclipse.emf.cdo.server.IRepository;
import org.eclipse.emf.cdo.server.IStoreAccessor.CommitContext;
import org.eclipse.emf.cdo.server.security.ISecurityManager;

import org.eclipse.net4j.util.container.IManagedContainer;

/**
 * @author Eike Stepper
 */
public interface InternalSecurityManager extends ISecurityManager
{
  public IManagedContainer getContainer();

  public IRepository getRepository();

  public String getRealmPath();

  public CommitHandler[] getCommitHandlers();

  public void addCommitHandler(CommitHandler handler);

  public void removeCommitHandler(CommitHandler handler);

  /**
   * @author Eike Stepper
   */
  public interface CommitHandler
  {
    public void handleCommit(InternalSecurityManager securityManager, CommitContext commitContext, User user);
  }
}