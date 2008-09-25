/***************************************************************************
 * Copyright (c) 2004 - 2008 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.emf.cdo.tests;

import org.eclipse.emf.cdo.CDOAudit;
import org.eclipse.emf.cdo.CDOSession;
import org.eclipse.emf.cdo.CDOTransaction;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.server.IRepository;
import org.eclipse.emf.cdo.tests.model1.Company;

import org.eclipse.emf.internal.cdo.CDOAuditImpl;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;

import java.util.Map;

/**
 * @author Eike Stepper
 */
public class AuditTest extends AbstractCDOTest
{
  @Override
  public Map<String, Object> getTestProperties()
  {
    Map<String, Object> testProperties = super.getTestProperties();
    testProperties.put(IRepository.Props.PROP_SUPPORTING_AUDITS, "true");
    return testProperties;
  }

  public void testRepositoryCreationTime() throws Exception
  {
    CDOSession session = openSession();
    long repositoryCreationTime = session.getRepositoryCreationTime();
    assertEquals(getRepository().getCreationTime(), repositoryCreationTime);
    assertEquals(getRepository().getStore().getCreationTime(), repositoryCreationTime);
  }

  public void testRepositoryTime() throws Exception
  {
    CDOSession session = openSession();
    long repositoryTime = session.getRepositoryTime();
    assertTrue(Math.abs(System.currentTimeMillis() - repositoryTime) < 500);
  }

  public void testLocalAudit() throws Exception
  {
    CDOSession session = openModel1Session();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource("/res1");

    Company company = getModel1Factory().createCompany();
    company.setName("ESC");
    resource.getContents().add(company);
    transaction.commit();
    long commitTime1 = transaction.getLastCommitTime();
    assertTrue(session.getRepositoryCreationTime() < commitTime1);
    assertEquals("ESC", company.getName());
    sleep(100);

    company.setName("Sympedia");
    transaction.commit();
    long commitTime2 = transaction.getLastCommitTime();
    assertTrue(commitTime1 < commitTime2);
    assertTrue(session.getRepositoryCreationTime() < commitTime2);
    assertEquals("Sympedia", company.getName());

    company.setName("Eclipse");
    transaction.commit();
    long commitTime3 = transaction.getLastCommitTime();
    assertTrue(commitTime2 < commitTime3);
    assertTrue(session.getRepositoryCreationTime() < commitTime2);
    assertEquals("Eclipse", company.getName());

    CDOAudit audit = session.openAudit(commitTime1);
    CDOResource auditResource = audit.getResource("/res1");
    Company auditCompany = (Company)auditResource.getContents().get(0);
    assertEquals("ESC", auditCompany.getName());

    CDOAudit audit2 = session.openAudit(commitTime2);
    CDOResource auditResource2 = audit2.getResource("/res1");
    Company auditCompany2 = (Company)auditResource2.getContents().get(0);
    assertEquals("Sympedia", auditCompany2.getName());
    session.close();
  }

  public void testRemoteAudit() throws Exception
  {
    CDOSession session = openModel1Session();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource("/res1");

    Company company = getModel1Factory().createCompany();
    company.setName("ESC");
    resource.getContents().add(company);
    transaction.commit();
    long commitTime1 = transaction.getLastCommitTime();
    assertTrue(session.getRepositoryCreationTime() < commitTime1);
    assertEquals("ESC", company.getName());
    sleep(100);

    company.setName("Sympedia");
    transaction.commit();
    long commitTime2 = transaction.getLastCommitTime();
    assertTrue(commitTime1 < commitTime2);
    assertTrue(session.getRepositoryCreationTime() < commitTime2);
    assertEquals("Sympedia", company.getName());
    sleep(100);

    company.setName("Eclipse");
    transaction.commit();
    long commitTime3 = transaction.getLastCommitTime();
    assertTrue(commitTime2 < commitTime3);
    assertTrue(session.getRepositoryCreationTime() < commitTime2);
    assertEquals("Eclipse", company.getName());
    session.close();

    CDOSession session2 = openModel1Session();
    CDOAudit audit = session2.openAudit(commitTime1);
    CDOResource auditResource = audit.getResource("/res1");
    EList<EObject> contents = auditResource.getContents();
    Company auditCompany = (Company)contents.get(0);
    String name = auditCompany.getName();
    assertEquals("ESC", name);

    CDOAudit audit2 = session2.openAudit(commitTime2);
    CDOResource auditResource2 = audit2.getResource("/res1");
    Company auditCompany2 = (Company)auditResource2.getContents().get(0);
    assertEquals("Sympedia", auditCompany2.getName());
    session2.close();
  }

  public void testChangeLocalAudit() throws Exception
  {
    CDOSession session = openModel1Session();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource("/res1");
  
    Company company = getModel1Factory().createCompany();
    company.setName("ESC");
    resource.getContents().add(company);
    transaction.commit();
    long commitTime1 = transaction.getLastCommitTime();
    assertTrue(session.getRepositoryCreationTime() < commitTime1);
    assertEquals("ESC", company.getName());
    sleep(100);
  
    company.setName("Sympedia");
    transaction.commit();
    long commitTime2 = transaction.getLastCommitTime();
    assertTrue(commitTime1 < commitTime2);
    assertTrue(session.getRepositoryCreationTime() < commitTime2);
    assertEquals("Sympedia", company.getName());
  
    company.setName("Eclipse");
    transaction.commit();
    long commitTime3 = transaction.getLastCommitTime();
    assertTrue(commitTime2 < commitTime3);
    assertTrue(session.getRepositoryCreationTime() < commitTime2);
    assertEquals("Eclipse", company.getName());
  
    CDOAudit audit = session.openAudit(commitTime1);
    {
      CDOResource auditResource = audit.getResource("/res1");
      Company auditCompany = (Company)auditResource.getContents().get(0);
      assertEquals("ESC", auditCompany.getName());
    }
  
    ((CDOAuditImpl)audit).setTimeStamp(commitTime2);
    {
      CDOResource auditResource = audit.getResource("/res1");
      Company auditCompany = (Company)auditResource.getContents().get(0);
      assertEquals("Sympedia", auditCompany.getName());
    }
  
    ((CDOAuditImpl)audit).setTimeStamp(commitTime3);
    {
      CDOResource auditResource = audit.getResource("/res1");
      Company auditCompany = (Company)auditResource.getContents().get(0);
      assertEquals("Eclipse", auditCompany.getName());
    }
  
    session.close();
  }

  public void testChangeRemoteAudit() throws Exception
  {
    CDOSession session = openModel1Session();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource("/res1");

    Company company = getModel1Factory().createCompany();
    company.setName("ESC");
    resource.getContents().add(company);
    transaction.commit();
    long commitTime1 = transaction.getLastCommitTime();
    assertTrue(session.getRepositoryCreationTime() < commitTime1);
    assertEquals("ESC", company.getName());
    sleep(100);

    company.setName("Sympedia");
    transaction.commit();
    long commitTime2 = transaction.getLastCommitTime();
    assertTrue(commitTime1 < commitTime2);
    assertTrue(session.getRepositoryCreationTime() < commitTime2);
    assertEquals("Sympedia", company.getName());

    company.setName("Eclipse");
    transaction.commit();
    long commitTime3 = transaction.getLastCommitTime();
    assertTrue(commitTime2 < commitTime3);
    assertTrue(session.getRepositoryCreationTime() < commitTime2);
    assertEquals("Eclipse", company.getName());
    session.close();

    CDOSession session2 = openModel1Session();
    CDOAudit audit = session2.openAudit(commitTime1);
    {
      CDOResource auditResource = audit.getResource("/res1");
      Company auditCompany = (Company)auditResource.getContents().get(0);
      assertEquals("ESC", auditCompany.getName());
    }

    ((CDOAuditImpl)audit).setTimeStamp(commitTime2);
    {
      CDOResource auditResource = audit.getResource("/res1");
      Company auditCompany = (Company)auditResource.getContents().get(0);
      assertEquals("Sympedia", auditCompany.getName());
    }

    ((CDOAuditImpl)audit).setTimeStamp(commitTime3);
    {
      CDOResource auditResource = audit.getResource("/res1");
      Company auditCompany = (Company)auditResource.getContents().get(0);
      assertEquals("Eclipse", auditCompany.getName());
    }

    session2.close();
  }
}
