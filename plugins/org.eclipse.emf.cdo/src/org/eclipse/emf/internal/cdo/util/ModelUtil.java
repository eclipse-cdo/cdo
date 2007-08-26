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
import org.eclipse.emf.cdo.internal.protocol.model.CDOTypeImpl;
import org.eclipse.emf.cdo.internal.protocol.model.core.CDOCorePackageImpl;
import org.eclipse.emf.cdo.internal.protocol.model.resource.CDOResourceClassImpl;
import org.eclipse.emf.cdo.internal.protocol.model.resource.CDOResourcePackageImpl;
import org.eclipse.emf.cdo.protocol.CDOIDRange;
import org.eclipse.emf.cdo.protocol.model.CDOPackageManager;
import org.eclipse.emf.cdo.util.EMFUtil;

import org.eclipse.net4j.util.ImplementationError;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.impl.EPackageImpl;
import org.eclipse.emf.internal.cdo.CDOFactoryImpl;
import org.eclipse.emf.internal.cdo.CDOPackageRegistryImpl;
import org.eclipse.emf.internal.cdo.CDOSessionPackageManager;

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
      throw new ImplementationError("Should only be called for attributes");
      // return CDOTypeImpl.OBJECT;
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
        CDOTypeImpl type = CDOTypeImpl.getType(classifierID);
        if (type == CDOTypeImpl.OBJECT)
        {
          throw new ImplementationError("Attributes can not be of type OBJECT");
        }

        return type;
      }
    }

    return CDOTypeImpl.STRING;
  }

  public static void initializeCDOPackage(EPackage ePackage, CDOPackageImpl cdoPackage)
  {
    cdoPackage.setClientInfo(ePackage);
    for (EClass eClass : EMFUtil.getPersistentClasses(ePackage))
    {
      CDOClassImpl cdoClass = createCDOClass(eClass, cdoPackage);
      cdoPackage.addClass(cdoClass);
    }
  }

  public static CDOPackageImpl getCDOPackage(EPackage ePackage, CDOSessionPackageManager packageManager)
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

  public static CDOClassImpl getCDOClass(EClass eClass, CDOSessionPackageManager packageManager)
  {
    CDOPackageImpl cdoPackage = getCDOPackage(eClass.getEPackage(), packageManager);
    return cdoPackage.lookupClass(eClass.getClassifierID());
  }

  public static CDOFeatureImpl getCDOFeature(EStructuralFeature eFeature, CDOSessionPackageManager packageManager)
  {
    CDOClassImpl cdoClass = getCDOClass(eFeature.getEContainingClass(), packageManager);
    return cdoClass.lookupFeature(eFeature.getFeatureID());
  }

  private static CDOPackageImpl createCDOPackage(EPackage ePackage, CDOSessionPackageManager packageManager)
  {
    String packageURI = ePackage.getNsURI();
    String name = ePackage.getName();
    boolean dynamic = EMFUtil.isDynamicEPackage(ePackage);
    // String ecore = dynamic ? EMFUtil.ePackageToString(ePackage) : null;
    // TODO Serialize EcorePackage.eINSTANCE, too
    // Current problem with EcorePackage.eINSTANCE is mutual modification of
    // certain Ecore features (see newsgroup thread "eGenericSuperTypes" and
    // EMFUtil.getPersistentFeatures()

    String ecore = EcorePackage.eINSTANCE.getNsURI().equals(packageURI) ? null : EMFUtil.ePackageToString(ePackage);
    // String ecore = EMFUtil.ePackageToString(ePackage);
    CDOIDRange idRange = packageManager.getSession().registerEPackage(ePackage);

    CDOPackageImpl cdoPackage = new CDOPackageImpl(packageManager, packageURI, name, ecore, dynamic, idRange);
    initializeCDOPackage(ePackage, cdoPackage);
    return cdoPackage;
  }

  private static CDOClassImpl createCDOClass(EClass eClass, CDOPackageImpl containingPackage)
  {
    CDOClassImpl cdoClass = new CDOClassImpl(containingPackage, eClass.getClassifierID(), eClass.getName(), eClass
        .isAbstract());
    cdoClass.setClientInfo(eClass);

    for (EClass superType : eClass.getESuperTypes())
    {
      CDOClassRefImpl classRef = createClassRef(superType);
      cdoClass.addSuperType(classRef);
    }

    for (EStructuralFeature eFeature : EMFUtil.getPersistentFeatures(eClass.getEStructuralFeatures()))
    {
      CDOFeatureImpl cdoFeature = createCDOFeature(eFeature, cdoClass);
      cdoClass.addFeature(cdoFeature);
    }

    return cdoClass;
  }

  private static CDOFeatureImpl createCDOFeature(EStructuralFeature eFeature, CDOClassImpl containingClass)
  {
    CDOFeatureImpl cdoFeature = EMFUtil.isReference(eFeature) ? createCDOReference(eFeature, containingClass)
        : createCDOAttribute(eFeature, containingClass);
    cdoFeature.setClientInfo(eFeature);
    return cdoFeature;
  }

  private static CDOFeatureImpl createCDOReference(EStructuralFeature eFeature, CDOClassImpl containingClass)
  {
    CDOPackageManager packageManager = containingClass.getPackageManager();
    int featureID = eFeature.getFeatureID();
    String name = eFeature.getName();
    CDOClassRefImpl classRef = createClassRef(eFeature.getEType());
    boolean many = eFeature.isMany();
    boolean containment = EMFUtil.isContainment(eFeature);
    return new CDOFeatureImpl(containingClass, featureID, name, new CDOClassProxy(classRef, packageManager), many,
        containment);
  }

  private static CDOFeatureImpl createCDOAttribute(EStructuralFeature eFeature, CDOClassImpl containingClass)
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
      String uri = cdoPackage.getPackageURI();
      ePackage = packageRegistry.getEPackage(uri);
      if (ePackage == null)
      {
        ePackage = createEPackage(cdoPackage);
        packageRegistry.put(uri, ePackage);
      }

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

  public static EPackage createEPackage(CDOPackageImpl cdoPackage)
  {
    if (!cdoPackage.isDynamic())
    {
      EPackage ePackage = createGeneratedEPackage(cdoPackage.getPackageURI());
      if (ePackage != null)
      {
        return ePackage;
      }
    }

    return createDynamicEPackage(cdoPackage);
  }

  public static EPackage createGeneratedEPackage(String packageURI)
  {
    if (packageURI.equals(EcorePackage.eINSTANCE.getNsURI()))
    {
      return EcorePackage.eINSTANCE;
    }

    return EPackage.Registry.INSTANCE.getEPackage(packageURI);
  }

  public static EPackageImpl createDynamicEPackage(CDOPackageImpl cdoPackage)
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

  public static void addModelInfos(CDOSessionPackageManager packageManager)
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

  @Deprecated
  public static void removeModelInfos(CDOSessionPackageManager packageManager)
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
}
