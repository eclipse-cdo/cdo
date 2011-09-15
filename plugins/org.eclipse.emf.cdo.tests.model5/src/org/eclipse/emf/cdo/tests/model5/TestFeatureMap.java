/*
 * Copyright (c) 2004 - 2011 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.model5;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.FeatureMap;

/**
 * <!-- begin-user-doc --> A representation of the model object '<em><b>Test Feature Map</b></em>'. <!-- end-user-doc
 * -->
 * <p>
 * The following features are supported:
 * <ul>
 * <li>{@link org.eclipse.emf.cdo.tests.model5.TestFeatureMap#getManagers <em>Managers</em>}</li>
 * <li>{@link org.eclipse.emf.cdo.tests.model5.TestFeatureMap#getDoctors <em>Doctors</em>}</li>
 * <li>{@link org.eclipse.emf.cdo.tests.model5.TestFeatureMap#getPeople <em>People</em>}</li>
 * </ul>
 * </p>
 * 
 * @see org.eclipse.emf.cdo.tests.model5.Model5Package#getTestFeatureMap()
 * @model
 * @generated
 */
public interface TestFeatureMap extends EObject
{
  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  String copyright = "Copyright (c) 2004 - 2011 Eike Stepper (Berlin, Germany) and others.\r\nAll rights reserved. This program and the accompanying materials\r\nare made available under the terms of the Eclipse Public License v1.0\r\nwhich accompanies this distribution, and is available at\r\nhttp://www.eclipse.org/legal/epl-v10.html\r\n\r\nContributors:\r\n   Eike Stepper - initial API and implementation";

  /**
   * Returns the value of the '<em><b>Managers</b></em>' reference list. The list contents are of type
   * {@link org.eclipse.emf.cdo.tests.model5.Manager}. <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Managers</em>' containment reference list isn't clear, there really should be more of a
   * description here...
   * </p>
   * <!-- end-user-doc -->
   * 
   * @return the value of the '<em>Managers</em>' reference list.
   * @see org.eclipse.emf.cdo.tests.model5.Model5Package#getTestFeatureMap_Managers()
   * @model transient="true" extendedMetaData="group='#people'"
   * @generated
   */
  EList<Manager> getManagers();

  /**
   * Returns the value of the '<em><b>Doctors</b></em>' reference list. The list contents are of type
   * {@link org.eclipse.emf.cdo.tests.model5.Doctor}. <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Doctors</em>' containment reference list isn't clear, there really should be more of a
   * description here...
   * </p>
   * <!-- end-user-doc -->
   * 
   * @return the value of the '<em>Doctors</em>' reference list.
   * @see org.eclipse.emf.cdo.tests.model5.Model5Package#getTestFeatureMap_Doctors()
   * @model transient="true" extendedMetaData="group='#people'"
   * @generated
   */
  EList<Doctor> getDoctors();

  /**
   * Returns the value of the '<em><b>People</b></em>' attribute list. The list contents are of type
   * {@link org.eclipse.emf.ecore.util.FeatureMap.Entry}. <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>People</em>' attribute list isn't clear, there really should be more of a description
   * here...
   * </p>
   * <!-- end-user-doc -->
   * 
   * @return the value of the '<em>People</em>' attribute list.
   * @see org.eclipse.emf.cdo.tests.model5.Model5Package#getTestFeatureMap_People()
   * @model unique="false" dataType="org.eclipse.emf.ecore.EFeatureMapEntry" many="true" extendedMetaData="kind='group'"
   * @generated
   */
  FeatureMap getPeople();

} // TestFeatureMap
