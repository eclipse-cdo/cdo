/*
 * Copyright (c) 2009-2013, 2016, 2019, 2021 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Stefan Winkler - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.db.verifier;

import org.eclipse.emf.cdo.common.model.EMFUtil;
import org.eclipse.emf.cdo.server.IRepository;
import org.eclipse.emf.cdo.server.IStore;
import org.eclipse.emf.cdo.server.db.IDBStore;
import org.eclipse.emf.cdo.server.db.IDBStoreAccessor;
import org.eclipse.emf.cdo.server.db.mapping.IClassMapping;
import org.eclipse.emf.cdo.server.db.mapping.IListMapping;
import org.eclipse.emf.cdo.server.internal.db.mapping.horizontal.HorizontalAuditClassMapping;
import org.eclipse.emf.cdo.server.internal.db.mapping.horizontal.HorizontalAuditMappingStrategy;
import org.eclipse.emf.cdo.server.internal.db.mapping.horizontal.HorizontalNonAuditClassMapping;
import org.eclipse.emf.cdo.server.internal.db.mapping.horizontal.HorizontalNonAuditMappingStrategy;
import org.eclipse.emf.cdo.server.internal.db.mapping.horizontal.MappingNames;
import org.eclipse.emf.cdo.spi.common.model.InternalCDOPackageInfo;
import org.eclipse.emf.cdo.spi.common.model.InternalCDOPackageRegistry;
import org.eclipse.emf.cdo.tests.db.bundle.OM;

import org.eclipse.net4j.db.DBUtil;
import org.eclipse.net4j.util.collection.Pair;
import org.eclipse.net4j.util.om.trace.ContextTracer;

import org.eclipse.emf.ecore.EClass;

import org.junit.Assert;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import junit.framework.AssertionFailedError;

/**
 * @author Stefan Winkler
 */
public abstract class DBStoreVerifier extends Assert
{
  protected static final ContextTracer TRACER = new ContextTracer(OM.DEBUG, DBStoreVerifier.class);

  private IRepository repository;

  private IDBStoreAccessor accessor;

  public DBStoreVerifier(IRepository repository)
  {
    this.repository = repository;
    if (repository != null)
    {
      assertEquals(true, repository.getStore() instanceof IDBStore);
    }
  }

  protected IRepository getRepository()
  {
    return repository;
  }

  protected IDBStore getStore()
  {
    return (IDBStore)repository.getStore();
  }

  protected Statement getStatement()
  {
    if (accessor == null)
    {
      accessor = (IDBStoreAccessor)repository.getStore().getReader(null);
    }

    try
    {
      return accessor.getDBConnection().createStatement();
    }
    catch (SQLException ex)
    {
      ex.printStackTrace();
      return null;
    }
  }

  protected Connection getConnection() throws SQLException
  {
    return getStatement().getConnection();
  }

  protected DatabaseMetaData getMetaData() throws SQLException
  {
    return getConnection().getMetaData();
  }

  protected Object[] getColumnMetaData(String expectedTableName, String expectedColumnName) throws SQLException
  {
    ResultSet rset = getMetaData().getColumns(null, null, null, null);

    try
    {
      while (rset.next())
      {
        String tableName = rset.getString(3);
        String columnName = rset.getString(4);
        if (DBUtil.equalNames(expectedTableName, tableName, false) && DBUtil.equalNames(expectedColumnName, columnName, false))
        {
          return new Object[] { null, //
              rset.getString(1), rset.getString(2), tableName, columnName, rset.getInt(5), rset.getString(6), rset.getInt(7) };
        }
      }
    }
    finally
    {
      DBUtil.close(rset);
    }

    return null;
  }

  protected String getTableName(String expectedTableName) throws SQLException
  {
    ResultSet rset = getMetaData().getTables(null, null, null, null);

    try
    {
      while (rset.next())
      {
        String actualTableName = rset.getString(3);
        if (DBUtil.equalNames(expectedTableName, actualTableName, false))
        {
          return actualTableName;
        }
      }
    }
    finally
    {
      DBUtil.close(rset);
    }

    return null;
  }

  protected List<IClassMapping> getClassMappings()
  {
    ArrayList<IClassMapping> result = new ArrayList<>();
    InternalCDOPackageRegistry packageRegistry = (InternalCDOPackageRegistry)repository.getPackageRegistry();
    for (InternalCDOPackageInfo packageInfo : packageRegistry.getPackageInfos())
    {
      // CDO core package is not mapped in horizontal mapping
      if (!packageInfo.isCorePackage())
      {
        for (EClass cls : EMFUtil.getPersistentClasses(packageInfo.getEPackage()))
        {
          result.add(getStore().getMappingStrategy().getClassMapping(cls));
        }
      }
    }

    return result;
  }

  protected void cleanUp()
  {
    if (accessor != null)
    {
      accessor.release();
    }
  }

  public void verify() throws VerificationException
  {
    try
    {
      if (TRACER.isEnabled())
      {
        TRACER.format("Starting {0}...", getClass().getSimpleName());
      }

      doVerify();

      if (TRACER.isEnabled())
      {
        TRACER.format("{0} completed without complaints...", getClass().getSimpleName());
      }
    }
    catch (Exception e)
    {
      throw new VerificationException(e);
    }
    finally
    {
      cleanUp();
    }
  }

  protected void sqlDump(String sql)
  {
    ResultSet rs = null;

    try
    {
      TRACER.format("Dumping output of {0}", sql);
      rs = getStatement().executeQuery(sql);
      int numCol = rs.getMetaData().getColumnCount();

      StringBuilder row = new StringBuilder();
      for (int c = 1; c <= numCol; c++)
      {
        row.append(String.format("%10s | ", rs.getMetaData().getColumnLabel(c)));
      }

      TRACER.trace(row.toString());

      row = new StringBuilder();
      for (int c = 1; c <= numCol; c++)
      {
        row.append("-----------+--");
      }

      TRACER.trace(row.toString());

      while (rs.next())
      {
        row = new StringBuilder();
        for (int c = 1; c <= numCol; c++)
        {
          row.append(String.format("%10s | ", rs.getString(c)));
        }

        TRACER.trace(row.toString());
      }

      row = new StringBuilder();
      for (int c = 1; c <= numCol; c++)
      {
        row.append("-----------+-");
      }

      TRACER.trace(row.toString());
    }
    catch (SQLException ex)
    {
      // NOP
    }
    finally
    {
      if (rs != null)
      {
        try
        {
          rs.close();
        }
        catch (SQLException ex)
        {
          // NOP
        }
      }
    }
  }

  protected abstract void doVerify() throws Exception;

  /**
   * @author Stefan Winkler
   */
  public static class VerificationException extends RuntimeException
  {
    private static final long serialVersionUID = 1L;

    public VerificationException(String message)
    {
      super(message);
    }

    public VerificationException(String message, Throwable t)
    {
      super(message, t);
    }

    public VerificationException(Throwable t)
    {
      super(t);
    }
  }

  /**
   * @author Stefan Winkler
   */
  public static class Audit extends DBStoreVerifier
  {
    public Audit(IRepository repo)
    {
      super(repo);

      // this is a verifier for auditing mode
      assertEquals(true, getStore().getMappingStrategy() instanceof HorizontalAuditMappingStrategy);
    }

    @Override
    protected void doVerify() throws Exception
    {
      for (IClassMapping mapping : getClassMappings())
      {
        if (mapping != null && mapping.getDBTables() != null)
        {
          verifyClassMapping(mapping);
        }
      }
    }

    private void verifyClassMapping(IClassMapping mapping) throws Exception
    {
      verifyAtMostOneUnrevised(mapping);
      verifyUniqueIdVersion(mapping);
      verifyReferences(mapping);
    }

    private void verifyAtMostOneUnrevised(IClassMapping mapping) throws Exception
    {
      String tableName = mapping.getDBTables().iterator().next().getName();
      TRACER.format("verifyAtMostOneUnrevised: {0} ...", tableName);

      String sql = "SELECT " + MappingNames.ATTRIBUTES_ID + ", count(1) FROM " + tableName + " WHERE " + MappingNames.ATTRIBUTES_REVISED + "= 0 GROUP BY "
          + MappingNames.ATTRIBUTES_ID;
      TRACER.format("  Executing SQL: {0} ", sql);

      ResultSet resultSet = getStatement().executeQuery(sql);
      try
      {
        while (resultSet.next())
        {
          assertEquals("Multiple unrevised rows for ID " + resultSet.getLong(1), true, resultSet.getInt(2) <= 1);
        }
      }
      finally
      {
        resultSet.close();
      }
    }

    /**
     * Verify that the pair (id,version) is unique.
     */
    private void verifyUniqueIdVersion(IClassMapping mapping) throws Exception
    {
      String tableName = mapping.getDBTables().iterator().next().getName();
      TRACER.format("verifyUniqueIdVersion: {0} ...", tableName);

      String sql = "SELECT " + MappingNames.ATTRIBUTES_ID + "," + MappingNames.ATTRIBUTES_VERSION + ", count(1) FROM " + tableName + " GROUP BY "
          + MappingNames.ATTRIBUTES_ID + "," + MappingNames.ATTRIBUTES_VERSION;

      TRACER.format("  Executing SQL: {0} ", sql);

      ResultSet resultSet = getStatement().executeQuery(sql);
      try
      {
        while (resultSet.next())
        {
          assertEquals("Multiple rows for ID " + resultSet.getLong(1) + "v" + resultSet.getInt(2), true, resultSet.getInt(3) <= 1);
        }
      }
      catch (AssertionFailedError e)
      {
        TRACER.trace(e.getMessage());
        sqlDump("SELECT * FROM " + tableName + " WHERE " + MappingNames.ATTRIBUTES_REVISED + "=0");
        throw e;
      }
      finally
      {
        resultSet.close();
      }
    }

    private void verifyReferences(IClassMapping mapping) throws Exception
    {
      List<IListMapping> listMappings = ((HorizontalAuditClassMapping)mapping).getListMappings();
      if (listMappings == null)
      {
        return;
      }

      String tableName = mapping.getDBTables().iterator().next().getName();
      String sql = "SELECT " + MappingNames.ATTRIBUTES_ID + ", " + MappingNames.ATTRIBUTES_VERSION + " FROM " + tableName;

      ArrayList<Pair<Long, Integer>> idVersions = new ArrayList<>();

      ResultSet resultSet = getStatement().executeQuery(sql);
      try
      {
        while (resultSet.next())
        {
          idVersions.add(Pair.create(resultSet.getLong(1), resultSet.getInt(2)));
        }
      }
      finally
      {
        resultSet.close();
      }

      for (IListMapping listMapping : listMappings)
      {
        for (Pair<Long, Integer> idVersion : idVersions)
        {
          verifyCorrectIndices(listMapping, idVersion.getElement1(), idVersion.getElement2());
        }
      }
    }

    private void verifyCorrectIndices(IListMapping refMapping, long id, int version) throws Exception
    {
      String tableName = refMapping.getDBTables().iterator().next().getName();

      TRACER.format("verifyUniqueIdVersion: {0} for ID{1}v{2} ...", tableName, id, version);

      String sql = "SELECT " + MappingNames.LIST_IDX + " FROM " + tableName + " WHERE " + MappingNames.LIST_REVISION_ID + "=" + id + " AND "
          + MappingNames.LIST_REVISION_VERSION + "=" + version + " ORDER BY " + MappingNames.LIST_IDX;

      TRACER.format("  Executing SQL: {0} ", sql);

      ResultSet resultSet = getStatement().executeQuery(sql);
      int indexShouldBe = 0;

      try
      {
        while (resultSet.next())
        {
          assertEquals("Index " + indexShouldBe + " missing for ID" + id + "v" + version, indexShouldBe++, resultSet.getInt(1));
        }
      }
      catch (AssertionFailedError e)
      {
        sqlDump("SELECT * FROM " + tableName + " WHERE " + MappingNames.LIST_REVISION_ID + "=" + id + " AND " + MappingNames.LIST_REVISION_VERSION + "="
            + version + " ORDER BY " + MappingNames.LIST_IDX);
        throw e;
      }
      finally
      {
        resultSet.close();
      }
    }
  }

  /**
   * @author Stefan Winkler
   */
  public static class NonAudit extends DBStoreVerifier
  {
    public NonAudit(IRepository repo)
    {
      super(repo);

      // this is a verifier for non-auditing mode
      assertEquals(true, getStore().getRevisionTemporality() == IStore.RevisionTemporality.NONE);
      // ... and for horizontal class mapping
      assertEquals(true, getStore().getMappingStrategy() instanceof HorizontalNonAuditMappingStrategy);
    }

    @Override
    protected void doVerify() throws Exception
    {
      for (IClassMapping mapping : getClassMappings())
      {
        if (mapping != null && mapping.getDBTables().size() > 0)
        {
          verifyClassMapping(mapping);
        }
      }
    }

    private void verifyClassMapping(IClassMapping mapping) throws Exception
    {
      verifyNoUnrevisedRevisions(mapping);
      verifyUniqueId(mapping);
      verifyReferences(mapping);
    }

    /**
     * Verify that there is no row with cdo_revised == 0.
     */
    private void verifyNoUnrevisedRevisions(IClassMapping mapping) throws Exception
    {
      String tableName = mapping.getDBTables().iterator().next().getName();
      String sql = "SELECT count(1) FROM " + tableName + " WHERE " + MappingNames.ATTRIBUTES_REVISED + " <> 0";
      ResultSet resultSet = getStatement().executeQuery(sql);
      try
      {
        assertEquals(true, resultSet.next());
        assertEquals("Revised revision in table " + tableName, 0, resultSet.getInt(1));
      }
      finally
      {
        resultSet.close();
      }
    }

    /**
     * Verify that the id is unique.
     */
    private void verifyUniqueId(IClassMapping mapping) throws Exception
    {
      String tableName = mapping.getDBTables().iterator().next().getName();
      String sql = "SELECT " + MappingNames.ATTRIBUTES_ID + ", count(1) FROM " + tableName + " GROUP BY " + MappingNames.ATTRIBUTES_ID;

      ResultSet resultSet = getStatement().executeQuery(sql);

      try
      {
        while (resultSet.next())
        {
          assertEquals("Multiple rows for ID " + resultSet.getLong(1), 1, resultSet.getInt(2));
        }
      }
      finally
      {
        resultSet.close();
      }
    }

    private void verifyReferences(IClassMapping mapping) throws Exception
    {
      List<IListMapping> referenceMappings = ((HorizontalNonAuditClassMapping)mapping).getListMappings();
      if (referenceMappings == null)
      {
        return;
      }

      String tableName = mapping.getDBTables().iterator().next().getName();
      String sql = "SELECT " + MappingNames.ATTRIBUTES_ID + ", " + MappingNames.ATTRIBUTES_VERSION + " FROM " + tableName;

      ArrayList<Pair<Long, Integer>> idVersions = new ArrayList<>();

      ResultSet resultSet = getStatement().executeQuery(sql);
      try
      {
        while (resultSet.next())
        {
          idVersions.add(Pair.create(resultSet.getLong(1), resultSet.getInt(2)));
        }
      }
      finally
      {
        resultSet.close();
      }

      for (IListMapping refMapping : referenceMappings)
      {
        for (Pair<Long, Integer> idVersion : idVersions)
        {
          verifyCorrectIndices(refMapping, idVersion.getElement1());
        }
      }
    }

    private void verifyCorrectIndices(IListMapping refMapping, long id) throws Exception
    {
      String tableName = refMapping.getDBTables().iterator().next().getName();
      String sql = "SELECT " + MappingNames.LIST_IDX + " FROM " + tableName + " WHERE " + MappingNames.LIST_REVISION_ID + "=" + id + " ORDER BY "
          + MappingNames.LIST_IDX;

      ResultSet resultSet = getStatement().executeQuery(sql);
      int indexShouldBe = 0;
      try
      {
        while (resultSet.next())
        {
          assertEquals("Index " + indexShouldBe + " missing for ID" + id, indexShouldBe++, resultSet.getInt(1));
        }
      }
      finally
      {
        resultSet.close();
      }
    }
  }
}
