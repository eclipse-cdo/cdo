/***************************************************************************
 * Copyright (c) 2004-2007 Eike Stepper, Germany.
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
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.impl.EClassifierImpl;
import org.eclipse.emf.ecore.impl.EPackageImpl;
import org.eclipse.emf.ecore.xmi.XMIResource;
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

  public static List<EClass> getPersistentClasses(EPackage ePackage)
  {
    List<EClass> result = new ArrayList();
    for (EClassifier classifier : ePackage.getEClassifiers())
    {
      if (classifier instanceof EClass)
      {
        result.add((EClass)classifier);
      }
    }

    return result;
  }

  public static List<EStructuralFeature> getPersistentFeatures(EList<EStructuralFeature> eFeatues)
  {
    List<EStructuralFeature> result = new ArrayList();
    for (EStructuralFeature feature : eFeatues)
    {
      if (!feature.isTransient())
      {
        result.add(feature);
      }
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

  public static String ePackageToString(EPackage ePackage)
  {
    ByteArrayOutputStream stream = new ByteArrayOutputStream();
    XMIResource resource = new XMIResourceImpl();

    try
    {
      resource.getContents().add(ePackage);
      resource.save(stream, null);
      return stream.toString(ECORE_ENCODING);
    }
    catch (RuntimeException ex)
    {
      throw ex;
    }
    catch (IOException ex)
    {
      throw new IORuntimeException(ex);
    }
    finally
    {
      resource.getContents().clear();
    }
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

  // public static List<Change> analyzeListDifferences(CDORevisionImpl
  // oldRevision,
  // CDORevisionImpl newRevision, CDOFeatureImpl feature)
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
  // protected void add(List<Object> oldList, Object newObject, int index)
  // {
  // changes.add(new AddChange(newObject, index));
  // }
  //
  // @Override
  // protected void remove(List<?> oldList, int index)
  // {
  // changes.add(new RemoveChange(index));
  // }
  //
  // @Override
  // protected void move(List<?> oldList, int index, int toIndex)
  // {
  // changes.add(new MoveChange(index, toIndex));
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
  // public MoveChange(int index, int toIndex)
  // {
  // }
  // }
  //
  // /**
  // * @author Eike Stepper
  // */
  // public static class RemoveChange implements FeatureChange
  // {
  // public RemoveChange(int index)
  // {
  // }
  // }
  //
  // /**
  // * @author Eike Stepper
  // */
  // public static class AddChange implements FeatureChange
  // {
  // public AddChange(Object newObject, int index)
  // {
  // }
  // }
}
