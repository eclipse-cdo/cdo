/*
 * Copyright (c) 2012, 2015, 2016, 2020 Esteban Dugueperoux and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Esteban Dugueperoux - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.bugzilla;

import org.eclipse.emf.cdo.eresource.CDOResourceFactory;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;

import java.util.Collections;

/**
 * ResourceSet.getResource(URI,true) fails when called several times. See bug 395999.
 *
 * @author Esteban Dugueperoux
 */
public class Bugzilla_395999_Test extends AbstractCDOTest
{
  public void testTwiceGetCDOResourceOnResourceSetImpl() throws Exception
  {
    URI uri = URI.createURI(getURIPrefix() + "/" + getRepository().getName() + getResourcePath("/res1") + "?transactional=true");

    ResourceSet resourceSet = new ResourceSetImpl();
    resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put(getURIProtocol(), CDOResourceFactory.INSTANCE);

    Resource resource = resourceSet.createResource(uri);
    resource.save(Collections.emptyMap());

    loadTwiceAndSaveResource(uri);
  }

  public void testTwiceGetXMIResourceOnResourceSetImpl() throws Exception
  {
    ResourceSet resourceSet = new ResourceSetImpl();
    resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("model1", new XMIResourceFactoryImpl());

    URI uri = URI.createFileURI(createTempFile(getName(), ".model1").getCanonicalPath());
    Resource resource = resourceSet.createResource(uri);
    resource.save(Collections.emptyMap());

    loadTwiceAndSaveResource(uri);
  }

  private void loadTwiceAndSaveResource(URI resourceURI) throws Exception
  {
    ResourceSet resourceSet = new ResourceSetImpl();
    resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put(getURIProtocol(), CDOResourceFactory.INSTANCE);
    resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("model1", new XMIResourceFactoryImpl());

    Resource resource = resourceSet.getResource(resourceURI, true);
    assertEquals("The ResourceSetImpl should returns the same Resource as in the first call", resource, resourceSet.getResource(resourceURI, true));

    resource.save(Collections.emptyMap());
  }
}
