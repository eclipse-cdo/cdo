/***************************************************************************
 * Copyright (c) 2004 - 2008 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Simon McDuff - maintenance
 **************************************************************************/
package org.eclipse.emf.cdo;

import org.eclipse.emf.cdo.common.CDOProtocolSession;
import org.eclipse.emf.cdo.util.CDOPackageRegistry;

import org.eclipse.net4j.util.container.IContainer;

import org.eclipse.emf.ecore.resource.ResourceSet;

import java.util.Collection;

/**
 * @author Eike Stepper
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface CDOSession extends CDOProtocolSession, IContainer<CDOView>
{
  /**
   * @since 2.0
   */
  public CDOSessionProtocol getProtocol();

  public boolean isOpen();

  public String getRepositoryName();

  public String getRepositoryUUID();

  /**
   * @since 2.0
   */
  public long getRepositoryCreationTime();

  /**
   * @since 2.0
   */
  public long getRepositoryTime();

  /**
   * @since 2.0
   */
  public long getRepositoryTime(boolean forceRefresh);

  public CDOPackageRegistry getPackageRegistry();

  public CDOSessionPackageManager getPackageManager();

  public CDORevisionManager getRevisionManager();

  /**
   * @since 2.0
   */
  public CDOCollectionLoadingPolicy getCollectionLoadingPolicy();

  /**
   * @since 2.0
   */
  public void setCollectionLoadingPolicy(CDOCollectionLoadingPolicy policy);

  /**
   * Specifies whether objects will be invalidated due by other users changes.
   * <p>
   * E.g.: <code>session.setPassiveUpdateEnabled(false);</code>
   * <p>
   * By default this property is enabled. If this property is disabled the latest versions of objects can still be
   * obtained by calling {@link #refresh()}.
   * <p>
   * Passive update can be disabled in cases where more performance is needed and/or more control over when objects will
   * be refreshed.
   * <p>
   * When enabled again, a refresh will be automatically performed to be in sync with the server.
   * 
   * @since 2.0
   */
  public void setPassiveUpdateEnabled(boolean enabled);

  public CDOView[] getViews();

  public CDOTransaction openTransaction(ResourceSet resourceSet);

  public CDOTransaction openTransaction();

  public CDOView openView(ResourceSet resourceSet);

  public CDOView openView();

  public CDOAudit openAudit(ResourceSet resourceSet, long timeStamp);

  public CDOAudit openAudit(long timeStamp);

  /**
   * Refreshes the objects cache.
   * <p>
   * Takes CDOID and version of all objects in the cache and sends it to the server. {@link CDOTimeStampContext}
   * contains informations of which objects changed/detached. The collection is ordered by timestamp. In the case where
   * {@link #isPassiveUpdateEnabled()} is <code>true</code>, this method will return immediately without doing anything.
   * 
   * @since 2.0
   */
  public Collection<CDOTimeStampContext> refresh();
}
