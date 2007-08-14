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
import org.eclipse.emf.cdo.internal.protocol.revision.CDORevisionImpl;
import org.eclipse.emf.cdo.server.IStoreWriter;
import org.eclipse.emf.cdo.server.IView;

import org.eclipse.net4j.db.DBException;

import java.sql.SQLException;

/**
 * @author Eike Stepper
 */
public class DBStoreWriter extends DBStoreReader implements IStoreWriter
{
  private IView view;

  public DBStoreWriter(DBStore store, IView view) throws DBException
  {
    super(store);
    this.view = view;

    try
    {
      connection.setAutoCommit(false);
    }
    catch (SQLException ex)
    {
      throw new DBException(ex);
    }
  }

  @Override
  public void release() throws DBException
  {
    try
    {
      connection.commit();
    }
    catch (SQLException ex)
    {
      throw new DBException(ex);
    }

    super.release();
  }

  public IView getView()
  {
    return view;
  }

  public void writePackage(CDOPackageImpl cdoPackage)
  {
    // TODO Implement method DBStoreWriter.writePackage()
    // throw new UnsupportedOperationException("Not yet implemented");
  }

  public void writeRevision(CDORevisionImpl revision)
  {
    // TODO Implement method DBStoreWriter.writeRevision()
    // throw new UnsupportedOperationException("Not yet implemented");
  }
}
