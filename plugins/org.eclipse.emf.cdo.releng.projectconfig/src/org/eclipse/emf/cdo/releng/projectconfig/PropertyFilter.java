/**
 */
package org.eclipse.emf.cdo.releng.projectconfig;

import org.eclipse.emf.cdo.releng.predicates.Predicate;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;

import java.util.regex.Pattern;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Property Filter</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.releng.projectconfig.PropertyFilter#getOmissions <em>Omissions</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.releng.projectconfig.PropertyFilter#getPredicates <em>Predicates</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.emf.cdo.releng.projectconfig.ProjectConfigPackage#getPropertyFilter()
 * @model
 * @generated
 */
public interface PropertyFilter extends EObject
{
  /**
   * Returns the value of the '<em><b>Omissions</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Omissions</em>' attribute isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Omissions</em>' attribute.
   * @see #setOmissions(Pattern)
   * @see org.eclipse.emf.cdo.releng.projectconfig.ProjectConfigPackage#getPropertyFilter_Omissions()
   * @model dataType="org.eclipse.emf.cdo.releng.projectconfig.Pattern" required="true"
   * @generated
   */
  Pattern getOmissions();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.releng.projectconfig.PropertyFilter#getOmissions <em>Omissions</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Omissions</em>' attribute.
   * @see #getOmissions()
   * @generated
   */
  void setOmissions(Pattern value);

  /**
   * Returns the value of the '<em><b>Predicates</b></em>' containment reference list.
   * The list contents are of type {@link org.eclipse.emf.cdo.releng.predicates.Predicate}.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Predicates</em>' containment reference list isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Predicates</em>' containment reference list.
   * @see org.eclipse.emf.cdo.releng.projectconfig.ProjectConfigPackage#getPropertyFilter_Predicates()
   * @model containment="true"
   * @generated
   */
  EList<Predicate> getPredicates();

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @model
   * @generated
   */
  boolean matches(String value);

} // PropertyFilter
