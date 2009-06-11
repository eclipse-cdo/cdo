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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.eclipse.emf.cdo.common.db.CDOCommonDBUtil;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDObjectFactory;
import org.eclipse.emf.cdo.common.id.CDOIDProvider;
import org.eclipse.emf.cdo.common.internal.db.AbstractPreparedStatementFactory;
import org.eclipse.emf.cdo.common.internal.db.DBRevisionCacheUtil;
import org.eclipse.emf.cdo.common.internal.db.IPreparedStatementFactory;
import org.eclipse.emf.cdo.common.internal.db.bundle.OM;
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
import org.eclipse.emf.ecore.EClass;
import org.eclipse.net4j.db.DBException;
import org.eclipse.net4j.db.DBUtil;
import org.eclipse.net4j.db.IDBAdapter;
import org.eclipse.net4j.db.IDBConnectionProvider;
import org.eclipse.net4j.db.IDBRowHandler;
import org.eclipse.net4j.util.CheckUtil;
import org.eclipse.net4j.util.io.ExtendedDataInputStream;
import org.eclipse.net4j.util.io.ExtendedDataOutputStream;
import org.eclipse.net4j.util.lifecycle.Lifecycle;
import org.eclipse.net4j.util.om.trace.ContextTracer;

/**
 * A JDBC-based {@link CDORevisionCache}.
 * 
 * @author Andre Dietisheim
 */
public class DBRevisionCache extends Lifecycle implements CDORevisionCache {

	/** The Constant DB_REVISIONCACHE_NAME. */
	private static final String DB_REVISIONCACHE_NAME = "org.eclipse.emf.cdo.common.db.DBRevisionCache";

	private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG,
			DBRevisionCache.class);

	/** The data source. */
	private DataSource dataSource;

	/** The db adapter. */
	private IDBAdapter dbAdapter;

	/** The db connection provider. */
	private IDBConnectionProvider dbConnectionProvider;

	/** The cdo id provider. */
	private CDOIDProvider cdoIdProvider;

	/** The prepared statement used to insert new revisions. */
	private IPreparedStatementFactory<InternalCDORevision> insertRevisionStatementFactory;

	/**
	 * The prepared statement used to update the revised timestamp in the latest
	 * revision.
	 */
	private IPreparedStatementFactory<InternalCDORevision> updateRevisedStatementFactory;

	/** The prepared statement used to delete a revision. */
	private IPreparedStatementFactory<InternalCDORevision> deleteRevisionStatementFactory;

	/** The prepared statement used to delete all entries. */
	private IPreparedStatementFactory<InternalCDORevision> clearStatementFactory;

	/** The database connection used in this cache. */
	private Connection connection;

	protected CDOIDObjectFactory cdoIdObjectFactory;

	private CDOListFactory cdoListFactory;

	private CDOPackageRegistry cdoPackageRegistry;

	private CDORevisionResolver cdoRevisionResolver;

	private class InsertRevisionStatementFactory extends
			AbstractPreparedStatementFactory<InternalCDORevision> {
		@Override
		protected void doSetParameters(InternalCDORevision cdoRevision,
				PreparedStatement preparedStatement) throws Exception {
			preparedStatement.setString(1, cdoRevision.getID().toURIFragment());
			preparedStatement.setInt(2, cdoRevision.getVersion());
			preparedStatement.setLong(3, cdoRevision.getCreated());
			preparedStatement.setLong(4, cdoRevision.getRevised());
			preparedStatement.setBytes(5, toBytes(cdoRevision));
			setResourceNodeValues(cdoRevision, preparedStatement);
		}

		private void setResourceNodeValues(InternalCDORevision cdoRevision,
				PreparedStatement preparedStatement) throws SQLException {
			if (cdoRevision.isResourceNode()) {
				preparedStatement.setString(6, DBRevisionCacheUtil
						.getResourceNodeName(cdoRevision));
				CDOID containerID = (CDOID) cdoRevision.getContainerID();
				preparedStatement.setString(7, containerID.toURIFragment());
			} else {
				preparedStatement.setNull(6, Types.VARCHAR);
				preparedStatement.setNull(7, Types.INTEGER);
			}
		}

		@Override
		protected String getSqlStatement() {
			return "INSERT INTO " + DBRevisionCacheSchema.REVISIONS //
					+ " (" //
					+ DBRevisionCacheSchema.REVISIONS_ID //
					+ ", " + DBRevisionCacheSchema.REVISIONS_VERSION //
					+ ", " + DBRevisionCacheSchema.REVISIONS_CREATED //
					+ ", " + DBRevisionCacheSchema.REVISIONS_REVISED //
					+ ", " + DBRevisionCacheSchema.REVISIONS_CDOREVISION //
					+ ", " + DBRevisionCacheSchema.REVISIONS_RESOURCENODENAME //
					+ ", " + DBRevisionCacheSchema.REVISIONS_CONTAINERID //
					+ ") " //
					+ " VALUES (?, ?, ?, ?, ?, ? ,?)";
		}
	}

	private class UpdateRevisedStatementFactory extends
			AbstractPreparedStatementFactory<InternalCDORevision> {

		@Override
		protected String getSqlStatement() {

			return "UPDATE "
					+ DBRevisionCacheSchema.REVISIONS //
					+ " SET "
					+ DBRevisionCacheSchema.REVISIONS_REVISED
					+ " = ?" //
					+ " WHERE " + DBRevisionCacheSchema.REVISIONS_ID
					+ " = ?" //
					+ " AND " + DBRevisionCacheSchema.REVISIONS_VERSION
					+ " = ?";
		}

		@Override
		protected void doSetParameters(InternalCDORevision cdoRevision,
				PreparedStatement preparedStatement) throws Exception {
			preparedStatement.setLong(1, cdoRevision.getCreated() - 1);
			preparedStatement.setString(2, cdoRevision.getID().toURIFragment());
			preparedStatement.setInt(3, cdoRevision.getVersion() - 1);
		}
	}

	private class DeleteRevisionStatementFactory extends
			AbstractPreparedStatementFactory<InternalCDORevision> {
		@Override
		protected void doSetParameters(InternalCDORevision cdoRevision,
				PreparedStatement preparedStatement) throws Exception {
			preparedStatement.setString(1, cdoRevision.getID().toURIFragment());
			preparedStatement.setInt(2, cdoRevision.getVersion());
		}

		@Override
		protected String getSqlStatement() {
			return "DELETE FROM " + DBRevisionCacheSchema.REVISIONS //
					+ " WHERE " //
					+ " ID = ?" //
					+ " AND VERSION = ?";
		}
	}

	private class ClearStatementFactory extends
			AbstractPreparedStatementFactory<InternalCDORevision> {
		@Override
		protected String getSqlStatement() {
			return "DELETE FROM " //
					+ DBRevisionCacheSchema.REVISIONS; //
		}

		@Override
		protected void doSetParameters(InternalCDORevision t,
				PreparedStatement preparedStatement) throws Exception {
		}
	}

	/**
	 * Do before activate.
	 * 
	 * @throws Exception
	 *             the exception
	 */
	@Override
	protected void doBeforeActivate() throws Exception {
		super.doBeforeActivate();
		checkState(dataSource, "dataSource is null!"); //$NON-NLS-1$
		checkState(dbAdapter, "dbAdapter is null!"); //$NON-NLS-1$
		checkState(cdoIdProvider, "cdoIDProvider is null!"); //$NON-NLS-1$
		checkState(cdoIdObjectFactory, "cdoIdObjectFactory is null!"); //$NON-NLS-1$
		checkState(cdoListFactory, "cdoListFactory is null!"); //$NON-NLS-1$
		checkState(cdoPackageRegistry, "cdoPackageRegistry is null!"); //$NON-NLS-1$
		checkState(cdoRevisionResolver, "cdoRevisionResolver is null!"); //$NON-NLS-1$
	}

	/**
	 * Do activate.
	 * 
	 * @throws Exception
	 *             the exception
	 */
	@Override
	protected void doActivate() throws Exception {
		super.doActivate();

		insertRevisionStatementFactory = new InsertRevisionStatementFactory();
		updateRevisedStatementFactory = new UpdateRevisedStatementFactory();
		deleteRevisionStatementFactory = new DeleteRevisionStatementFactory();
		clearStatementFactory = new ClearStatementFactory();

		dbConnectionProvider = createDBConnectionProvider();
		createTable();
	}

	/**
	 * Do deactivate.
	 * 
	 * @throws Exception
	 *             the exception
	 */
	@Override
	protected void doDeactivate() throws Exception {
		try {
			updateRevisedStatementFactory.close();
			updateRevisedStatementFactory = null;
			insertRevisionStatementFactory.close();
			insertRevisionStatementFactory = null;
			deleteRevisionStatementFactory.close();
			deleteRevisionStatementFactory = null;
		} finally {
			DBUtil.close(connection);
			connection = null;
		}
		super.doDeactivate();
	}

	/**
	 * Creates the table.
	 * 
	 * @throws SQLException
	 *             the SQL exception
	 */
	private void createTable() throws SQLException {
		Connection connection = getConnection();
		DBRevisionCacheSchema.INSTANCE.create(dbAdapter, connection);
		CDOCommonDBUtil.commit(connection);
	}

	/**
	 * Adds a given revision to this cache. It furthermore updates the revised
	 * timestamp of the latest (before inserting the new one) revision
	 * 
	 * @param revision
	 *            the revision to add to this cache
	 * @return true, if successful
	 */
	public boolean addRevision(InternalCDORevision revision) {
		try {
			CDOCommonDBUtil
					.mandatoryInsertUpdate(insertRevisionStatementFactory
							.getPreparedStatement(revision, getConnection()));
			if (revision.getVersion() > 1) {
				// update former latest revision
				CDOCommonDBUtil.insertUpdate(updateRevisedStatementFactory
						.getPreparedStatement(revision, getConnection()));
			}

			return true;
		} catch (DBException ex) {
			throw ex;
		} catch (Exception ex) {
			throw new DBException(ex);
		}
	}

	/**
	 * Clear.
	 */
	public void clear() {
		try {
			CDOCommonDBUtil.insertUpdate(clearStatementFactory
					.getPreparedStatement(null, getConnection()));
			clearStatementFactory.getPreparedStatement(null, getConnection())
					.executeUpdate();
		} catch (DBException ex) {
			throw ex;
		} catch (Exception ex) {
			throw new DBException(ex);
		}
	}

	/**
	 * Gets the object type.
	 * 
	 * @param id
	 *            the id
	 * @return the object type
	 */
	public EClass getObjectType(CDOID id) {
		return null;
	}

	/**
	 * Gets the {@link CDOID} of a resource within this cache. The resource is
	 * picked if it matches the given folderID, name and timestamp
	 * 
	 * @param folderID
	 *            the folder id
	 * @param name
	 *            the name
	 * @param timeStamp
	 *            the time stamp
	 * 
	 * @return the resource id
	 */
	public CDOID getResourceID(CDOID folderID, String name, long timeStamp) {

		StringBuilder sb = new StringBuilder();
		sb.append(DBRevisionCacheSchema.REVISIONS_ID).append(" = ")//
				.append(folderID.toURIFragment()); // 
		sb.append(" AND ");
		appendTimestampCondition(timeStamp, sb);
		sb.append(" AND ");
		sb.append(DBRevisionCacheSchema.REVISIONS_RESOURCENODENAME) //
				.append(" = ").append("'").append(name).append("'");

		InternalCDORevision cdoRevision = selectCDORevision(sb.toString());

		if (cdoRevision != null) {
			return cdoRevision.getID();
		} else {
			return null;
		}
	}

	/**
	 * Gets the revision with the highest version for a given {@link CDOID}.
	 * 
	 * @param id
	 *            the id
	 * @return the revision
	 */
	public InternalCDORevision getRevision(CDOID id) {
		StringBuilder sb = new StringBuilder();
		sb.append("id = ").append(id.toURIFragment());
		sb.append(" AND ");
		sb.append(DBRevisionCacheSchema.REVISIONS_REVISED).append("=").append(
				CDORevision.UNSPECIFIED_DATE);
		return selectCDORevision(sb.toString());

	}

	/**
	 * Gets an {@link InternalCDORevision} that matches the given timestamp (it
	 * is >= created timestamp AND <= revised timestamp of the revision).
	 * 
	 * @param id
	 *            the id
	 * @param timeStamp
	 *            the time stamp
	 * 
	 * @return the revision by time
	 */
	public InternalCDORevision getRevisionByTime(CDOID id, long timeStamp) {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append(DBRevisionCacheSchema.REVISIONS_ID).append(" = ")
				.append(id.toURIFragment());
		stringBuilder.append(" AND ");
		appendTimestampCondition(timeStamp, stringBuilder);

		return selectCDORevision(stringBuilder.toString());
	}

	public static StringBuilder appendTimestampCondition(long timeStamp,
			StringBuilder stringBuilder) {
		stringBuilder.append(DBRevisionCacheSchema.REVISIONS_CREATED);
		stringBuilder.append(" <= ");
		stringBuilder.append(timeStamp);
		stringBuilder.append(" AND");
		stringBuilder.append(" ( ");
		stringBuilder.append(DBRevisionCacheSchema.REVISIONS_REVISED);
		stringBuilder.append(" >= ");
		stringBuilder.append(timeStamp);
		stringBuilder.append(" OR " + DBRevisionCacheSchema.REVISIONS_REVISED);
		stringBuilder.append(" = " + CDORevision.UNSPECIFIED_DATE);
		stringBuilder.append(" )");
		return stringBuilder;
	}

	private InternalCDORevision selectCDORevision(String sql) {
		final InternalCDORevision[] cdoRevisionArray = new InternalCDORevision[1];
		IDBRowHandler rowHandler = new IDBRowHandler() {
			public boolean handle(int row, final Object... values) {
				try {
					DBRevisionCacheUtil
							.assertIsNull(
									cdoRevisionArray[0],
									"database inconsistent: there's more than 1 revision with the same id and timestamp!");
					cdoRevisionArray[0] = toCDORevision(values[0], values[1]);
				} catch (IOException ex) {
					TRACER.format(
							"error reading CDORevision from database \"{0}\"!",
							ex);
				}
				return true;
			}
		};

		DBUtil.select(connection//
				, rowHandler //
				, sql //
				, DBRevisionCacheSchema.REVISIONS_CDOREVISION //
				, DBRevisionCacheSchema.REVISIONS_REVISED);
		return cdoRevisionArray[0];
	}

	/**
	 * Gets the revision by version.
	 * 
	 * @param id
	 *            the id
	 * @param version
	 *            the version
	 * @return the revision by version
	 */
	public InternalCDORevision getRevisionByVersion(CDOID id, int version) {
		final InternalCDORevision[] cdoRevisionArray = new InternalCDORevision[1];
		IDBRowHandler rowHandler = new IDBRowHandler() {
			public boolean handle(int row, final Object... values) {
				try {
					DBRevisionCacheUtil
							.assertIsNull(cdoRevisionArray[0],
									"database inconsistent: there's more than 1 revision with the same version!");
					cdoRevisionArray[0] = toCDORevision(values[0], values[1]);
				} catch (IOException ex) {
					TRACER.format(
							"error reading CDORevision from database \"{0}\"!",
							ex);
				}
				return true;
			}
		};

		String uriFragment = id.toURIFragment();
		DBUtil.select(getConnection() //
				, rowHandler //
				, "id = " + uriFragment + " AND VERSION = " + version //
				, DBRevisionCacheSchema.REVISIONS_CDOREVISION //
				, DBRevisionCacheSchema.REVISIONS_REVISED);
		return cdoRevisionArray[0];
	}

	/**
	 * Gets the latest revisions of all persisted model versions.
	 * 
	 * @return the revisions
	 */
	public List<CDORevision> getRevisions() {
		final List<CDORevision> cdoRevisionList = new ArrayList<CDORevision>();
		IDBRowHandler rowHandler = new IDBRowHandler() {
			public boolean handle(int row, final Object... values) {
				try {
					cdoRevisionList.add(toCDORevision((byte[]) values[0]));
				} catch (IOException ex) {
					TRACER.format(
							"error reading CDORevision from database \"{0}\"!",
							ex);
				}
				return true;
			}
		};

		DBUtil.select(getConnection() //
				, rowHandler //
				, DBRevisionCacheSchema.REVISIONS_REVISED + " = "
						+ CDORevision.UNSPECIFIED_DATE //
				, DBRevisionCacheSchema.REVISIONS_CDOREVISION);
		return cdoRevisionList;
	}

	/**
	 * Removes a revision by its Id and version. If the given revision does not
	 * exist <tt>null</tt> is returned. Otherwise the
	 * {@link InternalCDORevision}, that was removed is returned
	 * 
	 * @param id
	 *            the id of the revision to remove
	 * @param version
	 *            the version of the revision to remove
	 * @return the {@link InternalCDORevision} that was remove, <tt>null</tt>
	 *         otherwise
	 */
	public InternalCDORevision removeRevision(CDOID id, int version) {
		try {
			InternalCDORevision cdoRevision = getRevisionByVersion(id, version);
			if (cdoRevision != null) {
				CDOCommonDBUtil
						.mandatoryInsertUpdate(deleteRevisionStatementFactory
								.getPreparedStatement(cdoRevision,
										getConnection()));
			}
			return cdoRevision;
		} catch (DBException ex) {
			throw ex;
		} catch (Exception ex) {
			throw new DBException(ex);
		}
	}

	/**
	 * Creates a {@link IDBConnectionProvider}
	 * 
	 * @return the {@link IDBConnectionProvider}
	 */
	private IDBConnectionProvider createDBConnectionProvider() {
		return DBUtil.createConnectionProvider(dataSource);
	}

	/**
	 * Gets the {@link Connection}. If a connection was already created in a
	 * earlyer call, this connection is reused, Otherwise a new one is created
	 * 
	 * @return the connection
	 * @throws DBException
	 *             if an error occured while trying to create a connection
	 */
	protected Connection getConnection() {
		try {
			if (connection == null || connection.isClosed()) {
				connection = CDOCommonDBUtil
						.assertIsNotNull(dbConnectionProvider.getConnection());
				connection.setAutoCommit(false);
			}
			return connection;

		} catch (SQLException ex) {
			throw new DBException(ex);
		}
	}

	/**
	 * Converts a given {@link CDORevision} to an array of bytes
	 * 
	 * @param revision
	 *            the revision
	 * @return the bytes
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	private byte[] toBytes(CDORevision revision) throws IOException {
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		CDODataOutput cdoDataOutput = new CDODataOutputImpl(
				ExtendedDataOutputStream.wrap(byteArrayOutputStream)) {
			public CDOIDProvider getIDProvider() {
				return cdoIdProvider;
			}
		};

		cdoDataOutput.writeCDORevision(revision, CDORevision.UNCHUNKED);
		return byteArrayOutputStream.toByteArray();
	}

	/**
	 * Converts the given Objects to an {@link InternalCDORevision}. The object
	 * is deserialized to an instance of the correct type and the revised
	 * timestamp is set separatley. Whe you insert a new revision into this
	 * cache, the former latest revision gets a new revised timestamp. This
	 * timestamp's only updated in the database column 'revised', not in the
	 * blob that holds the serialized instance. Therefore the revised timestamp
	 * has to be set separately
	 * 
	 * @param revisedTimestampObject
	 *            the value
	 * @param cdoInstanceData
	 *            the cdo instance data
	 * 
	 * @return the internal cdo revision
	 * 
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	private InternalCDORevision toCDORevision(Object cdoInstanceData,
			Object revisedTimestampObject) throws IOException {
		CheckUtil.checkArg(cdoInstanceData instanceof byte[],
				"cdoInstanceDataArray is not of type byte[]");
		InternalCDORevision cdoRevision = toCDORevision((byte[]) cdoInstanceData);
		CheckUtil.checkArg(revisedTimestampObject instanceof Long,
				"revisedTimestampObject ins not of type Long");
		// revised timestamp's updated in the revised column only (not in the
		// blob)
		cdoRevision.setRevised((Long) revisedTimestampObject);
		return cdoRevision;
	}

	/**
	 * Converts a given byteArray to a CDORevision
	 * 
	 * @param byteArray
	 *            the byte array
	 * @return the cDO revision
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	private InternalCDORevision toCDORevision(byte[] byteArray)
			throws IOException {
		CDODataInput cdoDataInput = new CDODataInputImpl(
				ExtendedDataInputStream
						.wrap(new ByteArrayInputStream(byteArray))) {
			@Override
			protected CDOIDObjectFactory getIDFactory() {
				return cdoIdObjectFactory;
			}

			@Override
			protected CDOListFactory getListFactory() {
				return cdoListFactory;
			}

			@Override
			protected CDOPackageRegistry getPackageRegistry() {
				return cdoPackageRegistry;
			}

			@Override
			protected CDORevisionResolver getRevisionResolver() {
				return cdoRevisionResolver;
			}
		};
		return (InternalCDORevision) cdoDataInput.readCDORevision();
	}

	/**
	 * Sets the cDOID provider.
	 * 
	 * @param cdoIDProvider
	 *            the new cDOID provider
	 */
	public void setCDOIDProvider(CDOIDProvider cdoIDProvider) {
		cdoIdProvider = cdoIDProvider;
	}

	public void setCdoIdObjectFactory(CDOIDObjectFactory cdoIdObjectFactory) {
		this.cdoIdObjectFactory = cdoIdObjectFactory;
	}

	public void setCdoListFactory(CDOListFactory cdoListFactory) {
		this.cdoListFactory = cdoListFactory;
	}

	public void setCdoPackageRegistry(CDOPackageRegistry cdoPackageRegistry) {
		this.cdoPackageRegistry = cdoPackageRegistry;
	}

	public void setCdoRevisionResolver(CDORevisionResolver cdoRevisionResolver) {
		this.cdoRevisionResolver = cdoRevisionResolver;
	}

	/**
	 * Sets the data source.
	 * 
	 * @param dataSource
	 *            the new data source
	 */
	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	/**
	 * Sets the dB adapter.
	 * 
	 * @param dbAdapter
	 *            the new dB adapter
	 */
	public void setDBAdapter(IDBAdapter dbAdapter) {
		this.dbAdapter = dbAdapter;
	}
}