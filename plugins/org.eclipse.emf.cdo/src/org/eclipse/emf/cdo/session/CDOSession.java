/*
 * Copyright (c) 2009-2013, 2015, 2016, 2019, 2021-2024 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Simon McDuff - maintenance
 *    Victor Roldan Betancort - maintenance
 *    Andre Dietisheim - bug 256649
 *    Christian W. Damus (CEA LIST) - bug 399306
 */
package org.eclipse.emf.cdo.session;

import org.eclipse.emf.cdo.CDOLock;
import org.eclipse.emf.cdo.common.CDOCommonSession;
import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.branch.CDOBranchManager;
import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.commit.CDOChangeSetData;
import org.eclipse.emf.cdo.common.commit.CDOCommitInfoManager;
import org.eclipse.emf.cdo.common.id.CDOIDGenerator;
import org.eclipse.emf.cdo.common.lob.CDOBlob;
import org.eclipse.emf.cdo.common.lob.CDOClob;
import org.eclipse.emf.cdo.common.lob.CDOLobStore;
import org.eclipse.emf.cdo.common.model.CDOPackageRegistry;
import org.eclipse.emf.cdo.common.model.CDOPackageUnit;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.CDORevisionManager;
import org.eclipse.emf.cdo.common.security.CDOPermission;
import org.eclipse.emf.cdo.session.remote.CDORemoteSessionManager;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.transaction.CDOTransactionContainer;
import org.eclipse.emf.cdo.util.CDOUpdatable;
import org.eclipse.emf.cdo.util.CDOUtil;
import org.eclipse.emf.cdo.view.CDOFetchRuleManager;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.net4j.util.collection.Entity;
import org.eclipse.net4j.util.concurrent.DelegableReentrantLock;
import org.eclipse.net4j.util.options.IOptionsEvent;
import org.eclipse.net4j.util.security.IPasswordCredentialsProvider;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EPackage.Registry;
import org.eclipse.emf.spi.cdo.CDOPermissionUpdater;
import org.eclipse.emf.spi.cdo.CDOSessionProtocol;
import org.eclipse.emf.spi.cdo.CDOSessionProtocol.RefreshSessionResult;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.Map;
import java.util.concurrent.locks.Lock;

/**
 * Represents and controls the connection to a model repository in addition to the inherited {@link CDOView view}
 * management functions.
 * <p>
 * A session has the following responsibilities:
 * <ul>
 * <li> {@link CDOSession#getRepositoryInfo() Repository information}
 * <li> {@link CDOSession#getPackageRegistry() Package registry}
 * <li> {@link CDOSession#getBranchManager() Branch management}
 * <li> {@link CDOSession#getRevisionManager() Revision management}
 * <li> {@link CDOSession#getFetchRuleManager() Fetch rule management}
 * <li> {@link CDOSession#getCommitInfoManager() Commit information management}
 * <li> {@link CDOSession#getExceptionHandler() Exception handling}
 * <li> {@link CDOSession#getIDGenerator() ID generation}
 * <li> {@link CDOSession#getViews() View management}
 * </ul>
 * <p>
 * Note that in order to retrieve, access and store {@link EObject objects} a {@link CDOView view} is needed. The
 * various <code>openXYZ</code> methods are provided for this purpose.
 * <p>
 * A session can fire the following events:
 * <ul>
 * <li> {@link CDOSessionInvalidationEvent} after {@link Options#setPassiveUpdateEnabled(boolean) commit notifications}
 * have been received and processed.
 * <li> {@link CDOSessionLocksChangedEvent} after {@link CDOLock locks} have been acquired or released.
 * <li> {@link CDOSessionPermissionsChangedEvent} after {@link CDOPermission revision permissions} have changed.
 * </ul>
 *
 * @author Eike Stepper
 * @since 2.0
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface CDOSession extends CDOCommonSession, CDOUpdatable, CDOTransactionContainer, IPasswordCredentialsProvider.Provider
{
  /**
   * Returns an instance of {@link CDORepositoryInfo} that describes the model repository this {@link CDOSession
   * session} is connected to.
   *
   * @since 3.0
   */
  public CDORepositoryInfo getRepositoryInfo();

  /**
   * Returns the EMF {@link Registry package registry} that is used by all {@link EObject objects} of all
   * {@link CDOView views} of this session.
   * <p>
   * This registry is managed by the {@link CDOPackageUnit package unit manager} of this session. All {@link EPackage
   * packages} that are already persisted in the repository of this session are automatically registered with this
   * registry. New packages can be locally registered with this registry and are committed to the repository through a
   * {@link CDOTransaction transaction}, if needed.
   */
  public CDOPackageRegistry getPackageRegistry();

  /**
   * Returns the CDO {@link CDOBranchManager branch manager} that manages the {@link CDOBranch branches} of the
   * repository of this session.
   *
   * @since 3.0
   */
  @Override
  public CDOBranchManager getBranchManager();

  /**
   * Returns the CDO {@link CDORevisionManager revision manager} that manages the {@link CDORevision revisions} of the
   * repository of this session.
   *
   * @since 3.0
   */
  @Override
  public CDORevisionManager getRevisionManager();

  /**
   * Returns the CDO {@link CDOFetchRuleManager fetch rule manager} of this session.
   *
   * @since 3.0
   */
  public CDOFetchRuleManager getFetchRuleManager();

  /**
   * Returns the CDO {@link CDORemoteSessionManager remote session manager} that keeps track of the other remote
   * sessions served by the repository of this local session.
   */
  public CDORemoteSessionManager getRemoteSessionManager();

  /**
   * Returns the CDO {@link CDOCommitInfoManager commit info manager} of this session.
   *
   * @since 3.0
   */
  @Override
  public CDOCommitInfoManager getCommitInfoManager();

  /**
   * @since 4.26
   */
  public CDOUserInfoManager getUserInfoManager();

  /**
   * Returns the {@link ExceptionHandler exception handler} of this session.
   */
  public ExceptionHandler getExceptionHandler();

  /**
   * Returns the CDO {@link CDOIDGenerator ID generator} of this session.
   *
   * @since 4.1
   */
  public CDOIDGenerator getIDGenerator();

  /**
   * Refreshes the object caches of all (non-historical) {@link CDOView views}.
   *
   * @since 3.0
   */
  public long refresh();

  /**
   * @since 4.4
   */
  public long refresh(RefreshSessionResult.Provider provider);

  /**
   * Equivalent to calling {@link CDOView#waitForUpdate(long)} on each of this session's views. That is, this blocks the
   * calling thread until all of this session's views have incorporated a commit operation with the given time stamp (or
   * higher).
   *
   * @param updateTime the time stamp of the update to wait for in milliseconds since Unix epoch.
   */
  @Override
  public void waitForUpdate(long updateTime);

  /**
   * Equivalent to calling {@link CDOView#waitForUpdate(long)} on each of this session's views. That is, this blocks the
   * calling thread until all of this session's views have incorporated a commit operation with the given time stamp (or
   * higher) or the given total timeout has expired.
   *
   * @param updateTime the time stamp of the update to wait for in milliseconds since Unix epoch.
   * @param timeoutMillis the maximum number of milliseconds to wait for the update to occur,
   *        or {@link CDOUpdatable#NO_TIMEOUT} to wait indefinitely.
   * @return <code>true</code> if the update occurred within the specified timeout period, <code>false</code> otherwise.
   */
  @Override
  public boolean waitForUpdate(long updateTime, long timeoutMillis);

  /**
   * @since 4.0
   */
  public CDOChangeSetData compareRevisions(CDOBranchPoint source, CDOBranchPoint target);

  /**
   * Initiates (possibly interactive) changing of credentials for the user logged in in this session.
   * This is an optional operation of the session.
   *
   * @throws UnsupportedOperationException if the session implementation does not permit changing credentials
   *
   * @since 4.3
   * @deprecated As of 4.13 use {@link #changeServerPassword()}.
   *
   * @see #getCredentialsProvider()
   */
  @Deprecated
  public void changeCredentials();

  /**
   * Initiates (possibly interactive) changing of credentials for the user logged in in this session.
   * This is an optional operation of the session.
   *
   * @throws UnsupportedOperationException if the session implementation does not permit changing credentials
   *
   * @since 4.13
   * @see #getCredentialsProvider()
   */
  public char[] changeServerPassword();

  /**
   * @since 4.13
   */
  public CDOClob newClob(Reader contents) throws IOException;

  /**
   * @since 4.13
   */
  public CDOClob newClob(String contents) throws IOException;

  /**
   * @since 4.13
   */
  public CDOBlob newBlob(InputStream contents) throws IOException;

  /**
   * @since 4.13
   */
  public CDOBlob newBlob(byte[] contents) throws IOException;

  /**
   * @since 4.27
   */
  public Map<String, Entity> clientEntities();

  /**
   * Returns the {@link Options options} of this session.
   */
  @Override
  public Options options();

  /**
   * Encapsulates a set of notifying {@link CDOSession session} configuration options.
   * <p>
   * The session options can fire the following events:
   * <ul>
   * <li> {@link GeneratedPackageEmulationEvent} after the {@link #setGeneratedPackageEmulationEnabled(boolean) generated
   * package emulation mode} has changed.
   * <li> {@link CollectionLoadingPolicyEvent} after the {@link #setCollectionLoadingPolicy(CDOCollectionLoadingPolicy)
   * collection loading policy} has changed.
   * <li> {@link LobCacheEvent} after the {@link #setLobCache(CDOLobStore) large object cache} has changed.
   * </ul>
   *
   * @author Simon McDuff
   * @noextend This interface is not intended to be extended by clients.
   * @noimplement This interface is not intended to be implemented by clients.
   */
  public interface Options extends CDOCommonSession.Options
  {
    /**
     * Returns the {@link CDOSession session} of this options object.
     *
     * @since 4.0
     */
    @Override
    public CDOSession getContainer();

    public boolean isGeneratedPackageEmulationEnabled();

    public void setGeneratedPackageEmulationEnabled(boolean generatedPackageEmulationEnabled);

    /**
     * The {@link CDOCollectionLoadingPolicy collection loading policy} of this {@link CDOSession session} controls how
     * a list gets populated. By default, when an object is fetched, all its elements are filled with the proper values.
     * <p>
     * This could be time-consuming, especially if the reference list does not need to be accessed. In CDO it is
     * possible to partially load collections. The default list implementation that is shipped with CDO makes a
     * distinction between the two following situations:
     * <ol>
     * <li>How many CDOIDs to fill when an object is loaded for the first time;
     * <li>Which elements to fill with CDOIDs when the accessed element is not yet filled.
     * </ol>
     * Example:
     * <p>
     * <code>CDOUtil.createCollectionLoadingPolicy(initialElements, subsequentElements);</code>
     * <p>
     * The user can also provide its own implementation of the CDOCollectionLoadingPolicy interface.
     */
    public CDOCollectionLoadingPolicy getCollectionLoadingPolicy();

    /**
     * Sets the {@link CDOCollectionLoadingPolicy collection loading} to be used by this session.
     */
    public void setCollectionLoadingPolicy(CDOCollectionLoadingPolicy policy);

    /**
     * Returns the {@link CDOLobStore large object cache} currently being used by this session.
     *
     * @since 4.0
     */
    public CDOLobStore getLobCache();

    /**
     * Sets the {@link CDOLobStore large object cache} to be used by this session.
     *
     * @since 4.0
     */
    public void setLobCache(CDOLobStore lobCache);

    /**
     * Returns the {@link CDOPermissionUpdater permission updater} currently being used by this session.
     *
     * @since 4.3
     */
    public CDOPermissionUpdater getPermissionUpdater();

    /**
     * Sets the {@link CDOPermissionUpdater permission updater} to be used by this session.
     *
     * @since 4.3
     */
    public void setPermissionUpdater(CDOPermissionUpdater permissionUpdater);

    /**
     * @since 4.5
     */
    public boolean isDelegableViewLockEnabled();

    /**
     * This method is useful, for example, if EMF {@link Adapter adapters} call <code>Display.syncExec()</code> in response to CDO notifications.
     * In these cases a {@link DelegableReentrantLock} can be injected into the new {@link CDOView view},
     * which does not deadlock when both CDO's invalidation thread and the display thread acquire the view lock.
     *
     * @see CDOUtil#setNextViewLock(Lock)
     * @since 4.5
     */
    public void setDelegableViewLockEnabled(boolean delegableViewLockEnabled);

    /**
     * @since 4.26
     */
    public int getPrefetchSendMaxRevisionKeys();

    /**
     * @since 4.26
     */
    public void setPrefetchSendMaxRevisionKeys(int prefetchSendMaxRevisionKeys);

    /**
     * An {@link IOptionsEvent options event} fired when the
     * {@link Options#setGeneratedPackageEmulationEnabled(boolean) generated package emulation enabled} option of a
     * {@link CDOSession session} has changed.
     *
     * @author Eike Stepper
     * @noextend This interface is not intended to be extended by clients.
     * @noimplement This interface is not intended to be implemented by clients.
     */
    public interface GeneratedPackageEmulationEvent extends IOptionsEvent
    {
    }

    /**
     * An {@link IOptionsEvent options event} fired when the
     * {@link Options#setCollectionLoadingPolicy(CDOCollectionLoadingPolicy) collection loading policy} option of a
     * {@link CDOSession session} has changed.
     *
     * @author Eike Stepper
     * @noextend This interface is not intended to be extended by clients.
     * @noimplement This interface is not intended to be implemented by clients.
     */
    public interface CollectionLoadingPolicyEvent extends IOptionsEvent
    {
    }

    /**
     * An {@link IOptionsEvent options event} fired when the {@link Options#setLobCache(CDOLobStore) large object cache}
     * option of a {@link CDOSession session} has changed.
     *
     * @author Eike Stepper
     * @since 4.0
     * @noextend This interface is not intended to be extended by clients.
     * @noimplement This interface is not intended to be implemented by clients.
     */
    public interface LobCacheEvent extends IOptionsEvent
    {
    }

    /**
     * An {@link IOptionsEvent options event} fired when the {@link Options#setPermissionUpdater(CDOPermissionUpdater) permission updater}
     * option of a {@link CDOSession session} has changed.
     *
     * @author Eike Stepper
     * @since 4.3
     * @noextend This interface is not intended to be extended by clients.
     * @noimplement This interface is not intended to be implemented by clients.
     */
    public interface PermissionUpdaterEvent extends IOptionsEvent
    {
    }

    /**
     * An {@link IOptionsEvent options event} fired when the {@link Options#setDelegableViewLockEnabled(boolean) delegable view lock enabled}
     * option of a {@link CDOSession session} has changed.
     *
     * @author Eike Stepper
     * @since 4.5
     * @noextend This interface is not intended to be extended by clients.
     * @noimplement This interface is not intended to be implemented by clients.
     */
    public interface DelegableViewLockEvent extends IOptionsEvent
    {
    }

    /**
     * An {@link IOptionsEvent options event} fired when the {@link Options#setPrefetchSendMaxRevisionKeys(int) prefetch send max revision keys}
     * option of a {@link CDOSession session} has changed.
     *
     * @author Eike Stepper
     * @since 4.26
     * @noextend This interface is not intended to be extended by clients.
     * @noimplement This interface is not intended to be implemented by clients.
     */
    public interface PrefetchSendMaxRevisionKeysEvent extends IOptionsEvent
    {
    }
  }

  /**
   * Handles {@link CDOSessionProtocol protocol} exceptions if
   * {@link CDOSessionConfiguration#setExceptionHandler(CDOSession.ExceptionHandler) configured} before the session has
   * been opened.
   *
   * @author Eike Stepper
   */
  public interface ExceptionHandler
  {
    public void handleException(CDOSession session, int attempt, Exception exception) throws Exception;
  }
}
