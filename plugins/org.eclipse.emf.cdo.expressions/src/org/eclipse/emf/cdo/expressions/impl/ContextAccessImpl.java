/*
 * Copyright (c) 2013, 2016 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.expressions.impl;

import org.eclipse.emf.cdo.expressions.ContextAccess;
import org.eclipse.emf.cdo.expressions.EvaluationContext;
import org.eclipse.emf.cdo.expressions.ExpressionsPackage;

import org.eclipse.emf.ecore.EClass;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Context Value</b></em>'.
 * <!-- end-user-doc -->
 *
 * @generated
 */
public class ContextAccessImpl extends AccessImpl implements ContextAccess
{
  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected ContextAccessImpl()
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
    return ExpressionsPackage.Literals.CONTEXT_ACCESS;
  }

  @Override
  protected Object evaluate(EvaluationContext context, String name)
  {
    return context.get(name);
  }

} // ContextValueImpl
