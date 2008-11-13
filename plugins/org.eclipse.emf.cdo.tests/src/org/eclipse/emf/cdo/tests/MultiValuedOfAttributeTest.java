/***************************************************************************
 * Copyright (c) 2004 - 2008 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Simon McDuff - initial API and implementation
 **************************************************************************/
package org.eclipse.emf.cdo.tests;

import org.eclipse.emf.cdo.CDOSession;
import org.eclipse.emf.cdo.CDOTransaction;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.tests.model5.Doctor;
import org.eclipse.emf.cdo.tests.model5.GenListOfInt;
import org.eclipse.emf.cdo.tests.model5.GenListOfInteger;
import org.eclipse.emf.cdo.tests.model5.GenListOfString;
import org.eclipse.emf.cdo.tests.model5.TestFeatureMap;
import org.eclipse.emf.cdo.util.CDOUtil;

import java.util.List;

/**
 * @author Simon McDuff
 */
public class MultiValuedOfAttributeTest extends AbstractCDOTest
{
  public void testFeatureMaps() throws Exception
  {
    {
      CDOSession session = openSession(getModel5Package());
      CDOTransaction transaction = session.openTransaction();
      CDOResource resource = transaction.createResource("/res1");

      TestFeatureMap featureMap = getModel5Factory().createTestFeatureMap();

      Doctor doctor1 = getModel5Factory().createDoctor();
      Doctor doctor2 = getModel5Factory().createDoctor();
      resource.getContents().add(doctor1);
      resource.getContents().add(doctor2);

      featureMap.getPeople().add(getModel5Package().getTestFeatureMap_Doctors(), doctor1);
      featureMap.getPeople().add(getModel5Package().getTestFeatureMap_Doctors(), doctor2);

      resource.getContents().add(featureMap);

      assertEquals(doctor1, featureMap.getPeople().get(0).getValue());
      List<?> listForFeatureCustomers = (List<?>)featureMap.getPeople().get(
          getModel5Package().getTestFeatureMap_Doctors(), true);
      assertEquals(doctor1, listForFeatureCustomers.get(0));
      assertEquals(doctor2, listForFeatureCustomers.get(1));
      transaction.commit();
    }

    clearCache(getRepository().getRevisionManager());

    CDOSession session = openSession(getModel5Package());
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.getResource("/res1");

    TestFeatureMap featureMap = (TestFeatureMap)resource.getContents().get(0);
    List<?> listForFeatureCustomers = (List<?>)featureMap.getPeople().get(
        getModel5Package().getTestFeatureMap_Doctors(), true);
    assertTrue(listForFeatureCustomers.get(0) instanceof Doctor);
    assertTrue(listForFeatureCustomers.get(1) instanceof Doctor);
  }

  public void testListOfString() throws Exception
  {
    {
      CDOSession session = openSession(getModel5Package());
      CDOTransaction transaction = session.openTransaction();
      CDOResource resource = transaction.createResource("/res1");

      GenListOfString listOfString = getModel5Factory().createGenListOfString();

      listOfString.getElements().add("Ottawa");
      listOfString.getElements().add("Toronto");
      resource.getContents().add(listOfString);
      transaction.commit();
    }

    clearCache(getRepository().getRevisionManager());

    CDOSession session = openSession(getModel5Package());
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.getResource("/res1");

    GenListOfString listOfString = (GenListOfString)resource.getContents().get(0);
    assertEquals("Ottawa", listOfString.getElements().get(0));
    assertEquals("Toronto", listOfString.getElements().get(1));

    listOfString.getElements().add("Vancouver");
    transaction.commit();
  }

  public void testListOfStringProxy() throws Exception
  {
    {
      CDOSession session = openSession(getModel5Package());
      CDOTransaction transaction = session.openTransaction();
      CDOResource resource = transaction.createResource("/res1");

      GenListOfString listOfString = getModel5Factory().createGenListOfString();

      listOfString.getElements().add("Ottawa");
      listOfString.getElements().add("Toronto");
      resource.getContents().add(listOfString);
      transaction.commit();
    }

    clearCache(getRepository().getRevisionManager());

    CDOSession session = openSession(getModel5Package());
    session.setCollectionLoadingPolicy(CDOUtil.createCollectionLoadingPolicy(0, 100));
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.getResource("/res1");

    GenListOfString listOfString = (GenListOfString)resource.getContents().get(0);
    assertEquals("Ottawa", listOfString.getElements().get(0));
    assertEquals("Toronto", listOfString.getElements().get(1));

    listOfString.getElements().add("Vancouver");
    transaction.commit();
  }

  public void testListOfInt() throws Exception
  {
    {
      CDOSession session = openSession(getModel5Package());
      CDOTransaction transaction = session.openTransaction();
      CDOResource resource = transaction.createResource("/res1");

      GenListOfInt genList = getModel5Factory().createGenListOfInt();

      genList.getElements().add(1);
      genList.getElements().add(2);
      resource.getContents().add(genList);
      transaction.commit();
    }

    clearCache(getRepository().getRevisionManager());

    CDOSession session = openSession(getModel5Package());
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.getResource("/res1");

    GenListOfInt genList = (GenListOfInt)resource.getContents().get(0);
    assertEquals((Integer)1, genList.getElements().get(0));
    assertEquals((Integer)2, genList.getElements().get(1));

    genList.getElements().add(3);
    transaction.commit();
  }

  public void testListOfInteger() throws Exception
  {
    {
      CDOSession session = openSession(getModel5Package());
      CDOTransaction transaction = session.openTransaction();
      CDOResource resource = transaction.createResource("/res1");

      GenListOfInteger genList = getModel5Factory().createGenListOfInteger();

      genList.getElements().add(1);
      genList.getElements().add(2);
      genList.getElements().add(null);
      resource.getContents().add(genList);
      transaction.commit();
    }

    clearCache(getRepository().getRevisionManager());

    CDOSession session = openSession(getModel5Package());
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.getResource("/res1");

    GenListOfInteger genList = (GenListOfInteger)resource.getContents().get(0);
    assertEquals((Integer)1, genList.getElements().get(0));
    assertEquals((Integer)2, genList.getElements().get(1));
    assertEquals(null, genList.getElements().get(2));

    genList.getElements().add(3);
    transaction.commit();
  }
}
