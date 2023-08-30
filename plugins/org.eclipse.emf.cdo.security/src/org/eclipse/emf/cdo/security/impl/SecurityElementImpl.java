/*
 * Copyright (c) 2012, 2016, 2019, 2023 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.security.impl;

import org.eclipse.emf.cdo.etypes.impl.ModelElementImpl;
import org.eclipse.emf.cdo.security.Realm;
import org.eclipse.emf.cdo.security.SecurityElement;
import org.eclipse.emf.cdo.security.SecurityPackage;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;

import java.lang.reflect.InvocationTargetException;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Element</b></em>'.
 * <!-- end-user-doc -->
 *
 * @generated
 */
public abstract class SecurityElementImpl extends ModelElementImpl implements SecurityElement
{
  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected SecurityElementImpl()
  {
    super();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  protected EClass eStaticClass()
  {
    return SecurityPackage.Literals.SECURITY_ELEMENT;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  @Override
  public Realm getRealm()
  {
    EObject container = eContainer();
    if (container instanceof SecurityElement)
    {
      return ((SecurityElement)container).getRealm();
    }

    return null;
  }

  /**
   * <!-- begin-user-doc -->
  	 * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Object eInvoke(int operationID, EList<?> arguments) throws InvocationTargetException
  {
    switch (operationID)
    {
    case SecurityPackage.SECURITY_ELEMENT___GET_REALM:
      return getRealm();
    }
    return super.eInvoke(operationID, arguments);
  }

} // SecurityElementImpl
