/***************************************************************************
 * Copyright (c) 2004 - 2007 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.emf.cdo.server;

import org.eclipse.net4j.internal.util.lifecycle.LifecycleEventAdapter;

/**
 * @author Eike Stepper
 */
public interface IStore extends IRepositoryElement
{
  public void setRepository(IRepository repository);

  public String getStoreType();

  /**
   * @return <code>true</code> if this store supports the retrieval of historical revisions, <code>false</code>
   *         otherwise.
   */
  public boolean hasAuditingSupport();

  /**
   * @return <code>true</code> if this store supports the storage of concurrent revisions in separate branches,
   *         <code>false</code> otherwise.
   */
  public boolean hasBranchingSupport();

  /**
   * @return <code>true</code> if this store supports the efficient lookup of object types, <code>false</code>
   *         otherwise.
   */
  public boolean hasEfficientTypeLookup();

  public boolean hasCrashed();

  public void repairAfterCrash();

  /**
   * Returns a reader that can be used to read from this store in the context of the given session.
   * 
   * @param session
   *          The session that should be used as a context for read access or <code>null</code>. The store
   *          implementor is free to interpret and use the session in a manner suitable for him or ignore it at all. It
   *          is meant only as a hint. Implementor can use it as a key into a cache and/or register a
   *          {@link LifecycleEventAdapter} with it to intercept cleanup on session close. Note however that the session
   *          can be <code>null</code>, for example during startup of the server while the repositories are
   *          initialized but before any user session has been opened.
   */
  public IStoreReader getReader(ISession session);

  /**
   * Returns a writer that can be used to write to this store in the context of the given view. The given view is always
   * marked as a transaction.
   * 
   * @param session
   *          The view that must be used as a context for write access. The store implementor is free to interpret and
   *          use the view in a manner suitable for him or ignore it at all. It is meant only as a hint. Implementor can
   *          use it as a key into a cache and/or register a {@link LifecycleEventAdapter} with it to intercept cleanup
   *          on view close.
   */
  public IStoreWriter getWriter(IView view);
}
