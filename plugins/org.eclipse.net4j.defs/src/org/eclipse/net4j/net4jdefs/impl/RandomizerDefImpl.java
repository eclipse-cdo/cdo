/**
 * <copyright>
 * </copyright>
 *
 * $Id: RandomizerDefImpl.java,v 1.1 2008-12-28 18:07:28 estepper Exp $
 */
package org.eclipse.net4j.net4jdefs.impl;

import org.eclipse.net4j.net4jdefs.Net4jDefsPackage;
import org.eclipse.net4j.net4jdefs.RandomizerDef;
import org.eclipse.net4j.util.net4jutildefs.impl.DefImpl;
import org.eclipse.net4j.util.security.Randomizer;

import org.eclipse.emf.ecore.EClass;

/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>Randomizer Def</b></em>'. <!-- end-user-doc -->
 * <p>
 * </p>
 * 
 * @generated
 */
public class RandomizerDefImpl extends DefImpl implements RandomizerDef
{
  @Override
  protected Object createInstance()
  {
    return new Randomizer();
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  protected RandomizerDefImpl()
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
    return Net4jDefsPackage.Literals.RANDOMIZER_DEF;
  }

} // RandomizerDefImpl
