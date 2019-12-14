/*
 * Copyright (c) 2009-2012, 2016, 2019 Eike Stepper (Loehne, Germany) and others.
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

import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.tests.model4.GenRefMapNonContained;
import org.eclipse.emf.cdo.tests.model4.GenRefSingleContained;
import org.eclipse.emf.cdo.transaction.CDOTransaction;

import org.eclipse.emf.ecore.EObject;

import java.util.ArrayList;
import java.util.List;

/**
 * ClassCastException on BasicEMap.clear
 * <p>
 * See bug 259695
 *
 * @author Simon McDuff
 */
public class Bugzilla_259695_Test extends AbstractCDOTest
{
  public void testBugzilla_259695_ClassCast() throws Exception
  {
    CDOSession session = openSession();
    session.getPackageRegistry().putEPackage(getModel4InterfacesPackage());
    session.getPackageRegistry().putEPackage(getModel4Package());

    CDOTransaction transaction = session.openTransaction();

    CDOResource resource1 = transaction.createResource(getResourcePath("test1"));

    GenRefMapNonContained elementA = getModel4Factory().createGenRefMapNonContained();
    resource1.getContents().add(elementA);
    List<EObject> expectedValue = new ArrayList<>();
    for (int i = 0; i < 10; i++)
    {
      GenRefSingleContained value = getModel4Factory().createGenRefSingleContained();
      elementA.getElements().put(String.valueOf(i), value);
      resource1.getContents().add(value);
      expectedValue.add(value);
    }

    elementA.getElements().clear();
    transaction.commit();
  }
}
