/*
 * Copyright (c) 2023 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.lm.reviews.impl;

import org.eclipse.emf.cdo.lm.reviews.Heading;
import org.eclipse.emf.cdo.lm.reviews.ReviewsPackage;

import org.eclipse.emf.ecore.EClass;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Heading</b></em>'.
 * @noextend This class is not intended to be subclassed by clients.
 * <!-- end-user-doc -->
 *
 * @generated
 */
public class HeadingImpl extends CommentImpl implements Heading
{
  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected HeadingImpl()
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
    return ReviewsPackage.Literals.HEADING;
  }

} // HeadingImpl
