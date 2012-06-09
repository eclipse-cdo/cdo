/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.security.impl;

import org.eclipse.emf.cdo.etypes.EtypesPackage;
import org.eclipse.emf.cdo.security.Assignee;
import org.eclipse.emf.cdo.security.Check;
import org.eclipse.emf.cdo.security.ClassCheck;
import org.eclipse.emf.cdo.security.Directory;
import org.eclipse.emf.cdo.security.Group;
import org.eclipse.emf.cdo.security.PackageCheck;
import org.eclipse.emf.cdo.security.Permission;
import org.eclipse.emf.cdo.security.Realm;
import org.eclipse.emf.cdo.security.ResourceCheck;
import org.eclipse.emf.cdo.security.Role;
import org.eclipse.emf.cdo.security.SecurityElement;
import org.eclipse.emf.cdo.security.SecurityFactory;
import org.eclipse.emf.cdo.security.SecurityItem;
import org.eclipse.emf.cdo.security.SecurityPackage;
import org.eclipse.emf.cdo.security.User;
import org.eclipse.emf.cdo.security.UserPassword;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.impl.EPackageImpl;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model <b>Package</b>.
 * <!-- end-user-doc -->
 * @generated
 */
public class SecurityPackageImpl extends EPackageImpl implements SecurityPackage
{
  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass securityElementEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass realmEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass directoryEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass securityItemEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass roleEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass assigneeEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass groupEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass userEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass userPasswordEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass checkEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass classCheckEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass packageCheckEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass resourceCheckEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EEnum permissionEEnum = null;

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
   * @see org.eclipse.emf.cdo.security.SecurityPackage#eNS_URI
   * @see #init()
   * @generated
   */
  private SecurityPackageImpl()
  {
    super(eNS_URI, SecurityFactory.eINSTANCE);
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
   * <p>This method is used to initialize {@link SecurityPackage#eINSTANCE} when that field is accessed.
   * Clients should not invoke it directly. Instead, they should simply access that field to obtain the package.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #eNS_URI
   * @see #createPackageContents()
   * @see #initializePackageContents()
   * @generated
   */
  public static SecurityPackage init()
  {
    if (isInited)
      return (SecurityPackage)EPackage.Registry.INSTANCE.getEPackage(SecurityPackage.eNS_URI);

    // Obtain or create and register package
    SecurityPackageImpl theSecurityPackage = (SecurityPackageImpl)(EPackage.Registry.INSTANCE.get(eNS_URI) instanceof SecurityPackageImpl ? EPackage.Registry.INSTANCE
        .get(eNS_URI) : new SecurityPackageImpl());

    isInited = true;

    // Initialize simple dependencies
    EtypesPackage.eINSTANCE.eClass();

    // Create package meta-data objects
    theSecurityPackage.createPackageContents();

    // Initialize created meta-data
    theSecurityPackage.initializePackageContents();

    // Mark meta-data to indicate it can't be changed
    theSecurityPackage.freeze();

    // Update the registry and return the package
    EPackage.Registry.INSTANCE.put(SecurityPackage.eNS_URI, theSecurityPackage);
    return theSecurityPackage;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getSecurityElement()
  {
    return securityElementEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getRealm()
  {
    return realmEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getRealm_Items()
  {
    return (EReference)realmEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getRealm_AllUsers()
  {
    return (EReference)realmEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getRealm_AllGroups()
  {
    return (EReference)realmEClass.getEStructuralFeatures().get(2);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getRealm_AllRoles()
  {
    return (EReference)realmEClass.getEStructuralFeatures().get(3);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getRealm_AllChecks()
  {
    return (EReference)realmEClass.getEStructuralFeatures().get(4);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getRealm_Name()
  {
    return (EAttribute)realmEClass.getEStructuralFeatures().get(5);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getDirectory()
  {
    return directoryEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getDirectory_Items()
  {
    return (EReference)directoryEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getDirectory_Name()
  {
    return (EAttribute)directoryEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getSecurityItem()
  {
    return securityItemEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getRole()
  {
    return roleEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getRole_Assignees()
  {
    return (EReference)roleEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getRole_Id()
  {
    return (EAttribute)roleEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getRole_Checks()
  {
    return (EReference)roleEClass.getEStructuralFeatures().get(2);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getAssignee()
  {
    return assigneeEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getAssignee_Roles()
  {
    return (EReference)assigneeEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getAssignee_Id()
  {
    return (EAttribute)assigneeEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getGroup()
  {
    return groupEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getGroup_Users()
  {
    return (EReference)groupEClass.getEStructuralFeatures().get(5);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getGroup_InheritedGroups()
  {
    return (EReference)groupEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getGroup_InheritingGroups()
  {
    return (EReference)groupEClass.getEStructuralFeatures().get(2);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getGroup_AllInheritingGroups()
  {
    return (EReference)groupEClass.getEStructuralFeatures().get(3);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getGroup_AllInheritedGroups()
  {
    return (EReference)groupEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getGroup_AllRoles()
  {
    return (EReference)groupEClass.getEStructuralFeatures().get(4);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getUser()
  {
    return userEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getUser_Groups()
  {
    return (EReference)userEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getUser_AllGroups()
  {
    return (EReference)userEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getUser_AllRoles()
  {
    return (EReference)userEClass.getEStructuralFeatures().get(2);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getUser_Label()
  {
    return (EAttribute)userEClass.getEStructuralFeatures().get(3);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getUser_FirstName()
  {
    return (EAttribute)userEClass.getEStructuralFeatures().get(4);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getUser_LastName()
  {
    return (EAttribute)userEClass.getEStructuralFeatures().get(5);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getUser_Email()
  {
    return (EAttribute)userEClass.getEStructuralFeatures().get(6);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getUser_Locked()
  {
    return (EAttribute)userEClass.getEStructuralFeatures().get(7);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getUser_Password()
  {
    return (EReference)userEClass.getEStructuralFeatures().get(8);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getUserPassword()
  {
    return userPasswordEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getUserPassword_Encrypted()
  {
    return (EAttribute)userPasswordEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getCheck()
  {
    return checkEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getCheck_Role()
  {
    return (EReference)checkEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getCheck_Permission()
  {
    return (EAttribute)checkEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getClassCheck()
  {
    return classCheckEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getClassCheck_Classes()
  {
    return (EReference)classCheckEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getPackageCheck()
  {
    return packageCheckEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getPackageCheck_Packages()
  {
    return (EReference)packageCheckEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getResourceCheck()
  {
    return resourceCheckEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getResourceCheck_Pattern()
  {
    return (EAttribute)resourceCheckEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EEnum getPermission()
  {
    return permissionEEnum;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public SecurityFactory getSecurityFactory()
  {
    return (SecurityFactory)getEFactoryInstance();
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
    if (isCreated)
      return;
    isCreated = true;

    // Create classes and their features
    securityElementEClass = createEClass(SECURITY_ELEMENT);

    securityItemEClass = createEClass(SECURITY_ITEM);

    realmEClass = createEClass(REALM);
    createEReference(realmEClass, REALM__ITEMS);
    createEReference(realmEClass, REALM__ALL_USERS);
    createEReference(realmEClass, REALM__ALL_GROUPS);
    createEReference(realmEClass, REALM__ALL_ROLES);
    createEReference(realmEClass, REALM__ALL_CHECKS);
    createEAttribute(realmEClass, REALM__NAME);

    directoryEClass = createEClass(DIRECTORY);
    createEReference(directoryEClass, DIRECTORY__ITEMS);
    createEAttribute(directoryEClass, DIRECTORY__NAME);

    roleEClass = createEClass(ROLE);
    createEReference(roleEClass, ROLE__ASSIGNEES);
    createEAttribute(roleEClass, ROLE__ID);
    createEReference(roleEClass, ROLE__CHECKS);

    assigneeEClass = createEClass(ASSIGNEE);
    createEReference(assigneeEClass, ASSIGNEE__ROLES);
    createEAttribute(assigneeEClass, ASSIGNEE__ID);

    groupEClass = createEClass(GROUP);
    createEReference(groupEClass, GROUP__INHERITED_GROUPS);
    createEReference(groupEClass, GROUP__ALL_INHERITED_GROUPS);
    createEReference(groupEClass, GROUP__INHERITING_GROUPS);
    createEReference(groupEClass, GROUP__ALL_INHERITING_GROUPS);
    createEReference(groupEClass, GROUP__ALL_ROLES);
    createEReference(groupEClass, GROUP__USERS);

    userEClass = createEClass(USER);
    createEReference(userEClass, USER__GROUPS);
    createEReference(userEClass, USER__ALL_GROUPS);
    createEReference(userEClass, USER__ALL_ROLES);
    createEAttribute(userEClass, USER__LABEL);
    createEAttribute(userEClass, USER__FIRST_NAME);
    createEAttribute(userEClass, USER__LAST_NAME);
    createEAttribute(userEClass, USER__EMAIL);
    createEAttribute(userEClass, USER__LOCKED);
    createEReference(userEClass, USER__PASSWORD);

    userPasswordEClass = createEClass(USER_PASSWORD);
    createEAttribute(userPasswordEClass, USER_PASSWORD__ENCRYPTED);

    checkEClass = createEClass(CHECK);
    createEReference(checkEClass, CHECK__ROLE);
    createEAttribute(checkEClass, CHECK__PERMISSION);

    classCheckEClass = createEClass(CLASS_CHECK);
    createEReference(classCheckEClass, CLASS_CHECK__CLASSES);

    packageCheckEClass = createEClass(PACKAGE_CHECK);
    createEReference(packageCheckEClass, PACKAGE_CHECK__PACKAGES);

    resourceCheckEClass = createEClass(RESOURCE_CHECK);
    createEAttribute(resourceCheckEClass, RESOURCE_CHECK__PATTERN);

    // Create enums
    permissionEEnum = createEEnum(PERMISSION);
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
    if (isInitialized)
      return;
    isInitialized = true;

    // Initialize package
    setName(eNAME);
    setNsPrefix(eNS_PREFIX);
    setNsURI(eNS_URI);

    // Obtain other dependent packages
    EtypesPackage theEtypesPackage = (EtypesPackage)EPackage.Registry.INSTANCE.getEPackage(EtypesPackage.eNS_URI);
    EcorePackage theEcorePackage = (EcorePackage)EPackage.Registry.INSTANCE.getEPackage(EcorePackage.eNS_URI);

    // Create type parameters

    // Set bounds for type parameters

    // Add supertypes to classes
    securityElementEClass.getESuperTypes().add(theEtypesPackage.getModelElement());
    securityItemEClass.getESuperTypes().add(this.getSecurityElement());
    realmEClass.getESuperTypes().add(this.getSecurityElement());
    directoryEClass.getESuperTypes().add(this.getSecurityItem());
    roleEClass.getESuperTypes().add(this.getSecurityItem());
    assigneeEClass.getESuperTypes().add(this.getSecurityItem());
    groupEClass.getESuperTypes().add(this.getAssignee());
    userEClass.getESuperTypes().add(this.getAssignee());
    classCheckEClass.getESuperTypes().add(this.getCheck());
    packageCheckEClass.getESuperTypes().add(this.getCheck());
    resourceCheckEClass.getESuperTypes().add(this.getCheck());

    // Initialize classes and features; add operations and parameters
    initEClass(securityElementEClass, SecurityElement.class,
        "SecurityElement", IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$

    addEOperation(securityElementEClass, this.getRealm(), "getRealm", 1, 1, IS_UNIQUE, IS_ORDERED); //$NON-NLS-1$

    initEClass(securityItemEClass, SecurityItem.class,
        "SecurityItem", IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$

    initEClass(realmEClass, Realm.class, "Realm", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
    initEReference(
        getRealm_Items(),
        this.getSecurityItem(),
        null,
        "items", null, 0, -1, Realm.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
    initEReference(
        getRealm_AllUsers(),
        this.getUser(),
        null,
        "allUsers", null, 0, -1, Realm.class, IS_TRANSIENT, IS_VOLATILE, !IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
    initEReference(
        getRealm_AllGroups(),
        this.getGroup(),
        null,
        "allGroups", null, 0, -1, Realm.class, IS_TRANSIENT, IS_VOLATILE, !IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
    initEReference(
        getRealm_AllRoles(),
        this.getRole(),
        null,
        "allRoles", null, 0, -1, Realm.class, IS_TRANSIENT, IS_VOLATILE, !IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
    initEReference(
        getRealm_AllChecks(),
        this.getCheck(),
        null,
        "allChecks", null, 0, -1, Realm.class, IS_TRANSIENT, IS_VOLATILE, !IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
    initEAttribute(
        getRealm_Name(),
        theEcorePackage.getEString(),
        "name", null, 0, 1, Realm.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$

    initEClass(directoryEClass, Directory.class, "Directory", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
    initEReference(
        getDirectory_Items(),
        this.getSecurityItem(),
        null,
        "items", null, 0, -1, Directory.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
    initEAttribute(
        getDirectory_Name(),
        theEcorePackage.getEString(),
        "name", null, 0, 1, Directory.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$

    initEClass(roleEClass, Role.class, "Role", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
    initEReference(
        getRole_Assignees(),
        this.getAssignee(),
        this.getAssignee_Roles(),
        "assignees", null, 0, -1, Role.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
    initEAttribute(
        getRole_Id(),
        theEcorePackage.getEString(),
        "id", null, 0, 1, Role.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
    initEReference(
        getRole_Checks(),
        this.getCheck(),
        this.getCheck_Role(),
        "checks", null, 0, -1, Role.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$

    initEClass(assigneeEClass, Assignee.class, "Assignee", IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
    initEReference(
        getAssignee_Roles(),
        this.getRole(),
        this.getRole_Assignees(),
        "roles", null, 0, -1, Assignee.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
    initEAttribute(
        getAssignee_Id(),
        theEcorePackage.getEString(),
        "id", null, 0, 1, Assignee.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$

    initEClass(groupEClass, Group.class, "Group", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
    initEReference(
        getGroup_InheritedGroups(),
        this.getGroup(),
        this.getGroup_InheritingGroups(),
        "inheritedGroups", null, 0, -1, Group.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
    initEReference(
        getGroup_AllInheritedGroups(),
        this.getGroup(),
        null,
        "allInheritedGroups", null, 0, -1, Group.class, IS_TRANSIENT, IS_VOLATILE, !IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
    initEReference(
        getGroup_InheritingGroups(),
        this.getGroup(),
        this.getGroup_InheritedGroups(),
        "inheritingGroups", null, 0, -1, Group.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
    initEReference(
        getGroup_AllInheritingGroups(),
        this.getGroup(),
        null,
        "allInheritingGroups", null, 0, -1, Group.class, IS_TRANSIENT, IS_VOLATILE, !IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
    initEReference(
        getGroup_AllRoles(),
        this.getRole(),
        null,
        "allRoles", null, 0, -1, Group.class, IS_TRANSIENT, IS_VOLATILE, !IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
    initEReference(
        getGroup_Users(),
        this.getUser(),
        this.getUser_Groups(),
        "users", null, 0, -1, Group.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$

    initEClass(userEClass, User.class, "User", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
    initEReference(
        getUser_Groups(),
        this.getGroup(),
        this.getGroup_Users(),
        "groups", null, 0, -1, User.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
    initEReference(
        getUser_AllGroups(),
        this.getGroup(),
        null,
        "allGroups", null, 0, -1, User.class, IS_TRANSIENT, IS_VOLATILE, !IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
    initEReference(
        getUser_AllRoles(),
        this.getRole(),
        null,
        "allRoles", null, 0, -1, User.class, IS_TRANSIENT, IS_VOLATILE, !IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
    initEAttribute(
        getUser_Label(),
        theEcorePackage.getEString(),
        "label", null, 0, 1, User.class, IS_TRANSIENT, IS_VOLATILE, !IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
    initEAttribute(
        getUser_FirstName(),
        theEcorePackage.getEString(),
        "firstName", null, 0, 1, User.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
    initEAttribute(
        getUser_LastName(),
        theEcorePackage.getEString(),
        "lastName", null, 0, 1, User.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
    initEAttribute(
        getUser_Email(),
        theEcorePackage.getEString(),
        "email", null, 0, 1, User.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
    initEAttribute(
        getUser_Locked(),
        theEcorePackage.getEBoolean(),
        "locked", null, 0, 1, User.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
    initEReference(
        getUser_Password(),
        this.getUserPassword(),
        null,
        "password", null, 0, 1, User.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$

    initEClass(userPasswordEClass, UserPassword.class,
        "UserPassword", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
    initEAttribute(
        getUserPassword_Encrypted(),
        theEcorePackage.getEString(),
        "encrypted", null, 0, 1, UserPassword.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$

    initEClass(checkEClass, Check.class, "Check", IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
    initEReference(
        getCheck_Role(),
        this.getRole(),
        this.getRole_Checks(),
        "role", null, 1, 1, Check.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
    initEAttribute(
        getCheck_Permission(),
        this.getPermission(),
        "permission", "WRITE", 0, 1, Check.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$ //$NON-NLS-2$

    initEClass(classCheckEClass, ClassCheck.class,
        "ClassCheck", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
    initEReference(
        getClassCheck_Classes(),
        theEcorePackage.getEClass(),
        null,
        "classes", null, 1, -1, ClassCheck.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$

    initEClass(packageCheckEClass, PackageCheck.class,
        "PackageCheck", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
    initEReference(
        getPackageCheck_Packages(),
        theEcorePackage.getEPackage(),
        null,
        "packages", null, 1, -1, PackageCheck.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$

    initEClass(resourceCheckEClass, ResourceCheck.class,
        "ResourceCheck", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
    initEAttribute(
        getResourceCheck_Pattern(),
        theEcorePackage.getEString(),
        "pattern", null, 0, 1, ResourceCheck.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$

    // Initialize enums and add enum literals
    initEEnum(permissionEEnum, Permission.class, "Permission"); //$NON-NLS-1$
    addEEnumLiteral(permissionEEnum, Permission.READ);
    addEEnumLiteral(permissionEEnum, Permission.WRITE);

    // Create resource
    createResource(eNS_URI);
  }

} // SecurityPackageImpl
