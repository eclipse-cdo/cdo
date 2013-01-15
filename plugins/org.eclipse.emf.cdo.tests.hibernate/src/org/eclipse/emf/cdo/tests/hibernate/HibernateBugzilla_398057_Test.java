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

import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.tests.config.IRepositoryConfig;
import org.eclipse.emf.cdo.tests.config.impl.ConfigTest.CleanRepositoriesBefore;
import org.eclipse.emf.cdo.tests.config.impl.ConfigTest.Requires;
import org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.Bz398057A;
import org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.Bz398057B1;
import org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.HibernateTestFactory;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CommitException;

/**
 * @author Martin Taal
 */
@CleanRepositoriesBefore
@Requires(IRepositoryConfig.CAPABILITY_AUDITING)
public class HibernateBugzilla_398057_Test extends AbstractCDOTest
{

  public void testIssue() throws Exception
  {
    CDOSession session = openSession();
    {
      try
      {
        CDOTransaction transaction = session.openTransaction();
        CDOResource resource = transaction.createResource(getResourcePath("/res1"));
        Bz398057A a = HibernateTestFactory.eINSTANCE.createBz398057A();
        a.setId("1");
        Bz398057B1 b1 = HibernateTestFactory.eINSTANCE.createBz398057B1();
        b1.setId("1");
        b1.setValueStr("str");
        b1.setValue(1.0);
        a.getListOfB().add(b1);
        resource.getContents().add(a);
        transaction.commit();
        fail(); // expected to fail
      }
      catch (CommitException e)
      {
        // fine
      }
    }
  }
}
