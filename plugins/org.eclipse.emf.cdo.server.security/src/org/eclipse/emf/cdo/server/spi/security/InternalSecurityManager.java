/*
 * Copyright (c) 2012-2014, 2016, 2019, 2021 Eike Stepper (Loehne, Germany) and others.
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

import org.eclipse.net4j.util.container.IManagedContainerProvider;
import org.eclipse.net4j.util.factory.ProductCreationException;

/**
 * If the meaning of this type isn't clear, there really should be more of a description here...
 *
 * @author Eike Stepper
 * @noimplement This interface is not intended to be implemented by clients.
 * @noextend This interface is not intended to be extended by clients.
 */
public interface InternalSecurityManager extends ISecurityManager, IManagedContainerProvider
{
  public void setRepository(InternalRepository repository);

  /**
   * @since 4.6
   */
  @Override
  public InternalRepository[] getSecondaryRepositories();

  /**
   * @since 4.6
   */
  public void addSecondaryRepository(InternalRepository repository);

  /**
   * @since 4.6
   */
  public void removeSecondaryRepository(InternalRepository repository);

  public String getRealmPath();

  public CommitHandler[] getCommitHandlers();

  /**
   * @since 4.3
   */
  public CommitHandler2[] getCommitHandlers2();

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

    /**
     * Called <b>before</b> the commit is security checked and passed to the repository.
     *
     * @param user the committing user or <code>null</code> if this commit is
     * {@link ISecurityManager#modify(ISecurityManager.RealmOperation, boolean) triggered} by the system.
     *
     * @see CommitHandler2
     */
    public void handleCommit(InternalSecurityManager securityManager, CommitContext commitContext, User user);

    /**
     * Creates {@link CommitHandler} instances.
     *
     * @author Eike Stepper
     * @since 4.3
     */
    public static abstract class Factory extends org.eclipse.net4j.util.factory.Factory
    {
      public static final String PRODUCT_GROUP = "org.eclipse.emf.cdo.server.security.commitHandlers";

      public Factory(String type)
      {
        super(PRODUCT_GROUP, type);
      }

      @Override
      public abstract CommitHandler create(String description) throws ProductCreationException;
    }
  }

  /**
   * If the meaning of this type isn't clear, there really should be more of a description here...
   *
   * @author Eike Stepper
   * @since 4.3
   */
  public interface CommitHandler2 extends CommitHandler
  {
    /**
     * Called <b>after</b> the commit has succeeded.
     */
    public void handleCommitted(InternalSecurityManager securityManager, CommitContext commitContext);

    /**
     * If the meaning of this type isn't clear, there really should be more of a description here...
     *
     * @author Eike Stepper
     */
    public static abstract class WithUser implements CommitHandler2
    {
      @Override
      public void handleCommit(InternalSecurityManager securityManager, CommitContext commitContext, User user)
      {
        commitContext.setData(this, user);
      }

      @Override
      public void handleCommitted(InternalSecurityManager securityManager, CommitContext commitContext)
      {
        User user = commitContext.getData(this);
        handleCommitted(securityManager, commitContext, user);
      }

      protected abstract void handleCommitted(InternalSecurityManager securityManager, CommitContext commitContext, User user);
    }
  }
}
