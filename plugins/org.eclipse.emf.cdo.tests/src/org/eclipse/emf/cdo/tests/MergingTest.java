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
import org.eclipse.emf.cdo.common.commit.CDOCommitInfo;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.server.IRepository;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.transaction.CDOTransaction;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.spi.cdo.DefaultCDOMerger;

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

  public void testInitial() throws Exception
  {
    CDOSession session = openSession();
    CDOBranch mainBranch = session.getBranchManager().getMainBranch();
    CDOTransaction transaction = session.openTransaction(mainBranch);

    sleep(10);
    CDOResource resource = transaction.createResource("/res");
    EList<EObject> contents = resource.getContents();
    contents.add(getModel1Factory().createCompany());
    contents.add(getModel1Factory().createCompany());
    contents.add(getModel1Factory().createCompany());
    contents.add(getModel1Factory().createCompany());
    contents.add(getModel1Factory().createCompany());
    long time1 = transaction.commit().getTimeStamp();
    CDOBranch source1 = mainBranch.createBranch("source1", time1);

    sleep(10);
    contents.add(getModel1Factory().createCompany());
    contents.add(getModel1Factory().createCompany());
    contents.add(getModel1Factory().createCompany());
    contents.add(getModel1Factory().createCompany());
    long time2 = transaction.commit().getTimeStamp();
    CDOBranch source2 = mainBranch.createBranch("source2", time2);

    sleep(10);
    contents.add(getModel1Factory().createCompany());
    contents.add(getModel1Factory().createCompany());
    contents.add(getModel1Factory().createCompany());
    long time3 = transaction.commit().getTimeStamp();
    CDOBranch source3 = mainBranch.createBranch("source3", time3);

    CDOChangeSetData result = transaction.merge(source1.getHead(), new DefaultCDOMerger.PerFeature.ManyValued());
    assertEquals(true, result.isEmpty());
    assertEquals(false, transaction.isDirty());

    result = transaction.merge(source2.getHead(), new DefaultCDOMerger.PerFeature.ManyValued());
    assertEquals(true, result.isEmpty());
    assertEquals(false, transaction.isDirty());

    result = transaction.merge(source3.getHead(), new DefaultCDOMerger.PerFeature.ManyValued());
    assertEquals(true, result.isEmpty());
    assertEquals(false, transaction.isDirty());

    sleep(10);
    CDOTransaction tx1 = session.openTransaction(source1);
    CDOResource res1 = tx1.getResource("/res");
    EList<EObject> contents1 = res1.getContents();
    contents1.add(getModel1Factory().createCompany());
    contents1.add(getModel1Factory().createCompany());
    tx1.commit();
    tx1.close();

    result = transaction.merge(source1.getHead(), new DefaultCDOMerger.PerFeature.ManyValued());
    assertEquals(false, result.isEmpty());
    assertEquals(2, result.getNewObjects().size());
    assertEquals(1, result.getChangedObjects().size());
    assertEquals(0, result.getDetachedObjects().size());
    assertEquals(true, transaction.isDirty());

    CDOCommitInfo commitInfo1 = transaction.commit();
    assertEquals(2, commitInfo1.getNewObjects().size());
    assertEquals(1, commitInfo1.getChangedObjects().size());
    assertEquals(0, commitInfo1.getDetachedObjects().size());
    assertEquals(false, transaction.isDirty());
    assertEquals(mainBranch, ((CDORevision)commitInfo1.getNewObjects().get(0)).getBranch());
    assertEquals(mainBranch, ((CDORevision)commitInfo1.getNewObjects().get(1)).getBranch());
    assertEquals(1, ((CDORevision)commitInfo1.getNewObjects().get(0)).getVersion());
    assertEquals(1, ((CDORevision)commitInfo1.getNewObjects().get(1)).getVersion());

    result = transaction.merge(source1.getHead(), new DefaultCDOMerger.PerFeature.ManyValued());
    assertEquals(true, result.isEmpty());
    assertEquals(false, transaction.isDirty());

    sleep(10);
    CDOTransaction tx2 = session.openTransaction(source2);
    CDOResource res2 = tx2.getResource("/res");
    EList<EObject> contents2 = res2.getContents();
    contents2.add(getModel1Factory().createCompany());
    tx2.commit();
    tx2.close();

    result = transaction.merge(source2.getHead(), new DefaultCDOMerger.PerFeature.ManyValued());
    assertEquals(false, result.isEmpty());
    assertEquals(1, result.getNewObjects().size());
    assertEquals(1, result.getChangedObjects().size());
    assertEquals(0, result.getDetachedObjects().size());
    assertEquals(true, transaction.isDirty());

    CDOCommitInfo commitInfo2 = transaction.commit();
    assertEquals(1, commitInfo2.getNewObjects().size());
    assertEquals(1, commitInfo2.getChangedObjects().size());
    assertEquals(0, commitInfo2.getDetachedObjects().size());
    assertEquals(false, transaction.isDirty());
    assertEquals(mainBranch, ((CDORevision)commitInfo2.getNewObjects().get(0)).getBranch());
    assertEquals(1, ((CDORevision)commitInfo2.getNewObjects().get(0)).getVersion());

    result = transaction.merge(source2.getHead(), new DefaultCDOMerger.PerFeature.ManyValued());
    assertEquals(true, result.isEmpty());
    assertEquals(false, transaction.isDirty());

    session.close();
  }
}
