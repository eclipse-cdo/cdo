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
package org.eclipse.emf.internal.cdo.util;

import org.eclipse.emf.cdo.eresource.EresourcePackage;
import org.eclipse.emf.cdo.internal.protocol.model.CDOClassImpl;
import org.eclipse.emf.cdo.internal.protocol.model.CDOClassProxy;
import org.eclipse.emf.cdo.internal.protocol.model.CDOPackageImpl;
import org.eclipse.emf.cdo.protocol.id.CDOIDMetaRange;
import org.eclipse.emf.cdo.protocol.model.CDOClass;
import org.eclipse.emf.cdo.protocol.model.CDOClassRef;
import org.eclipse.emf.cdo.protocol.model.CDOFeature;
import org.eclipse.emf.cdo.protocol.model.CDOModelUtil;
import org.eclipse.emf.cdo.protocol.model.CDOPackage;
import org.eclipse.emf.cdo.protocol.model.CDOPackageManager;
import org.eclipse.emf.cdo.protocol.model.CDOType;
import org.eclipse.emf.cdo.protocol.model.core.CDOCorePackage;
import org.eclipse.emf.cdo.protocol.model.resource.CDOResourceClass;
import org.eclipse.emf.cdo.protocol.model.resource.CDOResourcePackage;
import org.eclipse.emf.cdo.util.EMFUtil;

import org.eclipse.emf.internal.cdo.CDOFactoryImpl;
import org.eclipse.emf.internal.cdo.CDOPackageRegistryImpl;
import org.eclipse.emf.internal.cdo.CDOSessionPackageManagerImpl;
import org.eclipse.emf.internal.cdo.bundle.OM;

import org.eclipse.net4j.internal.util.om.trace.ContextTracer;
import org.eclipse.net4j.util.ImplementationError;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.impl.EPackageImpl;

/**
 * @author Eike Stepper
 */
public final class ModelUtil
{
  private static final ContextTracer MODEL = new ContextTracer(OM.DEBUG_MODEL, ModelUtil.class);

  private ModelUtil()
  {
  }

  public static CDOType getCDOType(EStructuralFeature eFeature)
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
        CDOType type = CDOModelUtil.getType(classifierID);
        if (type == CDOType.OBJECT)
        {
          throw new ImplementationError("Attributes can not be of type OBJECT");
        }

        return type;
      }
    }

    return CDOType.STRING;
  }

  public static void initializeCDOPackage(EPackage ePackage, CDOPackage cdoPackage)
  {
    cdoPackage.setClientInfo(ePackage);
    for (EClass eClass : EMFUtil.getPersistentClasses(ePackage))
    {
      CDOClass cdoClass = createCDOClass(eClass, cdoPackage);
      ((CDOPackageImpl)cdoPackage).addClass(cdoClass);
    }
  }

  public static CDOPackage getCDOPackage(EPackage ePackage, CDOSessionPackageManagerImpl packageManager)
  {
    String packageURI = ePackage.getNsURI();
    CDOPackage cdoPackage = packageManager.lookupPackage(packageURI);
    if (cdoPackage == null)
    {
      cdoPackage = createCDOPackage(ePackage, packageManager);
      packageManager.addPackage(cdoPackage);
    }

    return cdoPackage;
  }

  public static CDOClass getCDOClass(EClass eClass, CDOSessionPackageManagerImpl packageManager)
  {
    CDOPackage cdoPackage = getCDOPackage(eClass.getEPackage(), packageManager);
    return cdoPackage.lookupClass(eClass.getClassifierID());
  }

  public static CDOFeature getCDOFeature(EStructuralFeature eFeature, CDOSessionPackageManagerImpl packageManager)
  {
    CDOClass cdoClass = getCDOClass(eFeature.getEContainingClass(), packageManager);
    return cdoClass.lookupFeature(eFeature.getFeatureID());
  }

  /**
   * @see EMFUtil#getPersistentFeatures(org.eclipse.emf.common.util.EList)
   * @see http://www.eclipse.org/newsportal/article.php?id=26780&group=eclipse.tools.emf#26780
   */
  private static CDOPackage createCDOPackage(EPackage ePackage, CDOSessionPackageManagerImpl packageManager)
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
    CDOIDMetaRange idRange = packageManager.getSession().registerEPackage(ePackage);

    CDOPackage cdoPackage = CDOModelUtil.createPackage(packageManager, packageURI, name, ecore, dynamic, idRange);
    initializeCDOPackage(ePackage, cdoPackage);
    return cdoPackage;
  }

  private static CDOClass createCDOClass(EClass eClass, CDOPackage containingPackage)
  {
    CDOClass cdoClass = CDOModelUtil.createClass(containingPackage, eClass.getClassifierID(), eClass.getName(), eClass
        .isAbstract());
    cdoClass.setClientInfo(eClass);

    for (EClass superType : eClass.getESuperTypes())
    {
      CDOClassRef classRef = createClassRef(superType);
      ((CDOClassImpl)cdoClass).addSuperType(classRef);
    }

    for (EStructuralFeature eFeature : EMFUtil.getPersistentFeatures(eClass.getEStructuralFeatures()))
    {
      CDOFeature cdoFeature = createCDOFeature(eFeature, cdoClass);
      ((CDOClassImpl)cdoClass).addFeature(cdoFeature);
    }

    return cdoClass;
  }

  private static CDOFeature createCDOFeature(EStructuralFeature eFeature, CDOClass containingClass)
  {
    CDOFeature cdoFeature = EMFUtil.isReference(eFeature) ? createCDOReference((EReference)eFeature, containingClass)
        : createCDOAttribute((EAttribute)eFeature, containingClass);
    cdoFeature.setClientInfo(eFeature);
    return cdoFeature;
  }

  private static CDOFeature createCDOReference(EReference eFeature, CDOClass containingClass)
  {
    CDOPackageManager packageManager = containingClass.getPackageManager();
    int featureID = eFeature.getFeatureID();
    String name = eFeature.getName();
    CDOClassRef classRef = createClassRef(eFeature.getEType());
    boolean many = eFeature.isMany();
    boolean containment = EMFUtil.isContainment(eFeature);
    CDOFeature cdoFeature = CDOModelUtil.createReference(containingClass, featureID, name, new CDOClassProxy(classRef,
        packageManager), many, containment);

    EReference opposite = eFeature.getEOpposite();
    if (MODEL.isEnabled() && opposite != null)
    {
      MODEL.format("Opposite info: package={0}, class={1}, feature={2}", opposite.getEContainingClass().getEPackage()
          .getNsURI(), opposite.getEContainingClass().getName(), opposite.getName());
    }

    return cdoFeature;
  }

  private static CDOFeature createCDOAttribute(EAttribute eFeature, CDOClass containingClass)
  {
    int featureID = eFeature.getFeatureID();
    String name = eFeature.getName();
    CDOType type = getCDOType(eFeature);
    boolean many = EMFUtil.isMany(eFeature);
    return CDOModelUtil.createAttribute(containingClass, featureID, name, type, many);
  }

  public static EPackage getEPackage(CDOPackage cdoPackage, CDOPackageRegistryImpl packageRegistry)
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

  public static EClass getEClass(CDOClass cdoClass, CDOPackageRegistryImpl packageRegistry)
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

  public static EStructuralFeature getEFeature(CDOFeature cdoFeature, CDOPackageRegistryImpl packageRegistry)
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

  public static EPackage createEPackage(CDOPackage cdoPackage)
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

  public static EPackageImpl createDynamicEPackage(CDOPackage cdoPackage)
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

  public static CDOClassRef createClassRef(EClassifier classifier)
  {
    if (classifier instanceof EClass)
    {
      String packageURI = classifier.getEPackage().getNsURI();
      int classifierID = classifier.getClassifierID();
      return CDOModelUtil.createClassRef(packageURI, classifierID);
    }

    return null;
  }

  public static void addModelInfos(CDOSessionPackageManagerImpl packageManager)
  {
    // Ecore
    CDOCorePackage corePackage = packageManager.getCDOCorePackage();
    corePackage.setClientInfo(EcorePackage.eINSTANCE);
    corePackage.getCDOObjectClass().setClientInfo(EcorePackage.eINSTANCE.getEObject());

    // Eresource
    CDOResourcePackage resourcePackage = packageManager.getCDOResourcePackage();
    resourcePackage.setClientInfo(EresourcePackage.eINSTANCE);
    CDOResourceClass resourceClass = resourcePackage.getCDOResourceClass();
    resourceClass.setClientInfo(EresourcePackage.eINSTANCE.getCDOResource());
    resourceClass.getCDOContentsFeature().setClientInfo(EresourcePackage.eINSTANCE.getCDOResource_Contents());
    resourceClass.getCDOPathFeature().setClientInfo(EresourcePackage.eINSTANCE.getCDOResource_Path());
  }

  @Deprecated
  public static void removeModelInfos(CDOSessionPackageManagerImpl packageManager)
  {
    // Ecore
    CDOCorePackage corePackage = packageManager.getCDOCorePackage();
    corePackage.setClientInfo(null);
    corePackage.getCDOObjectClass().setClientInfo(null);

    // Eresource
    CDOResourcePackage resourcePackage = packageManager.getCDOResourcePackage();
    resourcePackage.setClientInfo(null);
    CDOResourceClass resourceClass = resourcePackage.getCDOResourceClass();
    resourceClass.setClientInfo(null);
    resourceClass.getCDOContentsFeature().setClientInfo(null);
    resourceClass.getCDOPathFeature().setClientInfo(null);
  }
}
