/**
 * Copyright (c) 2004 - 2010 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests;

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.commit.CDOChangeSetData;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.CDORevisionUtil;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.internal.server.mem.MEMStore;
import org.eclipse.emf.cdo.server.IRepository;
import org.eclipse.emf.cdo.server.IStore;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.transaction.CDOMerger;
import org.eclipse.emf.cdo.transaction.CDOTransaction;

import org.eclipse.emf.spi.cdo.DefaultCDOMerger;

import java.util.List;
import java.util.Map;

/**
 * @author Eike Stepper
 */
public class MergingTest extends AbstractCDOTest
{
  @Override
  public synchronized Map<String, Object> getTestProperties()
  {
    Map<String, Object> testProperties = super.getTestProperties();
    testProperties.put(IRepository.Props.SUPPORTING_AUDITS, "true");
    testProperties.put(IRepository.Props.SUPPORTING_BRANCHES, "true");
    return testProperties;
  }

  public void testCreateBranch() throws Exception
  {
    CDOMerger merger = new DefaultCDOMerger.PerFeature();

    CDOSession session = openSession();
    CDOBranch mainBranch = session.getBranchManager().getMainBranch();
    CDOTransaction transaction = session.openTransaction(mainBranch);

    sleep(10);
    CDOResource resource = transaction.createResource("/res");
    resource.getContents().add(getModel1Factory().createCompany());
    resource.getContents().add(getModel1Factory().createCompany());
    resource.getContents().add(getModel1Factory().createCompany());
    resource.getContents().add(getModel1Factory().createCompany());
    resource.getContents().add(getModel1Factory().createCompany());
    long time1 = transaction.commit().getTimeStamp();
    CDOBranch source1 = mainBranch.createBranch("source1", time1);

    sleep(10);
    resource.getContents().add(getModel1Factory().createCompany());
    resource.getContents().add(getModel1Factory().createCompany());
    resource.getContents().add(getModel1Factory().createCompany());
    long time2 = transaction.commit().getTimeStamp();
    CDOBranch source2 = mainBranch.createBranch("source2", time2);

    sleep(10);
    resource.getContents().add(getModel1Factory().createCompany());
    resource.getContents().add(getModel1Factory().createCompany());
    resource.getContents().add(getModel1Factory().createCompany());
    long time3 = transaction.commit().getTimeStamp();
    CDOBranch source3 = mainBranch.createBranch("source3", time3);

    CDOChangeSetData result = transaction.merge(source1.getHead(), merger);
    assertEquals(true, result.isEmpty());
    assertEquals(false, transaction.isDirty());

    result = transaction.merge(source2.getHead(), merger);
    assertEquals(true, result.isEmpty());
    assertEquals(false, transaction.isDirty());

    result = transaction.merge(source3.getHead(), merger);
    assertEquals(true, result.isEmpty());
    assertEquals(false, transaction.isDirty());

    sleep(10);
    CDOTransaction tx1 = session.openTransaction(source1);
    CDOResource res1 = tx1.getResource("/res");
    res1.getContents().add(getModel1Factory().createCompany());
    res1.getContents().add(getModel1Factory().createCompany());
    res1.getContents().add(getModel1Factory().createCompany());
    tx1.commit();
    tx1.close();

    result = transaction.merge(source1.getHead(), merger);
    assertEquals(true, result.isEmpty());
    assertEquals(false, transaction.isDirty());

    session.close();
  }

  private void dumpAll(CDOSession session)
  {
    IStore store = getRepository().getStore();
    if (store instanceof MEMStore)
    {
      MEMStore memStore = (MEMStore)store;
      dump("MEMStore", memStore.getAllRevisions());
    }

    // dump("ServerCache", getRepository().getRevisionManager().getCache().getAllRevisions());
    // dump("ClientCache", ((InternalCDOSession)session).getRevisionManager().getCache().getAllRevisions());
  }

  public static void dump(String label, Map<CDOBranch, List<CDORevision>> revisions)
  {
    System.out.println();
    System.out.println();
    System.out.println(label);
    System.out
        .println("===============================================================================================");
    CDORevisionUtil.dumpAllRevisions(revisions, System.out);
    System.out.println();
    System.out.println();
  }
}
