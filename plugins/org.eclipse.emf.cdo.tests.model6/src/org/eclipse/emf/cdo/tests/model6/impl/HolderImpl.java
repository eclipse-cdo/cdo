/**
 */
package org.eclipse.emf.cdo.tests.model6.impl;

import org.eclipse.emf.cdo.tests.model6.Holdable;
import org.eclipse.emf.cdo.tests.model6.Holder;
import org.eclipse.emf.cdo.tests.model6.Model6Package;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Holder</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.tests.model6.impl.HolderImpl#getHeld <em>Held</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.tests.model6.impl.HolderImpl#getOwned <em>Owned</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class HolderImpl extends HoldableImpl implements Holder
{
  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected HolderImpl()
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
    return Model6Package.Literals.HOLDER;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @SuppressWarnings("unchecked")
  public EList<Holdable> getHeld()
  {
    return (EList<Holdable>)eGet(Model6Package.Literals.HOLDER__HELD, true);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @SuppressWarnings("unchecked")
  public EList<Holdable> getOwned()
  {
    return (EList<Holdable>)eGet(Model6Package.Literals.HOLDER__OWNED, true);
  }

} // HolderImpl
