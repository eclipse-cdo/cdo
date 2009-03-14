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
import org.eclipse.emf.cdo.server.db.mapping.IReferenceMapping;
import org.eclipse.emf.cdo.server.internal.db.CDODBSchema;
import org.eclipse.emf.cdo.server.internal.db.mapping.HorizontalMappingStrategy;

import org.eclipse.net4j.util.collection.Pair;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import junit.framework.AssertionFailedError;

/**
 * @author Stefan Winkler
 */
public class AuditDBStoreIntegrityVerifier extends AbstractDBStoreVerifier
{
  public AuditDBStoreIntegrityVerifier(IRepository repo)
  {
    super(repo);

    // this is a verifier for auditing mode
    assertTrue(getStore().getRevisionTemporality() == IStore.RevisionTemporality.AUDITING);
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
    verifyAtMostOneUnrevised(mapping);
    verifyUniqueIdVersion(mapping);
    verifyReferences(mapping);
  }

  private void verifyAtMostOneUnrevised(IClassMapping mapping) throws Exception
  {
    String tableName = mapping.getTable().getName();
    TRACER.format("verifyAtMostOneUnrevised: {0} ...", tableName);

    String sql = "SELECT " + CDODBSchema.ATTRIBUTES_ID + ", count(1) FROM " + tableName + " WHERE "
        + CDODBSchema.ATTRIBUTES_REVISED + "= 0 GROUP BY " + CDODBSchema.ATTRIBUTES_ID;
    TRACER.format("  Executing SQL: {0} ", sql);

    ResultSet resultSet = getStatement().executeQuery(sql);
    try
    {
      while (resultSet.next())
      {
        assertTrue("Multiple unrevised rows for ID " + resultSet.getLong(1), resultSet.getInt(2) <= 1);
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
    String tableName = mapping.getTable().getName();
    TRACER.format("verifyUniqueIdVersion: {0} ...", tableName);

    String sql = "SELECT " + CDODBSchema.ATTRIBUTES_ID + "," + CDODBSchema.ATTRIBUTES_VERSION + ", count(1) FROM "
        + tableName + " GROUP BY " + CDODBSchema.ATTRIBUTES_ID + "," + CDODBSchema.ATTRIBUTES_VERSION;

    TRACER.format("  Executing SQL: {0} ", sql);

    ResultSet resultSet = getStatement().executeQuery(sql);
    try
    {
      while (resultSet.next())
      {
        assertTrue("Multiple rows for ID " + resultSet.getLong(1) + "v" + resultSet.getInt(2), resultSet.getInt(3) <= 1);
      }
    }
    catch (AssertionFailedError e)
    {
      TRACER.trace(e.getMessage());
      sqlDump("SELECT * FROM " + tableName + " WHERE " + CDODBSchema.ATTRIBUTES_REVISED + "=0");
      throw e;
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
        verifyCorrectIndices(refMapping, idVersion.getElement1(), idVersion.getElement2());
      }
    }
  }

  private void verifyCorrectIndices(IReferenceMapping refMapping, long id, int version) throws Exception
  {
    String tableName = refMapping.getTable().getName();

    TRACER.format("verifyUniqueIdVersion: {0} for ID{1}v{2} ...", tableName, id, version);

    String sql = "SELECT " + CDODBSchema.REFERENCES_IDX + " FROM " + tableName + " WHERE "
        + CDODBSchema.REFERENCES_SOURCE + "=" + id + " AND " + CDODBSchema.REFERENCES_VERSION + "=" + version
        + " ORDER BY " + CDODBSchema.REFERENCES_IDX;

    TRACER.format("  Executing SQL: {0} ", sql);

    ResultSet resultSet = getStatement().executeQuery(sql);
    int indexShouldBe = 0;

    try
    {
      while (resultSet.next())
      {
        assertEquals("Index " + indexShouldBe + " missing for ID" + id + "v" + version, indexShouldBe++, resultSet
            .getInt(1));
      }
    }
    catch (AssertionFailedError e)
    {
      sqlDump("SELECT * FROM " + tableName + " WHERE " + CDODBSchema.REFERENCES_SOURCE + "=" + id + " AND "
          + CDODBSchema.REFERENCES_VERSION + "=" + version + " ORDER BY " + CDODBSchema.REFERENCES_IDX);
      throw e;
    }
    finally
    {
      resultSet.close();
    }
  }
}
