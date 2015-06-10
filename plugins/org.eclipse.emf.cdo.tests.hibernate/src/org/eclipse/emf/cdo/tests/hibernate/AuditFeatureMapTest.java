/*
 * Copyright (c) 2012, 2015 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Simon McDuff - initial API and implementation
 *    Eike Stepper - maintenance
 *    Stefan Winkler - enhanced test cases
 */
package org.eclipse.emf.cdo.tests.hibernate;

import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.tests.config.IRepositoryConfig;
import org.eclipse.emf.cdo.tests.config.impl.ConfigTest.Requires;
import org.eclipse.emf.cdo.tests.model5.Doctor;
import org.eclipse.emf.cdo.tests.model5.Manager;
import org.eclipse.emf.cdo.tests.model5.TestFeatureMap;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.util.FeatureMap;

import java.util.HashMap;
import java.util.Map;

/**
 * NOTE: this testcase does not pass currently for CDO.
 *
 * @author Simon McDuff
 * @Author Martin Taal
 */
@Requires(IRepositoryConfig.CAPABILITY_AUDITING)
public class AuditFeatureMapTest extends AbstractCDOTest
{

  @Skips("Postgresql")
  public void testFeatureMaps() throws Exception
  {
    skipStoreWithoutFeatureMaps();

    EReference doctorFeature = getModel5Package().getTestFeatureMap_Doctors();
    EReference managerFeature = getModel5Package().getTestFeatureMap_Managers();

    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(getResourcePath("/res1"));

    TestFeatureMap featureMap = getModel5Factory().createTestFeatureMap();
    resource.getContents().add(featureMap);
    FeatureMap people = featureMap.getPeople();

    Map<Long, Map<EStructuralFeature, Class<?>>> checkResult = new HashMap<Long, Map<EStructuralFeature, Class<?>>>();

    {
      long commitTime = transaction.commit().getTimeStamp();
      addToResultMap(commitTime, checkResult, people);
    }

    // add a few of them
    {
      Manager manager1 = getModel5Factory().createManager();
      Doctor doctor1 = getModel5Factory().createDoctor();
      Manager manager2 = getModel5Factory().createManager();
      Doctor doctor2 = getModel5Factory().createDoctor();

      resource.getContents().add(doctor1);
      resource.getContents().add(doctor2);
      resource.getContents().add(manager1);
      resource.getContents().add(manager2);

      people.add(managerFeature, manager1);
      people.add(doctorFeature, doctor1);
      people.add(managerFeature, manager2);
      people.add(doctorFeature, doctor2);
      long commitTime = transaction.commit().getTimeStamp();
      addToResultMap(commitTime, checkResult, people);
    }

    // delete one
    {
      people.remove(1);
      long commitTime = transaction.commit().getTimeStamp();
      addToResultMap(commitTime, checkResult, people);
    }

    // add a few of them
    {
      Manager manager1 = getModel5Factory().createManager();
      Doctor doctor1 = getModel5Factory().createDoctor();
      Manager manager2 = getModel5Factory().createManager();
      Doctor doctor2 = getModel5Factory().createDoctor();

      resource.getContents().add(doctor1);
      resource.getContents().add(doctor2);
      resource.getContents().add(manager1);
      resource.getContents().add(manager2);

      people.add(managerFeature, manager1);
      people.add(doctorFeature, doctor1);
      people.add(managerFeature, manager2);
      people.add(doctorFeature, doctor2);
      long commitTime = transaction.commit().getTimeStamp();
      addToResultMap(commitTime, checkResult, people);
    }

    // check it all
    for (long timeStamp : checkResult.keySet())
    {
      CDOView audit = session.openView(timeStamp);
      {
        CDOResource auditResource = audit.getResource(getResourcePath("/res1"));
        TestFeatureMap testFeatureMap = (TestFeatureMap)auditResource.getContents().get(0);
        checkFeatureMap(testFeatureMap.getPeople(), checkResult.get(timeStamp));
      }
    }
  }

  private void addToResultMap(long timeStamp, Map<Long, Map<EStructuralFeature, Class<?>>> result,
      FeatureMap featureMap)
  {
    final Map<EStructuralFeature, Class<?>> entryResult = new HashMap<EStructuralFeature, Class<?>>();
    result.put(timeStamp, entryResult);
    for (FeatureMap.Entry entry : featureMap)
    {
      entryResult.put(entry.getEStructuralFeature(), entry.getValue().getClass());
    }
  }

  private void checkFeatureMap(FeatureMap featureMap, Map<EStructuralFeature, Class<?>> checkMap)
  {
    assertEquals(featureMap.size(), checkMap.size());
    for (EStructuralFeature eFeature : checkMap.keySet())
    {
      final Object value = checkMap.get(eFeature);
      assertEquals(value.getClass(), featureMap.get(eFeature, true).getClass());
    }
  }
}
