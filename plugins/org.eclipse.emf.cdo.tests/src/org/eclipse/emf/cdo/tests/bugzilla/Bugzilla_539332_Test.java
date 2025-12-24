/*
 * Copyright (c) 2018 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.bugzilla;

import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.tests.config.IRepositoryConfig;
import org.eclipse.emf.cdo.transaction.CDOTransaction;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.util.EcoreUtil;

/**
 * Bug 539332 - Repository can't initialize dynamic packages.
 *
 * @author Eike Stepper
 */
public class Bugzilla_539332_Test extends AbstractCDOTest
{
  @Requires(IRepositoryConfig.CAPABILITY_RESTARTABLE)
  public void testRestartWithDynamicPackage() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(getResourcePath("res"));

    EClass eClass = createDynamicEClass();
    EObject eObject = EcoreUtil.create(eClass);

    resource.getContents().add(eObject);
    transaction.commit();

    restartRepository();
  }

  private EClass createDynamicEClass()
  {
    EPackage ePackage = createUniquePackage();

    EClass result = EcoreFactory.eINSTANCE.createEClass();
    result.setName("Dynamic");
    ePackage.getEClassifiers().add(result);
    return result;
  }
}
