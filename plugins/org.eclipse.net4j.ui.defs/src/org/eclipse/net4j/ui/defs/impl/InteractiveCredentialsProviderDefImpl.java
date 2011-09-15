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
package org.eclipse.net4j.ui.defs.impl;

import org.eclipse.net4j.ui.defs.InteractiveCredentialsProviderDef;
import org.eclipse.net4j.ui.defs.Net4JUIDefsPackage;
import org.eclipse.net4j.util.defs.impl.DefImpl;
import org.eclipse.net4j.util.ui.security.InteractiveCredentialsProvider;

import org.eclipse.emf.ecore.EClass;

/**
 * <!-- begin-user-doc --> An implementation of the model object ' <em><b>Interactive Credentials Provider Def</b></em>
 * '. <!-- end-user-doc -->
 * <p>
 * </p>
 * 
 * @generated
 */
public class InteractiveCredentialsProviderDefImpl extends DefImpl implements InteractiveCredentialsProviderDef
{
  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  protected InteractiveCredentialsProviderDefImpl()
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
    return Net4JUIDefsPackage.Literals.INTERACTIVE_CREDENTIALS_PROVIDER_DEF;
  }

  @Override
  protected Object createInstance()
  {
    return new InteractiveCredentialsProvider();
  }
} // InteractiveCredentialsProviderDefImpl
