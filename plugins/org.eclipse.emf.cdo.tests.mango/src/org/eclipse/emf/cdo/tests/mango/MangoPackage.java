/**
 * <copyright>
 * </copyright>
 *
 * $Id: MangoPackage.java,v 1.3 2008-04-27 08:57:26 estepper Exp $
 */
package org.eclipse.emf.cdo.tests.mango;

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
 * @see org.eclipse.emf.cdo.tests.mango.MangoFactory
 * @model kind="package"
 * @generated
 */
public interface MangoPackage extends EPackage
{
  /**
   * The package name. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  String eNAME = "mango";

  /**
   * The package namespace URI. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  String eNS_URI = "http://www.eclipse.org/emf/CDO/tests/mango";

  /**
   * The package namespace name. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  String eNS_PREFIX = "mango";

  /**
   * The singleton instance of the package. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  MangoPackage eINSTANCE = org.eclipse.emf.cdo.tests.mango.impl.MangoPackageImpl.init();

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.tests.mango.impl.ValueListImpl <em>Value List</em>}'
   * class. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @see org.eclipse.emf.cdo.tests.mango.impl.ValueListImpl
   * @see org.eclipse.emf.cdo.tests.mango.impl.MangoPackageImpl#getValueList()
   * @generated
   */
  int VALUE_LIST = 0;

  /**
   * The feature id for the '<em><b>Name</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int VALUE_LIST__NAME = 0;

  /**
   * The feature id for the '<em><b>Values</b></em>' reference list. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int VALUE_LIST__VALUES = 1;

  /**
   * The number of structural features of the '<em>Value List</em>' class. <!-- begin-user-doc --> <!-- end-user-doc
   * -->
   * 
   * @generated
   * @ordered
   */
  int VALUE_LIST_FEATURE_COUNT = 2;

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.tests.mango.impl.ValueImpl <em>Value</em>}' class. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   * 
   * @see org.eclipse.emf.cdo.tests.mango.impl.ValueImpl
   * @see org.eclipse.emf.cdo.tests.mango.impl.MangoPackageImpl#getValue()
   * @generated
   */
  int VALUE = 1;

  /**
   * The feature id for the '<em><b>Name</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int VALUE__NAME = 0;

  /**
   * The number of structural features of the '<em>Value</em>' class. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int VALUE_FEATURE_COUNT = 1;

  /**
   * Returns the meta object for class '{@link org.eclipse.emf.cdo.tests.mango.ValueList <em>Value List</em>}'. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the meta object for class '<em>Value List</em>'.
   * @see org.eclipse.emf.cdo.tests.mango.ValueList
   * @generated
   */
  EClass getValueList();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.tests.mango.ValueList#getName <em>Name</em>}'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the meta object for the attribute '<em>Name</em>'.
   * @see org.eclipse.emf.cdo.tests.mango.ValueList#getName()
   * @see #getValueList()
   * @generated
   */
  EAttribute getValueList_Name();

  /**
   * Returns the meta object for the reference list '{@link org.eclipse.emf.cdo.tests.mango.ValueList#getValues <em>Values</em>}'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the meta object for the reference list '<em>Values</em>'.
   * @see org.eclipse.emf.cdo.tests.mango.ValueList#getValues()
   * @see #getValueList()
   * @generated
   */
  EReference getValueList_Values();

  /**
   * Returns the meta object for class '{@link org.eclipse.emf.cdo.tests.mango.Value <em>Value</em>}'. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the meta object for class '<em>Value</em>'.
   * @see org.eclipse.emf.cdo.tests.mango.Value
   * @generated
   */
  EClass getValue();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.tests.mango.Value#getName <em>Name</em>}'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the meta object for the attribute '<em>Name</em>'.
   * @see org.eclipse.emf.cdo.tests.mango.Value#getName()
   * @see #getValue()
   * @generated
   */
  EAttribute getValue_Name();

  /**
   * Returns the factory that creates the instances of the model. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the factory that creates the instances of the model.
   * @generated
   */
  MangoFactory getMangoFactory();

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
     * The meta object literal for the '{@link org.eclipse.emf.cdo.tests.mango.impl.ValueListImpl <em>Value List</em>}'
     * class. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @see org.eclipse.emf.cdo.tests.mango.impl.ValueListImpl
     * @see org.eclipse.emf.cdo.tests.mango.impl.MangoPackageImpl#getValueList()
     * @generated
     */
    EClass VALUE_LIST = eINSTANCE.getValueList();

    /**
     * The meta object literal for the '<em><b>Name</b></em>' attribute feature. <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * 
     * @generated
     */
    EAttribute VALUE_LIST__NAME = eINSTANCE.getValueList_Name();

    /**
     * The meta object literal for the '<em><b>Values</b></em>' reference list feature. <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * 
     * @generated
     */
    EReference VALUE_LIST__VALUES = eINSTANCE.getValueList_Values();

    /**
     * The meta object literal for the '{@link org.eclipse.emf.cdo.tests.mango.impl.ValueImpl <em>Value</em>}' class.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @see org.eclipse.emf.cdo.tests.mango.impl.ValueImpl
     * @see org.eclipse.emf.cdo.tests.mango.impl.MangoPackageImpl#getValue()
     * @generated
     */
    EClass VALUE = eINSTANCE.getValue();

    /**
     * The meta object literal for the '<em><b>Name</b></em>' attribute feature. <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * 
     * @generated
     */
    EAttribute VALUE__NAME = eINSTANCE.getValue_Name();

  }

} // MangoPackage
