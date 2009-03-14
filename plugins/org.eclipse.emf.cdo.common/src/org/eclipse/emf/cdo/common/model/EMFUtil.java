/**
 * Copyright (c) 2004 - 2009 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Victor Roldan Betancort - http://bugs.eclipse.org/244801
 *    Simon McDuff - maintenance
 */
package org.eclipse.emf.cdo.common.model;

import org.eclipse.net4j.util.WrappedException;
import org.eclipse.net4j.util.io.IORuntimeException;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.EPackage.Registry;
import org.eclipse.emf.ecore.impl.EPackageImpl;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.xmi.impl.EcoreResourceFactoryImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

/**
 * @author Eike Stepper
 * @since 2.0
 */
public final class EMFUtil
{
  private EMFUtil()
  {
  }

  public static Map.Entry<String, Object>[] getSortedRegistryEntries(EPackage.Registry packageRegistry)
  {
    Set<Map.Entry<String, Object>> entries = packageRegistry.entrySet();
    @SuppressWarnings("unchecked")
    Map.Entry<String, Object>[] array = entries.toArray(new Entry[entries.size()]);
    Arrays.sort(array, new Comparator<Map.Entry<String, Object>>()
    {
      public int compare(Map.Entry<String, Object> o1, Map.Entry<String, Object> o2)
      {
        return o1.getKey().compareTo(o2.getKey());
      }
    });

    return array;
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

  public static EClassifier getClassifier(EPackage ePackage, int classifierID)
  {
    EList<EClassifier> classifiers = ePackage.getEClassifiers();
    for (EClassifier classifier : classifiers)
    {
      if (classifier.getClassifierID() == classifierID)
      {
        return classifier;
      }
    }

    return null;
  }

  public static String getParentURI(EPackage ePackage)
  {
    EPackage superPackage = ePackage.getESuperPackage();
    String parentURI = superPackage == null ? null : superPackage.getNsURI();
    return parentURI;
  }

  public static EPackage getTopLevelPackage(EPackage ePackage)
  {
    EPackage superPackage = ePackage.getESuperPackage();
    return superPackage == null ? ePackage : getTopLevelPackage(superPackage);
  }

  /**
   * @since 2.0
   */
  public static EPackage createEPackage(String name, String nsPrefix, String nsURI)
  {
    EPackage ePackage = EcoreFactory.eINSTANCE.createEPackage();
    ePackage.setName(name);
    ePackage.setNsPrefix(nsPrefix);
    ePackage.setNsURI(nsURI);
    return ePackage;
  }

  /**
   * @since 2.0
   */
  public static EClass createEClass(EPackage ePackage, String name, boolean isAbstract, boolean isInterface)
  {
    EClass eClass = EcoreFactory.eINSTANCE.createEClass();
    eClass.setName(name);
    eClass.setAbstract(isAbstract);
    eClass.setInterface(isInterface);
    ePackage.getEClassifiers().add(eClass);
    return eClass;
  }

  /**
   * @since 2.0
   */
  public static EAttribute createEAttribute(EClass eClass, String name, EClassifier type)
  {
    EAttribute eAttribute = EcoreFactory.eINSTANCE.createEAttribute();
    eAttribute.setName(name);
    eAttribute.setEType(type);
    eClass.getEStructuralFeatures().add(eAttribute);
    return eAttribute;
  }

  /**
   * @since 2.0
   */
  public static EReference createEReference(EClass eClass, String name, EClassifier type, boolean isRequired,
      boolean isMany)
  {
    EReference eReference = EcoreFactory.eINSTANCE.createEReference();
    eReference.setName(name);
    eReference.setEType(type);
    eReference.setLowerBound(isRequired ? 1 : 0);
    eReference.setUpperBound(isMany ? -1 : 0);
    eClass.getEStructuralFeatures().add(eReference);
    return eReference;
  }

  public static ResourceSet newResourceSet(Resource.Factory resourceFactory)
  {
    ResourceSet resourceSet = new ResourceSetImpl();
    resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("*", resourceFactory);
    return resourceSet;
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

  public static ResourceSet newEcoreResourceSet(EPackage.Registry packageRegistry)
  {
    ResourceSet resourceSet = newResourceSet(new EcoreResourceFactoryImpl());
    resourceSet.setPackageRegistry(packageRegistry);
    return resourceSet;
  }

  public static ResourceSet newEcoreResourceSet()
  {
    return newEcoreResourceSet(EPackage.Registry.INSTANCE);
  }

  public static EObject load(String fileName, ResourceSet resourceSet)
  {
    Resource resource = resourceSet.getResource(URI.createFileURI(fileName), true);
    return resource.getContents().get(0);
  }

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

  public static EObject loadEcore(String fileName, EPackage.Registry packageRegistry)
  {
    return load(fileName, newEcoreResourceSet(packageRegistry));
  }

  public static EObject loadEcore(String fileName)
  {
    return load(fileName, newEcoreResourceSet());
  }

  public static void save(String fileName, EObject root, ResourceSet resourceSet)
  {
    save(fileName, Collections.singletonList(root), resourceSet);
  }

  public static void save(String fileName, List<EObject> roots, ResourceSet resourceSet)
  {
    URI uri = URI.createURI(fileName);
    Resource resource = resourceSet.createResource(uri);
    for (EObject root : roots)
    {
      EObject copy = EcoreUtil.copy(root);
      resource.getContents().add(copy);
    }

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

  public static int countAllContents(EObject eObject)
  {
    int count = 0;
    for (TreeIterator<EObject> it = eObject.eAllContents(); it.hasNext(); it.next())
    {
      ++count;
    }

    return count;
  }

  public static EClass[] getConcreteClasses(EPackage ePackage)
  {
    List<EClass> result = new ArrayList<EClass>(0);
    for (EClassifier classifier : ePackage.getEClassifiers())
    {
      if (classifier instanceof EClass)
      {
        EClass eClass = (EClass)classifier;
        if (!eClass.isAbstract() && !eClass.isInterface())
        {
          result.add(eClass);
        }
      }
    }

    return result.toArray(new EClass[result.size()]);
  }

  public static EClass[] getPersistentClasses(EPackage ePackage)
  {
    List<EClass> result = new ArrayList<EClass>();
    for (EClassifier classifier : ePackage.getEClassifiers())
    {
      if (classifier instanceof EClass)
      {
        result.add((EClass)classifier);
      }
    }

    return result.toArray(new EClass[result.size()]);
  }

  public static List<EStructuralFeature> getPersistentFeatures(EList<EStructuralFeature> eFeatures)
  {
    List<EStructuralFeature> result = new ArrayList<EStructuralFeature>();
    for (EStructuralFeature feature : eFeatures)
    {
      if (feature.isTransient())
      {
        continue;
      }

      // TODO Make configurable via ExtPoint
      if (feature == EcorePackage.eINSTANCE.getEClass_ESuperTypes())
      {
        // See
        // http://www.eclipse.org/newsportal/article.php?id=26780&group=eclipse.tools.emf#26780
        continue;
      }

      if (feature == EcorePackage.eINSTANCE.getETypedElement_EType())
      {
        // See
        // http://www.eclipse.org/newsportal/article.php?id=26780&group=eclipse.tools.emf#26780
        continue;
      }

      if (feature == EcorePackage.eINSTANCE.getEOperation_EExceptions())
      {
        // See
        // http://www.eclipse.org/newsportal/article.php?id=26780&group=eclipse.tools.emf#26780
        continue;
      }

      result.add(feature);
    }

    return result;
  }

  public static boolean isDynamicEPackage(Object value)
  {
    return value.getClass() == EPackageImpl.class;
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

  public static byte[] getEPackageBytes(EPackage ePackage, boolean zipped, EPackage.Registry packageRegistry)
  {
    try
    {
      Resource resource = ePackage.eResource();
      if (resource == null)
      {
        resource = createEcoreResource(ePackage.getNsURI(), packageRegistry);
        resource.getContents().add(ePackage);
      }

      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      resource.save(baos, createResourceOptions(zipped));
      return baos.toByteArray();
    }
    catch (Exception ex)
    {
      throw WrappedException.wrap(ex);
    }
  }

  public static EPackage createEPackage(String uri, byte[] bytes, boolean zipped, EPackage.Registry packageRegistry)
  {
    try
    {
      ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
      Resource resource = createEcoreResource(uri, packageRegistry);
      resource.load(bais, createResourceOptions(zipped));

      EList<EObject> contents = resource.getContents();
      return (EPackage)contents.get(0);
    }
    catch (Exception ex)
    {
      throw WrappedException.wrap(ex);
    }
  }

  private static Resource createEcoreResource(String uri, EPackage.Registry packageRegistry)
  {
    ResourceSet resourceSet = new ResourceSetImpl();
    resourceSet.setPackageRegistry(packageRegistry);

    Resource.Factory resourceFactory = new EcoreResourceFactoryImpl();
    resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("*", resourceFactory);
    resourceSet.getResourceFactoryRegistry().getProtocolToFactoryMap().put("*", resourceFactory);

    return resourceSet.createResource(URI.createURI(uri));
  }

  private static Map<String, Object> createResourceOptions(boolean zipped)
  {
    Map<String, Object> options = new HashMap<String, Object>();
    if (zipped)
    {
      options.put(Resource.OPTION_ZIP, true);
    }

    return options;
  }

  public static void addAdapter(Notifier notifier, Adapter adapter)
  {
    synchronized (notifier)
    {
      EList<Adapter> adapters = notifier.eAdapters();
      if (!adapters.contains(adapter))
      {
        adapters.add(adapter);
      }
    }
  }

  public static EPackage[] getAllPackages(EPackage ePackage)
  {
    List<EPackage> result = new ArrayList<EPackage>();
    getAllPackages(ePackage, result);
    return result.toArray(new EPackage[result.size()]);
  }

  private static void getAllPackages(EPackage ePackage, List<EPackage> result)
  {
    result.add(ePackage);
    for (EPackage subPackage : ePackage.getESubpackages())
    {
      getAllPackages(subPackage, result);
    }
  }

  public static String getQualifiedName(EPackage ePackage, String separator)
  {
    StringBuilder builder = new StringBuilder();
    EPackage eSuperPackage = ePackage.getESuperPackage();
    if (eSuperPackage != null)
    {
      builder.append(getQualifiedName(eSuperPackage, separator));
      builder.append(separator);
    }

    builder.append(ePackage.getName());
    return builder.toString();
  }

  public static String getQualifiedName(EClassifier classifier, String separator)
  {
    StringBuilder builder = new StringBuilder();
    EPackage ePackage = classifier.getEPackage();
    if (ePackage != null)
    {
      builder.append(getQualifiedName(ePackage, separator));
      builder.append(separator);
    }

    builder.append(classifier.getName());
    return builder.toString();
  }
}
