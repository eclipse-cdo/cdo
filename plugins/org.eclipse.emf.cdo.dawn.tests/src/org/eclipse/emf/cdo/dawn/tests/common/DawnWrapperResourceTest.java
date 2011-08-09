/**
 * Copyright (c) 2004 - 2011 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Martin Fluegge - initial API and implementation
 */
package org.eclipse.emf.cdo.dawn.tests.common;

import org.eclipse.emf.cdo.dawn.resources.DawnWrapperResource;
import org.eclipse.emf.cdo.dawn.tests.AbstractDawnTest;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.config.impl.ConfigTest.CleanRepositoriesBefore;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceImpl;

import java.util.Collections;

/**
 * @author Martin Fluegge
 */
@CleanRepositoriesBefore
public class DawnWrapperResourceTest extends AbstractDawnTest
{
  public void testPostEventTransactionHandler() throws Exception
  {
    // TransactionalEditingDomain editingDomain = DawnGMFEditingDomainFactory.getInstance().createEditingDomain();
    CDOSession session = openSession();

    ResourceSet resourceSet = createResourceSet();
    session.openTransaction(resourceSet);
    resourceSet.getResourceFactoryRegistry().getProtocolToFactoryMap().put("acore_diagram", new XMIResourceImpl());

    Resource resource = resourceSet.createResource(URI.createURI("dawn://repo1/default10.acore_diagram"));

    assertInstanceOf(DawnWrapperResource.class, resource);

    resource.save(Collections.EMPTY_MAP);

    session.close();
  }
}
