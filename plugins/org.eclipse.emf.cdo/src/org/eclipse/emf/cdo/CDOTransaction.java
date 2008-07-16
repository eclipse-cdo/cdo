/***************************************************************************
 * Copyright (c) 2004 - 2008 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Simon McDuff - http://bugs.eclipse.org/201266
 **************************************************************************/
package org.eclipse.emf.cdo;

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.model.CDOClass;
import org.eclipse.emf.cdo.common.model.CDOPackage;
import org.eclipse.emf.cdo.common.revision.delta.CDORevisionDelta;
import org.eclipse.emf.cdo.eresource.CDOResource;

import org.eclipse.net4j.util.transaction.TransactionException;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.resource.ResourceSet;

import java.util.List;
import java.util.Map;

/**
 * @author Eike Stepper
 */
public interface CDOTransaction extends CDOView
{
  public static final long DEFAULT_COMMIT_TIMEOUT = 100000L;

  public long getCommitTimeout();

  public void setCommitTimeout(long timeout);

  /**
   * @see ResourceSet#createResource(URI)
   */
  public CDOResource createResource(String path);

  public CDOResource getOrCreateResource(String path);

  public CDOObject newInstance(EClass eClass);

  public CDOObject newInstance(CDOClass cdoClass);

  public void commit() throws TransactionException;

  public void rollback(boolean remote);

  public void addHandler(CDOTransactionHandler handler);

  public void removeHandler(CDOTransactionHandler handler);

  public CDOTransactionHandler[] getHandlers();

  public List<CDOPackage> getNewPackages();

  public Map<CDOID, CDOResource> getNewResources();

  public Map<CDOID, CDOObject> getNewObjects();

  public Map<CDOID, CDOObject> getDirtyObjects();

  public Map<CDOID, CDORevisionDelta> getRevisionDeltas();
}
