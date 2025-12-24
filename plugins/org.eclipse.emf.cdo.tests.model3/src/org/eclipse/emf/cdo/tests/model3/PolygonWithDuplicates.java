/*
 * Copyright (c) 2010-2012, 2015 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.model3;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc --> A representation of the model object '<em><b>Polygon With Duplicates</b></em>'. <!--
 * end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.tests.model3.PolygonWithDuplicates#getPoints <em>Points</em>}</li>
 * </ul>
 *
 * @see org.eclipse.emf.cdo.tests.model3.Model3Package#getPolygonWithDuplicates()
 * @model
 * @generated
 */
public interface PolygonWithDuplicates extends EObject
{
  /**
   * Returns the value of the '<em><b>Points</b></em>' attribute list.
   * The list contents are of type {@link org.eclipse.emf.cdo.tests.model3.Point}.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Points</em>' attribute list isn't clear, there really should be more of a description
   * here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Points</em>' attribute list.
   * @see org.eclipse.emf.cdo.tests.model3.Model3Package#getPolygonWithDuplicates_Points()
   * @model unique="false" dataType="org.eclipse.emf.cdo.tests.model3.Point" required="true"
   * @generated
   */
  EList<Point> getPoints();

} // PolygonWithDuplicates
