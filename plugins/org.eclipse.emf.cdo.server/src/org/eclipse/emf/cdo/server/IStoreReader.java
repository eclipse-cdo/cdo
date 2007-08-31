/***************************************************************************
 * Copyright (c) 2004-2007 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.emf.cdo.server;

import org.eclipse.emf.cdo.internal.protocol.model.CDOPackageImpl;
import org.eclipse.emf.cdo.internal.protocol.revision.CDORevisionImpl;
import org.eclipse.emf.cdo.internal.server.StoreUtil;
import org.eclipse.emf.cdo.protocol.CDOID;
import org.eclipse.emf.cdo.protocol.model.CDOClassRef;
import org.eclipse.emf.cdo.protocol.model.CDOPackageInfo;
import org.eclipse.emf.cdo.protocol.revision.CDORevision;

import org.eclipse.net4j.util.io.CloseableIterator;

import java.util.Collection;

/**
 * @see StoreUtil#getReader()
 * @author Eike Stepper
 */
public interface IStoreReader extends IStoreAccessor
{
  public ISession getSession();

  public Collection<CDOPackageInfo> readPackageInfos();

  /**
   * Demand loads a given package proxy that has been created on startup of the
   * repository.
   */
  public void readPackage(CDOPackageImpl cdoPackage);

  /**
   * Returns an iterator that iterates over all objects in the store and makes
   * their ids available for processing. This method is supposed to be called
   * very infrequently, for example during the recovery from a crash.
   */
  public CloseableIterator<CDOID> readObjectIDs(boolean withTypes);

  /**
   * Reads the type of an object from the associated store and returns a class
   * reference of it. This method is supposed to be called very infrequently if
   * the {@link IStore#hasEfficientTypeLookup()} returns <code>false</code>.
   */
  public CDOClassRef readObjectType(CDOID id);

  public CDORevision readRevision(CDOID id);

  public CDORevision readRevision(CDOID id, long timeStamp);

  public CDOID readResourceID(String path);

  public String readResourcePath(CDOID id);

  public CDORevisionImpl verifyRevision(CDORevisionImpl revision);
}
