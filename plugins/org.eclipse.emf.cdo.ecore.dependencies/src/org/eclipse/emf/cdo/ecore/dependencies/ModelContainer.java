/*
 * Copyright (c) 2022 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.ecore.dependencies;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Model Container</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.ecore.dependencies.ModelContainer#getModels <em>Models</em>}</li>
 * </ul>
 *
 * @see org.eclipse.emf.cdo.ecore.dependencies.DependenciesPackage#getModelContainer()
 * @model
 * @generated
 */
public interface ModelContainer extends EObject
{
  /**
   * Returns the value of the '<em><b>Models</b></em>' containment reference list.
   * The list contents are of type {@link org.eclipse.emf.cdo.ecore.dependencies.Model}.
   * It is bidirectional and its opposite is '{@link org.eclipse.emf.cdo.ecore.dependencies.Model#getContainer <em>Container</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the value of the '<em>Models</em>' containment reference list.
   * @see org.eclipse.emf.cdo.ecore.dependencies.DependenciesPackage#getModelContainer_Models()
   * @see org.eclipse.emf.cdo.ecore.dependencies.Model#getContainer
   * @model opposite="container" containment="true"
   * @generated
   */
  EList<Model> getModels();

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @model uriDataType="org.eclipse.emf.cdo.ecore.dependencies.URI" uriRequired="true"
   * @generated
   */
  Model getModel(URI uri);

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @model uriDataType="org.eclipse.emf.cdo.ecore.dependencies.URI" uriRequired="true"
   * @generated
   */
  Element getElement(URI uri);

} // ModelContainer
