/*
 * Copyright (c) 2004 - 2011 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Stefan Winkler - major refactoring
 *    Stefan Winkler - Bug 329025: [DB] Support branching for range-based mapping strategy
 */
package org.eclipse.emf.cdo.server.internal.db.mapping.horizontal;

import org.eclipse.emf.cdo.common.branch.CDOBranchVersion;
import org.eclipse.emf.cdo.server.db.CDODBUtil;
import org.eclipse.emf.cdo.server.db.IIDHandler;
import org.eclipse.emf.cdo.server.db.mapping.IClassMapping;
import org.eclipse.emf.cdo.server.db.mapping.IListMapping;
import org.eclipse.emf.cdo.server.internal.db.CDODBSchema;

import org.eclipse.net4j.db.DBUtil;
import org.eclipse.net4j.db.DBUtil.DeserializeRowHandler;
import org.eclipse.net4j.db.ddl.IDBField;
import org.eclipse.net4j.util.io.ExtendedDataInput;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EStructuralFeature;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * @author Eike Stepper
 * @since 2.0
 */
public class HorizontalBranchingMappingStrategyWithRanges extends HorizontalBranchingMappingStrategy
{
  private boolean copyOnBranch;

  public HorizontalBranchingMappingStrategyWithRanges()
  {
  }

  @Override
  public boolean hasDeltaSupport()
  {
    return true;
  }

  public boolean shallCopyOnBranch()
  {
    return copyOnBranch;
  }

  @Override
  public IClassMapping doCreateClassMapping(EClass eClass)
  {
    return new HorizontalBranchingClassMapping(this, eClass);
  }

  @Override
  public IListMapping doCreateListMapping(EClass containingClass, EStructuralFeature feature)
  {
    return new BranchingListTableMappingWithRanges(this, containingClass, feature);
  }

  @Override
  public IListMapping doCreateFeatureMapMapping(EClass containingClass, EStructuralFeature feature)
  {
    return new BranchingFeatureMapTableMappingWithRanges(this, containingClass, feature);
  }

  @Override
  protected String modifyListJoin(String attrTable, String listTable, String join, boolean forRawExport)
  {
    join += " AND " + listTable + "." + CDODBSchema.LIST_REVISION_VERSION_ADDED;
    if (forRawExport)
    {
      join += "=" + attrTable + "." + CDODBSchema.ATTRIBUTES_VERSION;
    }
    else
    {
      join += "<=" + attrTable + "." + CDODBSchema.ATTRIBUTES_VERSION;
      join += " AND (" + listTable + "." + CDODBSchema.LIST_REVISION_VERSION_REMOVED;
      join += " IS NULL OR " + listTable + "." + CDODBSchema.LIST_REVISION_VERSION_REMOVED;
      join += ">" + attrTable + "." + CDODBSchema.ATTRIBUTES_VERSION + ")";
    }

    join += " AND " + attrTable + "." + CDODBSchema.ATTRIBUTES_BRANCH;
    join += "=" + listTable + "." + CDODBSchema.LIST_REVISION_BRANCH;
    return join;
  }

  @Override
  protected DeserializeRowHandler getImportListHandler()
  {
    return new ImportListHandler();
  }

  @Override
  protected void doAfterActivate() throws Exception
  {
    super.doAfterActivate();

    String value = getProperties().get(CDODBUtil.PROP_COPY_ON_BRANCH);
    copyOnBranch = value == null ? false : Boolean.valueOf(value);
  }

  /**
   * @author Eike Stepper
   */
  private final class ImportListHandler implements DeserializeRowHandler
  {
    private final IIDHandler idHandler = getStore().getIDHandler();

    private PreparedStatement stmt;

    public void handleRow(ExtendedDataInput in, Connection connection, IDBField[] fields, Object[] values)
        throws SQLException, IOException
    {
      int versionAdded = (Integer)values[2];
      if (versionAdded == CDOBranchVersion.FIRST_VERSION)
      {
        return;
      }

      if (stmt == null)
      {
        String sql = "UPDATE " + fields[0].getTable() //
            + " SET " + CDODBSchema.LIST_REVISION_VERSION_REMOVED + "=?" //
            + " WHERE " + CDODBSchema.LIST_REVISION_ID + "=?" //
            + " AND " + CDODBSchema.LIST_REVISION_BRANCH + "=?" //
            + " AND " + CDODBSchema.LIST_IDX + "=?" //
            + " AND " + CDODBSchema.LIST_REVISION_VERSION_ADDED + "<?" //
            + " AND " + CDODBSchema.LIST_REVISION_VERSION_REMOVED + " IS NULL";
        stmt = connection.prepareStatement(sql);
      }

      Object sourceID = values[0];
      int branch = (Integer)values[1];
      int index = (Integer)values[4];

      stmt.setInt(1, versionAdded);
      idHandler.setCDOIDRaw(stmt, 2, sourceID);
      stmt.setInt(3, branch);
      stmt.setInt(4, index);
      stmt.setInt(5, versionAdded);

      stmt.addBatch();
    }

    public void done(boolean successful) throws SQLException, IOException
    {
      if (stmt != null)
      {
        try
        {
          if (successful)
          {
            stmt.executeBatch();
          }
        }
        finally
        {
          DBUtil.close(stmt);
          stmt = null;
        }
      }
    }
  }
}
