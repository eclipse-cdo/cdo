/**
 */
package org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Bz380987 Person</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.Bz380987_Person#getName <em>Name</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.Bz380987_Person#getGroup <em>Group</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.Bz380987_Person#getPlaces <em>Places</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.HibernateTestPackage#getBz380987_Person()
 * @model
 * @generated
 */
public interface Bz380987_Person extends EObject {
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
	 * @see org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.HibernateTestPackage#getBz380987_Person_Name()
	 * @model
	 * @generated
	 */
	String getName();

	/**
	 * Sets the value of the '{@link org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.Bz380987_Person#getName <em>Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Name</em>' attribute.
	 * @see #getName()
	 * @generated
	 */
	void setName(String value);

	/**
	 * Returns the value of the '<em><b>Group</b></em>' reference list.
	 * The list contents are of type {@link org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.Bz380987_Group}.
	 * It is bidirectional and its opposite is '{@link org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.Bz380987_Group#getPeople <em>People</em>}'.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Group</em>' reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Group</em>' reference list.
	 * @see org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.HibernateTestPackage#getBz380987_Person_Group()
	 * @see org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.Bz380987_Group#getPeople
	 * @model opposite="people"
	 * @generated
	 */
	EList<Bz380987_Group> getGroup();

	/**
	 * Returns the value of the '<em><b>Places</b></em>' reference list.
	 * The list contents are of type {@link org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.Bz380987_Place}.
	 * It is bidirectional and its opposite is '{@link org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.Bz380987_Place#getPeople <em>People</em>}'.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Places</em>' reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Places</em>' reference list.
	 * @see org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.HibernateTestPackage#getBz380987_Person_Places()
	 * @see org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.Bz380987_Place#getPeople
	 * @model opposite="people"
	 * @generated
	 */
	EList<Bz380987_Place> getPlaces();

} // Bz380987_Person
