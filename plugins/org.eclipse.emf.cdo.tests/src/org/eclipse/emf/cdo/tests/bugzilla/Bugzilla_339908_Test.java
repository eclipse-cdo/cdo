/*
 * Copyright (c) 2011, 2012 Eike Stepper (Loehne, Germany) and others.
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
import org.eclipse.emf.cdo.tests.model3.ClassWithIDAttribute;
import org.eclipse.emf.cdo.transaction.CDOTransaction;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;

import java.io.ByteArrayOutputStream;
import java.util.Map;

/**
 * @author Eike Stepper
 */
public class Bugzilla_339908_Test extends AbstractCDOTest
{
  public void testIDAttribute() throws Exception
  {
    String[] ids = new String[10];

    {
      CDOSession session = openSession();
      CDOTransaction transaction = session.openTransaction();
      CDOResource resource = transaction.createResource(getResourcePath("test"));

      for (int i = 0; i < ids.length; i++)
      {
        String id = java.util.UUID.randomUUID().toString();
        ids[i] = id;

        ClassWithIDAttribute object = getModel3Factory().createClassWithIDAttribute();
        object.setId(id);
        resource.getContents().add(object);
      }

      transaction.commit();

      clearCache(getRepository().getRevisionManager());
    }

    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.getResource(getResourcePath("test"));

    int i = 0;
    EList<EObject> contents = resource.getContents();
    for (EObject object : contents)
    {
      assertEquals(ids[i], ((ClassWithIDAttribute)object).getId());
      assertEquals(ids[i], EcoreUtil.getID(object));
      ++i;
    }
  }

  @SuppressWarnings("unused")
  private String createXMI() throws Exception
  {
    ResourceSet resourceSet = new ResourceSetImpl();
    Map<String, Object> map = resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap();
    map.put("xmi", new XMIResourceFactoryImpl());
    Resource xmiResource = resourceSet.createResource(URI.createURI("test.xmi"));

    for (int i = 0; i < 10; i++)
    {
      ClassWithIDAttribute object = getModel3Factory().createClassWithIDAttribute();
      object.setId(java.util.UUID.randomUUID().toString());
      xmiResource.getContents().add(object);
    }

    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    xmiResource.save(baos, null);

    String xmi = baos.toString();
    System.out.println(xmi);
    return xmi;
  }
}
