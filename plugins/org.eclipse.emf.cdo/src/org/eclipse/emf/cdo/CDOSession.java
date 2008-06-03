/***************************************************************************
 * Copyright (c) 2004 - 2008 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.emf.cdo;

import org.eclipse.emf.cdo.common.CDOProtocolSession;
import org.eclipse.emf.cdo.util.CDOPackageRegistry;

import org.eclipse.net4j.channel.IChannel;
import org.eclipse.net4j.connector.IConnector;
import org.eclipse.net4j.signal.failover.IFailOverStrategy;
import org.eclipse.net4j.util.container.IContainer;

import org.eclipse.emf.ecore.resource.ResourceSet;

/**
 * @author Eike Stepper
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

  public void close();
}
