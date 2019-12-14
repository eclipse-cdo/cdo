/*
 * Copyright (c) 2008-2012, 2015, 2019 Eike Stepper (Loehne, Germany) and others.
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
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.config.IRepositoryConfig;
import org.eclipse.emf.cdo.tests.config.impl.ConfigTest.Requires;
import org.eclipse.emf.cdo.tests.model1.Company;
import org.eclipse.emf.cdo.tests.model5.GenListOfInt;
import org.eclipse.emf.cdo.tests.model5.Model5Factory;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CDOURIUtil;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.net4j.util.lifecycle.LifecycleUtil;

import org.eclipse.emf.common.util.URI;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * @author Eike Stepper
 */
@Requires(IRepositoryConfig.CAPABILITY_AUDITING)
public class AuditTest extends AbstractCDOTest
{
  protected CDOSession session1;

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

  public void testNewAudit() throws Exception
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

    closeSession1();
    session = openSession2();

    CDOView audit = session.openView(commitTime1);
    CDOResource auditResource = audit.getResource(getResourcePath("/res1"));
    Company auditCompany = (Company)auditResource.getContents().get(0);
    assertEquals("ESC", auditCompany.getName());

    CDOView audit2 = session.openView(commitTime2);
    CDOResource auditResource2 = audit2.getResource(getResourcePath("/res1"));
    Company auditCompany2 = (Company)auditResource2.getContents().get(0);
    assertEquals("Sympedia", auditCompany2.getName());
    session.close();
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

    closeSession1();
    session = openSession2();

    CDOView audit = session.openView(commitTime1);
    {
      CDOResource auditResource = audit.getResource(getResourcePath("/res1"));
      Company auditCompany = (Company)auditResource.getContents().get(0);
      assertEquals("ESC", auditCompany.getName());
    }

    audit.setTimeStamp(commitTime2);
    {
      CDOResource auditResource = audit.getResource(getResourcePath("/res1"));
      Company auditCompany = (Company)auditResource.getContents().get(0);
      assertEquals("Sympedia", auditCompany.getName());
    }

    audit.setTimeStamp(commitTime3);
    {
      CDOResource auditResource = audit.getResource(getResourcePath("/res1"));
      Company auditCompany = (Company)auditResource.getContents().get(0);
      assertEquals("Eclipse", auditCompany.getName());
    }

    session.close();
  }

  public void testKeepHandle() throws Exception
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

    closeSession1();
    session = openSession2();

    CDOView audit = session.openView(commitTime1);
    CDOResource auditResource = audit.getResource(getResourcePath("/res1"));
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
    CDOResource resource = transaction.createResource(getResourcePath("/res1"));

    resource.getContents().add(getModel1Factory().createCompany());
    long commitTime1 = transaction.commit().getTimeStamp();

    resource.getContents().add(getModel1Factory().createCompany());
    long commitTime2 = transaction.commit().getTimeStamp();

    resource.getContents().add(getModel1Factory().createCompany());
    long commitTime3 = transaction.commit().getTimeStamp();
    closeSession1();

    session = openSession2();

    CDOView audit = session.openView(commitTime1);
    CDOResource auditResource = audit.getResource(getResourcePath("/res1"));
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
    CDOResource resource = transaction.createResource(getResourcePath("/res1"));

    resource.getContents().add(getModel1Factory().createCompany());
    resource.getContents().add(getModel1Factory().createCompany());
    resource.getContents().add(getModel1Factory().createCompany());
    resource.getContents().add(getModel1Factory().createCompany());
    resource.getContents().add(getModel1Factory().createCompany());
    long commitTime1 = transaction.commit().getTimeStamp();

    resource.getContents().remove(2);
    long commitTime2 = transaction.commit().getTimeStamp();

    resource.getContents().remove(2);
    long commitTime3 = transaction.commit().getTimeStamp();
    closeSession1();

    session = openSession2();

    CDOView audit = session.openView(commitTime1);
    CDOResource auditResource = audit.getResource(getResourcePath("/res1"));
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
    CDOResource resource = transaction.createResource(getResourcePath("/res1"));

    Company company = getModel1Factory().createCompany();
    company.setName("ESC");

    resource.getContents().add(getModel1Factory().createCompany());
    resource.getContents().add(getModel1Factory().createCompany());
    resource.getContents().add(company);
    resource.getContents().add(getModel1Factory().createCompany());
    resource.getContents().add(getModel1Factory().createCompany());
    long commitTime1 = transaction.commit().getTimeStamp();

    resource.getContents().remove(2);
    long commitTime2 = transaction.commit().getTimeStamp();

    closeSession1();
    session = openSession2();

    CDOView audit = session.openView(commitTime1);
    CDOResource auditResource = audit.getResource(getResourcePath("/res1"));
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

  @Requires(IRepositoryConfig.CAPABILITY_RESTARTABLE)
  public void testConsistentHistoryForIsMany() throws Exception
  {
    ArrayList<List<Integer>> history = new ArrayList<>();
    ArrayList<Long> timestamps = new ArrayList<>();

    {
      CDOSession session = openSession();
      CDOTransaction transaction = session.openTransaction();
      CDOResource resource = transaction.createResource(getResourcePath("/res1"));

      GenListOfInt persistentList = Model5Factory.eINSTANCE.createGenListOfInt();
      resource.getContents().add(persistentList);

      timestamps.add(transaction.commit().getTimeStamp());
      history.add(new ArrayList<>(persistentList.getElements()));

      persistentList.getElements().add(1);
      persistentList.getElements().add(2);
      persistentList.getElements().add(3);

      timestamps.add(transaction.commit().getTimeStamp());
      history.add(new ArrayList<>(persistentList.getElements()));

      persistentList.getElements().add(1, 4);

      timestamps.add(transaction.commit().getTimeStamp());
      history.add(new ArrayList<>(persistentList.getElements()));

      persistentList.getElements().add(0, 5);

      timestamps.add(transaction.commit().getTimeStamp());
      history.add(new ArrayList<>(persistentList.getElements()));

      persistentList.getElements().move(1, 3);

      timestamps.add(transaction.commit().getTimeStamp());
      history.add(new ArrayList<>(persistentList.getElements()));

      persistentList.getElements().move(4, 2);
      persistentList.getElements().move(1, 3);

      timestamps.add(transaction.commit().getTimeStamp());
      history.add(new ArrayList<>(persistentList.getElements()));

      persistentList.getElements().remove(2);

      timestamps.add(transaction.commit().getTimeStamp());
      history.add(new ArrayList<>(persistentList.getElements()));

      persistentList.getElements().add(1, 2);
      persistentList.getElements().remove(2);

      timestamps.add(transaction.commit().getTimeStamp());
      history.add(new ArrayList<>(persistentList.getElements()));

      persistentList.getElements().clear();
      persistentList.getElements().add(6);
      persistentList.getElements().add(7);
      persistentList.getElements().add(8);

      timestamps.add(transaction.commit().getTimeStamp());
      history.add(new ArrayList<>(persistentList.getElements()));

      resource.getContents().clear();

      transaction.commit();
      transaction.close();
      session.close();
      clearCache(getRepository().getRevisionManager());
    }

    {
      CDOSession session = openSession();

      for (int i = 0; i < timestamps.size(); i++)
      {
        msg("Checking timestamp " + i + " of " + timestamps.size() + ": " + timestamps.get(i));
        CDOView audit = session.openView(timestamps.get(i));
        CDOResource res = audit.getResource(getResourcePath("/res1"));
        GenListOfInt persistentList = (GenListOfInt)res.getContents().get(0);

        assertEquals(joinList(history.get(i)), joinList(persistentList.getElements()));
        audit.close();
      }

      CDOView view = session.openView();
      CDOResource res = view.getResource(getResourcePath("/res1"));
      assertEquals(true, res.getContents().isEmpty());
    }
  }

  private String joinList(List<Integer> list)
  {
    String result = "";
    for (Integer i : list)
    {
      result += " " + i;
    }

    return result;
  }

  public void testCanCreateAuditAtRepoCreationTime() throws Exception
  {
    CDOSession session = openSession1();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(getResourcePath("/res1"));
    Company company = getModel1Factory().createCompany();
    resource.getContents().add(company);
    transaction.commit();
    closeSession1();

    session = openSession2();
    session.openView(session.getRepositoryInfo().getCreationTime());
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
      CDOResource resource = transaction.createResource(getResourcePath("/res1"));
      Company company = getModel1Factory().createCompany();
      resource.getContents().add(company);
      transaction.commit();
      closeSession1();

      session = openSession2();
      session.openView(timeStampPriorToRepoCreation);
      fail("IllegalArgumentException expected");
    }
    catch (IllegalArgumentException expected)
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
      CDOResource resource = transaction.createResource(getResourcePath("/res1"));
      Company company = getModel1Factory().createCompany();
      resource.getContents().add(company);
      long commitTime1 = transaction.commit().getTimeStamp();
      closeSession1();

      session = openSession2();
      CDOView audit = session.openView(commitTime1);
      audit.setTimeStamp(timeStampPriorToRepoCreation);
      fail("Exception expected");
    }
    catch (Exception expected)
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
      CDOSession session = openSession();
      CDOTransaction transaction = session.openTransaction();
      CDOResource resource = transaction.createResource(getResourcePath("/my/resource"));
      commitTime1 = transaction.commit().getTimeStamp();

      resource.setPath(getResourcePath("/renamed"));
      commitTime2 = transaction.commit().getTimeStamp();
      session.close();
    }

    CDOSession session = openSession();
    CDOView audit1 = session.openView(commitTime1);
    assertEquals(true, audit1.hasResource(getResourcePath("/my/resource")));
    assertEquals(false, audit1.hasResource(getResourcePath("/renamed")));

    CDOView audit2 = session.openView(commitTime2);
    assertEquals(false, audit2.hasResource(getResourcePath("/my/resource")));
    assertEquals(true, audit2.hasResource(getResourcePath("/renamed")));
    session.close();
  }

  @SuppressWarnings("deprecation")
  public void testChangeURI() throws Exception
  {
    long commitTime1;
    long commitTime2;

    {
      CDOSession session = openSession();
      CDOTransaction transaction = session.openTransaction();
      CDOResource resource = transaction.createResource(getResourcePath("/my/resource"));
      commitTime1 = transaction.commit().getTimeStamp();

      URI uri = URI.createURI("cdo://repo1" + getResourcePath("/renamed"));
      assertEquals(CDOURIUtil.createResourceURI(session, getResourcePath("/renamed")), uri);
      resource.setURI(uri);

      commitTime2 = transaction.commit().getTimeStamp();
      session.close();
    }

    CDOSession session = openSession();
    CDOView audit1 = session.openView(commitTime1);
    assertEquals(true, audit1.hasResource(getResourcePath("/my/resource")));
    assertEquals(false, audit1.hasResource(getResourcePath("/renamed")));

    CDOView audit2 = session.openView(commitTime2);
    assertEquals(false, audit2.hasResource(getResourcePath("/my/resource")));
    assertEquals(true, audit2.hasResource(getResourcePath("/renamed")));
    session.close();
  }
}
