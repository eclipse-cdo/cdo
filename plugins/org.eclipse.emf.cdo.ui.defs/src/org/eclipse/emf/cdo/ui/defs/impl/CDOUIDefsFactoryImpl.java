/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Andre Dietisheim - initial API and implementation
 *    Eike Stepper - maintenance
 */
package org.eclipse.emf.cdo.ui.defs.impl;

//import org.eclipse.emf.cdo.ui.defs.*;
import org.eclipse.emf.cdo.ui.defs.CDOEditorDef;
import org.eclipse.emf.cdo.ui.defs.CDOUIDefsFactory;
import org.eclipse.emf.cdo.ui.defs.CDOUIDefsPackage;
import org.eclipse.emf.cdo.ui.defs.EditorDef;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.impl.EFactoryImpl;
import org.eclipse.emf.ecore.plugin.EcorePlugin;

/**
 * <!-- begin-user-doc --> An implementation of the model <b>Factory</b>. <!-- end-user-doc -->
 * @generated
 */
public class CDOUIDefsFactoryImpl extends EFactoryImpl implements CDOUIDefsFactory
{
  /**
   * Creates the default factory implementation.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  public static CDOUIDefsFactory init()
  {
    try
    {
      CDOUIDefsFactory theCDOUIDefsFactory = (CDOUIDefsFactory)EPackage.Registry.INSTANCE
          .getEFactory("http://www.eclipse.org/emf/CDO/ui/defs/1.0.0");
      if (theCDOUIDefsFactory != null)
      {
        return theCDOUIDefsFactory;
      }
    }
    catch (Exception exception)
    {
      EcorePlugin.INSTANCE.log(exception);
    }
    return new CDOUIDefsFactoryImpl();
  }

  /**
   * Creates an instance of the factory.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  public CDOUIDefsFactoryImpl()
  {
    super();
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EObject create(EClass eClass)
  {
    switch (eClass.getClassifierID())
    {
    case CDOUIDefsPackage.EDITOR_DEF:
      return createEditorDef();
    case CDOUIDefsPackage.CDO_EDITOR_DEF:
      return createCDOEditorDef();
    default:
      throw new IllegalArgumentException("The class '" + eClass.getName() + "' is not a valid classifier");
    }
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  public EditorDef createEditorDef()
  {
    EditorDefImpl editorDef = new EditorDefImpl();
    return editorDef;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  public CDOEditorDef createCDOEditorDef()
  {
    CDOEditorDefImpl cdoEditorDef = new CDOEditorDefImpl();
    return cdoEditorDef;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  public CDOUIDefsPackage getCDOUIDefsPackage()
  {
    return (CDOUIDefsPackage)getEPackage();
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @deprecated
   * @generated
   */
  @Deprecated
  public static CDOUIDefsPackage getPackage()
  {
    return CDOUIDefsPackage.eINSTANCE;
  }

} // CDOUIDefsFactoryImpl
