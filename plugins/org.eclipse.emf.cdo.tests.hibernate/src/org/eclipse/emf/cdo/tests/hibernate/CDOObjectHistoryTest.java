/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.hibernate;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.CDOObjectHistory;
import org.eclipse.emf.cdo.common.commit.CDOCommitHistory;
import org.eclipse.emf.cdo.common.commit.CDOCommitInfo;
import org.eclipse.emf.cdo.common.revision.CDOIDAndVersion;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.CDORevisionKey;
import org.eclipse.emf.cdo.common.revision.delta.CDOFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDORevisionDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDOSetFeatureDelta;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.eresource.EresourcePackage;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.tests.config.IRepositoryConfig;
import org.eclipse.emf.cdo.tests.config.impl.ConfigTest.Requires;
import org.eclipse.emf.cdo.tests.legacy.model1.Model1Package;
import org.eclipse.emf.cdo.tests.model1.Company;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.net4j.util.concurrent.ConcurrencyUtil;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;

import java.util.List;

/**
 * @author Martin Taal
 * @author Eike Stepper
 */
@Requires(IRepositoryConfig.CAPABILITY_AUDITING)
public class CDOObjectHistoryTest extends AbstractCDOTest
{
  protected CDOSession session1;

  private boolean finishedLoadingHistory = false;

  @Override
  protected void doTearDown() throws Exception
  {
    LifecycleUtil.deactivate(session1);
    session1 = null;
    super.doTearDown();
  }

  protected CDOSession openSession1()
  {
    session1 = openSession();
    return session1;
  }

  protected void closeSession1()
  {
    session1.close();
  }

  protected CDOSession openSession2()
  {
    return openSession();
  }

  public void testChangedAudit() throws Exception
  {
    CDOSession session = openSession1();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(getResourcePath("/res1"));

    Company company = getModel1Factory().createCompany();
    company.setName("ESC");
    resource.getContents().add(company);
    long commitTime1 = transaction.commit().getTimeStamp();
    assertEquals(true, session.getRepositoryInfo().getCreationTime() < commitTime1);
    assertEquals("ESC", company.getName());

    company.setName("Sympedia");
    long commitTime2 = transaction.commit().getTimeStamp();
    assertEquals(true, commitTime1 < commitTime2);
    assertEquals(true, session.getRepositoryInfo().getCreationTime() < commitTime2);
    assertEquals("Sympedia", company.getName());

    company.setName("Eclipse");
    long commitTime3 = transaction.commit().getTimeStamp();
    assertEquals(true, commitTime2 < commitTime3);
    assertEquals(true, session.getRepositoryInfo().getCreationTime() < commitTime2);
    assertEquals("Eclipse", company.getName());

    resource.getContents().remove(company);
    long commitTime4 = transaction.commit().getTimeStamp();

    closeSession1();
    session = openSession2();

    CDOView audit = session.openView(commitTime1);
    {
      CDOResource auditResource = audit.getResource(getResourcePath("/res1"));
      Company auditCompany = (Company)auditResource.getContents().get(0);
      assertEquals("ESC", auditCompany.getName());

      final CDOObjectHistory cdoObjectHistory = getCDOObjectHistory(audit, auditCompany);
      assertEquals(1, cdoObjectHistory.getElements().length);
      for (CDOCommitInfo cdoCommitInfo : cdoObjectHistory.getElements())
      {
        if (cdoCommitInfo.getTimeStamp() == commitTime1)
        {
          checkCommitInfo1(cdoCommitInfo);
        }
        else
        {
          fail();
        }
      }
      audit.close();
    }

    audit = session.openView(commitTime2);
    {
      CDOResource auditResource = audit.getResource(getResourcePath("/res1"));
      Company auditCompany = (Company)auditResource.getContents().get(0);
      assertEquals("Sympedia", auditCompany.getName());

      final CDOObjectHistory cdoObjectHistory = getCDOObjectHistory(audit, auditCompany);
      assertEquals(2, cdoObjectHistory.getElements().length);
      for (CDOCommitInfo cdoCommitInfo : cdoObjectHistory.getElements())
      {
        if (cdoCommitInfo.getTimeStamp() == commitTime1)
        {
          checkCommitInfo1(cdoCommitInfo);
        }
        else if (cdoCommitInfo.getTimeStamp() == commitTime2)
        {
          checkCommitInfo2(cdoCommitInfo);
        }
        else
        {
          fail();
        }
      }
      audit.close();
    }

    audit = session.openView(commitTime3);
    {
      CDOResource auditResource = audit.getResource(getResourcePath("/res1"));
      Company auditCompany = (Company)auditResource.getContents().get(0);
      assertEquals("Eclipse", auditCompany.getName());

      final CDOObjectHistory cdoObjectHistory = getCDOObjectHistory(audit, auditCompany);
      assertEquals(3, cdoObjectHistory.getElements().length);
      for (CDOCommitInfo cdoCommitInfo : cdoObjectHistory.getElements())
      {
        if (cdoCommitInfo.getTimeStamp() == commitTime1)
        {
          checkCommitInfo1(cdoCommitInfo);
        }
        else if (cdoCommitInfo.getTimeStamp() == commitTime2)
        {
          checkCommitInfo2(cdoCommitInfo);
        }
        else if (cdoCommitInfo.getTimeStamp() == commitTime3)
        {
          checkCommitInfo3(cdoCommitInfo);
        }
        else
        {
          fail();
        }
      }
      audit.close();
    }

    audit = session.openView(commitTime4);
    {
      final CDOCommitHistory cdoCommitHistory = getCDOCommitHistory(audit);
      assertEquals(5, cdoCommitHistory.getElements().length);
      for (CDOCommitInfo cdoCommitInfo : cdoCommitHistory.getElements())
      {
        if (cdoCommitInfo.getTimeStamp() == commitTime1)
        {
          checkCommitInfo1(cdoCommitInfo);
        }
        else if (cdoCommitInfo.getTimeStamp() == commitTime2)
        {
          checkCommitInfo2(cdoCommitInfo);
        }
        else if (cdoCommitInfo.getTimeStamp() == commitTime3)
        {
          checkCommitInfo3(cdoCommitInfo);
        }
        else if (cdoCommitInfo.getTimeStamp() == commitTime4)
        {
          checkCommitInfo4(cdoCommitInfo);
        }
        else if (cdoCommitInfo.getComment() != null && cdoCommitInfo.getComment().equals("<initialize>"))
        {
          // as expected
        }
        else
        {
          fail();
        }
      }
      audit.close();
    }

    session.close();
  }

  private void checkCommitInfo1(CDOCommitInfo cdoCommitInfo)
  {
    final List<CDOIDAndVersion> newObjects = cdoCommitInfo.getNewObjects();
    final List<CDORevisionKey> changedObjects = cdoCommitInfo.getChangedObjects();
    final List<CDOIDAndVersion> detachedObjects = cdoCommitInfo.getDetachedObjects();
    assertEquals(0, detachedObjects.size());
    assertEquals(3, newObjects.size());
    assertEquals(1, changedObjects.size());
    for (Object o : changedObjects)
    {
      final CDORevisionDelta cdoRevisionDelta = (CDORevisionDelta)o;
      final CDOFeatureDelta cdoFeatureDelta = cdoRevisionDelta.getFeatureDelta(EresourcePackage.eINSTANCE
          .getCDOResource_Contents());
      assertNotNull(cdoFeatureDelta);
      assertEquals(CDOFeatureDelta.Type.LIST, cdoFeatureDelta.getType());
    }
    int resourceCnt = 0;
    int resourceFolderCnt = 0;
    int companyCnt = 0;
    for (Object o : newObjects)
    {
      final CDORevision cdoRevision = (CDORevision)o;
      if (cdoRevision.getEClass().getName().equals(Model1Package.eINSTANCE.getCompany().getName()))
      {
        companyCnt++;
      }
      if (cdoRevision.getEClass() == EresourcePackage.eINSTANCE.getCDOResource())
      {
        resourceCnt++;
      }
      if (cdoRevision.getEClass() == EresourcePackage.eINSTANCE.getCDOResourceFolder())
      {
        resourceFolderCnt++;
      }
    }
    assertEquals(1, companyCnt);
    assertEquals(1, resourceCnt);
    assertEquals(1, resourceFolderCnt);
  }

  private void checkCommitInfo2(CDOCommitInfo cdoCommitInfo)
  {
    final List<CDOIDAndVersion> newObjects = cdoCommitInfo.getNewObjects();
    final List<CDORevisionKey> changedObjects = cdoCommitInfo.getChangedObjects();
    final List<CDOIDAndVersion> detachedObjects = cdoCommitInfo.getDetachedObjects();
    assertEquals(0, detachedObjects.size());
    assertEquals(0, newObjects.size());
    assertEquals(1, changedObjects.size());
    for (Object o : changedObjects)
    {
      final CDORevisionDelta cdoRevisionDelta = (CDORevisionDelta)o;
      final CDOSetFeatureDelta cdoFeatureDelta = (CDOSetFeatureDelta)cdoRevisionDelta.getFeatureDelta(cdoRevisionDelta
          .getEClass().getEStructuralFeature("name"));
      assertNotNull(cdoFeatureDelta);
      assertEquals("Sympedia", cdoFeatureDelta.getValue());
    }
  }

  private void checkCommitInfo3(CDOCommitInfo cdoCommitInfo)
  {
    final List<CDOIDAndVersion> newObjects = cdoCommitInfo.getNewObjects();
    final List<CDORevisionKey> changedObjects = cdoCommitInfo.getChangedObjects();
    final List<CDOIDAndVersion> detachedObjects = cdoCommitInfo.getDetachedObjects();
    assertEquals(0, detachedObjects.size());
    assertEquals(0, newObjects.size());
    assertEquals(1, changedObjects.size());
    for (Object o : changedObjects)
    {
      final CDORevisionDelta cdoRevisionDelta = (CDORevisionDelta)o;
      final CDOSetFeatureDelta cdoFeatureDelta = (CDOSetFeatureDelta)cdoRevisionDelta.getFeatureDelta(cdoRevisionDelta
          .getEClass().getEStructuralFeature("name"));
      assertNotNull(cdoFeatureDelta);
      assertEquals("Eclipse", cdoFeatureDelta.getValue());
    }
  }

  private void checkCommitInfo4(CDOCommitInfo cdoCommitInfo)
  {
    final List<CDOIDAndVersion> newObjects = cdoCommitInfo.getNewObjects();
    final List<CDORevisionKey> changedObjects = cdoCommitInfo.getChangedObjects();
    final List<CDOIDAndVersion> detachedObjects = cdoCommitInfo.getDetachedObjects();
    assertEquals(1, detachedObjects.size());
    assertEquals(0, newObjects.size());
    assertEquals(1, changedObjects.size());
    for (Object o : changedObjects)
    {
      final CDORevisionDelta cdoRevisionDelta = (CDORevisionDelta)o;
      final CDOFeatureDelta cdoFeatureDelta = cdoRevisionDelta.getFeatureDelta(EresourcePackage.eINSTANCE
          .getCDOResource_Contents());
      assertNotNull(cdoFeatureDelta);
      assertEquals(CDOFeatureDelta.Type.LIST, cdoFeatureDelta.getType());
    }
  }

  private synchronized CDOObjectHistory getCDOObjectHistory(CDOView audit, Object object)
  {
    CDOObjectHistory cdoObjectHistory = audit.getHistory((CDOObject)object);

    cdoObjectHistory.triggerLoad();
    long startTime = System.currentTimeMillis();
    while (cdoObjectHistory.isLoading())
    {
      ConcurrencyUtil.sleep(10);

      // waited too long
      if (System.currentTimeMillis() - startTime > 5000)
      {
        throw new IllegalStateException("commit info could not be loaded");
      }
    }
    return cdoObjectHistory;
  }

  private synchronized CDOCommitHistory getCDOCommitHistory(CDOView audit)
  {
    CDOCommitHistory cdoCommitHistory = audit.getHistory();

    cdoCommitHistory.triggerLoad();
    long startTime = System.currentTimeMillis();
    while (cdoCommitHistory.isLoading())
    {
      ConcurrencyUtil.sleep(10);

      // waited too long
      if (System.currentTimeMillis() - startTime > 5000)
      {
        throw new IllegalStateException("commit info could not be loaded");
      }
    }
    return cdoCommitHistory;
  }
}
