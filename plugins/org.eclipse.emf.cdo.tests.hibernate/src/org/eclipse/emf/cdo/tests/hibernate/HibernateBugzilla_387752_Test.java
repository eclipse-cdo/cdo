/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Martin Taal
 */
package org.eclipse.emf.cdo.tests.hibernate;

import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.tests.config.IRepositoryConfig;
import org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.Bz387752_Enum;
import org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.Bz387752_Main;
import org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.HibernateTestFactory;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.emf.teneo.PersistenceOptions;

/**
 * Read external reference annotation.
 * 
 * @author Martin Taal
 */
public class HibernateBugzilla_387752_Test extends AbstractCDOTest
{

  @Override
  protected void doSetUp() throws Exception
  {
    final IRepositoryConfig repConfig = getRepositoryConfig();
    final HibernateConfig hbConfig = (HibernateConfig)repConfig;
    hbConfig.getAdditionalProperties().put(PersistenceOptions.HANDLE_UNSET_AS_NULL, "false");
    super.doSetUp();
  }

  @Override
  protected void doTearDown() throws Exception
  {
    final IRepositoryConfig repConfig = getRepositoryConfig();
    final HibernateConfig hbConfig = (HibernateConfig)repConfig;
    hbConfig.getAdditionalProperties().clear();
    super.doTearDown();
  }

  public void testBugzilla() throws Exception
  {
    {
      CDOSession session = openSession();
      CDOTransaction transaction = session.openTransaction();

      CDOResource resource = transaction.createResource(getResourcePath("/test1"));

      Bz387752_Main main = HibernateTestFactory.eINSTANCE.createBz387752_Main();
      main.setEnumSettable(null);
      main.setStrSettable(null);
      // resource.getContents().add(main);

      Bz387752_Main main2 = HibernateTestFactory.eINSTANCE.createBz387752_Main();
      resource.getContents().add(main2);

      transaction.commit();
      session.close();
    }

    clearCache(getRepository().getRevisionManager());

    {
      CDOSession session = openSession();
      CDOView view = session.openView();
      CDOResource cdoResource = view.getResource(getResourcePath("/test1"));
      Bz387752_Main main = (Bz387752_Main)cdoResource.getContents().get(0);
      // assertNull(main.getStrSettable());
      assertEquals(Bz387752_Enum.VAL0, main.getEnumSettable());
      assertEquals("def_value", main.getStrUnsettable());
      assertEquals(Bz387752_Enum.VAL1, main.getEnumUnsettable());

      Bz387752_Main main2 = (Bz387752_Main)cdoResource.getContents().get(1);
      assertNull("value", main2.getStrSettable());
      assertEquals(Bz387752_Enum.VAL0, main2.getEnumSettable());
      assertEquals("def_value", main2.getStrUnsettable());
      assertEquals(Bz387752_Enum.VAL1, main2.getEnumUnsettable());

      session.close();
    }
  }
}
