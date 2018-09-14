/**
 */
package org.eclipse.emf.cdo.evolution;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EFactory;
import org.eclipse.emf.ecore.EModelElement;
import org.eclipse.emf.ecore.EStructuralFeature;

/**
 * <!-- begin-user-doc -->
 * The <b>Factory</b> for the model.
 * It provides a create method for each non-abstract class of the model.
 * <!-- end-user-doc -->
 * @see org.eclipse.emf.cdo.evolution.EvolutionPackage
 * @generated
 */
public interface EvolutionFactory extends EFactory
{
  /**
   * The singleton instance of the factory.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  EvolutionFactory eINSTANCE = org.eclipse.emf.cdo.evolution.impl.EvolutionFactoryImpl.init();

  /**
   * Returns a new object of class '<em>Model</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Model</em>'.
   * @generated
   */
  Model createModel();

  Model createModel(URI uri);

  /**
   * Returns a new object of class '<em>Evolution</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Evolution</em>'.
   * @generated
   */
  Evolution createEvolution();

  /**
   * Returns a new object of class '<em>Release</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Release</em>'.
   * @generated
   */
  Release createRelease();

  /**
   * Returns a new object of class '<em>Model Set Change</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Model Set Change</em>'.
   * @generated
   */
  ModelSetChange createModelSetChange();

  ModelSetChange createModelSetChange(ModelSet[] modelSetChain);

  /**
   * Returns a new object of class '<em>Element Change</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Element Change</em>'.
   * @generated
   */
  ElementChange createElementChange();

  ElementChange createElementChange(EModelElement oldElement, EModelElement newElement, ChangeKind kind);

  /**
   * Returns a new object of class '<em>Property Change</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Property Change</em>'.
   * @generated
   */
  PropertyChange createPropertyChange();

  /**
   * Returns a new object of class '<em>Feature Path Migration</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Feature Path Migration</em>'.
   * @generated
   */
  FeaturePathMigration createFeaturePathMigration();

  PropertyChange createPropertyChange(EStructuralFeature feature, Object oldValue, Object newValue);

  /**
   * Returns an instance of data type '<em>Change Kind</em>' corresponding the given literal.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param literal a literal of the data type.
   * @return a new instance value of the data type.
   * @generated
   */
  ChangeKind createChangeKind(String literal);

  /**
   * Returns a literal representation of an instance of data type '<em>Change Kind</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param instanceValue an instance value of the data type.
   * @return a literal representation of the instance value.
   * @generated
   */
  String convertChangeKind(ChangeKind instanceValue);

  /**
   * Returns an instance of data type '<em>URI</em>' corresponding the given literal.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param literal a literal of the data type.
   * @return a new instance value of the data type.
   * @generated
   */
  URI createURI(String literal);

  /**
   * Returns a literal representation of an instance of data type '<em>URI</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param instanceValue an instance value of the data type.
   * @return a literal representation of the instance value.
   * @generated
   */
  String convertURI(URI instanceValue);

  /**
   * Returns the package supported by this factory.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the package supported by this factory.
   * @generated
   */
  EvolutionPackage getEvolutionPackage();

} // EvolutionFactory
