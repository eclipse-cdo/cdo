/**
 */
package org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.util;

import org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.Bz356181_Main;
import org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.Bz356181_NonTransient;
import org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.Bz356181_Transient;
import org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.Bz380987_Group;
import org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.Bz380987_Person;
import org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.Bz380987_Place;
import org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.Bz387752_Main;
import org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.HibernateTestPackage;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.util.Switch;

/**
 * <!-- begin-user-doc -->
 * The <b>Switch</b> for the model's inheritance hierarchy.
 * It supports the call {@link #doSwitch(EObject) doSwitch(object)}
 * to invoke the <code>caseXXX</code> method for each class of the model,
 * starting with the actual class of the object
 * and proceeding up the inheritance hierarchy
 * until a non-null result is returned,
 * which is the result of the switch.
 * <!-- end-user-doc -->
 * @see org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.HibernateTestPackage
 * @generated
 */
public class HibernateTestSwitch<T> extends Switch<T> {
	/**
	 * The cached model package
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected static HibernateTestPackage modelPackage;

	/**
	 * Creates an instance of the switch.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public HibernateTestSwitch() {
		if (modelPackage == null) {
			modelPackage = HibernateTestPackage.eINSTANCE;
		}
	}

	/**
	 * Checks whether this is a switch for the given package.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @parameter ePackage the package in question.
	 * @return whether this is a switch for the given package.
	 * @generated
	 */
	@Override
	protected boolean isSwitchFor(EPackage ePackage) {
		return ePackage == modelPackage;
	}

	/**
	 * Calls <code>caseXXX</code> for each class of the model until one returns a non null result; it yields that result.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the first non-null result returned by a <code>caseXXX</code> call.
	 * @generated
	 */
	@Override
	protected T doSwitch(int classifierID, EObject theEObject) {
		switch (classifierID) {
			case HibernateTestPackage.BZ356181_MAIN: {
				Bz356181_Main bz356181_Main = (Bz356181_Main)theEObject;
				T result = caseBz356181_Main(bz356181_Main);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case HibernateTestPackage.BZ356181_TRANSIENT: {
				Bz356181_Transient bz356181_Transient = (Bz356181_Transient)theEObject;
				T result = caseBz356181_Transient(bz356181_Transient);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case HibernateTestPackage.BZ356181_NON_TRANSIENT: {
				Bz356181_NonTransient bz356181_NonTransient = (Bz356181_NonTransient)theEObject;
				T result = caseBz356181_NonTransient(bz356181_NonTransient);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case HibernateTestPackage.BZ387752_MAIN: {
				Bz387752_Main bz387752_Main = (Bz387752_Main)theEObject;
				T result = caseBz387752_Main(bz387752_Main);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case HibernateTestPackage.BZ380987_GROUP: {
				Bz380987_Group bz380987_Group = (Bz380987_Group)theEObject;
				T result = caseBz380987_Group(bz380987_Group);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case HibernateTestPackage.BZ380987_PLACE: {
				Bz380987_Place bz380987_Place = (Bz380987_Place)theEObject;
				T result = caseBz380987_Place(bz380987_Place);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case HibernateTestPackage.BZ380987_PERSON: {
				Bz380987_Person bz380987_Person = (Bz380987_Person)theEObject;
				T result = caseBz380987_Person(bz380987_Person);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			default: return defaultCase(theEObject);
		}
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Bz356181 Main</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Bz356181 Main</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseBz356181_Main(Bz356181_Main object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Bz356181 Transient</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Bz356181 Transient</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseBz356181_Transient(Bz356181_Transient object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Bz356181 Non Transient</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Bz356181 Non Transient</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseBz356181_NonTransient(Bz356181_NonTransient object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Bz387752 Main</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Bz387752 Main</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseBz387752_Main(Bz387752_Main object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Bz380987 Group</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Bz380987 Group</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseBz380987_Group(Bz380987_Group object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Bz380987 Place</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Bz380987 Place</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseBz380987_Place(Bz380987_Place object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Bz380987 Person</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Bz380987 Person</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseBz380987_Person(Bz380987_Person object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>EObject</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch, but this is the last case anyway.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>EObject</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject)
	 * @generated
	 */
	@Override
	public T defaultCase(EObject object) {
		return null;
	}

} //HibernateTestSwitch
