/***************************************************************************
 * Copyright (c) 2004 - 2007 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.emf.cdo.server.internal.db;

import org.eclipse.emf.cdo.internal.protocol.revision.CDORevisionImpl;
import org.eclipse.emf.cdo.protocol.CDOID;
import org.eclipse.emf.cdo.server.ITransaction;

import org.eclipse.net4j.internal.db.DBStoreTransaction;
import org.eclipse.net4j.util.store.IStoreManager;
import org.eclipse.net4j.util.store.IStoreTransaction;

import java.sql.Connection;

/**
 * @author Eike Stepper
 */
public class CDODBTransaction extends DBStoreTransaction implements ITransaction
{
  public CDODBTransaction(IStoreManager<? extends IStoreTransaction> storeManager, Connection connection)
  {
    super(storeManager, connection);
  }

  public void registerResource(CDOID id, String path)
  {
  }

  public CDOID getResourceID(String path)
  {
    return null;
  }

  public String getResourcePath(CDOID id)
  {
    return null;
  }

  public void addRevision(CDORevisionImpl revision)
  {
  }

  public CDORevisionImpl getRevision(CDOID id)
  {
    return null;
  }

  public CDORevisionImpl getRevision(CDOID id, long timeStamp)
  {
    return null;
  }
}
