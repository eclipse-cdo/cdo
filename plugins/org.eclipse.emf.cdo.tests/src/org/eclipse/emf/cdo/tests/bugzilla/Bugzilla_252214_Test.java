/*
 * Copyright (c) 2008-2012 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Simon McDuff - initial API and implementation
 *    Eike Stepper - maintenance
 */
package org.eclipse.emf.cdo.tests.bugzilla;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.server.IRepository;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.tests.model1.Company;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CDOUtil;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.emf.internal.cdo.view.CDOStateMachine;

import org.eclipse.emf.spi.cdo.InternalCDOObject;

import java.util.Map;

/**
 * NPE when calling audit.setTimeStamp()
 * <p>
 * See bug 252214
 *
 * @author Simon McDuff
 */
public class Bugzilla_252214_Test extends AbstractCDOTest
{
  @Override
  public void initTestProperties(Map<String, Object> properties)
  {
    super.initTestProperties(properties);
    properties.put(IRepository.Props.SUPPORTING_AUDITS, "true");
  }

  public void testBugzilla_252214() throws Exception
  {
    long commitTime1 = 0;
    long commitTime2 = 0;

    {
      CDOSession session = openSession();
      CDOTransaction transaction = session.openTransaction();
      CDOResource resource = transaction.createResource(getResourcePath("/res1"));

      Company company = getModel1Factory().createCompany();
      company.setName("ESC");
      resource.getContents().add(company);
      commitTime1 = transaction.commit().getTimeStamp();
      assertEquals(true, session.getRepositoryInfo().getCreationTime() < commitTime1);
      assertEquals("ESC", company.getName());

      company.setName("Sympedia");
      commitTime2 = transaction.commit().getTimeStamp();
      assertEquals(true, commitTime1 < commitTime2);
      assertEquals(true, session.getRepositoryInfo().getCreationTime() < commitTime2);
      assertEquals("Sympedia", company.getName());
    }

    CDOSession session = openSession();
    CDOView audit = session.openView(commitTime1);

    {
      CDOResource auditResource = audit.getResource(getResourcePath("/res1"));
      Company auditCompany = (Company)auditResource.getContents().get(0);
      CDOObject cdoAuditCompany = CDOUtil.getCDOObject(auditCompany);
      CDOStateMachine.INSTANCE.invalidate((InternalCDOObject)cdoAuditCompany, null);
    }

    audit.setTimeStamp(commitTime2);

    {
      CDOResource auditResource = audit.getResource(getResourcePath("/res1"));
      Company auditCompany = (Company)auditResource.getContents().get(0);
      assertEquals("Sympedia", auditCompany.getName());
    }

    session.close();
  }
}
