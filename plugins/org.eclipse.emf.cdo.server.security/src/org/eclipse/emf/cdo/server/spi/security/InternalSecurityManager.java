/*
 * Copyright (c) 2012-2014, 2016, 2019, 2021, 2023, 2025 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
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

import java.util.Map;

/**
 * Internal security service bound to a repository. It evaluates operations against a realm, coordinates secondary
 * repositories and permission caches, and invokes commit handlers around repository writes.
 *
 * @author Eike Stepper
 * @noimplement This interface is not intended to be implemented by clients.
 * @noextend This interface is not intended to be extended by clients.
 */
public interface InternalSecurityManager extends ISecurityManager, IManagedContainerProvider
{
  public void setRepository(InternalRepository repository);

  /**
   * Returns the permission-cache generation creator used by this manager.
   * <p>
   * A creator explicitly supplied through {@link #setPermissionCacheCreator(PermissionCache.Creator)} takes
   * precedence over repository properties. Otherwise, the creator is resolved from the repository properties when a
   * repository is assigned. If no permission-cache type is configured, the built-in default type is used.
   *
   * @since 4.13
   */
  public PermissionCache.Creator getPermissionCacheCreator();

  /**
   * Sets an explicit permission-cache generation creator while this manager is inactive.
   * <p>
   * An explicitly supplied creator takes precedence over creator type and description configured in the repository
   * properties. If no explicit creator is supplied, the manager resolves its creator from the repository properties
   * when a repository is assigned; an omitted cache type selects the built-in default type.
   * <p>
   * This method may only be called while the manager is inactive.
   *
   * @param creator the creator to use for this manager
   * @since 4.13
   */
  public void setPermissionCacheCreator(PermissionCache.Creator creator);

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
   * @since 4.10
   */
  public void addSecondaryRepository(InternalRepository repository, Map<String, Object> authorizationContext);

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
   * Repository extension invoked before a commit is security-checked and written. Implementations can inspect or
   * prepare security-related state for the committing user.
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
      public static final String PRODUCT_GROUP = "org.eclipse.emf.cdo.server.security.commitHandlers"; //$NON-NLS-1$

      public Factory(String type)
      {
        super(PRODUCT_GROUP, type);
      }

      @Override
      public abstract CommitHandler create(String description) throws ProductCreationException;
    }
  }

  /**
   * Commit-handler extension that receives a callback after a commit succeeds, allowing security state to be updated
   * only once the repository has accepted the change.
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
     * Convenience base for post-commit handlers that need the user associated with the commit. It stores that user in
     * the commit context before the commit and passes it to the post-commit callback.
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
