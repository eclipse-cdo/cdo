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
import org.eclipse.emf.cdo.util.EMFUtil;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.impl.EPackageImpl;
import org.eclipse.emf.internal.cdo.CDOFactoryImpl;
import org.eclipse.emf.internal.cdo.CDOPackageRegistryImpl;

/**
 * @author Eike Stepper
 */
public final class ModelUtil
{
  private ModelUtil()
  {
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
      cdoPackage = createCDOPackage(ePackage, packageManager);
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

  private static CDOPackageImpl createCDOPackage(EPackage ePackage, CDOPackageManagerImpl packageManager)
  {
    String packageURI = ePackage.getNsURI();
    String name = ePackage.getName();
    boolean dynamic = EMFUtil.isDynamicEPackage(ePackage);
    String ecore = dynamic ? EMFUtil.ePackageToString(ePackage) : null;

    CDOPackageImpl cdoPackage = new CDOPackageImpl(packageManager, packageURI, name, ecore, dynamic);
    cdoPackage.setClientInfo(ePackage);
    for (EClass eClass : EMFUtil.getPersistentClasses(ePackage))
    {
      CDOClassImpl cdoClass = createCDOClass(cdoPackage, eClass, packageManager);
      cdoPackage.addClass(cdoClass);
    }

    return cdoPackage;
  }

  private static CDOClassImpl createCDOClass(CDOPackageImpl containingPackage, EClass eClass,
      CDOPackageManagerImpl packageManager)
  {
    CDOClassImpl cdoClass = new CDOClassImpl(containingPackage, eClass.getClassifierID(), eClass.getName(), eClass
        .isAbstract());
    cdoClass.setClientInfo(eClass);

    // XXX for (EClass superEClass : eClass.getESuperTypes())
    // {
    // CDOClassRefImpl classRef = createClassRef(superEClass);
    // cdoClass.addSuperType(classRef);
    // }

    for (EStructuralFeature eFeature : EMFUtil.getPersistentFeatures(eClass.getEStructuralFeatures()))
    {
      CDOFeatureImpl cdoFeature = createCDOFeature(cdoClass, eFeature, packageManager);
      cdoClass.addFeature(cdoFeature);
    }

    // XXX initAllSuperTypes(cdoClass, packageManager);
    // XXX initAllFeatures(cdoClass, packageManager);
    return cdoClass;
  }

  private static CDOFeatureImpl createCDOFeature(CDOClassImpl containingClass, EStructuralFeature eFeature,
      CDOPackageManagerImpl packageManager)
  {
    CDOFeatureImpl cdoFeature = EMFUtil.isReference(eFeature) ? createCDOReference(containingClass, eFeature,
        packageManager) : createCDOAttribute(containingClass, eFeature);
    cdoFeature.setClientInfo(eFeature);
    return cdoFeature;
  }

  private static CDOFeatureImpl createCDOReference(CDOClassImpl containingClass, EStructuralFeature eFeature,
      CDOPackageManagerImpl packageManager)
  {
    int featureID = eFeature.getFeatureID();
    String name = eFeature.getName();
    CDOClassRefImpl classRef = createClassRef(eFeature.getEType());
    boolean many = eFeature.isMany();
    boolean containment = EMFUtil.isContainment(eFeature);
    return new CDOFeatureImpl(containingClass, featureID, name, new CDOClassProxy(classRef, packageManager), many,
        containment);
  }

  private static CDOFeatureImpl createCDOAttribute(CDOClassImpl containingClass, EStructuralFeature eFeature)
  {
    int featureID = eFeature.getFeatureID();
    String name = eFeature.getName();
    CDOTypeImpl type = getCDOType(eFeature);
    boolean many = EMFUtil.isMany(eFeature);
    return new CDOFeatureImpl(containingClass, featureID, name, type, many);
  }

  public static EPackage getEPackage(CDOPackageImpl cdoPackage, CDOPackageRegistryImpl packageRegistry)
  {
    EPackage ePackage = (EPackage)cdoPackage.getClientInfo();
    if (ePackage == null)
    {
      ePackage = createEPackage(cdoPackage);
      packageRegistry.internalPut(ePackage.getNsURI(), ePackage);
      cdoPackage.setClientInfo(ePackage);
    }

    return ePackage;
  }

  public static EClass getEClass(CDOClassImpl cdoClass, CDOPackageRegistryImpl packageRegistry)
  {
    EClass eClass = (EClass)cdoClass.getClientInfo();
    if (eClass == null)
    {
      EPackage ePackage = getEPackage(cdoClass.getContainingPackage(), packageRegistry);
      eClass = (EClass)ePackage.getEClassifier(cdoClass.getName());
      cdoClass.setClientInfo(eClass);
    }

    return eClass;
  }

  public static EStructuralFeature getEFeature(CDOFeatureImpl cdoFeature, CDOPackageRegistryImpl packageRegistry)
  {
    EStructuralFeature eFeature = (EStructuralFeature)cdoFeature.getClientInfo();
    if (eFeature == null)
    {
      EClass eClass = getEClass(cdoFeature.getContainingClass(), packageRegistry);
      eFeature = eClass.getEStructuralFeature(cdoFeature.getFeatureID());
      cdoFeature.setClientInfo(eFeature);
    }

    return eFeature;
  }

  private static EPackage createEPackage(CDOPackageImpl cdoPackage)
  {
    String ecore = cdoPackage.getEcore();
    EPackageImpl ePackage = (EPackageImpl)EMFUtil.ePackageFromString(ecore);
    prepareEPackage(ePackage);
    return ePackage;
  }

  public static void prepareEPackage(EPackageImpl ePackage)
  {
    ePackage.setEFactoryInstance(new CDOFactoryImpl(ePackage));
    EMFUtil.fixEClassifiers(ePackage);
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

  // private static void initAllSuperTypes(CDOClassImpl cdoClass,
  // CDOPackageManagerImpl packageManager)
  // {
  // EClass eClass = getEClass(cdoClass);
  // EList<EClass> eClasses = eClass.getEAllSuperTypes();
  // CDOClassImpl[] allSuperTypes = new CDOClassImpl[eClasses.size()];
  //
  // int i = 0;
  // for (EClass superEClass : eClasses)
  // {
  // CDOClassImpl superType = getCDOClass(superEClass, packageManager);
  // allSuperTypes[i++] = superType;
  // }
  //
  // cdoClass.setAllSuperTypes(allSuperTypes);
  // }

  // private static void initAllFeatures(CDOClassImpl cdoClass,
  // CDOPackageManagerImpl packageManager)
  // {
  // EClass eClass = getEClass(cdoClass);
  // List<EStructuralFeature> eFeatures =
  // getPersistentFeatures(eClass.getEAllStructuralFeatures());
  // CDOFeatureImpl[] allFeatures = new CDOFeatureImpl[eFeatures.size()];
  //
  // int i = 0;
  // for (EStructuralFeature eFeature : eFeatures)
  // {
  // CDOFeatureImpl cdoFeature = getCDOFeature(eFeature, packageManager);
  // if (cdoFeature.getFeatureID() != i)
  // {
  // throw new ImplementationError("Wrong featureID: " + cdoFeature);
  // }
  //
  // allFeatures[i++] = cdoFeature;
  // }
  //
  // cdoClass.setAllFeatures(allFeatures);
  // }
}
