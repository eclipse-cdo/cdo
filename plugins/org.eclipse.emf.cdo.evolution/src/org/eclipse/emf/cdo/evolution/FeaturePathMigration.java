/**
 */
package org.eclipse.emf.cdo.evolution;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EReference;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Feature Path Migration</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.evolution.FeaturePathMigration#getFromClass <em>From Class</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.evolution.FeaturePathMigration#getToClass <em>To Class</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.evolution.FeaturePathMigration#getFeaturePath <em>Feature Path</em>}</li>
 * </ul>
 *
 * @see org.eclipse.emf.cdo.evolution.EvolutionPackage#getFeaturePathMigration()
 * @model annotation="http://www.eclipse.org/emf/2002/Ecore constraints='NewFeatureReachable'"
 * @generated
 */
public interface FeaturePathMigration extends Migration
{
  /**
   * Returns the value of the '<em><b>From Class</b></em>' reference.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>From Class</em>' reference isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>From Class</em>' reference.
   * @see #setFromClass(EClass)
   * @see org.eclipse.emf.cdo.evolution.EvolutionPackage#getFeaturePathMigration_FromClass()
   * @model required="true"
   * @generated
   */
  EClass getFromClass();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.evolution.FeaturePathMigration#getFromClass <em>From Class</em>}' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>From Class</em>' reference.
   * @see #getFromClass()
   * @generated
   */
  void setFromClass(EClass value);

  /**
   * Returns the value of the '<em><b>To Class</b></em>' reference.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>To Class</em>' reference isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>To Class</em>' reference.
   * @see #setToClass(EClass)
   * @see org.eclipse.emf.cdo.evolution.EvolutionPackage#getFeaturePathMigration_ToClass()
   * @model required="true"
   * @generated
   */
  EClass getToClass();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.evolution.FeaturePathMigration#getToClass <em>To Class</em>}' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>To Class</em>' reference.
   * @see #getToClass()
   * @generated
   */
  void setToClass(EClass value);

  /**
   * Returns the value of the '<em><b>Feature Path</b></em>' reference list.
   * The list contents are of type {@link org.eclipse.emf.ecore.EReference}.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Feature Path</em>' reference list isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Feature Path</em>' reference list.
   * @see org.eclipse.emf.cdo.evolution.EvolutionPackage#getFeaturePathMigration_FeaturePath()
   * @model
   * @generated
   */
  EList<EReference> getFeaturePath();

} // FeaturePathMigration
