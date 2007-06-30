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
package org.eclipse.emf.internal.cdo.util;

import org.eclipse.emf.cdo.eresource.EresourcePackage;
import org.eclipse.emf.cdo.internal.protocol.bundle.CDOProtocol;
import org.eclipse.emf.cdo.internal.protocol.model.CDOClassImpl;
import org.eclipse.emf.cdo.internal.protocol.model.CDOClassRefImpl;
import org.eclipse.emf.cdo.internal.protocol.model.CDOFeatureImpl;
import org.eclipse.emf.cdo.internal.protocol.model.CDOPackageImpl;
import org.eclipse.emf.cdo.internal.protocol.model.CDOPackageManagerImpl;
import org.eclipse.emf.cdo.internal.protocol.model.CDOTypeImpl;
import org.eclipse.emf.cdo.internal.protocol.model.core.CDOCorePackageImpl;
import org.eclipse.emf.cdo.internal.protocol.model.core.CDOObjectClassImpl;
import org.eclipse.emf.cdo.internal.protocol.model.resource.CDOContentsFeatureImpl;
import org.eclipse.emf.cdo.internal.protocol.model.resource.CDOPathFeatureImpl;
import org.eclipse.emf.cdo.internal.protocol.model.resource.CDOResourceClassImpl;
import org.eclipse.emf.cdo.internal.protocol.model.resource.CDOResourcePackageImpl;
import org.eclipse.emf.cdo.protocol.model.CDOClass;
import org.eclipse.emf.cdo.protocol.model.CDOFeature;
import org.eclipse.emf.cdo.protocol.model.CDOPackage;

import org.eclipse.net4j.util.om.trace.ContextTracer;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EcorePackage;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Eike Stepper
 */
public final class EMFUtil
{
  private static final CDOPackageManagerImpl PACKAGE_MANAGER = CDOPackageManagerImpl.INSTANCE;

  @SuppressWarnings("unused")
  private static final ContextTracer TRACER = new ContextTracer(CDOProtocol.DEBUG_MODEL, EMFUtil.class);

  private EMFUtil()
  {
  }

  public static CDOClassRefImpl createClassRef(EClassifier classifier)
  {
    if (classifier instanceof EClass)
    {
      String packageURI = classifier.getEPackage().getNsURI();
      int classifierID = classifier.getClassifierID();
      return new CDOClassRefImpl(packageURI, classifierID);
    }

    return null;
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

  public static List<EStructuralFeature> getPersistentFeatures(EClass eClass)
  {
    List<EStructuralFeature> result = new ArrayList();
    for (EStructuralFeature feature : eClass.getEStructuralFeatures())
    {
      if (!feature.isTransient())
      {
        result.add(feature);
      }
    }

    return result;
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

  public static CDOTypeImpl getCDOType(EStructuralFeature eFeature)
  {
    if (eFeature instanceof EReference)
    {
      return CDOTypeImpl.OBJECT;
    }

    EClassifier classifier = eFeature.getEType();
    if (classifier.getEPackage() == EcorePackage.eINSTANCE)
    {
      int classifierID = classifier.getClassifierID();
      switch (classifierID)
      {
      case EcorePackage.EBOOLEAN:
      case EcorePackage.EBOOLEAN_OBJECT:
      case EcorePackage.EBYTE:
      case EcorePackage.EBYTE_OBJECT:
      case EcorePackage.ECHAR:
      case EcorePackage.ECHARACTER_OBJECT:
      case EcorePackage.EDATE:
      case EcorePackage.EDOUBLE:
      case EcorePackage.EDOUBLE_OBJECT:
      case EcorePackage.EFLOAT:
      case EcorePackage.EFLOAT_OBJECT:
      case EcorePackage.EINT:
      case EcorePackage.EINTEGER_OBJECT:
      case EcorePackage.ELONG:
      case EcorePackage.ELONG_OBJECT:
      case EcorePackage.ESHORT:
      case EcorePackage.ESHORT_OBJECT:
        return CDOTypeImpl.getType(classifierID);
      }
    }

    return CDOTypeImpl.STRING;
  }

  public static CDOPackageImpl getCDOPackage(EPackage ePackage)
  {
    String packageURI = ePackage.getNsURI();
    CDOPackageImpl cdoPackage = PACKAGE_MANAGER.lookupPackage(packageURI);
    if (cdoPackage == null)
    {
      cdoPackage = convertPackage(ePackage);
      PACKAGE_MANAGER.addPackage(cdoPackage);
    }

    return cdoPackage;
  }

  public static CDOClassImpl getCDOClass(EClass eClass)
  {
    CDOPackageImpl cdoPackage = getCDOPackage(eClass.getEPackage());
    return cdoPackage.lookupClass(eClass.getClassifierID());
  }

  public static CDOFeatureImpl getCDOFeature(EStructuralFeature eFeature)
  {
    CDOClassImpl cdoClass = getCDOClass(eFeature.getEContainingClass());
    return cdoClass.lookupFeature(eFeature.getFeatureID());
  }

  public static EPackage getEPackage(CDOPackage cdoPackage)
  {
    return (EPackage)cdoPackage.getClientInfo();
  }

  public static EClass getEClass(CDOClass cdoClass)
  {
    return (EClass)cdoClass.getClientInfo();
  }

  public static EStructuralFeature getEFeature(CDOFeature cdoFeature)
  {
    return (EStructuralFeature)cdoFeature.getClientInfo();
  }

  public static void addModelInfos()
  {
    // Ecore
    CDOCorePackageImpl.INSTANCE.setClientInfo(EcorePackage.eINSTANCE);
    CDOObjectClassImpl.INSTANCE.setClientInfo(EcorePackage.eINSTANCE.getEObject());

    // Eresource
    CDOResourcePackageImpl.INSTANCE.setClientInfo(EresourcePackage.eINSTANCE);
    CDOResourceClassImpl.INSTANCE.setClientInfo(EresourcePackage.eINSTANCE.getCDOResource());
    CDOContentsFeatureImpl.INSTANCE.setClientInfo(EresourcePackage.eINSTANCE.getCDOResource_Contents());
    CDOPathFeatureImpl.INSTANCE.setClientInfo(EresourcePackage.eINSTANCE.getCDOResource_Path());
  }

  public static void removeModelInfos()
  {
    // Ecore
    CDOCorePackageImpl.INSTANCE.setClientInfo(null);
    CDOObjectClassImpl.INSTANCE.setClientInfo(null);

    // Eresource
    CDOResourcePackageImpl.INSTANCE.setClientInfo(null);
    CDOResourceClassImpl.INSTANCE.setClientInfo(null);
    CDOContentsFeatureImpl.INSTANCE.setClientInfo(null);
    CDOPathFeatureImpl.INSTANCE.setClientInfo(null);
  }

  private static CDOPackageImpl convertPackage(EPackage ePackage)
  {
    CDOPackageImpl cdoPackage = new CDOPackageImpl(ePackage.getNsURI(), ePackage.getName());
    cdoPackage.setClientInfo(ePackage);
    for (EClass eClass : getPersistentClasses(ePackage))
    {
      CDOClassImpl cdoClass = convertClass(eClass);
      cdoPackage.addClass(cdoClass);
    }

    return cdoPackage;
  }

  private static CDOClassImpl convertClass(EClass eClass)
  {
    CDOClassImpl cdoClass = new CDOClassImpl(eClass.getClassifierID(), eClass.getName(), eClass.isAbstract());
    cdoClass.setClientInfo(eClass);
    for (EStructuralFeature eFeature : getPersistentFeatures(eClass))
    {
      CDOFeatureImpl cdoFeature = convertFeature(eFeature);
      cdoClass.addFeature(cdoFeature);
    }

    return cdoClass;
  }

  private static CDOFeatureImpl convertFeature(EStructuralFeature eFeature)
  {
    CDOFeatureImpl cdoFeature = // 
    isReference(eFeature) ? convertReference(eFeature) : convertAttribute(eFeature);
    cdoFeature.setClientInfo(eFeature);
    return cdoFeature;
  }

  private static CDOFeatureImpl convertReference(EStructuralFeature eFeature)
  {
    int featureID = eFeature.getFeatureID();
    String name = eFeature.getName();
    CDOClassRefImpl classRef = createClassRef(eFeature.getEType());
    boolean many = eFeature.isMany();
    boolean containment = isContainment(eFeature);
    return new CDOFeatureImpl(featureID, name, classRef, many, containment);
  }

  private static CDOFeatureImpl convertAttribute(EStructuralFeature eFeature)
  {
    int featureID = eFeature.getFeatureID();
    String name = eFeature.getName();
    CDOTypeImpl type = getCDOType(eFeature);
    boolean many = isMany(eFeature);
    return new CDOFeatureImpl(featureID, name, type, many);
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
