/*
 * Copyright (c) 2010-2014, 2016, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Stefan Winkler - bug 259402
 *    Stefan Winkler - redesign (prepared statements)
 *    Stefan Winkler - bug 276926
 */
package org.eclipse.emf.cdo.server.internal.db.mapping.horizontal;

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.model.CDOClassifierRef;
import org.eclipse.emf.cdo.common.protocol.CDODataInput;
import org.eclipse.emf.cdo.common.protocol.CDODataOutput;
import org.eclipse.emf.cdo.server.db.IDBStore;
import org.eclipse.emf.cdo.server.db.IDBStoreAccessor;
import org.eclipse.emf.cdo.server.db.IIDHandler;
import org.eclipse.emf.cdo.server.internal.db.DBStoreTable;
import org.eclipse.emf.cdo.server.internal.db.IObjectTypeMapper;
import org.eclipse.emf.cdo.spi.server.InternalRepository;

import org.eclipse.net4j.db.DBException;
import org.eclipse.net4j.db.DBType;
import org.eclipse.net4j.db.DBUtil;
import org.eclipse.net4j.db.IDBPreparedStatement;
import org.eclipse.net4j.db.IDBPreparedStatement.ReuseProbability;
import org.eclipse.net4j.db.ddl.IDBField;
import org.eclipse.net4j.db.ddl.IDBIndex;
import org.eclipse.net4j.db.ddl.IDBTable;
import org.eclipse.net4j.util.om.monitor.OMMonitor;

import org.eclipse.emf.ecore.EClass;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * @author Eike Stepper
 * @since 4.0
 */
public class ObjectTypeTable extends DBStoreTable implements IObjectTypeMapper
{
  private IDBField id;

  private IDBField clazz;

  private IDBField created;

  private String sqlDelete;

  private String sqlInsert;

  private String sqlSelect;

  public ObjectTypeTable(IDBStore store)
  {
    super(store, MappingNames.CDO_OBJECTS);
  }

  public final IDBField id()
  {
    return id;
  }

  public final IDBField clazz()
  {
    return clazz;
  }

  @Override
  public final CDOClassifierRef getObjectType(IDBStoreAccessor accessor, CDOID id)
  {
    IIDHandler idHandler = store().getIDHandler();
    IDBPreparedStatement stmt = accessor.getDBConnection().prepareStatement(sqlSelect, ReuseProbability.MAX);

    try
    {
      idHandler.setCDOID(stmt, 1, id);

      if (DBUtil.isTracerEnabled())
      {
        DBUtil.trace(stmt.toString());
      }

      ResultSet resultSet = stmt.executeQuery();

      if (!resultSet.next())
      {
        if (DBUtil.isTracerEnabled())
        {
          DBUtil.trace("ClassID for CDOID " + id + " not found"); //$NON-NLS-1$ //$NON-NLS-2$
        }

        return null;
      }

      CDOID classID = idHandler.getCDOID(resultSet, 1);
      EClass eClass = (EClass)store().getMetaDataManager().getMetaInstance(classID);
      return new CDOClassifierRef(eClass);
    }
    catch (SQLException ex)
    {
      throw new DBException(ex);
    }
    finally
    {
      DBUtil.close(stmt);
    }
  }

  @Override
  public final boolean putObjectType(IDBStoreAccessor accessor, long timeStamp, CDOID id, EClass type)
  {
    IIDHandler idHandler = store().getIDHandler();
    IDBPreparedStatement stmt = accessor.getDBConnection().prepareStatement(sqlInsert, ReuseProbability.MAX);

    try
    {
      idHandler.setCDOID(stmt, 1, id);
      idHandler.setCDOID(stmt, 2, store().getMetaDataManager().getMetaID(type, timeStamp));
      stmt.setLong(3, timeStamp);

      if (DBUtil.isTracerEnabled())
      {
        DBUtil.trace(stmt.toString());
      }

      int result = stmt.executeUpdate();
      if (result != 1)
      {
        throw new DBException("Object type could not be inserted: " + id); //$NON-NLS-1$
      }

      return true;
    }
    catch (SQLException ex)
    {
      if (store().getDBAdapter().isDuplicateKeyException(ex))
      {
        // Unique key violation can occur in rare cases (merging new objects from other branches)
        return false;
      }

      throw new DBException(ex);
    }
    finally
    {
      DBUtil.close(stmt);
    }
  }

  @Override
  public final boolean removeObjectType(IDBStoreAccessor accessor, CDOID id)
  {
    IIDHandler idHandler = store().getIDHandler();
    IDBPreparedStatement stmt = accessor.getDBConnection().prepareStatement(sqlDelete, ReuseProbability.MAX);

    try
    {
      idHandler.setCDOID(stmt, 1, id);

      if (DBUtil.isTracerEnabled())
      {
        DBUtil.trace(stmt.toString());
      }

      int result = stmt.executeUpdate();
      return result == 1;
    }
    catch (SQLException ex)
    {
      throw new DBException(ex);
    }
    finally
    {
      DBUtil.close(stmt);
    }
  }

  @Override
  public CDOID getMaxID(Connection connection, IIDHandler idHandler)
  {
    Statement stmt = null;
    ResultSet resultSet = null;

    try
    {
      stmt = connection.createStatement();
      resultSet = stmt.executeQuery("SELECT MAX(" + id + ") FROM " + table());

      if (resultSet.next())
      {
        return idHandler.getCDOID(resultSet, 1);
      }

      return null;
    }
    catch (SQLException ex)
    {
      throw new DBException(ex);
    }
    finally
    {
      DBUtil.close(resultSet);
      DBUtil.close(stmt);
    }
  }

  @Override
  public void rawExport(Connection connection, CDODataOutput out, long fromCommitTime, long toCommitTime) throws IOException
  {
    String where = " WHERE " + created + " BETWEEN " + fromCommitTime + " AND " + toCommitTime;
    DBUtil.serializeTable(out, connection, table(), null, where);
  }

  @Override
  public void rawImport(Connection connection, CDODataInput in, OMMonitor monitor) throws IOException
  {
    DBUtil.deserializeTable(in, connection, table(), monitor);
  }

  @Override
  protected void firstActivate(IDBTable table)
  {
    DBType idType = store().getIDHandler().getDBType();
    int idLength = store().getIDColumnLength();

    id = table.addField(MappingNames.ATTRIBUTES_ID, idType, idLength, true);
    clazz = table.addField(MappingNames.ATTRIBUTES_CLASS, idType, idLength);
    created = table.addField(MappingNames.ATTRIBUTES_CREATED, DBType.BIGINT);

    table.addIndex(IDBIndex.Type.PRIMARY_KEY, id);

    InternalRepository repository = (InternalRepository)store().getRepository();
    if (repository.isSupportingUnits())
    {
      table.addIndex(IDBIndex.Type.NON_UNIQUE, clazz);
    }
  }

  @Override
  protected void reActivate(IDBTable table)
  {
    id = table.getField(MappingNames.ATTRIBUTES_ID);
    clazz = table.getField(MappingNames.ATTRIBUTES_CLASS);
    created = table.getField(MappingNames.ATTRIBUTES_CREATED);
  }

  @Override
  protected void initSQL(IDBTable table)
  {
    sqlSelect = "SELECT " + clazz + " FROM " + table + " WHERE " + id + "=?";
    sqlInsert = "INSERT INTO " + table + "(" + id + "," + clazz + "," + created + ") VALUES (?, ?, ?)";
    sqlDelete = "DELETE FROM " + table + " WHERE " + id + "=?";
  }
}
