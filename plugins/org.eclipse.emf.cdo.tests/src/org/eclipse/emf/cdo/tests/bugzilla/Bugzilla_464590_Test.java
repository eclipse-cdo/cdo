/*
 * Copyright (c) 2015, 2020, 2021 Eike Stepper (Loehne, Germany) and others.
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

import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.tests.model6.ContainmentObject;
import org.eclipse.emf.cdo.tests.model6.ReferenceObject;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.ECrossReferenceAdapter;
import org.eclipse.emf.ecore.xmi.XMIResource;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;

import java.util.Collections;

/**
 * Bug 464590 about {@link EObject#eIsSet(org.eclipse.emf.ecore.EStructuralFeature)} which should not resolve EMF proxy in {@link XMIResource}.
 *
 * @author Esteban Dugueperoux
 */
public class Bugzilla_464590_Test extends AbstractCDOTest
{
  /**
   * Test {@link EObject#eIsSet(org.eclipse.emf.ecore.EStructuralFeature)} with EMF proxies in XMIResource.
   */
  public void testEObjectEIsSetWithXMIResource() throws Exception
  {
    ResourceSet resourceSet = new ResourceSetImpl();
    resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("model6", new XMIResourceFactoryImpl());
    resourceSet.eAdapters().add(new NonResolvingECrossReferenceAdapter());

    URI localFragmentResourceURI = URI.createFileURI(createTempFile("fragment", ".model6").getCanonicalPath());
    Resource localFragmentResource = resourceSet.createResource(localFragmentResourceURI);

    URI localMainResourceURI = URI.createFileURI(createTempFile("main", ".model6").getCanonicalPath());
    Resource localMainResource = resourceSet.createResource(localMainResourceURI);

    ContainmentObject mainContainmentObject = getModel6Factory().createContainmentObject();
    ContainmentObject rootContainmentObject = getModel6Factory().createContainmentObject();
    mainContainmentObject.getContainmentList().add(rootContainmentObject);
    ReferenceObject childReferenceObject = getModel6Factory().createReferenceObject();
    rootContainmentObject.getContainmentList().add(childReferenceObject);
    childReferenceObject.setReferenceOptional(rootContainmentObject);

    localMainResource.getContents().add(mainContainmentObject);
    localFragmentResource.getContents().add(rootContainmentObject);
    localMainResource.save(Collections.emptyMap());
    localFragmentResource.save(Collections.emptyMap());

    // Test
    assertFalse(rootContainmentObject.eIsProxy());
    assertFalse(childReferenceObject.eIsProxy());

    localFragmentResource.unload();

    assertFalse(localFragmentResource.isLoaded());
    assertTrue(rootContainmentObject.eIsProxy());
    assertTrue(childReferenceObject.eIsProxy());

  }

  /**
   * @author Eike Stepper
   */
  private static class NonResolvingECrossReferenceAdapter extends ECrossReferenceAdapter
  {
    @Override
    protected boolean resolve()
    {
      return false;
    }
  }
}
