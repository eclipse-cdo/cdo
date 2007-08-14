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

import org.eclipse.emf.cdo.internal.protocol.model.CDOPackageImpl;
import org.eclipse.emf.cdo.protocol.CDOID;
import org.eclipse.emf.cdo.protocol.model.CDOClassRef;
import org.eclipse.emf.cdo.protocol.revision.CDORevision;
import org.eclipse.emf.cdo.server.IStoreReader;

import org.eclipse.net4j.db.DBException;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * @author Eike Stepper
 */
public class DBStoreReader implements IStoreReader
{
  protected DBStore store;

  protected Connection connection;

  public DBStoreReader(DBStore store) throws DBException
  {
    this.store = store;

    try
    {
      connection = store.getDataSource().getConnection();
    }
    catch (SQLException ex)
    {
      throw new DBException(ex);
    }
  }

  public void release() throws DBException
  {
    try
    {
      connection.close();
    }
    catch (SQLException ex)
    {
      throw new DBException(ex);
    }
  }

  public DBStore getStore()
  {
    return store;
  }

  public CDOPackageImpl[] readPackages()
  {
    // TODO Implement method DBStoreReader.readPackages()
    throw new UnsupportedOperationException("Not yet implemented");
  }

  public void readPackage(CDOPackageImpl cdoPackage)
  {
    // TODO Implement method DBStoreReader.readPackage()
    throw new UnsupportedOperationException("Not yet implemented");
  }

  public CDORevision readRevision(CDOID id, long timeStamp)
  {
    // TODO Implement method DBStoreReader.readRevision()
    throw new UnsupportedOperationException("Not yet implemented");
  }

  public CDOID readResourceID(String path)
  {
    // TODO Implement method DBStoreReader.readResourceID()
    throw new UnsupportedOperationException("Not yet implemented");
  }

  public String readResourcePath(CDOID id)
  {
    // TODO Implement method DBStoreReader.readResourcePath()
    throw new UnsupportedOperationException("Not yet implemented");
  }

  public CDORevision readRevision(CDOID id)
  {
    // TODO Implement method DBStoreReader.readRevision()
    throw new UnsupportedOperationException("Not yet implemented");
  }

  public CDOClassRef readObjectType(CDOID id)
  {
    // TODO Implement method DBStoreReader.readObjectType()
    throw new UnsupportedOperationException("Not yet implemented");
  }
}
