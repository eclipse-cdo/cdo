/*
 * Copyright (c) 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Bz380987 Group</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.Bz380987_Group#getPeople <em>People</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.HibernateTestPackage#getBz380987_Group()
 * @model
 * @generated
 */
public interface Bz380987_Group extends EObject {
	/**
	 * Returns the value of the '<em><b>People</b></em>' reference list.
	 * The list contents are of type {@link org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.Bz380987_Person}.
	 * It is bidirectional and its opposite is '{@link org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.Bz380987_Person#getGroup <em>Group</em>}'.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>People</em>' reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>People</em>' reference list.
	 * @see org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.HibernateTestPackage#getBz380987_Group_People()
	 * @see org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.Bz380987_Person#getGroup
	 * @model opposite="group"
	 * @generated
	 */
	EList<Bz380987_Person> getPeople();

} // Bz380987_Group
