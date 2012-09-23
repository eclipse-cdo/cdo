/**
 */
package org.eclipse.emf.cdo.tests.legacy.model3.impl;

import org.eclipse.emf.cdo.tests.legacy.model3.Model3Package;
import org.eclipse.emf.cdo.tests.model3.ClassWithJavaObjectAttribute;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.EObjectImpl;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Class With Java Object Attribute</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.tests.legacy.model3.impl.ClassWithJavaObjectAttributeImpl#getJavaObject <em>Java Object</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class ClassWithJavaObjectAttributeImpl extends EObjectImpl implements ClassWithJavaObjectAttribute
{
  /**
   * The default value of the '{@link #getJavaObject() <em>Java Object</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getJavaObject()
   * @generated
   * @ordered
   */
  protected static final Object JAVA_OBJECT_EDEFAULT = null;

  /**
   * The cached value of the '{@link #getJavaObject() <em>Java Object</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getJavaObject()
   * @generated
   * @ordered
   */
  protected Object javaObject = JAVA_OBJECT_EDEFAULT;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected ClassWithJavaObjectAttributeImpl()
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
    return Model3Package.eINSTANCE.getClassWithJavaObjectAttribute();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public Object getJavaObject()
  {
    return javaObject;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setJavaObject(Object newJavaObject)
  {
    Object oldJavaObject = javaObject;
    javaObject = newJavaObject;
    if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET,
          Model3Package.CLASS_WITH_JAVA_OBJECT_ATTRIBUTE__JAVA_OBJECT, oldJavaObject, javaObject));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Object eGet(int featureID, boolean resolve, boolean coreType)
  {
    switch (featureID)
    {
    case Model3Package.CLASS_WITH_JAVA_OBJECT_ATTRIBUTE__JAVA_OBJECT:
      return getJavaObject();
    }
    return super.eGet(featureID, resolve, coreType);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void eSet(int featureID, Object newValue)
  {
    switch (featureID)
    {
    case Model3Package.CLASS_WITH_JAVA_OBJECT_ATTRIBUTE__JAVA_OBJECT:
      setJavaObject(newValue);
      return;
    }
    super.eSet(featureID, newValue);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void eUnset(int featureID)
  {
    switch (featureID)
    {
    case Model3Package.CLASS_WITH_JAVA_OBJECT_ATTRIBUTE__JAVA_OBJECT:
      setJavaObject(JAVA_OBJECT_EDEFAULT);
      return;
    }
    super.eUnset(featureID);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public boolean eIsSet(int featureID)
  {
    switch (featureID)
    {
    case Model3Package.CLASS_WITH_JAVA_OBJECT_ATTRIBUTE__JAVA_OBJECT:
      return JAVA_OBJECT_EDEFAULT == null ? javaObject != null : !JAVA_OBJECT_EDEFAULT.equals(javaObject);
    }
    return super.eIsSet(featureID);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public String toString()
  {
    if (eIsProxy())
      return super.toString();

    StringBuffer result = new StringBuffer(super.toString());
    result.append(" (javaObject: ");
    result.append(javaObject);
    result.append(')');
    return result.toString();
  }

} //ClassWithJavaObjectAttributeImpl
