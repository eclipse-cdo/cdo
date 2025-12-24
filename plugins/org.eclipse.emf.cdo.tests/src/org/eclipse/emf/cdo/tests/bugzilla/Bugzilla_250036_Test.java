/*
 * Copyright (c) 2008-2012, 2016, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Simon McDuff - initial API and implementation
 *    Eike Stepper - maintenance
 */
package org.eclipse.emf.cdo.tests.bugzilla;

import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.tests.model4.GenRefMapNonContained;
import org.eclipse.emf.cdo.tests.model4.GenRefSingleContained;
import org.eclipse.emf.cdo.tests.util.TestAdapter;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CDOUtil;

import org.eclipse.emf.common.util.EMap;
import org.eclipse.emf.ecore.EObject;

import java.util.ArrayList;
import java.util.List;

/**
 * See bug 250036
 *
 * @author Simon McDuff
 */
public class Bugzilla_250036_Test extends AbstractCDOTest
{
  public void testBugzilla_250036_Invalidation() throws Exception
  {
    CDOSession session = openSession();
    session.getPackageRegistry().putEPackage(getModel4InterfacesPackage());
    session.getPackageRegistry().putEPackage(getModel4Package());

    CDOTransaction transaction = session.openTransaction();

    CDOResource resource1 = transaction.createResource(getResourcePath("test1"));
    CDOResource resource2 = transaction.createResource(getResourcePath("test2"));

    GenRefMapNonContained elementA = getModel4Factory().createGenRefMapNonContained();
    resource1.getContents().add(elementA);
    List<EObject> expectedValue = new ArrayList<>();
    for (int i = 0; i < 10; i++)
    {
      GenRefSingleContained value = getModel4Factory().createGenRefSingleContained();
      elementA.getElements().put(String.valueOf(i), value);
      resource2.getContents().add(value);
      expectedValue.add(value);
    }

    transaction.commit();

    /********* transaction 2 ***************/
    CDOTransaction transaction2 = session.openTransaction();
    CDOResource resource1FromTx2 = transaction2.getResource(resource1.getPath());

    GenRefMapNonContained genRefMap = (GenRefMapNonContained)resource1FromTx2.getContents().get(0);
    assertClean(genRefMap, transaction2);
    assertEquals(10, genRefMap.getElements().size());
    EMap<String, EObject> mapOfEObject = genRefMap.getElements();
    for (int i = 0; i < 10; i++)
    {
      EObject object = mapOfEObject.get(String.valueOf(i));
      assertNotNull(object);
      assertEquals(CDOUtil.getCDOObject(expectedValue.get(i)).cdoID(), CDOUtil.getCDOObject(object).cdoID());
    }

    final TestAdapter adapter = new TestAdapter(genRefMap);
    transaction2.options().setInvalidationNotificationEnabled(true);

    /********* transaction 1 ***************/
    final int counter[] = { 0 };
    for (int i = 10; i < 20; i++)
    {
      GenRefSingleContained value = getModel4Factory().createGenRefSingleContained();
      elementA.getElements().put(String.valueOf(i), value);
      resource2.getContents().add(value);
      expectedValue.add(value);
      ++counter[0];
    }

    transaction.commit();

    msg("Checking after commit");
    // 10 delta notifications from local commit (same session) plus 1 invalidation notification
    adapter.assertNoTimeout(1 + counter[0]);

    /********* transaction 2 ***************/
    EMap<String, EObject> mapOfEObjectAfterCommit = genRefMap.getElements();
    assertSame(mapOfEObject, mapOfEObjectAfterCommit);
    assertEquals(20, mapOfEObjectAfterCommit.size());
    for (int i = 0; i < 20; i++)
    {
      EObject object = mapOfEObject.get(String.valueOf(i));
      assertNotNull(object);
      assertEquals(CDOUtil.getCDOObject(expectedValue.get(i)).cdoID(), CDOUtil.getCDOObject(object).cdoID());
    }
  }
}
