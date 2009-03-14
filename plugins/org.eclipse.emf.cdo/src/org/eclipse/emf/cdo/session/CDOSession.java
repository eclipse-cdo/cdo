/**
 * Copyright (c) 2004 - 2009 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Simon McDuff - maintenance
 *    Victor Roldan Betancort - maintenance
 */
package org.eclipse.emf.cdo.session;

import org.eclipse.emf.cdo.common.CDOCommonSession;
import org.eclipse.emf.cdo.common.model.CDOPackageRegistry;
import org.eclipse.emf.cdo.common.model.CDOPackageUnit;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.CDORevisionFactory;
import org.eclipse.emf.cdo.session.remote.CDORemoteSessionManager;
import org.eclipse.emf.cdo.transaction.CDOTimeStampContext;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CDOEvent;
import org.eclipse.emf.cdo.view.CDOAudit;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.net4j.util.container.IContainer;
import org.eclipse.net4j.util.options.IOptionsContainer;
import org.eclipse.net4j.util.options.IOptionsEvent;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.ResourceSet;

import java.util.Collection;

/**
 * Represents and controls the connection to a model repository.
 * <p>
 * A session has the following responsibilities:
 * <ul>
 * <li> {@link CDOSession#repository() Repository information}
 * <li> {@link CDOSession#getPackageRegistry() Package registry}
 * <li> {@link CDOSession#getPackageUnitManager() Package unit management}
 * <li> {@link CDOSession#getRevisionManager() Data management}
 * <li> {@link CDOSession#getViews() View management}
 * </ul>
 * <p>
 * Note that, in order to retrieve, access and store {@link EObject objects} a {@link CDOView view} is needed. The
 * various <code>openXYZ</code> methods are provided for this purpose.
 * 
 * @author Eike Stepper
 * @noimplement This interface is not intended to be implemented by clients.
 * @since 2.0
 */
public interface CDOSession extends CDOCommonSession, IContainer<CDOView>, IOptionsContainer
{
  /**
   * Returns the EMF {@link EPackage.Registry package registry} that is used by all {@link EObject objects} of all
   * {@link CDOView views} of this session.
   * <p>
   * This registry is managed by the {@link CDOPackageUnit package unit manager} of this session. All {@link EPackage
   * packages} that are already persisted in the repository of this session are automatically registered with this
   * registry. New packages can be locally registered with this registry and are committed to the repository through a
   * {@link CDOTransaction transaction}, if needed.
   * 
   * @see #getPackageUnitManager()
   */
  public CDOPackageRegistry getPackageRegistry();

  /**
   * Returns the CDO {@link CDORevisionManager revision manager} that manages the {@link CDORevision revisions} of the
   * repository of this session.
   */
  public CDORevisionManager getRevisionManager();

  /**
   * Returns the CDO {@link CDORemoteSessionManager remote session manager} that keeps track of the other remote
   * sessions served by the repository of this local session.
   */
  public CDORemoteSessionManager getRemoteSessionManager();

  /**
   * Opens and returns a new {@link CDOTransaction transaction} on the given EMF {@link ResourceSet resource set}.
   * 
   * @see #openTransaction()
   */
  public CDOTransaction openTransaction(ResourceSet resourceSet);

  /**
   * Opens and returns a new {@link CDOTransaction transaction} on a new EMF {@link ResourceSet resource set}.
   * <p>
   * Same as calling <code>openTransaction(new ResourceSetImpl())</code>.
   * 
   * @see #openTransaction(ResourceSet)
   */
  public CDOTransaction openTransaction();

  /**
   * Opens and returns a new {@link CDOView view} on the given EMF {@link ResourceSet resource set}.
   * 
   * @see #openView()
   */
  public CDOView openView(ResourceSet resourceSet);

  /**
   * Opens and returns a new {@link CDOView view} on a new EMF {@link ResourceSet resource set}.
   * <p>
   * Same as calling <code>openView(new ResourceSetImpl())</code>.
   * 
   * @see #openView(ResourceSet)
   */
  public CDOView openView();

  /**
   * Opens and returns a new {@link CDOAudit audit} on the given EMF {@link ResourceSet resource set}.
   * 
   * @see #openAudit()
   */
  public CDOAudit openAudit(ResourceSet resourceSet, long timeStamp);

  /**
   * Opens and returns a new {@link CDOAudit audit} on a new EMF {@link ResourceSet resource set}.
   * <p>
   * Same as calling <code>openAudit(new ResourceSetImpl(), timeStamp)</code>.
   * 
   * @see #openAudit(ResourceSet, long)
   */
  public CDOAudit openAudit(long timeStamp);

  /**
   * Returns an array of all open {@link CDOView views}, {@link CDOTransaction transactions} and {@link CDOAudit audits}
   * of this session.
   * 
   * @see #openView()
   * @see #openTransaction()
   * @see #openAudit(long)
   */
  public CDOView[] getViews();

  /**
   * Refreshes the objects cache.
   * <p>
   * Takes CDOID and version of all objects in the cache and sends it to the server. {@link CDOTimeStampContext}
   * contains informations of which objects changed/detached. The collection is ordered by timestamp. In the case where
   * {@link #isPassiveUpdateEnabled()} is <code>true</code>, this method will return immediately without doing anything.
   */
  public Collection<CDOTimeStampContext> refresh();

  /**
   * Returns the {@link Options options} of this session.
   */
  public Options options();

  /**
   * Returns an instance of {@link Repository} that describes the model repository this {@link CDOSession session} is
   * connected to.
   */
  public Repository repository();

  /**
   * @author Simon McDuff
   */
  public interface Options extends CDOCommonSession.Options
  {

    public boolean isGeneratedPackageEmulationEnabled();

    public void setGeneratedPackageEmulationEnabled(boolean generatedPackageEmulationEnabled);

    /**
     * Specifies whether objects will be invalidated due by other users changes.
     * <p>
     * Example:
     * <p>
     * <code>session.setPassiveUpdateEnabled(false);</code>
     * <p>
     * By default this property is enabled. If this property is disabled the latest versions of objects can still be
     * obtained by calling {@link #refresh()}.
     * <p>
     * Passive update can be disabled in cases where more performance is needed and/or more control over when objects
     * will be refreshed.
     * <p>
     * When enabled again, a refresh will be automatically performed to be in sync with the server.
     */
    public void setPassiveUpdateEnabled(boolean enabled);

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
     * <code>CDONet4jUtil.createCollectionLoadingPolicy(initialElements, subsequentElements);</code>
     * <p>
     * The user can also provide its own implementation of the CDOCollectionLoadingPolicy interface.
     */
    public CDOCollectionLoadingPolicy getCollectionLoadingPolicy();

    /**
     * Returns the CDOCollectionLoadingPolicy currently being used by this session.
     */
    public void setCollectionLoadingPolicy(CDOCollectionLoadingPolicy policy);

    /**
     * Returns the CDORevisionFactory currently being used by this session.
     */
    public CDORevisionFactory getRevisionFactory();

    /**
     * Sets the CDORevisionFactory to be used by this session.
     */
    public void setRevisionFactory(CDORevisionFactory factory);

    /**
     * @author Eike Stepper
     */
    public interface GeneratedPackageEmulationEvent extends IOptionsEvent, CDOEvent
    {
    }

    /**
     * @author Eike Stepper
     */
    public interface PassiveUpdateEvent extends IOptionsEvent, CDOEvent
    {
    }

    /**
     * @author Eike Stepper
     */
    public interface CollectionLoadingPolicyEvent extends IOptionsEvent, CDOEvent
    {
    }

    /**
     * @author Eike Stepper
     */
    public interface RevisionFactoryEvent extends IOptionsEvent, CDOEvent
    {
    }
  }

  /**
   * Describes a model repository a {@link CDOSession session} is connected to.
   * 
   * @author Eike Stepper
   * @see CDOSession#repository()
   */
  public interface Repository
  {
    /**
     * Returns the name of this repository.
     * 
     * @see IRepository#getName()
     */
    public String getName();

    /**
     * Returns the UUID of this repository.
     * 
     * @see IRepository#getUUID()
     */
    public String getUUID();

    /**
     * Returns the creation time of this repository.
     * 
     * @see IRepository#getCreationTime()
     */
    public long getCreationTime();

    /**
     * Returns the approximate current time of this repository.
     * <p>
     * Same as calling <code>getCurrentTime(false)</code>.
     * 
     * @see #getCurrentTime(boolean)
     */
    public long getCurrentTime();

    /**
     * Returns the approximate current time of this repository by optionally refreshing the approximation from the
     * server.
     */
    public long getCurrentTime(boolean forceRefresh);

    /**
     * Returns <code>true</code> if this repository supports auditing, <code>false</code> otherwise.
     * 
     * @see IRepository#isSupportingAudits()
     */
    public boolean isSupportingAudits();
  }
}
