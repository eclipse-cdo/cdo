/**
 */
package org.eclipse.emf.cdo.evolution;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.common.model.CDOPackageRegistry;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EModelElement;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EStructuralFeature;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Model Set</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.evolution.ModelSet#getChange <em>Change</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.evolution.ModelSet#getMigrations <em>Migrations</em>}</li>
 * </ul>
 *
 * @see org.eclipse.emf.cdo.evolution.EvolutionPackage#getModelSet()
 * @model abstract="true"
 * @extends CDOObject
 * @generated
 */
public interface ModelSet extends CDOObject
{
  /**
   * Returns the value of the '<em><b>Change</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Change</em>' reference isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Change</em>' containment reference.
   * @see #isSetChange()
   * @see org.eclipse.emf.cdo.evolution.EvolutionPackage#getModelSet_Change()
   * @model containment="true" unsettable="true" transient="true" derived="true" suppressedSetVisibility="true" suppressedUnsetVisibility="true"
   * @generated
   */
  ModelSetChange getChange();

  /**
   * Returns whether the value of the '{@link org.eclipse.emf.cdo.evolution.ModelSet#getChange <em>Change</em>}' containment reference is set.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return whether the value of the '<em>Change</em>' containment reference is set.
   * @see #getChange()
   * @generated
   */
  boolean isSetChange();

  /**
   * Returns the value of the '<em><b>Migrations</b></em>' containment reference list.
   * The list contents are of type {@link org.eclipse.emf.cdo.evolution.Migration}.
   * It is bidirectional and its opposite is '{@link org.eclipse.emf.cdo.evolution.Migration#getModelSet <em>Model Set</em>}'.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Migrations</em>' containment reference list isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Migrations</em>' containment reference list.
   * @see org.eclipse.emf.cdo.evolution.EvolutionPackage#getModelSet_Migrations()
   * @see org.eclipse.emf.cdo.evolution.Migration#getModelSet
   * @model opposite="modelSet" containment="true" resolveProxies="true"
   * @generated
   */
  EList<Migration> getMigrations();

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @model kind="operation"
   * @generated
   */
  Evolution getEvolution();

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @model kind="operation"
   * @generated
   */
  int getVersion();

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @model kind="operation"
   * @generated
   */
  Release getPreviousRelease();

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @model kind="operation"
   * @generated
   */
  EList<EPackage> getRootPackages();

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @model kind="operation"
   * @generated
   */
  EList<EPackage> getAllPackages();

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @model
   * @generated
   */
  EPackage getPackage(String nsURI);

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @model
   * @generated
   */
  boolean containsElement(EModelElement modelElement);

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @model
   * @generated
   */
  <T extends EModelElement> T getElement(String id);

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @model
   * @generated
   */
  String getElementID(EModelElement modelElement);

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @model
   * @generated
   */
  String getElementID(EModelElement modelElement, boolean considerOldIDs);

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @model
   * @generated
   */
  ModelSetChange compare(ModelSet other);

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @model
   * @generated
   */
  Migration getMigration(String diagnosticID);

  EList<ElementChange> getElementChanges(EClass elementType, ChangeKind... changeKinds);

  EList<PropertyChange> getPropertyChanges(EStructuralFeature feature, ChangeKind... changeKinds);

  CDOPackageRegistry createPackageRegistry();

} // ModelSet
