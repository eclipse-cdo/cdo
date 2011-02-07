/**
 * Copyright (c) 2004 - 2011 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Stefan Winkler - 271444: [DB] Multiple refactorings bug 271444
 *    Stefan Winkler - derived branch mapping from audit mapping
 */
package org.eclipse.emf.cdo.server.internal.db.mapping.horizontal;

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.server.db.IDBStoreAccessor;
import org.eclipse.emf.cdo.server.db.mapping.IMappingStrategy;
import org.eclipse.emf.cdo.server.internal.db.CDODBSchema;

import org.eclipse.net4j.db.DBType;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EStructuralFeature;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * This is a list-table mapping for audit mode. It has ID and version columns and no delta support.
 * 
 * @author Eike Stepper
 * @author Stefan Winkler
 * @since 3.0
 */
public class BranchingListTableMapping extends AbstractListTableMapping
{
  private FieldInfo[] keyFields;

  public BranchingListTableMapping(IMappingStrategy mappingStrategy, EClass eClass, EStructuralFeature feature)
  {
    super(mappingStrategy, eClass, feature);
  }

  @Override
  protected FieldInfo[] getKeyFields()
  {
    if (keyFields == null)
    {
      keyFields = new FieldInfo[] {
          new FieldInfo(CDODBSchema.LIST_REVISION_ID, getMappingStrategy().getStore().getIDHandler().getDBType()),
          new FieldInfo(CDODBSchema.LIST_REVISION_BRANCH, DBType.INTEGER),
          new FieldInfo(CDODBSchema.LIST_REVISION_VERSION, DBType.INTEGER) };
    }

    return keyFields;
  }

  @Override
  protected void setKeyFields(PreparedStatement stmt, CDORevision revision) throws SQLException
  {
    getMappingStrategy().getStore().getIDHandler().setCDOID(stmt, 1, revision.getID());
    stmt.setInt(2, revision.getBranch().getID());
    stmt.setInt(3, revision.getVersion());
  }

  public void objectDetached(IDBStoreAccessor accessor, CDOID id, long revised)
  {
    // the audit list mapping does not care about revised references -> NOP
  }
}
