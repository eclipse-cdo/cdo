/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.defs;

import org.eclipse.emf.cdo.defs.CDODefsFactory;
import org.eclipse.emf.cdo.defs.EGlobalPackageDef;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;

import org.eclipse.emf.ecore.EPackage;

/**
 * @author Eike Stepper
 */
public class EGlobalPackageDefImplTest extends AbstractCDOTest
{
  public void testEGlobalPackageDefReturnsPackage()
  {
    EPackage model1Package = getModel1Package();
    EPackage.Registry.INSTANCE.put(model1Package.getNsURI(), model1Package);

    EGlobalPackageDef packageDef = CDODefsFactory.eINSTANCE.createEGlobalPackageDef();
    packageDef.setNsURI(model1Package.getNsURI());

    packageDef.getInstance();
  }
}
