/*
 * Copyright (c) 2026 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.emf.cdo.tests.db;

import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.server.db.IDBStoreAccessor;
import org.eclipse.emf.cdo.server.internal.db.DBStore;
import org.eclipse.emf.cdo.server.internal.db.mapping.horizontal.AbstractHorizontalMappingStrategy;
import org.eclipse.emf.cdo.server.internal.db.mapping.horizontal.HorizontalMappingStrategy;
import org.eclipse.emf.cdo.server.internal.db.mapping.horizontal.ObjectTypeTable;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.tests.config.IRepositoryConfig;
import org.eclipse.emf.cdo.tests.model1.Category;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CDOUtil;
import org.eclipse.emf.cdo.view.CDOView;

import java.util.Map;

/**
 * Focused integration tests for batched ObjectType inserts.
 *
 * @author Eike Stepper
 */
public class ObjectTypeBatchingTest extends AbstractCDOTest
{
  @Override
  protected void initTestProperties(Map<String, Object> properties)
  {
    super.initTestProperties(properties);

    properties.put(DBConfig.PROP_TEST_OBJECT_TYPE_BATCH_SIZE, getName().contains("BatchSizeOne") ? 1 : 4); //$NON-NLS-1$

    if (getName().contains("FailMode")) //$NON-NLS-1$
    {
      properties.put(DBConfig.PROP_TEST_OBJECT_TYPE_DUPLICATE_POLICY, "FAIL"); //$NON-NLS-1$
    }
  }

  public void testManyNewObjectsRemainTypeReadable() throws Exception
  {
    final int objectCount = 64;
    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(getResourcePath("/object-types")); //$NON-NLS-1$

    for (int i = 0; i < objectCount; i++)
    {
      Category category = getModel1Factory().createCategory();
      category.setName("Category-" + i); //$NON-NLS-1$
      resource.getContents().add(category);
    }

    transaction.commit();
    session.close();

    session = openSession();
    CDOView view = session.openView();
    try
    {
      CDOResource persisted = view.getResource(getResourcePath("/object-types")); //$NON-NLS-1$
      assertEquals(objectCount, persisted.getContents().size());

      for (int i = 0; i < objectCount; i++)
      {
        Category category = (Category)persisted.getContents().get(i);
        assertEquals("Category-" + i, category.getName()); //$NON-NLS-1$
      }
    }
    finally
    {
      view.close();
      session.close();
    }
  }

  public void testExistingObjectTypesRemainUnchanged() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(getResourcePath("/existing-object-types")); //$NON-NLS-1$

    Category first = getModel1Factory().createCategory();
    first.setName("First"); //$NON-NLS-1$
    resource.getContents().add(first);

    Category second = getModel1Factory().createCategory();
    second.setName("Second"); //$NON-NLS-1$
    resource.getContents().add(second);
    transaction.commit();

    InternalCDORevision[] revisions = { (InternalCDORevision)CDOUtil.getCDOObject(first).cdoRevision(),
        (InternalCDORevision)CDOUtil.getCDOObject(second).cdoRevision() };

    DBStore store = (DBStore)getRepository().getStore();
    AbstractHorizontalMappingStrategy mappingStrategy;

    if (store.getMappingStrategy() instanceof HorizontalMappingStrategy)
    {
      mappingStrategy = (AbstractHorizontalMappingStrategy)((HorizontalMappingStrategy)store.getMappingStrategy()).getDelegate();
    }
    else
    {
      mappingStrategy = (AbstractHorizontalMappingStrategy)store.getMappingStrategy();
    }

    ObjectTypeTable objectTypes = mappingStrategy.objects();
    IDBStoreAccessor accessor = store.getWriter(null);

    try
    {
      objectTypes.putObjectTypes(accessor, revisions, first.eClass());
    }
    finally
    {
      accessor.release();
      session.close();
    }

    session = openSession();
    CDOView view = session.openView();

    try
    {
      CDOResource persisted = view.getResource(getResourcePath("/existing-object-types")); //$NON-NLS-1$
      assertEquals(2, persisted.getContents().size());
      assertTrue(persisted.getContents().get(0) instanceof Category);
      assertTrue(persisted.getContents().get(1) instanceof Category);
    }
    finally
    {
      view.close();
      session.close();
    }
  }

  public void testFailModeDirectBatching() throws Exception
  {
    testManyNewObjectsRemainTypeReadable();
  }

  public void testObjectTypeBatchSizeOneUsesSynchronousPath() throws Exception
  {
    testManyNewObjectsRemainTypeReadable();
  }

  @Requires(IRepositoryConfig.CAPABILITY_BRANCHING)
  public void testBranchingBulkObjectTypesSurviveReopen() throws Exception
  {
    int objectCount = 16;
    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction(session.getBranchManager().getMainBranch());
    CDOResource resource = transaction.createResource(getResourcePath("/branching-object-types")); //$NON-NLS-1$

    for (int i = 0; i < objectCount; i++)
    {
      Category category = getModel1Factory().createCategory();
      category.setName("BranchingCategory-" + i); //$NON-NLS-1$
      resource.getContents().add(category);
    }

    transaction.commit();
    transaction.close();
    session.close();

    session = openSession();
    CDOView view = session.openView(session.getBranchManager().getMainBranch());
    try
    {
      CDOResource persisted = view.getResource(getResourcePath("/branching-object-types")); //$NON-NLS-1$
      assertEquals(objectCount, persisted.getContents().size());

      for (int i = 0; i < objectCount; i++)
      {
        Category category = (Category)CDOUtil.getEObject(persisted.getContents().get(i));
        assertEquals("BranchingCategory-" + i, category.getName()); //$NON-NLS-1$
      }
    }
    finally
    {
      view.close();
      session.close();
    }
  }
}
