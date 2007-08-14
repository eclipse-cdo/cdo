/***************************************************************************
 * Copyright (c) 2004, 2005, 2006 Eike Stepper, Germany.
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
import org.eclipse.emf.cdo.internal.protocol.model.CDOPackageManagerImpl;
import org.eclipse.emf.cdo.internal.server.StoreUtil;
import org.eclipse.emf.cdo.protocol.CDOID;
import org.eclipse.emf.cdo.protocol.CDOIDRange;
import org.eclipse.emf.cdo.protocol.model.CDOClassRef;
import org.eclipse.emf.cdo.protocol.revision.CDORevision;

/**
 * @see StoreUtil#getReader()
 * @author Eike Stepper
 */
public interface IStoreReader extends IStoreAccessor
{
  /**
   * Creates and returns package instances for all packages stored in the
   * repository. This method is called on startup of a repository.
   * <p>
   * <b>Note:</b> The implementor is free to create and return package proxies
   * that are demand loaded at a later point in time.
   * <p>
   * 
   * @see CDOPackageImpl#CDOPackageImpl(CDOPackageManagerImpl, String, boolean,
   *      CDOIDRange)
   */
  public CDOPackageImpl[] readPackages();

  /**
   * Demand loads a given package proxy that has been created on startup of the
   * repository.
   * <p>
   */
  public void readPackage(CDOPackageImpl cdoPackage);

  public CDOID readResourceID(String path);

  public String readResourcePath(CDOID id);

  public CDORevision readRevision(CDOID id);

  public CDORevision readRevision(CDOID id, long timeStamp);

  public CDOClassRef readObjectType(CDOID id);
}
