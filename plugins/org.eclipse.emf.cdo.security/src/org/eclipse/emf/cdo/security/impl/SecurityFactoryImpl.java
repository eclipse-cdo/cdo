/*
 * Copyright (c) 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.security.impl;

//import org.eclipse.emf.cdo.security.*;
import org.eclipse.emf.cdo.expressions.Expression;
import org.eclipse.emf.cdo.security.Access;
import org.eclipse.emf.cdo.security.AndFilter;
import org.eclipse.emf.cdo.security.ClassFilter;
import org.eclipse.emf.cdo.security.ClassPermission;
import org.eclipse.emf.cdo.security.Directory;
import org.eclipse.emf.cdo.security.ExpressionFilter;
import org.eclipse.emf.cdo.security.FilterPermission;
import org.eclipse.emf.cdo.security.Group;
import org.eclipse.emf.cdo.security.Inclusion;
import org.eclipse.emf.cdo.security.LinkedFilter;
import org.eclipse.emf.cdo.security.NotFilter;
import org.eclipse.emf.cdo.security.OrFilter;
import org.eclipse.emf.cdo.security.PackageFilter;
import org.eclipse.emf.cdo.security.PackagePermission;
import org.eclipse.emf.cdo.security.PermissionFilter;
import org.eclipse.emf.cdo.security.Realm;
import org.eclipse.emf.cdo.security.ResourceFilter;
import org.eclipse.emf.cdo.security.ResourcePermission;
import org.eclipse.emf.cdo.security.Role;
import org.eclipse.emf.cdo.security.SecurityFactory;
import org.eclipse.emf.cdo.security.SecurityPackage;
import org.eclipse.emf.cdo.security.User;
import org.eclipse.emf.cdo.security.UserPassword;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.impl.EFactoryImpl;
import org.eclipse.emf.ecore.plugin.EcorePlugin;

import java.util.Arrays;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model <b>Factory</b>.
 * <!-- end-user-doc -->
 * @generated
 */
@SuppressWarnings("deprecation")
public class SecurityFactoryImpl extends EFactoryImpl implements SecurityFactory
{
  /**
   * @since 4.3
   */
  public static final Access DEFAULT_PERMISSION = Access.WRITE;

  /**
   * Creates the default factory implementation.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public static SecurityFactory init()
  {
    try
    {
      SecurityFactory theSecurityFactory = (SecurityFactory)EPackage.Registry.INSTANCE
          .getEFactory(SecurityPackage.eNS_URI);
      if (theSecurityFactory != null)
      {
        return theSecurityFactory;
      }
    }
    catch (Exception exception)
    {
      EcorePlugin.INSTANCE.log(exception);
    }
    return new SecurityFactoryImpl();
  }

  /**
   * Creates an instance of the factory.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public SecurityFactoryImpl()
  {
    super();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EObject create(EClass eClass)
  {
    switch (eClass.getClassifierID())
    {
    case SecurityPackage.REALM:
      return (EObject)createRealm();
    case SecurityPackage.DIRECTORY:
      return (EObject)createDirectory();
    case SecurityPackage.ROLE:
      return (EObject)createRole();
    case SecurityPackage.GROUP:
      return (EObject)createGroup();
    case SecurityPackage.USER:
      return (EObject)createUser();
    case SecurityPackage.USER_PASSWORD:
      return (EObject)createUserPassword();
    case SecurityPackage.CLASS_PERMISSION:
      return (EObject)createClassPermission();
    case SecurityPackage.PACKAGE_PERMISSION:
      return (EObject)createPackagePermission();
    case SecurityPackage.RESOURCE_PERMISSION:
      return (EObject)createResourcePermission();
    case SecurityPackage.FILTER_PERMISSION:
      return (EObject)createFilterPermission();
    case SecurityPackage.LINKED_FILTER:
      return (EObject)createLinkedFilter();
    case SecurityPackage.PACKAGE_FILTER:
      return (EObject)createPackageFilter();
    case SecurityPackage.CLASS_FILTER:
      return (EObject)createClassFilter();
    case SecurityPackage.RESOURCE_FILTER:
      return (EObject)createResourceFilter();
    case SecurityPackage.EXPRESSION_FILTER:
      return (EObject)createExpressionFilter();
    case SecurityPackage.NOT_FILTER:
      return (EObject)createNotFilter();
    case SecurityPackage.AND_FILTER:
      return (EObject)createAndFilter();
    case SecurityPackage.OR_FILTER:
      return (EObject)createOrFilter();
    default:
      throw new IllegalArgumentException("The class '" + eClass.getName() + "' is not a valid classifier"); //$NON-NLS-1$ //$NON-NLS-2$
    }
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Object createFromString(EDataType eDataType, String initialValue)
  {
    switch (eDataType.getClassifierID())
    {
    case SecurityPackage.INCLUSION:
      return createInclusionFromString(eDataType, initialValue);
    case SecurityPackage.ACCESS:
      return createAccessFromString(eDataType, initialValue);
    case SecurityPackage.ACCESS_OBJECT:
      return createAccessObjectFromString(eDataType, initialValue);
    default:
      throw new IllegalArgumentException("The datatype '" + eDataType.getName() + "' is not a valid classifier"); //$NON-NLS-1$ //$NON-NLS-2$
    }
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public String convertToString(EDataType eDataType, Object instanceValue)
  {
    switch (eDataType.getClassifierID())
    {
    case SecurityPackage.INCLUSION:
      return convertInclusionToString(eDataType, instanceValue);
    case SecurityPackage.ACCESS:
      return convertAccessToString(eDataType, instanceValue);
    case SecurityPackage.ACCESS_OBJECT:
      return convertAccessObjectToString(eDataType, instanceValue);
    default:
      throw new IllegalArgumentException("The datatype '" + eDataType.getName() + "' is not a valid classifier"); //$NON-NLS-1$ //$NON-NLS-2$
    }
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public Realm createRealm()
  {
    RealmImpl realm = new RealmImpl();
    return realm;
  }

  public Realm createRealm(String name)
  {
    Realm realm = createRealm();
    realm.setName(name);
    return realm;
  }

  public Realm createRealm(String name, Access defaultAccess)
  {
    Realm realm = createRealm(name);
    realm.setDefaultAccess(defaultAccess);
    return realm;
  }

  public Directory createDirectory(String name)
  {
    Directory directory = createDirectory();
    directory.setName(name);
    return directory;
  }

  public Role createRole(String id)
  {
    Role role = createRole();
    role.setId(id);
    return role;
  }

  public Group createGroup(String id)
  {
    Group group = createGroup();
    group.setId(id);
    return group;
  }

  public User createUser(String id)
  {
    User user = createUser();
    user.setId(id);
    return user;
  }

  public User createUser(String id, String password)
  {
    UserPassword userPassword = createUserPassword();
    userPassword.setEncrypted(password);

    User user = createUser(id);
    user.setPassword(userPassword);
    return user;
  }

  @Deprecated
  public org.eclipse.emf.cdo.security.ClassPermission createClassPermission(EClass eClass, Access access)
  {
    org.eclipse.emf.cdo.security.ClassPermission permission = createClassPermission();
    permission.setApplicableClass(eClass);
    permission.setAccess(access);
    return permission;
  }

  @Deprecated
  public org.eclipse.emf.cdo.security.PackagePermission createPackagePermission(EPackage ePackage, Access access)
  {
    org.eclipse.emf.cdo.security.PackagePermission permission = createPackagePermission();
    permission.setApplicablePackage(ePackage);
    permission.setAccess(access);
    return permission;
  }

  @Deprecated
  public org.eclipse.emf.cdo.security.ResourcePermission createResourcePermission(String pattern, Access access)
  {
    org.eclipse.emf.cdo.security.ResourcePermission permission = createResourcePermission();
    permission.setPattern(pattern);
    permission.setAccess(access);
    return permission;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public Directory createDirectory()
  {
    DirectoryImpl directory = new DirectoryImpl();
    return directory;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public Role createRole()
  {
    RoleImpl role = new RoleImpl();
    return role;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public Group createGroup()
  {
    GroupImpl group = new GroupImpl();
    return group;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public User createUser()
  {
    UserImpl user = new UserImpl();
    return user;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public UserPassword createUserPassword()
  {
    UserPasswordImpl userPassword = new UserPasswordImpl();
    return userPassword;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Deprecated
  public ClassPermission createClassPermission()
  {
    ClassPermissionImpl classPermission = new ClassPermissionImpl();
    return classPermission;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Deprecated
  public PackagePermission createPackagePermission()
  {
    PackagePermissionImpl packagePermission = new PackagePermissionImpl();
    return packagePermission;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Deprecated
  public ResourcePermission createResourcePermission()
  {
    ResourcePermissionImpl resourcePermission = new ResourcePermissionImpl();
    return resourcePermission;
  }

  /**
   * <!-- begin-user-doc -->
   * @since 4.3
   * <!-- end-user-doc -->
   * @generated
   */
  public FilterPermission createFilterPermission()
  {
    FilterPermissionImpl filterPermission = new FilterPermissionImpl();
    return filterPermission;
  }

  /**
   * <!-- begin-user-doc -->
   * @since 4.3
   * <!-- end-user-doc -->
   * @generated
   */
  public LinkedFilter createLinkedFilter()
  {
    LinkedFilterImpl linkedFilter = new LinkedFilterImpl();
    return linkedFilter;
  }

  /**
   * @since 4.3
   */
  public FilterPermission createFilterPermission(Access access, PermissionFilter... filters)
  {
    FilterPermission permission = createFilterPermission();
    permission.setAccess(access);
    permission.getFilters().addAll(Arrays.asList(filters));
    return permission;
  }

  /**
   * <!-- begin-user-doc -->
   * @since 4.3
   * <!-- end-user-doc -->
   * @generated
   */
  public PackageFilter createPackageFilter()
  {
    PackageFilterImpl packageFilter = new PackageFilterImpl();
    return packageFilter;
  }

  /**
   * @since 4.3
   */
  public PackageFilter createPackageFilter(EPackage ePackage)
  {
    PackageFilter filter = createPackageFilter();
    filter.setApplicablePackage(ePackage);
    return filter;
  }

  /**
   * <!-- begin-user-doc -->
   * @since 4.3
   * <!-- end-user-doc -->
   * @generated
   */
  public ClassFilter createClassFilter()
  {
    ClassFilterImpl classFilter = new ClassFilterImpl();
    return classFilter;
  }

  /**
   * @since 4.3
   */
  public ClassFilter createClassFilter(EClass eClass)
  {
    ClassFilter filter = createClassFilter();
    filter.setApplicableClass(eClass);
    return filter;
  }

  /**
   * <!-- begin-user-doc -->
   * @since 4.3
   * <!-- end-user-doc -->
   * @generated
   */
  public ResourceFilter createResourceFilter()
  {
    ResourceFilterImpl resourceFilter = new ResourceFilterImpl();
    return resourceFilter;
  }

  /**
   * @since 4.3
   */
  public ResourceFilter createResourceFilter(String path)
  {
    ResourceFilter filter = createResourceFilter();
    filter.setPath(path);
    return filter;
  }

  /**
   * @since 4.3
   */
  public ResourceFilter createResourceFilter(String path, Inclusion inclusion)
  {
    ResourceFilter filter = createResourceFilter(path);
    filter.setInclusion(inclusion);
    return filter;
  }

  /**
   * <!-- begin-user-doc -->
   * @since 4.3
   * <!-- end-user-doc -->
   * @generated
   */
  public ExpressionFilter createExpressionFilter()
  {
    ExpressionFilterImpl expressionFilter = new ExpressionFilterImpl();
    return expressionFilter;
  }

  /**
   * @since 4.3
   */
  public ExpressionFilter createExpressionFilter(Expression expression)
  {
    ExpressionFilter filter = createExpressionFilter();
    filter.setExpression(expression);
    return filter;
  }

  /**
   * <!-- begin-user-doc -->
   * @since 4.3
   * <!-- end-user-doc -->
   * @generated
   */
  public NotFilter createNotFilter()
  {
    NotFilterImpl notFilter = new NotFilterImpl();
    return notFilter;
  }

  /**
   * @since 4.3
   */
  public NotFilter createNotFilter(PermissionFilter operand)
  {
    NotFilter filter = createNotFilter();
    filter.getOperands().add(operand);
    return filter;
  }

  /**
   * <!-- begin-user-doc -->
   * @since 4.3
   * <!-- end-user-doc -->
   * @generated
   */
  public AndFilter createAndFilter()
  {
    AndFilterImpl andFilter = new AndFilterImpl();
    return andFilter;
  }

  /**
   * @since 4.3
   */
  public AndFilter createAndFilter(PermissionFilter... operands)
  {
    AndFilter filter = createAndFilter();
    filter.getOperands().addAll(Arrays.asList(operands));
    return filter;
  }

  /**
   * <!-- begin-user-doc -->
   * @since 4.3
   * <!-- end-user-doc -->
   * @generated
   */
  public OrFilter createOrFilter()
  {
    OrFilterImpl orFilter = new OrFilterImpl();
    return orFilter;
  }

  /**
   * <!-- begin-user-doc -->
   * @since 4.3
   * <!-- end-user-doc -->
   * @generated
   */
  public Inclusion createInclusionFromString(EDataType eDataType, String initialValue)
  {
    Inclusion result = Inclusion.get(initialValue);
    if (result == null)
      throw new IllegalArgumentException(
          "The value '" + initialValue + "' is not a valid enumerator of '" + eDataType.getName() + "'"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    return result;
  }

  /**
   * <!-- begin-user-doc -->
   * @since 4.3
   * <!-- end-user-doc -->
   * @generated
   */
  public String convertInclusionToString(EDataType eDataType, Object instanceValue)
  {
    return instanceValue == null ? null : instanceValue.toString();
  }

  /**
   * @since 4.3
   */
  public OrFilter createOrFilter(PermissionFilter... operands)
  {
    OrFilter filter = createOrFilter();
    filter.getOperands().addAll(Arrays.asList(operands));
    return filter;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public Access createAccessFromString(EDataType eDataType, String initialValue)
  {
    Access result = Access.get(initialValue);
    if (result == null)
      throw new IllegalArgumentException(
          "The value '" + initialValue + "' is not a valid enumerator of '" + eDataType.getName() + "'"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    return result;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public String convertAccessToString(EDataType eDataType, Object instanceValue)
  {
    return instanceValue == null ? null : instanceValue.toString();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public Access createAccessObjectFromString(EDataType eDataType, String initialValue)
  {
    return createAccessFromString(SecurityPackage.Literals.ACCESS, initialValue);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public String convertAccessObjectToString(EDataType eDataType, Object instanceValue)
  {
    return convertAccessToString(SecurityPackage.Literals.ACCESS, instanceValue);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public SecurityPackage getSecurityPackage()
  {
    return (SecurityPackage)getEPackage();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @deprecated
   * @generated
   */
  @Deprecated
  public static SecurityPackage getPackage()
  {
    return SecurityPackage.eINSTANCE;
  }

} // SecurityFactoryImpl
