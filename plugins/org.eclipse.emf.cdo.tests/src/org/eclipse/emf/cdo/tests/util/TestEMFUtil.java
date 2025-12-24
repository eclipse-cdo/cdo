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
 *    Victor Roldan Betancort - maintenance
 */
package org.eclipse.emf.cdo.tests.util;

import org.eclipse.net4j.util.io.IORuntimeException;

import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EPackage.Registry;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.xmi.impl.EcoreResourceFactoryImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * @author Eike Stepper
 */
public final class TestEMFUtil
{
  public static List<EObject> loadMultiple(String fileName, ResourceSet resourceSet)
  {
    Resource resource = resourceSet.getResource(URI.createURI(fileName), true);
    return resource.getContents();
  }

  public static EObject load(String fileName, ResourceSet resourceSet)
  {
    return loadMultiple(fileName, resourceSet).get(0);
  }

  /**
   * returns the first node of the resource content
   */
  public static EObject loadXMI(String fileName, EPackage... ePackages)
  {
    ResourceSet resourceSet = newXMIResourceSet(ePackages);
    return load(fileName, resourceSet);
  }

  public static EObject loadXMI(String fileName, EPackage.Registry packageRegistry)
  {
    ResourceSet resourceSet = newXMIResourceSet();
    resourceSet.setPackageRegistry(packageRegistry);
    return load(fileName, resourceSet);
  }

  public static List<EObject> loadXMIMultiple(String fileName, EPackage.Registry packageRegistry)
  {
    ResourceSet resourceSet = newXMIResourceSet();
    resourceSet.setPackageRegistry(packageRegistry);
    return loadMultiple(fileName, resourceSet);
  }

  public static List<EObject> loadXMIMultiple(String fileName, EPackage... ePackages)
  {
    ResourceSet resourceSet = newXMIResourceSet(ePackages);
    return loadMultiple(fileName, resourceSet);
  }

  public static EPackage loadEcore(String fileName, EPackage.Registry packageRegistry)
  {
    return (EPackage)load(fileName, newEcoreResourceSet(packageRegistry));
  }

  public static EPackage loadEcore(String fileName)
  {
    return (EPackage)load(fileName, newEcoreResourceSet());
  }

  public static void save(String fileName, EObject root, ResourceSet resourceSet)
  {
    save(fileName, Collections.singletonList(root), resourceSet);
  }

  public static void save(String fileName, List<EObject> roots, ResourceSet resourceSet)
  {
    URI uri = URI.createURI(fileName);
    Resource resource = resourceSet.createResource(uri);

    Collection<EObject> copiedRoots = EcoreUtil.copyAll(roots);
    resource.getContents().addAll(copiedRoots);

    try
    {
      resource.save(null);
    }
    catch (IOException ex)
    {
      throw new IORuntimeException(ex);
    }
  }

  public static void saveXMI(String fileName, EObject root)
  {
    save(fileName, root, newXMIResourceSet());
  }

  public static void saveXMI(String fileName, List<EObject> roots)
  {
    save(fileName, roots, newXMIResourceSet());
  }

  public static void saveEcore(String fileName, EObject root)
  {
    save(fileName, root, newEcoreResourceSet());
  }

  public static ResourceSet newXMIResourceSet(EPackage... ePackages)
  {
    ResourceSet resourceSet = newResourceSet(new XMIResourceFactoryImpl());
    if (ePackages != null && ePackages.length != 0)
    {
      Registry packageRegistry = resourceSet.getPackageRegistry();
      for (EPackage ePackage : ePackages)
      {
        packageRegistry.put(ePackage.getNsURI(), ePackage);
      }
    }

    return resourceSet;
  }

  public static ResourceSet newEcoreResourceSet()
  {
    return newEcoreResourceSet(EPackage.Registry.INSTANCE);
  }

  public static ResourceSet newResourceSet(Resource.Factory resourceFactory)
  {
    ResourceSet resourceSet = new ResourceSetImpl();
    resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("*", resourceFactory);
    return resourceSet;
  }

  public static ResourceSet newEcoreResourceSet(EPackage.Registry packageRegistry)
  {
    ResourceSet resourceSet = newResourceSet(new EcoreResourceFactoryImpl());
    resourceSet.setPackageRegistry(packageRegistry);
    return resourceSet;
  }

  public static int countAllContents(EObject eObject)
  {
    int count = 0;
    for (TreeIterator<EObject> it = eObject.eAllContents(); it.hasNext(); it.next())
    {
      ++count;
    }

    return count;
  }

  public static boolean isMany(EStructuralFeature eFeature)
  {
    return eFeature.isMany();
  }

  public static boolean isReference(EStructuralFeature eFeature)
  {
    return eFeature instanceof EReference;
  }

  public static boolean isContainment(EStructuralFeature eFeature)
  {
    if (isReference(eFeature))
    {
      EReference ref = (EReference)eFeature;
      return ref.isContainment();
    }

    return false;
  }

  public static void registerPackage(EPackage ePackage, EPackage.Registry... packageRegistries)
  {
    ePackage.getClass(); // Initialize package in standalone mode
    if (packageRegistries == null || packageRegistries.length == 0)
    {
      EPackage.Registry[] globalRegistry = { EPackage.Registry.INSTANCE };
      packageRegistries = globalRegistry;
    }

    for (EPackage.Registry packageRegistry : packageRegistries)
    {
      packageRegistry.put(ePackage.getNsURI(), ePackage);
    }
  }
}
