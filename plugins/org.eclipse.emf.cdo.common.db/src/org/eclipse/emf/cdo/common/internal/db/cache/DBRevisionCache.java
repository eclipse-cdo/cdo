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
import org.eclipse.emf.cdo.common.id.CDOIDObjectFactory;
import org.eclipse.emf.cdo.common.id.CDOIDProvider;
import org.eclipse.emf.cdo.common.internal.db.AbstractPreparedStatementFactory;
import org.eclipse.emf.cdo.common.internal.db.DBRevisionCacheUtil;
import org.eclipse.emf.cdo.common.internal.db.IPreparedStatementFactory;
import org.eclipse.emf.cdo.common.io.CDODataInput;
import org.eclipse.emf.cdo.common.io.CDODataOutput;
import org.eclipse.emf.cdo.common.model.CDOPackageRegistry;
import org.eclipse.emf.cdo.common.revision.CDOListFactory;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.CDORevisionResolver;
import org.eclipse.emf.cdo.common.revision.cache.CDORevisionCache;
import org.eclipse.emf.cdo.internal.common.io.CDODataInputImpl;
import org.eclipse.emf.cdo.internal.common.io.CDODataOutputImpl;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;

import org.eclipse.net4j.db.DBException;
import org.eclipse.net4j.db.DBUtil;
import org.eclipse.net4j.db.IDBAdapter;
import org.eclipse.net4j.db.IDBConnectionProvider;
import org.eclipse.net4j.db.IDBRowHandler;
import org.eclipse.net4j.util.CheckUtil;
import org.eclipse.net4j.util.WrappedException;
import org.eclipse.net4j.util.io.ExtendedDataInputStream;
import org.eclipse.net4j.util.io.ExtendedDataOutputStream;
import org.eclipse.net4j.util.lifecycle.Lifecycle;

import org.eclipse.emf.ecore.EClass;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
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
  private CDOIDObjectFactory idObjectFactory;

  private CDOIDProvider idProvider;

  private CDOListFactory listFactory;

  private CDOPackageRegistry packageRegistry;

  private CDORevisionResolver revisionResolver;

  private IDBAdapter dbAdapter;

  private IDBConnectionProvider dbConnectionProvider;

  /**
   * The prepared statement used to insert new revisions.
   */
  private IPreparedStatementFactory insertRevisionStatementFactory = new InsertRevisionStatementFactory();

  /**
   * The prepared statement used to update the revised timestamp in the latest revision.
   */
  private IPreparedStatementFactory updateRevisedStatementFactory = new UpdateRevisedStatementFactory();

  /**
   * The prepared statement used to delete a revision.
   */
  private IPreparedStatementFactory deleteRevisionStatementFactory = new DeleteRevisionStatementFactory();

  /**
   * The prepared statement used to delete all entries.
   */
  private IPreparedStatementFactory clearStatementFactory = new ClearStatementFactory();

  /**
   * The database connection used in this cache.
   */
  private Connection connection;

  public DBRevisionCache()
  {
  }

  public CDOIDObjectFactory getIDObjectFactory()
  {
    return idObjectFactory;
  }

  public void setIDObjectFactory(CDOIDObjectFactory idObjectFactory)
  {
    this.idObjectFactory = idObjectFactory;
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

  public CDORevisionResolver getRevisionResolver()
  {
    return revisionResolver;
  }

  public void setRevisionResolver(CDORevisionResolver revisionResolver)
  {
    this.revisionResolver = revisionResolver;
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
   * name and timestamp TODO Use prepared statement!
   * 
   * @param folderID
   *          the folder id
   * @param name
   *          the name
   * @param timeStamp
   *          the time stamp
   * @return the resource id
   */
  public CDOID getResourceID(CDOID folderID, String name, long timeStamp)
  {
    StringBuilder builder = new StringBuilder();
    builder.append(DBRevisionCacheSchema.REVISIONS_ID);
    builder.append("=");
    builder.append(folderID.toURIFragment());
    builder.append(" AND ");
    appendTimestampCondition(builder, timeStamp);
    builder.append(" AND ");
    builder.append(DBRevisionCacheSchema.REVISIONS_RESOURCENODE_NAME);
    builder.append("='");
    builder.append(name);
    builder.append("'");

    InternalCDORevision revision = selectRevision(builder.toString());
    if (revision == null)
    {
      return null;
    }

    return revision.getID();
  }

  /**
   * Gets the revision with the highest version for a given {@link CDOID}. TODO Use prepared statement!
   * 
   * @param id
   *          the id
   * @return the revision
   */
  public InternalCDORevision getRevision(CDOID id)
  {
    StringBuilder builder = new StringBuilder();
    builder.append(DBRevisionCacheSchema.REVISIONS_ID);
    builder.append("=");
    builder.append(id.toURIFragment());
    builder.append(" AND ");
    builder.append(DBRevisionCacheSchema.REVISIONS_REVISED);
    builder.append("=");
    builder.append(CDORevision.UNSPECIFIED_DATE);
    return selectRevision(builder.toString());
  }

  /**
   * Gets an {@link InternalCDORevision} that matches the given timestamp (it is >= created timestamp AND <= revised
   * timestamp of the revision). TODO Use prepared statement!
   * 
   * @param id
   *          the id
   * @param timeStamp
   *          the time stamp
   * @return the revision by time
   */
  public InternalCDORevision getRevisionByTime(CDOID id, long timeStamp)
  {
    StringBuilder builder = new StringBuilder();
    builder.append(DBRevisionCacheSchema.REVISIONS_ID);
    builder.append("=");
    builder.append(id.toURIFragment());
    builder.append(" AND ");
    appendTimestampCondition(builder, timeStamp);
    return selectRevision(builder.toString());
  }

  /**
   * TODO Use prepared statement!
   */
  public InternalCDORevision getRevisionByVersion(CDOID id, int version)
  {
    StringBuilder builder = new StringBuilder();
    builder.append(DBRevisionCacheSchema.REVISIONS_ID);
    builder.append("=");
    builder.append(id.toURIFragment());
    builder.append(" AND ");
    builder.append(DBRevisionCacheSchema.REVISIONS_REVISED);
    builder.append("=");
    builder.append(CDORevision.UNSPECIFIED_DATE);
    return selectRevision(builder.toString());
  }

  /**
   * Gets the latest revisions of all persisted model versions.
   * 
   * @return the revisions
   */
  public List<CDORevision> getRevisions()
  {
    final List<CDORevision> result = new ArrayList<CDORevision>();
    IDBRowHandler rowHandler = new IDBRowHandler()
    {
      public boolean handle(int row, final Object... values)
      {
        try
        {
          byte[] blob = (byte[])values[0];
          result.add(toRevision(blob));
          return true;
        }
        catch (IOException ex)
        {
          throw WrappedException.wrap(ex);
        }
      }
    };

    DBUtil.select(connection //
        , rowHandler //
        , DBRevisionCacheSchema.REVISIONS_REVISED + "=" + CDORevision.UNSPECIFIED_DATE //
        , DBRevisionCacheSchema.REVISIONS_CDOREVISION);
    return result;
  }

  /**
   * Adds a given revision to this cache. It furthermore updates the revised timestamp of the latest (before inserting
   * the new one) revision
   * 
   * @param revision
   *          the revision to add to this cache
   * @return true, if successful
   */
  public boolean addRevision(InternalCDORevision revision)
  {
    try
    {
      PreparedStatement stmt = insertRevisionStatementFactory.getPreparedStatement(revision, connection);
      DBRevisionCacheUtil.mandatoryInsertUpdate(stmt);
      if (revision.getVersion() > 1)
      {
        // Update former latest revision
        DBRevisionCacheUtil.insertUpdate(updateRevisedStatementFactory.getPreparedStatement(revision, connection));
      }

      return true;
    }
    catch (DBException ex)
    {
      throw ex;
    }
    catch (Exception ex)
    {
      throw new DBException(ex);
    }
  }

  /**
   * Removes a revision by its Id and version. If the given revision does not exist <tt>null</tt> is returned. Otherwise
   * the {@link InternalCDORevision}, that was removed is returned
   * 
   * @param id
   *          the id of the revision to remove
   * @param version
   *          the version of the revision to remove
   * @return the {@link InternalCDORevision} that was remove, <tt>null</tt> otherwise
   */
  public InternalCDORevision removeRevision(CDOID id, int version)
  {
    try
    {
      InternalCDORevision revision = getRevisionByVersion(id, version);
      if (revision != null)
      {
        PreparedStatement stmt = deleteRevisionStatementFactory.getPreparedStatement(revision, connection);
        DBRevisionCacheUtil.mandatoryInsertUpdate(stmt);
      }

      return revision;
    }
    catch (DBException ex)
    {
      throw ex;
    }
    catch (Exception ex)
    {
      throw new DBException(ex);
    }
  }

  public void clear()
  {
    try
    {
      PreparedStatement stmt = clearStatementFactory.getPreparedStatement(null, connection);
      DBRevisionCacheUtil.insertUpdate(stmt);
      stmt.executeUpdate();
    }
    catch (DBException ex)
    {
      throw ex;
    }
    catch (Exception ex)
    {
      throw new DBException(ex);
    }
  }

  @Override
  protected void doBeforeActivate() throws Exception
  {
    super.doBeforeActivate();
    checkState(idObjectFactory, "idObjectFactory"); //$NON-NLS-1$
    checkState(idProvider, "idProvider"); //$NON-NLS-1$
    checkState(listFactory, "listFactory"); //$NON-NLS-1$
    checkState(packageRegistry, "packageRegistry"); //$NON-NLS-1$
    checkState(revisionResolver, "revisionResolver"); //$NON-NLS-1$
    checkState(dbAdapter, "dbAdapter"); //$NON-NLS-1$
    checkState(dbConnectionProvider, "dbConnectionProvider"); //$NON-NLS-1$
  }

  @Override
  protected void doActivate() throws Exception
  {
    super.doActivate();
    connection = dbConnectionProvider.getConnection();
    connection.setAutoCommit(false);
    createTable();
  }

  @Override
  protected void doDeactivate() throws Exception
  {
    try
    {
      updateRevisedStatementFactory.close();
      updateRevisedStatementFactory = null;

      insertRevisionStatementFactory.close();
      insertRevisionStatementFactory = null;

      deleteRevisionStatementFactory.close();
      deleteRevisionStatementFactory = null;
    }
    finally
    {
      DBUtil.close(connection);
      connection = null;
    }

    super.doDeactivate();
  }

  private void createTable() throws SQLException
  {
    DBRevisionCacheSchema.INSTANCE.create(dbAdapter, connection);
    DBRevisionCacheUtil.commit(connection);
  }

  public static StringBuilder appendTimestampCondition(StringBuilder builder, long timeStamp)
  {
    builder.append(DBRevisionCacheSchema.REVISIONS_CREATED);
    builder.append("<=");
    builder.append(timeStamp);
    builder.append(" AND");
    builder.append(" (");
    builder.append(DBRevisionCacheSchema.REVISIONS_REVISED);
    builder.append(">=");
    builder.append(timeStamp);
    builder.append(" OR " + DBRevisionCacheSchema.REVISIONS_REVISED);
    builder.append("=" + CDORevision.UNSPECIFIED_DATE);
    builder.append(")");
    return builder;
  }

  private InternalCDORevision selectRevision(String sql)
  {
    final InternalCDORevision[] result = new InternalCDORevision[1];
    IDBRowHandler rowHandler = new IDBRowHandler()
    {
      public boolean handle(int row, final Object... values)
      {
        try
        {
          CheckUtil.checkState(result[0], "Database inconsistent: There is more than 1 revision satisfying the query");
          byte[] revisionBlob = (byte[])values[0];
          long revisedTimestamp = (Long)values[1];
          result[0] = toRevision(revisionBlob, revisedTimestamp);
          return true;
        }
        catch (IOException ex)
        {
          throw WrappedException.wrap(ex);
        }
      }
    };

    DBUtil.select(connection//
        , rowHandler //
        , sql //
        , DBRevisionCacheSchema.REVISIONS_CDOREVISION //
        , DBRevisionCacheSchema.REVISIONS_REVISED);
    return result[0];
  }

  /**
   * Converts a given {@link CDORevision revision} to an array of bytes.
   * 
   * @param revision
   *          the revision
   * @return the bytes
   * @throws IOException
   *           Signals that an I/O exception has occurred.
   */
  private byte[] toBytes(CDORevision revision) throws IOException
  {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    CDODataOutput out = new CDODataOutputImpl(ExtendedDataOutputStream.wrap(baos))
    {
      public CDOIDProvider getIDProvider()
      {
        return idProvider;
      }
    };

    out.writeCDORevision(revision, CDORevision.UNCHUNKED);
    return baos.toByteArray();
  }

  /**
   * Converts the given Objects to an {@link InternalCDORevision}. The object is deserialized to an instance of the
   * correct type and the revised timestamp is set separatley. Whe you insert a new revision into this cache, the former
   * latest revision gets a new revised timestamp. This timestamp's only updated in the database column 'revised', not
   * in the blob that holds the serialized instance. Therefore the revised timestamp has to be set separately
   * 
   * @param revisedTimestamp
   *          the value
   * @param blob
   *          the cdo instance data
   * @return the internal cdo revision
   * @throws IOException
   *           Signals that an I/O exception has occurred.
   */
  private InternalCDORevision toRevision(byte[] blob, long revisedTimestamp) throws IOException
  {
    InternalCDORevision revision = toRevision(blob);
    // Revised timestamp's updated in the revised column only (not in the blob)
    revision.setRevised(revisedTimestamp);
    return revision;
  }

  /**
   * Converts a given byteArray to a CDORevision
   * 
   * @param blob
   *          the byte array
   * @return the cDO revision
   * @throws IOException
   *           Signals that an I/O exception has occurred.
   */
  private InternalCDORevision toRevision(byte[] blob) throws IOException
  {
    CDODataInput in = new CDODataInputImpl(ExtendedDataInputStream.wrap(new ByteArrayInputStream(blob)))
    {
      @Override
      protected CDOIDObjectFactory getIDFactory()
      {
        return idObjectFactory;
      }

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
      protected CDORevisionResolver getRevisionResolver()
      {
        return revisionResolver;
      }
    };

    return (InternalCDORevision)in.readCDORevision();
  }

  private class InsertRevisionStatementFactory extends AbstractPreparedStatementFactory
  {
    @Override
    protected String getSQL()
    {
      return "INSERT INTO " + DBRevisionCacheSchema.REVISIONS //
          + " (" //
          + DBRevisionCacheSchema.REVISIONS_ID //
          + ", " + DBRevisionCacheSchema.REVISIONS_VERSION //
          + ", " + DBRevisionCacheSchema.REVISIONS_CREATED //
          + ", " + DBRevisionCacheSchema.REVISIONS_REVISED //
          + ", " + DBRevisionCacheSchema.REVISIONS_CDOREVISION //
          + ", " + DBRevisionCacheSchema.REVISIONS_RESOURCENODE_NAME //
          + ", " + DBRevisionCacheSchema.REVISIONS_CONTAINERID //
          + ") " //
          + " VALUES (?, ?, ?, ?, ?, ? ,?)";
    }

    @Override
    protected void setParameters(InternalCDORevision revision, PreparedStatement preparedStatement) throws Exception
    {
      preparedStatement.setString(1, revision.getID().toURIFragment());
      preparedStatement.setInt(2, revision.getVersion());
      preparedStatement.setLong(3, revision.getCreated());
      preparedStatement.setLong(4, revision.getRevised());
      preparedStatement.setBytes(5, toBytes(revision));
      setResourceNodeValues(revision, preparedStatement);
    }

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
  }

  private class UpdateRevisedStatementFactory extends AbstractPreparedStatementFactory
  {
    @Override
    protected String getSQL()
    {
      return "UPDATE " + DBRevisionCacheSchema.REVISIONS //
          + " SET " + DBRevisionCacheSchema.REVISIONS_REVISED + " = ?" //
          + " WHERE " + DBRevisionCacheSchema.REVISIONS_ID + " = ?" //
          + " AND " + DBRevisionCacheSchema.REVISIONS_VERSION + " = ?";
    }

    @Override
    protected void setParameters(InternalCDORevision revision, PreparedStatement preparedStatement) throws Exception
    {
      preparedStatement.setLong(1, revision.getCreated() - 1);
      preparedStatement.setString(2, revision.getID().toURIFragment());
      preparedStatement.setInt(3, revision.getVersion() - 1);
    }
  }

  private class DeleteRevisionStatementFactory extends AbstractPreparedStatementFactory
  {
    @Override
    protected String getSQL()
    {
      return "DELETE FROM " + DBRevisionCacheSchema.REVISIONS //
          + " WHERE " //
          + " ID = ?" //
          + " AND VERSION = ?";
    }

    @Override
    protected void setParameters(InternalCDORevision revision, PreparedStatement preparedStatement) throws Exception
    {
      preparedStatement.setString(1, revision.getID().toURIFragment());
      preparedStatement.setInt(2, revision.getVersion());
    }
  }

  private class ClearStatementFactory extends AbstractPreparedStatementFactory
  {
    @Override
    protected String getSQL()
    {
      return "DELETE FROM " //
          + DBRevisionCacheSchema.REVISIONS; //
    }

    @Override
    protected void setParameters(InternalCDORevision revision, PreparedStatement preparedStatement) throws Exception
    {
    }
  }
}
