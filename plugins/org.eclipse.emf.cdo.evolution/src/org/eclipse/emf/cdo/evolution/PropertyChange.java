/**
 */
package org.eclipse.emf.cdo.evolution;

import org.eclipse.emf.ecore.EStructuralFeature;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Property Change</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.evolution.PropertyChange#getFeature <em>Feature</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.evolution.PropertyChange#getOldValue <em>Old Value</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.evolution.PropertyChange#getNewValue <em>New Value</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.evolution.PropertyChange#getKind <em>Kind</em>}</li>
 * </ul>
 *
 * @see org.eclipse.emf.cdo.evolution.EvolutionPackage#getPropertyChange()
 * @model
 * @generated
 */
public interface PropertyChange extends Change
{
  /**
   * Returns the value of the '<em><b>Feature</b></em>' reference.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Feature</em>' reference isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Feature</em>' reference.
   * @see org.eclipse.emf.cdo.evolution.EvolutionPackage#getPropertyChange_Feature()
   * @model suppressedSetVisibility="true"
   * @generated
   */
  EStructuralFeature getFeature();

  /**
   * Returns the value of the '<em><b>Old Value</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Old Value</em>' attribute isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Old Value</em>' attribute.
   * @see org.eclipse.emf.cdo.evolution.EvolutionPackage#getPropertyChange_OldValue()
   * @model suppressedSetVisibility="true"
   * @generated
   */
  Object getOldValue();

  /**
   * Returns the value of the '<em><b>New Value</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>New Value</em>' attribute isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>New Value</em>' attribute.
   * @see org.eclipse.emf.cdo.evolution.EvolutionPackage#getPropertyChange_NewValue()
   * @model suppressedSetVisibility="true"
   * @generated
   */
  Object getNewValue();

  /**
   * Returns the value of the '<em><b>Kind</b></em>' attribute.
   * The literals are from the enumeration {@link org.eclipse.emf.cdo.evolution.ChangeKind}.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Kind</em>' attribute isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Kind</em>' attribute.
   * @see org.eclipse.emf.cdo.evolution.ChangeKind
   * @see org.eclipse.emf.cdo.evolution.EvolutionPackage#getPropertyChange_Kind()
   * @model transient="true" changeable="false" volatile="true" derived="true"
   * @generated
   */
  ChangeKind getKind();

} // PropertyChange
