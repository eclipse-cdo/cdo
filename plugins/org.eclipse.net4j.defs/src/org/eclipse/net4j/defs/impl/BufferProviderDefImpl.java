/**
 *  * Copyright (c) 2004 - 2009 Eike Stepper (Berlin, Germany) and others.
 *  * All rights reserved. This program and the accompanying materials
 *  * are made available under the terms of the Eclipse Public License v1.0
 *  * which accompanies this distribution, and is available at
 *  * http://www.eclipse.org/legal/epl-v10.html
 *  * 
 *  * Contributors:
 *  *    Andre Dietisheim - initial API and implementation
 *
 * $Id: BufferProviderDefImpl.java,v 1.2 2009-01-10 14:57:25 estepper Exp $
 */
package org.eclipse.net4j.defs.impl;

import org.eclipse.net4j.defs.BufferProviderDef;
import org.eclipse.net4j.defs.Net4jDefsPackage;
import org.eclipse.net4j.util.defs.impl.DefImpl;

import org.eclipse.emf.ecore.EClass;

/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>Buffer Provider Def</b></em>'. <!--
 * end-user-doc -->
 * <p>
 * </p>
 * 
 * @generated
 */
public abstract class BufferProviderDefImpl extends DefImpl implements BufferProviderDef
{
  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  protected BufferProviderDefImpl()
  {
    super();
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  @Override
  protected EClass eStaticClass()
  {
    return Net4jDefsPackage.Literals.BUFFER_PROVIDER_DEF;
  }

} // BufferProviderDefImpl
