/*
 * Copyright (c) 2010-2012, 2014, 2016 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.etypes;

import org.eclipse.emf.cdo.CDOObject;

import org.eclipse.emf.common.util.EList;

/**
 * <!-- begin-user-doc --> A representation of the model object '<em><b>Model Element</b></em>'.
 *
 * @since 4.0 <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.etypes.ModelElement#getAnnotations <em>Annotations</em>}</li>
 * </ul>
 *
 * @see org.eclipse.emf.cdo.etypes.EtypesPackage#getModelElement()
 * @model abstract="true"
 * @extends CDOObject
 * @generated
 */
public interface ModelElement extends CDOObject
{
  /**
   * Returns the value of the '<em><b>Annotations</b></em>' containment reference list.
   * The list contents are of type {@link org.eclipse.emf.cdo.etypes.Annotation}.
   * It is bidirectional and its opposite is '{@link org.eclipse.emf.cdo.etypes.Annotation#getModelElement <em>Model Element</em>}'.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Annotations</em>' containment reference list isn't clear, there really should be more of
   * a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Annotations</em>' containment reference list.
   * @see org.eclipse.emf.cdo.etypes.EtypesPackage#getModelElement_Annotations()
   * @see org.eclipse.emf.cdo.etypes.Annotation#getModelElement
   * @model opposite="modelElement" containment="true"
   * @generated
   */
  EList<Annotation> getAnnotations();

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @model
   * @generated
   */
  Annotation getAnnotation(String source);

} // ModelElement
