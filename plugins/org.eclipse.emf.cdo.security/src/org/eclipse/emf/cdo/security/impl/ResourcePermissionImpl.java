/*
 * Copyright (c) 2012, 2013, 2015, 2016, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.security.impl;

import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.CDORevisionProvider;
import org.eclipse.emf.cdo.common.revision.CDORevisionUtil;
import org.eclipse.emf.cdo.security.ResourcePermission;
import org.eclipse.emf.cdo.security.SecurityPackage;

import org.eclipse.emf.ecore.EClass;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Resource Permission</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.security.impl.ResourcePermissionImpl#getPattern <em>Pattern</em>}</li>
 * </ul>
 *
 * @generated
 */
@Deprecated
public class ResourcePermissionImpl extends PermissionImpl implements ResourcePermission
{
  private static final Pattern OMNI_PATTERN = Pattern.compile(".*");

  private Pattern pattern;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Deprecated
  protected ResourcePermissionImpl()
  {
    super();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Deprecated
  @Override
  protected EClass eStaticClass()
  {
    return SecurityPackage.Literals.RESOURCE_PERMISSION;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Deprecated
  @Override
  public String getPattern()
  {
    return (String)eGet(SecurityPackage.Literals.RESOURCE_PERMISSION__PATTERN, true);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Deprecated
  @Override
  public void setPattern(String newPattern)
  {
    eSet(SecurityPackage.Literals.RESOURCE_PERMISSION__PATTERN, newPattern);
  }

  @Deprecated
  @Override
  public boolean isApplicable(CDORevision revision, CDORevisionProvider revisionProvider, CDOBranchPoint securityContext)
  {
    if (pattern == null)
    {
      String str = getPattern();
      pattern = compilePattern(str);

      if (pattern == null)
      {
        return false;
      }
    }

    if (pattern == OMNI_PATTERN)
    {
      return true;
    }

    if (revisionProvider == null)
    {
      return false;
    }

    String path = CDORevisionUtil.getResourceNodePath(revision, revisionProvider);

    Matcher matcher = pattern.matcher(path);
    return matcher.matches();
  }

  @Deprecated
  @Override
  public boolean isImpacted(CommitImpactContext context)
  {
    return ResourceFilterImpl.isResourceTreeImpacted(context);
  }

  private Pattern compilePattern(String value)
  {
    if (value == null)
    {
      return null;
    }

    if (value.equals(OMNI_PATTERN.pattern()))
    {
      return OMNI_PATTERN;
    }

    try
    {
      return Pattern.compile(value);
    }
    catch (PatternSyntaxException ex)
    {
      return null;
    }
  }

} // ResourcePermissionImpl
