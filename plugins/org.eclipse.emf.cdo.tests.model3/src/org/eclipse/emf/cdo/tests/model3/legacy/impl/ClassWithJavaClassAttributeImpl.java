/**
 */
package org.eclipse.emf.cdo.tests.model3.legacy.impl;

import org.eclipse.emf.cdo.tests.model3.ClassWithJavaClassAttribute;
import org.eclipse.emf.cdo.tests.model3.legacy.Model3Package;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.EObjectImpl;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Class With Java Class Attribute</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.tests.model3.legacy.impl.ClassWithJavaClassAttributeImpl#getJavaClass <em>Java Class</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class ClassWithJavaClassAttributeImpl extends EObjectImpl implements ClassWithJavaClassAttribute
{
  /**
   * The cached value of the '{@link #getJavaClass() <em>Java Class</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getJavaClass()
   * @generated
   * @ordered
   */
  protected Class<?> javaClass;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected ClassWithJavaClassAttributeImpl()
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
    return Model3Package.eINSTANCE.getClassWithJavaClassAttribute();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public Class<?> getJavaClass()
  {
    return javaClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setJavaClass(Class<?> newJavaClass)
  {
    Class<?> oldJavaClass = javaClass;
    javaClass = newJavaClass;
    if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, Model3Package.CLASS_WITH_JAVA_CLASS_ATTRIBUTE__JAVA_CLASS,
          oldJavaClass, javaClass));
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
    case Model3Package.CLASS_WITH_JAVA_CLASS_ATTRIBUTE__JAVA_CLASS:
      return getJavaClass();
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
    case Model3Package.CLASS_WITH_JAVA_CLASS_ATTRIBUTE__JAVA_CLASS:
      setJavaClass((Class<?>)newValue);
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
    case Model3Package.CLASS_WITH_JAVA_CLASS_ATTRIBUTE__JAVA_CLASS:
      setJavaClass((Class<?>)null);
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
    case Model3Package.CLASS_WITH_JAVA_CLASS_ATTRIBUTE__JAVA_CLASS:
      return javaClass != null;
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
    result.append(" (javaClass: ");
    result.append(javaClass);
    result.append(')');
    return result.toString();
  }

} //ClassWithJavaClassAttributeImpl
