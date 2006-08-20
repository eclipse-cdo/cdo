/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package testmodel1.impl;


import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.util.EObjectWithInverseResolvingEList;
import org.eclipse.emf.ecore.util.InternalEList;

import testmodel1.ExtendedNode;
import testmodel1.TestModel1Package;

import java.util.Collection;


/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Extended Node</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link testmodel1.impl.ExtendedNodeImpl#getBidiSource <em>Bidi Source</em>}</li>
 *   <li>{@link testmodel1.impl.ExtendedNodeImpl#getBidiTarget <em>Bidi Target</em>}</li>
 *   <li>{@link testmodel1.impl.ExtendedNodeImpl#getStringFeature2 <em>String Feature2</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class ExtendedNodeImpl extends TreeNodeImpl implements ExtendedNode
{
  /**
   * The cached value of the '{@link #getBidiSource() <em>Bidi Source</em>}' reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getBidiSource()
   * @generated
   * @ordered
   */
  protected EList bidiSource = null;

  /**
   * The cached value of the '{@link #getBidiTarget() <em>Bidi Target</em>}' reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getBidiTarget()
   * @generated
   * @ordered
   */
  protected EList bidiTarget = null;

  /**
   * The default value of the '{@link #getStringFeature2() <em>String Feature2</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getStringFeature2()
   * @generated
   * @ordered
   */
  protected static final String STRING_FEATURE2_EDEFAULT = null;

  /**
   * The cached value of the '{@link #getStringFeature2() <em>String Feature2</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getStringFeature2()
   * @generated
   * @ordered
   */
  protected String stringFeature2 = STRING_FEATURE2_EDEFAULT;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected ExtendedNodeImpl()
  {
    super();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected EClass eStaticClass()
  {
    return TestModel1Package.Literals.EXTENDED_NODE;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EList getBidiSource()
  {
    cdoLoad();
    if (bidiSource == null)
    {
      bidiSource = new EObjectWithInverseResolvingEList.ManyInverse(ExtendedNode.class, this,
          TestModel1Package.EXTENDED_NODE__BIDI_SOURCE,
          TestModel1Package.EXTENDED_NODE__BIDI_TARGET);
    }
    return bidiSource;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EList getBidiTarget()
  {
    cdoLoad();
    if (bidiTarget == null)
    {
      bidiTarget = new EObjectWithInverseResolvingEList.ManyInverse(ExtendedNode.class, this,
          TestModel1Package.EXTENDED_NODE__BIDI_TARGET,
          TestModel1Package.EXTENDED_NODE__BIDI_SOURCE);
    }
    return bidiTarget;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public String getStringFeature2()
  {
    cdoLoad();
    return stringFeature2;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setStringFeature2(String newStringFeature2)
  {
    cdoLoad();
    String oldStringFeature2 = stringFeature2;
    stringFeature2 = newStringFeature2;
    if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET,
          TestModel1Package.EXTENDED_NODE__STRING_FEATURE2, oldStringFeature2, stringFeature2));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public NotificationChain eInverseAdd(InternalEObject otherEnd, int featureID,
      NotificationChain msgs)
  {
    switch (featureID)
    {
      case TestModel1Package.EXTENDED_NODE__BIDI_SOURCE:
        return ((InternalEList) getBidiSource()).basicAdd(otherEnd, msgs);
      case TestModel1Package.EXTENDED_NODE__BIDI_TARGET:
        return ((InternalEList) getBidiTarget()).basicAdd(otherEnd, msgs);
    }
    return super.eInverseAdd(otherEnd, featureID, msgs);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID,
      NotificationChain msgs)
  {
    switch (featureID)
    {
      case TestModel1Package.EXTENDED_NODE__BIDI_SOURCE:
        return ((InternalEList) getBidiSource()).basicRemove(otherEnd, msgs);
      case TestModel1Package.EXTENDED_NODE__BIDI_TARGET:
        return ((InternalEList) getBidiTarget()).basicRemove(otherEnd, msgs);
    }
    return super.eInverseRemove(otherEnd, featureID, msgs);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public Object eGet(int featureID, boolean resolve, boolean coreType)
  {
    switch (featureID)
    {
      case TestModel1Package.EXTENDED_NODE__BIDI_SOURCE:
        return getBidiSource();
      case TestModel1Package.EXTENDED_NODE__BIDI_TARGET:
        return getBidiTarget();
      case TestModel1Package.EXTENDED_NODE__STRING_FEATURE2:
        return getStringFeature2();
    }
    return super.eGet(featureID, resolve, coreType);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void eSet(int featureID, Object newValue)
  {
    switch (featureID)
    {
      case TestModel1Package.EXTENDED_NODE__BIDI_SOURCE:
        getBidiSource().clear();
        getBidiSource().addAll((Collection) newValue);
        return;
      case TestModel1Package.EXTENDED_NODE__BIDI_TARGET:
        getBidiTarget().clear();
        getBidiTarget().addAll((Collection) newValue);
        return;
      case TestModel1Package.EXTENDED_NODE__STRING_FEATURE2:
        setStringFeature2((String) newValue);
        return;
    }
    super.eSet(featureID, newValue);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void eUnset(int featureID)
  {
    switch (featureID)
    {
      case TestModel1Package.EXTENDED_NODE__BIDI_SOURCE:
        getBidiSource().clear();
        return;
      case TestModel1Package.EXTENDED_NODE__BIDI_TARGET:
        getBidiTarget().clear();
        return;
      case TestModel1Package.EXTENDED_NODE__STRING_FEATURE2:
        setStringFeature2(STRING_FEATURE2_EDEFAULT);
        return;
    }
    super.eUnset(featureID);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public boolean eIsSet(int featureID)
  {
    switch (featureID)
    {
      case TestModel1Package.EXTENDED_NODE__BIDI_SOURCE:
        return bidiSource != null && !bidiSource.isEmpty();
      case TestModel1Package.EXTENDED_NODE__BIDI_TARGET:
        return bidiTarget != null && !bidiTarget.isEmpty();
      case TestModel1Package.EXTENDED_NODE__STRING_FEATURE2:
        return STRING_FEATURE2_EDEFAULT == null ? stringFeature2 != null
            : !STRING_FEATURE2_EDEFAULT.equals(stringFeature2);
    }
    return super.eIsSet(featureID);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public String toString()
  {
    if (eIsProxy()) return super.toString();

    StringBuffer result = new StringBuffer(super.toString());
    result.append(" (stringFeature2: ");
    result.append(stringFeature2);
    result.append(')');
    return result.toString();
  }

} //ExtendedNodeImpl