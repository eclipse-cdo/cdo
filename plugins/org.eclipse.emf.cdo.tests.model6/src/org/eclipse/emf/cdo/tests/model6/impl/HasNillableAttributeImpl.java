/**
 */
package org.eclipse.emf.cdo.tests.model6.impl;

import org.eclipse.emf.cdo.tests.model6.HasNillableAttribute;
import org.eclipse.emf.cdo.tests.model6.Model6Package;

import org.eclipse.emf.internal.cdo.CDOObjectImpl;

import org.eclipse.emf.ecore.EClass;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Has Nillable Attribute</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.tests.model6.impl.HasNillableAttributeImpl#getNillable <em>Nillable</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class HasNillableAttributeImpl extends CDOObjectImpl implements HasNillableAttribute
{
  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected HasNillableAttributeImpl()
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
    return Model6Package.Literals.HAS_NILLABLE_ATTRIBUTE;
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
  public String getNillable()
  {
    return (String)eGet(Model6Package.Literals.HAS_NILLABLE_ATTRIBUTE__NILLABLE, true);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setNillable(String newNillable)
  {
    eSet(Model6Package.Literals.HAS_NILLABLE_ATTRIBUTE__NILLABLE, newNillable);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void unsetNillable()
  {
    eUnset(Model6Package.Literals.HAS_NILLABLE_ATTRIBUTE__NILLABLE);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public boolean isSetNillable()
  {
    return eIsSet(Model6Package.Literals.HAS_NILLABLE_ATTRIBUTE__NILLABLE);
  }

} // HasNillableAttributeImpl
