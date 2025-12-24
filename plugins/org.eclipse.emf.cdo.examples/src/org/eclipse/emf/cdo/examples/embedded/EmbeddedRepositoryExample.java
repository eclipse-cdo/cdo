/*
 * Copyright (c) 2017, 2024 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.examples.embedded;

import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.eresource.EresourcePackage;
import org.eclipse.emf.cdo.etypes.EtypesPackage;
import org.eclipse.emf.cdo.examples.company.Company;
import org.eclipse.emf.cdo.examples.company.CompanyFactory;
import org.eclipse.emf.cdo.examples.company.CompanyPackage;
import org.eclipse.emf.cdo.transaction.CDOTransaction;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EcorePackage;

/**
 * @author Eike Stepper
 */
public class EmbeddedRepositoryExample
{
  public static void main(String[] args) throws Exception
  {
    // In stand-alone initialize the needed EPackages eagerly!
    EcorePackage.eINSTANCE.getClass();
    EresourcePackage.eINSTANCE.getClass();
    EtypesPackage.eINSTANCE.getClass();
    CompanyPackage.eINSTANCE.getClass();

    CDOFacade.INSTANCE.activate();

    try
    {
      CDOTransaction transaction = CDOFacade.INSTANCE.getTransaction();
      CDOResource resource = transaction.getOrCreateResource("test1");

      Company company = CompanyFactory.eINSTANCE.createCompany();
      company.setName("Company-" + System.currentTimeMillis());

      resource.getContents().add(company);
      transaction.commit();

      for (EObject object : resource.getContents())
      {
        if (object instanceof Company)
        {
          System.out.println(((Company)object).getName());
        }
      }
    }
    finally
    {
      CDOFacade.INSTANCE.deactivate();
    }
  }
}
