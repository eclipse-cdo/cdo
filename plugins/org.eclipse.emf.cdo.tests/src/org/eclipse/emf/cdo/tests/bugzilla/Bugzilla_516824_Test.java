/*
 * Copyright (c) 2017 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Maxime Porhel (Obeo) - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.bugzilla;

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.tests.config.IRepositoryConfig;
import org.eclipse.emf.cdo.tests.config.impl.ConfigTest.Requires;
import org.eclipse.emf.cdo.tests.model1.Company;
import org.eclipse.emf.cdo.tests.model1.Supplier;
import org.eclipse.emf.cdo.transaction.CDOTransaction;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.util.EContentAdapter;

/**
 * Bug 516824: Multiple revision instances are loaded during branch switch.
 *
 * @author Maxime Porhel (Obeo)
 */
@Requires(IRepositoryConfig.CAPABILITY_BRANCHING)
public class Bugzilla_516824_Test extends AbstractCDOTest
{
  /**
   * Ensure that there is no ClassCastException thrown during the branch switching.
   */
  public void testSetBranch() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();

    Supplier supplier = getModel1Factory().createSupplier();
    supplier.setName("s0");
    // supplier.setPreferred(false); default value

    Company company = getModel1Factory().createCompany();
    company.setName("c0");
    company.getSuppliers().add(supplier);

    CDOResource resource = transaction.createResource(getResourcePath("newResource.company"));
    resource.getContents().add(company);

    transaction.commit();

    // Make sure invalidation will trigger delta notifications
    resource.eAdapters().add(new EContentAdapter());

    CDOBranch subBranch = transaction.getBranch().createBranch(getBranchName("subBranch"));
    transaction.setBranch(subBranch);
  }

  public void testSetBranchLegacy() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();

    CDOResource resource = transaction.createResource(getResourcePath("newResource.ecore"));
    EPackage ePackage = EcoreFactory.eINSTANCE.createEPackage();
    EClass eClass = EcoreFactory.eINSTANCE.createEClass();

    ePackage.setName("p0");
    eClass.setName("c0");

    resource.getContents().add(ePackage);
    ePackage.getEClassifiers().add(eClass);

    transaction.commit();

    // Make sure invalidation will trigger delta notifications
    resource.eAdapters().add(new EContentAdapter());

    CDOBranch subBranch = transaction.getBranch().createBranch(getBranchName("subBranch"));
    transaction.setBranch(subBranch);
  }
}
