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

import org.eclipse.emf.cdo.internal.protocol.CDOIDImpl;
import org.eclipse.emf.cdo.protocol.CDOID;
import org.eclipse.emf.cdo.protocol.model.CDOClassRef;
import org.eclipse.emf.cdo.server.db.IDBStoreAccessor;

import org.eclipse.net4j.db.DBException;
import org.eclipse.net4j.db.DBUtil;
import org.eclipse.net4j.util.io.CloseableIterator;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author Eike Stepper
 */
public abstract class ObjectIDIterator implements CloseableIterator<CDOID>
{
  private MappingStrategy mappingStrategy;

  private IDBStoreAccessor storeAccessor;

  private boolean withTypes;

  private ResultSet currentResultSet;

  private CDOID nextID;

  /**
   * Creates an iterator over all objects in a store. It is important to {@link #dispose()} of this iterator after usage
   * to properly close internal result sets.
   */
  public ObjectIDIterator(MappingStrategy mappingStrategy, IDBStoreAccessor storeAccessor, boolean withTypes)
  {
    this.mappingStrategy = mappingStrategy;
    this.storeAccessor = storeAccessor;
    this.withTypes = withTypes;
  }

  public void close() throws IOException
  {
    DBUtil.close(currentResultSet);
  }

  public MappingStrategy getMappingStrategy()
  {
    return mappingStrategy;
  }

  public IDBStoreAccessor getStoreAccessor()
  {
    return storeAccessor;
  }

  public boolean isWithTypes()
  {
    return withTypes;
  }

  public boolean hasNext()
  {
    nextID = null;
    for (;;)
    {
      if (currentResultSet == null)
      {
        currentResultSet = getNextResultSet();
        if (currentResultSet == null)
        {
          return false;
        }
      }

      try
      {
        if (currentResultSet.next())
        {
          long id = currentResultSet.getLong(1);
          if (withTypes)
          {
            int classID = currentResultSet.getInt(2);
            CDOClassRef type = mappingStrategy.getClassRef(storeAccessor, classID);
            nextID = CDOIDImpl.create(id, type);
          }
          else
          {
            nextID = CDOIDImpl.create(id);
          }
          return true;
        }

        DBUtil.close(currentResultSet);
        currentResultSet = null;
      }
      catch (SQLException ex)
      {
        throw new DBException(ex);
      }
    }
  }

  public CDOID next()
  {
    return nextID;
  }

  public void remove()
  {
    throw new UnsupportedOperationException();
  }

  protected abstract ResultSet getNextResultSet();
}
