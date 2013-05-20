/*
 * Copyright (c) 2010-2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
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
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.tests.model3.PolygonWithDuplicates#getPoints <em>Points</em>}</li>
 * </ul>
 * </p>
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
