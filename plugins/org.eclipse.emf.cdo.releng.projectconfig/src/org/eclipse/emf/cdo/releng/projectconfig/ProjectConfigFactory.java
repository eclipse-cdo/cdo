/**
 */
package org.eclipse.emf.cdo.releng.projectconfig;

import org.eclipse.emf.ecore.EFactory;

/**
 * <!-- begin-user-doc -->
 * The <b>Factory</b> for the model.
 * It provides a create method for each non-abstract class of the model.
 * <!-- end-user-doc -->
 * @see org.eclipse.emf.cdo.releng.projectconfig.ProjectConfigPackage
 * @generated
 */
public interface ProjectConfigFactory extends EFactory
{
  /**
   * The singleton instance of the factory.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  ProjectConfigFactory eINSTANCE = org.eclipse.emf.cdo.releng.projectconfig.impl.ProjectConfigFactoryImpl.init();

  /**
   * Returns a new object of class '<em>Workspace Configuration</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Workspace Configuration</em>'.
   * @generated
   */
  WorkspaceConfiguration createWorkspaceConfiguration();

  /**
   * Returns a new object of class '<em>Project</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Project</em>'.
   * @generated
   */
  Project createProject();

  /**
   * Returns a new object of class '<em>Preference Profile</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Preference Profile</em>'.
   * @generated
   */
  PreferenceProfile createPreferenceProfile();

  /**
   * Returns a new object of class '<em>Preference Filter</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Preference Filter</em>'.
   * @generated
   */
  PreferenceFilter createPreferenceFilter();

  /**
   * Returns the package supported by this factory.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the package supported by this factory.
   * @generated
   */
  ProjectConfigPackage getProjectConfigPackage();

} //ProjectConfigFactory
