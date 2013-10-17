/**
 */
package org.eclipse.emf.cdo.releng.preferences.impl;

import org.eclipse.emf.cdo.releng.preferences.PreferenceNode;
import org.eclipse.emf.cdo.releng.preferences.PreferencesFactory;
import org.eclipse.emf.cdo.releng.preferences.PreferencesPackage;
import org.eclipse.emf.cdo.releng.preferences.Property;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EOperation;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;

import org.eclipse.emf.ecore.impl.EPackageImpl;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model <b>Package</b>.
 * <!-- end-user-doc -->
 * @generated
 */
public class PreferencesPackageImpl extends EPackageImpl implements PreferencesPackage
{
  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass preferenceNodeEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass propertyEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EDataType escapedStringEDataType = null;

  /**
   * Creates an instance of the model <b>Package</b>, registered with
   * {@link org.eclipse.emf.ecore.EPackage.Registry EPackage.Registry} by the package
   * package URI value.
   * <p>Note: the correct way to create the package is via the static
   * factory method {@link #init init()}, which also performs
   * initialization of the package, or returns the registered package,
   * if one already exists.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.emf.ecore.EPackage.Registry
   * @see org.eclipse.emf.cdo.releng.preferences.PreferencesPackage#eNS_URI
   * @see #init()
   * @generated
   */
  private PreferencesPackageImpl()
  {
    super(eNS_URI, PreferencesFactory.eINSTANCE);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private static boolean isInited = false;

  /**
   * Creates, registers, and initializes the <b>Package</b> for this model, and for any others upon which it depends.
   * 
   * <p>This method is used to initialize {@link PreferencesPackage#eINSTANCE} when that field is accessed.
   * Clients should not invoke it directly. Instead, they should simply access that field to obtain the package.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #eNS_URI
   * @see #createPackageContents()
   * @see #initializePackageContents()
   * @generated
   */
  public static PreferencesPackage init()
  {
    if (isInited) return (PreferencesPackage)EPackage.Registry.INSTANCE.getEPackage(PreferencesPackage.eNS_URI);

    // Obtain or create and register package
    PreferencesPackageImpl thePreferencesPackage = (PreferencesPackageImpl)(EPackage.Registry.INSTANCE.get(eNS_URI) instanceof PreferencesPackageImpl ? EPackage.Registry.INSTANCE.get(eNS_URI) : new PreferencesPackageImpl());

    isInited = true;

    // Create package meta-data objects
    thePreferencesPackage.createPackageContents();

    // Initialize created meta-data
    thePreferencesPackage.initializePackageContents();

    // Mark meta-data to indicate it can't be changed
    thePreferencesPackage.freeze();

  
    // Update the registry and return the package
    EPackage.Registry.INSTANCE.put(PreferencesPackage.eNS_URI, thePreferencesPackage);
    return thePreferencesPackage;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getPreferenceNode()
  {
    return preferenceNodeEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getPreferenceNode_Children()
  {
    return (EReference)preferenceNodeEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getPreferenceNode_Parent()
  {
    return (EReference)preferenceNodeEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getPreferenceNode_Properties()
  {
    return (EReference)preferenceNodeEClass.getEStructuralFeatures().get(2);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getPreferenceNode_Name()
  {
    return (EAttribute)preferenceNodeEClass.getEStructuralFeatures().get(3);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getPreferenceNode_Location()
  {
    return (EAttribute)preferenceNodeEClass.getEStructuralFeatures().get(4);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EOperation getPreferenceNode__GetNode__String()
  {
    return preferenceNodeEClass.getEOperations().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EOperation getPreferenceNode__GetProperty__String()
  {
    return preferenceNodeEClass.getEOperations().get(1);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getProperty()
  {
    return propertyEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getProperty_Name()
  {
    return (EAttribute)propertyEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getProperty_Parent()
  {
    return (EReference)propertyEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getProperty_Value()
  {
    return (EAttribute)propertyEClass.getEStructuralFeatures().get(2);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EDataType getEscapedString()
  {
    return escapedStringEDataType;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public PreferencesFactory getPreferencesFactory()
  {
    return (PreferencesFactory)getEFactoryInstance();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private boolean isCreated = false;

  /**
   * Creates the meta-model objects for the package.  This method is
   * guarded to have no affect on any invocation but its first.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void createPackageContents()
  {
    if (isCreated) return;
    isCreated = true;

    // Create classes and their features
    preferenceNodeEClass = createEClass(PREFERENCE_NODE);
    createEReference(preferenceNodeEClass, PREFERENCE_NODE__CHILDREN);
    createEReference(preferenceNodeEClass, PREFERENCE_NODE__PARENT);
    createEReference(preferenceNodeEClass, PREFERENCE_NODE__PROPERTIES);
    createEAttribute(preferenceNodeEClass, PREFERENCE_NODE__NAME);
    createEAttribute(preferenceNodeEClass, PREFERENCE_NODE__LOCATION);
    createEOperation(preferenceNodeEClass, PREFERENCE_NODE___GET_NODE__STRING);
    createEOperation(preferenceNodeEClass, PREFERENCE_NODE___GET_PROPERTY__STRING);

    propertyEClass = createEClass(PROPERTY);
    createEAttribute(propertyEClass, PROPERTY__NAME);
    createEReference(propertyEClass, PROPERTY__PARENT);
    createEAttribute(propertyEClass, PROPERTY__VALUE);

    // Create data types
    escapedStringEDataType = createEDataType(ESCAPED_STRING);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private boolean isInitialized = false;

  /**
   * Complete the initialization of the package and its meta-model.  This
   * method is guarded to have no affect on any invocation but its first.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void initializePackageContents()
  {
    if (isInitialized) return;
    isInitialized = true;

    // Initialize package
    setName(eNAME);
    setNsPrefix(eNS_PREFIX);
    setNsURI(eNS_URI);

    // Create type parameters

    // Set bounds for type parameters

    // Add supertypes to classes

    // Initialize classes, features, and operations; add parameters
    initEClass(preferenceNodeEClass, PreferenceNode.class, "PreferenceNode", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEReference(getPreferenceNode_Children(), this.getPreferenceNode(), this.getPreferenceNode_Parent(), "children", null, 0, -1, PreferenceNode.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    getPreferenceNode_Children().getEKeys().add(this.getPreferenceNode_Name());
    initEReference(getPreferenceNode_Parent(), this.getPreferenceNode(), this.getPreferenceNode_Children(), "parent", null, 0, 1, PreferenceNode.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getPreferenceNode_Properties(), this.getProperty(), this.getProperty_Parent(), "properties", null, 0, -1, PreferenceNode.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    getPreferenceNode_Properties().getEKeys().add(this.getProperty_Name());
    initEAttribute(getPreferenceNode_Name(), ecorePackage.getEString(), "name", null, 1, 1, PreferenceNode.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getPreferenceNode_Location(), ecorePackage.getEString(), "location", null, 0, 1, PreferenceNode.class, IS_TRANSIENT, IS_VOLATILE, !IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, IS_DERIVED, IS_ORDERED);

    EOperation op = initEOperation(getPreferenceNode__GetNode__String(), this.getPreferenceNode(), "getNode", 0, 1, IS_UNIQUE, IS_ORDERED);
    addEParameter(op, ecorePackage.getEString(), "name", 0, 1, IS_UNIQUE, IS_ORDERED);

    op = initEOperation(getPreferenceNode__GetProperty__String(), this.getProperty(), "getProperty", 0, 1, IS_UNIQUE, IS_ORDERED);
    addEParameter(op, ecorePackage.getEString(), "name", 0, 1, IS_UNIQUE, IS_ORDERED);

    initEClass(propertyEClass, Property.class, "Property", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getProperty_Name(), ecorePackage.getEString(), "name", null, 1, 1, Property.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getProperty_Parent(), this.getPreferenceNode(), this.getPreferenceNode_Properties(), "parent", null, 0, 1, Property.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getProperty_Value(), this.getEscapedString(), "value", null, 0, 1, Property.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    // Initialize data types
    initEDataType(escapedStringEDataType, String.class, "EscapedString", IS_SERIALIZABLE, !IS_GENERATED_INSTANCE_CLASS);

    // Create resource
    createResource(eNS_URI);
  }

} //PreferencesPackageImpl
