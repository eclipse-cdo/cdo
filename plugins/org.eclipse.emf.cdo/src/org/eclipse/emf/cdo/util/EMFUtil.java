/***************************************************************************
 * Copyright (c) 2004 - 2008 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.emf.cdo.util;

import org.eclipse.net4j.util.io.IORuntimeException;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.EPackage.Registry;
import org.eclipse.emf.ecore.impl.EClassifierImpl;
import org.eclipse.emf.ecore.impl.EPackageImpl;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.xmi.XMIResource;
import org.eclipse.emf.ecore.xmi.impl.EcoreResourceFactoryImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceImpl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author Eike Stepper
 */
public final class EMFUtil
{
  private static final String ECORE_ENCODING = "ASCII";

  private EMFUtil()
  {
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
    Resource resource = resourceSet.createResource(URI.createFileURI(fileName));
    EObject copy = EcoreUtil.copy(root);
    resource.getContents().add(copy);

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

  public static List<EClass> getPersistentClasses(EPackage ePackage)
  {
    List<EClass> result = new ArrayList<EClass>();
    for (EClassifier classifier : ePackage.getEClassifiers())
    {
      if (classifier instanceof EClass)
      {
        result.add((EClass)classifier);
      }
    }

    return result;
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

      // if (feature == EcorePackage.eINSTANCE.getEGenericType_EClassifier())
      // {
      // continue;
      // }

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

  public static EPackage ePackageFromString(String ecore)
  {
    try
    {
      ByteArrayInputStream stream = new ByteArrayInputStream(ecore.getBytes(ECORE_ENCODING));
      XMIResource resource = new XMIResourceImpl();
      resource.load(stream, null);
      return (EPackage)resource.getContents().get(0);
    }
    catch (RuntimeException ex)
    {
      throw ex;
    }
    catch (IOException ex)
    {
      throw new IORuntimeException(ex);
    }
  }

  public static String ePackageToString(EPackage ePackage, EPackage.Registry packageRegistry)
  {
    synchronized (EMFUtil.class)
    {
      Resource.Factory resourceFactory = new XMIResourceFactoryImpl();
      ResourceSetImpl resourceSet = new ResourceSetImpl()
      // {
      // @Override
      // protected Resource delegatedGetResource(URI uri, boolean loadOnDemand)
      // {
      // System.out.println("\nGET_RESOURCE: " + uri);
      // return delegatedGetResource(uri, loadOnDemand);
      // }
      // }
      ;

      resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("*", resourceFactory);
      resourceSet.getResourceFactoryRegistry().getProtocolToFactoryMap().put("*", resourceFactory);

      Resource packageResource = createPackageResource(resourceSet, ePackage);
      // for (Object object : packageRegistry.values())
      // {
      // if (object != ePackage)
      // {
      // createPackageResource(resourceSet, (EPackage)object);
      // }
      // }

      ByteArrayOutputStream stream = new ByteArrayOutputStream();

      try
      {
        packageResource.save(stream, null);
        String string = stream.toString(ECORE_ENCODING);
        return string;
      }
      catch (RuntimeException ex)
      {
        throw ex;
      }
      catch (IOException ex)
      {
        throw new IORuntimeException(ex);
      }
    }
  }

  private static Resource createPackageResource(ResourceSetImpl resourceSet, EPackage ePackage)
  {
    URI uri = URI.createURI(ePackage.getNsURI());
    Resource resource = resourceSet.createResource(uri);
    resource.getContents().add(ePackage);
    return resource;
  }

  /**
   * TODO Remove when EMF has fixed this
   */
  public static void fixEClassifiers(EPackageImpl ePackage)
  {
    int id = 0;
    for (Iterator<EClassifier> i = ePackage.getEClassifiers().iterator(); i.hasNext();)
    {
      EClassifierImpl eClassifier = (EClassifierImpl)i.next();
      if (eClassifier instanceof EClass)
      {
        eClassifier.setClassifierID(id++);
      }
    }

    for (Iterator<EClassifier> i = ePackage.getEClassifiers().iterator(); i.hasNext();)
    {
      EClassifierImpl eClassifier = (EClassifierImpl)i.next();
      if (eClassifier.getClassifierID() == -1 && eClassifier instanceof EEnum)
      {
        eClassifier.setClassifierID(id++);
      }
    }

    for (Iterator<EClassifier> i = ePackage.getEClassifiers().iterator(); i.hasNext();)
    {
      EClassifierImpl eClassifier = (EClassifierImpl)i.next();
      if (eClassifier.getClassifierID() == -1 && eClassifier instanceof EDataType)
      {
        eClassifier.setClassifierID(id++);
      }
    }

    // try
    // {
    // Method method = EPackageImpl.class.getDeclaredMethod("fixEClassifiers",
    // ReflectUtil.NO_PARAMETERS);
    // if (!method.isAccessible())
    // {
    // method.setAccessible(true);
    // }
    //
    // method.invoke(ePackage, ReflectUtil.NO_ARGUMENTS);
    // }
    // catch (Exception ex)
    // {
    // OM.LOG.error(ex);
    // }
  }

  // public static List<Change> analyzeListDifferences(InternalCDORevision
  // oldRevision,
  // InternalCDORevision newRevision, CDOFeatureImpl feature)
  // {
  // if (!feature.isMany())
  // {
  // throw new IllegalArgumentException("Feature is not many: " + feature);
  // }
  //
  // final List<Object> oldList = (List)oldRevision.getValue(feature);
  // final List newList = (List)newRevision.getValue(feature);
  // final List<Change> changes = new ArrayList(0);
  //
  // new ECollections.ListDifferenceAnalyzer()
  // {
  // @Override
  // protected void add(List<Object> oldList, Object newObject, int ids)
  // {
  // changes.add(new AddChange(newObject, ids));
  // }
  //
  // @Override
  // protected void remove(List<?> oldList, int ids)
  // {
  // changes.add(new RemoveChange(ids));
  // }
  //
  // @Override
  // protected void move(List<?> oldList, int ids, int toIndex)
  // {
  // changes.add(new MoveChange(ids, toIndex));
  // }
  // }.createListChanges(oldList, newList);
  //
  // return changes;
  // }
  //
  // /**
  // * @author Eike Stepper
  // */
  // public static class MoveChange implements FeatureChange
  // {
  // public MoveChange(int ids, int toIndex)
  // {
  // }
  // }
  //
  // /**
  // * @author Eike Stepper
  // */
  // public static class RemoveChange implements FeatureChange
  // {
  // public RemoveChange(int ids)
  // {
  // }
  // }
  //
  // /**
  // * @author Eike Stepper
  // */
  // public static class AddChange implements FeatureChange
  // {
  // public AddChange(Object newObject, int ids)
  // {
  // }
  // }
}
