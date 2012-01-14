/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Andre Dietisheim - initial API and implementation
 *    Eike Stepper - maintenance
 */
package org.eclipse.net4j.util.defs;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc --> A representation of the model object '<em><b>Defs Container</b></em>'. <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.eclipse.net4j.util.defs.DefContainer#getDefinitions <em>Definitions</em>}</li>
 *   <li>{@link org.eclipse.net4j.util.defs.DefContainer#getDefaultDefinition <em>Default Definition</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.net4j.util.defs.Net4jUtilDefsPackage#getDefContainer()
 * @model
 * @generated
 */
public interface DefContainer extends EObject
{
  /**
   * Returns the value of the '<em><b>Definitions</b></em>' containment reference list.
   * The list contents are of type {@link org.eclipse.net4j.util.defs.Def}.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Definitions</em>' containment reference list isn't clear, there really should be more of
   * a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Definitions</em>' containment reference list.
   * @see org.eclipse.net4j.util.defs.Net4jUtilDefsPackage#getDefContainer_Definitions()
   * @model containment="true" required="true"
   * @generated
   */
  EList<Def> getDefinitions();

  /**
   * Returns the value of the '<em><b>Default Definition</b></em>' reference.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Default Definition</em>' reference isn't clear, there really should be more of a
   * description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Default Definition</em>' reference.
   * @see #setDefaultDefinition(Def)
   * @see org.eclipse.net4j.util.defs.Net4jUtilDefsPackage#getDefContainer_DefaultDefinition()
   * @model
   * @generated
   */
  Def getDefaultDefinition();

  /**
   * Sets the value of the '{@link org.eclipse.net4j.util.defs.DefContainer#getDefaultDefinition <em>Default Definition</em>}' reference.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @param value the new value of the '<em>Default Definition</em>' reference.
   * @see #getDefaultDefinition()
   * @generated
   */
  void setDefaultDefinition(Def value);

} // DefsContainer
