/*
 * Copyright (c) 2013 Eike Stepper (Berlin, Germany) and others.
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
import org.eclipse.emf.cdo.tests.config.impl.ConfigTest.Requires;
import org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.Bz397682C;
import org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.Bz397682P;
import org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.HibernateTestFactory;
import org.eclipse.emf.cdo.transaction.CDOTransaction;

/**
 * @author Martin Taal
 */
@Requires(IRepositoryConfig.CAPABILITY_AUDITING)
public class HibernateBugzilla_397682_Test extends AbstractCDOTest
{

  public void testIssue() throws Exception
  {
    CDOSession session = openSession();
    {
      CDOTransaction transaction = session.openTransaction();
      CDOResource resource = transaction.createResource(getResourcePath("/res1"));
      Bz397682P p = HibernateTestFactory.eINSTANCE.createBz397682P();
      p.setDbId("1");
      Bz397682C c1 = HibernateTestFactory.eINSTANCE.createBz397682C();
      c1.setDbId("2");
      p.getListOfC().add(c1);
      assertNotNull(c1.getRefToP());
      resource.getContents().add(p);
      transaction.commit();
      assertNotNull(c1.getRefToP());
    }
    {
      CDOTransaction transaction = session.openTransaction();
      CDOResource resource = transaction.getResource(getResourcePath("/res1"));
      Bz397682P p = (Bz397682P)resource.getContents().get(0);
      Bz397682C c2 = HibernateTestFactory.eINSTANCE.createBz397682C();
      c2.setDbId("3");
      c2.setRefToC(p.getListOfC().get(0));
      p.getListOfC().add(c2);
      assertNotNull(c2.getRefToP());
      resource.getContents().add(p);
      transaction.commit();
      assertNotNull(c2.getRefToP());
      assertNotNull(p.getListOfC().get(0).getRefToP());
    }
  }
}
