/*
 * Copyright (c) 2009-2013, 2016, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Andre Dietisheim - initial API and implementation
 *    Eike Stepper - maintenance
 */
package org.eclipse.emf.cdo.common.internal.db.cache;

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.branch.CDOBranchManager;
import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.branch.CDOBranchVersion;
import org.eclipse.emf.cdo.common.commit.CDOCommitInfoManager;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDProvider;
import org.eclipse.emf.cdo.common.internal.db.AbstractQueryStatement;
import org.eclipse.emf.cdo.common.internal.db.AbstractUpdateStatement;
import org.eclipse.emf.cdo.common.internal.db.DBRevisionCacheUtil;
import org.eclipse.emf.cdo.common.lob.CDOLobStore;
import org.eclipse.emf.cdo.common.model.CDOPackageRegistry;
import org.eclipse.emf.cdo.common.protocol.CDODataInput;
import org.eclipse.emf.cdo.common.protocol.CDODataOutput;
import org.eclipse.emf.cdo.common.revision.CDOListFactory;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.CDORevisionCache;
import org.eclipse.emf.cdo.common.revision.CDORevisionFactory;
import org.eclipse.emf.cdo.spi.common.protocol.CDODataInputImpl;
import org.eclipse.emf.cdo.spi.common.protocol.CDODataOutputImpl;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevisionCache;

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
import java.util.Map;

/**
 * A JDBC-based {@link CDORevisionCache}.
 *
 * @author Andre Dietisheim
 */
public class DBRevisionCache extends Lifecycle implements InternalCDORevisionCache
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

  @Override
  public InternalCDORevisionCache instantiate(CDORevision revision)
  {
    // TODO: Support branches directly
    throw new UnsupportedOperationException();
  }

  public CDOIDProvider getIDProvider()
  {
    return idProvider;
  }

  public void setIDProvider(CDOIDProvider idProvider)
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

  @Override
  public EClass getObjectType(CDOID id)
  {
    return null;
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
    String sql = null;

    try
    {
      connection = getConnection();
      AbstractQueryStatement<InternalCDORevision> query = createGetRevisionByIDStatement(id);
      sql = query.getSQL();
      return query.query(connection);
    }
    catch (Exception e)
    {
      throw new DBException("Error while retrieving the revision from the database", e, sql); //$NON-NLS-1$
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
      public String getSQL()
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
   * @return the revision by time
   */
  @Override
  public InternalCDORevision getRevision(final CDOID id, final CDOBranchPoint branchPoint)
  {
    Connection connection = null;
    String sql = null;

    try
    {
      connection = getConnection();
      AbstractQueryStatement<InternalCDORevision> statement = new AbstractQueryStatement<InternalCDORevision>()
      {
        @Override
        public String getSQL()
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
          long timeStamp = branchPoint.getTimeStamp();
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

      sql = statement.getSQL();
      return statement.query(connection);
    }
    catch (Exception e)
    {
      throw new DBException("Error while retrieving a revision by timestamp from the database", e, sql); //$NON-NLS-1$
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
   * @return the revision by version
   */
  @Override
  public InternalCDORevision getRevisionByVersion(final CDOID id, final CDOBranchVersion branchVersion)
  {
    Connection connection = null;
    String sql = null;

    try
    {
      connection = getConnection();
      AbstractQueryStatement<InternalCDORevision> statement = new AbstractQueryStatement<InternalCDORevision>()
      {
        @Override
        public String getSQL()
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
          preparedStatement.setInt(2, branchVersion.getVersion());
        }

        @Override
        protected InternalCDORevision getResult(ResultSet resultSet) throws Exception
        {
          long revised = resultSet.getLong(2);
          Blob blob = resultSet.getBlob(1);
          return toRevision(blob, revised);
        }
      };

      sql = statement.getSQL();
      return statement.query(connection);
    }
    catch (Exception e)
    {
      throw new DBException("Error while retrieving a revision by version from the database", e, sql); //$NON-NLS-1$
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
  @Override
  public List<CDORevision> getCurrentRevisions()
  {
    Connection connection = null;
    String sql = null;

    try
    {
      connection = getConnection();
      AbstractQueryStatement<List<CDORevision>> query = new AbstractQueryStatement<List<CDORevision>>()
      {
        @Override
        public String getSQL()
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
          final List<CDORevision> revisionList = new ArrayList<>();

          do
          {
            long revised = resultSet.getLong(2);
            Blob blob = resultSet.getBlob(1);
            revisionList.add(toRevision(blob, revised));
          } while (resultSet.next());

          return revisionList;
        }
      };

      sql = query.getSQL();
      return query.query(connection);
    }
    catch (Exception e)
    {
      throw new DBException("Error while retrieving a revision by version from the database", e, sql); //$NON-NLS-1$
    }
    finally
    {
      DBUtil.close(connection);
    }
  }

  @Override
  public void getAllRevisions(List<InternalCDORevision> result)
  {
    // TODO: implement DBRevisionCache.enclosing_method(enclosing_method_arguments)
    throw new UnsupportedOperationException();
  }

  /**
   * Adds a given revision to this cache. It furthermore updates the revised timestamp of the latest (before inserting
   * the new one) revision
   *
   * @param revision
   *          the revision to add to this cache
   */
  @Override
  public void addRevision(CDORevision revision)
  {
    CheckUtil.checkArg(revision, "revision");
    Connection connection = null;
    String sql = null;

    try
    {
      connection = getConnection();
      AbstractUpdateStatement update = createAddRevisionStatement((InternalCDORevision)revision);
      sql = update.getSQL();
      update.update(connection);

      if (revision.getVersion() > CDORevision.FIRST_VERSION)
      {
        // Update former latest revision
        update = createUpdateRevisedStatement((InternalCDORevision)revision);
        update.update(connection);
      }
    }
    catch (DBException e)
    {
      throw e;
    }
    catch (Exception e)
    {
      throw new DBException("Error while retrieving the revision from the database", e, sql); //$NON-NLS-1$
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
      public String getSQL()
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
        preparedStatement.setLong(1, revision.getTimeStamp() - 1);
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
      public String getSQL()
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
        preparedStatement.setLong(3, revision.getTimeStamp());
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
      private void setResourceNodeValues(InternalCDORevision revision, PreparedStatement preparedStatement) throws SQLException
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

  /**
   * Removes a revision by its Id and version. If the given revision does not exist <tt>null</tt> is returned. Otherwise
   * the {@link InternalCDORevision}, that was removed is returned
   *
   * @param id
   *          the id of the revision to remove
   * @return the {@link InternalCDORevision} that was removed, <tt>null</tt> otherwise
   */
  @Override
  public InternalCDORevision removeRevision(CDOID id, CDOBranchVersion branchVersion)
  {
    Connection connection = null;
    String sql = null;

    try
    {
      final InternalCDORevision revision = getRevisionByVersion(id, branchVersion);
      if (revision != null)
      {
        connection = getConnection();
        AbstractUpdateStatement statement = new AbstractUpdateStatement()
        {
          @Override
          public String getSQL()
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

        sql = statement.getSQL();
        statement.update(connection);
      }

      return revision;
    }
    catch (Exception e)
    {
      throw new DBException("Error while removing a revision from the database", e, sql); //$NON-NLS-1$
    }
    finally
    {
      DBUtil.close(connection);
    }
  }

  /**
   * Removes all revisions from this cache (and its database).
   */
  @Override
  public void clear()
  {
    Connection connection = null;
    String sql = null;

    try
    {
      connection = getConnection();
      AbstractUpdateStatement update = new AbstractUpdateStatement()
      {
        @Override
        public String getSQL()
        {
          StringBuilder builder = new StringBuilder();
          builder.append("DELETE FROM  "); //$NON-NLS-1$
          builder.append(DBRevisionCacheSchema.REVISIONS);
          return builder.toString();
        }
      };

      sql = update.getSQL();
      update.update(connection);
    }
    catch (Exception e)
    {
      throw new DBException("Error while clearing the database", e, sql);
    }
    finally
    {
      DBUtil.close(connection);
    }
  }

  @Override
  public Map<CDOBranch, List<CDORevision>> getAllRevisions()
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public List<CDORevision> getRevisions(CDOBranchPoint branchPoint)
  {
    throw new UnsupportedOperationException();
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
      public CDOPackageRegistry getPackageRegistry()
      {
        return packageRegistry;
      }

      @Override
      protected CDOBranchManager getBranchManager()
      {
        return null;
      }

      @Override
      protected CDOCommitInfoManager getCommitInfoManager()
      {
        return null;
      }

      @Override
      protected CDORevisionFactory getRevisionFactory()
      {
        return revisionFactory;
      }

      @Override
      protected CDOListFactory getListFactory()
      {
        return listFactory;
      }

      @Override
      protected CDOLobStore getLobStore()
      {
        return null;
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
      @Override
      public CDOPackageRegistry getPackageRegistry()
      {
        return packageRegistry;
      }

      @Override
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
