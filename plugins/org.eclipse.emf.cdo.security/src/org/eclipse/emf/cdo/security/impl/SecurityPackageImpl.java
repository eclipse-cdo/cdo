/*
 * Copyright (c) 2012, 2013, 2015, 2016, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Christian W. Damus (CEA LIST) - bug 399487
 */
package org.eclipse.emf.cdo.security.impl;

import org.eclipse.emf.cdo.etypes.EtypesPackage;
import org.eclipse.emf.cdo.expressions.ExpressionsPackage;
import org.eclipse.emf.cdo.security.Access;
import org.eclipse.emf.cdo.security.AndFilter;
import org.eclipse.emf.cdo.security.Assignee;
import org.eclipse.emf.cdo.security.ClassFilter;
import org.eclipse.emf.cdo.security.ClassPermission;
import org.eclipse.emf.cdo.security.CombinedFilter;
import org.eclipse.emf.cdo.security.Directory;
import org.eclipse.emf.cdo.security.ExpressionFilter;
import org.eclipse.emf.cdo.security.FilterPermission;
import org.eclipse.emf.cdo.security.Group;
import org.eclipse.emf.cdo.security.LinkedFilter;
import org.eclipse.emf.cdo.security.NotFilter;
import org.eclipse.emf.cdo.security.ObjectFilter;
import org.eclipse.emf.cdo.security.ObjectPermission;
import org.eclipse.emf.cdo.security.OrFilter;
import org.eclipse.emf.cdo.security.PackageFilter;
import org.eclipse.emf.cdo.security.PackagePermission;
import org.eclipse.emf.cdo.security.PatternStyle;
import org.eclipse.emf.cdo.security.Permission;
import org.eclipse.emf.cdo.security.PermissionFilter;
import org.eclipse.emf.cdo.security.Realm;
import org.eclipse.emf.cdo.security.ResourceFilter;
import org.eclipse.emf.cdo.security.ResourcePermission;
import org.eclipse.emf.cdo.security.Role;
import org.eclipse.emf.cdo.security.SecurityElement;
import org.eclipse.emf.cdo.security.SecurityFactory;
import org.eclipse.emf.cdo.security.SecurityItem;
import org.eclipse.emf.cdo.security.SecurityPackage;
import org.eclipse.emf.cdo.security.User;
import org.eclipse.emf.cdo.security.UserPassword;
import org.eclipse.emf.cdo.security.util.SecurityValidator;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EValidator;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.impl.EPackageImpl;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model <b>Package</b>.
 * <!-- end-user-doc -->
 * @generated
 */
@SuppressWarnings("deprecation")
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
  private EClass permissionEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass classPermissionEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass packagePermissionEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass resourcePermissionEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass objectPermissionEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass filterPermissionEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass permissionFilterEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass linkedFilterEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass packageFilterEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass classFilterEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass resourceFilterEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass objectFilterEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass expressionFilterEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass combinedFilterEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass notFilterEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass andFilterEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass orFilterEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EEnum patternStyleEEnum = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EEnum accessEEnum = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EDataType accessObjectEDataType = null;

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
    {
      return (SecurityPackage)EPackage.Registry.INSTANCE.getEPackage(SecurityPackage.eNS_URI);
    }

    // Obtain or create and register package
    SecurityPackageImpl theSecurityPackage = (SecurityPackageImpl)(EPackage.Registry.INSTANCE.get(eNS_URI) instanceof SecurityPackageImpl
        ? EPackage.Registry.INSTANCE.get(eNS_URI) : new SecurityPackageImpl());

    isInited = true;

    // Initialize simple dependencies
    EtypesPackage.eINSTANCE.eClass();
    ExpressionsPackage.eINSTANCE.eClass();

    // Create package meta-data objects
    theSecurityPackage.createPackageContents();

    // Initialize created meta-data
    theSecurityPackage.initializePackageContents();

    // Register package validator
    EValidator.Registry.INSTANCE.put(theSecurityPackage, new EValidator.Descriptor()
    {
      @Override
      public EValidator getEValidator()
      {
        return SecurityValidator.INSTANCE;
      }
    });

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
  @Override
  public EClass getSecurityElement()
  {
    return securityElementEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EClass getRealm()
  {
    return realmEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EReference getRealm_Items()
  {
    return (EReference)realmEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EReference getRealm_AllUsers()
  {
    return (EReference)realmEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EReference getRealm_AllGroups()
  {
    return (EReference)realmEClass.getEStructuralFeatures().get(2);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EReference getRealm_AllRoles()
  {
    return (EReference)realmEClass.getEStructuralFeatures().get(3);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EReference getRealm_AllPermissions()
  {
    return (EReference)realmEClass.getEStructuralFeatures().get(4);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EAttribute getRealm_Name()
  {
    return (EAttribute)realmEClass.getEStructuralFeatures().get(5);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EAttribute getRealm_DefaultAccess()
  {
    return (EAttribute)realmEClass.getEStructuralFeatures().get(6);
  }

  /**
   * <!-- begin-user-doc -->
   * @since 4.2
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EReference getRealm_DefaultUserDirectory()
  {
    return (EReference)realmEClass.getEStructuralFeatures().get(7);
  }

  /**
   * <!-- begin-user-doc -->
   * @since 4.2
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EReference getRealm_DefaultGroupDirectory()
  {
    return (EReference)realmEClass.getEStructuralFeatures().get(8);
  }

  /**
   * <!-- begin-user-doc -->
   * @since 4.2
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EReference getRealm_DefaultRoleDirectory()
  {
    return (EReference)realmEClass.getEStructuralFeatures().get(9);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EClass getDirectory()
  {
    return directoryEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EReference getDirectory_Items()
  {
    return (EReference)directoryEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EAttribute getDirectory_Name()
  {
    return (EAttribute)directoryEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EClass getSecurityItem()
  {
    return securityItemEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EClass getRole()
  {
    return roleEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EReference getRole_Assignees()
  {
    return (EReference)roleEClass.getEStructuralFeatures().get(2);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EAttribute getRole_Id()
  {
    return (EAttribute)roleEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EReference getRole_Permissions()
  {
    return (EReference)roleEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EClass getAssignee()
  {
    return assigneeEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EReference getAssignee_Roles()
  {
    return (EReference)assigneeEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EAttribute getAssignee_Id()
  {
    return (EAttribute)assigneeEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EClass getGroup()
  {
    return groupEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EReference getGroup_Users()
  {
    return (EReference)groupEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EReference getGroup_InheritedGroups()
  {
    return (EReference)groupEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EReference getGroup_InheritingGroups()
  {
    return (EReference)groupEClass.getEStructuralFeatures().get(2);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EReference getGroup_AllInheritingGroups()
  {
    return (EReference)groupEClass.getEStructuralFeatures().get(4);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EReference getGroup_AllInheritedGroups()
  {
    return (EReference)groupEClass.getEStructuralFeatures().get(3);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EReference getGroup_AllRoles()
  {
    return (EReference)groupEClass.getEStructuralFeatures().get(5);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EClass getUser()
  {
    return userEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EReference getUser_Groups()
  {
    return (EReference)userEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EReference getUser_AllGroups()
  {
    return (EReference)userEClass.getEStructuralFeatures().get(9);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EReference getUser_AllRoles()
  {
    return (EReference)userEClass.getEStructuralFeatures().get(10);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EReference getUser_AllPermissions()
  {
    return (EReference)userEClass.getEStructuralFeatures().get(11);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EReference getUser_UnassignedRoles()
  {
    return (EReference)userEClass.getEStructuralFeatures().get(12);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EAttribute getUser_Label()
  {
    return (EAttribute)userEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EAttribute getUser_FirstName()
  {
    return (EAttribute)userEClass.getEStructuralFeatures().get(2);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EAttribute getUser_LastName()
  {
    return (EAttribute)userEClass.getEStructuralFeatures().get(3);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EAttribute getUser_Email()
  {
    return (EAttribute)userEClass.getEStructuralFeatures().get(4);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EAttribute getUser_DefaultAccessOverride()
  {
    return (EAttribute)userEClass.getEStructuralFeatures().get(5);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EAttribute getUser_DefaultAccess()
  {
    return (EAttribute)userEClass.getEStructuralFeatures().get(6);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EAttribute getUser_Locked()
  {
    return (EAttribute)userEClass.getEStructuralFeatures().get(7);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EReference getUser_Password()
  {
    return (EReference)userEClass.getEStructuralFeatures().get(8);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EClass getUserPassword()
  {
    return userPasswordEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EAttribute getUserPassword_Encrypted()
  {
    return (EAttribute)userPasswordEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EClass getPermission()
  {
    return permissionEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EReference getPermission_Role()
  {
    return (EReference)permissionEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EAttribute getPermission_Access()
  {
    return (EAttribute)permissionEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EClass getClassPermission()
  {
    return classPermissionEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EReference getClassPermission_ApplicableClass()
  {
    return (EReference)classPermissionEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EClass getPackagePermission()
  {
    return packagePermissionEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EReference getPackagePermission_ApplicablePackage()
  {
    return (EReference)packagePermissionEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EClass getResourcePermission()
  {
    return resourcePermissionEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EAttribute getResourcePermission_Pattern()
  {
    return (EAttribute)resourcePermissionEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * @since 4.2
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EClass getObjectPermission()
  {
    return objectPermissionEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * @since 4.3
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EClass getFilterPermission()
  {
    return filterPermissionEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * @since 4.3
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EReference getFilterPermission_Filters()
  {
    return (EReference)filterPermissionEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * @since 4.3
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EClass getPermissionFilter()
  {
    return permissionFilterEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * @since 4.3
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EClass getLinkedFilter()
  {
    return linkedFilterEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * @since 4.3
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EReference getLinkedFilter_Filter()
  {
    return (EReference)linkedFilterEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * @since 4.3
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EClass getPackageFilter()
  {
    return packageFilterEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * @since 4.3
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EReference getPackageFilter_ApplicablePackage()
  {
    return (EReference)packageFilterEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * @since 4.3
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EClass getClassFilter()
  {
    return classFilterEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * @since 4.3
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EReference getClassFilter_ApplicableClass()
  {
    return (EReference)classFilterEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * @since 4.3
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EAttribute getClassFilter_SubTypes()
  {
    return (EAttribute)classFilterEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc -->
   * @since 4.3
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EClass getResourceFilter()
  {
    return resourceFilterEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * @since 4.3
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EAttribute getResourceFilter_Path()
  {
    return (EAttribute)resourceFilterEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * @since 4.3
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EAttribute getResourceFilter_PatternStyle()
  {
    return (EAttribute)resourceFilterEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc -->
   * @since 4.3
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EAttribute getResourceFilter_Folders()
  {
    return (EAttribute)resourceFilterEClass.getEStructuralFeatures().get(2);
  }

  /**
   * <!-- begin-user-doc -->
   * @since 4.3
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EAttribute getResourceFilter_ModelResources()
  {
    return (EAttribute)resourceFilterEClass.getEStructuralFeatures().get(5);
  }

  /**
   * <!-- begin-user-doc -->
   * @since 4.3
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EAttribute getResourceFilter_ModelObjects()
  {
    return (EAttribute)resourceFilterEClass.getEStructuralFeatures().get(6);
  }

  /**
   * <!-- begin-user-doc -->
   * @since 4.3
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EAttribute getResourceFilter_IncludeParents()
  {
    return (EAttribute)resourceFilterEClass.getEStructuralFeatures().get(7);
  }

  /**
   * <!-- begin-user-doc -->
   * @since 4.3
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EAttribute getResourceFilter_IncludeRoot()
  {
    return (EAttribute)resourceFilterEClass.getEStructuralFeatures().get(8);
  }

  /**
   * <!-- begin-user-doc -->
   * @since 4.3
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EAttribute getResourceFilter_TextResources()
  {
    return (EAttribute)resourceFilterEClass.getEStructuralFeatures().get(3);
  }

  /**
   * <!-- begin-user-doc -->
   * @since 4.3
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EAttribute getResourceFilter_BinaryResources()
  {
    return (EAttribute)resourceFilterEClass.getEStructuralFeatures().get(4);
  }

  /**
   * <!-- begin-user-doc -->
   * @since 4.3
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EClass getObjectFilter()
  {
    return objectFilterEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * @since 4.3
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EClass getExpressionFilter()
  {
    return expressionFilterEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * @since 4.3
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EReference getExpressionFilter_Expression()
  {
    return (EReference)expressionFilterEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * @since 4.3
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EClass getCombinedFilter()
  {
    return combinedFilterEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * @since 4.3
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EReference getCombinedFilter_Operands()
  {
    return (EReference)combinedFilterEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * @since 4.3
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EClass getNotFilter()
  {
    return notFilterEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * @since 4.3
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EClass getAndFilter()
  {
    return andFilterEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * @since 4.3
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EClass getOrFilter()
  {
    return orFilterEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * @since 4.3
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EEnum getPatternStyle()
  {
    return patternStyleEEnum;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EEnum getAccess()
  {
    return accessEEnum;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EDataType getAccessObject()
  {
    return accessObjectEDataType;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
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
    {
      return;
    }
    isCreated = true;

    // Create classes and their features
    securityElementEClass = createEClass(SECURITY_ELEMENT);

    securityItemEClass = createEClass(SECURITY_ITEM);

    realmEClass = createEClass(REALM);
    createEReference(realmEClass, REALM__ITEMS);
    createEReference(realmEClass, REALM__ALL_USERS);
    createEReference(realmEClass, REALM__ALL_GROUPS);
    createEReference(realmEClass, REALM__ALL_ROLES);
    createEReference(realmEClass, REALM__ALL_PERMISSIONS);
    createEAttribute(realmEClass, REALM__NAME);
    createEAttribute(realmEClass, REALM__DEFAULT_ACCESS);
    createEReference(realmEClass, REALM__DEFAULT_USER_DIRECTORY);
    createEReference(realmEClass, REALM__DEFAULT_GROUP_DIRECTORY);
    createEReference(realmEClass, REALM__DEFAULT_ROLE_DIRECTORY);

    directoryEClass = createEClass(DIRECTORY);
    createEReference(directoryEClass, DIRECTORY__ITEMS);
    createEAttribute(directoryEClass, DIRECTORY__NAME);

    roleEClass = createEClass(ROLE);
    createEAttribute(roleEClass, ROLE__ID);
    createEReference(roleEClass, ROLE__PERMISSIONS);
    createEReference(roleEClass, ROLE__ASSIGNEES);

    assigneeEClass = createEClass(ASSIGNEE);
    createEAttribute(assigneeEClass, ASSIGNEE__ID);
    createEReference(assigneeEClass, ASSIGNEE__ROLES);

    groupEClass = createEClass(GROUP);
    createEReference(groupEClass, GROUP__USERS);
    createEReference(groupEClass, GROUP__INHERITED_GROUPS);
    createEReference(groupEClass, GROUP__INHERITING_GROUPS);
    createEReference(groupEClass, GROUP__ALL_INHERITED_GROUPS);
    createEReference(groupEClass, GROUP__ALL_INHERITING_GROUPS);
    createEReference(groupEClass, GROUP__ALL_ROLES);

    userEClass = createEClass(USER);
    createEReference(userEClass, USER__GROUPS);
    createEAttribute(userEClass, USER__LABEL);
    createEAttribute(userEClass, USER__FIRST_NAME);
    createEAttribute(userEClass, USER__LAST_NAME);
    createEAttribute(userEClass, USER__EMAIL);
    createEAttribute(userEClass, USER__DEFAULT_ACCESS_OVERRIDE);
    createEAttribute(userEClass, USER__DEFAULT_ACCESS);
    createEAttribute(userEClass, USER__LOCKED);
    createEReference(userEClass, USER__PASSWORD);
    createEReference(userEClass, USER__ALL_GROUPS);
    createEReference(userEClass, USER__ALL_ROLES);
    createEReference(userEClass, USER__ALL_PERMISSIONS);
    createEReference(userEClass, USER__UNASSIGNED_ROLES);

    userPasswordEClass = createEClass(USER_PASSWORD);
    createEAttribute(userPasswordEClass, USER_PASSWORD__ENCRYPTED);

    permissionEClass = createEClass(PERMISSION);
    createEReference(permissionEClass, PERMISSION__ROLE);
    createEAttribute(permissionEClass, PERMISSION__ACCESS);

    classPermissionEClass = createEClass(CLASS_PERMISSION);
    createEReference(classPermissionEClass, CLASS_PERMISSION__APPLICABLE_CLASS);

    packagePermissionEClass = createEClass(PACKAGE_PERMISSION);
    createEReference(packagePermissionEClass, PACKAGE_PERMISSION__APPLICABLE_PACKAGE);

    resourcePermissionEClass = createEClass(RESOURCE_PERMISSION);
    createEAttribute(resourcePermissionEClass, RESOURCE_PERMISSION__PATTERN);

    objectPermissionEClass = createEClass(OBJECT_PERMISSION);

    filterPermissionEClass = createEClass(FILTER_PERMISSION);
    createEReference(filterPermissionEClass, FILTER_PERMISSION__FILTERS);

    permissionFilterEClass = createEClass(PERMISSION_FILTER);

    linkedFilterEClass = createEClass(LINKED_FILTER);
    createEReference(linkedFilterEClass, LINKED_FILTER__FILTER);

    packageFilterEClass = createEClass(PACKAGE_FILTER);
    createEReference(packageFilterEClass, PACKAGE_FILTER__APPLICABLE_PACKAGE);

    classFilterEClass = createEClass(CLASS_FILTER);
    createEReference(classFilterEClass, CLASS_FILTER__APPLICABLE_CLASS);
    createEAttribute(classFilterEClass, CLASS_FILTER__SUB_TYPES);

    resourceFilterEClass = createEClass(RESOURCE_FILTER);
    createEAttribute(resourceFilterEClass, RESOURCE_FILTER__PATH);
    createEAttribute(resourceFilterEClass, RESOURCE_FILTER__PATTERN_STYLE);
    createEAttribute(resourceFilterEClass, RESOURCE_FILTER__FOLDERS);
    createEAttribute(resourceFilterEClass, RESOURCE_FILTER__TEXT_RESOURCES);
    createEAttribute(resourceFilterEClass, RESOURCE_FILTER__BINARY_RESOURCES);
    createEAttribute(resourceFilterEClass, RESOURCE_FILTER__MODEL_RESOURCES);
    createEAttribute(resourceFilterEClass, RESOURCE_FILTER__MODEL_OBJECTS);
    createEAttribute(resourceFilterEClass, RESOURCE_FILTER__INCLUDE_PARENTS);
    createEAttribute(resourceFilterEClass, RESOURCE_FILTER__INCLUDE_ROOT);

    objectFilterEClass = createEClass(OBJECT_FILTER);

    expressionFilterEClass = createEClass(EXPRESSION_FILTER);
    createEReference(expressionFilterEClass, EXPRESSION_FILTER__EXPRESSION);

    combinedFilterEClass = createEClass(COMBINED_FILTER);
    createEReference(combinedFilterEClass, COMBINED_FILTER__OPERANDS);

    notFilterEClass = createEClass(NOT_FILTER);

    andFilterEClass = createEClass(AND_FILTER);

    orFilterEClass = createEClass(OR_FILTER);

    // Create enums
    patternStyleEEnum = createEEnum(PATTERN_STYLE);
    accessEEnum = createEEnum(ACCESS);

    // Create data types
    accessObjectEDataType = createEDataType(ACCESS_OBJECT);
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
    {
      return;
    }
    isInitialized = true;

    // Initialize package
    setName(eNAME);
    setNsPrefix(eNS_PREFIX);
    setNsURI(eNS_URI);

    // Obtain other dependent packages
    EtypesPackage theEtypesPackage = (EtypesPackage)EPackage.Registry.INSTANCE.getEPackage(EtypesPackage.eNS_URI);
    EcorePackage theEcorePackage = (EcorePackage)EPackage.Registry.INSTANCE.getEPackage(EcorePackage.eNS_URI);
    ExpressionsPackage theExpressionsPackage = (ExpressionsPackage)EPackage.Registry.INSTANCE.getEPackage(ExpressionsPackage.eNS_URI);

    // Create type parameters

    // Set bounds for type parameters

    // Add supertypes to classes
    securityElementEClass.getESuperTypes().add(theEtypesPackage.getModelElement());
    securityItemEClass.getESuperTypes().add(getSecurityElement());
    realmEClass.getESuperTypes().add(getSecurityElement());
    directoryEClass.getESuperTypes().add(getSecurityItem());
    roleEClass.getESuperTypes().add(getSecurityItem());
    assigneeEClass.getESuperTypes().add(getSecurityItem());
    groupEClass.getESuperTypes().add(getAssignee());
    userEClass.getESuperTypes().add(getAssignee());
    classPermissionEClass.getESuperTypes().add(getPermission());
    packagePermissionEClass.getESuperTypes().add(getPermission());
    resourcePermissionEClass.getESuperTypes().add(getPermission());
    objectPermissionEClass.getESuperTypes().add(getPermission());
    filterPermissionEClass.getESuperTypes().add(getPermission());
    linkedFilterEClass.getESuperTypes().add(getPermissionFilter());
    packageFilterEClass.getESuperTypes().add(getPermissionFilter());
    classFilterEClass.getESuperTypes().add(getPermissionFilter());
    resourceFilterEClass.getESuperTypes().add(getPermissionFilter());
    objectFilterEClass.getESuperTypes().add(getPermissionFilter());
    expressionFilterEClass.getESuperTypes().add(getObjectFilter());
    combinedFilterEClass.getESuperTypes().add(getPermissionFilter());
    notFilterEClass.getESuperTypes().add(getCombinedFilter());
    andFilterEClass.getESuperTypes().add(getCombinedFilter());
    orFilterEClass.getESuperTypes().add(getCombinedFilter());

    // Initialize classes and features; add operations and parameters
    initEClass(securityElementEClass, SecurityElement.class, "SecurityElement", IS_ABSTRACT, !IS_INTERFACE, //$NON-NLS-1$
        IS_GENERATED_INSTANCE_CLASS);

    addEOperation(securityElementEClass, getRealm(), "getRealm", 1, 1, IS_UNIQUE, IS_ORDERED); //$NON-NLS-1$

    initEClass(securityItemEClass, SecurityItem.class, "SecurityItem", IS_ABSTRACT, !IS_INTERFACE, //$NON-NLS-1$
        IS_GENERATED_INSTANCE_CLASS);

    initEClass(realmEClass, Realm.class, "Realm", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
    initEReference(getRealm_Items(), getSecurityItem(), null, "items", null, 0, -1, Realm.class, !IS_TRANSIENT, //$NON-NLS-1$
        !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getRealm_AllUsers(), getUser(), null, "allUsers", null, 0, -1, Realm.class, IS_TRANSIENT, //$NON-NLS-1$
        IS_VOLATILE, !IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, IS_DERIVED, IS_ORDERED);
    initEReference(getRealm_AllGroups(), getGroup(), null, "allGroups", null, 0, -1, Realm.class, IS_TRANSIENT, //$NON-NLS-1$
        IS_VOLATILE, !IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, IS_DERIVED, IS_ORDERED);
    initEReference(getRealm_AllRoles(), getRole(), null, "allRoles", null, 0, -1, Realm.class, IS_TRANSIENT, //$NON-NLS-1$
        IS_VOLATILE, !IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, IS_DERIVED, IS_ORDERED);
    initEReference(getRealm_AllPermissions(), getPermission(), null, "allPermissions", null, 0, -1, Realm.class, //$NON-NLS-1$
        IS_TRANSIENT, IS_VOLATILE, !IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, IS_DERIVED, IS_ORDERED);
    initEAttribute(getRealm_Name(), theEcorePackage.getEString(), "name", null, 0, 1, Realm.class, !IS_TRANSIENT, //$NON-NLS-1$
        !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getRealm_DefaultAccess(), getAccessObject(), "defaultAccess", null, 0, 1, Realm.class, //$NON-NLS-1$
        !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getRealm_DefaultUserDirectory(), getDirectory(), null, "defaultUserDirectory", null, 0, 1, //$NON-NLS-1$
        Realm.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getRealm_DefaultGroupDirectory(), getDirectory(), null, "defaultGroupDirectory", null, 0, 1, //$NON-NLS-1$
        Realm.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getRealm_DefaultRoleDirectory(), getDirectory(), null, "defaultRoleDirectory", null, 0, 1, //$NON-NLS-1$
        Realm.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(directoryEClass, Directory.class, "Directory", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
    initEReference(getDirectory_Items(), getSecurityItem(), null, "items", null, 0, -1, Directory.class, //$NON-NLS-1$
        !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getDirectory_Name(), theEcorePackage.getEString(), "name", null, 0, 1, Directory.class, //$NON-NLS-1$
        !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(roleEClass, Role.class, "Role", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
    initEAttribute(getRole_Id(), theEcorePackage.getEString(), "id", null, 0, 1, Role.class, !IS_TRANSIENT, //$NON-NLS-1$
        !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getRole_Permissions(), getPermission(), getPermission_Role(), "permissions", null, 0, -1, //$NON-NLS-1$
        Role.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getRole_Assignees(), getAssignee(), getAssignee_Roles(), "assignees", null, 0, -1, //$NON-NLS-1$
        Role.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(assigneeEClass, Assignee.class, "Assignee", IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
    initEAttribute(getAssignee_Id(), theEcorePackage.getEString(), "id", null, 0, 1, Assignee.class, !IS_TRANSIENT, //$NON-NLS-1$
        !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getAssignee_Roles(), getRole(), getRole_Assignees(), "roles", null, 0, -1, Assignee.class, //$NON-NLS-1$
        !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(groupEClass, Group.class, "Group", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
    initEReference(getGroup_Users(), getUser(), getUser_Groups(), "users", null, 0, -1, Group.class, //$NON-NLS-1$
        !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getGroup_InheritedGroups(), getGroup(), getGroup_InheritingGroups(), "inheritedGroups", //$NON-NLS-1$
        null, 0, -1, Group.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED,
        IS_ORDERED);
    initEReference(getGroup_InheritingGroups(), getGroup(), getGroup_InheritedGroups(), "inheritingGroups", //$NON-NLS-1$
        null, 0, -1, Group.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED,
        IS_ORDERED);
    initEReference(getGroup_AllInheritedGroups(), getGroup(), null, "allInheritedGroups", null, 0, -1, Group.class, //$NON-NLS-1$
        IS_TRANSIENT, IS_VOLATILE, !IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, IS_DERIVED, IS_ORDERED);
    initEReference(getGroup_AllInheritingGroups(), getGroup(), null, "allInheritingGroups", null, 0, -1, //$NON-NLS-1$
        Group.class, IS_TRANSIENT, IS_VOLATILE, !IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, IS_DERIVED, IS_ORDERED);
    initEReference(getGroup_AllRoles(), getRole(), null, "allRoles", null, 0, -1, Group.class, IS_TRANSIENT, //$NON-NLS-1$
        IS_VOLATILE, !IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, IS_DERIVED, IS_ORDERED);

    initEClass(userEClass, User.class, "User", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
    initEReference(getUser_Groups(), getGroup(), getGroup_Users(), "groups", null, 0, -1, User.class, //$NON-NLS-1$
        !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getUser_Label(), theEcorePackage.getEString(), "label", null, 0, 1, User.class, IS_TRANSIENT, //$NON-NLS-1$
        IS_VOLATILE, !IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, IS_DERIVED, IS_ORDERED);
    initEAttribute(getUser_FirstName(), theEcorePackage.getEString(), "firstName", null, 0, 1, User.class, //$NON-NLS-1$
        !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getUser_LastName(), theEcorePackage.getEString(), "lastName", null, 0, 1, User.class, !IS_TRANSIENT, //$NON-NLS-1$
        !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getUser_Email(), theEcorePackage.getEString(), "email", null, 0, 1, User.class, !IS_TRANSIENT, //$NON-NLS-1$
        !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getUser_DefaultAccessOverride(), getAccessObject(), "defaultAccessOverride", null, 0, 1, //$NON-NLS-1$
        User.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getUser_DefaultAccess(), getAccessObject(), "defaultAccess", null, 0, 1, User.class, //$NON-NLS-1$
        IS_TRANSIENT, IS_VOLATILE, !IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, IS_DERIVED, IS_ORDERED);
    initEAttribute(getUser_Locked(), theEcorePackage.getEBoolean(), "locked", null, 0, 1, User.class, !IS_TRANSIENT, //$NON-NLS-1$
        !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getUser_Password(), getUserPassword(), null, "password", null, 0, 1, User.class, !IS_TRANSIENT, //$NON-NLS-1$
        !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getUser_AllGroups(), getGroup(), null, "allGroups", null, 0, -1, User.class, IS_TRANSIENT, //$NON-NLS-1$
        IS_VOLATILE, !IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, IS_DERIVED, IS_ORDERED);
    initEReference(getUser_AllRoles(), getRole(), null, "allRoles", null, 0, -1, User.class, IS_TRANSIENT, //$NON-NLS-1$
        IS_VOLATILE, !IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, IS_DERIVED, IS_ORDERED);
    initEReference(getUser_AllPermissions(), getPermission(), null, "allPermissions", null, 0, -1, User.class, //$NON-NLS-1$
        IS_TRANSIENT, IS_VOLATILE, !IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, IS_DERIVED, IS_ORDERED);
    initEReference(getUser_UnassignedRoles(), getRole(), null, "unassignedRoles", null, 0, -1, User.class, //$NON-NLS-1$
        IS_TRANSIENT, IS_VOLATILE, !IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, IS_DERIVED, IS_ORDERED);

    initEClass(userPasswordEClass, UserPassword.class, "UserPassword", !IS_ABSTRACT, !IS_INTERFACE, //$NON-NLS-1$
        IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getUserPassword_Encrypted(), theEcorePackage.getEString(), "encrypted", null, 0, 1, //$NON-NLS-1$
        UserPassword.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(permissionEClass, Permission.class, "Permission", IS_ABSTRACT, !IS_INTERFACE, //$NON-NLS-1$
        IS_GENERATED_INSTANCE_CLASS);
    initEReference(getPermission_Role(), getRole(), getRole_Permissions(), "role", null, 1, 1, //$NON-NLS-1$
        Permission.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getPermission_Access(), getAccess(), "access", "WRITE", 1, 1, Permission.class, !IS_TRANSIENT, //$NON-NLS-1$//$NON-NLS-2$
        !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(classPermissionEClass, ClassPermission.class, "ClassPermission", !IS_ABSTRACT, !IS_INTERFACE, //$NON-NLS-1$
        IS_GENERATED_INSTANCE_CLASS);
    initEReference(getClassPermission_ApplicableClass(), theEcorePackage.getEClass(), null, "applicableClass", null, 1, //$NON-NLS-1$
        1, ClassPermission.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED,
        IS_ORDERED);

    initEClass(packagePermissionEClass, PackagePermission.class, "PackagePermission", !IS_ABSTRACT, !IS_INTERFACE, //$NON-NLS-1$
        IS_GENERATED_INSTANCE_CLASS);
    initEReference(getPackagePermission_ApplicablePackage(), theEcorePackage.getEPackage(), null, "applicablePackage", //$NON-NLS-1$
        null, 1, 1, PackagePermission.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE,
        !IS_DERIVED, IS_ORDERED);

    initEClass(resourcePermissionEClass, ResourcePermission.class, "ResourcePermission", !IS_ABSTRACT, !IS_INTERFACE, //$NON-NLS-1$
        IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getResourcePermission_Pattern(), theEcorePackage.getEString(), "pattern", null, 0, 1, //$NON-NLS-1$
        ResourcePermission.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(objectPermissionEClass, ObjectPermission.class, "ObjectPermission", IS_ABSTRACT, !IS_INTERFACE, //$NON-NLS-1$
        IS_GENERATED_INSTANCE_CLASS);

    initEClass(filterPermissionEClass, FilterPermission.class, "FilterPermission", !IS_ABSTRACT, !IS_INTERFACE, //$NON-NLS-1$
        IS_GENERATED_INSTANCE_CLASS);
    initEReference(getFilterPermission_Filters(), getPermissionFilter(), null, "filters", null, 1, -1, //$NON-NLS-1$
        FilterPermission.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED,
        IS_ORDERED);

    initEClass(permissionFilterEClass, PermissionFilter.class, "PermissionFilter", IS_ABSTRACT, !IS_INTERFACE, //$NON-NLS-1$
        IS_GENERATED_INSTANCE_CLASS);

    initEClass(linkedFilterEClass, LinkedFilter.class, "LinkedFilter", !IS_ABSTRACT, !IS_INTERFACE, //$NON-NLS-1$
        IS_GENERATED_INSTANCE_CLASS);
    initEReference(getLinkedFilter_Filter(), getPermissionFilter(), null, "filter", null, 1, 1, LinkedFilter.class, //$NON-NLS-1$
        !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(packageFilterEClass, PackageFilter.class, "PackageFilter", !IS_ABSTRACT, !IS_INTERFACE, //$NON-NLS-1$
        IS_GENERATED_INSTANCE_CLASS);
    initEReference(getPackageFilter_ApplicablePackage(), theEcorePackage.getEPackage(), null, "applicablePackage", null, //$NON-NLS-1$
        1, 1, PackageFilter.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED,
        IS_ORDERED);

    initEClass(classFilterEClass, ClassFilter.class, "ClassFilter", !IS_ABSTRACT, !IS_INTERFACE, //$NON-NLS-1$
        IS_GENERATED_INSTANCE_CLASS);
    initEReference(getClassFilter_ApplicableClass(), theEcorePackage.getEClass(), null, "applicableClass", null, 1, 1, //$NON-NLS-1$
        ClassFilter.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getClassFilter_SubTypes(), theEcorePackage.getEBoolean(), "subTypes", "true", 0, 1, //$NON-NLS-1$//$NON-NLS-2$
        ClassFilter.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(resourceFilterEClass, ResourceFilter.class, "ResourceFilter", !IS_ABSTRACT, !IS_INTERFACE, //$NON-NLS-1$
        IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getResourceFilter_Path(), theEcorePackage.getEString(), "path", null, 0, 1, ResourceFilter.class, //$NON-NLS-1$
        !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getResourceFilter_PatternStyle(), getPatternStyle(), "patternStyle", "TREE", 0, 1, //$NON-NLS-1$//$NON-NLS-2$
        ResourceFilter.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getResourceFilter_Folders(), theEcorePackage.getEBoolean(), "folders", "true", 0, 1, //$NON-NLS-1$//$NON-NLS-2$
        ResourceFilter.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getResourceFilter_TextResources(), theEcorePackage.getEBoolean(), "textResources", "true", 0, 1, //$NON-NLS-1$//$NON-NLS-2$
        ResourceFilter.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getResourceFilter_BinaryResources(), theEcorePackage.getEBoolean(), "binaryResources", "true", 0, 1, //$NON-NLS-1$//$NON-NLS-2$
        ResourceFilter.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getResourceFilter_ModelResources(), theEcorePackage.getEBoolean(), "modelResources", "true", 0, 1, //$NON-NLS-1$//$NON-NLS-2$
        ResourceFilter.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getResourceFilter_ModelObjects(), theEcorePackage.getEBoolean(), "modelObjects", "true", 0, 1, //$NON-NLS-1$//$NON-NLS-2$
        ResourceFilter.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getResourceFilter_IncludeParents(), theEcorePackage.getEBoolean(), "includeParents", "true", 0, 1, //$NON-NLS-1$//$NON-NLS-2$
        ResourceFilter.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getResourceFilter_IncludeRoot(), theEcorePackage.getEBoolean(), "includeRoot", "true", 0, 1, //$NON-NLS-1$//$NON-NLS-2$
        ResourceFilter.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(objectFilterEClass, ObjectFilter.class, "ObjectFilter", IS_ABSTRACT, !IS_INTERFACE, //$NON-NLS-1$
        IS_GENERATED_INSTANCE_CLASS);

    initEClass(expressionFilterEClass, ExpressionFilter.class, "ExpressionFilter", !IS_ABSTRACT, !IS_INTERFACE, //$NON-NLS-1$
        IS_GENERATED_INSTANCE_CLASS);
    initEReference(getExpressionFilter_Expression(), theExpressionsPackage.getExpression(), null, "expression", null, 1, //$NON-NLS-1$
        1, ExpressionFilter.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED,
        IS_ORDERED);

    initEClass(combinedFilterEClass, CombinedFilter.class, "CombinedFilter", IS_ABSTRACT, !IS_INTERFACE, //$NON-NLS-1$
        IS_GENERATED_INSTANCE_CLASS);
    initEReference(getCombinedFilter_Operands(), getPermissionFilter(), null, "operands", null, 1, -1, //$NON-NLS-1$
        CombinedFilter.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED,
        IS_ORDERED);

    initEClass(notFilterEClass, NotFilter.class, "NotFilter", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$

    initEClass(andFilterEClass, AndFilter.class, "AndFilter", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$

    initEClass(orFilterEClass, OrFilter.class, "OrFilter", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$

    // Initialize enums and add enum literals
    initEEnum(patternStyleEEnum, PatternStyle.class, "PatternStyle"); //$NON-NLS-1$
    addEEnumLiteral(patternStyleEEnum, PatternStyle.EXACT);
    addEEnumLiteral(patternStyleEEnum, PatternStyle.TREE);
    addEEnumLiteral(patternStyleEEnum, PatternStyle.ANT);
    addEEnumLiteral(patternStyleEEnum, PatternStyle.REGEX);

    initEEnum(accessEEnum, Access.class, "Access"); //$NON-NLS-1$
    addEEnumLiteral(accessEEnum, Access.READ);
    addEEnumLiteral(accessEEnum, Access.WRITE);

    // Initialize data types
    initEDataType(accessObjectEDataType, Access.class, "AccessObject", IS_SERIALIZABLE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$

    // Create resource
    createResource(eNS_URI);

    // Create annotations
    // http://www.eclipse.org/emf/2002/Ecore
    createEcoreAnnotations();
    // http:///org/eclipse/emf/ecore/util/ExtendedMetaData
    createExtendedMetaDataAnnotations();
  }

  /**
   * Initializes the annotations for <b>http://www.eclipse.org/emf/2002/Ecore</b>.
   * <!-- begin-user-doc -->
   * @since 4.3
   * <!-- end-user-doc -->
   * @generated
   */
  protected void createEcoreAnnotations()
  {
    String source = "http://www.eclipse.org/emf/2002/Ecore"; //$NON-NLS-1$
    addAnnotation(realmEClass, source, new String[] { "constraints", "HasAdministrator" //$NON-NLS-1$ //$NON-NLS-2$
    });
    addAnnotation(groupEClass, source, new String[] { "constraints", "AcyclicInheritance" //$NON-NLS-1$ //$NON-NLS-2$
    });
  }

  /**
   * Initializes the annotations for <b>http:///org/eclipse/emf/ecore/util/ExtendedMetaData</b>.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected void createExtendedMetaDataAnnotations()
  {
    String source = "http:///org/eclipse/emf/ecore/util/ExtendedMetaData"; //$NON-NLS-1$
    addAnnotation(accessObjectEDataType, source, new String[] { "name", "Access:Object", //$NON-NLS-1$ //$NON-NLS-2$
        "baseType", "Access" //$NON-NLS-1$ //$NON-NLS-2$
    });
  }

} // SecurityPackageImpl
