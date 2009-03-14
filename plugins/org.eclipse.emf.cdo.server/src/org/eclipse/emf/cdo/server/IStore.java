/**
 * Copyright (c) 2004 - 2009 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.server;

import org.eclipse.emf.cdo.common.id.CDOIDLibraryDescriptor;
import org.eclipse.emf.cdo.common.id.CDOIDLibraryProvider;
import org.eclipse.emf.cdo.common.id.CDOIDMetaRange;
import org.eclipse.emf.cdo.common.id.CDOIDObjectFactory;

import org.eclipse.net4j.util.om.monitor.ProgressDistributor;

import java.util.Set;

/**
 * @author Eike Stepper
 */
public interface IStore
{
  public IRepository getRepository();

  /**
   * Internal.
   * 
   * @since 2.0
   */
  public void setRepository(IRepository repository);

  /**
   * @since 2.0
   */
  public String getType();

  public CDOIDObjectFactory getCDOIDObjectFactory();

  public CDOIDLibraryDescriptor getCDOIDLibraryDescriptor();

  public CDOIDLibraryProvider getCDOIDLibraryProvider();

  /**
   * @since 2.0
   */
  public Set<ChangeFormat> getSupportedChangeFormats();

  /**
   * @since 2.0
   */
  public Set<RevisionTemporality> getSupportedRevisionTemporalities();

  /**
   * @since 2.0
   */
  public Set<RevisionParallelism> getSupportedRevisionParallelisms();

  /**
   * @since 2.0
   */
  public RevisionTemporality getRevisionTemporality();

  /**
   * @since 2.0
   */
  public void setRevisionTemporality(RevisionTemporality revisionTemporality);

  /**
   * @since 2.0
   */
  public RevisionParallelism getRevisionParallelism();

  /**
   * @since 2.0
   */
  public void setRevisionParallelism(RevisionParallelism revisionParallelism);

  public CDOIDMetaRange getNextMetaIDRange(int count);

  /**
   * Returns the store creation time.
   * 
   * @since 2.0
   */
  public long getCreationTime();

  /**
   * Returns <code>true</code>if this store was activated for the first time, <code>false</code> otherwise.
   * 
   * @since 2.0
   */
  public boolean isFirstTime();

  /**
   * Returns a reader that can be used to read from this store in the context of the given session.
   * 
   * @param session
   *          The session that should be used as a context for read access or <code>null</code>. The store implementor
   *          is free to interpret and use the session in a manner suitable for him or ignore it at all. It is meant
   *          only as a hint. Implementor can use it as a key into a cache and/or register a
   *          {@link org.eclipse.net4j.util.lifecycle.LifecycleEventAdapter LifecycleEventAdapter} with it to intercept
   *          cleanup on session close. Note however that the session can be <code>null</code>, for example during
   *          startup of the server while the repositories are initialized but before any user session has been opened.
   * @return a reader that can be used to read from this store in the context of the given session, never
   *         <code>null</code>.
   * @since 2.0
   */
  public IStoreAccessor getReader(ISession session);

  /**
   * Returns a writer that can be used to write to this store in the context of the given view. The given view is always
   * marked as a transaction.
   * 
   * @param transaction
   *          The view that must be used as a context for write access. The store implementor is free to interpret and
   *          use the view in a manner suitable for him or ignore it at all. It is meant only as a hint. Implementor can
   *          use it as a key into a cache and/or register a
   *          {@link org.eclipse.net4j.util.lifecycle.LifecycleEventAdapter LifecycleEventAdapter} with it to intercept
   *          cleanup on view close.
   * @return a writer that can be used to write to this store in the context of the given view, never <code>null</code>.
   * @since 2.0
   */
  public IStoreAccessor getWriter(ITransaction transaction);

  /**
   * @since 2.0
   */
  public ProgressDistributor getIndicatingCommitDistributor();

  /**
   * @author Eike Stepper
   * @since 2.0
   */
  public enum ChangeFormat
  {
    REVISION, DELTA
  }

  /**
   * @author Eike Stepper
   * @since 2.0
   */
  public enum RevisionTemporality
  {
    NONE, AUDITING
  }

  /**
   * @author Eike Stepper
   * @since 2.0
   */
  public enum RevisionParallelism
  {
    NONE, BRANCHING
  }
}
