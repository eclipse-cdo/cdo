/*
 * Copyright (c) 2012 Eike Stepper (Berlin, Germany) and others.
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
import org.eclipse.emf.cdo.server.IStoreAccessor.CommitContext;
import org.eclipse.emf.cdo.server.security.ISecurityManager;
import org.eclipse.emf.cdo.spi.server.InternalRepository;

import org.eclipse.net4j.util.container.IManagedContainer;

/**
 * If the meaning of this type isn't clear, there really should be more of a description here...
 *
 * @author Eike Stepper
 * @noimplement This interface is not intended to be implemented by clients.
 * @noextend This interface is not intended to be extended by clients.
 */
public interface InternalSecurityManager extends ISecurityManager
{
  public IManagedContainer getContainer();

  public void setRepository(InternalRepository repository);

  public String getRealmPath();

  public CommitHandler[] getCommitHandlers();

  public void addCommitHandler(CommitHandler handler);

  public void removeCommitHandler(CommitHandler handler);

  /**
   * If the meaning of this type isn't clear, there really should be more of a description here...
   *
   * @author Eike Stepper
   */
  public interface CommitHandler
  {
    public void init(InternalSecurityManager securityManager, boolean firstTime);

    public void handleCommit(InternalSecurityManager securityManager, CommitContext commitContext, User user);
  }
}
