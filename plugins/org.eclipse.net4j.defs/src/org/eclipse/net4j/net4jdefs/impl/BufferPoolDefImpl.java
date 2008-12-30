/**
 * <copyright>
 * </copyright>
 *
 * $Id: BufferPoolDefImpl.java,v 1.2 2008-12-30 08:43:13 estepper Exp $
 */
package org.eclipse.net4j.net4jdefs.impl;

import org.eclipse.net4j.Net4jUtil;
import org.eclipse.net4j.buffer.IBufferProvider;
import org.eclipse.net4j.net4jdefs.BufferPoolDef;
import org.eclipse.net4j.net4jdefs.Net4jDefsPackage;

import org.eclipse.emf.ecore.EClass;

/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>Buffer Pool Def</b></em>'. <!-- end-user-doc
 * -->
 * <p>
 * </p>
 * 
 * @generated
 */
public class BufferPoolDefImpl extends BufferProviderDefImpl implements BufferPoolDef
{
  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  protected BufferPoolDefImpl()
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
    return Net4jDefsPackage.Literals.BUFFER_POOL_DEF;
  }

  /**
   * creates and returns a buffer provider.
   * 
   * @generated NOT
   */
  @Override
  protected Object createInstance()
  {
    IBufferProvider bufferProvider = Net4jUtil.createBufferPool();
    // LifecycleUtil.activate(bufferProvider);
    return bufferProvider;
  }

} // BufferPoolDefImpl
