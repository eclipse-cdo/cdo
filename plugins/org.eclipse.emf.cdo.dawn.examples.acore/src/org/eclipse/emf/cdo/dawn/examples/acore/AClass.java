/*
 * Copyright (c) 2004 - 2011 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *  
 * Contributors:
 *     Martin Fluegge - initial API and implementation
 * 
 */
package org.eclipse.emf.cdo.dawn.examples.acore;

import org.eclipse.emf.common.util.EList;

/**
 * <!-- begin-user-doc --> A representation of the model object '<em><b>AClass</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are supported:
 * <ul>
 * <li>{@link org.eclipse.emf.cdo.dawn.examples.acore.AClass#getSubClasses <em>Sub Classes</em>}</li>
 * <li>{@link org.eclipse.emf.cdo.dawn.examples.acore.AClass#getImplementedInterfaces <em>Implemented Interfaces</em>}</li>
 * <li>{@link org.eclipse.emf.cdo.dawn.examples.acore.AClass#getAssociations <em>Associations</em>}</li>
 * <li>{@link org.eclipse.emf.cdo.dawn.examples.acore.AClass#getCompositions <em>Compositions</em>}</li>
 * <li>{@link org.eclipse.emf.cdo.dawn.examples.acore.AClass#getAggregations <em>Aggregations</em>}</li>
 * </ul>
 * </p>
 * 
 * @see org.eclipse.emf.cdo.dawn.examples.acore.AcorePackage#getAClass()
 * @model
 * @generated
 */
public interface AClass extends ABasicClass
{
  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  String copyright = "Copyright (c) 2004 - 2011 Eike Stepper (Berlin, Germany) and others.\r\nAll rights reserved. This program and the accompanying materials\r\nare made available under the terms of the Eclipse Public License v1.0\r\nwhich accompanies this distribution, and is available at\r\nhttp://www.eclipse.org/legal/epl-v10.html\r\n \r\nContributors:\r\n    Martin Fluegge - initial API and implementation\r\n";

  /**
   * Returns the value of the '<em><b>Sub Classes</b></em>' reference list. The list contents are of type
   * {@link org.eclipse.emf.cdo.dawn.examples.acore.AClass}. <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Sub Classes</em>' reference list isn't clear, there really should be more of a
   * description here...
   * </p>
   * <!-- end-user-doc -->
   * 
   * @return the value of the '<em>Sub Classes</em>' reference list.
   * @see org.eclipse.emf.cdo.dawn.examples.acore.AcorePackage#getAClass_SubClasses()
   * @model
   * @generated
   */
  EList<AClass> getSubClasses();

  /**
   * Returns the value of the '<em><b>Implemented Interfaces</b></em>' reference list. The list contents are of type
   * {@link org.eclipse.emf.cdo.dawn.examples.acore.AInterface}. <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Implemented Interfaces</em>' reference list isn't clear, there really should be more of
   * a description here...
   * </p>
   * <!-- end-user-doc -->
   * 
   * @return the value of the '<em>Implemented Interfaces</em>' reference list.
   * @see org.eclipse.emf.cdo.dawn.examples.acore.AcorePackage#getAClass_ImplementedInterfaces()
   * @model
   * @generated
   */
  EList<AInterface> getImplementedInterfaces();

  /**
   * Returns the value of the '<em><b>Associations</b></em>' reference list. The list contents are of type
   * {@link org.eclipse.emf.cdo.dawn.examples.acore.AClass}. <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Associations</em>' reference list isn't clear, there really should be more of a
   * description here...
   * </p>
   * <!-- end-user-doc -->
   * 
   * @return the value of the '<em>Associations</em>' reference list.
   * @see org.eclipse.emf.cdo.dawn.examples.acore.AcorePackage#getAClass_Associations()
   * @model
   * @generated
   */
  EList<AClass> getAssociations();

  /**
   * Returns the value of the '<em><b>Compositions</b></em>' reference list. The list contents are of type
   * {@link org.eclipse.emf.cdo.dawn.examples.acore.AClass}. <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Compositions</em>' reference list isn't clear, there really should be more of a
   * description here...
   * </p>
   * <!-- end-user-doc -->
   * 
   * @return the value of the '<em>Compositions</em>' reference list.
   * @see org.eclipse.emf.cdo.dawn.examples.acore.AcorePackage#getAClass_Compositions()
   * @model
   * @generated
   */
  EList<AClass> getCompositions();

  /**
   * Returns the value of the '<em><b>Aggregations</b></em>' reference list. The list contents are of type
   * {@link org.eclipse.emf.cdo.dawn.examples.acore.AClass}. <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Aggregations</em>' reference list isn't clear, there really should be more of a
   * description here...
   * </p>
   * <!-- end-user-doc -->
   * 
   * @return the value of the '<em>Aggregations</em>' reference list.
   * @see org.eclipse.emf.cdo.dawn.examples.acore.AcorePackage#getAClass_Aggregations()
   * @model
   * @generated
   */
  EList<AClass> getAggregations();

} // AClass
