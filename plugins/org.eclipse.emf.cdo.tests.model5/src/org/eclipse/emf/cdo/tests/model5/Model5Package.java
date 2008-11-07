/**
 * <copyright>
 * </copyright>
 *
 * $Id: Model5Package.java,v 1.1 2008-11-07 02:50:07 smcduff Exp $
 */
package org.eclipse.emf.cdo.tests.model5;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;

/**
 * <!-- begin-user-doc --> The <b>Package</b> for the model. It contains accessors for the meta objects to represent
 * <ul>
 * <li>each class,</li>
 * <li>each feature of each class,</li>
 * <li>each enum,</li>
 * <li>and each data type</li>
 * </ul>
 * <!-- end-user-doc -->
 * 
 * @see org.eclipse.emf.cdo.tests.model5.Model5Factory
 * @model kind="package"
 * @generated
 */
public interface Model5Package extends EPackage
{
  /**
   * The package name. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  String eNAME = "model5";

  /**
   * The package namespace URI. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  String eNS_URI = "http://www.eclipse.org/emf/CDO/tests/model5/1.0.0";

  /**
   * The package namespace name. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  String eNS_PREFIX = "model5";

  /**
   * The singleton instance of the package. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  Model5Package eINSTANCE = org.eclipse.emf.cdo.tests.model5.impl.Model5PackageImpl.init();

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.tests.model5.impl.TestFeatureMapImpl
   * <em>Test Feature Map</em>}' class. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @see org.eclipse.emf.cdo.tests.model5.impl.TestFeatureMapImpl
   * @see org.eclipse.emf.cdo.tests.model5.impl.Model5PackageImpl#getTestFeatureMap()
   * @generated
   */
  int TEST_FEATURE_MAP = 0;

  /**
   * The feature id for the '<em><b>Managers</b></em>' reference list. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int TEST_FEATURE_MAP__MANAGERS = 0;

  /**
   * The feature id for the '<em><b>Doctors</b></em>' containment reference list. <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int TEST_FEATURE_MAP__DOCTORS = 1;

  /**
   * The feature id for the '<em><b>People</b></em>' attribute list. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int TEST_FEATURE_MAP__PEOPLE = 2;

  /**
   * The number of structural features of the '<em>Test Feature Map</em>' class. <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int TEST_FEATURE_MAP_FEATURE_COUNT = 3;

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.tests.model5.impl.ManagerImpl <em>Manager</em>}' class. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   * 
   * @see org.eclipse.emf.cdo.tests.model5.impl.ManagerImpl
   * @see org.eclipse.emf.cdo.tests.model5.impl.Model5PackageImpl#getManager()
   * @generated
   */
  int MANAGER = 1;

  /**
   * The number of structural features of the '<em>Manager</em>' class. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int MANAGER_FEATURE_COUNT = 0;

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.tests.model5.impl.DoctorImpl <em>Doctor</em>}' class. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   * 
   * @see org.eclipse.emf.cdo.tests.model5.impl.DoctorImpl
   * @see org.eclipse.emf.cdo.tests.model5.impl.Model5PackageImpl#getDoctor()
   * @generated
   */
  int DOCTOR = 2;

  /**
   * The number of structural features of the '<em>Doctor</em>' class. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int DOCTOR_FEATURE_COUNT = 0;

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.tests.model5.impl.GenListOfIntImpl <em>Gen List Of Int</em>}
   * ' class. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @see org.eclipse.emf.cdo.tests.model5.impl.GenListOfIntImpl
   * @see org.eclipse.emf.cdo.tests.model5.impl.Model5PackageImpl#getGenListOfInt()
   * @generated
   */
  int GEN_LIST_OF_INT = 3;

  /**
   * The feature id for the '<em><b>Elements</b></em>' attribute list. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int GEN_LIST_OF_INT__ELEMENTS = 0;

  /**
   * The number of structural features of the '<em>Gen List Of Int</em>' class. <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int GEN_LIST_OF_INT_FEATURE_COUNT = 1;

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.tests.model5.impl.GenListOfStringImpl
   * <em>Gen List Of String</em>}' class. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @see org.eclipse.emf.cdo.tests.model5.impl.GenListOfStringImpl
   * @see org.eclipse.emf.cdo.tests.model5.impl.Model5PackageImpl#getGenListOfString()
   * @generated
   */
  int GEN_LIST_OF_STRING = 4;

  /**
   * The feature id for the '<em><b>Elements</b></em>' attribute list. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int GEN_LIST_OF_STRING__ELEMENTS = 0;

  /**
   * The number of structural features of the '<em>Gen List Of String</em>' class. <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int GEN_LIST_OF_STRING_FEATURE_COUNT = 1;

  /**
   * Returns the meta object for class '{@link org.eclipse.emf.cdo.tests.model5.TestFeatureMap
   * <em>Test Feature Map</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the meta object for class '<em>Test Feature Map</em>'.
   * @see org.eclipse.emf.cdo.tests.model5.TestFeatureMap
   * @generated
   */
  EClass getTestFeatureMap();

  /**
   * Returns the meta object for the reference list '{@link org.eclipse.emf.cdo.tests.model5.TestFeatureMap#getManagers
   * <em>Managers</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the meta object for the reference list '<em>Managers</em>'.
   * @see org.eclipse.emf.cdo.tests.model5.TestFeatureMap#getManagers()
   * @see #getTestFeatureMap()
   * @generated
   */
  EReference getTestFeatureMap_Managers();

  /**
   * Returns the meta object for the containment reference list '
   * {@link org.eclipse.emf.cdo.tests.model5.TestFeatureMap#getDoctors <em>Doctors</em>}'. <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * 
   * @return the meta object for the containment reference list '<em>Doctors</em>'.
   * @see org.eclipse.emf.cdo.tests.model5.TestFeatureMap#getDoctors()
   * @see #getTestFeatureMap()
   * @generated
   */
  EReference getTestFeatureMap_Doctors();

  /**
   * Returns the meta object for the attribute list '{@link org.eclipse.emf.cdo.tests.model5.TestFeatureMap#getPeople
   * <em>People</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the meta object for the attribute list '<em>People</em>'.
   * @see org.eclipse.emf.cdo.tests.model5.TestFeatureMap#getPeople()
   * @see #getTestFeatureMap()
   * @generated
   */
  EAttribute getTestFeatureMap_People();

  /**
   * Returns the meta object for class '{@link org.eclipse.emf.cdo.tests.model5.Manager <em>Manager</em>}'. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the meta object for class '<em>Manager</em>'.
   * @see org.eclipse.emf.cdo.tests.model5.Manager
   * @generated
   */
  EClass getManager();

  /**
   * Returns the meta object for class '{@link org.eclipse.emf.cdo.tests.model5.Doctor <em>Doctor</em>}'. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the meta object for class '<em>Doctor</em>'.
   * @see org.eclipse.emf.cdo.tests.model5.Doctor
   * @generated
   */
  EClass getDoctor();

  /**
   * Returns the meta object for class '{@link org.eclipse.emf.cdo.tests.model5.GenListOfInt <em>Gen List Of Int</em>}'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the meta object for class '<em>Gen List Of Int</em>'.
   * @see org.eclipse.emf.cdo.tests.model5.GenListOfInt
   * @generated
   */
  EClass getGenListOfInt();

  /**
   * Returns the meta object for the attribute list '{@link org.eclipse.emf.cdo.tests.model5.GenListOfInt#getElements
   * <em>Elements</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the meta object for the attribute list '<em>Elements</em>'.
   * @see org.eclipse.emf.cdo.tests.model5.GenListOfInt#getElements()
   * @see #getGenListOfInt()
   * @generated
   */
  EAttribute getGenListOfInt_Elements();

  /**
   * Returns the meta object for class '{@link org.eclipse.emf.cdo.tests.model5.GenListOfString
   * <em>Gen List Of String</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the meta object for class '<em>Gen List Of String</em>'.
   * @see org.eclipse.emf.cdo.tests.model5.GenListOfString
   * @generated
   */
  EClass getGenListOfString();

  /**
   * Returns the meta object for the attribute list '
   * {@link org.eclipse.emf.cdo.tests.model5.GenListOfString#getElements <em>Elements</em>}'. <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * 
   * @return the meta object for the attribute list '<em>Elements</em>'.
   * @see org.eclipse.emf.cdo.tests.model5.GenListOfString#getElements()
   * @see #getGenListOfString()
   * @generated
   */
  EAttribute getGenListOfString_Elements();

  /**
   * Returns the factory that creates the instances of the model. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the factory that creates the instances of the model.
   * @generated
   */
  Model5Factory getModel5Factory();

  /**
   * <!-- begin-user-doc --> Defines literals for the meta objects that represent
   * <ul>
   * <li>each class,</li>
   * <li>each feature of each class,</li>
   * <li>each enum,</li>
   * <li>and each data type</li>
   * </ul>
   * <!-- end-user-doc -->
   * 
   * @generated
   */
  interface Literals
  {
    /**
     * The meta object literal for the '{@link org.eclipse.emf.cdo.tests.model5.impl.TestFeatureMapImpl
     * <em>Test Feature Map</em>}' class. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @see org.eclipse.emf.cdo.tests.model5.impl.TestFeatureMapImpl
     * @see org.eclipse.emf.cdo.tests.model5.impl.Model5PackageImpl#getTestFeatureMap()
     * @generated
     */
    EClass TEST_FEATURE_MAP = eINSTANCE.getTestFeatureMap();

    /**
     * The meta object literal for the '<em><b>Managers</b></em>' reference list feature. <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * 
     * @generated
     */
    EReference TEST_FEATURE_MAP__MANAGERS = eINSTANCE.getTestFeatureMap_Managers();

    /**
     * The meta object literal for the '<em><b>Doctors</b></em>' containment reference list feature. <!-- begin-user-doc
     * --> <!-- end-user-doc -->
     * 
     * @generated
     */
    EReference TEST_FEATURE_MAP__DOCTORS = eINSTANCE.getTestFeatureMap_Doctors();

    /**
     * The meta object literal for the '<em><b>People</b></em>' attribute list feature. <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * 
     * @generated
     */
    EAttribute TEST_FEATURE_MAP__PEOPLE = eINSTANCE.getTestFeatureMap_People();

    /**
     * The meta object literal for the '{@link org.eclipse.emf.cdo.tests.model5.impl.ManagerImpl <em>Manager</em>}'
     * class. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @see org.eclipse.emf.cdo.tests.model5.impl.ManagerImpl
     * @see org.eclipse.emf.cdo.tests.model5.impl.Model5PackageImpl#getManager()
     * @generated
     */
    EClass MANAGER = eINSTANCE.getManager();

    /**
     * The meta object literal for the '{@link org.eclipse.emf.cdo.tests.model5.impl.DoctorImpl <em>Doctor</em>}' class.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @see org.eclipse.emf.cdo.tests.model5.impl.DoctorImpl
     * @see org.eclipse.emf.cdo.tests.model5.impl.Model5PackageImpl#getDoctor()
     * @generated
     */
    EClass DOCTOR = eINSTANCE.getDoctor();

    /**
     * The meta object literal for the '{@link org.eclipse.emf.cdo.tests.model5.impl.GenListOfIntImpl
     * <em>Gen List Of Int</em>}' class. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @see org.eclipse.emf.cdo.tests.model5.impl.GenListOfIntImpl
     * @see org.eclipse.emf.cdo.tests.model5.impl.Model5PackageImpl#getGenListOfInt()
     * @generated
     */
    EClass GEN_LIST_OF_INT = eINSTANCE.getGenListOfInt();

    /**
     * The meta object literal for the '<em><b>Elements</b></em>' attribute list feature. <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * 
     * @generated
     */
    EAttribute GEN_LIST_OF_INT__ELEMENTS = eINSTANCE.getGenListOfInt_Elements();

    /**
     * The meta object literal for the '{@link org.eclipse.emf.cdo.tests.model5.impl.GenListOfStringImpl
     * <em>Gen List Of String</em>}' class. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @see org.eclipse.emf.cdo.tests.model5.impl.GenListOfStringImpl
     * @see org.eclipse.emf.cdo.tests.model5.impl.Model5PackageImpl#getGenListOfString()
     * @generated
     */
    EClass GEN_LIST_OF_STRING = eINSTANCE.getGenListOfString();

    /**
     * The meta object literal for the '<em><b>Elements</b></em>' attribute list feature. <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * 
     * @generated
     */
    EAttribute GEN_LIST_OF_STRING__ELEMENTS = eINSTANCE.getGenListOfString_Elements();

  }

} // Model5Package
