/**
 * Copyright (c) 2004 - 2009 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Simon McDuff - initial API and implementation
 *    Eike Stepper - maintenance
 */
package org.eclipse.emf.cdo.tests;

import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.model5.Doctor;
import org.eclipse.emf.cdo.tests.model5.TestFeatureMap;
import org.eclipse.emf.cdo.transaction.CDOTransaction;

import java.util.List;

/**
 * @author Simon McDuff
 */
public class FeatureMapTest extends AbstractCDOTest
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
}
