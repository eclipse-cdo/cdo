/*
 * Copyright (c) 2026 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.emf.cdo.tests.general;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.CDOObjectReference;
import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.internal.server.mem.MEMStore;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.spi.common.revision.DetachedCDORevision;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.tests.config.impl.ConfigTest.Requires;
import org.eclipse.emf.cdo.tests.model1.Category;
import org.eclipse.emf.cdo.tests.model1.Company;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CDOUtil;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Verifies revision storage behavior specific to MEMStore.
 *
 * @author Eike Stepper
 */
@Requires("MEM")
public class MEMStoreAlgorithmTest extends AbstractCDOTest
{
  public void testListLimitTransitions() throws Exception
  {
    if (getRepository().isSupportingBranches())
    {
      return;
    }

    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(getResourcePath("/memstore-list-limit"));
    Company company = getModel1Factory().createCompany();
    resource.getContents().add(company);
    transaction.commit();

    CDOID id = CDOUtil.getCDOObject(company).cdoID();
    MEMStore store = (MEMStore)getRepository().getStore();
    if (!getRepository().isSupportingAudits())
    {
      assertTrue(getStoredValue(store, id) instanceof InternalCDORevision);
    }

    for (int version = 2; version <= 4; version++)
    {
      company.setName("company-" + version);
      transaction.commit();
    }

    int expectedHistorySize = getRepository().isSupportingAudits() ? 4 : 1;
    assertEquals(expectedHistorySize, getRevisionCount(store, id));

    store.setListLimit(2);
    assertEquals(2, store.getListLimit());
    assertEquals(Math.min(2, expectedHistorySize), getRevisionCount(store, id));
    assertNotNull(store.getRevision(id, transaction.getBranch().getHead()));
    if (!getRepository().isSupportingAudits())
    {
      assertTrue(getStoredValue(store, id) instanceof InternalCDORevision);
    }
    if (getRepository().isSupportingAudits())
    {
      assertNull(store.getRevisionByVersion(id, transaction.getBranch().getVersion(2)));
      assertNotNull(store.getRevisionByVersion(id, transaction.getBranch().getVersion(3)));
      assertNotNull(store.getRevisionByVersion(id, transaction.getBranch().getVersion(4)));
      InternalCDORevision version3 = store.getRevisionByVersion(id, transaction.getBranch().getVersion(3));
      assertSame(version3, store.getRevision(id, transaction.getBranch().getPoint(version3.getTimeStamp())));
    }

    store.setListLimit(1);
    assertEquals(1, getRevisionCount(store, id));

    store.setListLimit(3);
    store.setListLimit(3);
    company.setName("company-5");
    transaction.commit();
    company.setName("company-6");
    transaction.commit();
    assertEquals(getRepository().isSupportingAudits() ? 3 : 1, getRevisionCount(store, id));
    if (getRepository().isSupportingAudits())
    {
      assertNull(store.getRevisionByVersion(id, transaction.getBranch().getVersion(3)));
      assertNotNull(store.getRevisionByVersion(id, transaction.getBranch().getVersion(4)));
      assertNotNull(store.getRevisionByVersion(id, transaction.getBranch().getVersion(6)));
    }

    store.setListLimit(MEMStore.UNLIMITED);
    company.setName("company-7");
    transaction.commit();
    assertEquals(getRepository().isSupportingAudits() ? 4 : 1, getRevisionCount(store, id));
    assertNotNull(store.getRevision(id, transaction.getBranch().getHead()));

    transaction.close();
    session.close();
  }

  public void testAuditingRevisionOrderingAndDuplicates() throws Exception
  {
    if (!getRepository().isSupportingAudits() || getRepository().isSupportingBranches())
    {
      return;
    }

    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(getResourcePath("/memstore-revision-order"));
    Company company = getModel1Factory().createCompany();
    resource.getContents().add(company);
    transaction.commit();

    CDOID id = CDOUtil.getCDOObject(company).cdoID();
    MEMStore store = (MEMStore)getRepository().getStore();
    InternalCDORevision base = store.getRevisionByVersion(id, transaction.getBranch().getVersion(1));
    InternalCDORevision highest = base.copy();
    highest.setVersion(9);
    store.addRevision(highest, true);

    InternalCDORevision lower = base.copy();
    lower.setVersion(3);
    store.addRevision(lower, true);

    InternalCDORevision duplicateHighest = base.copy();
    duplicateHighest.setVersion(9);
    store.addRevision(duplicateHighest, true);

    InternalCDORevision duplicateFirst = base.copy();
    store.addRevision(duplicateFirst, true);

    InternalCDORevision duplicateMiddle = lower.copy();
    store.addRevision(duplicateMiddle, true);

    List<Integer> versions = new ArrayList<>();
    for (InternalCDORevision revision : getRevisionsForID(store, id))
    {
      versions.add(revision.getVersion());
    }

    assertEquals(Arrays.asList(1, 1, 3, 3, 9, 9), versions);
    assertEquals(Arrays.asList(base, duplicateFirst, lower, duplicateMiddle, highest, duplicateHighest), getRevisionsForID(store, id));
    assertSame(base, store.getRevisionByVersion(id, transaction.getBranch().getVersion(1)));
    assertSame(lower, store.getRevisionByVersion(id, transaction.getBranch().getVersion(3)));
    assertSame(highest, store.getRevisionByVersion(id, transaction.getBranch().getVersion(9)));
    assertSame(duplicateHighest, getCurrentRevision(store, id));

    store.rawDelete(id, 9, transaction.getBranch());
    assertSame(duplicateHighest, store.getRevisionByVersion(id, transaction.getBranch().getVersion(9)));
    store.addRevision(highest, true);
    assertEquals(Arrays.asList(base, duplicateFirst, lower, duplicateMiddle, duplicateHighest, highest), getRevisionsForID(store, id));
    assertSame(duplicateHighest, store.getRevisionByVersion(id, transaction.getBranch().getVersion(9)));

    assertTrue(store.rollbackRevision(lower));
    assertSame(duplicateMiddle, store.getRevisionByVersion(id, transaction.getBranch().getVersion(3)));

    store.restoreRevision(lower);
    assertEquals(Arrays.asList(1, 1, 3, 9, 9), getVersionNumbers(store, id));
    assertSame(duplicateMiddle, store.getRevisionByVersion(id, transaction.getBranch().getVersion(3)));

    DetachedCDORevision detached = store.detachObject(id, transaction.getBranch(), transaction.getBranch().getHead().getTimeStamp() + 1);
    assertEquals(10, detached.getVersion());

    transaction.close();
    session.close();
  }

  public void testNonAuditingRawRevisionRetention() throws Exception
  {
    if (getRepository().isSupportingAudits())
    {
      return;
    }

    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(getResourcePath("/memstore-raw-revisions"));
    Company company = getModel1Factory().createCompany();
    resource.getContents().add(company);
    transaction.commit();

    CDOID id = CDOUtil.getCDOObject(company).cdoID();
    MEMStore store = (MEMStore)getRepository().getStore();
    InternalCDORevision original = store.getRevisionByVersion(id, transaction.getBranch().getVersion(1));
    assertSame(original, getStoredValue(store, id));
    InternalCDORevision raw = original.copy();
    raw.setVersion(3);
    store.addRevision(raw, true);

    assertEquals(2, getRevisionCount(store, id));
    assertTrue(getStoredValue(store, id) instanceof List<?>);
    assertSame(original, store.getRevisionByVersion(id, transaction.getBranch().getVersion(1)));
    assertSame(raw, store.getRevisionByVersion(id, transaction.getBranch().getVersion(3)));
    assertSame(raw, getCurrentRevision(store, id));

    InternalCDORevision duplicateRaw = original.copy();
    duplicateRaw.setVersion(3);
    store.addRevision(duplicateRaw, true);
    assertSame(raw, store.getRevisionByVersion(id, transaction.getBranch().getVersion(3)));
    assertSame(duplicateRaw, getCurrentRevision(store, id));

    store.rawDelete(id, 3, transaction.getBranch());
    assertSame(duplicateRaw, store.getRevisionByVersion(id, transaction.getBranch().getVersion(3)));
    store.rawDelete(id, 3, transaction.getBranch());
    assertSame(original, getStoredValue(store, id));

    InternalCDORevision update = original.copy();
    update.setVersion(2);
    store.addRevision(update, false);
    assertSame(update, getStoredValue(store, id));
    assertTrue(store.rollbackRevision(update));
    store.restoreRevision(original);
    assertSame(original, getStoredValue(store, id));

    DetachedCDORevision detached = store.detachObject(id, transaction.getBranch(), transaction.getBranch().getHead().getTimeStamp() + 1);
    assertSame(detached, getStoredValue(store, id));
    store.rawDelete(id, detached.getVersion(), transaction.getBranch());
    assertNull(getStoredValue(store, id));

    transaction.close();
    session.close();
  }

  public void testCrossReferenceLookupWithManyTargets() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(getResourcePath("/memstore-xrefs"));
    Category[] categories = new Category[8];
    Set<CDOObject> targets = new HashSet<>();
    Set<CDOObject> sparseTargets = new HashSet<>();
    for (int i = 0; i < categories.length; i++)
    {
      categories[i] = getModel1Factory().createCategory();
      resource.getContents().add(categories[i]);
      targets.add(CDOUtil.getCDOObject(categories[i]));

      Category sparseTarget = getModel1Factory().createCategory();
      resource.getContents().add(sparseTarget);
      sparseTargets.add(CDOUtil.getCDOObject(sparseTarget));
    }

    Company company = getModel1Factory().createCompany();
    for (Category category : categories)
    {
      company.getCategories().add(category);
    }

    resource.getContents().add(company);
    transaction.commit();

    CDOID companyID = CDOUtil.getCDOObject(company).cdoID();
    List<CDOObjectReference> references = transaction.queryXRefs(targets, getModel1Package().getCompany_Categories());
    Set<String> actual = new HashSet<>();
    for (CDOObjectReference reference : references)
    {
      assertEquals(getModel1Package().getCompany_Categories(), reference.getSourceReference());
      actual.add(reference.getSourceID() + ":" + reference.getTargetID() + ":" + reference.getSourceIndex());
    }

    Set<String> expected = new HashSet<>();
    for (int i = 0; i < categories.length; i++)
    {
      expected.add(companyID + ":" + CDOUtil.getCDOObject(categories[i]).cdoID() + ":" + i);
    }

    assertEquals(expected, actual);
    assertEquals(8, references.size());
    assertTrue(transaction.queryXRefs(sparseTargets, getModel1Package().getCompany_Categories()).isEmpty());

    transaction.close();
    session.close();
  }

  private int getRevisionCount(MEMStore store, CDOID id)
  {
    Map<CDOBranch, List<CDORevision>> allRevisions = store.getAllRevisions();
    int count = 0;
    for (List<CDORevision> revisions : allRevisions.values())
    {
      for (CDORevision revision : revisions)
      {
        if (id.equals(revision.getID()))
        {
          count++;
        }
      }
    }

    return count;
  }

  private InternalCDORevision getCurrentRevision(MEMStore store, CDOID id)
  {
    for (InternalCDORevision revision : store.getCurrentRevisions())
    {
      if (id.equals(revision.getID()))
      {
        return revision;
      }
    }

    return null;
  }

  private List<InternalCDORevision> getRevisionsForID(MEMStore store, CDOID id)
  {
    List<InternalCDORevision> result = new ArrayList<>();
    for (List<CDORevision> branchRevisions : store.getAllRevisions().values())
    {
      for (CDORevision revision : branchRevisions)
      {
        if (id.equals(revision.getID()))
        {
          result.add((InternalCDORevision)revision);
        }
      }
    }

    return result;
  }

  private List<Integer> getVersionNumbers(MEMStore store, CDOID id)
  {
    List<Integer> result = new ArrayList<>();
    for (InternalCDORevision revision : getRevisionsForID(store, id))
    {
      result.add(revision.getVersion());
    }

    return result;
  }

  private Object getStoredValue(MEMStore store, CDOID id) throws Exception
  {
    Field field = MEMStore.class.getDeclaredField("revisions");
    field.setAccessible(true);
    return ((Map<?, ?>)field.get(store)).get(id);
  }
}
