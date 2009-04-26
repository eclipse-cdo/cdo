/**
 * Copyright (c) 2004 - 2009 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Stefan Winkler - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.store.verifier;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

import org.eclipse.emf.cdo.server.IRepository;
import org.eclipse.emf.cdo.server.IStore;
import org.eclipse.emf.cdo.server.db.mapping.IClassMapping;
import org.eclipse.emf.cdo.server.db.mapping.IListMapping;
import org.eclipse.emf.cdo.server.internal.db.CDODBSchema;
import org.eclipse.emf.cdo.server.internal.db.mapping.horizontal.HorizontalNonAuditClassMapping;
import org.eclipse.emf.cdo.server.internal.db.mapping.horizontal.HorizontalNonAuditMappingStrategy;

import org.eclipse.net4j.util.collection.Pair;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Stefan Winkler
 */
public class NonAuditDBStoreIntegrityVerifier extends AbstractDBStoreVerifier
{
  public NonAuditDBStoreIntegrityVerifier(IRepository repo)
  {
    super(repo);

    // this is a verifier for non-auditing mode
    assertTrue(getStore().getRevisionTemporality() == IStore.RevisionTemporality.NONE);
    // ... and for horizontal class mapping
    assertTrue(getStore().getMappingStrategy() instanceof HorizontalNonAuditMappingStrategy);
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
    String sql = "SELECT count(1) FROM " + tableName + " WHERE " + CDODBSchema.ATTRIBUTES_REVISED + " <> 0";
    ResultSet resultSet = getStatement().executeQuery(sql);
    try
    {
      assertTrue(resultSet.next());
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
    String sql = "SELECT " + CDODBSchema.ATTRIBUTES_ID + ", count(1) FROM " + tableName + " GROUP BY "
        + CDODBSchema.ATTRIBUTES_ID;

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
    String sql = "SELECT " + CDODBSchema.ATTRIBUTES_ID + ", " + CDODBSchema.ATTRIBUTES_VERSION + " FROM " + tableName;

    ArrayList<Pair<Long, Integer>> idVersions = new ArrayList<Pair<Long, Integer>>();

    ResultSet resultSet = getStatement().executeQuery(sql);
    try
    {
      while (resultSet.next())
      {
        idVersions.add(new Pair<Long, Integer>(resultSet.getLong(1), resultSet.getInt(2)));
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
    String sql = "SELECT " + CDODBSchema.LIST_IDX + " FROM " + tableName + " WHERE " + CDODBSchema.LIST_REVISION_ID
        + "=" + id + " ORDER BY " + CDODBSchema.LIST_IDX;

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
