/*
 * Copyright (c) 2012, 2013, 2016, 2019 Eike Stepper (Loehne, Germany) and others.
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
import org.eclipse.emf.cdo.security.LinkedFilter;
import org.eclipse.emf.cdo.security.NotFilter;
import org.eclipse.emf.cdo.security.OrFilter;
import org.eclipse.emf.cdo.security.PackageFilter;
import org.eclipse.emf.cdo.security.PackagePermission;
import org.eclipse.emf.cdo.security.PatternStyle;
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
      SecurityFactory theSecurityFactory = (SecurityFactory)EPackage.Registry.INSTANCE.getEFactory(SecurityPackage.eNS_URI);
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
      return createRealm();
    case SecurityPackage.DIRECTORY:
      return createDirectory();
    case SecurityPackage.ROLE:
      return createRole();
    case SecurityPackage.GROUP:
      return createGroup();
    case SecurityPackage.USER:
      return createUser();
    case SecurityPackage.USER_PASSWORD:
      return createUserPassword();
    case SecurityPackage.CLASS_PERMISSION:
      return createClassPermission();
    case SecurityPackage.PACKAGE_PERMISSION:
      return createPackagePermission();
    case SecurityPackage.RESOURCE_PERMISSION:
      return createResourcePermission();
    case SecurityPackage.FILTER_PERMISSION:
      return createFilterPermission();
    case SecurityPackage.LINKED_FILTER:
      return createLinkedFilter();
    case SecurityPackage.PACKAGE_FILTER:
      return createPackageFilter();
    case SecurityPackage.CLASS_FILTER:
      return createClassFilter();
    case SecurityPackage.RESOURCE_FILTER:
      return createResourceFilter();
    case SecurityPackage.EXPRESSION_FILTER:
      return createExpressionFilter();
    case SecurityPackage.NOT_FILTER:
      return createNotFilter();
    case SecurityPackage.AND_FILTER:
      return createAndFilter();
    case SecurityPackage.OR_FILTER:
      return createOrFilter();
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
    case SecurityPackage.PATTERN_STYLE:
      return createPatternStyleFromString(eDataType, initialValue);
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
    case SecurityPackage.PATTERN_STYLE:
      return convertPatternStyleToString(eDataType, instanceValue);
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
  @Override
  public Realm createRealm()
  {
    RealmImpl realm = new RealmImpl();
    return realm;
  }

  @Override
  public Realm createRealm(String name)
  {
    Realm realm = createRealm();
    realm.setName(name);
    return realm;
  }

  @Override
  public Realm createRealm(String name, Access defaultAccess)
  {
    Realm realm = createRealm(name);
    realm.setDefaultAccess(defaultAccess);
    return realm;
  }

  @Override
  public Directory createDirectory(String name)
  {
    Directory directory = createDirectory();
    directory.setName(name);
    return directory;
  }

  @Override
  public Role createRole(String id)
  {
    Role role = createRole();
    role.setId(id);
    return role;
  }

  @Override
  public Group createGroup(String id)
  {
    Group group = createGroup();
    group.setId(id);
    return group;
  }

  @Override
  public User createUser(String id)
  {
    User user = createUser();
    user.setId(id);
    return user;
  }

  @Override
  public User createUser(String id, String password)
  {
    UserPassword userPassword = createUserPassword();
    userPassword.setEncrypted(password);

    User user = createUser(id);
    user.setPassword(userPassword);
    return user;
  }

  @Override
  @Deprecated
  public org.eclipse.emf.cdo.security.ClassPermission createClassPermission(EClass eClass, Access access)
  {
    org.eclipse.emf.cdo.security.ClassPermission permission = createClassPermission();
    permission.setApplicableClass(eClass);
    permission.setAccess(access);
    return permission;
  }

  @Override
  @Deprecated
  public org.eclipse.emf.cdo.security.PackagePermission createPackagePermission(EPackage ePackage, Access access)
  {
    org.eclipse.emf.cdo.security.PackagePermission permission = createPackagePermission();
    permission.setApplicablePackage(ePackage);
    permission.setAccess(access);
    return permission;
  }

  @Override
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
  @Override
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
  @Override
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
  @Override
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
  @Override
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
  @Override
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
  @Override
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
  @Override
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
  @Override
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
  @Override
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
  @Override
  public LinkedFilter createLinkedFilter()
  {
    LinkedFilterImpl linkedFilter = new LinkedFilterImpl();
    return linkedFilter;
  }

  /**
   * @since 4.3
   */
  @Override
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
  @Override
  public PackageFilter createPackageFilter()
  {
    PackageFilterImpl packageFilter = new PackageFilterImpl();
    return packageFilter;
  }

  /**
   * @since 4.3
   */
  @Override
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
  @Override
  public ClassFilter createClassFilter()
  {
    ClassFilterImpl classFilter = new ClassFilterImpl();
    return classFilter;
  }

  /**
   * @since 4.3
   */
  @Override
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
  @Override
  public ResourceFilter createResourceFilter()
  {
    ResourceFilterImpl resourceFilter = new ResourceFilterImpl();
    return resourceFilter;
  }

  /**
   * @since 4.3
   */
  @Override
  public ResourceFilter createResourceFilter(String path)
  {
    ResourceFilter filter = createResourceFilter();
    filter.setPath(path);
    return filter;
  }

  /**
   * @since 4.3
   */
  @Override
  public ResourceFilter createResourceFilter(String path, PatternStyle patternStyle)
  {
    ResourceFilter filter = createResourceFilter(path);
    filter.setPatternStyle(patternStyle);
    return filter;
  }

  /**
   * @since 4.3
   */
  @Override
  public ResourceFilter createResourceFilter(String path, PatternStyle patternStyle, boolean includeParents)
  {
    ResourceFilter filter = createResourceFilter(path, patternStyle);
    filter.setIncludeParents(includeParents);
    return filter;
  }

  /**
   * <!-- begin-user-doc -->
   * @since 4.3
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public ExpressionFilter createExpressionFilter()
  {
    ExpressionFilterImpl expressionFilter = new ExpressionFilterImpl();
    return expressionFilter;
  }

  /**
   * @since 4.3
   */
  @Override
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
  @Override
  public NotFilter createNotFilter()
  {
    NotFilterImpl notFilter = new NotFilterImpl();
    return notFilter;
  }

  /**
   * @since 4.3
   */
  @Override
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
  @Override
  public AndFilter createAndFilter()
  {
    AndFilterImpl andFilter = new AndFilterImpl();
    return andFilter;
  }

  /**
   * @since 4.3
   */
  @Override
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
  @Override
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
  public PatternStyle createPatternStyleFromString(EDataType eDataType, String initialValue)
  {
    PatternStyle result = PatternStyle.get(initialValue);
    if (result == null)
    {
      throw new IllegalArgumentException("The value '" + initialValue + "' is not a valid enumerator of '" + eDataType.getName() + "'"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    }
    return result;
  }

  /**
   * <!-- begin-user-doc -->
   * @since 4.3
   * <!-- end-user-doc -->
   * @generated
   */
  public String convertPatternStyleToString(EDataType eDataType, Object instanceValue)
  {
    return instanceValue == null ? null : instanceValue.toString();
  }

  /**
   * @since 4.3
   */
  @Override
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
    {
      throw new IllegalArgumentException("The value '" + initialValue + "' is not a valid enumerator of '" + eDataType.getName() + "'"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    }
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
  @Override
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
