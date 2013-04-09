/*
 * Copyright (c) 2004 - 2012 Esteban Dugueperoux (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Esteban Dugueperoux - initial API and implementation
 *    Christian W. Damus (CEA) - adapted to ensure clear separation of native and legacy models
 */
package org.eclipse.emf.cdo.tests.bugzilla;

import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.tests.config.IModelConfig;
import org.eclipse.emf.cdo.tests.config.impl.ConfigTest.Requires;
import org.eclipse.emf.cdo.tests.model6.CanReferenceLegacy;
import org.eclipse.emf.cdo.transaction.CDOTransaction;

import org.eclipse.emf.ecore.EAnnotation;
import org.eclipse.emf.ecore.EcoreFactory;

import java.util.Arrays;

/**
 * @author Esteban Dugueperoux
 */
// don't execute in legacy configs because it's a native/legacy interaction problem
@Requires(IModelConfig.CAPABILITY_NATIVE)
public class Bugzilla_400236_Test extends AbstractCDOTest
{

  public void testCommit() throws Exception
  {

    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resourceA = transaction.createResource(getResourcePath("test1"));
    CDOResource resourceB = transaction.createResource(getResourcePath("test2"));

    CanReferenceLegacy ref1 = getModel6Factory().createCanReferenceLegacy();
    EAnnotation annot1 = EcoreFactory.eINSTANCE.createEAnnotation(); // legacy object
    annot1.setSource("annot1");
    ref1.setSingleContainment(annot1);
    EAnnotation annot2 = EcoreFactory.eINSTANCE.createEAnnotation(); // legacy object
    annot2.setSource("annot2");
    ref1.getMultipleContainment().add(annot2);

    CanReferenceLegacy ref2 = getModel6Factory().createCanReferenceLegacy();
    ref2.setSingleReference(annot1);
    ref2.getMultipleReference().add(annot2);

    resourceA.getContents().add(ref1);
    resourceB.getContents().add(ref2);
    transaction.commit();
    session.close();

    session = openSession();
    transaction = session.openTransaction();
    resourceA = transaction.getResource(getResourcePath("test1"));
    resourceB = transaction.getResource(getResourcePath("test2"));
    ref1 = (CanReferenceLegacy)resourceA.getContents().get(0);
    ref2 = (CanReferenceLegacy)resourceB.getContents().get(0);

    annot1 = transaction.getObject(annot1);
    annot2 = transaction.getObject(annot2);

    EAnnotation annot3 = EcoreFactory.eINSTANCE.createEAnnotation();
    annot3.setSource("annot3");

    ref1.getMultipleContainment().add(annot3);

    ref2.getMultipleReference().add(annot3);

    assertSame(annot1, ref2.getSingleReference());
    assertSame(ref1.getSingleContainment(), ref2.getSingleReference());
    assertEquals(Arrays.asList(annot2, annot3), ref2.getMultipleReference());
    assertEquals(ref1.getMultipleContainment(), ref2.getMultipleReference());
  }

}
