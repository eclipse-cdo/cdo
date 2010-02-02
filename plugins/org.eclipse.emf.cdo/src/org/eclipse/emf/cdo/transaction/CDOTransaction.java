/**
 * Copyright (c) 2004 - 2009 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Simon McDuff - bug 201266
 *    Simon McDuff - bug 215688
 *    Simon McDuff - bug 213402
 *    Victor Roldan Betancort - maintenance
 *    Gonzague Reydet - bug 298334
 */
package org.eclipse.emf.cdo.transaction;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.revision.delta.CDORevisionDelta;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.net4j.util.options.IOptionsEvent;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.ResourceSet;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;
import java.util.Set;

/**
 * A read-write view to the <em>current</em> (i.e. latest) state of the object graph in the repository.
 * 
 * @author Eike Stepper
 * @noimplement This interface is not intended to be implemented by clients.
 * @since 2.0
 */
public interface CDOTransaction extends CDOView, CDOUserTransaction
{
  /**
   * Returns <code>true</code> if this transaction is not closed and contains uncommitted changes, <code>false</code>
   * otherwise.
   */
  public boolean isDirty();

  /**
   * Returns <code>true</code> if this transaction contains local modifications that are conflicting with remote
   * modifications, <code>false</code> otherwise.
   */
  public boolean hasConflict();

  public Set<CDOObject> getConflicts();

  public void resolveConflicts(CDOConflictResolver... resolver);

  /**
   * @see ResourceSet#createResource(URI)
   */
  public CDOResource createResource(String path);

  public CDOResource getOrCreateResource(String path);

  /**
   * @since 3.0
   */
  public void addTransactionHandler(CDOTransactionHandler handler);

  /**
   * @since 3.0
   */
  public void removeTransactionHandler(CDOTransactionHandler handler);

  /**
   * @since 3.0
   */
  public CDOTransactionHandler[] getTransactionHandlers();

  /**
   * @since 3.0
   */
  public CDOSavepoint setSavepoint();

  /**
   * @since 3.0
   */
  public CDOSavepoint getLastSavepoint();

  public Map<CDOID, CDOResource> getNewResources();

  public Map<CDOID, CDOObject> getNewObjects();

  /**
   * @since 2.0
   */
  public Map<CDOID, CDOObject> getDetachedObjects();

  public Map<CDOID, CDOObject> getDirtyObjects();

  public Map<CDOID, CDORevisionDelta> getRevisionDeltas();

  /**
   * @since 3.0
   */
  public CDOSavepoint[] exportChanges(OutputStream out) throws IOException;

  /**
   * @since 3.0
   */
  public CDOSavepoint[] importChanges(InputStream in, boolean reconstructSavepoints) throws IOException;

  public Options options();

  /**
   * @author Simon McDuff
   */
  public interface Options extends CDOView.Options
  {
    /**
     * Returns a copy of the conflict resolver list of this transaction.
     */
    public CDOConflictResolver[] getConflictResolvers();

    /**
     * Sets the conflict resolver list of this transaction.
     */
    public void setConflictResolvers(CDOConflictResolver[] resolvers);

    /**
     * Adds a conflict resolver to the list of conflict resolvers of this transaction.
     */
    public void addConflictResolver(CDOConflictResolver resolver);

    /**
     * Removes a conflict resolver from the list of conflict resolvers of this transaction.
     */
    public void removeConflictResolver(CDOConflictResolver resolver);

    /**
     * Returns true if locks in this view will be removes when {@link CDOTransaction#commit()} or
     * {@link CDOTransaction#rollback()} is called.
     * <p>
     * Default value is true.
     */
    public boolean isAutoReleaseLocksEnabled();

    /**
     * Specifies whether locks in this view will be removed when {@link CDOTransaction#commit()} or
     * {@link CDOTransaction#rollback()} is called.
     * <p>
     * If false all locks are kept.
     * <p>
     * Default value is true.
     */
    public void setAutoReleaseLocksEnabled(boolean on);

    /**
     * @author Eike Stepper
     */
    public interface ConflictResolversEvent extends IOptionsEvent
    {
    }

    /**
     * @author Eike Stepper
     * @since 3.0
     */
    public interface AutoReleaseLocksEvent extends IOptionsEvent
    {
    }
  }
}
