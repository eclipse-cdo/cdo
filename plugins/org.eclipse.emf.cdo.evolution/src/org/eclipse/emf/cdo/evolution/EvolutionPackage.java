/**
 */
package org.eclipse.emf.cdo.evolution;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EOperation;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;

/**
 * <!-- begin-user-doc -->
 * The <b>Package</b> for the model.
 * It contains accessors for the meta objects to represent
 * <ul>
 *   <li>each class,</li>
 *   <li>each feature of each class,</li>
 *   <li>each operation of each class,</li>
 *   <li>each enum,</li>
 *   <li>and each data type</li>
 * </ul>
 * <!-- end-user-doc -->
 * @see org.eclipse.emf.cdo.evolution.EvolutionFactory
 * @model kind="package"
 * @generated
 */
public interface EvolutionPackage extends EPackage
{
  /**
   * The package name.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  String eNAME = "evolution";

  /**
   * The package namespace URI.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  String eNS_URI = "http://www.eclipse.org/emf/CDO/evolution/1.0.0";

  /**
   * The package namespace name.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  String eNS_PREFIX = "evolution";

  /**
   * The singleton instance of the package.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  EvolutionPackage eINSTANCE = org.eclipse.emf.cdo.evolution.impl.EvolutionPackageImpl.init();

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.evolution.impl.ModelSetImpl <em>Model Set</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.emf.cdo.evolution.impl.ModelSetImpl
   * @see org.eclipse.emf.cdo.evolution.impl.EvolutionPackageImpl#getModelSet()
   * @generated
   */
  int MODEL_SET = 0;

  /**
   * The feature id for the '<em><b>Change</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int MODEL_SET__CHANGE = 0;

  /**
   * The feature id for the '<em><b>Migrations</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int MODEL_SET__MIGRATIONS = 1;

  /**
   * The number of structural features of the '<em>Model Set</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int MODEL_SET_FEATURE_COUNT = 2;

  /**
   * The operation id for the '<em>Get Evolution</em>' operation.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int MODEL_SET___GET_EVOLUTION = 0;

  /**
   * The operation id for the '<em>Get Version</em>' operation.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int MODEL_SET___GET_VERSION = 1;

  /**
   * The operation id for the '<em>Get Previous Release</em>' operation.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int MODEL_SET___GET_PREVIOUS_RELEASE = 2;

  /**
   * The operation id for the '<em>Get Root Packages</em>' operation.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int MODEL_SET___GET_ROOT_PACKAGES = 3;

  /**
   * The operation id for the '<em>Get All Packages</em>' operation.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int MODEL_SET___GET_ALL_PACKAGES = 4;

  /**
   * The operation id for the '<em>Contains Element</em>' operation.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int MODEL_SET___CONTAINS_ELEMENT__EMODELELEMENT = 5;

  /**
   * The operation id for the '<em>Get Element</em>' operation.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int MODEL_SET___GET_ELEMENT__STRING = 6;

  /**
   * The operation id for the '<em>Get Element ID</em>' operation.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int MODEL_SET___GET_ELEMENT_ID__EMODELELEMENT = 7;

  /**
   * The operation id for the '<em>Get Element ID</em>' operation.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int MODEL_SET___GET_ELEMENT_ID__EMODELELEMENT_BOOLEAN = 8;

  /**
   * The operation id for the '<em>Compare</em>' operation.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int MODEL_SET___COMPARE__MODELSET = 9;

  /**
   * The operation id for the '<em>Get Migration</em>' operation.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int MODEL_SET___GET_MIGRATION__STRING = 10;

  /**
   * The number of operations of the '<em>Model Set</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int MODEL_SET_OPERATION_COUNT = 11;

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.evolution.impl.ModelImpl <em>Model</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.emf.cdo.evolution.impl.ModelImpl
   * @see org.eclipse.emf.cdo.evolution.impl.EvolutionPackageImpl#getModel()
   * @generated
   */
  int MODEL = 1;

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.evolution.impl.EvolutionImpl <em>Evolution</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.emf.cdo.evolution.impl.EvolutionImpl
   * @see org.eclipse.emf.cdo.evolution.impl.EvolutionPackageImpl#getEvolution()
   * @generated
   */
  int EVOLUTION = 2;

  /**
   * The feature id for the '<em><b>Evolution</b></em>' container reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int MODEL__EVOLUTION = 0;

  /**
   * The feature id for the '<em><b>URI</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int MODEL__URI = 1;

  /**
   * The feature id for the '<em><b>Root Package</b></em>' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int MODEL__ROOT_PACKAGE = 2;

  /**
   * The feature id for the '<em><b>All Packages</b></em>' reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int MODEL__ALL_PACKAGES = 3;

  /**
   * The feature id for the '<em><b>Referenced Packages</b></em>' reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int MODEL__REFERENCED_PACKAGES = 4;

  /**
   * The feature id for the '<em><b>Missing Packages</b></em>' reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int MODEL__MISSING_PACKAGES = 5;

  /**
   * The number of structural features of the '<em>Model</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int MODEL_FEATURE_COUNT = 6;

  /**
   * The number of operations of the '<em>Model</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int MODEL_OPERATION_COUNT = 0;

  /**
   * The feature id for the '<em><b>Change</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int EVOLUTION__CHANGE = MODEL_SET__CHANGE;

  /**
   * The feature id for the '<em><b>Migrations</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int EVOLUTION__MIGRATIONS = MODEL_SET__MIGRATIONS;

  /**
   * The feature id for the '<em><b>Use Ecore Package</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int EVOLUTION__USE_ECORE_PACKAGE = MODEL_SET_FEATURE_COUNT + 0;

  /**
   * The feature id for the '<em><b>Use Eresource Package</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int EVOLUTION__USE_ERESOURCE_PACKAGE = MODEL_SET_FEATURE_COUNT + 1;

  /**
   * The feature id for the '<em><b>Use Etypes Package</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int EVOLUTION__USE_ETYPES_PACKAGE = MODEL_SET_FEATURE_COUNT + 2;

  /**
   * The feature id for the '<em><b>Unique Namespaces</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int EVOLUTION__UNIQUE_NAMESPACES = MODEL_SET_FEATURE_COUNT + 3;

  /**
   * The feature id for the '<em><b>Models</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int EVOLUTION__MODELS = MODEL_SET_FEATURE_COUNT + 4;

  /**
   * The feature id for the '<em><b>Root Packages</b></em>' reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int EVOLUTION__ROOT_PACKAGES = MODEL_SET_FEATURE_COUNT + 5;

  /**
   * The feature id for the '<em><b>All Packages</b></em>' reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int EVOLUTION__ALL_PACKAGES = MODEL_SET_FEATURE_COUNT + 6;

  /**
   * The feature id for the '<em><b>Missing Packages</b></em>' reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int EVOLUTION__MISSING_PACKAGES = MODEL_SET_FEATURE_COUNT + 7;

  /**
   * The feature id for the '<em><b>Releases</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int EVOLUTION__RELEASES = MODEL_SET_FEATURE_COUNT + 8;

  /**
   * The feature id for the '<em><b>Ordered Releases</b></em>' reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int EVOLUTION__ORDERED_RELEASES = MODEL_SET_FEATURE_COUNT + 9;

  /**
   * The feature id for the '<em><b>Latest Release</b></em>' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int EVOLUTION__LATEST_RELEASE = MODEL_SET_FEATURE_COUNT + 10;

  /**
   * The feature id for the '<em><b>Next Release Version</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int EVOLUTION__NEXT_RELEASE_VERSION = MODEL_SET_FEATURE_COUNT + 11;

  /**
   * The number of structural features of the '<em>Evolution</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int EVOLUTION_FEATURE_COUNT = MODEL_SET_FEATURE_COUNT + 12;

  /**
   * The operation id for the '<em>Get Evolution</em>' operation.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int EVOLUTION___GET_EVOLUTION = MODEL_SET___GET_EVOLUTION;

  /**
   * The operation id for the '<em>Get Version</em>' operation.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int EVOLUTION___GET_VERSION = MODEL_SET___GET_VERSION;

  /**
   * The operation id for the '<em>Get Previous Release</em>' operation.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int EVOLUTION___GET_PREVIOUS_RELEASE = MODEL_SET___GET_PREVIOUS_RELEASE;

  /**
   * The operation id for the '<em>Get Root Packages</em>' operation.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int EVOLUTION___GET_ROOT_PACKAGES = MODEL_SET___GET_ROOT_PACKAGES;

  /**
   * The operation id for the '<em>Get All Packages</em>' operation.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int EVOLUTION___GET_ALL_PACKAGES = MODEL_SET___GET_ALL_PACKAGES;

  /**
   * The operation id for the '<em>Contains Element</em>' operation.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int EVOLUTION___CONTAINS_ELEMENT__EMODELELEMENT = MODEL_SET___CONTAINS_ELEMENT__EMODELELEMENT;

  /**
   * The operation id for the '<em>Get Element</em>' operation.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int EVOLUTION___GET_ELEMENT__STRING = MODEL_SET___GET_ELEMENT__STRING;

  /**
   * The operation id for the '<em>Get Element ID</em>' operation.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int EVOLUTION___GET_ELEMENT_ID__EMODELELEMENT = MODEL_SET___GET_ELEMENT_ID__EMODELELEMENT;

  /**
   * The operation id for the '<em>Get Element ID</em>' operation.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int EVOLUTION___GET_ELEMENT_ID__EMODELELEMENT_BOOLEAN = MODEL_SET___GET_ELEMENT_ID__EMODELELEMENT_BOOLEAN;

  /**
   * The operation id for the '<em>Compare</em>' operation.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int EVOLUTION___COMPARE__MODELSET = MODEL_SET___COMPARE__MODELSET;

  /**
   * The operation id for the '<em>Get Migration</em>' operation.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int EVOLUTION___GET_MIGRATION__STRING = MODEL_SET___GET_MIGRATION__STRING;

  /**
   * The operation id for the '<em>Get Release</em>' operation.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int EVOLUTION___GET_RELEASE__INT = MODEL_SET_OPERATION_COUNT + 0;

  /**
   * The number of operations of the '<em>Evolution</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int EVOLUTION_OPERATION_COUNT = MODEL_SET_OPERATION_COUNT + 1;

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.evolution.impl.ReleaseImpl <em>Release</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.emf.cdo.evolution.impl.ReleaseImpl
   * @see org.eclipse.emf.cdo.evolution.impl.EvolutionPackageImpl#getRelease()
   * @generated
   */
  int RELEASE = 3;

  /**
   * The feature id for the '<em><b>Change</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int RELEASE__CHANGE = MODEL_SET__CHANGE;

  /**
   * The feature id for the '<em><b>Migrations</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int RELEASE__MIGRATIONS = MODEL_SET__MIGRATIONS;

  /**
   * The feature id for the '<em><b>Evolution</b></em>' container reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int RELEASE__EVOLUTION = MODEL_SET_FEATURE_COUNT + 0;

  /**
   * The feature id for the '<em><b>Version</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int RELEASE__VERSION = MODEL_SET_FEATURE_COUNT + 1;

  /**
   * The feature id for the '<em><b>Date</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int RELEASE__DATE = MODEL_SET_FEATURE_COUNT + 2;

  /**
   * The feature id for the '<em><b>Next Release</b></em>' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int RELEASE__NEXT_RELEASE = MODEL_SET_FEATURE_COUNT + 3;

  /**
   * The feature id for the '<em><b>Previous Release</b></em>' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int RELEASE__PREVIOUS_RELEASE = MODEL_SET_FEATURE_COUNT + 4;

  /**
   * The feature id for the '<em><b>Root Packages</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int RELEASE__ROOT_PACKAGES = MODEL_SET_FEATURE_COUNT + 5;

  /**
   * The feature id for the '<em><b>All Packages</b></em>' reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int RELEASE__ALL_PACKAGES = MODEL_SET_FEATURE_COUNT + 6;

  /**
   * The number of structural features of the '<em>Release</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int RELEASE_FEATURE_COUNT = MODEL_SET_FEATURE_COUNT + 7;

  /**
   * The operation id for the '<em>Get Evolution</em>' operation.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int RELEASE___GET_EVOLUTION = MODEL_SET___GET_EVOLUTION;

  /**
   * The operation id for the '<em>Get Version</em>' operation.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int RELEASE___GET_VERSION = MODEL_SET___GET_VERSION;

  /**
   * The operation id for the '<em>Get Previous Release</em>' operation.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int RELEASE___GET_PREVIOUS_RELEASE = MODEL_SET___GET_PREVIOUS_RELEASE;

  /**
   * The operation id for the '<em>Get Root Packages</em>' operation.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int RELEASE___GET_ROOT_PACKAGES = MODEL_SET___GET_ROOT_PACKAGES;

  /**
   * The operation id for the '<em>Get All Packages</em>' operation.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int RELEASE___GET_ALL_PACKAGES = MODEL_SET___GET_ALL_PACKAGES;

  /**
   * The operation id for the '<em>Contains Element</em>' operation.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int RELEASE___CONTAINS_ELEMENT__EMODELELEMENT = MODEL_SET___CONTAINS_ELEMENT__EMODELELEMENT;

  /**
   * The operation id for the '<em>Get Element</em>' operation.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int RELEASE___GET_ELEMENT__STRING = MODEL_SET___GET_ELEMENT__STRING;

  /**
   * The operation id for the '<em>Get Element ID</em>' operation.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int RELEASE___GET_ELEMENT_ID__EMODELELEMENT = MODEL_SET___GET_ELEMENT_ID__EMODELELEMENT;

  /**
   * The operation id for the '<em>Get Element ID</em>' operation.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int RELEASE___GET_ELEMENT_ID__EMODELELEMENT_BOOLEAN = MODEL_SET___GET_ELEMENT_ID__EMODELELEMENT_BOOLEAN;

  /**
   * The operation id for the '<em>Compare</em>' operation.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int RELEASE___COMPARE__MODELSET = MODEL_SET___COMPARE__MODELSET;

  /**
   * The operation id for the '<em>Get Migration</em>' operation.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int RELEASE___GET_MIGRATION__STRING = MODEL_SET___GET_MIGRATION__STRING;

  /**
   * The number of operations of the '<em>Release</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int RELEASE_OPERATION_COUNT = MODEL_SET_OPERATION_COUNT + 0;

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.evolution.impl.ChangeImpl <em>Change</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.emf.cdo.evolution.impl.ChangeImpl
   * @see org.eclipse.emf.cdo.evolution.impl.EvolutionPackageImpl#getChange()
   * @generated
   */
  int CHANGE = 4;

  /**
   * The feature id for the '<em><b>Parent</b></em>' container reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int CHANGE__PARENT = 0;

  /**
   * The feature id for the '<em><b>Children</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int CHANGE__CHILDREN = 1;

  /**
   * The number of structural features of the '<em>Change</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int CHANGE_FEATURE_COUNT = 2;

  /**
   * The operation id for the '<em>Get Model Set Change</em>' operation.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int CHANGE___GET_MODEL_SET_CHANGE = 0;

  /**
   * The operation id for the '<em>Get Old Element For</em>' operation.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int CHANGE___GET_OLD_ELEMENT_FOR__EMODELELEMENT = 1;

  /**
   * The operation id for the '<em>Get New Elements For</em>' operation.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int CHANGE___GET_NEW_ELEMENTS_FOR__EMODELELEMENT = 2;

  /**
   * The operation id for the '<em>Get Old Model Set</em>' operation.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int CHANGE___GET_OLD_MODEL_SET = 3;

  /**
   * The operation id for the '<em>Get New Model Set</em>' operation.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int CHANGE___GET_NEW_MODEL_SET = 4;

  /**
   * The number of operations of the '<em>Change</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int CHANGE_OPERATION_COUNT = 5;

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.evolution.impl.ModelSetChangeImpl <em>Model Set Change</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.emf.cdo.evolution.impl.ModelSetChangeImpl
   * @see org.eclipse.emf.cdo.evolution.impl.EvolutionPackageImpl#getModelSetChange()
   * @generated
   */
  int MODEL_SET_CHANGE = 5;

  /**
   * The feature id for the '<em><b>Parent</b></em>' container reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int MODEL_SET_CHANGE__PARENT = CHANGE__PARENT;

  /**
   * The feature id for the '<em><b>Children</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int MODEL_SET_CHANGE__CHILDREN = CHANGE__CHILDREN;

  /**
   * The feature id for the '<em><b>Old Model Set</b></em>' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int MODEL_SET_CHANGE__OLD_MODEL_SET = CHANGE_FEATURE_COUNT + 0;

  /**
   * The feature id for the '<em><b>New Model Set</b></em>' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int MODEL_SET_CHANGE__NEW_MODEL_SET = CHANGE_FEATURE_COUNT + 1;

  /**
   * The number of structural features of the '<em>Model Set Change</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int MODEL_SET_CHANGE_FEATURE_COUNT = CHANGE_FEATURE_COUNT + 2;

  /**
   * The operation id for the '<em>Get Model Set Change</em>' operation.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int MODEL_SET_CHANGE___GET_MODEL_SET_CHANGE = CHANGE___GET_MODEL_SET_CHANGE;

  /**
   * The operation id for the '<em>Get Old Element For</em>' operation.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int MODEL_SET_CHANGE___GET_OLD_ELEMENT_FOR__EMODELELEMENT = CHANGE___GET_OLD_ELEMENT_FOR__EMODELELEMENT;

  /**
   * The operation id for the '<em>Get New Elements For</em>' operation.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int MODEL_SET_CHANGE___GET_NEW_ELEMENTS_FOR__EMODELELEMENT = CHANGE___GET_NEW_ELEMENTS_FOR__EMODELELEMENT;

  /**
   * The operation id for the '<em>Get Old Model Set</em>' operation.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int MODEL_SET_CHANGE___GET_OLD_MODEL_SET = CHANGE___GET_OLD_MODEL_SET;

  /**
   * The operation id for the '<em>Get New Model Set</em>' operation.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int MODEL_SET_CHANGE___GET_NEW_MODEL_SET = CHANGE___GET_NEW_MODEL_SET;

  /**
   * The number of operations of the '<em>Model Set Change</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int MODEL_SET_CHANGE_OPERATION_COUNT = CHANGE_OPERATION_COUNT + 0;

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.evolution.impl.ElementChangeImpl <em>Element Change</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.emf.cdo.evolution.impl.ElementChangeImpl
   * @see org.eclipse.emf.cdo.evolution.impl.EvolutionPackageImpl#getElementChange()
   * @generated
   */
  int ELEMENT_CHANGE = 6;

  /**
   * The feature id for the '<em><b>Parent</b></em>' container reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ELEMENT_CHANGE__PARENT = CHANGE__PARENT;

  /**
   * The feature id for the '<em><b>Children</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ELEMENT_CHANGE__CHILDREN = CHANGE__CHILDREN;

  /**
   * The feature id for the '<em><b>Old Element</b></em>' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ELEMENT_CHANGE__OLD_ELEMENT = CHANGE_FEATURE_COUNT + 0;

  /**
   * The feature id for the '<em><b>New Element</b></em>' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ELEMENT_CHANGE__NEW_ELEMENT = CHANGE_FEATURE_COUNT + 1;

  /**
   * The feature id for the '<em><b>Kind</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ELEMENT_CHANGE__KIND = CHANGE_FEATURE_COUNT + 2;

  /**
   * The number of structural features of the '<em>Element Change</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ELEMENT_CHANGE_FEATURE_COUNT = CHANGE_FEATURE_COUNT + 3;

  /**
   * The operation id for the '<em>Get Model Set Change</em>' operation.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ELEMENT_CHANGE___GET_MODEL_SET_CHANGE = CHANGE___GET_MODEL_SET_CHANGE;

  /**
   * The operation id for the '<em>Get Old Element For</em>' operation.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ELEMENT_CHANGE___GET_OLD_ELEMENT_FOR__EMODELELEMENT = CHANGE___GET_OLD_ELEMENT_FOR__EMODELELEMENT;

  /**
   * The operation id for the '<em>Get New Elements For</em>' operation.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ELEMENT_CHANGE___GET_NEW_ELEMENTS_FOR__EMODELELEMENT = CHANGE___GET_NEW_ELEMENTS_FOR__EMODELELEMENT;

  /**
   * The operation id for the '<em>Get Old Model Set</em>' operation.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ELEMENT_CHANGE___GET_OLD_MODEL_SET = CHANGE___GET_OLD_MODEL_SET;

  /**
   * The operation id for the '<em>Get New Model Set</em>' operation.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ELEMENT_CHANGE___GET_NEW_MODEL_SET = CHANGE___GET_NEW_MODEL_SET;

  /**
   * The operation id for the '<em>Get Element</em>' operation.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ELEMENT_CHANGE___GET_ELEMENT = CHANGE_OPERATION_COUNT + 0;

  /**
   * The number of operations of the '<em>Element Change</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ELEMENT_CHANGE_OPERATION_COUNT = CHANGE_OPERATION_COUNT + 1;

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.evolution.impl.PropertyChangeImpl <em>Property Change</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.emf.cdo.evolution.impl.PropertyChangeImpl
   * @see org.eclipse.emf.cdo.evolution.impl.EvolutionPackageImpl#getPropertyChange()
   * @generated
   */
  int PROPERTY_CHANGE = 7;

  /**
   * The feature id for the '<em><b>Parent</b></em>' container reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int PROPERTY_CHANGE__PARENT = CHANGE__PARENT;

  /**
   * The feature id for the '<em><b>Children</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int PROPERTY_CHANGE__CHILDREN = CHANGE__CHILDREN;

  /**
   * The feature id for the '<em><b>Feature</b></em>' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int PROPERTY_CHANGE__FEATURE = CHANGE_FEATURE_COUNT + 0;

  /**
   * The feature id for the '<em><b>Old Value</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int PROPERTY_CHANGE__OLD_VALUE = CHANGE_FEATURE_COUNT + 1;

  /**
   * The feature id for the '<em><b>New Value</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int PROPERTY_CHANGE__NEW_VALUE = CHANGE_FEATURE_COUNT + 2;

  /**
   * The feature id for the '<em><b>Kind</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int PROPERTY_CHANGE__KIND = CHANGE_FEATURE_COUNT + 3;

  /**
   * The number of structural features of the '<em>Property Change</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int PROPERTY_CHANGE_FEATURE_COUNT = CHANGE_FEATURE_COUNT + 4;

  /**
   * The operation id for the '<em>Get Model Set Change</em>' operation.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int PROPERTY_CHANGE___GET_MODEL_SET_CHANGE = CHANGE___GET_MODEL_SET_CHANGE;

  /**
   * The operation id for the '<em>Get Old Element For</em>' operation.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int PROPERTY_CHANGE___GET_OLD_ELEMENT_FOR__EMODELELEMENT = CHANGE___GET_OLD_ELEMENT_FOR__EMODELELEMENT;

  /**
   * The operation id for the '<em>Get New Elements For</em>' operation.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int PROPERTY_CHANGE___GET_NEW_ELEMENTS_FOR__EMODELELEMENT = CHANGE___GET_NEW_ELEMENTS_FOR__EMODELELEMENT;

  /**
   * The operation id for the '<em>Get Old Model Set</em>' operation.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int PROPERTY_CHANGE___GET_OLD_MODEL_SET = CHANGE___GET_OLD_MODEL_SET;

  /**
   * The operation id for the '<em>Get New Model Set</em>' operation.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int PROPERTY_CHANGE___GET_NEW_MODEL_SET = CHANGE___GET_NEW_MODEL_SET;

  /**
   * The number of operations of the '<em>Property Change</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int PROPERTY_CHANGE_OPERATION_COUNT = CHANGE_OPERATION_COUNT + 0;

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.evolution.impl.MigrationImpl <em>Migration</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.emf.cdo.evolution.impl.MigrationImpl
   * @see org.eclipse.emf.cdo.evolution.impl.EvolutionPackageImpl#getMigration()
   * @generated
   */
  int MIGRATION = 8;

  /**
   * The feature id for the '<em><b>Model Set</b></em>' container reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int MIGRATION__MODEL_SET = 0;

  /**
   * The feature id for the '<em><b>Diagnostic ID</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int MIGRATION__DIAGNOSTIC_ID = 1;

  /**
   * The number of structural features of the '<em>Migration</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int MIGRATION_FEATURE_COUNT = 2;

  /**
   * The number of operations of the '<em>Migration</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int MIGRATION_OPERATION_COUNT = 0;

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.evolution.impl.FeaturePathMigrationImpl <em>Feature Path Migration</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.emf.cdo.evolution.impl.FeaturePathMigrationImpl
   * @see org.eclipse.emf.cdo.evolution.impl.EvolutionPackageImpl#getFeaturePathMigration()
   * @generated
   */
  int FEATURE_PATH_MIGRATION = 9;

  /**
   * The feature id for the '<em><b>Model Set</b></em>' container reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int FEATURE_PATH_MIGRATION__MODEL_SET = MIGRATION__MODEL_SET;

  /**
   * The feature id for the '<em><b>Diagnostic ID</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int FEATURE_PATH_MIGRATION__DIAGNOSTIC_ID = MIGRATION__DIAGNOSTIC_ID;

  /**
   * The feature id for the '<em><b>From Class</b></em>' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int FEATURE_PATH_MIGRATION__FROM_CLASS = MIGRATION_FEATURE_COUNT + 0;

  /**
   * The feature id for the '<em><b>To Class</b></em>' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int FEATURE_PATH_MIGRATION__TO_CLASS = MIGRATION_FEATURE_COUNT + 1;

  /**
   * The feature id for the '<em><b>Feature Path</b></em>' reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int FEATURE_PATH_MIGRATION__FEATURE_PATH = MIGRATION_FEATURE_COUNT + 2;

  /**
   * The number of structural features of the '<em>Feature Path Migration</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int FEATURE_PATH_MIGRATION_FEATURE_COUNT = MIGRATION_FEATURE_COUNT + 3;

  /**
   * The number of operations of the '<em>Feature Path Migration</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int FEATURE_PATH_MIGRATION_OPERATION_COUNT = MIGRATION_OPERATION_COUNT + 0;

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.evolution.ChangeKind <em>Change Kind</em>}' enum.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.emf.cdo.evolution.ChangeKind
   * @see org.eclipse.emf.cdo.evolution.impl.EvolutionPackageImpl#getChangeKind()
   * @generated
   */
  int CHANGE_KIND = 10;

  /**
   * The meta object id for the '<em>URI</em>' data type.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.emf.common.util.URI
   * @see org.eclipse.emf.cdo.evolution.impl.EvolutionPackageImpl#getURI()
   * @generated
   */
  int URI = 11;

  /**
   * Returns the meta object for class '{@link org.eclipse.emf.cdo.evolution.ModelSet <em>Model Set</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Model Set</em>'.
   * @see org.eclipse.emf.cdo.evolution.ModelSet
   * @generated
   */
  EClass getModelSet();

  /**
   * Returns the meta object for the containment reference '{@link org.eclipse.emf.cdo.evolution.ModelSet#getChange <em>Change</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference '<em>Change</em>'.
   * @see org.eclipse.emf.cdo.evolution.ModelSet#getChange()
   * @see #getModelSet()
   * @generated
   */
  EReference getModelSet_Change();

  /**
   * Returns the meta object for the containment reference list '{@link org.eclipse.emf.cdo.evolution.ModelSet#getMigrations <em>Migrations</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference list '<em>Migrations</em>'.
   * @see org.eclipse.emf.cdo.evolution.ModelSet#getMigrations()
   * @see #getModelSet()
   * @generated
   */
  EReference getModelSet_Migrations();

  /**
   * Returns the meta object for the '{@link org.eclipse.emf.cdo.evolution.ModelSet#getEvolution() <em>Get Evolution</em>}' operation.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the '<em>Get Evolution</em>' operation.
   * @see org.eclipse.emf.cdo.evolution.ModelSet#getEvolution()
   * @generated
   */
  EOperation getModelSet__GetEvolution();

  /**
   * Returns the meta object for the '{@link org.eclipse.emf.cdo.evolution.ModelSet#getVersion() <em>Get Version</em>}' operation.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the '<em>Get Version</em>' operation.
   * @see org.eclipse.emf.cdo.evolution.ModelSet#getVersion()
   * @generated
   */
  EOperation getModelSet__GetVersion();

  /**
   * Returns the meta object for the '{@link org.eclipse.emf.cdo.evolution.ModelSet#getPreviousRelease() <em>Get Previous Release</em>}' operation.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the '<em>Get Previous Release</em>' operation.
   * @see org.eclipse.emf.cdo.evolution.ModelSet#getPreviousRelease()
   * @generated
   */
  EOperation getModelSet__GetPreviousRelease();

  /**
   * Returns the meta object for the '{@link org.eclipse.emf.cdo.evolution.ModelSet#getRootPackages() <em>Get Root Packages</em>}' operation.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the '<em>Get Root Packages</em>' operation.
   * @see org.eclipse.emf.cdo.evolution.ModelSet#getRootPackages()
   * @generated
   */
  EOperation getModelSet__GetRootPackages();

  /**
   * Returns the meta object for the '{@link org.eclipse.emf.cdo.evolution.ModelSet#getAllPackages() <em>Get All Packages</em>}' operation.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the '<em>Get All Packages</em>' operation.
   * @see org.eclipse.emf.cdo.evolution.ModelSet#getAllPackages()
   * @generated
   */
  EOperation getModelSet__GetAllPackages();

  /**
   * Returns the meta object for the '{@link org.eclipse.emf.cdo.evolution.ModelSet#containsElement(org.eclipse.emf.ecore.EModelElement) <em>Contains Element</em>}' operation.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the '<em>Contains Element</em>' operation.
   * @see org.eclipse.emf.cdo.evolution.ModelSet#containsElement(org.eclipse.emf.ecore.EModelElement)
   * @generated
   */
  EOperation getModelSet__ContainsElement__EModelElement();

  /**
   * Returns the meta object for the '{@link org.eclipse.emf.cdo.evolution.ModelSet#getElement(java.lang.String) <em>Get Element</em>}' operation.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the '<em>Get Element</em>' operation.
   * @see org.eclipse.emf.cdo.evolution.ModelSet#getElement(java.lang.String)
   * @generated
   */
  EOperation getModelSet__GetElement__String();

  /**
   * Returns the meta object for the '{@link org.eclipse.emf.cdo.evolution.ModelSet#getElementID(org.eclipse.emf.ecore.EModelElement) <em>Get Element ID</em>}' operation.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the '<em>Get Element ID</em>' operation.
   * @see org.eclipse.emf.cdo.evolution.ModelSet#getElementID(org.eclipse.emf.ecore.EModelElement)
   * @generated
   */
  EOperation getModelSet__GetElementID__EModelElement();

  /**
   * Returns the meta object for the '{@link org.eclipse.emf.cdo.evolution.ModelSet#getElementID(org.eclipse.emf.ecore.EModelElement, boolean) <em>Get Element ID</em>}' operation.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the '<em>Get Element ID</em>' operation.
   * @see org.eclipse.emf.cdo.evolution.ModelSet#getElementID(org.eclipse.emf.ecore.EModelElement, boolean)
   * @generated
   */
  EOperation getModelSet__GetElementID__EModelElement_boolean();

  /**
   * Returns the meta object for the '{@link org.eclipse.emf.cdo.evolution.ModelSet#compare(org.eclipse.emf.cdo.evolution.ModelSet) <em>Compare</em>}' operation.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the '<em>Compare</em>' operation.
   * @see org.eclipse.emf.cdo.evolution.ModelSet#compare(org.eclipse.emf.cdo.evolution.ModelSet)
   * @generated
   */
  EOperation getModelSet__Compare__ModelSet();

  /**
   * Returns the meta object for the '{@link org.eclipse.emf.cdo.evolution.ModelSet#getMigration(java.lang.String) <em>Get Migration</em>}' operation.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the '<em>Get Migration</em>' operation.
   * @see org.eclipse.emf.cdo.evolution.ModelSet#getMigration(java.lang.String)
   * @generated
   */
  EOperation getModelSet__GetMigration__String();

  /**
   * Returns the meta object for class '{@link org.eclipse.emf.cdo.evolution.Model <em>Model</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Model</em>'.
   * @see org.eclipse.emf.cdo.evolution.Model
   * @generated
   */
  EClass getModel();

  /**
   * Returns the meta object for the container reference '{@link org.eclipse.emf.cdo.evolution.Model#getEvolution <em>Evolution</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the container reference '<em>Evolution</em>'.
   * @see org.eclipse.emf.cdo.evolution.Model#getEvolution()
   * @see #getModel()
   * @generated
   */
  EReference getModel_Evolution();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.evolution.Model#getURI <em>URI</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>URI</em>'.
   * @see org.eclipse.emf.cdo.evolution.Model#getURI()
   * @see #getModel()
   * @generated
   */
  EAttribute getModel_URI();

  /**
   * Returns the meta object for the reference '{@link org.eclipse.emf.cdo.evolution.Model#getRootPackage <em>Root Package</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the reference '<em>Root Package</em>'.
   * @see org.eclipse.emf.cdo.evolution.Model#getRootPackage()
   * @see #getModel()
   * @generated
   */
  EReference getModel_RootPackage();

  /**
   * Returns the meta object for the reference list '{@link org.eclipse.emf.cdo.evolution.Model#getAllPackages <em>All Packages</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the reference list '<em>All Packages</em>'.
   * @see org.eclipse.emf.cdo.evolution.Model#getAllPackages()
   * @see #getModel()
   * @generated
   */
  EReference getModel_AllPackages();

  /**
   * Returns the meta object for the reference list '{@link org.eclipse.emf.cdo.evolution.Model#getReferencedPackages <em>Referenced Packages</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the reference list '<em>Referenced Packages</em>'.
   * @see org.eclipse.emf.cdo.evolution.Model#getReferencedPackages()
   * @see #getModel()
   * @generated
   */
  EReference getModel_ReferencedPackages();

  /**
   * Returns the meta object for the reference list '{@link org.eclipse.emf.cdo.evolution.Model#getMissingPackages <em>Missing Packages</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the reference list '<em>Missing Packages</em>'.
   * @see org.eclipse.emf.cdo.evolution.Model#getMissingPackages()
   * @see #getModel()
   * @generated
   */
  EReference getModel_MissingPackages();

  /**
   * Returns the meta object for class '{@link org.eclipse.emf.cdo.evolution.Evolution <em>Evolution</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Evolution</em>'.
   * @see org.eclipse.emf.cdo.evolution.Evolution
   * @generated
   */
  EClass getEvolution();

  /**
   * Returns the meta object for the containment reference list '{@link org.eclipse.emf.cdo.evolution.Evolution#getModels <em>Models</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference list '<em>Models</em>'.
   * @see org.eclipse.emf.cdo.evolution.Evolution#getModels()
   * @see #getEvolution()
   * @generated
   */
  EReference getEvolution_Models();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.evolution.Evolution#isUseEcorePackage <em>Use Ecore Package</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Use Ecore Package</em>'.
   * @see org.eclipse.emf.cdo.evolution.Evolution#isUseEcorePackage()
   * @see #getEvolution()
   * @generated
   */
  EAttribute getEvolution_UseEcorePackage();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.evolution.Evolution#isUseEresourcePackage <em>Use Eresource Package</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Use Eresource Package</em>'.
   * @see org.eclipse.emf.cdo.evolution.Evolution#isUseEresourcePackage()
   * @see #getEvolution()
   * @generated
   */
  EAttribute getEvolution_UseEresourcePackage();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.evolution.Evolution#isUseEtypesPackage <em>Use Etypes Package</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Use Etypes Package</em>'.
   * @see org.eclipse.emf.cdo.evolution.Evolution#isUseEtypesPackage()
   * @see #getEvolution()
   * @generated
   */
  EAttribute getEvolution_UseEtypesPackage();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.evolution.Evolution#isUniqueNamespaces <em>Unique Namespaces</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Unique Namespaces</em>'.
   * @see org.eclipse.emf.cdo.evolution.Evolution#isUniqueNamespaces()
   * @see #getEvolution()
   * @generated
   */
  EAttribute getEvolution_UniqueNamespaces();

  /**
   * Returns the meta object for the reference list '{@link org.eclipse.emf.cdo.evolution.Evolution#getRootPackages <em>Root Packages</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the reference list '<em>Root Packages</em>'.
   * @see org.eclipse.emf.cdo.evolution.Evolution#getRootPackages()
   * @see #getEvolution()
   * @generated
   */
  EReference getEvolution_RootPackages();

  /**
   * Returns the meta object for the reference list '{@link org.eclipse.emf.cdo.evolution.Evolution#getAllPackages <em>All Packages</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the reference list '<em>All Packages</em>'.
   * @see org.eclipse.emf.cdo.evolution.Evolution#getAllPackages()
   * @see #getEvolution()
   * @generated
   */
  EReference getEvolution_AllPackages();

  /**
   * Returns the meta object for the containment reference list '{@link org.eclipse.emf.cdo.evolution.Evolution#getReleases <em>Releases</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference list '<em>Releases</em>'.
   * @see org.eclipse.emf.cdo.evolution.Evolution#getReleases()
   * @see #getEvolution()
   * @generated
   */
  EReference getEvolution_Releases();

  /**
   * Returns the meta object for the reference list '{@link org.eclipse.emf.cdo.evolution.Evolution#getOrderedReleases <em>Ordered Releases</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the reference list '<em>Ordered Releases</em>'.
   * @see org.eclipse.emf.cdo.evolution.Evolution#getOrderedReleases()
   * @see #getEvolution()
   * @generated
   */
  EReference getEvolution_OrderedReleases();

  /**
   * Returns the meta object for the reference '{@link org.eclipse.emf.cdo.evolution.Evolution#getLatestRelease <em>Latest Release</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the reference '<em>Latest Release</em>'.
   * @see org.eclipse.emf.cdo.evolution.Evolution#getLatestRelease()
   * @see #getEvolution()
   * @generated
   */
  EReference getEvolution_LatestRelease();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.evolution.Evolution#getNextReleaseVersion <em>Next Release Version</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Next Release Version</em>'.
   * @see org.eclipse.emf.cdo.evolution.Evolution#getNextReleaseVersion()
   * @see #getEvolution()
   * @generated
   */
  EAttribute getEvolution_NextReleaseVersion();

  /**
   * Returns the meta object for the reference list '{@link org.eclipse.emf.cdo.evolution.Evolution#getMissingPackages <em>Missing Packages</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the reference list '<em>Missing Packages</em>'.
   * @see org.eclipse.emf.cdo.evolution.Evolution#getMissingPackages()
   * @see #getEvolution()
   * @generated
   */
  EReference getEvolution_MissingPackages();

  /**
   * Returns the meta object for the '{@link org.eclipse.emf.cdo.evolution.Evolution#getRelease(int) <em>Get Release</em>}' operation.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the '<em>Get Release</em>' operation.
   * @see org.eclipse.emf.cdo.evolution.Evolution#getRelease(int)
   * @generated
   */
  EOperation getEvolution__GetRelease__int();

  /**
   * Returns the meta object for class '{@link org.eclipse.emf.cdo.evolution.Release <em>Release</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Release</em>'.
   * @see org.eclipse.emf.cdo.evolution.Release
   * @generated
   */
  EClass getRelease();

  /**
   * Returns the meta object for the container reference '{@link org.eclipse.emf.cdo.evolution.Release#getEvolution <em>Evolution</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the container reference '<em>Evolution</em>'.
   * @see org.eclipse.emf.cdo.evolution.Release#getEvolution()
   * @see #getRelease()
   * @generated
   */
  EReference getRelease_Evolution();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.evolution.Release#getDate <em>Date</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Date</em>'.
   * @see org.eclipse.emf.cdo.evolution.Release#getDate()
   * @see #getRelease()
   * @generated
   */
  EAttribute getRelease_Date();

  /**
   * Returns the meta object for the reference '{@link org.eclipse.emf.cdo.evolution.Release#getNextRelease <em>Next Release</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the reference '<em>Next Release</em>'.
   * @see org.eclipse.emf.cdo.evolution.Release#getNextRelease()
   * @see #getRelease()
   * @generated
   */
  EReference getRelease_NextRelease();

  /**
   * Returns the meta object for the reference '{@link org.eclipse.emf.cdo.evolution.Release#getPreviousRelease <em>Previous Release</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the reference '<em>Previous Release</em>'.
   * @see org.eclipse.emf.cdo.evolution.Release#getPreviousRelease()
   * @see #getRelease()
   * @generated
   */
  EReference getRelease_PreviousRelease();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.evolution.Release#getVersion <em>Version</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Version</em>'.
   * @see org.eclipse.emf.cdo.evolution.Release#getVersion()
   * @see #getRelease()
   * @generated
   */
  EAttribute getRelease_Version();

  /**
   * Returns the meta object for the containment reference list '{@link org.eclipse.emf.cdo.evolution.Release#getRootPackages <em>Root Packages</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference list '<em>Root Packages</em>'.
   * @see org.eclipse.emf.cdo.evolution.Release#getRootPackages()
   * @see #getRelease()
   * @generated
   */
  EReference getRelease_RootPackages();

  /**
   * Returns the meta object for the reference list '{@link org.eclipse.emf.cdo.evolution.Release#getAllPackages <em>All Packages</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the reference list '<em>All Packages</em>'.
   * @see org.eclipse.emf.cdo.evolution.Release#getAllPackages()
   * @see #getRelease()
   * @generated
   */
  EReference getRelease_AllPackages();

  /**
   * Returns the meta object for class '{@link org.eclipse.emf.cdo.evolution.Change <em>Change</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Change</em>'.
   * @see org.eclipse.emf.cdo.evolution.Change
   * @generated
   */
  EClass getChange();

  /**
   * Returns the meta object for the container reference '{@link org.eclipse.emf.cdo.evolution.Change#getParent <em>Parent</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the container reference '<em>Parent</em>'.
   * @see org.eclipse.emf.cdo.evolution.Change#getParent()
   * @see #getChange()
   * @generated
   */
  EReference getChange_Parent();

  /**
   * Returns the meta object for the containment reference list '{@link org.eclipse.emf.cdo.evolution.Change#getChildren <em>Children</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference list '<em>Children</em>'.
   * @see org.eclipse.emf.cdo.evolution.Change#getChildren()
   * @see #getChange()
   * @generated
   */
  EReference getChange_Children();

  /**
   * Returns the meta object for the '{@link org.eclipse.emf.cdo.evolution.Change#getOldModelSet() <em>Get Old Model Set</em>}' operation.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the '<em>Get Old Model Set</em>' operation.
   * @see org.eclipse.emf.cdo.evolution.Change#getOldModelSet()
   * @generated
   */
  EOperation getChange__GetOldModelSet();

  /**
   * Returns the meta object for the '{@link org.eclipse.emf.cdo.evolution.Change#getNewModelSet() <em>Get New Model Set</em>}' operation.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the '<em>Get New Model Set</em>' operation.
   * @see org.eclipse.emf.cdo.evolution.Change#getNewModelSet()
   * @generated
   */
  EOperation getChange__GetNewModelSet();

  /**
   * Returns the meta object for the '{@link org.eclipse.emf.cdo.evolution.Change#getModelSetChange() <em>Get Model Set Change</em>}' operation.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the '<em>Get Model Set Change</em>' operation.
   * @see org.eclipse.emf.cdo.evolution.Change#getModelSetChange()
   * @generated
   */
  EOperation getChange__GetModelSetChange();

  /**
   * Returns the meta object for the '{@link org.eclipse.emf.cdo.evolution.Change#getOldElementFor(org.eclipse.emf.ecore.EModelElement) <em>Get Old Element For</em>}' operation.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the '<em>Get Old Element For</em>' operation.
   * @see org.eclipse.emf.cdo.evolution.Change#getOldElementFor(org.eclipse.emf.ecore.EModelElement)
   * @generated
   */
  EOperation getChange__GetOldElementFor__EModelElement();

  /**
   * Returns the meta object for the '{@link org.eclipse.emf.cdo.evolution.Change#getNewElementsFor(org.eclipse.emf.ecore.EModelElement) <em>Get New Elements For</em>}' operation.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the '<em>Get New Elements For</em>' operation.
   * @see org.eclipse.emf.cdo.evolution.Change#getNewElementsFor(org.eclipse.emf.ecore.EModelElement)
   * @generated
   */
  EOperation getChange__GetNewElementsFor__EModelElement();

  /**
   * Returns the meta object for class '{@link org.eclipse.emf.cdo.evolution.ModelSetChange <em>Model Set Change</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Model Set Change</em>'.
   * @see org.eclipse.emf.cdo.evolution.ModelSetChange
   * @generated
   */
  EClass getModelSetChange();

  /**
   * Returns the meta object for the reference '{@link org.eclipse.emf.cdo.evolution.ModelSetChange#getOldModelSet <em>Old Model Set</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the reference '<em>Old Model Set</em>'.
   * @see org.eclipse.emf.cdo.evolution.ModelSetChange#getOldModelSet()
   * @see #getModelSetChange()
   * @generated
   */
  EReference getModelSetChange_OldModelSet();

  /**
   * Returns the meta object for the reference '{@link org.eclipse.emf.cdo.evolution.ModelSetChange#getNewModelSet <em>New Model Set</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the reference '<em>New Model Set</em>'.
   * @see org.eclipse.emf.cdo.evolution.ModelSetChange#getNewModelSet()
   * @see #getModelSetChange()
   * @generated
   */
  EReference getModelSetChange_NewModelSet();

  /**
   * Returns the meta object for class '{@link org.eclipse.emf.cdo.evolution.ElementChange <em>Element Change</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Element Change</em>'.
   * @see org.eclipse.emf.cdo.evolution.ElementChange
   * @generated
   */
  EClass getElementChange();

  /**
   * Returns the meta object for the reference '{@link org.eclipse.emf.cdo.evolution.ElementChange#getOldElement <em>Old Element</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the reference '<em>Old Element</em>'.
   * @see org.eclipse.emf.cdo.evolution.ElementChange#getOldElement()
   * @see #getElementChange()
   * @generated
   */
  EReference getElementChange_OldElement();

  /**
   * Returns the meta object for the reference '{@link org.eclipse.emf.cdo.evolution.ElementChange#getNewElement <em>New Element</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the reference '<em>New Element</em>'.
   * @see org.eclipse.emf.cdo.evolution.ElementChange#getNewElement()
   * @see #getElementChange()
   * @generated
   */
  EReference getElementChange_NewElement();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.evolution.ElementChange#getKind <em>Kind</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Kind</em>'.
   * @see org.eclipse.emf.cdo.evolution.ElementChange#getKind()
   * @see #getElementChange()
   * @generated
   */
  EAttribute getElementChange_Kind();

  /**
   * Returns the meta object for the '{@link org.eclipse.emf.cdo.evolution.ElementChange#getElement() <em>Get Element</em>}' operation.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the '<em>Get Element</em>' operation.
   * @see org.eclipse.emf.cdo.evolution.ElementChange#getElement()
   * @generated
   */
  EOperation getElementChange__GetElement();

  /**
   * Returns the meta object for class '{@link org.eclipse.emf.cdo.evolution.PropertyChange <em>Property Change</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Property Change</em>'.
   * @see org.eclipse.emf.cdo.evolution.PropertyChange
   * @generated
   */
  EClass getPropertyChange();

  /**
   * Returns the meta object for the reference '{@link org.eclipse.emf.cdo.evolution.PropertyChange#getFeature <em>Feature</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the reference '<em>Feature</em>'.
   * @see org.eclipse.emf.cdo.evolution.PropertyChange#getFeature()
   * @see #getPropertyChange()
   * @generated
   */
  EReference getPropertyChange_Feature();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.evolution.PropertyChange#getOldValue <em>Old Value</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Old Value</em>'.
   * @see org.eclipse.emf.cdo.evolution.PropertyChange#getOldValue()
   * @see #getPropertyChange()
   * @generated
   */
  EAttribute getPropertyChange_OldValue();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.evolution.PropertyChange#getNewValue <em>New Value</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>New Value</em>'.
   * @see org.eclipse.emf.cdo.evolution.PropertyChange#getNewValue()
   * @see #getPropertyChange()
   * @generated
   */
  EAttribute getPropertyChange_NewValue();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.evolution.PropertyChange#getKind <em>Kind</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Kind</em>'.
   * @see org.eclipse.emf.cdo.evolution.PropertyChange#getKind()
   * @see #getPropertyChange()
   * @generated
   */
  EAttribute getPropertyChange_Kind();

  /**
   * Returns the meta object for class '{@link org.eclipse.emf.cdo.evolution.Migration <em>Migration</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Migration</em>'.
   * @see org.eclipse.emf.cdo.evolution.Migration
   * @generated
   */
  EClass getMigration();

  /**
   * Returns the meta object for the container reference '{@link org.eclipse.emf.cdo.evolution.Migration#getModelSet <em>Model Set</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the container reference '<em>Model Set</em>'.
   * @see org.eclipse.emf.cdo.evolution.Migration#getModelSet()
   * @see #getMigration()
   * @generated
   */
  EReference getMigration_ModelSet();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.evolution.Migration#getDiagnosticID <em>Diagnostic ID</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Diagnostic ID</em>'.
   * @see org.eclipse.emf.cdo.evolution.Migration#getDiagnosticID()
   * @see #getMigration()
   * @generated
   */
  EAttribute getMigration_DiagnosticID();

  /**
   * Returns the meta object for class '{@link org.eclipse.emf.cdo.evolution.FeaturePathMigration <em>Feature Path Migration</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Feature Path Migration</em>'.
   * @see org.eclipse.emf.cdo.evolution.FeaturePathMigration
   * @generated
   */
  EClass getFeaturePathMigration();

  /**
   * Returns the meta object for the reference '{@link org.eclipse.emf.cdo.evolution.FeaturePathMigration#getFromClass <em>From Class</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the reference '<em>From Class</em>'.
   * @see org.eclipse.emf.cdo.evolution.FeaturePathMigration#getFromClass()
   * @see #getFeaturePathMigration()
   * @generated
   */
  EReference getFeaturePathMigration_FromClass();

  /**
   * Returns the meta object for the reference '{@link org.eclipse.emf.cdo.evolution.FeaturePathMigration#getToClass <em>To Class</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the reference '<em>To Class</em>'.
   * @see org.eclipse.emf.cdo.evolution.FeaturePathMigration#getToClass()
   * @see #getFeaturePathMigration()
   * @generated
   */
  EReference getFeaturePathMigration_ToClass();

  /**
   * Returns the meta object for the reference list '{@link org.eclipse.emf.cdo.evolution.FeaturePathMigration#getFeaturePath <em>Feature Path</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the reference list '<em>Feature Path</em>'.
   * @see org.eclipse.emf.cdo.evolution.FeaturePathMigration#getFeaturePath()
   * @see #getFeaturePathMigration()
   * @generated
   */
  EReference getFeaturePathMigration_FeaturePath();

  /**
   * Returns the meta object for enum '{@link org.eclipse.emf.cdo.evolution.ChangeKind <em>Change Kind</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for enum '<em>Change Kind</em>'.
   * @see org.eclipse.emf.cdo.evolution.ChangeKind
   * @generated
   */
  EEnum getChangeKind();

  /**
   * Returns the meta object for data type '{@link org.eclipse.emf.common.util.URI <em>URI</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for data type '<em>URI</em>'.
   * @see org.eclipse.emf.common.util.URI
   * @model instanceClass="org.eclipse.emf.common.util.URI"
   * @generated
   */
  EDataType getURI();

  /**
   * Returns the factory that creates the instances of the model.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the factory that creates the instances of the model.
   * @generated
   */
  EvolutionFactory getEvolutionFactory();

  /**
   * <!-- begin-user-doc -->
   * Defines literals for the meta objects that represent
   * <ul>
   *   <li>each class,</li>
   *   <li>each feature of each class,</li>
   *   <li>each operation of each class,</li>
   *   <li>each enum,</li>
   *   <li>and each data type</li>
   * </ul>
   * <!-- end-user-doc -->
   * @generated
   */
  interface Literals
  {
    /**
     * The meta object literal for the '{@link org.eclipse.emf.cdo.evolution.impl.ModelSetImpl <em>Model Set</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.emf.cdo.evolution.impl.ModelSetImpl
     * @see org.eclipse.emf.cdo.evolution.impl.EvolutionPackageImpl#getModelSet()
     * @generated
     */
    EClass MODEL_SET = eINSTANCE.getModelSet();

    /**
     * The meta object literal for the '<em><b>Change</b></em>' containment reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference MODEL_SET__CHANGE = eINSTANCE.getModelSet_Change();

    /**
     * The meta object literal for the '<em><b>Migrations</b></em>' containment reference list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference MODEL_SET__MIGRATIONS = eINSTANCE.getModelSet_Migrations();

    /**
     * The meta object literal for the '<em><b>Get Evolution</b></em>' operation.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EOperation MODEL_SET___GET_EVOLUTION = eINSTANCE.getModelSet__GetEvolution();

    /**
     * The meta object literal for the '<em><b>Get Version</b></em>' operation.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EOperation MODEL_SET___GET_VERSION = eINSTANCE.getModelSet__GetVersion();

    /**
     * The meta object literal for the '<em><b>Get Previous Release</b></em>' operation.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EOperation MODEL_SET___GET_PREVIOUS_RELEASE = eINSTANCE.getModelSet__GetPreviousRelease();

    /**
     * The meta object literal for the '<em><b>Get Root Packages</b></em>' operation.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EOperation MODEL_SET___GET_ROOT_PACKAGES = eINSTANCE.getModelSet__GetRootPackages();

    /**
     * The meta object literal for the '<em><b>Get All Packages</b></em>' operation.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EOperation MODEL_SET___GET_ALL_PACKAGES = eINSTANCE.getModelSet__GetAllPackages();

    /**
     * The meta object literal for the '<em><b>Contains Element</b></em>' operation.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EOperation MODEL_SET___CONTAINS_ELEMENT__EMODELELEMENT = eINSTANCE.getModelSet__ContainsElement__EModelElement();

    /**
     * The meta object literal for the '<em><b>Get Element</b></em>' operation.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EOperation MODEL_SET___GET_ELEMENT__STRING = eINSTANCE.getModelSet__GetElement__String();

    /**
     * The meta object literal for the '<em><b>Get Element ID</b></em>' operation.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EOperation MODEL_SET___GET_ELEMENT_ID__EMODELELEMENT = eINSTANCE.getModelSet__GetElementID__EModelElement();

    /**
     * The meta object literal for the '<em><b>Get Element ID</b></em>' operation.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EOperation MODEL_SET___GET_ELEMENT_ID__EMODELELEMENT_BOOLEAN = eINSTANCE.getModelSet__GetElementID__EModelElement_boolean();

    /**
     * The meta object literal for the '<em><b>Compare</b></em>' operation.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EOperation MODEL_SET___COMPARE__MODELSET = eINSTANCE.getModelSet__Compare__ModelSet();

    /**
     * The meta object literal for the '<em><b>Get Migration</b></em>' operation.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EOperation MODEL_SET___GET_MIGRATION__STRING = eINSTANCE.getModelSet__GetMigration__String();

    /**
     * The meta object literal for the '{@link org.eclipse.emf.cdo.evolution.impl.ModelImpl <em>Model</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.emf.cdo.evolution.impl.ModelImpl
     * @see org.eclipse.emf.cdo.evolution.impl.EvolutionPackageImpl#getModel()
     * @generated
     */
    EClass MODEL = eINSTANCE.getModel();

    /**
     * The meta object literal for the '<em><b>Evolution</b></em>' container reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference MODEL__EVOLUTION = eINSTANCE.getModel_Evolution();

    /**
     * The meta object literal for the '<em><b>URI</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute MODEL__URI = eINSTANCE.getModel_URI();

    /**
     * The meta object literal for the '<em><b>Root Package</b></em>' reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference MODEL__ROOT_PACKAGE = eINSTANCE.getModel_RootPackage();

    /**
     * The meta object literal for the '<em><b>All Packages</b></em>' reference list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference MODEL__ALL_PACKAGES = eINSTANCE.getModel_AllPackages();

    /**
     * The meta object literal for the '<em><b>Referenced Packages</b></em>' reference list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference MODEL__REFERENCED_PACKAGES = eINSTANCE.getModel_ReferencedPackages();

    /**
     * The meta object literal for the '<em><b>Missing Packages</b></em>' reference list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference MODEL__MISSING_PACKAGES = eINSTANCE.getModel_MissingPackages();

    /**
     * The meta object literal for the '{@link org.eclipse.emf.cdo.evolution.impl.EvolutionImpl <em>Evolution</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.emf.cdo.evolution.impl.EvolutionImpl
     * @see org.eclipse.emf.cdo.evolution.impl.EvolutionPackageImpl#getEvolution()
     * @generated
     */
    EClass EVOLUTION = eINSTANCE.getEvolution();

    /**
     * The meta object literal for the '<em><b>Models</b></em>' containment reference list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference EVOLUTION__MODELS = eINSTANCE.getEvolution_Models();

    /**
     * The meta object literal for the '<em><b>Use Ecore Package</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute EVOLUTION__USE_ECORE_PACKAGE = eINSTANCE.getEvolution_UseEcorePackage();

    /**
     * The meta object literal for the '<em><b>Use Eresource Package</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute EVOLUTION__USE_ERESOURCE_PACKAGE = eINSTANCE.getEvolution_UseEresourcePackage();

    /**
     * The meta object literal for the '<em><b>Use Etypes Package</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute EVOLUTION__USE_ETYPES_PACKAGE = eINSTANCE.getEvolution_UseEtypesPackage();

    /**
     * The meta object literal for the '<em><b>Unique Namespaces</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute EVOLUTION__UNIQUE_NAMESPACES = eINSTANCE.getEvolution_UniqueNamespaces();

    /**
     * The meta object literal for the '<em><b>Root Packages</b></em>' reference list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference EVOLUTION__ROOT_PACKAGES = eINSTANCE.getEvolution_RootPackages();

    /**
     * The meta object literal for the '<em><b>All Packages</b></em>' reference list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference EVOLUTION__ALL_PACKAGES = eINSTANCE.getEvolution_AllPackages();

    /**
     * The meta object literal for the '<em><b>Releases</b></em>' containment reference list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference EVOLUTION__RELEASES = eINSTANCE.getEvolution_Releases();

    /**
     * The meta object literal for the '<em><b>Ordered Releases</b></em>' reference list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference EVOLUTION__ORDERED_RELEASES = eINSTANCE.getEvolution_OrderedReleases();

    /**
     * The meta object literal for the '<em><b>Latest Release</b></em>' reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference EVOLUTION__LATEST_RELEASE = eINSTANCE.getEvolution_LatestRelease();

    /**
     * The meta object literal for the '<em><b>Next Release Version</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute EVOLUTION__NEXT_RELEASE_VERSION = eINSTANCE.getEvolution_NextReleaseVersion();

    /**
     * The meta object literal for the '<em><b>Missing Packages</b></em>' reference list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference EVOLUTION__MISSING_PACKAGES = eINSTANCE.getEvolution_MissingPackages();

    /**
     * The meta object literal for the '<em><b>Get Release</b></em>' operation.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EOperation EVOLUTION___GET_RELEASE__INT = eINSTANCE.getEvolution__GetRelease__int();

    /**
     * The meta object literal for the '{@link org.eclipse.emf.cdo.evolution.impl.ReleaseImpl <em>Release</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.emf.cdo.evolution.impl.ReleaseImpl
     * @see org.eclipse.emf.cdo.evolution.impl.EvolutionPackageImpl#getRelease()
     * @generated
     */
    EClass RELEASE = eINSTANCE.getRelease();

    /**
     * The meta object literal for the '<em><b>Evolution</b></em>' container reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference RELEASE__EVOLUTION = eINSTANCE.getRelease_Evolution();

    /**
     * The meta object literal for the '<em><b>Date</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute RELEASE__DATE = eINSTANCE.getRelease_Date();

    /**
     * The meta object literal for the '<em><b>Next Release</b></em>' reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference RELEASE__NEXT_RELEASE = eINSTANCE.getRelease_NextRelease();

    /**
     * The meta object literal for the '<em><b>Previous Release</b></em>' reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference RELEASE__PREVIOUS_RELEASE = eINSTANCE.getRelease_PreviousRelease();

    /**
     * The meta object literal for the '<em><b>Version</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute RELEASE__VERSION = eINSTANCE.getRelease_Version();

    /**
     * The meta object literal for the '<em><b>Root Packages</b></em>' containment reference list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference RELEASE__ROOT_PACKAGES = eINSTANCE.getRelease_RootPackages();

    /**
     * The meta object literal for the '<em><b>All Packages</b></em>' reference list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference RELEASE__ALL_PACKAGES = eINSTANCE.getRelease_AllPackages();

    /**
     * The meta object literal for the '{@link org.eclipse.emf.cdo.evolution.impl.ChangeImpl <em>Change</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.emf.cdo.evolution.impl.ChangeImpl
     * @see org.eclipse.emf.cdo.evolution.impl.EvolutionPackageImpl#getChange()
     * @generated
     */
    EClass CHANGE = eINSTANCE.getChange();

    /**
     * The meta object literal for the '<em><b>Parent</b></em>' container reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference CHANGE__PARENT = eINSTANCE.getChange_Parent();

    /**
     * The meta object literal for the '<em><b>Children</b></em>' containment reference list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference CHANGE__CHILDREN = eINSTANCE.getChange_Children();

    /**
     * The meta object literal for the '<em><b>Get Old Model Set</b></em>' operation.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EOperation CHANGE___GET_OLD_MODEL_SET = eINSTANCE.getChange__GetOldModelSet();

    /**
     * The meta object literal for the '<em><b>Get New Model Set</b></em>' operation.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EOperation CHANGE___GET_NEW_MODEL_SET = eINSTANCE.getChange__GetNewModelSet();

    /**
     * The meta object literal for the '<em><b>Get Model Set Change</b></em>' operation.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EOperation CHANGE___GET_MODEL_SET_CHANGE = eINSTANCE.getChange__GetModelSetChange();

    /**
     * The meta object literal for the '<em><b>Get Old Element For</b></em>' operation.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EOperation CHANGE___GET_OLD_ELEMENT_FOR__EMODELELEMENT = eINSTANCE.getChange__GetOldElementFor__EModelElement();

    /**
     * The meta object literal for the '<em><b>Get New Elements For</b></em>' operation.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EOperation CHANGE___GET_NEW_ELEMENTS_FOR__EMODELELEMENT = eINSTANCE.getChange__GetNewElementsFor__EModelElement();

    /**
     * The meta object literal for the '{@link org.eclipse.emf.cdo.evolution.impl.ModelSetChangeImpl <em>Model Set Change</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.emf.cdo.evolution.impl.ModelSetChangeImpl
     * @see org.eclipse.emf.cdo.evolution.impl.EvolutionPackageImpl#getModelSetChange()
     * @generated
     */
    EClass MODEL_SET_CHANGE = eINSTANCE.getModelSetChange();

    /**
     * The meta object literal for the '<em><b>Old Model Set</b></em>' reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference MODEL_SET_CHANGE__OLD_MODEL_SET = eINSTANCE.getModelSetChange_OldModelSet();

    /**
     * The meta object literal for the '<em><b>New Model Set</b></em>' reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference MODEL_SET_CHANGE__NEW_MODEL_SET = eINSTANCE.getModelSetChange_NewModelSet();

    /**
     * The meta object literal for the '{@link org.eclipse.emf.cdo.evolution.impl.ElementChangeImpl <em>Element Change</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.emf.cdo.evolution.impl.ElementChangeImpl
     * @see org.eclipse.emf.cdo.evolution.impl.EvolutionPackageImpl#getElementChange()
     * @generated
     */
    EClass ELEMENT_CHANGE = eINSTANCE.getElementChange();

    /**
     * The meta object literal for the '<em><b>Old Element</b></em>' reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference ELEMENT_CHANGE__OLD_ELEMENT = eINSTANCE.getElementChange_OldElement();

    /**
     * The meta object literal for the '<em><b>New Element</b></em>' reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference ELEMENT_CHANGE__NEW_ELEMENT = eINSTANCE.getElementChange_NewElement();

    /**
     * The meta object literal for the '<em><b>Kind</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute ELEMENT_CHANGE__KIND = eINSTANCE.getElementChange_Kind();

    /**
     * The meta object literal for the '<em><b>Get Element</b></em>' operation.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EOperation ELEMENT_CHANGE___GET_ELEMENT = eINSTANCE.getElementChange__GetElement();

    /**
     * The meta object literal for the '{@link org.eclipse.emf.cdo.evolution.impl.PropertyChangeImpl <em>Property Change</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.emf.cdo.evolution.impl.PropertyChangeImpl
     * @see org.eclipse.emf.cdo.evolution.impl.EvolutionPackageImpl#getPropertyChange()
     * @generated
     */
    EClass PROPERTY_CHANGE = eINSTANCE.getPropertyChange();

    /**
     * The meta object literal for the '<em><b>Feature</b></em>' reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference PROPERTY_CHANGE__FEATURE = eINSTANCE.getPropertyChange_Feature();

    /**
     * The meta object literal for the '<em><b>Old Value</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute PROPERTY_CHANGE__OLD_VALUE = eINSTANCE.getPropertyChange_OldValue();

    /**
     * The meta object literal for the '<em><b>New Value</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute PROPERTY_CHANGE__NEW_VALUE = eINSTANCE.getPropertyChange_NewValue();

    /**
     * The meta object literal for the '<em><b>Kind</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute PROPERTY_CHANGE__KIND = eINSTANCE.getPropertyChange_Kind();

    /**
     * The meta object literal for the '{@link org.eclipse.emf.cdo.evolution.impl.MigrationImpl <em>Migration</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.emf.cdo.evolution.impl.MigrationImpl
     * @see org.eclipse.emf.cdo.evolution.impl.EvolutionPackageImpl#getMigration()
     * @generated
     */
    EClass MIGRATION = eINSTANCE.getMigration();

    /**
     * The meta object literal for the '<em><b>Model Set</b></em>' container reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference MIGRATION__MODEL_SET = eINSTANCE.getMigration_ModelSet();

    /**
     * The meta object literal for the '<em><b>Diagnostic ID</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute MIGRATION__DIAGNOSTIC_ID = eINSTANCE.getMigration_DiagnosticID();

    /**
     * The meta object literal for the '{@link org.eclipse.emf.cdo.evolution.impl.FeaturePathMigrationImpl <em>Feature Path Migration</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.emf.cdo.evolution.impl.FeaturePathMigrationImpl
     * @see org.eclipse.emf.cdo.evolution.impl.EvolutionPackageImpl#getFeaturePathMigration()
     * @generated
     */
    EClass FEATURE_PATH_MIGRATION = eINSTANCE.getFeaturePathMigration();

    /**
     * The meta object literal for the '<em><b>From Class</b></em>' reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference FEATURE_PATH_MIGRATION__FROM_CLASS = eINSTANCE.getFeaturePathMigration_FromClass();

    /**
     * The meta object literal for the '<em><b>To Class</b></em>' reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference FEATURE_PATH_MIGRATION__TO_CLASS = eINSTANCE.getFeaturePathMigration_ToClass();

    /**
     * The meta object literal for the '<em><b>Feature Path</b></em>' reference list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference FEATURE_PATH_MIGRATION__FEATURE_PATH = eINSTANCE.getFeaturePathMigration_FeaturePath();

    /**
     * The meta object literal for the '{@link org.eclipse.emf.cdo.evolution.ChangeKind <em>Change Kind</em>}' enum.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.emf.cdo.evolution.ChangeKind
     * @see org.eclipse.emf.cdo.evolution.impl.EvolutionPackageImpl#getChangeKind()
     * @generated
     */
    EEnum CHANGE_KIND = eINSTANCE.getChangeKind();

    /**
     * The meta object literal for the '<em>URI</em>' data type.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.emf.common.util.URI
     * @see org.eclipse.emf.cdo.evolution.impl.EvolutionPackageImpl#getURI()
     * @generated
     */
    EDataType URI = eINSTANCE.getURI();

  }

} // EvolutionPackage
