/**
 * Copyright (c) 2004 - 2009 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests;

import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.server.IRepository;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.model1.Company;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CDOURIUtil;
import org.eclipse.emf.cdo.view.CDOAudit;

import org.eclipse.net4j.signal.RemoteException;

import org.eclipse.emf.common.util.URI;

import java.util.Calendar;
import java.util.GregorianCalendar;
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
    testProperties.put(IRepository.Props.SUPPORTING_AUDITS, "true");
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
    assertEquals(true, session.repository().getCreationTime() < commitTime1);
    assertEquals("ESC", company.getName());

    company.setName("Sympedia");
    transaction.commit();
    long commitTime2 = transaction.getLastCommitTime();
    assertEquals(true, commitTime1 < commitTime2);
    assertEquals(true, session.repository().getCreationTime() < commitTime2);
    assertEquals("Sympedia", company.getName());

    company.setName("Eclipse");
    transaction.commit();
    long commitTime3 = transaction.getLastCommitTime();
    assertEquals(true, commitTime2 < commitTime3);
    assertEquals(true, session.repository().getCreationTime() < commitTime2);
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
    assertEquals(true, session.repository().getCreationTime() < commitTime1);
    assertEquals("ESC", company.getName());

    company.setName("Sympedia");
    transaction.commit();
    long commitTime2 = transaction.getLastCommitTime();
    assertEquals(true, commitTime1 < commitTime2);
    assertEquals(true, session.repository().getCreationTime() < commitTime2);
    assertEquals("Sympedia", company.getName());

    company.setName("Eclipse");
    transaction.commit();
    long commitTime3 = transaction.getLastCommitTime();
    assertEquals(true, commitTime2 < commitTime3);
    assertEquals(true, session.repository().getCreationTime() < commitTime2);
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
    assertEquals(true, session.repository().getCreationTime() < commitTime1);
    assertEquals("ESC", company.getName());

    company.setName("Sympedia");
    transaction.commit();
    long commitTime2 = transaction.getLastCommitTime();
    assertEquals(true, commitTime1 < commitTime2);
    assertEquals(true, session.repository().getCreationTime() < commitTime2);
    assertEquals("Sympedia", company.getName());

    company.setName("Eclipse");
    transaction.commit();
    long commitTime3 = transaction.getLastCommitTime();
    assertEquals(true, commitTime2 < commitTime3);
    assertEquals(true, session.repository().getCreationTime() < commitTime2);
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
    assertInvalid(auditCompany);

    audit.setTimeStamp(commitTime1);
    assertInvalid(auditCompany);
    assertEquals(5, auditResource.getContents().size());
    session.close();
  }

  public void testCanCreateAuditAtRepoCreationTime() throws Exception
  {
    CDOSession session = openSession1();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource("/res1");
    Company company = getModel1Factory().createCompany();
    resource.getContents().add(company);
    transaction.commit();
    closeSession1();

    session = openSession2();
    session.openAudit(session.repository().getCreationTime());
    session.close();
  }

  public void testCannotCreateAuditWithTimestampPriorToRepo() throws Exception
  {
    Calendar calendar = GregorianCalendar.getInstance();
    calendar.set(Calendar.YEAR, 19);
    calendar.set(Calendar.MONTH, 11);
    calendar.set(Calendar.DAY_OF_MONTH, 11);

    long timeStampPriorToRepoCreation = calendar.getTime().getTime();
    CDOSession session = openSession1();

    try
    {
      CDOTransaction transaction = session.openTransaction();
      CDOResource resource = transaction.createResource("/res1");
      Company company = getModel1Factory().createCompany();
      resource.getContents().add(company);
      transaction.commit();
      closeSession1();

      session = openSession2();
      session.openAudit(timeStampPriorToRepoCreation);
      fail("SignalRemoteException expected");
    }
    catch (RemoteException eexpected)
    {
      // Success
    }
    finally
    {
      session.close();
    }
  }

  public void testCannotSetAuditTimestampPriorToRepo() throws Exception
  {
    Calendar calendar = GregorianCalendar.getInstance();
    calendar.set(Calendar.YEAR, 19);
    calendar.set(Calendar.MONTH, 11);
    calendar.set(Calendar.DAY_OF_MONTH, 11);

    long timeStampPriorToRepoCreation = calendar.getTime().getTime();
    CDOSession session = openSession1();

    try
    {
      CDOTransaction transaction = session.openTransaction();
      CDOResource resource = transaction.createResource("/res1");
      Company company = getModel1Factory().createCompany();
      resource.getContents().add(company);
      transaction.commit();
      long commitTime1 = transaction.getLastCommitTime();
      closeSession1();

      session = openSession2();
      CDOAudit audit = session.openAudit(commitTime1);
      audit.setTimeStamp(timeStampPriorToRepoCreation);
      fail("SignalRemoteException expected");
    }
    catch (RemoteException expected)
    {
      // Success
    }
    finally
    {
      session.close();
    }
  }

  public void testChangePath() throws Exception
  {
    long commitTime1;
    long commitTime2;

    {
      CDOSession session = openModel1Session();
      CDOTransaction transaction = session.openTransaction();
      CDOResource resource = transaction.createResource("/my/resource");
      transaction.commit();
      commitTime1 = transaction.getLastCommitTime();

      resource.setPath("/renamed");
      transaction.commit();
      commitTime2 = transaction.getLastCommitTime();
      session.close();
    }

    CDOSession session = openModel1Session();
    CDOAudit audit1 = session.openAudit(commitTime1);
    assertEquals(true, audit1.hasResource("/my/resource"));
    assertEquals(false, audit1.hasResource("/renamed"));

    CDOAudit audit2 = session.openAudit(commitTime2);
    assertEquals(false, audit2.hasResource("/my/resource"));
    assertEquals(true, audit2.hasResource("/renamed"));
    session.close();
  }

  public void testChangeURI() throws Exception
  {
    long commitTime1;
    long commitTime2;

    {
      CDOSession session = openModel1Session();
      CDOTransaction transaction = session.openTransaction();
      CDOResource resource = transaction.createResource("/my/resource");
      transaction.commit();
      commitTime1 = transaction.getLastCommitTime();

      URI uri = URI.createURI("cdo://repo1/renamed");
      assertEquals(CDOURIUtil.createResourceURI(session, "/renamed"), uri);
      resource.setURI(uri);

      transaction.commit();
      commitTime2 = transaction.getLastCommitTime();
      session.close();
    }

    CDOSession session = openModel1Session();
    CDOAudit audit1 = session.openAudit(commitTime1);
    assertEquals(true, audit1.hasResource("/my/resource"));
    assertEquals(false, audit1.hasResource("/renamed"));

    CDOAudit audit2 = session.openAudit(commitTime2);
    assertEquals(false, audit2.hasResource("/my/resource"));
    assertEquals(true, audit2.hasResource("/renamed"));
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
      long repositoryCreationTime = session.repository().getCreationTime();
      assertEquals(getRepository().getCreationTime(), repositoryCreationTime);
      assertEquals(getRepository().getStore().getCreationTime(), repositoryCreationTime);
    }

    public void testRepositoryTime() throws Exception
    {
      CDOSession session = openSession();
      long repositoryTime = session.repository().getCurrentTime();
      assertEquals(true, Math.abs(System.currentTimeMillis() - repositoryTime) < 500);
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
