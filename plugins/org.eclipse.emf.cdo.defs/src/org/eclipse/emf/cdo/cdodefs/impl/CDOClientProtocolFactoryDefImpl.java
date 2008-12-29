/**
 * <copyright>
 * </copyright>
 *
 * $Id: CDOClientProtocolFactoryDefImpl.java,v 1.2 2008-12-29 14:01:19 estepper Exp $
 */
package org.eclipse.emf.cdo.cdodefs.impl;

import org.eclipse.emf.cdo.cdodefs.CDOClientProtocolFactoryDef;
import org.eclipse.emf.cdo.cdodefs.CDODefsPackage;

import org.eclipse.emf.internal.cdo.protocol.CDOClientProtocolFactory;

import org.eclipse.net4j.net4jdefs.impl.ClientProtocolFactoryDefImpl;

import org.eclipse.emf.ecore.EClass;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>CDO Client Protocol Factory Def</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * </p>
 *
 * @generated
 */
public class CDOClientProtocolFactoryDefImpl extends ClientProtocolFactoryDefImpl implements CDOClientProtocolFactoryDef {
	/**
   * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
   * @generated
   */
	protected CDOClientProtocolFactoryDefImpl() {
    super();
  }

	/**
   * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
   * @generated
   */
	@Override
	protected EClass eStaticClass() {
    return CDODefsPackage.Literals.CDO_CLIENT_PROTOCOL_FACTORY_DEF;
  }

	@Override
	protected Object createInstance() {
		return new CDOClientProtocolFactory();
	}

} //CDOClientProtocolFactoryDefImpl
