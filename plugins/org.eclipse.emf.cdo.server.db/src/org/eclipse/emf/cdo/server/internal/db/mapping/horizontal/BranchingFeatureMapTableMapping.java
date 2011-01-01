/**
 * Copyright (c) 2004 - 2011 Eike Stepper (Berlin, Germany) and others. 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Stefan Winkler - Bug 271444: [DB] Multiple refactorings bug 271444
 *    Christopher Albert - Bug 254455: [DB] Support FeatureMaps bug 254455
 *    Stefan Winkler - derived branch mapping from audit mapping  
 */
package org.eclipse.emf.cdo.server.internal.db.mapping.horizontal;

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDUtil;
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
 * This is a featuremap-table mapping for audit mode. It has ID and version columns and no delta support.
 * 
 * @author Eike Stepper
 * @author Stefan Winkler
 * @since 3.0
 */
public class BranchingFeatureMapTableMapping extends AbstractFeatureMapTableMapping
{
  private static final FieldInfo[] KEY_FIELDS = { new FieldInfo(CDODBSchema.FEATUREMAP_REVISION_ID, DBType.BIGINT),
      new FieldInfo(CDODBSchema.FEATUREMAP_BRANCH, DBType.INTEGER),
      new FieldInfo(CDODBSchema.FEATUREMAP_VERSION, DBType.INTEGER) };

  public BranchingFeatureMapTableMapping(IMappingStrategy mappingStrategy, EClass eClass, EStructuralFeature feature)
  {
    super(mappingStrategy, eClass, feature);
  }

  @Override
  protected FieldInfo[] getKeyFields()
  {
    return KEY_FIELDS;
  }

  @Override
  protected void setKeyFields(PreparedStatement stmt, CDORevision revision) throws SQLException
  {
    stmt.setLong(1, CDOIDUtil.getLong(revision.getID()));
    stmt.setInt(2, revision.getBranch().getID());
    stmt.setInt(3, revision.getVersion());
  }

  public void objectDetached(IDBStoreAccessor accessor, CDOID id, long revised)
  {
    // the audit list mapping does not care about revised references -> NOP
  }
}
