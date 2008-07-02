/**
 * <copyright>
 * </copyright>
 *
 * $Id: BaseClassImpl.java,v 1.1.2.2 2008-07-02 14:10:02 estepper Exp $
 */
package base.impl;

import base.BaseClass;
import base.BasePackage;

import org.eclipse.emf.ecore.EClass;

import org.eclipse.emf.internal.cdo.CDOObjectImpl;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Class</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link base.impl.BaseClassImpl#getCouter <em>Couter</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class BaseClassImpl extends CDOObjectImpl implements BaseClass
{
  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected BaseClassImpl()
  {
    super();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  protected EClass eStaticClass()
  {
    return BasePackage.Literals.BASE_CLASS;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  protected int eStaticFeatureCount()
  {
    return 0;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public int getCouter()
  {
    return ((Integer)eGet(BasePackage.Literals.BASE_CLASS__COUTER, true)).intValue();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setCouter(int newCouter)
  {
    eSet(BasePackage.Literals.BASE_CLASS__COUTER, new Integer(newCouter));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void increment()
  {
    // TODO: implement this method
    // Ensure that you remove @generated or mark it @generated NOT
    throw new UnsupportedOperationException();
  }

} //BaseClassImpl
