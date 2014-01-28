/**
 */
package org.eclipse.emf.cdo.releng.setup;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Targlet Data</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.releng.setup.TargletData#getName <em>Name</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.releng.setup.TargletData#getRoots <em>Roots</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.releng.setup.TargletData#getSourceLocators <em>Source Locators</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.releng.setup.TargletData#getRepositoryLists <em>Repository Lists</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.releng.setup.TargletData#getActiveRepositoryList <em>Active Repository List</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.releng.setup.TargletData#getActiveP2Repositories <em>Active P2 Repositories</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.releng.setup.TargletData#isIncludeSources <em>Include Sources</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.releng.setup.TargletData#isIncludeAllPlatforms <em>Include All Platforms</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.emf.cdo.releng.setup.SetupPackage#getTargletData()
 * @model abstract="true"
 * @generated
 */
public interface TargletData extends EObject
{
  /**
   * Returns the value of the '<em><b>Name</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Name</em>' attribute isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Name</em>' attribute.
   * @see #setName(String)
   * @see org.eclipse.emf.cdo.releng.setup.SetupPackage#getTargletData_Name()
   * @model required="true"
   * @generated
   */
  String getName();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.releng.setup.TargletData#getName <em>Name</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Name</em>' attribute.
   * @see #getName()
   * @generated
   */
  void setName(String value);

  /**
   * Returns the value of the '<em><b>Roots</b></em>' containment reference list.
   * The list contents are of type {@link org.eclipse.emf.cdo.releng.setup.InstallableUnit}.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Roots</em>' containment reference list isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Roots</em>' containment reference list.
   * @see org.eclipse.emf.cdo.releng.setup.SetupPackage#getTargletData_Roots()
   * @model containment="true" resolveProxies="true" required="true"
   * @generated
   */
  EList<InstallableUnit> getRoots();

  /**
   * Returns the value of the '<em><b>Source Locators</b></em>' containment reference list.
   * The list contents are of type {@link org.eclipse.emf.cdo.releng.setup.AutomaticSourceLocator}.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Source Locators</em>' containment reference list isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Source Locators</em>' containment reference list.
   * @see org.eclipse.emf.cdo.releng.setup.SetupPackage#getTargletData_SourceLocators()
   * @model containment="true" resolveProxies="true"
   * @generated
   */
  EList<AutomaticSourceLocator> getSourceLocators();

  /**
   * Returns the value of the '<em><b>Repository Lists</b></em>' containment reference list.
   * The list contents are of type {@link org.eclipse.emf.cdo.releng.setup.RepositoryList}.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Repository Lists</em>' containment reference list isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Repository Lists</em>' containment reference list.
   * @see org.eclipse.emf.cdo.releng.setup.SetupPackage#getTargletData_RepositoryLists()
   * @model containment="true" resolveProxies="true"
   * @generated
   */
  EList<RepositoryList> getRepositoryLists();

  /**
   * Returns the value of the '<em><b>Active Repository List</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Active Repository List</em>' attribute isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Active Repository List</em>' attribute.
   * @see #setActiveRepositoryList(String)
   * @see org.eclipse.emf.cdo.releng.setup.SetupPackage#getTargletData_ActiveRepositoryList()
   * @model
   * @generated
   */
  String getActiveRepositoryList();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.releng.setup.TargletData#getActiveRepositoryList <em>Active Repository List</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Active Repository List</em>' attribute.
   * @see #getActiveRepositoryList()
   * @generated
   */
  void setActiveRepositoryList(String value);

  /**
   * Returns the value of the '<em><b>Active P2 Repositories</b></em>' reference list.
   * The list contents are of type {@link org.eclipse.emf.cdo.releng.setup.P2Repository}.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Active P2 Repositories</em>' reference list isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Active P2 Repositories</em>' reference list.
   * @see org.eclipse.emf.cdo.releng.setup.SetupPackage#getTargletData_ActiveP2Repositories()
   * @model resolveProxies="false" transient="true" changeable="false" volatile="true" derived="true"
   * @generated
   */
  EList<P2Repository> getActiveP2Repositories();

  /**
   * Returns the value of the '<em><b>Include Sources</b></em>' attribute.
   * The default value is <code>"true"</code>.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Include Sources</em>' attribute isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Include Sources</em>' attribute.
   * @see #setIncludeSources(boolean)
   * @see org.eclipse.emf.cdo.releng.setup.SetupPackage#getTargletData_IncludeSources()
   * @model default="true"
   * @generated
   */
  boolean isIncludeSources();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.releng.setup.TargletData#isIncludeSources <em>Include Sources</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Include Sources</em>' attribute.
   * @see #isIncludeSources()
   * @generated
   */
  void setIncludeSources(boolean value);

  /**
   * Returns the value of the '<em><b>Include All Platforms</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Include All Platforms</em>' attribute isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Include All Platforms</em>' attribute.
   * @see #setIncludeAllPlatforms(boolean)
   * @see org.eclipse.emf.cdo.releng.setup.SetupPackage#getTargletData_IncludeAllPlatforms()
   * @model
   * @generated
   */
  boolean isIncludeAllPlatforms();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.releng.setup.TargletData#isIncludeAllPlatforms <em>Include All Platforms</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Include All Platforms</em>' attribute.
   * @see #isIncludeAllPlatforms()
   * @generated
   */
  void setIncludeAllPlatforms(boolean value);

} // TargletData
