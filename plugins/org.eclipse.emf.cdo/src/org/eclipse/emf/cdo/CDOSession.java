/***************************************************************************
 * Copyright (c) 2004 - 2008 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Simon McDuff - https://bugs.eclipse.org/230832
 **************************************************************************/
package org.eclipse.emf.cdo;

import org.eclipse.emf.cdo.common.CDOProtocolSession;
import org.eclipse.emf.cdo.common.id.CDOIDAndVersion;
import org.eclipse.emf.cdo.util.CDOPackageRegistry;

import org.eclipse.net4j.channel.IChannel;
import org.eclipse.net4j.connector.IConnector;
import org.eclipse.net4j.signal.failover.IFailOverStrategy;
import org.eclipse.net4j.util.container.IContainer;

import org.eclipse.emf.ecore.resource.ResourceSet;

import java.util.Set;

/**
 * @author Eike Stepper
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface CDOSession extends CDOProtocolSession, IContainer<CDOView>
{
  public int getReferenceChunkSize();

  public void setReferenceChunkSize(int referenceChunkSize);

  public IFailOverStrategy getFailOverStrategy();

  public IChannel getChannel();

  public IConnector getConnector();

  public boolean isOpen();

  public String getRepositoryName();

  public String getRepositoryUUID();

  public CDOPackageRegistry getPackageRegistry();

  public CDOSessionPackageManager getPackageManager();

  public CDORevisionManager getRevisionManager();

  public CDOView[] getViews();

  public CDOTransaction openTransaction(ResourceSet resourceSet);

  public CDOTransaction openTransaction();

  public CDOView openView(ResourceSet resourceSet);

  public CDOView openView();

  public CDOAudit openAudit(ResourceSet resourceSet, long timeStamp);

  public CDOAudit openAudit(long timeStamp);

  /**
   * Specifies whether object will be invalidate from others users changes.
   * <p>
   * By default this value is enabled.
   * <p>
   * If you disabled this property, you can still have the latest version of objects by calling {@link #refresh()}.
   * <p>
   * You would disabled it in the case where you need performance and/or want to control when objects will be refresh.
   * <p>
   * When we enable it, it will automatically perform a refresh to be in sync with the server.
   * 
   * @since 2.0
   */
  public void setPassiveUpdateEnabled(boolean enable);

  /**
   * Refresh objects cache.
   * <p>
   * Take CDOID and version of all objects in the cache, sent it to the server. It will return only dirty objects.
   * <p>
   * In the case where <code>isPassiveUpdateEnabled<code> is <code>true</code>, it will return immediately without doing
   * anything.
   * 
   * @since 2.0
   */
  public Set<CDOIDAndVersion> refresh();

  public void close();
}
