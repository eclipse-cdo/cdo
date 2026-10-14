/*
 * Copyright (c) 2010-2013, 2018, 2019, 2021, 2023, 2025 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Stefan Winkler - 271444: [DB] Multiple refactorings bug 271444
 *    Stefan Winkler - derived branch mapping from audit mapping
 */
package org.eclipse.emf.cdo.server.internal.db.mapping.horizontal;

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.server.db.IDBStoreAccessor;
import org.eclipse.emf.cdo.server.db.IIDHandler;
import org.eclipse.emf.cdo.server.db.mapping.IBranchDeletionSupport;
import org.eclipse.emf.cdo.server.db.mapping.IMappingStrategy;

import org.eclipse.net4j.db.Batch;
import org.eclipse.net4j.db.DBException;
import org.eclipse.net4j.db.DBType;
import org.eclipse.net4j.db.DBUtil;
import org.eclipse.net4j.db.IDBAdapter;
import org.eclipse.net4j.db.IDBPreparedStatement;
import org.eclipse.net4j.db.IDBPreparedStatement.ReuseProbability;
import org.eclipse.net4j.db.ddl.IDBField;
import org.eclipse.net4j.db.ddl.IDBTable;
import org.eclipse.net4j.db.dml.SQL;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EStructuralFeature;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

/**
 * This is a list-table mapping for audit mode. It has ID and version columns and no delta support.
 *
 * @author Eike Stepper
 * @author Stefan Winkler
 * @since 3.0
 */
public class BranchingListTableMapping extends AbstractListTableMapping implements IBranchDeletionSupport
{
  private IDBField branchField;

  private IDBField versionField;

  private String sqlClear;

  public BranchingListTableMapping(IMappingStrategy mappingStrategy, EClass eClass, EStructuralFeature feature)
  {
    super(mappingStrategy, eClass, feature);
  }

  @Override
  protected void initSQLStrings()
  {
    super.initSQLStrings();

    IDBTable table = getTable();
    branchField = table.getField(MappingNames.LIST_REVISION_BRANCH);
    versionField = table.getField(MappingNames.LIST_REVISION_VERSION);

    // ----------- clear list -------------------------
    sqlClear = SQL.deleteFrom(table) //
        .where(SQL.eq(sourceField, SQL.param()) //
            .and(SQL.eq(branchField, SQL.param())) //
            .and(SQL.eq(versionField, SQL.param()))) //
        .compile(getDBAdapter()).getSQL();
  }

  @Override
  protected void addKeyFields(List<FieldInfo> list)
  {
    list.add(new FieldInfo(MappingNames.LIST_REVISION_BRANCH, DBType.INTEGER));
    list.add(new FieldInfo(MappingNames.LIST_REVISION_VERSION, DBType.INTEGER));
  }

  @Override
  protected void setKeyFields(PreparedStatement stmt, CDORevision revision) throws SQLException
  {
    IIDHandler idHandler = getStore().getIDHandler();
    idHandler.setCDOID(stmt, 1, revision.getID());
    stmt.setInt(2, revision.getBranch().getID());
    stmt.setInt(3, revision.getVersion());
  }

  @Override
  public void objectDetached(IDBStoreAccessor accessor, CDOID id, long revised)
  {
    // the audit list mapping does not care about revised references -> NOP
  }

  @Override
  public void rawDeleted(IDBStoreAccessor accessor, CDOID id, CDOBranch branch, int version)
  {
    if (getTable() == null)
    {
      initTable(accessor);
    }

    IIDHandler idHandler = getStore().getIDHandler();
    IDBPreparedStatement stmt = accessor.getDBConnection().prepareStatement(sqlClear, ReuseProbability.HIGH);

    try
    {
      idHandler.setCDOID(stmt, 1, id);
      stmt.setInt(2, branch.getID());
      stmt.setInt(3, version);
      DBUtil.update(stmt, false);
    }
    catch (SQLException e)
    {
      throw new DBException(e);
    }
    finally
    {
      DBUtil.close(stmt);
    }
  }

  @Override
  public void deleteBranches(IDBStoreAccessor accessor, Batch batch, String idList)
  {
    IDBTable table = getTable();
    if (table == null)
    {
      return;
    }

    IDBAdapter dbAdapter = getDBAdapter();
    String sql = SQL.deleteFrom(table) //
        .where(SQL.fragment() //
            .column(branchField) //
            .sql(" IN (") //
            .sql(idList) //
            .sql(")") //
            .build().condition()) //
        .compile(dbAdapter).getSQL();
    batch.add(sql);
  }
}
