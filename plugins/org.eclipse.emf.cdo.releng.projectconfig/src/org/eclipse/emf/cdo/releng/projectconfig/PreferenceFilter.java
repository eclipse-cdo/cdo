/**
 */
package org.eclipse.emf.cdo.releng.projectconfig;

import java.util.regex.Pattern;
import org.eclipse.emf.cdo.releng.preferences.PreferenceNode;
import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Preference Filter</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.releng.projectconfig.PreferenceFilter#getPreferenceNode <em>Preference Node</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.releng.projectconfig.PreferenceFilter#getPreferenceProfile <em>Preference Profile</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.releng.projectconfig.PreferenceFilter#getInclusions <em>Inclusions</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.releng.projectconfig.PreferenceFilter#getExclusions <em>Exclusions</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.emf.cdo.releng.projectconfig.ProjectConfigPackage#getPreferenceFilter()
 * @model
 * @generated
 */
public interface PreferenceFilter extends EObject
{
  /**
   * Returns the value of the '<em><b>Preference Node</b></em>' reference.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Preference Node</em>' reference isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Preference Node</em>' reference.
   * @see #setPreferenceNode(PreferenceNode)
   * @see org.eclipse.emf.cdo.releng.projectconfig.ProjectConfigPackage#getPreferenceFilter_PreferenceNode()
   * @model required="true"
   * @generated
   */
  PreferenceNode getPreferenceNode();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.releng.projectconfig.PreferenceFilter#getPreferenceNode <em>Preference Node</em>}' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Preference Node</em>' reference.
   * @see #getPreferenceNode()
   * @generated
   */
  void setPreferenceNode(PreferenceNode value);

  /**
   * Returns the value of the '<em><b>Preference Profile</b></em>' container reference.
   * It is bidirectional and its opposite is '{@link org.eclipse.emf.cdo.releng.projectconfig.PreferenceProfile#getPreferenceFilters <em>Preference Filters</em>}'.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Preference Profile</em>' container reference isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Preference Profile</em>' container reference.
   * @see #setPreferenceProfile(PreferenceProfile)
   * @see org.eclipse.emf.cdo.releng.projectconfig.ProjectConfigPackage#getPreferenceFilter_PreferenceProfile()
   * @see org.eclipse.emf.cdo.releng.projectconfig.PreferenceProfile#getPreferenceFilters
   * @model opposite="preferenceFilters" required="true" transient="false"
   * @generated
   */
  PreferenceProfile getPreferenceProfile();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.releng.projectconfig.PreferenceFilter#getPreferenceProfile <em>Preference Profile</em>}' container reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Preference Profile</em>' container reference.
   * @see #getPreferenceProfile()
   * @generated
   */
  void setPreferenceProfile(PreferenceProfile value);

  /**
   * Returns the value of the '<em><b>Inclusions</b></em>' attribute.
   * The default value is <code>".*"</code>.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Inclusions</em>' attribute isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Inclusions</em>' attribute.
   * @see #setInclusions(Pattern)
   * @see org.eclipse.emf.cdo.releng.projectconfig.ProjectConfigPackage#getPreferenceFilter_Inclusions()
   * @model default=".*" dataType="org.eclipse.emf.cdo.releng.projectconfig.Pattern" required="true"
   * @generated
   */
  Pattern getInclusions();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.releng.projectconfig.PreferenceFilter#getInclusions <em>Inclusions</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Inclusions</em>' attribute.
   * @see #getInclusions()
   * @generated
   */
  void setInclusions(Pattern value);

  /**
   * Returns the value of the '<em><b>Exclusions</b></em>' attribute.
   * The default value is <code>""</code>.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Exclusions</em>' attribute isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Exclusions</em>' attribute.
   * @see #setExclusions(Pattern)
   * @see org.eclipse.emf.cdo.releng.projectconfig.ProjectConfigPackage#getPreferenceFilter_Exclusions()
   * @model default="" dataType="org.eclipse.emf.cdo.releng.projectconfig.Pattern" required="true"
   * @generated
   */
  Pattern getExclusions();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.releng.projectconfig.PreferenceFilter#getExclusions <em>Exclusions</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Exclusions</em>' attribute.
   * @see #getExclusions()
   * @generated
   */
  void setExclusions(Pattern value);

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @model
   * @generated
   */
  boolean matches(String value);

} // PreferenceFilter
