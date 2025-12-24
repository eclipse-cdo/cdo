/*
 * Copyright (c) 2016, 2020 Eike Stepper (Loehne, Germany) and others.
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
import org.eclipse.emf.cdo.tests.model1.Company;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CDOCrossReferenceAdapter;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.ECrossReferenceAdapter;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;

/**
 * Bug 467075 - Provide ECrossReferenceAdapter that does not recreate removed CDOResource
 *
 * @author Eike Stepper
 */
public class Bugzilla_467075_Test extends AbstractCDOTest
{
  public void testResourceSetRemoveCDOResource() throws Exception
  {
    ECrossReferenceAdapter adapter = createAdapter();
    Company company = getModel1Factory().createCompany();

    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(getResourcePath("test1.model1"));
    resource.getContents().add(company);
    transaction.commit();

    doTest(transaction.getResourceSet(), resource, adapter);
  }

  public void testResourceSetRemoveXMIResource() throws Exception
  {
    Company company = getModel1Factory().createCompany();
    ECrossReferenceAdapter adapter = createAdapter();

    ResourceSet resourceSet = new ResourceSetImpl();
    resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("model1", new XMIResourceFactoryImpl());

    URI uri = URI.createFileURI(createTempFile("resource", ".model1").getCanonicalPath());
    Resource resource = resourceSet.createResource(uri);
    resource.getContents().add(company);
    resource.save(null);

    doTest(resourceSet, resource, adapter);
  }

  private ECrossReferenceAdapter createAdapter()
  {
    return new CDOCrossReferenceAdapter();
  }

  private void doTest(ResourceSet resourceSet, Resource resource, ECrossReferenceAdapter adapter)
  {
    resource.eAdapters().add(adapter);
    resourceSet.getResources().remove(resource);
    resource.eAdapters().remove(adapter);
    assertEquals(0, resourceSet.getResources().size());
  }
}
