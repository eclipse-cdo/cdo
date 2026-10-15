/*
 * Copyright (c) 2009-2013, 2018, 2019, 2023, 2025 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Stefan Winkler - 271444: [DB] Multiple refactorings bug 271444
 */
package org.eclipse.emf.cdo.server.internal.db.mapping.horizontal;

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.server.db.IDBStoreAccessor;
import org.eclipse.emf.cdo.server.db.mapping.IMappingStrategy;

import org.eclipse.net4j.db.DBException;
import org.eclipse.net4j.db.DBType;
import org.eclipse.net4j.db.DBUtil;
import org.eclipse.net4j.db.IDBPreparedStatement;
import org.eclipse.net4j.db.IDBPreparedStatement.ReuseProbability;
import org.eclipse.net4j.db.ddl.IDBField;
import org.eclipse.net4j.db.ddl.IDBTable;
import org.eclipse.net4j.db.dml.SQL;
import org.eclipse.net4j.db.dml.SQLStatement;
import org.eclipse.net4j.util.ReflectUtil.ExcludeFromDump;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EStructuralFeature;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

/**
 * This is a list-table mapping for audit mode. It has ID and version columns and no delta support.
 *
 * @author Eike Stepper
 * @since 2.0
 */
public class AuditListTableMapping extends AbstractListTableMapping
{
  private IDBField versionField;

  @ExcludeFromDump
  private transient SQLStatement sqlClear;

  public AuditListTableMapping(IMappingStrategy mappingStrategy, EClass eClass, EStructuralFeature feature)
  {
    super(mappingStrategy, eClass, feature);
  }

  @Override
  protected void initSQLStrings()
  {
    super.initSQLStrings();

    IDBTable table = getTable();
    versionField = table.getField(MappingNames.LIST_REVISION_VERSION);

    // ----------- clear list -------------------------
    sqlClear = SQL.deleteFrom(table) //
        .where(SQL.eq(sourceField, SQL.param()) //
            .and(SQL.eq(versionField, SQL.param()))) //
        .compile(getDBAdapter());
  }

  @Override
  protected void addKeyFields(List<FieldInfo> list)
  {
    list.add(new FieldInfo(MappingNames.LIST_REVISION_VERSION, DBType.INTEGER));
  }

  @Override
  protected void bindKeyFields(SQLStatement sql, PreparedStatement stmt, CDORevision revision) throws SQLException
  {
    sql.binder(stmt).bindParameter(revision.getID(), getStore().getIDHandler()).bind(revision.getVersion());
  }

  @Override
  public void objectDetached(IDBStoreAccessor accessor, CDOID id, long revised)
  {
    // The audit list mapping does not care about revised references -> NOOP
  }

  @Override
  public void rawDeleted(IDBStoreAccessor accessor, CDOID id, CDOBranch branch, int version)
  {
    if (getTable() == null)
    {
      initTable(accessor);
    }

    IDBPreparedStatement stmt = DBUtil.prepare(sqlClear, accessor.getDBConnection(), ReuseProbability.HIGH);

    try
    {
      sqlClear.bindParameter(stmt, 1, id, getStore().getIDHandler());
      sqlClear.bindParameter(stmt, 2, version);
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
}
