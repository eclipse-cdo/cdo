/**
 */
package org.eclipse.emf.cdo.releng.preferences;

import org.eclipse.emf.ecore.EFactory;

/**
 * <!-- begin-user-doc -->
 * The <b>Factory</b> for the model.
 * It provides a create method for each non-abstract class of the model.
 * <!-- end-user-doc -->
 * @see org.eclipse.emf.cdo.releng.preferences.PreferencesPackage
 * @generated
 */
public interface PreferencesFactory extends EFactory
{
  /**
   * The singleton instance of the factory.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  PreferencesFactory eINSTANCE = org.eclipse.emf.cdo.releng.preferences.impl.PreferencesFactoryImpl.init();

  /**
   * Returns a new object of class '<em>Preference Node</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Preference Node</em>'.
   * @generated
   */
  PreferenceNode createPreferenceNode();

  /**
   * Returns a new object of class '<em>Property</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Property</em>'.
   * @generated
   */
  Property createProperty();

  /**
   * Returns the package supported by this factory.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the package supported by this factory.
   * @generated
   */
  PreferencesPackage getPreferencesPackage();

} //PreferencesFactory
