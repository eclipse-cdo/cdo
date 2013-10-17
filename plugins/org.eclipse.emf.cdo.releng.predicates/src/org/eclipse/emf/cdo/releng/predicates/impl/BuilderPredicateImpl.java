/**
 */
package org.eclipse.emf.cdo.releng.predicates.impl;

import org.eclipse.emf.cdo.releng.predicates.BuilderPredicate;
import org.eclipse.emf.cdo.releng.predicates.PredicatesPackage;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.MinimalEObjectImpl;

import org.eclipse.core.resources.ICommand;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;

import java.lang.reflect.InvocationTargetException;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Builder Predicate</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.releng.predicates.impl.BuilderPredicateImpl#getBuilder <em>Builder</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class BuilderPredicateImpl extends MinimalEObjectImpl.Container implements BuilderPredicate
{
  /**
   * The default value of the '{@link #getBuilder() <em>Builder</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getBuilder()
   * @generated
   * @ordered
   */
  protected static final String BUILDER_EDEFAULT = null;

  /**
   * The cached value of the '{@link #getBuilder() <em>Builder</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getBuilder()
   * @generated
   * @ordered
   */
  protected String builder = BUILDER_EDEFAULT;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected BuilderPredicateImpl()
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
    return PredicatesPackage.Literals.BUILDER_PREDICATE;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public String getBuilder()
  {
    return builder;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setBuilder(String newBuilder)
  {
    String oldBuilder = builder;
    builder = newBuilder;
    if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, PredicatesPackage.BUILDER_PREDICATE__BUILDER, oldBuilder, builder));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public boolean matches(IProject project)
  {
    String builder = getBuilder();
    if (builder != null)
    {
      try
      {
        for (ICommand command : project.getDescription().getBuildSpec())
        {
          String name = command.getBuilderName();
          if (builder.equals(name))
          {
            return true;
          }
        }
      }
      catch (CoreException ex)
      {
        // Ignore
      }
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
      case PredicatesPackage.BUILDER_PREDICATE__BUILDER:
        return getBuilder();
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
      case PredicatesPackage.BUILDER_PREDICATE__BUILDER:
        setBuilder((String)newValue);
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
      case PredicatesPackage.BUILDER_PREDICATE__BUILDER:
        setBuilder(BUILDER_EDEFAULT);
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
      case PredicatesPackage.BUILDER_PREDICATE__BUILDER:
        return BUILDER_EDEFAULT == null ? builder != null : !BUILDER_EDEFAULT.equals(builder);
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
      case PredicatesPackage.BUILDER_PREDICATE___MATCHES__IPROJECT:
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
    result.append(" (builder: ");
    result.append(builder);
    result.append(')');
    return result.toString();
  }

} // BuilderPredicateImpl
