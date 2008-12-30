/**
 * <copyright>
 * </copyright>
 *
 * $Id: TCPSelectorDefImpl.java,v 1.2 2008-12-30 08:43:13 estepper Exp $
 */
package org.eclipse.net4j.net4jdefs.impl;

import org.eclipse.net4j.internal.tcp.TCPSelector;
import org.eclipse.net4j.net4jdefs.Net4jDefsPackage;
import org.eclipse.net4j.net4jdefs.TCPSelectorDef;
import org.eclipse.net4j.util.net4jutildefs.impl.DefImpl;

import org.eclipse.emf.ecore.EClass;

/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>TCP Selector Def</b></em>'. <!-- end-user-doc
 * -->
 * <p>
 * </p>
 * 
 * @generated
 */
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
    selector.activate();
    return selector;
  }
} // TCPSelectorDefImpl
