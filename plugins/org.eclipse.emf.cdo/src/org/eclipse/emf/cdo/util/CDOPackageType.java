/***************************************************************************
 * Copyright (c) 2004 - 2008 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.emf.cdo.util;

import org.eclipse.emf.internal.cdo.CDOAdapterImpl;
import org.eclipse.emf.internal.cdo.CDOObjectImpl;
import org.eclipse.emf.internal.cdo.CDOStateMachine;

/**
 * @author Eike Stepper
 */
public enum CDOPackageType
{
  /**
   * The type of <code>EPackages</code> that have been dedicatedly generated for CDO. Instances of <code>EClasses</code>
   * of these packages are represented by {@link CDOObjectImpl} to the {@link CDOStateMachine}.
   */
  NATIVE,

  /**
   * The type of <code>EPackages</code> that have been normally generated for EMF. Instances of <code>EClasses</code> of
   * these packages are represented by {@link CDOAdapterImpl} to the {@link CDOStateMachine}.
   */
  LEGACY,

  /**
   * The type of <code>EPackages</code> that have been normally generated for EMF and later converted to CDO. Instances
   * of <code>EClasses</code> of these packages are represented by {@link org.eclipse.emf.internal.cdo.CDOCallbackImpl}
   * to the {@link CDOStateMachine}.
   */
  CONVERTED
}
