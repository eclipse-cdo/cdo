/**
 * Copyright (c) 2004 - 2010 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.dawn.tests;

import org.eclipse.emf.cdo.dawn.resources.DawnWrapperResource;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.transaction.CDOTransaction;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceImpl;

import java.util.Collections;

/**
 * @author Martin Fluegge
 */
public class DawnWrapperResourceTest extends AbstractCDOTest
{

  public void testPostEventTransactionHandler() throws Exception
  {

    // TransactionalEditingDomain editingDomain = DawnGMFEditingDomainFactory.getInstance().createEditingDomain();

    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();

    ResourceSet resourceSet = new ResourceSetImpl();
    resourceSet.getResourceFactoryRegistry().getProtocolToFactoryMap().put("acore_diagram", new XMIResourceImpl());

    // ResourceSet resourceSet = editingDomain.getResourceSet();

    Resource resource = resourceSet.createResource(URI.createURI("dawn://repo1//default10.acore_diagram"));

    assertInstanceOf(DawnWrapperResource.class, resource);

    resource.save(Collections.EMPTY_MAP);

    session.close();
  }
}
