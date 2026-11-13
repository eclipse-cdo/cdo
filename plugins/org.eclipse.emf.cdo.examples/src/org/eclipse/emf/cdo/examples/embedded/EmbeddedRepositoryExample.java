/*
 * Copyright (c) 2017, 2024, 2025 Eike Stepper (Loehne, Germany) and others.
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
import org.eclipse.emf.cdo.examples.company.Company;
import org.eclipse.emf.cdo.examples.company.CompanyFactory;
import org.eclipse.emf.cdo.transaction.CDOTransaction;

import org.eclipse.emf.ecore.EObject;

/**
 * @author Eike Stepper
 */
public class EmbeddedRepositoryExample
{
  public static void main(String[] args) throws Exception
  {
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
