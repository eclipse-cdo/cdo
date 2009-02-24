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
import org.eclipse.emf.cdo.server.db.IClassMapping;
import org.eclipse.emf.cdo.server.db.IReferenceMapping;
import org.eclipse.emf.cdo.server.internal.db.CDODBSchema;
import org.eclipse.emf.cdo.server.internal.db.HorizontalMappingStrategy;

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
    assertTrue(getStore().getMappingStrategy() instanceof HorizontalMappingStrategy);
  }

  @Override
  protected void doVerify() throws Exception
  {
    for (IClassMapping mapping : getClassMappings())
    {
      if (mapping != null && mapping.getTable() != null)
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
    String tableName = mapping.getTable().getName();
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
    String tableName = mapping.getTable().getName();
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
    List<IReferenceMapping> referenceMappings = mapping.getReferenceMappings();
    if (referenceMappings == null)
    {
      return;
    }

    String tableName = mapping.getTable().getName();
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

    for (IReferenceMapping refMapping : referenceMappings)
    {
      for (Pair<Long, Integer> idVersion : idVersions)
      {
        verifyOnlyLatestReferences(refMapping, idVersion.getElement1(), idVersion.getElement2());
        verifyCorrectIndices(refMapping, idVersion.getElement1());
      }
    }
  }

  /**
   * Verify that no reference with sourceId == ID exist which have another version
   */
  private void verifyOnlyLatestReferences(IReferenceMapping refMapping, long id, int version) throws Exception
  {
    String tableName = refMapping.getTable().getName();
    String sql = "SELECT count(1) FROM " + tableName + " WHERE " + CDODBSchema.REFERENCES_SOURCE + "=" + id + " AND "
        + CDODBSchema.REFERENCES_VERSION + "<>" + version;

    ResultSet resultSet = getStatement().executeQuery(sql);
    try
    {
      assertTrue(resultSet.next());
      assertEquals("Table " + tableName + " contains old references for id " + id + "(version should be " + version
          + ")", 0, resultSet.getInt(1));
    }
    finally
    {
      resultSet.close();
    }
  }

  private void verifyCorrectIndices(IReferenceMapping refMapping, long id) throws Exception
  {
    String tableName = refMapping.getTable().getName();
    String sql = "SELECT " + CDODBSchema.REFERENCES_IDX + " FROM " + tableName + " WHERE "
        + CDODBSchema.REFERENCES_SOURCE + "=" + id + " ORDER BY " + CDODBSchema.REFERENCES_IDX;

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
