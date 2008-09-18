/**
 * <copyright>
 * </copyright>
 *
 * $Id: ParameterImpl.java,v 1.2 2008-09-18 12:56:15 estepper Exp $
 */
package org.eclipse.emf.cdo.tests.legacy.mango.impl;

import org.eclipse.emf.cdo.tests.legacy.mango.MangoPackage;
import org.eclipse.emf.cdo.tests.mango.Parameter;
import org.eclipse.emf.cdo.tests.mango.ParameterPassing;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.EObjectImpl;

/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>Parameter</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 * <li>{@link org.eclipse.emf.cdo.tests.legacy.mango.impl.ParameterImpl#getName <em>Name</em>}</li>
 * <li>{@link org.eclipse.emf.cdo.tests.legacy.mango.impl.ParameterImpl#getPassing <em>Passing</em>}</li>
 * </ul>
 * </p>
 * 
 * @generated
 */
public class ParameterImpl extends EObjectImpl implements Parameter
{
  /**
   * The default value of the '{@link #getName() <em>Name</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc
   * -->
   * 
   * @see #getName()
   * @generated
   * @ordered
   */
  protected static final String NAME_EDEFAULT = null;

  /**
   * The cached value of the '{@link #getName() <em>Name</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @see #getName()
   * @generated
   * @ordered
   */
  protected String name = NAME_EDEFAULT;

  /**
   * The default value of the '{@link #getPassing() <em>Passing</em>}' attribute. <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * 
   * @see #getPassing()
   * @generated
   * @ordered
   */
  protected static final ParameterPassing PASSING_EDEFAULT = ParameterPassing.BY_VALUE;

  /**
   * The cached value of the '{@link #getPassing() <em>Passing</em>}' attribute. <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * 
   * @see #getPassing()
   * @generated
   * @ordered
   */
  protected ParameterPassing passing = PASSING_EDEFAULT;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  protected ParameterImpl()
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
    return MangoPackage.Literals.PARAMETER;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public String getName()
  {
    eFireRead(MangoPackage.PARAMETER__NAME);
    return name;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public void setName(String newName)
  {
    eFireWrite(MangoPackage.PARAMETER__NAME);
    String oldName = name;
    name = newName;
    if (eNotificationRequired())
    {
      eNotify(new ENotificationImpl(this, Notification.SET, MangoPackage.PARAMETER__NAME, oldName, name));
    }
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public ParameterPassing getPassing()
  {
    eFireRead(MangoPackage.PARAMETER__PASSING);
    return passing;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public void setPassing(ParameterPassing newPassing)
  {
    eFireWrite(MangoPackage.PARAMETER__PASSING);
    ParameterPassing oldPassing = passing;
    passing = newPassing == null ? PASSING_EDEFAULT : newPassing;
    if (eNotificationRequired())
    {
      eNotify(new ENotificationImpl(this, Notification.SET, MangoPackage.PARAMETER__PASSING, oldPassing, passing));
    }
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  @Override
  public Object eGet(int featureID, boolean resolve, boolean coreType)
  {
    switch (featureID)
    {
    case MangoPackage.PARAMETER__NAME:
      return getName();
    case MangoPackage.PARAMETER__PASSING:
      return getPassing();
    }
    return super.eGet(featureID, resolve, coreType);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  @Override
  public void eSet(int featureID, Object newValue)
  {
    switch (featureID)
    {
    case MangoPackage.PARAMETER__NAME:
      setName((String)newValue);
      return;
    case MangoPackage.PARAMETER__PASSING:
      setPassing((ParameterPassing)newValue);
      return;
    }
    super.eSet(featureID, newValue);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  @Override
  public void eUnset(int featureID)
  {
    switch (featureID)
    {
    case MangoPackage.PARAMETER__NAME:
      setName(NAME_EDEFAULT);
      return;
    case MangoPackage.PARAMETER__PASSING:
      setPassing(PASSING_EDEFAULT);
      return;
    }
    super.eUnset(featureID);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  @Override
  public boolean eIsSet(int featureID)
  {
    switch (featureID)
    {
    case MangoPackage.PARAMETER__NAME:
      return NAME_EDEFAULT == null ? name != null : !NAME_EDEFAULT.equals(name);
    case MangoPackage.PARAMETER__PASSING:
      return passing != PASSING_EDEFAULT;
    }
    return super.eIsSet(featureID);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  @Override
  public String toString()
  {
    if (eIsProxy())
    {
      return super.toString();
    }

    StringBuffer result = new StringBuffer(super.toString());
    result.append(" (name: ");
    result.append(name);
    result.append(", passing: ");
    result.append(passing);
    result.append(')');
    return result.toString();
  }

} // ParameterImpl
