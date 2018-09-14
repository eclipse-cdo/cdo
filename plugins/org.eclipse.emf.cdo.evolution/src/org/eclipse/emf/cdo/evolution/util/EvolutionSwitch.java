/**
 */
package org.eclipse.emf.cdo.evolution.util;

import org.eclipse.emf.cdo.evolution.Change;
import org.eclipse.emf.cdo.evolution.ElementChange;
import org.eclipse.emf.cdo.evolution.Evolution;
import org.eclipse.emf.cdo.evolution.EvolutionPackage;
import org.eclipse.emf.cdo.evolution.FeaturePathMigration;
import org.eclipse.emf.cdo.evolution.Migration;
import org.eclipse.emf.cdo.evolution.Model;
import org.eclipse.emf.cdo.evolution.ModelSet;
import org.eclipse.emf.cdo.evolution.ModelSetChange;
import org.eclipse.emf.cdo.evolution.PropertyChange;
import org.eclipse.emf.cdo.evolution.Release;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.util.Switch;

/**
 * <!-- begin-user-doc -->
 * The <b>Switch</b> for the model's inheritance hierarchy.
 * It supports the call {@link #doSwitch(EObject) doSwitch(object)}
 * to invoke the <code>caseXXX</code> method for each class of the model,
 * starting with the actual class of the object
 * and proceeding up the inheritance hierarchy
 * until a non-null result is returned,
 * which is the result of the switch.
 * <!-- end-user-doc -->
 * @see org.eclipse.emf.cdo.evolution.EvolutionPackage
 * @generated
 */
public class EvolutionSwitch<T> extends Switch<T>
{
  /**
   * The cached model package
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected static EvolutionPackage modelPackage;

  /**
   * Creates an instance of the switch.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EvolutionSwitch()
  {
    if (modelPackage == null)
    {
      modelPackage = EvolutionPackage.eINSTANCE;
    }
  }

  /**
   * Checks whether this is a switch for the given package.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param ePackage the package in question.
   * @return whether this is a switch for the given package.
   * @generated
   */
  @Override
  protected boolean isSwitchFor(EPackage ePackage)
  {
    return ePackage == modelPackage;
  }

  /**
   * Calls <code>caseXXX</code> for each class of the model until one returns a non null result; it yields that result.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the first non-null result returned by a <code>caseXXX</code> call.
   * @generated
   */
  @Override
  protected T doSwitch(int classifierID, EObject theEObject)
  {
    switch (classifierID)
    {
    case EvolutionPackage.MODEL_SET:
    {
      ModelSet modelSet = (ModelSet)theEObject;
      T result = caseModelSet(modelSet);
      if (result == null)
      {
        result = defaultCase(theEObject);
      }
      return result;
    }
    case EvolutionPackage.MODEL:
    {
      Model model = (Model)theEObject;
      T result = caseModel(model);
      if (result == null)
      {
        result = defaultCase(theEObject);
      }
      return result;
    }
    case EvolutionPackage.EVOLUTION:
    {
      Evolution evolution = (Evolution)theEObject;
      T result = caseEvolution(evolution);
      if (result == null)
      {
        result = caseModelSet(evolution);
      }
      if (result == null)
      {
        result = defaultCase(theEObject);
      }
      return result;
    }
    case EvolutionPackage.RELEASE:
    {
      Release release = (Release)theEObject;
      T result = caseRelease(release);
      if (result == null)
      {
        result = caseModelSet(release);
      }
      if (result == null)
      {
        result = defaultCase(theEObject);
      }
      return result;
    }
    case EvolutionPackage.CHANGE:
    {
      Change change = (Change)theEObject;
      T result = caseChange(change);
      if (result == null)
      {
        result = defaultCase(theEObject);
      }
      return result;
    }
    case EvolutionPackage.MODEL_SET_CHANGE:
    {
      ModelSetChange modelSetChange = (ModelSetChange)theEObject;
      T result = caseModelSetChange(modelSetChange);
      if (result == null)
      {
        result = caseChange(modelSetChange);
      }
      if (result == null)
      {
        result = defaultCase(theEObject);
      }
      return result;
    }
    case EvolutionPackage.ELEMENT_CHANGE:
    {
      ElementChange elementChange = (ElementChange)theEObject;
      T result = caseElementChange(elementChange);
      if (result == null)
      {
        result = caseChange(elementChange);
      }
      if (result == null)
      {
        result = defaultCase(theEObject);
      }
      return result;
    }
    case EvolutionPackage.PROPERTY_CHANGE:
    {
      PropertyChange propertyChange = (PropertyChange)theEObject;
      T result = casePropertyChange(propertyChange);
      if (result == null)
      {
        result = caseChange(propertyChange);
      }
      if (result == null)
      {
        result = defaultCase(theEObject);
      }
      return result;
    }
    case EvolutionPackage.MIGRATION:
    {
      Migration migration = (Migration)theEObject;
      T result = caseMigration(migration);
      if (result == null)
      {
        result = defaultCase(theEObject);
      }
      return result;
    }
    case EvolutionPackage.FEATURE_PATH_MIGRATION:
    {
      FeaturePathMigration featurePathMigration = (FeaturePathMigration)theEObject;
      T result = caseFeaturePathMigration(featurePathMigration);
      if (result == null)
      {
        result = caseMigration(featurePathMigration);
      }
      if (result == null)
      {
        result = defaultCase(theEObject);
      }
      return result;
    }
    default:
      return defaultCase(theEObject);
    }
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Model Set</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Model Set</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseModelSet(ModelSet object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Model</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Model</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseModel(Model object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Evolution</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Evolution</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseEvolution(Evolution object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Release</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Release</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseRelease(Release object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Change</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Change</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseChange(Change object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Model Set Change</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Model Set Change</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseModelSetChange(ModelSetChange object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Element Change</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Element Change</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseElementChange(ElementChange object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Property Change</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Property Change</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T casePropertyChange(PropertyChange object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Migration</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Migration</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseMigration(Migration object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Feature Path Migration</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Feature Path Migration</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseFeaturePathMigration(FeaturePathMigration object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>EObject</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch, but this is the last case anyway.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>EObject</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject)
   * @generated
   */
  @Override
  public T defaultCase(EObject object)
  {
    return null;
  }

} // EvolutionSwitch
