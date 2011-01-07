/**
 * Copyright (c) 2004 - 2011 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Andre Dietisheim - initial API and implementation
 *    Eike Stepper - maintenance
 */
package org.eclipse.emf.cdo.tests.defs;

import org.eclipse.emf.cdo.defs.CDODefsFactory;
import org.eclipse.emf.cdo.defs.CDOPackageRegistryDef;
import org.eclipse.emf.cdo.defs.EGlobalPackageDef;
import org.eclipse.emf.cdo.defs.EPackageDef;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.tests.model1.Model1Package;
import org.eclipse.emf.cdo.tests.model2.Model2Package;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EPackage;

/**
 * @author Andre Dietisheim
 */
public class CDOPackageRegistryDefImplTest extends AbstractCDOTest
{
  public void testReturnsPackageInstances()
  {
    CDOPackageRegistryDef packageRegistryDef = CDODefsFactory.eINSTANCE.createCDOPackageRegistryDef();
    EList<EPackageDef> packages = packageRegistryDef.getPackages();
    Model1Package model1package = getModel1Package();
    packages.add(createEGlobalPackageDef(model1package));
    Model2Package model2Package = getModel2Package();
    packages.add(createEGlobalPackageDef(model2Package));

    EPackage.Registry packageRegistry = (EPackage.Registry)packageRegistryDef.getInstance();
    EPackage ePackage1 = packageRegistry.getEPackage(getModel1Package().getNsURI());
    assertEquals(true, ePackage1 instanceof Model1Package);
    EPackage ePackage2 = packageRegistry.getEPackage(getModel2Package().getNsURI());
    assertEquals(true, ePackage2 instanceof Model2Package);
  }

  private EGlobalPackageDef createEGlobalPackageDef(EPackage ePackage)
  {
    EGlobalPackageDef eGlobalPackageDef = CDODefsFactory.eINSTANCE.createEGlobalPackageDef();
    eGlobalPackageDef.setNsURI(ePackage.getNsURI());
    return eGlobalPackageDef;
  }
}
