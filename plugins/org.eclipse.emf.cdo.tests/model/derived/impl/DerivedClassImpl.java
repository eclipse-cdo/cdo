/**
 * <copyright>
 * </copyright>
 *
 * $Id: DerivedClassImpl.java,v 1.1.2.3 2008-07-16 16:34:52 estepper Exp $
 */
package derived.impl;

import org.eclipse.emf.ecore.EClass;

import base.impl.BaseClassImpl;
import derived.DerivedClass;
import derived.DerivedPackage;

/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>Class</b></em>'. <!-- end-user-doc -->
 * <p>
 * </p>
 * 
 * @generated
 */
public class DerivedClassImpl extends BaseClassImpl implements DerivedClass
{
  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  protected DerivedClassImpl()
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
    return DerivedPackage.Literals.DERIVED_CLASS;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public void decrement()
  {
    // TODO: implement this method
    // Ensure that you remove @generated or mark it @generated NOT
    throw new UnsupportedOperationException();
  }

} // DerivedClassImpl
