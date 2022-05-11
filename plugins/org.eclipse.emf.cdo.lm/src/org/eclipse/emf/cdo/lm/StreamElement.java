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
package org.eclipse.emf.cdo.lm;

/**
 * <!-- begin-user-doc --> A representation of the model object '<em><b>Stream
 * Element</b></em>'.
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 * <!-- end-user-doc -->
 *
 *
 * @see org.eclipse.emf.cdo.lm.LMPackage#getStreamElement()
 * @model interface="true" abstract="true"
 * @generated
 */
public interface StreamElement extends ModuleElement
{
  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @model kind="operation" required="true"
   * @generated
   */
  Stream getStream();

} // StreamElement
