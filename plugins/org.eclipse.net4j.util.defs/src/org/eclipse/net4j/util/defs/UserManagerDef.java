/*
 * Copyright (c) 2008, 2009, 2011, 2012 Eike Stepper (Berlin, Germany) and others.
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

/**
 * <!-- begin-user-doc --> A representation of the model object '<em><b>User Manager Def</b></em>'. <!-- end-user-doc
 * -->
 * <p>
 * The following features are supported:
 * <ul>
 * <li>{@link org.eclipse.net4j.util.defs.UserManagerDef#getUser <em>User</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.net4j.util.defs.Net4jUtilDefsPackage#getUserManagerDef()
 * @model
 * @generated
 */
public interface UserManagerDef extends Def
{
  /**
   * Returns the value of the '<em><b>User</b></em>' reference list.
   * The list contents are of type {@link org.eclipse.net4j.util.defs.User}.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>User</em>' reference list isn't clear, there really should be more of a description
   * here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>User</em>' reference list.
   * @see #isSetUser()
   * @see #unsetUser()
   * @see org.eclipse.net4j.util.defs.Net4jUtilDefsPackage#getUserManagerDef_User()
   * @model unsettable="true" required="true"
   * @generated
   */
  EList<User> getUser();

  /**
   * Unsets the value of the '{@link org.eclipse.net4j.util.defs.UserManagerDef#getUser <em>User</em>}' reference list.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @see #isSetUser()
   * @see #getUser()
   * @generated
   */
  void unsetUser();

  /**
   * Returns whether the value of the '{@link org.eclipse.net4j.util.defs.UserManagerDef#getUser <em>User</em>}' reference list is set.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return whether the value of the '<em>User</em>' reference list is set.
   * @see #unsetUser()
   * @see #getUser()
   * @generated
   */
  boolean isSetUser();

} // UserManagerDef
