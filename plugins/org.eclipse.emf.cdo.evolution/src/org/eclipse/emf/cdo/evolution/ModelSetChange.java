/**
 */
package org.eclipse.emf.cdo.evolution;

import org.eclipse.emf.ecore.EModelElement;

import java.util.Map;
import java.util.Set;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Model Set Change</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.evolution.ModelSetChange#getOldModelSet <em>Old Model Set</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.evolution.ModelSetChange#getNewModelSet <em>New Model Set</em>}</li>
 * </ul>
 *
 * @see org.eclipse.emf.cdo.evolution.EvolutionPackage#getModelSetChange()
 * @model
 * @generated
 */
public interface ModelSetChange extends Change
{
  /**
   * Returns the value of the '<em><b>Old Model Set</b></em>' reference.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Old Model Set</em>' reference isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Old Model Set</em>' reference.
   * @see org.eclipse.emf.cdo.evolution.EvolutionPackage#getModelSetChange_OldModelSet()
   * @model suppressedSetVisibility="true"
   * @generated
   */
  ModelSet getOldModelSet();

  /**
   * Returns the value of the '<em><b>New Model Set</b></em>' reference.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>New Model Set</em>' reference isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>New Model Set</em>' reference.
   * @see org.eclipse.emf.cdo.evolution.EvolutionPackage#getModelSetChange_NewModelSet()
   * @model suppressedSetVisibility="true"
   * @generated
   */
  ModelSet getNewModelSet();

  ModelSet[] getModelSetChain();

  Map<EModelElement, ElementChange> getElementChanges();

  Map<EModelElement, EModelElement> getNewToOldElements();

  Map<EModelElement, Set<EModelElement>> getOldToNewElements();

  Set<EModelElement> getAddedElements();

  Set<EModelElement> getRemovedElements();

} // ModelSetChange
