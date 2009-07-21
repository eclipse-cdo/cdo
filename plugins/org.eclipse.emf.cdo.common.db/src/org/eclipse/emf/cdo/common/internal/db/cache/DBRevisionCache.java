/***************************************************************************
 * Copyright (c) 2004 - 2009 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Andre Dietisheim - initial API and implementation
 *    Eike Stepper - maintenance
 **************************************************************************/
package org.eclipse.emf.cdo.common.internal.db.cache;

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDProvider;
import org.eclipse.emf.cdo.common.internal.db.AbstractQueryStatement;
import org.eclipse.emf.cdo.common.internal.db.AbstractUpdateStatement;
import org.eclipse.emf.cdo.common.internal.db.DBRevisionCacheUtil;
import org.eclipse.emf.cdo.common.io.CDODataInput;
import org.eclipse.emf.cdo.common.io.CDODataOutput;
import org.eclipse.emf.cdo.common.model.CDOPackageRegistry;
import org.eclipse.emf.cdo.common.revision.CDOListFactory;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.CDORevisionFactory;
import org.eclipse.emf.cdo.common.revision.cache.CDORevisionCache;
import org.eclipse.emf.cdo.internal.common.io.CDODataInputImpl;
import org.eclipse.emf.cdo.internal.common.io.CDODataOutputImpl;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;

import org.eclipse.net4j.db.DBException;
import org.eclipse.net4j.db.DBUtil;
import org.eclipse.net4j.db.IDBAdapter;
import org.eclipse.net4j.db.IDBConnectionProvider;
import org.eclipse.net4j.util.CheckUtil;
import org.eclipse.net4j.util.io.ExtendedDataInputStream;
import org.eclipse.net4j.util.io.ExtendedDataOutput;
import org.eclipse.net4j.util.io.ExtendedDataOutputStream;
import org.eclipse.net4j.util.lifecycle.Lifecycle;

import org.eclipse.emf.ecore.EClass;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

/**
 * A JDBC-based {@link CDORevisionCache}.
 * 
 * @author Andre Dietisheim
 */
public class DBRevisionCache extends Lifecycle implements CDORevisionCache
{
  private CDOIDProvider idProvider;

  private CDOListFactory listFactory;

  private CDOPackageRegistry packageRegistry;

  private CDORevisionFactory revisionFactory;

  private IDBAdapter dbAdapter;

  private IDBConnectionProvider dbConnectionProvider;

  public DBRevisionCache()
  {
  }

  public CDOIDProvider getIdProvider()
  {
    return idProvider;
  }

  public void setIdProvider(CDOIDProvider idProvider)
  {
    this.idProvider = idProvider;
  }

  public CDOListFactory getListFactory()
  {
    return listFactory;
  }

  public void setListFactory(CDOListFactory listFactory)
  {
    this.listFactory = listFactory;
  }

  public CDOPackageRegistry getPackageRegistry()
  {
    return packageRegistry;
  }

  public void setPackageRegistry(CDOPackageRegistry packageRegistry)
  {
    this.packageRegistry = packageRegistry;
  }

  public CDORevisionFactory getRevisionFactory()
  {
    return revisionFactory;
  }

  public void setRevisionFactory(CDORevisionFactory revisionFactory)
  {
    this.revisionFactory = revisionFactory;
  }

  public IDBAdapter getDBAdapter()
  {
    return dbAdapter;
  }

  public void setDBAdapter(IDBAdapter dbAdapter)
  {
    this.dbAdapter = dbAdapter;
  }

  public IDBConnectionProvider getDBConnectionProvider()
  {
    return dbConnectionProvider;
  }

  public void setDBConnectionProvider(IDBConnectionProvider dbConnectionProvider)
  {
    this.dbConnectionProvider = dbConnectionProvider;
  }

  public EClass getObjectType(CDOID id)
  {
    return null;
  }

  /**
   * Gets the {@link CDOID} of a resource within this cache. The resource is picked if it matches the given folderID,
   * name and timestamp
   * 
   * @param folderID
   *          the folder id
   * @param name
   *          the name
   * @param timeStamp
   *          the time stamp
   * @return the resource id
   */
  public CDOID getResourceID(final CDOID folderID, final String name, final long timeStamp)
  {
    Connection connection = null;
    try
    {
      connection = getConnection();
      AbstractQueryStatement<CDOID> query = new AbstractQueryStatement<CDOID>()
      {
        @Override
        protected String getSQL()
        {
          StringBuilder builder = new StringBuilder();
          builder.append("SELECT "); //$NON-NLS-1$
          builder.append(DBRevisionCacheSchema.REVISIONS_CDOREVISION);
          builder.append(", "); //$NON-NLS-1$
          builder.append(DBRevisionCacheSchema.REVISIONS_REVISED);
          builder.append(" FROM "); //$NON-NLS-1$
          builder.append(DBRevisionCacheSchema.REVISIONS);
          builder.append(" WHERE "); //$NON-NLS-1$
          builder.append(DBRevisionCacheSchema.REVISIONS_ID);
          builder.append("=? AND "); //$NON-NLS-1$
          appendTimestampCondition(builder);
          builder.append(" AND "); //$NON-NLS-1$
          builder.append(DBRevisionCacheSchema.REVISIONS_RESOURCENODE_NAME);
          builder.append("=?"); //$NON-NLS-1$
          return builder.toString();
        }

        @Override
        protected void setParameters(PreparedStatement preparedStatement) throws SQLException
        {
          preparedStatement.setString(1, folderID.toURIFragment());
          preparedStatement.setLong(2, timeStamp);
          preparedStatement.setLong(3, timeStamp);
          preparedStatement.setString(4, name);
        }

        @Override
        protected CDOID getResult(ResultSet resultSet) throws Exception
        {
          long revised = resultSet.getLong(2);
          Blob blob = resultSet.getBlob(1);
          InternalCDORevision revision = toRevision(blob, revised);
          if (revision == null)
          {
            return null;
          }

          return revision.getID();
        }
      };

      return query.query(connection);
    }
    catch (Exception e)
    {
      throw new DBException("Error while retrieving the resource ID from the database", e); //$NON-NLS-1$
    }
    finally
    {
      DBUtil.close(connection);
    }
  }

  /**
   * Gets the revision with the highest version for a given {@link CDOID}.
   * 
   * @param id
   *          the id to match
   * @return the revision that was found
   */
  public InternalCDORevision getRevision(final CDOID id)
  {
    Connection connection = null;

    try
    {
      connection = getConnection();
      AbstractQueryStatement<InternalCDORevision> query = createGetRevisionByIDStatement(id);
      return query.query(connection);
    }
    catch (Exception e)
    {
      throw new DBException("Error while retrieving the revision from the database", e); //$NON-NLS-1$
    }
    finally
    {
      DBUtil.close(connection);
    }
  }

  private AbstractQueryStatement<InternalCDORevision> createGetRevisionByIDStatement(final CDOID id)
  {
    return new AbstractQueryStatement<InternalCDORevision>()
    {
      @Override
      protected String getSQL()
      {
        StringBuilder builder = new StringBuilder();
        builder.append("SELECT "); //$NON-NLS-1$
        builder.append(DBRevisionCacheSchema.REVISIONS_CDOREVISION);
        builder.append(", "); //$NON-NLS-1$
        builder.append(DBRevisionCacheSchema.REVISIONS_REVISED);
        builder.append(" FROM "); //$NON-NLS-1$
        builder.append(DBRevisionCacheSchema.REVISIONS);
        builder.append(" WHERE "); //$NON-NLS-1$
        builder.append(DBRevisionCacheSchema.REVISIONS_ID);
        builder.append("=? AND "); //$NON-NLS-1$
        builder.append(DBRevisionCacheSchema.REVISIONS_REVISED);
        builder.append("="); //$NON-NLS-1$
        builder.append(CDORevision.UNSPECIFIED_DATE);
        return builder.toString();
      }

      @Override
      protected void setParameters(PreparedStatement preparedStatement) throws SQLException
      {
        preparedStatement.setString(1, id.toURIFragment());
      }

      @Override
      protected InternalCDORevision getResult(ResultSet resultSet) throws Exception
      {
        long revised = resultSet.getLong(2);
        Blob blob = resultSet.getBlob(1);
        return toRevision(blob, revised);
      }
    };
  }

  /**
   * Gets an {@link InternalCDORevision} that matches the given timestamp (it is >= created timestamp AND <= revised
   * timestamp of the revision).
   * 
   * @param id
   *          the id
   * @param timeStamp
   *          the time stamp
   * @return the revision by time
   */
  public InternalCDORevision getRevisionByTime(final CDOID id, final long timeStamp)
  {
    Connection connection = null;

    try
    {
      connection = getConnection();
      AbstractQueryStatement<InternalCDORevision> statement = new AbstractQueryStatement<InternalCDORevision>()
      {
        @Override
        protected String getSQL()
        {
          StringBuilder builder = new StringBuilder();
          builder.append("SELECT "); //$NON-NLS-1$
          builder.append(DBRevisionCacheSchema.REVISIONS_CDOREVISION);
          builder.append(", "); //$NON-NLS-1$
          builder.append(DBRevisionCacheSchema.REVISIONS_REVISED);
          builder.append(" FROM "); //$NON-NLS-1$
          builder.append(DBRevisionCacheSchema.REVISIONS);
          builder.append(" WHERE "); //$NON-NLS-1$
          builder.append(DBRevisionCacheSchema.REVISIONS_ID);
          builder.append("=? AND "); //$NON-NLS-1$
          appendTimestampCondition(builder);
          return builder.toString();
        }

        @Override
        protected void setParameters(PreparedStatement preparedStatement) throws SQLException
        {
          preparedStatement.setString(1, id.toURIFragment());
          preparedStatement.setLong(2, timeStamp);
          preparedStatement.setLong(3, timeStamp);
        }

        @Override
        protected InternalCDORevision getResult(ResultSet resultSet) throws Exception
        {
          long revised = resultSet.getLong(2);
          Blob blob = resultSet.getBlob(1);
          return toRevision(blob, revised);
        }
      };

      return statement.query(connection);
    }
    catch (Exception e)
    {
      throw new DBException("Error while retrieving a revision by timestamp from the database", e); //$NON-NLS-1$
    }
    finally
    {
      DBUtil.close(connection);
    }
  }

  /**
   * Gets a {@link InternalCDORevision} by a given id and version.
   * 
   * @param id
   *          the id to match the revision against
   * @param version
   *          the version to match the revision against
   * @return the revision by version
   */
  public InternalCDORevision getRevisionByVersion(final CDOID id, final int version)
  {
    Connection connection = null;

    try
    {
      connection = getConnection();
      AbstractQueryStatement<InternalCDORevision> statement = new AbstractQueryStatement<InternalCDORevision>()
      {
        @Override
        protected String getSQL()
        {
          StringBuilder builder = new StringBuilder();
          builder.append("SELECT "); //$NON-NLS-1$
          builder.append(DBRevisionCacheSchema.REVISIONS_CDOREVISION);
          builder.append(", "); //$NON-NLS-1$
          builder.append(DBRevisionCacheSchema.REVISIONS_REVISED);
          builder.append(" FROM "); //$NON-NLS-1$
          builder.append(DBRevisionCacheSchema.REVISIONS);
          builder.append(" WHERE "); //$NON-NLS-1$
          builder.append(DBRevisionCacheSchema.REVISIONS_ID);
          builder.append("=? AND "); //$NON-NLS-1$
          builder.append(DBRevisionCacheSchema.REVISIONS_VERSION);
          builder.append("=?"); //$NON-NLS-1$
          return builder.toString();
        }

        @Override
        protected void setParameters(PreparedStatement preparedStatement) throws SQLException
        {
          preparedStatement.setString(1, id.toURIFragment());
          preparedStatement.setInt(2, version);
        }

        @Override
        protected InternalCDORevision getResult(ResultSet resultSet) throws Exception
        {
          long revised = resultSet.getLong(2);
          Blob blob = resultSet.getBlob(1);
          return toRevision(blob, revised);
        }
      };

      return statement.query(connection);
    }
    catch (Exception e)
    {
      throw new DBException("Error while retrieving a revision by version from the database", e); //$NON-NLS-1$
    }
    finally
    {
      DBUtil.close(connection);
    }
  }

  /**
   * Gets the latest revisions of all persisted model versions.
   * 
   * @return the revisions
   */
  public List<CDORevision> getRevisions()
  {
    Connection connection = null;

    try
    {
      connection = getConnection();
      AbstractQueryStatement<List<CDORevision>> query = new AbstractQueryStatement<List<CDORevision>>()
      {
        @Override
        protected String getSQL()
        {
          StringBuilder builder = new StringBuilder();
          builder.append("SELECT "); //$NON-NLS-1$
          builder.append(DBRevisionCacheSchema.REVISIONS_CDOREVISION);
          builder.append(", "); //$NON-NLS-1$
          builder.append(DBRevisionCacheSchema.REVISIONS_REVISED);
          builder.append(" FROM "); //$NON-NLS-1$
          builder.append(DBRevisionCacheSchema.REVISIONS);
          builder.append(" WHERE "); //$NON-NLS-1$
          builder.append(DBRevisionCacheSchema.REVISIONS_REVISED);
          builder.append("="); //$NON-NLS-1$
          builder.append(CDORevision.UNSPECIFIED_DATE);
          return builder.toString();
        }

        @Override
        protected void setParameters(PreparedStatement preparedStatement) throws SQLException
        {
        }

        @Override
        protected List<CDORevision> getResult(ResultSet resultSet) throws Exception
        {
          final List<CDORevision> revisionList = new ArrayList<CDORevision>();

          do
          {
            long revised = resultSet.getLong(2);
            Blob blob = resultSet.getBlob(1);
            revisionList.add(toRevision(blob, revised));
          } while (resultSet.next());

          return revisionList;
        }
      };

      return query.query(connection);
    }
    catch (Exception e)
    {
      throw new DBException("Error while retrieving a revision by version from the database", e); //$NON-NLS-1$
    }
    finally
    {
      DBUtil.close(connection);
    }
  }

  /**
   * Adds a given revision to this cache. It furthermore updates the revised timestamp of the latest (before inserting
   * the new one) revision
   * 
   * @param revision
   *          the revision to add to this cache
   * @return true, if successful
   */
  public boolean addRevision(final CDORevision revision)
  {
    CheckUtil.checkArg(revision, "revision");
    Connection connection = null;

    try
    {
      connection = getConnection();
      AbstractUpdateStatement update = createAddRevisionStatement((InternalCDORevision)revision);
      update.update(connection);

      if (revision.getVersion() > 1)
      {
        // Update former latest revision
        update = createUpdateRevisedStatement((InternalCDORevision)revision);
        update.update(connection);
      }

      return true;
    }
    catch (DBException e)
    {
      throw e;
    }
    catch (Exception e)
    {
      throw new DBException("Error while retrieving the revision from the database", e); //$NON-NLS-1$
    }
    finally
    {
      DBUtil.close(connection);
    }
  }

  private AbstractUpdateStatement createUpdateRevisedStatement(final InternalCDORevision revision)
  {
    return new AbstractUpdateStatement()
    {
      @Override
      protected String getSQL()
      {
        StringBuilder builder = new StringBuilder();
        builder.append("UPDATE "); //$NON-NLS-1$
        builder.append(DBRevisionCacheSchema.REVISIONS);
        builder.append(" SET "); //$NON-NLS-1$
        builder.append(DBRevisionCacheSchema.REVISIONS_REVISED);
        builder.append(" =? WHERE "); //$NON-NLS-1$
        builder.append(DBRevisionCacheSchema.REVISIONS_ID);
        builder.append(" =? AND "); //$NON-NLS-1$
        builder.append(DBRevisionCacheSchema.REVISIONS_VERSION);
        builder.append(" =?"); //$NON-NLS-1$
        return builder.toString();
      }

      @Override
      protected void setParameters(PreparedStatement preparedStatement) throws SQLException
      {
        preparedStatement.setLong(1, revision.getCreated() - 1);
        preparedStatement.setString(2, revision.getID().toURIFragment());
        preparedStatement.setInt(3, revision.getVersion() - 1);
      }
    };
  }

  private AbstractUpdateStatement createAddRevisionStatement(final InternalCDORevision revision)
  {
    return new AbstractUpdateStatement()
    {
      @Override
      protected String getSQL()
      {
        StringBuilder builder = new StringBuilder();
        builder.append("INSERT INTO "); //$NON-NLS-1$
        builder.append(DBRevisionCacheSchema.REVISIONS);
        builder.append(" ("); //$NON-NLS-1$
        builder.append(DBRevisionCacheSchema.REVISIONS_ID);
        builder.append(", "); //$NON-NLS-1$
        builder.append(DBRevisionCacheSchema.REVISIONS_VERSION);
        builder.append(", "); //$NON-NLS-1$
        builder.append(DBRevisionCacheSchema.REVISIONS_CREATED);
        builder.append(", "); //$NON-NLS-1$
        builder.append(DBRevisionCacheSchema.REVISIONS_REVISED);
        builder.append(", "); //$NON-NLS-1$
        builder.append(DBRevisionCacheSchema.REVISIONS_CDOREVISION);
        builder.append(", "); //$NON-NLS-1$
        builder.append(DBRevisionCacheSchema.REVISIONS_RESOURCENODE_NAME);
        builder.append(", "); //$NON-NLS-1$
        builder.append(DBRevisionCacheSchema.REVISIONS_CONTAINERID);
        builder.append(") "); //$NON-NLS-1$
        builder.append(" VALUES (?, ?, ?, ?, ?, ? ,?)"); //$NON-NLS-1$
        return builder.toString();
      }

      @Override
      protected void setParameters(PreparedStatement preparedStatement) throws Exception
      {
        preparedStatement.setString(1, revision.getID().toURIFragment());
        preparedStatement.setInt(2, revision.getVersion());
        preparedStatement.setLong(3, revision.getCreated());
        preparedStatement.setLong(4, revision.getRevised());
        preparedStatement.setBytes(5, toBytes(revision));
        setResourceNodeValues(revision, preparedStatement);
      }

      /**
       * Sets the values in the prepared statment, that are related to the given revision. If the revision is a resource
       * node, the values are set otherwise the fields are set to <tt>null</tt>
       * 
       * @param revision
       *          the revision
       * @param preparedStatement
       *          the prepared statement
       * @throws SQLException
       *           the SQL exception
       */
      private void setResourceNodeValues(InternalCDORevision revision, PreparedStatement preparedStatement)
          throws SQLException
      {
        if (revision.isResourceNode())
        {
          preparedStatement.setString(6, DBRevisionCacheUtil.getResourceNodeName(revision));
          CDOID containerID = (CDOID)revision.getContainerID();
          preparedStatement.setString(7, containerID.toURIFragment());
        }
        else
        {
          preparedStatement.setNull(6, Types.VARCHAR);
          preparedStatement.setNull(7, Types.INTEGER);
        }
      }
    };
  }

  public void removeRevision(CDORevision revision)
  {
    removeRevision(revision.getID(), revision.getVersion());
  }

  /**
   * Removes a revision by its Id and version. If the given revision does not exist <tt>null</tt> is returned. Otherwise
   * the {@link InternalCDORevision}, that was removed is returned
   * 
   * @param id
   *          the id of the revision to remove
   * @param version
   *          the version of the revision to remove
   * @return the {@link InternalCDORevision} that was removed, <tt>null</tt> otherwise
   */
  public InternalCDORevision removeRevision(CDOID id, int version)
  {
    Connection connection = null;

    try
    {
      final InternalCDORevision revision = getRevisionByVersion(id, version);
      if (revision != null)
      {
        connection = getConnection();
        AbstractUpdateStatement statement = new AbstractUpdateStatement()
        {
          @Override
          protected String getSQL()
          {
            StringBuilder builder = new StringBuilder();
            builder.append("DELETE FROM "); //$NON-NLS-1$
            builder.append(DBRevisionCacheSchema.REVISIONS);
            builder.append(" WHERE ID =? AND VERSION =?"); //$NON-NLS-1$
            return builder.toString();
          }

          @Override
          protected void setParameters(PreparedStatement preparedStatement) throws Exception
          {
            preparedStatement.setString(1, revision.getID().toURIFragment());
            preparedStatement.setInt(2, revision.getVersion());
          }
        };

        statement.update(connection);
      }

      return revision;
    }
    catch (Exception e)
    {
      throw new DBException("Error while removing a revision from the database", e); //$NON-NLS-1$
    }
    finally
    {
      DBUtil.close(connection);
    }
  }

  /**
   * Removes all revisions from this cache (and its database).
   */
  public void clear()
  {
    Connection connection = null;

    try
    {
      connection = getConnection();
      AbstractUpdateStatement update = new AbstractUpdateStatement()
      {
        @Override
        protected String getSQL()
        {
          StringBuilder builder = new StringBuilder();
          builder.append("DELETE FROM  "); //$NON-NLS-1$
          builder.append(DBRevisionCacheSchema.REVISIONS);
          return builder.toString();
        }
      };

      update.update(connection);
    }
    catch (Exception e)
    {
      throw new DBException("Error while clearing the database", e);
    }
    finally
    {
      DBUtil.close(connection);
    }
  }

  @Override
  protected void doBeforeActivate() throws Exception
  {
    super.doBeforeActivate();
    checkState(idProvider, "idProvider"); //$NON-NLS-1$
    checkState(listFactory, "listFactory");//$NON-NLS-1$
    checkState(packageRegistry, "packageRegistry"); //$NON-NLS-1$
    checkState(revisionFactory, "revisionFactory"); //$NON-NLS-1$
    checkState(dbAdapter, "dbAdapter"); //$NON-NLS-1$
    checkState(dbConnectionProvider, "dbConnectionProvider"); //$NON-NLS-1$
  }

  @Override
  protected void doActivate() throws Exception
  {
    super.doActivate();
    createTable();
  }

  /**
   * Creates the (single) table that's used to store the cached revisions.
   * 
   * @throws SQLException
   *           Signals that an error has occured while getting the connection or committing the transaction
   */
  private void createTable() throws SQLException
  {
    Connection connection = null;

    try
    {
      connection = getConnection();
      DBRevisionCacheSchema.INSTANCE.create(dbAdapter, connection);
      connection.commit();
    }
    finally
    {
      DBUtil.close(connection);
    }
  }

  private static StringBuilder appendTimestampCondition(StringBuilder builder)
  {
    builder.append(DBRevisionCacheSchema.REVISIONS_CREATED);
    builder.append("<=? AND ("); //$NON-NLS-1$
    builder.append(DBRevisionCacheSchema.REVISIONS_REVISED);
    builder.append(">=? OR "); //$NON-NLS-1$
    builder.append(DBRevisionCacheSchema.REVISIONS_REVISED);
    builder.append("="); //$NON-NLS-1$
    builder.append(CDORevision.UNSPECIFIED_DATE);
    builder.append(")"); //$NON-NLS-1$
    return builder;
  }

  /**
   * Converts the given Objects to an {@link InternalCDORevision}. The object is deserialized to an instance of the
   * correct type and the revised timestamp is set separatley. Whe you insert a new revision into this cache, the former
   * latest revision gets a new revised timestamp. This timestamp's only updated in the database column 'revised', not
   * in the blob that holds the serialized instance. Therefore the revised timestamp has to be set separately
   * 
   * @param revisedTimestamp
   *          the revised timestamp to set to the revision
   * @param blob
   *          the blob that holds the revision
   * @return the revision
   * @throws IOException
   *           Signals that an error has occurred while reading the revision from the blob.
   * @throws SQLException
   *           Signals that an error hass occured while getting the binary stream from the blob
   */
  private InternalCDORevision toRevision(Blob blob, long revisedTimestamp) throws IOException, SQLException
  {
    CDODataInput dataInput = getCDODataInput(ExtendedDataInputStream.wrap(blob.getBinaryStream()));
    InternalCDORevision revision = (InternalCDORevision)dataInput.readCDORevision();
    // Revised timestamp's updated in the revised column only (not in the blob)
    revision.setRevised(revisedTimestamp);
    return revision;
  }

  private CDODataInput getCDODataInput(ExtendedDataInputStream inputStream) throws IOException
  {
    return new CDODataInputImpl(inputStream)
    {
      @Override
      protected CDOListFactory getListFactory()
      {
        return listFactory;
      }

      @Override
      protected CDOPackageRegistry getPackageRegistry()
      {
        return packageRegistry;
      }

      @Override
      protected CDORevisionFactory getRevisionFactory()
      {
        return revisionFactory;
      }
    };
  }

  /**
   * Converts a given {@link CDORevision} to a byte array.
   * 
   * @param revision
   *          the revision
   * @return the array of bytes for the given revision
   * @throws IOException
   *           Signals an error has occurred while writing the revision to the byte array.
   */
  private byte[] toBytes(InternalCDORevision revision) throws IOException
  {
    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
    CDODataOutput dataOutput = getCDODataOutput(ExtendedDataOutputStream.wrap(byteArrayOutputStream));
    dataOutput.writeCDORevision(revision, CDORevision.UNCHUNKED);
    return byteArrayOutputStream.toByteArray();
  }

  private CDODataOutput getCDODataOutput(ExtendedDataOutput extendedDataOutputStream)
  {
    return new CDODataOutputImpl(extendedDataOutputStream)
    {
      public CDOPackageRegistry getPackageRegistry()
      {
        return packageRegistry;
      }

      public CDOIDProvider getIDProvider()
      {
        return idProvider;
      }
    };
  }

  /**
   * Gets a connection from the {@link IDBConnectionProvider} within this cache. The Connection is set not to auto
   * commit transactions.
   * 
   * @return the connection
   * @throws SQLException
   *           Signals that an error occured while getting the connection from the connection provider
   */
  private Connection getConnection() throws SQLException
  {
    Connection connection = dbConnectionProvider.getConnection();
    connection.setAutoCommit(false);
    return connection;
  }
}
