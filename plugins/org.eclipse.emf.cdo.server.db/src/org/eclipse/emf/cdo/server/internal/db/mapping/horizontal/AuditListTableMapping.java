/*
 * Copyright (c) 2009-2013, 2018, 2019, 2023 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
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
import org.eclipse.emf.cdo.server.db.IIDHandler;
import org.eclipse.emf.cdo.server.db.mapping.IMappingStrategy;

import org.eclipse.net4j.db.DBException;
import org.eclipse.net4j.db.DBType;
import org.eclipse.net4j.db.DBUtil;
import org.eclipse.net4j.db.IDBPreparedStatement;
import org.eclipse.net4j.db.IDBPreparedStatement.ReuseProbability;
import org.eclipse.net4j.db.ddl.IDBField;
import org.eclipse.net4j.db.ddl.IDBTable;

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

  private String sqlClear;

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
    StringBuilder builder = new StringBuilder();
    builder.append("DELETE FROM "); //$NON-NLS-1$
    builder.append(table);
    builder.append(" WHERE "); //$NON-NLS-1$
    builder.append(sourceField);
    builder.append("=? AND "); //$NON-NLS-1$
    builder.append(versionField);
    builder.append("=?"); //$NON-NLS-1$
    sqlClear = builder.toString();
  }

  @Override
  protected void addKeyFields(List<FieldInfo> list)
  {
    list.add(new FieldInfo(MappingNames.LIST_REVISION_VERSION, DBType.INTEGER));
  }

  @Override
  protected void setKeyFields(PreparedStatement stmt, CDORevision revision) throws SQLException
  {
    IIDHandler idHandler = getMappingStrategy().getStore().getIDHandler();
    idHandler.setCDOID(stmt, 1, revision.getID());
    stmt.setInt(2, revision.getVersion());
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

    IIDHandler idHandler = getMappingStrategy().getStore().getIDHandler();
    IDBPreparedStatement stmt = accessor.getDBConnection().prepareStatement(sqlClear, ReuseProbability.HIGH);

    try
    {
      idHandler.setCDOID(stmt, 1, id);
      stmt.setInt(2, version);
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
