/**
 */
package org.eclipse.emf.cdo.releng.predicates.impl;

import org.eclipse.emf.cdo.releng.predicates.NaturePredicate;
import org.eclipse.emf.cdo.releng.predicates.PredicatesPackage;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.MinimalEObjectImpl;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;

import java.lang.reflect.InvocationTargetException;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Nature Predicate</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.releng.predicates.impl.NaturePredicateImpl#getNature <em>Nature</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class NaturePredicateImpl extends MinimalEObjectImpl.Container implements NaturePredicate
{
  /**
   * The default value of the '{@link #getNature() <em>Nature</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getNature()
   * @generated
   * @ordered
   */
  protected static final String NATURE_EDEFAULT = null;

  /**
   * The cached value of the '{@link #getNature() <em>Nature</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getNature()
   * @generated
   * @ordered
   */
  protected String nature = NATURE_EDEFAULT;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected NaturePredicateImpl()
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
    return PredicatesPackage.Literals.NATURE_PREDICATE;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public String getNature()
  {
    return nature;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setNature(String newNature)
  {
    String oldNature = nature;
    nature = newNature;
    if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, PredicatesPackage.NATURE_PREDICATE__NATURE, oldNature, nature));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public boolean matches(IProject project)
  {
    try
    {
      String[] natureIds = project.getDescription().getNatureIds();
      for (String natureId : natureIds)
      {
        if (natureId.equals(nature))
        {
          return true;
        }
      }
    }
    catch (CoreException ex)
    {
      // Ignore
    }
    return false;
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
      case PredicatesPackage.NATURE_PREDICATE__NATURE:
        return getNature();
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
      case PredicatesPackage.NATURE_PREDICATE__NATURE:
        setNature((String)newValue);
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
      case PredicatesPackage.NATURE_PREDICATE__NATURE:
        setNature(NATURE_EDEFAULT);
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
      case PredicatesPackage.NATURE_PREDICATE__NATURE:
        return NATURE_EDEFAULT == null ? nature != null : !NATURE_EDEFAULT.equals(nature);
    }
    return super.eIsSet(featureID);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Object eInvoke(int operationID, EList<?> arguments) throws InvocationTargetException
  {
    switch (operationID)
    {
      case PredicatesPackage.NATURE_PREDICATE___MATCHES__IPROJECT:
        return matches((IProject)arguments.get(0));
    }
    return super.eInvoke(operationID, arguments);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public String toString()
  {
    if (eIsProxy()) return super.toString();

    StringBuffer result = new StringBuffer(super.toString());
    result.append(" (nature: ");
    result.append(nature);
    result.append(')');
    return result.toString();
  }

} // NaturePredicateImpl
