/**
 * Copyright (c) 2004 - 2011 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Martin Fluegge - initial API and implementation
 */
package org.eclipse.emf.cdo.dawn.codegen.dawngenmodel.gmf.dawnGmfGenmodel.impl;

import org.eclipse.emf.cdo.dawn.codegen.dawngenmodel.gmf.dawnGmfGenmodel.DawnGMFGenerator;
import org.eclipse.emf.cdo.dawn.codegen.dawngenmodel.gmf.dawnGmfGenmodel.DawnGmfGenmodelFactory;
import org.eclipse.emf.cdo.dawn.codegen.dawngenmodel.gmf.dawnGmfGenmodel.DawnGmfGenmodelPackage;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.impl.EFactoryImpl;
import org.eclipse.emf.ecore.plugin.EcorePlugin;

/**
 * <!-- begin-user-doc --> An implementation of the model <b>Factory</b>. <!-- end-user-doc -->
 * 
 * @generated
 * @author Martin Fluegge
 */
public class DawnGmfGenmodelFactoryImpl extends EFactoryImpl implements DawnGmfGenmodelFactory
{
  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public static final String copyright = "Copyright (c) 2004 - 2011 Eike Stepper (Berlin, Germany) and others.\r\nAll rights reserved. This program and the accompanying materials\r\nare made available under the terms of the Eclipse Public License v1.0\r\nwhich accompanies this distribution, and is available at\r\nhttp://www.eclipse.org/legal/epl-v10.html\r\n\r\nContributors:\r\n   Martin Fluegge - initial API and implementation";

  /**
   * Creates the default factory implementation. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public static DawnGmfGenmodelFactory init()
  {
    try
    {
      DawnGmfGenmodelFactory theDawnGmfGenmodelFactory = (DawnGmfGenmodelFactory)EPackage.Registry.INSTANCE
          .getEFactory("http://www.eclipse.org/emf/cdo/dawn/2010/GenModel/gmf");
      if (theDawnGmfGenmodelFactory != null)
      {
        return theDawnGmfGenmodelFactory;
      }
    }
    catch (Exception exception)
    {
      EcorePlugin.INSTANCE.log(exception);
    }
    return new DawnGmfGenmodelFactoryImpl();
  }

  /**
   * Creates an instance of the factory. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public DawnGmfGenmodelFactoryImpl()
  {
    super();
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  @Override
  public EObject create(EClass eClass)
  {
    switch (eClass.getClassifierID())
    {
    case DawnGmfGenmodelPackage.DAWN_GMF_GENERATOR:
      return createDawnGMFGenerator();
    default:
      throw new IllegalArgumentException("The class '" + eClass.getName() + "' is not a valid classifier");
    }
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public DawnGMFGenerator createDawnGMFGenerator()
  {
    DawnGMFGeneratorImpl dawnGMFGenerator = new DawnGMFGeneratorImpl();
    return dawnGMFGenerator;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public DawnGmfGenmodelPackage getDawnGmfGenmodelPackage()
  {
    return (DawnGmfGenmodelPackage)getEPackage();
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @deprecated
   * @generated
   */
  @Deprecated
  public static DawnGmfGenmodelPackage getPackage()
  {
    return DawnGmfGenmodelPackage.eINSTANCE;
  }

} // DawnGmfGenmodelFactoryImpl
