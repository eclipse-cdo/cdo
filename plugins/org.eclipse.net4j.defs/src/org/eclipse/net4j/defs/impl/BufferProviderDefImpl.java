/**
 *  * Copyright (c) 2004 - 2008 André Dietisheim, Switzerland.
 *  * All rights reserved. This program and the accompanying materials
 *  * are made available under the terms of the Eclipse Public License v1.0
 *  * which accompanies this distribution, and is available at
 *  * http://www.eclipse.org/legal/epl-v10.html
 *  * 
 *  * Contributors:
 *  *    André Dietisheim - initial API and implementation
 *
 * $Id: BufferProviderDefImpl.java,v 1.1 2008-12-31 14:43:20 estepper Exp $
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
