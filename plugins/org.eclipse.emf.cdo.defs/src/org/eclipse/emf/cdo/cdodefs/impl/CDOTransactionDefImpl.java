/**
 * <copyright>
 * </copyright>
 *
 * $Id: CDOTransactionDefImpl.java,v 1.1 2008-12-28 18:05:24 estepper Exp $
 */
package org.eclipse.emf.cdo.cdodefs.impl;

import org.eclipse.emf.cdo.CDOSession;
import org.eclipse.emf.cdo.cdodefs.CDODefsPackage;
import org.eclipse.emf.cdo.cdodefs.CDOTransactionDef;

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
    CDOSession cdoSession = (CDOSession) getCdoSessionDef().getInstance();
    return cdoSession.openTransaction();
  }

} // CDOTransactionDefImpl
