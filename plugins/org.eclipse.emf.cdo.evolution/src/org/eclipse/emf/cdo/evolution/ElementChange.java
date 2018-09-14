/**
 */
package org.eclipse.emf.cdo.evolution;

import org.eclipse.emf.ecore.EModelElement;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Element Change</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.evolution.ElementChange#getOldElement <em>Old Element</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.evolution.ElementChange#getNewElement <em>New Element</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.evolution.ElementChange#getKind <em>Kind</em>}</li>
 * </ul>
 *
 * @see org.eclipse.emf.cdo.evolution.EvolutionPackage#getElementChange()
 * @model annotation="http://www.eclipse.org/emf/2002/Ecore constraints='FeaturePathIsKnown'"
 * @generated
 */
public interface ElementChange extends Change
{
  /**
   * Returns the value of the '<em><b>Old Element</b></em>' reference.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Old Element</em>' reference isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Old Element</em>' reference.
   * @see org.eclipse.emf.cdo.evolution.EvolutionPackage#getElementChange_OldElement()
   * @model suppressedSetVisibility="true"
   * @generated
   */
  EModelElement getOldElement();

  /**
   * Returns the value of the '<em><b>New Element</b></em>' reference.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>New Element</em>' reference isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>New Element</em>' reference.
   * @see org.eclipse.emf.cdo.evolution.EvolutionPackage#getElementChange_NewElement()
   * @model suppressedSetVisibility="true"
   * @generated
   */
  EModelElement getNewElement();

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
   * @see org.eclipse.emf.cdo.evolution.EvolutionPackage#getElementChange_Kind()
   * @model suppressedSetVisibility="true"
   * @generated
   */
  ChangeKind getKind();

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @model kind="operation"
   * @generated
   */
  EModelElement getElement();

} // ElementChange
