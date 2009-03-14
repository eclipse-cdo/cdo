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

import org.eclipse.emf.cdo.common.model.CDOPackageRegistry;
import org.eclipse.emf.cdo.common.revision.CDORevision;

import org.eclipse.net4j.util.container.IContainer;
import org.eclipse.net4j.util.om.monitor.OMMonitor;

import org.eclipse.emf.ecore.EPackage;

import java.util.List;
import java.util.Map;

/**
 * @author Eike Stepper
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface IRepository extends IContainer<Object>, IQueryHandlerProvider
{
  public String getName();

  public void setName(String name);

  public IStore getStore();

  public void setStore(IStore store);

  public Map<String, String> getProperties();

  public void setProperties(Map<String, String> properties);

  public String getUUID();

  public boolean isSupportingRevisionDeltas();

  public boolean isSupportingAudits();

  public boolean isVerifyingRevisions();

  /**
   * Returns the EMF {@link EPackage.Registry package registry} that is used by this repository.
   * <p>
   * This registry is managed by the {@link CDOPackageUnitManager package manager} of this repository.
   * 
   * @see #getPackageUnitManager()
   */
  public CDOPackageRegistry getPackageRegistry();

  public ISessionManager getSessionManager();

  public IRevisionManager getRevisionManager();

  /**
   * @since 2.0
   */
  public INotificationManager getNotificationManager();

  /**
   * @since 2.0
   */
  public IQueryHandlerProvider getQueryHandlerProvider();

  /**
   * Returns the repository creation time.
   * 
   * @since 2.0
   */
  public long getCreationTime();

  /**
   * Validates the given timeStamp against the repository time.
   * 
   * @throws IllegalArgumentException
   *           if the given timeStamp is less than the repository creation time or greater than the current repository
   *           time.
   * @since 2.0
   */
  public void validateTimeStamp(long timeStamp) throws IllegalArgumentException;

  /**
   * @since 2.0
   */
  public void addHandler(Handler handler);

  /**
   * @since 2.0
   */
  public void removeHandler(Handler handler);

  /**
   * A marker interface to indicate valid arguments to {@link IRepository#addHandler(Handler)} and
   * {@link IRepository#removeHandler(Handler)}.
   * 
   * @see ReadAccessHandler
   * @see WriteAccessHandler
   * @author Eike Stepper
   * @since 2.0
   */
  public interface Handler
  {
  }

  /**
   * Provides a way to handle revisions that are to be sent to the client.
   * 
   * @author Eike Stepper
   * @since 2.0
   */
  public interface ReadAccessHandler extends Handler
  {
    /**
     * Provides a way to handle revisions that are to be sent to the client.
     * 
     * @param session
     *          The session that is going to send the revisions.
     * @param revisions
     *          The revisions that are requested by the client. If the client must not see any of these revisions an
     *          unchecked exception must be thrown.
     * @param additionalRevisions
     *          The additional revisions that are to be sent to the client because internal optimizers believe that they
     *          will be needed soon. If the client must not see any of these revisions they should be removed from the
     *          list.
     * @throws RuntimeException
     *           to indicate that none of the revisions must be sent to the client. This exception will be visible at
     *           the client side!
     */
    public void handleRevisionsBeforeSending(ISession session, CDORevision[] revisions,
        List<CDORevision> additionalRevisions) throws RuntimeException;
  }

  /**
   * @author Eike Stepper
   * @since 2.0
   */
  public interface WriteAccessHandler extends Handler
  {
    /**
     * Provides a way to handle transactions that are to be committed to the backend store.
     * 
     * @param transaction
     *          The transaction that is going to be committed.
     * @param commitContext
     *          The context of the commit operation that is to be executed against the backend store. The context can be
     *          used to introspect all aspects of the current commit operation.
     * @param monitor
     *          A monitor that should be used by the implementor to avoid timeouts.
     * @throws RuntimeException
     *           to indicate that the commit operation must not be executed against the backend store. This exception
     *           will be visible at the client side!
     */
    public void handleTransactionBeforeCommitting(ITransaction transaction, IStoreAccessor.CommitContext commitContext,
        OMMonitor monitor) throws RuntimeException;
  }

  /**
   * @author Eike Stepper
   */
  public interface Props
  {
    /**
     * @since 2.0
     */
    public static final String OVERRIDE_UUID = "overrideUUID";

    /**
     * @since 2.0
     */
    public static final String SUPPORTING_AUDITS = "supportingAudits";

    /**
     * @since 2.0
     */
    public static final String VERIFYING_REVISIONS = "verifyingRevisions";

    /**
     * @since 2.0
     */
    public static final String CURRENT_LRU_CAPACITY = "currentLRUCapacity";

    /**
     * @since 2.0
     */
    public static final String REVISED_LRU_CAPACITY = "revisedLRUCapacity";
  }
}
