/**
 */
package org.eclipse.emf.cdo.evolution;

import org.eclipse.emf.cdo.CDOObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Migration</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.evolution.Migration#getModelSet <em>Model Set</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.evolution.Migration#getDiagnosticID <em>Diagnostic ID</em>}</li>
 * </ul>
 *
 * @see org.eclipse.emf.cdo.evolution.EvolutionPackage#getMigration()
 * @model abstract="true"
 *        annotation="http://www.eclipse.org/emf/2002/Ecore constraints='NotObsolete'"
 * @extends CDOObject
 * @generated
 */
public interface Migration extends CDOObject
{
  /**
   * Returns the value of the '<em><b>Model Set</b></em>' container reference.
   * It is bidirectional and its opposite is '{@link org.eclipse.emf.cdo.evolution.ModelSet#getMigrations <em>Migrations</em>}'.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Model Set</em>' container reference isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Model Set</em>' container reference.
   * @see #setModelSet(ModelSet)
   * @see org.eclipse.emf.cdo.evolution.EvolutionPackage#getMigration_ModelSet()
   * @see org.eclipse.emf.cdo.evolution.ModelSet#getMigrations
   * @model opposite="migrations" transient="false"
   * @generated
   */
  ModelSet getModelSet();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.evolution.Migration#getModelSet <em>Model Set</em>}' container reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Model Set</em>' container reference.
   * @see #getModelSet()
   * @generated
   */
  void setModelSet(ModelSet value);

  /**
   * Returns the value of the '<em><b>Diagnostic ID</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Diagnostic ID</em>' attribute isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Diagnostic ID</em>' attribute.
   * @see #setDiagnosticID(String)
   * @see org.eclipse.emf.cdo.evolution.EvolutionPackage#getMigration_DiagnosticID()
   * @model
   * @generated
   */
  String getDiagnosticID();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.evolution.Migration#getDiagnosticID <em>Diagnostic ID</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Diagnostic ID</em>' attribute.
   * @see #getDiagnosticID()
   * @generated
   */
  void setDiagnosticID(String value);

} // Migration
