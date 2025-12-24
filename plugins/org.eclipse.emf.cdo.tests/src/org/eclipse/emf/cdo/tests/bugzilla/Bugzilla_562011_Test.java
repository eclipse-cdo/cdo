/*
 * Copyright (c) 2020 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Robert Schulk - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.bugzilla;

import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.tests.bundle.OM;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;

import Testmodel562011.util.Testmodel562011ResourceFactoryImpl;

/**
 * @author Robert Schulk
 */
public class Bugzilla_562011_Test extends AbstractCDOTest
{
  public void testDocumentRoot_FeatureMap() throws Exception
  {
    Resource resource = new Testmodel562011ResourceFactoryImpl().createResource(null);
    resource.load(OM.BUNDLE.getInputStream("model/Testmodel_562011.xml"), null);

    EObject eObject = resource.getContents().get(0);
    System.out.println(eObject);
  }
}
