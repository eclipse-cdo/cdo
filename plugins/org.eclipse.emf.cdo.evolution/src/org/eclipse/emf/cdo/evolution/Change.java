/**
 */
package org.eclipse.emf.cdo.evolution;

import org.eclipse.emf.cdo.CDOObject;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EModelElement;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Change</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.evolution.Change#getParent <em>Parent</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.evolution.Change#getChildren <em>Children</em>}</li>
 * </ul>
 *
 * @see org.eclipse.emf.cdo.evolution.EvolutionPackage#getChange()
 * @model abstract="true"
 * @extends CDOObject
 * @generated
 */
public interface Change extends CDOObject
{
  /**
   * Returns the value of the '<em><b>Parent</b></em>' container reference.
   * It is bidirectional and its opposite is '{@link org.eclipse.emf.cdo.evolution.Change#getChildren <em>Children</em>}'.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Parent</em>' container reference isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Parent</em>' container reference.
   * @see #setParent(Change)
   * @see org.eclipse.emf.cdo.evolution.EvolutionPackage#getChange_Parent()
   * @see org.eclipse.emf.cdo.evolution.Change#getChildren
   * @model opposite="children" resolveProxies="false" transient="false"
   * @generated
   */
  Change getParent();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.evolution.Change#getParent <em>Parent</em>}' container reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Parent</em>' container reference.
   * @see #getParent()
   * @generated
   */
  void setParent(Change value);

  /**
   * Returns the value of the '<em><b>Children</b></em>' containment reference list.
   * The list contents are of type {@link org.eclipse.emf.cdo.evolution.Change}.
   * It is bidirectional and its opposite is '{@link org.eclipse.emf.cdo.evolution.Change#getParent <em>Parent</em>}'.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Children</em>' containment reference list isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Children</em>' containment reference list.
   * @see org.eclipse.emf.cdo.evolution.EvolutionPackage#getChange_Children()
   * @see org.eclipse.emf.cdo.evolution.Change#getParent
   * @model opposite="parent" containment="true"
   * @generated
   */
  EList<Change> getChildren();

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @model kind="operation"
   * @generated
   */
  ModelSet getOldModelSet();

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @model kind="operation"
   * @generated
   */
  ModelSet getNewModelSet();

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @model kind="operation"
   * @generated
   */
  ModelSetChange getModelSetChange();

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @model
   * @generated
   */
  EModelElement getOldElementFor(EModelElement newElement);

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @model
   * @generated
   */
  EList<EModelElement> getNewElementsFor(EModelElement oldElement);

} // Change
