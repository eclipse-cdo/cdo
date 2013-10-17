/**
 */
package org.eclipse.emf.cdo.releng.preferences;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Property</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.releng.preferences.Property#getName <em>Name</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.releng.preferences.Property#getParent <em>Parent</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.releng.preferences.Property#getValue <em>Value</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.emf.cdo.releng.preferences.PreferencesPackage#getProperty()
 * @model
 * @generated
 */
public interface Property extends EObject
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
   * @see org.eclipse.emf.cdo.releng.preferences.PreferencesPackage#getProperty_Name()
   * @model required="true"
   * @generated
   */
  String getName();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.releng.preferences.Property#getName <em>Name</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Name</em>' attribute.
   * @see #getName()
   * @generated
   */
  void setName(String value);

  /**
   * Returns the value of the '<em><b>Parent</b></em>' container reference.
   * It is bidirectional and its opposite is '{@link org.eclipse.emf.cdo.releng.preferences.PreferenceNode#getProperties <em>Properties</em>}'.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Parent</em>' container reference isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Parent</em>' container reference.
   * @see #setParent(PreferenceNode)
   * @see org.eclipse.emf.cdo.releng.preferences.PreferencesPackage#getProperty_Parent()
   * @see org.eclipse.emf.cdo.releng.preferences.PreferenceNode#getProperties
   * @model opposite="properties" transient="false"
   * @generated
   */
  PreferenceNode getParent();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.releng.preferences.Property#getParent <em>Parent</em>}' container reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Parent</em>' container reference.
   * @see #getParent()
   * @generated
   */
  void setParent(PreferenceNode value);

  /**
   * Returns the value of the '<em><b>Value</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Value</em>' attribute isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Value</em>' attribute.
   * @see #setValue(String)
   * @see org.eclipse.emf.cdo.releng.preferences.PreferencesPackage#getProperty_Value()
   * @model dataType="org.eclipse.emf.cdo.releng.preferences.EscapedString"
   * @generated
   */
  String getValue();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.releng.preferences.Property#getValue <em>Value</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Value</em>' attribute.
   * @see #getValue()
   * @generated
   */
  void setValue(String value);

} // Property
