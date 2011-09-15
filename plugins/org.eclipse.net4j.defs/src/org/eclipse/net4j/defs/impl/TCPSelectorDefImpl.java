/*
 * Copyright (c) 2004 - 2011 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Andre Dietisheim - initial API and implementation
 *    Eike Stepper - maintenance
 */
package org.eclipse.net4j.defs.impl;

import org.eclipse.net4j.defs.Net4jDefsPackage;
import org.eclipse.net4j.defs.TCPSelectorDef;
import org.eclipse.net4j.internal.tcp.TCPSelector;
import org.eclipse.net4j.util.defs.impl.DefImpl;

import org.eclipse.emf.ecore.EClass;

public class TCPSelectorDefImpl extends DefImpl implements TCPSelectorDef
{
  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  protected TCPSelectorDefImpl()
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
    return Net4jDefsPackage.Literals.TCP_SELECTOR_DEF;
  }

  /**
   * Creates and returns a {@link TCPSelector}.
   * 
   * @return a new tcp selector instance
   * @generated NOT
   */
  @Override
  public Object createInstance()
  {
    TCPSelector selector = new TCPSelector();
    return selector;
  }
} // TCPSelectorDefImpl
