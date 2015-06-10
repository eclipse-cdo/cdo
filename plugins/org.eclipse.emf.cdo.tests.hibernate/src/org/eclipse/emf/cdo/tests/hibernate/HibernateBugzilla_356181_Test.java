/*
 * Copyright (c) 2012, 2015 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Martin Taal - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.hibernate;

import org.eclipse.emf.cdo.CDOObjectReference;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.Bz356181_Main;
import org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.Bz356181_NonTransient;
import org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.Bz356181_Transient;
import org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.HibernateTestFactory;
import org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.HibernateTestPackage;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CDOUtil;
import org.eclipse.emf.cdo.view.CDOView;

import java.util.Collections;
import java.util.List;

/**
 *
 * @author Martin Taal
 */
public class HibernateBugzilla_356181_Test extends AbstractCDOTest
{
  public void testBugzilla() throws Exception
  {
    {
      CDOSession session = openSession();
      CDOTransaction transaction = session.openTransaction();

      CDOResource resource = transaction.createResource(getResourcePath("/test1"));

      Bz356181_Main main = HibernateTestFactory.eINSTANCE.createBz356181_Main();
      main.setNonTransient("non transient");
      main.setTransient("transient");
      resource.getContents().add(main);

      Bz356181_NonTransient nonTransient = HibernateTestFactory.eINSTANCE.createBz356181_NonTransient();
      nonTransient.setMain(main);
      resource.getContents().add(nonTransient);

      Bz356181_Transient trans = HibernateTestFactory.eINSTANCE.createBz356181_Transient();
      main.setTransientRef(trans);

      main.setTransientOtherRef(HibernateTestFactory.eINSTANCE.createBz356181_NonTransient());

      transaction.commit();
      session.close();
    }

    clearCache(getRepository().getRevisionManager());

    {
      CDOSession session = openSession();
      CDOView view = session.openView();
      CDOResource cdoResource = view.getResource(getResourcePath("/test1"));
      Bz356181_Main main = (Bz356181_Main)cdoResource.getContents().get(0);
      assertNull(main.getTransient());
      assertNull(main.getTransientRef());
      assertNull(main.getTransientOtherRef());
      assertEquals("non transient", main.getNonTransient());
      List<CDOObjectReference> results = view.queryXRefs(Collections.singleton(CDOUtil.getCDOObject(main)));
      assertEquals(1, results.size());
      assertEquals(2, cdoResource.getContents().size());
      assertEquals(1, results.size());
      assertEquals(true,
          results.get(0).getSourceObject().eClass() == HibernateTestPackage.eINSTANCE.getBz356181_NonTransient());
      assertEquals(true,
          results.get(0).getTargetObject().eClass() == HibernateTestPackage.eINSTANCE.getBz356181_Main());
      session.close();
    }
  }
}
