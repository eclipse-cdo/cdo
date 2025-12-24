/*
 * Copyright (c) 2014, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Esteban Dugueperoux
 */
package org.eclipse.emf.cdo.tests.bugzilla;

import org.eclipse.emf.cdo.CDOObjectReference;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.tests.model6.A;
import org.eclipse.emf.cdo.tests.model6.CanReferenceLegacy;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CDOUtil;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature.Setting;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.EcoreUtil;

import java.util.Collection;
import java.util.List;

/**
 * Test {@link CDOView#queryXRefs(org.eclipse.emf.cdo.CDOObject, EReference...)} with cross references typed EObject.
 *
 * @author Esteban Dugueperoux
 */
public class Bugzilla_417782_Test extends AbstractCDOTest
{

  public void testEObjectReference() throws Exception
  {
    A itemA = getModel6Factory().createA();
    CanReferenceLegacy itemRefContainer = getModel6Factory().createCanReferenceLegacy();

    itemRefContainer.setSingleReference(itemA);

    CDOSession session1 = openSession();
    CDOTransaction transaction = session1.openTransaction();

    CDOResource resource = transaction.createResource(getResourcePath("/test1"));
    resource.getContents().add(itemA);
    resource.getContents().add(itemRefContainer);

    transaction.commit();

    List<CDOObjectReference> rc = transaction.queryXRefs(CDOUtil.getCDOObject(itemA));

    assertEquals(1, rc.size());
    Collection<Setting> rc2 = EcoreUtil.UsageCrossReferencer.find(itemA, (Resource)resource);
    assertEquals(1, rc2.size());
  }

}
