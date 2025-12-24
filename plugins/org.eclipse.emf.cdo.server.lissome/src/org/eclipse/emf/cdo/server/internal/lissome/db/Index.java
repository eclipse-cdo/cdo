/*
 * Copyright (c) 2012, 2013, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.server.internal.lissome.db;

import org.eclipse.emf.cdo.common.CDOCommonRepository.IDGenerationLocation;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDUtil;
import org.eclipse.emf.cdo.server.internal.lissome.LissomeStore;
import org.eclipse.emf.cdo.spi.server.InternalRepository;

import org.eclipse.net4j.db.DBException;
import org.eclipse.net4j.db.DBUtil;
import org.eclipse.net4j.db.IDBAdapter;
import org.eclipse.net4j.db.IDBConnectionProvider;
import org.eclipse.net4j.db.ddl.IDBSchema;
import org.eclipse.net4j.db.h2.H2Adapter;
import org.eclipse.net4j.spi.db.DBAdapter;
import org.eclipse.net4j.spi.db.ddl.InternalDBSchema;
import org.eclipse.net4j.util.io.IOUtil;
import org.eclipse.net4j.util.om.trace.ContextTracer;

import org.h2.jdbcx.JdbcDataSource;

import javax.sql.DataSource;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author Eike Stepper
 */
public class Index implements IDBConnectionProvider
{
  public static final long NULL_POINTER = 0; // A pointer value that has no meaning in the vob.

  private static final boolean DEBUG = false;

  private static final String INDENT = "       ";

  private LissomeStore store;

  private IDBSchema schema = DBUtil.createSchema(LissomeStore.TYPE);

  private IDBAdapter adapter;

  private DataSource dataSource;

  private IndexWriter writer;

  protected CommitInfosTable commitInfos;

  protected ObjectsTable objects;

  protected BranchesTable branches;

  public Index(LissomeStore store)
  {
    this.store = store;

    adapter = createAdapter();
    dataSource = createDataSource();
    writer = createWriter();

    commitInfos = new CommitInfosTable(this);
    objects = new ObjectsTable(this);
    if (isSupportingBranches())
    {
      branches = new BranchesTable(this);
    }

    ((InternalDBSchema)schema).lock();
  }

  public LissomeStore getStore()
  {
    return store;
  }

  public InternalRepository getRepository()
  {
    return store.getRepository();
  }

  public IDGenerationLocation getIDGenerationLocation()
  {
    return getRepository().getIDGenerationLocation();
  }

  public boolean isSupportingAudits()
  {
    return getRepository().isSupportingAudits();
  }

  public boolean isSupportingBranches()
  {
    return getRepository().isSupportingBranches();
  }

  public IndexWriter getWriter()
  {
    return writer;
  }

  public CDOID getCDOID(ResultSet resultSet, int column) throws SQLException
  {
    if (getIDGenerationLocation() == IDGenerationLocation.CLIENT)
    {
      byte[] value = resultSet.getBytes(column);
      return CDOIDUtil.createUUID(value);
    }

    long value = resultSet.getLong(column);
    return CDOIDUtil.createLong(value);
  }

  public void setCDOID(PreparedStatement statement, int column, CDOID id) throws SQLException
  {
    if (getIDGenerationLocation() == IDGenerationLocation.CLIENT)
    {
      byte[] value = CDOIDUtil.getByteArray(id);
      statement.setBytes(column, value);
    }
    else
    {
      long value = CDOIDUtil.getLong(id);
      statement.setLong(column, value);
    }
  }

  protected IDBAdapter createAdapter()
  {
    return new H2Adapter();
  }

  protected DataSource createDataSource()
  {
    String repoName = getRepository().getName();
    return createDataSource(store.getFolder(), repoName, repoName);
  }

  protected IndexWriter createWriter()
  {
    return new IndexWriter(this);
  }

  public IndexReader createReader()
  {
    return new IndexReader(this);
  }

  public void createTables()
  {
    Connection connection = writer.getConnection();
    schema.create(adapter, connection);
  }

  public IDBSchema getSchema()
  {
    return schema;
  }

  @Override
  public Connection getConnection()
  {
    try
    {
      return dataSource.getConnection();
    }
    catch (SQLException ex)
    {
      throw new DBException(ex);
    }
  }

  public void trace(ContextTracer tracer, PreparedStatement stmt)
  {
    if (DEBUG)
    {
      IOUtil.OUT().println(format(stmt));
    }
    else if (tracer.isEnabled())
    {
      tracer.trace(format(stmt));
    }
  }

  private String format(PreparedStatement stmt)
  {
    if (adapter instanceof DBAdapter)
    {
      return ((DBAdapter)adapter).format(stmt);
    }

    return stmt.toString();
  }

  public void trace(ContextTracer tracer, ResultSet resultSet)
  {
    if (DEBUG)
    {
      IOUtil.OUT().println(format(resultSet));
    }
    else if (tracer.isEnabled())
    {
      tracer.trace(format(resultSet));
    }
  }

  private String format(ResultSet resultSet)
  {
    if (adapter instanceof DBAdapter)
    {
      return INDENT + ((DBAdapter)adapter).format(resultSet);
    }

    return INDENT + resultSet;
  }

  public static DataSource createDataSource(File folder, String dbName, String schemaName)
  {
    File database = new File(new File(folder, "db"), dbName);
    String schema = schemaName != null ? ";SCHEMA=" + schemaName : "";

    JdbcDataSource dataSource = new JdbcDataSource();
    dataSource.setURL("jdbc:h2:" + database.getAbsolutePath() + schema);
    return dataSource;
  }
}
