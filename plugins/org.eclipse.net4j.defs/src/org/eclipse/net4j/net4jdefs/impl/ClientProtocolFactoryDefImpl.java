/**
 * <copyright>
 * </copyright>
 *
 * $Id: ClientProtocolFactoryDefImpl.java,v 1.2 2008-12-30 08:43:13 estepper Exp $
 */
package org.eclipse.net4j.net4jdefs.impl;

import org.eclipse.net4j.net4jdefs.ClientProtocolFactoryDef;
import org.eclipse.net4j.net4jdefs.Net4jDefsPackage;

import org.eclipse.emf.ecore.EClass;

/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>Client Protocol Factory Def</b></em>'. <!--
 * end-user-doc -->
 * <p>
 * </p>
 * 
 * @generated
 */
public abstract class ClientProtocolFactoryDefImpl extends ProtocolProviderDefImpl implements ClientProtocolFactoryDef
{
  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  protected ClientProtocolFactoryDefImpl()
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
    return Net4jDefsPackage.Literals.CLIENT_PROTOCOL_FACTORY_DEF;
  }

} // ClientProtocolFactoryDefImpl
