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
import org.eclipse.emf.cdo.internal.protocol.model.CDOClassProxy;
import org.eclipse.emf.cdo.internal.protocol.model.CDOClassRefImpl;
import org.eclipse.emf.cdo.internal.protocol.model.CDOFeatureImpl;
import org.eclipse.emf.cdo.internal.protocol.model.CDOPackageImpl;
import org.eclipse.emf.cdo.internal.protocol.model.CDOPackageManagerImpl;
import org.eclipse.emf.cdo.internal.protocol.model.CDOTypeImpl;
import org.eclipse.emf.cdo.internal.protocol.model.core.CDOCorePackageImpl;
import org.eclipse.emf.cdo.internal.protocol.model.resource.CDOResourceClassImpl;
import org.eclipse.emf.cdo.internal.protocol.model.resource.CDOResourcePackageImpl;
import org.eclipse.emf.cdo.protocol.model.CDOClass;
import org.eclipse.emf.cdo.protocol.model.CDOFeature;
import org.eclipse.emf.cdo.protocol.model.CDOPackage;
import org.eclipse.emf.cdo.protocol.util.ImplementationError;

import org.eclipse.net4j.util.om.trace.ContextTracer;

import org.eclipse.emf.common.util.EList;
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
  public static final CDOPackageManagerImpl PACKAGE_MANAGER = new CDOPackageManagerImpl();

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

  public static CDOPackageImpl getCDOPackage(EPackage ePackage, CDOPackageManagerImpl packageManager)
  {
    String packageURI = ePackage.getNsURI();
    CDOPackageImpl cdoPackage = packageManager.lookupPackage(packageURI);
    if (cdoPackage == null)
    {
      cdoPackage = convertPackage(ePackage, packageManager);
      packageManager.addPackage(cdoPackage);
    }

    return cdoPackage;
  }

  public static CDOClassImpl getCDOClass(EClass eClass, CDOPackageManagerImpl packageManager)
  {
    CDOPackageImpl cdoPackage = getCDOPackage(eClass.getEPackage(), packageManager);
    return cdoPackage.lookupClass(eClass.getClassifierID());
  }

  public static CDOFeatureImpl getCDOFeature(EStructuralFeature eFeature, CDOPackageManagerImpl packageManager)
  {
    CDOClassImpl cdoClass = getCDOClass(eFeature.getEContainingClass(), packageManager);
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

  public static void addModelInfos(CDOPackageManagerImpl packageManager)
  {
    // Ecore
    CDOCorePackageImpl corePackage = packageManager.getCDOCorePackage();
    corePackage.setClientInfo(EcorePackage.eINSTANCE);
    corePackage.getCDOObjectClass().setClientInfo(EcorePackage.eINSTANCE.getEObject());

    // Eresource
    CDOResourcePackageImpl resourcePackage = packageManager.getCDOResourcePackage();
    resourcePackage.setClientInfo(EresourcePackage.eINSTANCE);
    CDOResourceClassImpl resourceClass = resourcePackage.getCDOResourceClass();
    resourceClass.setClientInfo(EresourcePackage.eINSTANCE.getCDOResource());
    resourceClass.getCDOContentsFeature().setClientInfo(EresourcePackage.eINSTANCE.getCDOResource_Contents());
    resourceClass.getCDOPathFeature().setClientInfo(EresourcePackage.eINSTANCE.getCDOResource_Path());
  }

  public static void removeModelInfos(CDOPackageManagerImpl packageManager)
  {
    // Ecore
    CDOCorePackageImpl corePackage = packageManager.getCDOCorePackage();
    corePackage.setClientInfo(null);
    corePackage.getCDOObjectClass().setClientInfo(null);

    // Eresource
    CDOResourcePackageImpl resourcePackage = packageManager.getCDOResourcePackage();
    resourcePackage.setClientInfo(null);
    CDOResourceClassImpl resourceClass = resourcePackage.getCDOResourceClass();
    resourceClass.setClientInfo(null);
    resourceClass.getCDOContentsFeature().setClientInfo(null);
    resourceClass.getCDOPathFeature().setClientInfo(null);
  }

  private static CDOPackageImpl convertPackage(EPackage ePackage, CDOPackageManagerImpl packageManager)
  {
    CDOPackageImpl cdoPackage = new CDOPackageImpl(packageManager, ePackage.getNsURI(), ePackage.getName());
    cdoPackage.setClientInfo(ePackage);
    for (EClass eClass : getPersistentClasses(ePackage))
    {
      CDOClassImpl cdoClass = convertClass(eClass, packageManager);
      cdoPackage.addClass(cdoClass);
    }

    return cdoPackage;
  }

  private static CDOClassImpl convertClass(EClass eClass, CDOPackageManagerImpl packageManager)
  {
    CDOClassImpl cdoClass = new CDOClassImpl(eClass.getClassifierID(), eClass.getName(), eClass.isAbstract());
    cdoClass.setClientInfo(eClass);

    for (EClass superEClass : eClass.getESuperTypes())
    {
      CDOClassRefImpl classRef = createClassRef(superEClass);
      cdoClass.addSuperType(classRef);
    }

    for (EStructuralFeature eFeature : getPersistentFeatures(eClass.getEStructuralFeatures()))
    {
      CDOFeatureImpl cdoFeature = convertFeature(eFeature, packageManager);
      cdoClass.addFeature(cdoFeature);
    }

    initAllSuperTypes(cdoClass, packageManager);
    initAllFeatures(cdoClass, packageManager);
    return cdoClass;
  }

  private static CDOFeatureImpl convertFeature(EStructuralFeature eFeature, CDOPackageManagerImpl packageManager)
  {
    CDOFeatureImpl cdoFeature = isReference(eFeature) ? convertReference(eFeature, packageManager)
        : convertAttribute(eFeature);
    cdoFeature.setClientInfo(eFeature);
    return cdoFeature;
  }

  private static CDOFeatureImpl convertReference(EStructuralFeature eFeature, CDOPackageManagerImpl packageManager)
  {
    int featureID = eFeature.getFeatureID();
    String name = eFeature.getName();
    CDOClassRefImpl classRef = createClassRef(eFeature.getEType());
    boolean many = eFeature.isMany();
    boolean containment = isContainment(eFeature);
    return new CDOFeatureImpl(featureID, name, new CDOClassProxy(classRef, packageManager), many, containment);
  }

  private static CDOFeatureImpl convertAttribute(EStructuralFeature eFeature)
  {
    int featureID = eFeature.getFeatureID();
    String name = eFeature.getName();
    CDOTypeImpl type = getCDOType(eFeature);
    boolean many = isMany(eFeature);
    return new CDOFeatureImpl(featureID, name, type, many);
  }

  private static void initAllSuperTypes(CDOClassImpl cdoClass, CDOPackageManagerImpl packageManager)
  {
    EClass eClass = getEClass(cdoClass);
    EList<EClass> eClasses = eClass.getEAllSuperTypes();
    CDOClassImpl[] allSuperTypes = new CDOClassImpl[eClasses.size()];

    int i = 0;
    for (EClass superEClass : eClasses)
    {
      CDOClassImpl superType = getCDOClass(superEClass, packageManager);
      allSuperTypes[i++] = superType;
    }

    cdoClass.setAllSuperTypes(allSuperTypes);
  }

  private static void initAllFeatures(CDOClassImpl cdoClass, CDOPackageManagerImpl packageManager)
  {
    EClass eClass = getEClass(cdoClass);
    List<EStructuralFeature> eFeatures = getPersistentFeatures(eClass.getEAllStructuralFeatures());
    CDOFeatureImpl[] allFeatures = new CDOFeatureImpl[eFeatures.size()];

    int i = 0;
    for (EStructuralFeature eFeature : eFeatures)
    {
      CDOFeatureImpl cdoFeature = getCDOFeature(eFeature, packageManager);
      if (cdoFeature.getFeatureID() != i)
      {
        throw new ImplementationError("Wrong featureID: " + cdoFeature);
      }

      allFeatures[i++] = cdoFeature;
    }

    cdoClass.setAllFeatures(allFeatures);
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
