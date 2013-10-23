/**
 */
package org.eclipse.emf.cdo.releng.workingsets.impl;

import org.eclipse.emf.cdo.releng.workingsets.InclusionPredicate;
import org.eclipse.emf.cdo.releng.workingsets.WorkingSet;
import org.eclipse.emf.cdo.releng.workingsets.WorkingSetsPackage;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.impl.MinimalEObjectImpl;
import org.eclipse.emf.ecore.util.EObjectResolvingEList;

import org.eclipse.core.resources.IProject;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Inclusion Predicate</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.releng.workingsets.impl.InclusionPredicateImpl#getIncludedWorkingSets <em>Included Working Sets</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class InclusionPredicateImpl extends MinimalEObjectImpl.Container implements InclusionPredicate
{
  /**
   * The cached value of the '{@link #getIncludedWorkingSets() <em>Included Working Sets</em>}' reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getIncludedWorkingSets()
   * @generated
   * @ordered
   */
  protected EList<WorkingSet> includedWorkingSets;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected InclusionPredicateImpl()
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
    return WorkingSetsPackage.Literals.INCLUSION_PREDICATE;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EList<WorkingSet> getIncludedWorkingSets()
  {
    if (includedWorkingSets == null)
    {
      includedWorkingSets = new EObjectResolvingEList<WorkingSet>(WorkingSet.class, this,
          WorkingSetsPackage.INCLUSION_PREDICATE__INCLUDED_WORKING_SETS);
    }
    return includedWorkingSets;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public boolean matches(IProject project)
  {
    for (WorkingSet workingSet : getIncludedWorkingSets())
    {
      if (workingSet.matches(project))
      {
        return true;
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
    case WorkingSetsPackage.INCLUSION_PREDICATE__INCLUDED_WORKING_SETS:
      return getIncludedWorkingSets();
    }
    return super.eGet(featureID, resolve, coreType);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @SuppressWarnings("unchecked")
  @Override
  public void eSet(int featureID, Object newValue)
  {
    switch (featureID)
    {
    case WorkingSetsPackage.INCLUSION_PREDICATE__INCLUDED_WORKING_SETS:
      getIncludedWorkingSets().clear();
      getIncludedWorkingSets().addAll((Collection<? extends WorkingSet>)newValue);
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
    case WorkingSetsPackage.INCLUSION_PREDICATE__INCLUDED_WORKING_SETS:
      getIncludedWorkingSets().clear();
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
    case WorkingSetsPackage.INCLUSION_PREDICATE__INCLUDED_WORKING_SETS:
      return includedWorkingSets != null && !includedWorkingSets.isEmpty();
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
    case WorkingSetsPackage.INCLUSION_PREDICATE___MATCHES__IPROJECT:
      return matches((IProject)arguments.get(0));
    }
    return super.eInvoke(operationID, arguments);
  }

} // InclusionPredicateImpl
