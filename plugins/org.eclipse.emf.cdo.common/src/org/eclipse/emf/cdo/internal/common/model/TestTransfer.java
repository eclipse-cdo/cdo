/**
 * Copyright (c) 2004 - 2009 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.internal.common.model;

import org.eclipse.net4j.util.WrappedException;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.EcoreResourceFactoryImpl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * @author Eike Stepper
 */
public class TestTransfer
{
  public static void main(String[] args) throws IOException
  {
    // Create packageA
    //
    EAttribute attributeA = EcoreFactory.eINSTANCE.createEAttribute();
    attributeA.setName("attributeA");
    attributeA.setEType(EcorePackage.eINSTANCE.getEString());

    EClass classA = EcoreFactory.eINSTANCE.createEClass();
    classA.setName("classA");
    classA.getEStructuralFeatures().add(attributeA);

    EPackage packageA = EcoreFactory.eINSTANCE.createEPackage();
    packageA.setName("packageA");
    packageA.setNsPrefix("packageA");
    packageA.setNsURI("http://packageA");
    packageA.getEClassifiers().add(classA);

    // Create packageB
    //
    EAttribute attributeB = EcoreFactory.eINSTANCE.createEAttribute();
    attributeB.setName("attributeB");
    attributeB.setEType(EcorePackage.eINSTANCE.getEString());

    EClass classB = EcoreFactory.eINSTANCE.createEClass();
    classB.setName("classA");
    classB.getEStructuralFeatures().add(attributeB);
    classB.getESuperTypes().add(classA);

    EPackage packageB = EcoreFactory.eINSTANCE.createEPackage();
    packageB.setName("packageB");
    packageB.setNsPrefix("packageB");
    packageB.setNsURI("http://packageB");
    packageB.getEClassifiers().add(classB);

    // Simulate generated global packages
    //
    EPackage.Registry.INSTANCE.put(packageA.getNsURI(), packageA);
    EPackage.Registry.INSTANCE.put(packageB.getNsURI(), packageB);

    // Serialize both packages
    //
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    serialize(packageA, baos);
    serialize(packageB, baos);
  }

  private static void serialize(EPackage ePackage, OutputStream out) throws IOException
  {
    Resource resource = ePackage.eResource();
    if (resource == null)
    {
      resource = createPackageResource(ePackage.getNsURI());
      resource.getContents().add(ePackage);
    }

    resource.save(out, null);
  }

  private static Resource createPackageResource(String uri)
  {
    Resource.Factory resourceFactory = new EcoreResourceFactoryImpl();

    ResourceSet resourceSet = new ResourceSetImpl();
    resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("*", resourceFactory);
    resourceSet.getResourceFactoryRegistry().getProtocolToFactoryMap().put("*", resourceFactory);

    return resourceSet.createResource(URI.createURI(uri));
  }

  public static class UTIL
  {
    public static byte[] getPackageBytes(EPackage ePackage, boolean zipped)
    {
      try
      {
        Resource resource = ePackage.eResource();
        if (resource == null)
        {
          resource = createPackageResource(ePackage.getNsURI());
          resource.getContents().add(ePackage);
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        resource.save(baos, null);
        return baos.toByteArray();
      }
      catch (Exception ex)
      {
        throw WrappedException.wrap(ex);
      }
    }

    public static EPackage createPackage(String uri, byte[] bytes, boolean zipped)
    {
      try
      {
        ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
        Resource resource = createPackageResource(uri);
        resource.load(bais, null);

        EList<EObject> contents = resource.getContents();
        return (EPackage)contents.get(0);
      }
      catch (Exception ex)
      {
        throw WrappedException.wrap(ex);
      }
    }
  }
}
