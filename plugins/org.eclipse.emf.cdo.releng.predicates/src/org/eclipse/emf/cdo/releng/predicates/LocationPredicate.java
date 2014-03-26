/**
 */
package org.eclipse.emf.cdo.releng.predicates;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Location Predicate</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.releng.predicates.LocationPredicate#getPattern <em>Pattern</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.emf.cdo.releng.predicates.PredicatesPackage#getLocationPredicate()
 * @model
 * @generated
 */
public interface LocationPredicate extends Predicate
{
  /**
   * Returns the value of the '<em><b>Pattern</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Pattern</em>' attribute isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Pattern</em>' attribute.
   * @see #setPattern(String)
   * @see org.eclipse.emf.cdo.releng.predicates.PredicatesPackage#getLocationPredicate_Pattern()
   * @model required="true"
   * @generated
   */
  String getPattern();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.releng.predicates.LocationPredicate#getPattern <em>Pattern</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Pattern</em>' attribute.
   * @see #getPattern()
   * @generated
   */
  void setPattern(String value);

} // LocationPredicate
