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
package org.eclipse.emf.cdo.defs.impl;

import org.eclipse.emf.cdo.defs.CDODefsPackage;
import org.eclipse.emf.cdo.defs.CDOTransactionDef;
import org.eclipse.emf.cdo.session.CDOSession;

import org.eclipse.emf.ecore.EClass;

/**
 * <!-- begin-user-doc --> An implementation of the model object ' <em><b>CDO Transaction Def</b></em>'. <!--
 * end-user-doc -->
 * <p>
 * </p>
 *
 * @generated
 */
public class CDOTransactionDefImpl extends CDOViewDefImpl implements CDOTransactionDef
{

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  protected CDOTransactionDefImpl()
  {
    super();
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  protected EClass eStaticClass()
  {
    return CDODefsPackage.Literals.CDO_TRANSACTION_DEF;
  }

  @Override
  protected Object createInstance()
  {
    CDOSession cdoSession = (CDOSession)getCdoSessionDef().getInstance();
    return cdoSession.openTransaction();
  }

} // CDOTransactionDefImpl
