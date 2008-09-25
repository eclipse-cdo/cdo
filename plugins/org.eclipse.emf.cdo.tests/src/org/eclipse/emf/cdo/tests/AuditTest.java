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

import java.util.Map;

/**
 * @author Eike Stepper
 */
public class AuditTest extends AbstractCDOTest
{
  protected CDOSession session1;

  @Override
  public Map<String, Object> getTestProperties()
  {
    Map<String, Object> testProperties = super.getTestProperties();
    testProperties.put(IRepository.Props.PROP_SUPPORTING_AUDITS, "true");
    return testProperties;
  }

  protected CDOSession openSession1()
  {
    session1 = openModel1Session();
    return session1;
  }

  protected void closeSession1()
  {
    session1.close();
  }

  protected CDOSession openSession2()
  {
    return openModel1Session();
  }

  public void testNewAudit() throws Exception
  {
    CDOSession session = openSession1();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource("/res1");

    Company company = getModel1Factory().createCompany();
    company.setName("ESC");
    resource.getContents().add(company);
    transaction.commit();
    long commitTime1 = transaction.getLastCommitTime();
    assertTrue(session.getRepositoryCreationTime() < commitTime1);
    assertEquals("ESC", company.getName());

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

    closeSession1();
    session = openSession2();

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

  public void testChangedAudit() throws Exception
  {
    CDOSession session = openSession1();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource("/res1");

    Company company = getModel1Factory().createCompany();
    company.setName("ESC");
    resource.getContents().add(company);
    transaction.commit();
    long commitTime1 = transaction.getLastCommitTime();
    assertTrue(session.getRepositoryCreationTime() < commitTime1);
    assertEquals("ESC", company.getName());

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

    closeSession1();
    session = openSession2();

    CDOAudit audit = session.openAudit(commitTime1);
    {
      CDOResource auditResource = audit.getResource("/res1");
      Company auditCompany = (Company)auditResource.getContents().get(0);
      assertEquals("ESC", auditCompany.getName());
    }

    audit.setTimeStamp(commitTime2);
    {
      CDOResource auditResource = audit.getResource("/res1");
      Company auditCompany = (Company)auditResource.getContents().get(0);
      assertEquals("Sympedia", auditCompany.getName());
    }

    audit.setTimeStamp(commitTime3);
    {
      CDOResource auditResource = audit.getResource("/res1");
      Company auditCompany = (Company)auditResource.getContents().get(0);
      assertEquals("Eclipse", auditCompany.getName());
    }

    session.close();
  }

  public void testKeepHandle() throws Exception
  {
    CDOSession session = openSession1();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource("/res1");

    Company company = getModel1Factory().createCompany();
    company.setName("ESC");
    resource.getContents().add(company);
    transaction.commit();
    long commitTime1 = transaction.getLastCommitTime();
    assertTrue(session.getRepositoryCreationTime() < commitTime1);
    assertEquals("ESC", company.getName());

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

    closeSession1();
    session = openSession2();

    CDOAudit audit = session.openAudit(commitTime1);
    CDOResource auditResource = audit.getResource("/res1");
    Company auditCompany = (Company)auditResource.getContents().get(0);
    assertEquals("ESC", auditCompany.getName());

    audit.setTimeStamp(commitTime2);
    assertEquals("Sympedia", auditCompany.getName());

    audit.setTimeStamp(commitTime3);
    assertEquals("Eclipse", auditCompany.getName());
    session.close();
  }

  public void testAddingContents() throws Exception
  {
    CDOSession session = openSession1();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource("/res1");

    resource.getContents().add(getModel1Factory().createCompany());
    transaction.commit();
    long commitTime1 = transaction.getLastCommitTime();

    resource.getContents().add(getModel1Factory().createCompany());
    transaction.commit();
    long commitTime2 = transaction.getLastCommitTime();

    resource.getContents().add(getModel1Factory().createCompany());
    transaction.commit();
    long commitTime3 = transaction.getLastCommitTime();
    closeSession1();

    session = openSession2();

    CDOAudit audit = session.openAudit(commitTime1);
    CDOResource auditResource = audit.getResource("/res1");
    assertEquals(1, auditResource.getContents().size());

    audit.setTimeStamp(commitTime2);
    assertEquals(2, auditResource.getContents().size());

    audit.setTimeStamp(commitTime3);
    assertEquals(3, auditResource.getContents().size());
    session.close();
  }

  public void testRemovingContents() throws Exception
  {
    CDOSession session = openSession1();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource("/res1");

    resource.getContents().add(getModel1Factory().createCompany());
    resource.getContents().add(getModel1Factory().createCompany());
    resource.getContents().add(getModel1Factory().createCompany());
    resource.getContents().add(getModel1Factory().createCompany());
    resource.getContents().add(getModel1Factory().createCompany());
    transaction.commit();
    long commitTime1 = transaction.getLastCommitTime();

    resource.getContents().remove(2);
    transaction.commit();
    long commitTime2 = transaction.getLastCommitTime();

    resource.getContents().remove(2);
    transaction.commit();
    long commitTime3 = transaction.getLastCommitTime();
    closeSession1();

    session = openSession2();

    CDOAudit audit = session.openAudit(commitTime1);
    CDOResource auditResource = audit.getResource("/res1");
    assertEquals(5, auditResource.getContents().size());

    audit.setTimeStamp(commitTime2);
    assertEquals(4, auditResource.getContents().size());

    audit.setTimeStamp(commitTime3);
    assertEquals(3, auditResource.getContents().size());
    session.close();
  }

  public void testRemovingContentsKeepHandle() throws Exception
  {
    CDOSession session = openSession1();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource("/res1");

    Company company = getModel1Factory().createCompany();
    company.setName("ESC");

    resource.getContents().add(getModel1Factory().createCompany());
    resource.getContents().add(getModel1Factory().createCompany());
    resource.getContents().add(company);
    resource.getContents().add(getModel1Factory().createCompany());
    resource.getContents().add(getModel1Factory().createCompany());
    transaction.commit();
    long commitTime1 = transaction.getLastCommitTime();

    resource.getContents().remove(2);
    transaction.commit();
    long commitTime2 = transaction.getLastCommitTime();

    closeSession1();
    session = openSession2();

    CDOAudit audit = session.openAudit(commitTime1);
    CDOResource auditResource = audit.getResource("/res1");
    assertEquals(5, auditResource.getContents().size());

    Company auditCompany = (Company)auditResource.getContents().get(2);
    assertEquals("ESC", auditCompany.getName());
    assertClean(auditCompany, audit);

    audit.setTimeStamp(commitTime2);
    assertEquals(4, auditResource.getContents().size());
    assertTransient(auditCompany);

    audit.setTimeStamp(commitTime1);
    assertTransient(auditCompany);
    assertEquals(5, auditResource.getContents().size());
    session.close();
  }

  /**
   * @author Eike Stepper
   */
  public static class LocalAuditTest extends AuditTest
  {
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

    @Override
    protected void closeSession1()
    {
      // Do nothing
    }

    @Override
    protected CDOSession openSession2()
    {
      return session1;
    }
  }
}
