/*
 * Copyright (c) 2013 Eike Stepper (Berlin, Germany) and others.
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
import org.eclipse.emf.cdo.security.Inclusion;
import org.eclipse.emf.cdo.security.ResourceFilter;
import org.eclipse.emf.cdo.security.SecurityPackage;

import org.eclipse.emf.ecore.EClass;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Resource Filter</b></em>'.
 * @since 4.3
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.security.impl.ResourceFilterImpl#getPath <em>Path</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.security.impl.ResourceFilterImpl#getInclusion <em>Inclusion</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class ResourceFilterImpl extends PermissionFilterImpl implements ResourceFilter
{
  private static final String USER_TOKEN = "${user}";

  private static final Pattern OMNI_PATTERN = Pattern.compile(".*");

  private Pattern pattern;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected ResourceFilterImpl()
  {
    super();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  protected EClass eStaticClass()
  {
    return SecurityPackage.Literals.RESOURCE_FILTER;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public String getPath()
  {
    return (String)eGet(SecurityPackage.Literals.RESOURCE_FILTER__PATH, true);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setPath(String newPath)
  {
    eSet(SecurityPackage.Literals.RESOURCE_FILTER__PATH, newPath);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public Inclusion getInclusion()
  {
    return (Inclusion)eGet(SecurityPackage.Literals.RESOURCE_FILTER__INCLUSION, true);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setInclusion(Inclusion newInclusion)
  {
    eSet(SecurityPackage.Literals.RESOURCE_FILTER__INCLUSION, newInclusion);
  }

  @Override
  protected boolean filter(CDORevision revision, CDORevisionProvider revisionProvider, CDOBranchPoint securityContext,
      int level) throws Exception
  {
    if (revisionProvider == null)
    {
      return false;
    }

    Inclusion inclusion = getInclusion();
    switch (inclusion)
    {
    case EXACT:
      return includesExact(revision, revisionProvider);

    case EXACT_AND_UP:
      return includesExactAndUp(revision, revisionProvider);

    case EXACT_AND_DOWN:
      return includesExactAndDown(revision, revisionProvider);

    case REGEX:
      return includesRegex(revision, revisionProvider);

    default:
      throw new IllegalStateException("Unhandled inclusion value: " + inclusion);
    }
  }

  private boolean includesExact(CDORevision revision, CDORevisionProvider revisionProvider)
  {
    if (!revision.isResourceNode())
    {
      return false;
    }

    String revisionPath = CDORevisionUtil.getResourceNodePath(revision, revisionProvider);
    String path = getSubstitutedPath();

    return revisionPath.equals(path);
  }

  private boolean includesExactAndUp(CDORevision revision, CDORevisionProvider revisionProvider)
  {
    if (!revision.isResourceNode())
    {
      return false;
    }

    String revisionPath = CDORevisionUtil.getResourceNodePath(revision, revisionProvider);
    String path = getSubstitutedPath();

    int length = revisionPath.length();
    if (length > path.length())
    {
      return false;
    }

    path = path.substring(0, length);
    return revisionPath.equals(path);
  }

  private boolean includesExactAndDown(CDORevision revision, CDORevisionProvider revisionProvider)
  {
    String revisionPath = CDORevisionUtil.getResourceNodePath(revision, revisionProvider);
    String path = getSubstitutedPath();

    int length = path.length();
    if (length > revisionPath.length())
    {
      return false;
    }

    revisionPath = revisionPath.substring(0, length);
    return path.equals(revisionPath);
  }

  private boolean includesRegex(CDORevision revision, CDORevisionProvider revisionProvider)
  {
    if (pattern == null)
    {
      String path = getSubstitutedPath();
      pattern = compilePattern(path);

      if (pattern == null)
      {
        return false;
      }
    }

    if (pattern == OMNI_PATTERN)
    {
      return true;
    }

    String revisionPath = CDORevisionUtil.getResourceNodePath(revision, revisionProvider);

    Matcher matcher = pattern.matcher(revisionPath);
    return matcher.matches();
  }

  private String getSubstitutedPath()
  {
    String path = getPath();
    int pos = path.indexOf(USER_TOKEN);
    if (pos != -1)
    {
      String user = getUser();
      if (user == null || user.length() == 0)
      {
        throw new IllegalStateException("User required for evaluation of path " + path);
      }

      path = path.substring(0, pos) + user + path.substring(pos + USER_TOKEN.length());
    }

    return path;
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

  public String format()
  {
    String label = "?";

    String path = getPath();
    if (path != null)
    {
      if (!path.startsWith("/"))
      {
        path = "/" + path;
      }

      label = path;
    }

    String operator = formatOperator();
    return "resource" + operator + label;
  }

  private String formatOperator()
  {
    Inclusion inclusion = getInclusion();
    switch (inclusion)
    {
    case EXACT:
      return " == ";

    case EXACT_AND_UP:
      return " <= ";

    case EXACT_AND_DOWN:
      return " >= ";

    case REGEX:
      return " >~ ";

    default:
      throw new IllegalStateException("Unhandled inclusion value: " + inclusion);
    }
  }
} // ResourceFilterImpl
