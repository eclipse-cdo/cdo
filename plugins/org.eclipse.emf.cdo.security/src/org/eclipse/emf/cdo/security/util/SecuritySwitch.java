/*
 * Copyright (c) 2012, 2013, 2015, 2016 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.security.util;

import org.eclipse.emf.cdo.etypes.ModelElement;
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
import org.eclipse.emf.cdo.security.Permission;
import org.eclipse.emf.cdo.security.PermissionFilter;
import org.eclipse.emf.cdo.security.Realm;
import org.eclipse.emf.cdo.security.ResourceFilter;
import org.eclipse.emf.cdo.security.ResourcePermission;
import org.eclipse.emf.cdo.security.Role;
import org.eclipse.emf.cdo.security.SecurityElement;
import org.eclipse.emf.cdo.security.SecurityItem;
import org.eclipse.emf.cdo.security.SecurityPackage;
import org.eclipse.emf.cdo.security.User;
import org.eclipse.emf.cdo.security.UserPassword;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;

import java.util.List;

//import org.eclipse.emf.cdo.security.*;

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
 * @see org.eclipse.emf.cdo.security.SecurityPackage
 * @generated
 */
@SuppressWarnings("deprecation")
public class SecuritySwitch<T>
{
  /**
   * The cached model package
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected static SecurityPackage modelPackage;

  /**
   * Creates an instance of the switch.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public SecuritySwitch()
  {
    if (modelPackage == null)
    {
      modelPackage = SecurityPackage.eINSTANCE;
    }
  }

  /**
   * Calls <code>caseXXX</code> for each class of the model until one returns a non null result; it yields that result.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the first non-null result returned by a <code>caseXXX</code> call.
   * @generated
   */
  public T doSwitch(EObject theEObject)
  {
    return doSwitch(theEObject.eClass(), theEObject);
  }

  /**
   * Calls <code>caseXXX</code> for each class of the model until one returns a non null result; it yields that result.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the first non-null result returned by a <code>caseXXX</code> call.
   * @generated
   */
  protected T doSwitch(EClass theEClass, EObject theEObject)
  {
    if (theEClass.eContainer() == modelPackage)
    {
      return doSwitch(theEClass.getClassifierID(), theEObject);
    }
    else
    {
      List<EClass> eSuperTypes = theEClass.getESuperTypes();
      return eSuperTypes.isEmpty() ? defaultCase(theEObject) : doSwitch(eSuperTypes.get(0), theEObject);
    }
  }

  /**
   * Calls <code>caseXXX</code> for each class of the model until one returns a non null result; it yields that result.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the first non-null result returned by a <code>caseXXX</code> call.
   * @generated
   */
  protected T doSwitch(int classifierID, EObject theEObject)
  {
    switch (classifierID)
    {
    case SecurityPackage.SECURITY_ELEMENT:
    {
      SecurityElement securityElement = (SecurityElement)theEObject;
      T result = caseSecurityElement(securityElement);
      if (result == null)
      {
        result = caseModelElement(securityElement);
      }
      if (result == null)
      {
        result = defaultCase(theEObject);
      }
      return result;
    }
    case SecurityPackage.SECURITY_ITEM:
    {
      SecurityItem securityItem = (SecurityItem)theEObject;
      T result = caseSecurityItem(securityItem);
      if (result == null)
      {
        result = caseSecurityElement(securityItem);
      }
      if (result == null)
      {
        result = caseModelElement(securityItem);
      }
      if (result == null)
      {
        result = defaultCase(theEObject);
      }
      return result;
    }
    case SecurityPackage.REALM:
    {
      Realm realm = (Realm)theEObject;
      T result = caseRealm(realm);
      if (result == null)
      {
        result = caseSecurityElement(realm);
      }
      if (result == null)
      {
        result = caseModelElement(realm);
      }
      if (result == null)
      {
        result = defaultCase(theEObject);
      }
      return result;
    }
    case SecurityPackage.DIRECTORY:
    {
      Directory directory = (Directory)theEObject;
      T result = caseDirectory(directory);
      if (result == null)
      {
        result = caseSecurityItem(directory);
      }
      if (result == null)
      {
        result = caseSecurityElement(directory);
      }
      if (result == null)
      {
        result = caseModelElement(directory);
      }
      if (result == null)
      {
        result = defaultCase(theEObject);
      }
      return result;
    }
    case SecurityPackage.ROLE:
    {
      Role role = (Role)theEObject;
      T result = caseRole(role);
      if (result == null)
      {
        result = caseSecurityItem(role);
      }
      if (result == null)
      {
        result = caseSecurityElement(role);
      }
      if (result == null)
      {
        result = caseModelElement(role);
      }
      if (result == null)
      {
        result = defaultCase(theEObject);
      }
      return result;
    }
    case SecurityPackage.ASSIGNEE:
    {
      Assignee assignee = (Assignee)theEObject;
      T result = caseAssignee(assignee);
      if (result == null)
      {
        result = caseSecurityItem(assignee);
      }
      if (result == null)
      {
        result = caseSecurityElement(assignee);
      }
      if (result == null)
      {
        result = caseModelElement(assignee);
      }
      if (result == null)
      {
        result = defaultCase(theEObject);
      }
      return result;
    }
    case SecurityPackage.GROUP:
    {
      Group group = (Group)theEObject;
      T result = caseGroup(group);
      if (result == null)
      {
        result = caseAssignee(group);
      }
      if (result == null)
      {
        result = caseSecurityItem(group);
      }
      if (result == null)
      {
        result = caseSecurityElement(group);
      }
      if (result == null)
      {
        result = caseModelElement(group);
      }
      if (result == null)
      {
        result = defaultCase(theEObject);
      }
      return result;
    }
    case SecurityPackage.USER:
    {
      User user = (User)theEObject;
      T result = caseUser(user);
      if (result == null)
      {
        result = caseAssignee(user);
      }
      if (result == null)
      {
        result = caseSecurityItem(user);
      }
      if (result == null)
      {
        result = caseSecurityElement(user);
      }
      if (result == null)
      {
        result = caseModelElement(user);
      }
      if (result == null)
      {
        result = defaultCase(theEObject);
      }
      return result;
    }
    case SecurityPackage.USER_PASSWORD:
    {
      UserPassword userPassword = (UserPassword)theEObject;
      T result = caseUserPassword(userPassword);
      if (result == null)
      {
        result = defaultCase(theEObject);
      }
      return result;
    }
    case SecurityPackage.PERMISSION:
    {
      Permission permission = (Permission)theEObject;
      T result = casePermission(permission);
      if (result == null)
      {
        result = defaultCase(theEObject);
      }
      return result;
    }
    case SecurityPackage.CLASS_PERMISSION:
    {
      ClassPermission classPermission = (ClassPermission)theEObject;
      T result = caseClassPermission(classPermission);
      if (result == null)
      {
        result = casePermission(classPermission);
      }
      if (result == null)
      {
        result = defaultCase(theEObject);
      }
      return result;
    }
    case SecurityPackage.PACKAGE_PERMISSION:
    {
      PackagePermission packagePermission = (PackagePermission)theEObject;
      T result = casePackagePermission(packagePermission);
      if (result == null)
      {
        result = casePermission(packagePermission);
      }
      if (result == null)
      {
        result = defaultCase(theEObject);
      }
      return result;
    }
    case SecurityPackage.RESOURCE_PERMISSION:
    {
      ResourcePermission resourcePermission = (ResourcePermission)theEObject;
      T result = caseResourcePermission(resourcePermission);
      if (result == null)
      {
        result = casePermission(resourcePermission);
      }
      if (result == null)
      {
        result = defaultCase(theEObject);
      }
      return result;
    }
    case SecurityPackage.OBJECT_PERMISSION:
    {
      ObjectPermission objectPermission = (ObjectPermission)theEObject;
      T result = caseObjectPermission(objectPermission);
      if (result == null)
      {
        result = casePermission(objectPermission);
      }
      if (result == null)
      {
        result = defaultCase(theEObject);
      }
      return result;
    }
    case SecurityPackage.FILTER_PERMISSION:
    {
      FilterPermission filterPermission = (FilterPermission)theEObject;
      T result = caseFilterPermission(filterPermission);
      if (result == null)
      {
        result = casePermission(filterPermission);
      }
      if (result == null)
      {
        result = defaultCase(theEObject);
      }
      return result;
    }
    case SecurityPackage.PERMISSION_FILTER:
    {
      PermissionFilter permissionFilter = (PermissionFilter)theEObject;
      T result = casePermissionFilter(permissionFilter);
      if (result == null)
      {
        result = defaultCase(theEObject);
      }
      return result;
    }
    case SecurityPackage.LINKED_FILTER:
    {
      LinkedFilter linkedFilter = (LinkedFilter)theEObject;
      T result = caseLinkedFilter(linkedFilter);
      if (result == null)
      {
        result = casePermissionFilter(linkedFilter);
      }
      if (result == null)
      {
        result = defaultCase(theEObject);
      }
      return result;
    }
    case SecurityPackage.PACKAGE_FILTER:
    {
      PackageFilter packageFilter = (PackageFilter)theEObject;
      T result = casePackageFilter(packageFilter);
      if (result == null)
      {
        result = casePermissionFilter(packageFilter);
      }
      if (result == null)
      {
        result = defaultCase(theEObject);
      }
      return result;
    }
    case SecurityPackage.CLASS_FILTER:
    {
      ClassFilter classFilter = (ClassFilter)theEObject;
      T result = caseClassFilter(classFilter);
      if (result == null)
      {
        result = casePermissionFilter(classFilter);
      }
      if (result == null)
      {
        result = defaultCase(theEObject);
      }
      return result;
    }
    case SecurityPackage.RESOURCE_FILTER:
    {
      ResourceFilter resourceFilter = (ResourceFilter)theEObject;
      T result = caseResourceFilter(resourceFilter);
      if (result == null)
      {
        result = casePermissionFilter(resourceFilter);
      }
      if (result == null)
      {
        result = defaultCase(theEObject);
      }
      return result;
    }
    case SecurityPackage.OBJECT_FILTER:
    {
      ObjectFilter objectFilter = (ObjectFilter)theEObject;
      T result = caseObjectFilter(objectFilter);
      if (result == null)
      {
        result = casePermissionFilter(objectFilter);
      }
      if (result == null)
      {
        result = defaultCase(theEObject);
      }
      return result;
    }
    case SecurityPackage.EXPRESSION_FILTER:
    {
      ExpressionFilter expressionFilter = (ExpressionFilter)theEObject;
      T result = caseExpressionFilter(expressionFilter);
      if (result == null)
      {
        result = caseObjectFilter(expressionFilter);
      }
      if (result == null)
      {
        result = casePermissionFilter(expressionFilter);
      }
      if (result == null)
      {
        result = defaultCase(theEObject);
      }
      return result;
    }
    case SecurityPackage.COMBINED_FILTER:
    {
      CombinedFilter combinedFilter = (CombinedFilter)theEObject;
      T result = caseCombinedFilter(combinedFilter);
      if (result == null)
      {
        result = casePermissionFilter(combinedFilter);
      }
      if (result == null)
      {
        result = defaultCase(theEObject);
      }
      return result;
    }
    case SecurityPackage.NOT_FILTER:
    {
      NotFilter notFilter = (NotFilter)theEObject;
      T result = caseNotFilter(notFilter);
      if (result == null)
      {
        result = caseCombinedFilter(notFilter);
      }
      if (result == null)
      {
        result = casePermissionFilter(notFilter);
      }
      if (result == null)
      {
        result = defaultCase(theEObject);
      }
      return result;
    }
    case SecurityPackage.AND_FILTER:
    {
      AndFilter andFilter = (AndFilter)theEObject;
      T result = caseAndFilter(andFilter);
      if (result == null)
      {
        result = caseCombinedFilter(andFilter);
      }
      if (result == null)
      {
        result = casePermissionFilter(andFilter);
      }
      if (result == null)
      {
        result = defaultCase(theEObject);
      }
      return result;
    }
    case SecurityPackage.OR_FILTER:
    {
      OrFilter orFilter = (OrFilter)theEObject;
      T result = caseOrFilter(orFilter);
      if (result == null)
      {
        result = caseCombinedFilter(orFilter);
      }
      if (result == null)
      {
        result = casePermissionFilter(orFilter);
      }
      if (result == null)
      {
        result = defaultCase(theEObject);
      }
      return result;
    }
    default:
      return defaultCase(theEObject);
    }
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Element</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Element</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseSecurityElement(SecurityElement object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Realm</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Realm</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseRealm(Realm object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Directory</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Directory</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseDirectory(Directory object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Item</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Item</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseSecurityItem(SecurityItem object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Role</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Role</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseRole(Role object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Assignee</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Assignee</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseAssignee(Assignee object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Group</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Group</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseGroup(Group object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>User</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>User</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseUser(User object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>User Password</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>User Password</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseUserPassword(UserPassword object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Permission</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Permission</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T casePermission(Permission object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Class Permission</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Class Permission</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseClassPermission(ClassPermission object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Package Permission</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Package Permission</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T casePackagePermission(PackagePermission object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Resource Permission</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Resource Permission</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseResourcePermission(ResourcePermission object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Object Permission</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * @since 4.2
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Object Permission</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseObjectPermission(ObjectPermission object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Filter Permission</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * @since 4.3
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Filter Permission</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseFilterPermission(FilterPermission object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Permission Filter</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * @since 4.3
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Permission Filter</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T casePermissionFilter(PermissionFilter object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Linked Filter</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * @since 4.3
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Linked Filter</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseLinkedFilter(LinkedFilter object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Package Filter</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * @since 4.3
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Package Filter</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T casePackageFilter(PackageFilter object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Class Filter</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * @since 4.3
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Class Filter</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseClassFilter(ClassFilter object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Resource Filter</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * @since 4.3
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Resource Filter</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseResourceFilter(ResourceFilter object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Object Filter</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * @since 4.3
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Object Filter</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseObjectFilter(ObjectFilter object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Expression Filter</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * @since 4.3
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Expression Filter</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseExpressionFilter(ExpressionFilter object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Combined Filter</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * @since 4.3
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Combined Filter</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseCombinedFilter(CombinedFilter object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Not Filter</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * @since 4.3
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Not Filter</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseNotFilter(NotFilter object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>And Filter</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * @since 4.3
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>And Filter</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseAndFilter(AndFilter object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Or Filter</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * @since 4.3
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Or Filter</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseOrFilter(OrFilter object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Model Element</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Model Element</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseModelElement(ModelElement object)
  {
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
  public T defaultCase(EObject object)
  {
    return null;
  }

} // SecuritySwitch
