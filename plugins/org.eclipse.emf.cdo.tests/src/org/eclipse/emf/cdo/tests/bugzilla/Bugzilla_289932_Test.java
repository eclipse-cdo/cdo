/*
 * Copyright (c) 2009, 2011, 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Simon McDuff - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.bugzilla;

import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.tests.model5.Doctor;
import org.eclipse.emf.cdo.tests.model5.TestFeatureMap;
import org.eclipse.emf.cdo.transaction.CDOTransaction;

/**
 * Objects that are contained by a transient feature could be persisted
 * <p>
 * See bug 289932
 * 
 * @author Simon McDuff
 */
public class Bugzilla_289932_Test extends AbstractCDOTest
{
  public void testBugzilla_289932() throws Exception
  {
    CDOSession session = openSession();
    session.getPackageRegistry().putEPackage(getModel5Package());
    CDOTransaction transaction = session.openTransaction();
    CDOResource res1 = transaction.createResource(getResourcePath("/res1"));

    TestFeatureMap testFeatureMap = getModel5Factory().createTestFeatureMap();
    Doctor doctor = getModel5Factory().createDoctor();
    testFeatureMap.getDoctors().add(doctor);
    res1.getContents().add(testFeatureMap);
    transaction.commit();

    // Transient feature for Doctor, should not persist it.
    assertTransient(doctor);
  }
}
