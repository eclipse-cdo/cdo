/*
 * Copyright (c) 2013, 2015, 2016, 2019, 2025 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Christian W. Damus (CEA LIST) - initial API and implementation
 */
package org.eclipse.emf.cdo.security.util;

import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.CDORevisionProvider;
import org.eclipse.emf.cdo.internal.security.bundle.OM;
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
import org.eclipse.emf.cdo.security.SecurityItem;
import org.eclipse.emf.cdo.security.SecurityPackage;
import org.eclipse.emf.cdo.security.User;
import org.eclipse.emf.cdo.security.UserPassword;

import org.eclipse.emf.common.util.Diagnostic;
import org.eclipse.emf.common.util.DiagnosticChain;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.ResourceLocator;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.util.EObjectValidator;

import java.util.Map;
import java.util.Queue;
import java.util.Set;

/**
 * <!-- begin-user-doc -->
 * The <b>Validator</b> for the model.
 * @since 4.3
 * <!-- end-user-doc -->
 * @see org.eclipse.emf.cdo.security.SecurityPackage
 * @generated
 */
@SuppressWarnings("deprecation")
public class SecurityValidator extends EObjectValidator
{
  /**
   * The cached model package
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public static final SecurityValidator INSTANCE = new SecurityValidator();

  /**
   * A constant for the {@link org.eclipse.emf.common.util.Diagnostic#getSource() source} of diagnostic {@link org.eclipse.emf.common.util.Diagnostic#getCode() codes} from this package.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.emf.common.util.Diagnostic#getSource()
   * @see org.eclipse.emf.common.util.Diagnostic#getCode()
   * @generated
   */
  public static final String DIAGNOSTIC_SOURCE = "org.eclipse.emf.cdo.security"; //$NON-NLS-1$

  /**
   * A constant with a fixed name that can be used as the base value for additional hand written constants.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private static final int GENERATED_DIAGNOSTIC_CODE_COUNT = 0;

  /**
   * A constant with a fixed name that can be used as the base value for additional hand written constants in a derived class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected static final int DIAGNOSTIC_CODE_COUNT = GENERATED_DIAGNOSTIC_CODE_COUNT;

  /**
   * Creates an instance of the switch.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public SecurityValidator()
  {
    super();
  }

  /**
   * Returns the package of this validator switch.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  protected EPackage getEPackage()
  {
    return SecurityPackage.eINSTANCE;
  }

  /**
   * Calls <code>validateXXX</code> for the corresponding classifier of the model.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  protected boolean validate(int classifierID, Object value, DiagnosticChain diagnostics, Map<Object, Object> context)
  {
    switch (classifierID)
    {
    case SecurityPackage.SECURITY_ELEMENT:
      return validateSecurityElement((SecurityElement)value, diagnostics, context);
    case SecurityPackage.SECURITY_ITEM:
      return validateSecurityItem((SecurityItem)value, diagnostics, context);
    case SecurityPackage.REALM:
      return validateRealm((Realm)value, diagnostics, context);
    case SecurityPackage.DIRECTORY:
      return validateDirectory((Directory)value, diagnostics, context);
    case SecurityPackage.ROLE:
      return validateRole((Role)value, diagnostics, context);
    case SecurityPackage.ASSIGNEE:
      return validateAssignee((Assignee)value, diagnostics, context);
    case SecurityPackage.GROUP:
      return validateGroup((Group)value, diagnostics, context);
    case SecurityPackage.USER:
      return validateUser((User)value, diagnostics, context);
    case SecurityPackage.USER_PASSWORD:
      return validateUserPassword((UserPassword)value, diagnostics, context);
    case SecurityPackage.PERMISSION:
      return validatePermission((Permission)value, diagnostics, context);
    case SecurityPackage.CLASS_PERMISSION:
      return validateClassPermission((ClassPermission)value, diagnostics, context);
    case SecurityPackage.PACKAGE_PERMISSION:
      return validatePackagePermission((PackagePermission)value, diagnostics, context);
    case SecurityPackage.RESOURCE_PERMISSION:
      return validateResourcePermission((ResourcePermission)value, diagnostics, context);
    case SecurityPackage.OBJECT_PERMISSION:
      return validateObjectPermission((ObjectPermission)value, diagnostics, context);
    case SecurityPackage.FILTER_PERMISSION:
      return validateFilterPermission((FilterPermission)value, diagnostics, context);
    case SecurityPackage.PERMISSION_FILTER:
      return validatePermissionFilter((PermissionFilter)value, diagnostics, context);
    case SecurityPackage.LINKED_FILTER:
      return validateLinkedFilter((LinkedFilter)value, diagnostics, context);
    case SecurityPackage.PACKAGE_FILTER:
      return validatePackageFilter((PackageFilter)value, diagnostics, context);
    case SecurityPackage.CLASS_FILTER:
      return validateClassFilter((ClassFilter)value, diagnostics, context);
    case SecurityPackage.RESOURCE_FILTER:
      return validateResourceFilter((ResourceFilter)value, diagnostics, context);
    case SecurityPackage.OBJECT_FILTER:
      return validateObjectFilter((ObjectFilter)value, diagnostics, context);
    case SecurityPackage.EXPRESSION_FILTER:
      return validateExpressionFilter((ExpressionFilter)value, diagnostics, context);
    case SecurityPackage.COMBINED_FILTER:
      return validateCombinedFilter((CombinedFilter)value, diagnostics, context);
    case SecurityPackage.NOT_FILTER:
      return validateNotFilter((NotFilter)value, diagnostics, context);
    case SecurityPackage.AND_FILTER:
      return validateAndFilter((AndFilter)value, diagnostics, context);
    case SecurityPackage.OR_FILTER:
      return validateOrFilter((OrFilter)value, diagnostics, context);
    case SecurityPackage.PATTERN_STYLE:
      return validatePatternStyle((PatternStyle)value, diagnostics, context);
    case SecurityPackage.ACCESS:
      return validateAccess((Access)value, diagnostics, context);
    case SecurityPackage.ACCESS_OBJECT:
      return validateAccessObject((Access)value, diagnostics, context);
    default:
      return true;
    }
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public boolean validateSecurityElement(SecurityElement securityElement, DiagnosticChain diagnostics, Map<Object, Object> context)
  {
    return validate_EveryDefaultConstraint(securityElement, diagnostics, context);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public boolean validateSecurityItem(SecurityItem securityItem, DiagnosticChain diagnostics, Map<Object, Object> context)
  {
    return validate_EveryDefaultConstraint(securityItem, diagnostics, context);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public boolean validateRealm(Realm realm, DiagnosticChain diagnostics, Map<Object, Object> context)
  {
    if (!validate_NoCircularContainment(realm, diagnostics, context))
    {
      return false;
    }
    boolean result = validate_EveryMultiplicityConforms(realm, diagnostics, context);
    if (result || diagnostics != null)
    {
      result &= validate_EveryDataValueConforms(realm, diagnostics, context);
    }
    if (result || diagnostics != null)
    {
      result &= validate_EveryReferenceIsContained(realm, diagnostics, context);
    }
    if (result || diagnostics != null)
    {
      result &= validate_EveryBidirectionalReferenceIsPaired(realm, diagnostics, context);
    }
    if (result || diagnostics != null)
    {
      result &= validate_EveryProxyResolves(realm, diagnostics, context);
    }
    if (result || diagnostics != null)
    {
      result &= validate_UniqueID(realm, diagnostics, context);
    }
    if (result || diagnostics != null)
    {
      result &= validate_EveryKeyUnique(realm, diagnostics, context);
    }
    if (result || diagnostics != null)
    {
      result &= validate_EveryMapEntryUnique(realm, diagnostics, context);
    }
    if (result || diagnostics != null)
    {
      result &= validateRealm_HasAdministrator(realm, diagnostics, context);
    }
    return result;
  }

  /**
   * Validates the HasAdministrator constraint of '<em>Realm</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public boolean validateRealm_HasAdministrator(Realm realm, DiagnosticChain diagnostics, Map<Object, Object> context)
  {
    final CDORevision realmRevision = realm.cdoRevision();
    final CDORevisionProvider revisionProvider = getRevisionProvider(realm, context);
    final CDOBranchPoint securityContext = getSecurityContext(realm, context);

    EList<User> allUsers = realm.getAllUsers();
    for (User user : allUsers)
    {
      if (user.getDefaultAccess() == Access.WRITE)
      {
        // Constraint is satisfied: this user can write anything at all in the repository
        return true;
      }

      for (Permission permission : user.getAllPermissions())
      {
        if (permission.getAccess() == Access.WRITE && permission.isApplicable(realmRevision, revisionProvider, securityContext))
        {
          // Constraint is satisfied: this user can write the realm
          return true;
        }
      }
    }

    if (diagnostics != null)
    {
      diagnostics.add(createDiagnostic(Diagnostic.ERROR, DIAGNOSTIC_SOURCE, 0, "_UI_Realm_hasAdministrator_diagnostic", //$NON-NLS-1$
          new Object[0], new Object[] { realm }, context));
    }
    return false;
  }

  protected <T> T getContextByClassKey(Map<Object, Object> context, Class<T> key, T defaultValue)
  {
    if (context != null)
    {
      Object value = context.get(key);
      if (key.isInstance(value))
      {
        return key.cast(value);
      }
    }

    return defaultValue;
  }

  protected CDORevisionProvider getRevisionProvider(Realm realm, Map<Object, Object> context)
  {
    return getContextByClassKey(context, CDORevisionProvider.class, realm.cdoView());
  }

  protected CDOBranchPoint getSecurityContext(Realm realm, Map<Object, Object> context)
  {
    return getContextByClassKey(context, CDOBranchPoint.class, realm.cdoView());
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public boolean validateDirectory(Directory directory, DiagnosticChain diagnostics, Map<Object, Object> context)
  {
    return validate_EveryDefaultConstraint(directory, diagnostics, context);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public boolean validateRole(Role role, DiagnosticChain diagnostics, Map<Object, Object> context)
  {
    return validate_EveryDefaultConstraint(role, diagnostics, context);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public boolean validateAssignee(Assignee assignee, DiagnosticChain diagnostics, Map<Object, Object> context)
  {
    return validate_EveryDefaultConstraint(assignee, diagnostics, context);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public boolean validateGroup(Group group, DiagnosticChain diagnostics, Map<Object, Object> context)
  {
    if (!validate_NoCircularContainment(group, diagnostics, context))
    {
      return false;
    }
    boolean result = validate_EveryMultiplicityConforms(group, diagnostics, context);
    if (result || diagnostics != null)
    {
      result &= validate_EveryDataValueConforms(group, diagnostics, context);
    }
    if (result || diagnostics != null)
    {
      result &= validate_EveryReferenceIsContained(group, diagnostics, context);
    }
    if (result || diagnostics != null)
    {
      result &= validate_EveryBidirectionalReferenceIsPaired(group, diagnostics, context);
    }
    if (result || diagnostics != null)
    {
      result &= validate_EveryProxyResolves(group, diagnostics, context);
    }
    if (result || diagnostics != null)
    {
      result &= validate_UniqueID(group, diagnostics, context);
    }
    if (result || diagnostics != null)
    {
      result &= validate_EveryKeyUnique(group, diagnostics, context);
    }
    if (result || diagnostics != null)
    {
      result &= validate_EveryMapEntryUnique(group, diagnostics, context);
    }
    if (result || diagnostics != null)
    {
      result &= validateGroup_AcyclicInheritance(group, diagnostics, context);
    }
    return result;
  }

  /**
   * Validates the AcyclicInheritance constraint of '<em>Group</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public boolean validateGroup_AcyclicInheritance(Group group, DiagnosticChain diagnostics, Map<Object, Object> context)
  {
    final Set<Group> visited = new java.util.HashSet<>();
    final Queue<Group> toVisit = new java.util.LinkedList<>();
    toVisit.offer(group);

    for (Group next = toVisit.poll(); next != null; next = toVisit.poll())
    {
      if (visited.add(next))
      {
        toVisit.addAll(next.getInheritedGroups());
      }
      else
      {
        // Detected a cycle
        if (diagnostics != null)
        {
          diagnostics.add(createDiagnostic(Diagnostic.ERROR, DIAGNOSTIC_SOURCE, 0, "_UI_Group_acyclicInheritance_diagnostic", //$NON-NLS-1$
              new Object[] { group.getId() }, new Object[] { group }, context));
        }

        return false;
      }
    }

    return true;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public boolean validateUser(User user, DiagnosticChain diagnostics, Map<Object, Object> context)
  {
    return validate_EveryDefaultConstraint(user, diagnostics, context);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public boolean validateUserPasswordGen(UserPassword userPassword, DiagnosticChain diagnostics, Map<Object, Object> context)
  {
    return validate_EveryDefaultConstraint(userPassword, diagnostics, context);
  }

  /**
   * No user, not even the Administrator, is permitted to read the properties of a
   * {@link UserPassword}, so this validation is a no-op.
   */
  public boolean validateUserPassword(UserPassword userPassword, DiagnosticChain diagnostics, Map<Object, Object> context)
  {
    return true;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public boolean validatePermission(Permission permission, DiagnosticChain diagnostics, Map<Object, Object> context)
  {
    return validate_EveryDefaultConstraint(permission, diagnostics, context);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public boolean validateClassPermission(ClassPermission classPermission, DiagnosticChain diagnostics, Map<Object, Object> context)
  {
    return validate_EveryDefaultConstraint(classPermission, diagnostics, context);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public boolean validatePackagePermission(PackagePermission packagePermission, DiagnosticChain diagnostics, Map<Object, Object> context)
  {
    return validate_EveryDefaultConstraint(packagePermission, diagnostics, context);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public boolean validateResourcePermission(ResourcePermission resourcePermission, DiagnosticChain diagnostics, Map<Object, Object> context)
  {
    return validate_EveryDefaultConstraint(resourcePermission, diagnostics, context);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public boolean validateObjectPermission(ObjectPermission objectPermission, DiagnosticChain diagnostics, Map<Object, Object> context)
  {
    return validate_EveryDefaultConstraint(objectPermission, diagnostics, context);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public boolean validateFilterPermission(FilterPermission filterPermission, DiagnosticChain diagnostics, Map<Object, Object> context)
  {
    return validate_EveryDefaultConstraint(filterPermission, diagnostics, context);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public boolean validatePermissionFilter(PermissionFilter permissionFilter, DiagnosticChain diagnostics, Map<Object, Object> context)
  {
    return validate_EveryDefaultConstraint(permissionFilter, diagnostics, context);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public boolean validateLinkedFilter(LinkedFilter linkedFilter, DiagnosticChain diagnostics, Map<Object, Object> context)
  {
    return validate_EveryDefaultConstraint(linkedFilter, diagnostics, context);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public boolean validatePackageFilter(PackageFilter packageFilter, DiagnosticChain diagnostics, Map<Object, Object> context)
  {
    return validate_EveryDefaultConstraint(packageFilter, diagnostics, context);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public boolean validateClassFilter(ClassFilter classFilter, DiagnosticChain diagnostics, Map<Object, Object> context)
  {
    return validate_EveryDefaultConstraint(classFilter, diagnostics, context);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public boolean validateResourceFilter(ResourceFilter resourceFilter, DiagnosticChain diagnostics, Map<Object, Object> context)
  {
    return validate_EveryDefaultConstraint(resourceFilter, diagnostics, context);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public boolean validateObjectFilter(ObjectFilter objectFilter, DiagnosticChain diagnostics, Map<Object, Object> context)
  {
    return validate_EveryDefaultConstraint(objectFilter, diagnostics, context);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public boolean validateExpressionFilter(ExpressionFilter expressionFilter, DiagnosticChain diagnostics, Map<Object, Object> context)
  {
    return validate_EveryDefaultConstraint(expressionFilter, diagnostics, context);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public boolean validateCombinedFilter(CombinedFilter combinedFilter, DiagnosticChain diagnostics, Map<Object, Object> context)
  {
    return validate_EveryDefaultConstraint(combinedFilter, diagnostics, context);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public boolean validateNotFilter(NotFilter notFilter, DiagnosticChain diagnostics, Map<Object, Object> context)
  {
    return validate_EveryDefaultConstraint(notFilter, diagnostics, context);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public boolean validateAndFilter(AndFilter andFilter, DiagnosticChain diagnostics, Map<Object, Object> context)
  {
    return validate_EveryDefaultConstraint(andFilter, diagnostics, context);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public boolean validateOrFilter(OrFilter orFilter, DiagnosticChain diagnostics, Map<Object, Object> context)
  {
    return validate_EveryDefaultConstraint(orFilter, diagnostics, context);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public boolean validatePatternStyle(PatternStyle patternStyle, DiagnosticChain diagnostics, Map<Object, Object> context)
  {
    return true;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public boolean validateAccess(Access access, DiagnosticChain diagnostics, Map<Object, Object> context)
  {
    return true;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public boolean validateAccessObject(Access accessObject, DiagnosticChain diagnostics, Map<Object, Object> context)
  {
    return true;
  }

  /**
   * Returns the resource locator that will be used to fetch messages for this validator's diagnostics.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  @Override
  public ResourceLocator getResourceLocator()
  {
    return OM.EMF_PLUGIN;
  }

} // SecurityValidator
