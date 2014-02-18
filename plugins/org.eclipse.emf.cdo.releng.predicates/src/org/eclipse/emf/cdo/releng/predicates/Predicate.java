/*
 * Copyright (c) 2013, 2014 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.releng.predicates;

import org.eclipse.emf.ecore.EObject;

import org.eclipse.core.resources.IProject;

import java.io.File;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Predicate</b></em>'.
 * <!-- end-user-doc -->
 *
 *
 * @see org.eclipse.emf.cdo.releng.predicates.PredicatesPackage#getPredicate()
 * @model abstract="true"
 * @generated
 */
public interface Predicate extends EObject
{
  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @model projectDataType="org.eclipse.emf.cdo.releng.predicates.Project"
   * @generated
   */
  boolean matches(IProject project);

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @model projectFolderDataType="org.eclipse.emf.cdo.releng.predicates.File"
   * @generated
   */
  boolean matches(File projectFolder);

} // Predicate
