/*
 * Copyright (c) 2013, 2014, 2016, 2019, 2021 Eike Stepper (Loehne, Germany) and others.
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
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDUtil;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.CDORevisionData;
import org.eclipse.emf.cdo.common.revision.CDORevisionProvider;
import org.eclipse.emf.cdo.common.revision.CDORevisionUtil;
import org.eclipse.emf.cdo.common.revision.delta.CDOContainerFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDOFeatureDelta;
import org.eclipse.emf.cdo.eresource.CDOResourceNode;
import org.eclipse.emf.cdo.eresource.EresourcePackage;
import org.eclipse.emf.cdo.security.PatternStyle;
import org.eclipse.emf.cdo.security.ResourceFilter;
import org.eclipse.emf.cdo.security.SecurityPackage;
import org.eclipse.emf.cdo.security.impl.PermissionImpl.CommitImpactContext;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevisionDelta;

import org.eclipse.net4j.util.StringUtil;
import org.eclipse.net4j.util.WrappedException;

import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.ecore.EClass;

import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Resource Filter</b></em>'.
 * @since 4.3
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.security.impl.ResourceFilterImpl#getPath <em>Path</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.security.impl.ResourceFilterImpl#getPatternStyle <em>Pattern Style</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.security.impl.ResourceFilterImpl#isFolders <em>Folders</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.security.impl.ResourceFilterImpl#isTextResources <em>Text Resources</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.security.impl.ResourceFilterImpl#isBinaryResources <em>Binary Resources</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.security.impl.ResourceFilterImpl#isModelResources <em>Model Resources</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.security.impl.ResourceFilterImpl#isModelObjects <em>Model Objects</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.security.impl.ResourceFilterImpl#isIncludeParents <em>Include Parents</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.security.impl.ResourceFilterImpl#isIncludeRoot <em>Include Root</em>}</li>
 * </ul>
 *
 * @generated
 */
public class ResourceFilterImpl extends PermissionFilterImpl implements ResourceFilter
{
  private static final String USER_TOKEN = "${user}";

  private static final int USER_TOKEN_NONE = -1;

  private static final int USER_TOKEN_UNINITIALIZED = -2;

  private transient int userTokenPos = USER_TOKEN_UNINITIALIZED;

  private transient BasicEList<Matcher> matchers;

  private transient int lastVersion;

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
  @Override
  public String getPath()
  {
    return (String)eGet(SecurityPackage.Literals.RESOURCE_FILTER__PATH, true);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  @Override
  public ResourceFilter setPath(String newPath)
  {
    eSet(SecurityPackage.Literals.RESOURCE_FILTER__PATH, newPath);
    return this;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public PatternStyle getPatternStyle()
  {
    return (PatternStyle)eGet(SecurityPackage.Literals.RESOURCE_FILTER__PATTERN_STYLE, true);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  @Override
  public ResourceFilter setPatternStyle(PatternStyle newPatternStyle)
  {
    eSet(SecurityPackage.Literals.RESOURCE_FILTER__PATTERN_STYLE, newPatternStyle);
    return this;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public boolean isFolders()
  {
    return (Boolean)eGet(SecurityPackage.Literals.RESOURCE_FILTER__FOLDERS, true);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  @Override
  public ResourceFilter setFolders(boolean newFolders)
  {
    eSet(SecurityPackage.Literals.RESOURCE_FILTER__FOLDERS, newFolders);
    return this;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public boolean isModelResources()
  {
    return (Boolean)eGet(SecurityPackage.Literals.RESOURCE_FILTER__MODEL_RESOURCES, true);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  @Override
  public ResourceFilter setModelResources(boolean newModelResources)
  {
    eSet(SecurityPackage.Literals.RESOURCE_FILTER__MODEL_RESOURCES, newModelResources);
    return this;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public boolean isModelObjects()
  {
    return (Boolean)eGet(SecurityPackage.Literals.RESOURCE_FILTER__MODEL_OBJECTS, true);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  @Override
  public ResourceFilter setModelObjects(boolean newModelObjects)
  {
    eSet(SecurityPackage.Literals.RESOURCE_FILTER__MODEL_OBJECTS, newModelObjects);
    return this;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public boolean isIncludeParents()
  {
    return (Boolean)eGet(SecurityPackage.Literals.RESOURCE_FILTER__INCLUDE_PARENTS, true);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  @Override
  public ResourceFilter setIncludeParents(boolean newIncludeParents)
  {
    eSet(SecurityPackage.Literals.RESOURCE_FILTER__INCLUDE_PARENTS, newIncludeParents);
    return this;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public boolean isIncludeRoot()
  {
    return (Boolean)eGet(SecurityPackage.Literals.RESOURCE_FILTER__INCLUDE_ROOT, true);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  @Override
  public ResourceFilter setIncludeRoot(boolean newIncludeRoot)
  {
    eSet(SecurityPackage.Literals.RESOURCE_FILTER__INCLUDE_ROOT, newIncludeRoot);
    return this;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public boolean isTextResources()
  {
    return (Boolean)eGet(SecurityPackage.Literals.RESOURCE_FILTER__TEXT_RESOURCES, true);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  @Override
  public ResourceFilter setTextResources(boolean newTextResources)
  {
    eSet(SecurityPackage.Literals.RESOURCE_FILTER__TEXT_RESOURCES, newTextResources);
    return this;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public boolean isBinaryResources()
  {
    return (Boolean)eGet(SecurityPackage.Literals.RESOURCE_FILTER__BINARY_RESOURCES, true);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  @Override
  public ResourceFilter setBinaryResources(boolean newBinaryResources)
  {
    eSet(SecurityPackage.Literals.RESOURCE_FILTER__BINARY_RESOURCES, newBinaryResources);
    return this;
  }

  @Override
  public boolean isImpacted(CommitImpactContext context)
  {
    return isResourceTreeImpacted(context);
  }

  @Override
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

  protected String formatOperator()
  {
    PatternStyle patternStyle = getPatternStyle();
    switch (patternStyle)
    {
    case EXACT:
      return " == ";

    case TREE:
      return " >= ";

    case ANT:
    case REGEX:
      return " ~= ";

    default:
      throw new IllegalStateException("Unhandled pattern style: " + patternStyle);
    }
  }

  @Override
  protected boolean filter(CDORevision revision, CDORevisionProvider revisionProvider, CDOBranchPoint securityContext, int level) throws Exception
  {
    if (!preChecks(revision, revisionProvider))
    {
      return false;
    }

    String revisionPath = CDORevisionUtil.getResourceNodePath(revision, revisionProvider);

    BasicEList<Matcher> list = getMatchers();
    Object[] matchers = list.data();

    int length = list.size();
    for (int i = 0; i < length; i++)
    {
      Matcher matcher = (Matcher)matchers[i];
      if (matcher.matches(revisionPath))
      {
        return true;
      }
    }

    return false;
  }

  private boolean preChecks(CDORevision revision, CDORevisionProvider revisionProvider)
  {
    if (revisionProvider == null)
    {
      return false;
    }

    EClass eClass = revision.getEClass();
    boolean resourceNode = false;

    // Check folders
    if (eClass == EresourcePackage.Literals.CDO_RESOURCE_FOLDER)
    {
      resourceNode = true;
      if (!isFolders())
      {
        return false;
      }
    }

    // Check model resources
    if (eClass == EresourcePackage.Literals.CDO_RESOURCE)
    {
      resourceNode = true;

      CDORevisionData revisionData = revision.data();
      boolean rootResource = CDOIDUtil.isNull((CDOID)revisionData.getContainerID()) && CDOIDUtil.isNull(revisionData.getResourceID());

      if (rootResource)
      {
        if (!isIncludeRoot())
        {
          return false;
        }
      }
      else
      {
        if (!isModelResources())
        {
          return false;
        }
      }
    }

    // Check text resources
    if (eClass == EresourcePackage.Literals.CDO_TEXT_RESOURCE)
    {
      resourceNode = true;
      if (!isTextResources())
      {
        return false;
      }
    }

    // Check binary resources
    if (eClass == EresourcePackage.Literals.CDO_BINARY_RESOURCE)
    {
      resourceNode = true;
      if (!isBinaryResources())
      {
        return false;
      }
    }

    // Check model objects
    boolean modelObject = !resourceNode;
    if (modelObject && !isModelObjects())
    {
      return false;
    }

    return true;
  }

  private BasicEList<Matcher> getMatchers()
  {
    InternalCDORevision revision = cdoRevision();
    if (revision != null)
    {
      int currentVersion = revision.getVersion();
      if (currentVersion > lastVersion)
      {
        userTokenPos = USER_TOKEN_UNINITIALIZED;
        matchers = null;
        lastVersion = currentVersion;
      }

      if (matchers != null)
      {
        return matchers;
      }
    }

    String path = getPath();
    PatternStyle patternStyle = getPatternStyle();
    boolean includeParents = isIncludeParents();

    if (userTokenPos == USER_TOKEN_UNINITIALIZED || revision == null)
    {
      userTokenPos = path.indexOf(USER_TOKEN);
    }

    if (userTokenPos != USER_TOKEN_NONE)
    {
      String user = getUser();
      if (user == null || user.length() == 0)
      {
        throw new IllegalStateException("User required for evaluation of path " + path);
      }

      path = path.substring(0, userTokenPos) + user + path.substring(userTokenPos + USER_TOKEN.length());
    }

    BasicEList<Matcher> list = new BasicEList<>(1);
    getMatchers(list, path, patternStyle, includeParents);

    if (userTokenPos == USER_TOKEN_NONE)
    {
      // Cache matchers if no user token is specified
      matchers = list;
    }

    return list;
  }

  private void getMatchers(BasicEList<Matcher> matchers, String path, PatternStyle patternStyle, boolean includeParents)
  {
    Matcher matcher = createMatcher(path, patternStyle);
    matchers.add(matcher);

    if (includeParents)
    {
      int pos = path.lastIndexOf("/");
      if (pos != -1)
      {
        path = path.substring(0, pos);
        patternStyle = matcher.getParentPatternStyle();

        getMatchers(matchers, path, patternStyle, includeParents);
      }
    }
  }

  private Matcher createMatcher(String path, PatternStyle patternStyle)
  {
    switch (patternStyle)
    {
    case EXACT:
      return new ExactMatcher(path);

    case TREE:
      return new TreeMatcher(path);

    case ANT:
      return new AntMatcher(path);

    case REGEX:
      return new RegexMatcher(path);

    default:
      throw new IllegalStateException("Unhandled pattern style: " + patternStyle);
    }
  }

  public static boolean isResourceTreeImpacted(CommitImpactContext context)
  {
    InternalCDORevisionDelta[] revisionDeltas = (InternalCDORevisionDelta[])context.getDirtyObjectDeltas();

    for (int i = 0; i < revisionDeltas.length; i++)
    {
      InternalCDORevisionDelta revisionDelta = revisionDeltas[i];

      // Any tree move might impact this permission
      CDOFeatureDelta containerDelta = revisionDelta.getFeatureDelta(CDOContainerFeatureDelta.CONTAINER_FEATURE);
      if (containerDelta != null)
      {
        return true;
      }

      // Any change of a resource node name might impact this permission
      CDOFeatureDelta nameDelta = revisionDelta.getFeatureDelta(EresourcePackage.Literals.CDO_RESOURCE_NODE__NAME);
      if (nameDelta != null)
      {
        return true;
      }
    }

    return false;
  }

  /**
   * Matches the path of a {@link CDOResourceNode resource node}.
   *
   * @author Eike Stepper
   */
  public interface Matcher
  {
    public boolean matches(String revisionPath);

    public PatternStyle getParentPatternStyle();
  }

  /**
   * Matches the path of a {@link CDOResourceNode resource node} against a {@link #path} string.
   *
   * @author Eike Stepper
   */
  protected static abstract class PathMatcher implements Matcher
  {
    protected final String path;

    public PathMatcher(String path)
    {
      this.path = path == null || path.length() == 0 ? "/" : path;
    }

    @Override
    public String toString()
    {
      return getClass().getSimpleName() + "[" + path + "]";
    }
  }

  /**
   * @author Eike Stepper
   */
  protected static class ExactMatcher extends PathMatcher
  {
    public ExactMatcher(String path)
    {
      super(path);
    }

    @Override
    public boolean matches(String revisionPath)
    {
      return path.equals(revisionPath);
    }

    @Override
    public PatternStyle getParentPatternStyle()
    {
      return PatternStyle.EXACT;
    }
  }

  /**
   * @author Eike Stepper
   */
  protected static class TreeMatcher extends PathMatcher
  {
    public TreeMatcher(String path)
    {
      super(path);
    }

    @Override
    public boolean matches(String revisionPath)
    {
      if (revisionPath == null)
      {
        return path.length() == 0;
      }

      return revisionPath.startsWith(path);
    }

    @Override
    public PatternStyle getParentPatternStyle()
    {
      return PatternStyle.EXACT;
    }
  }

  /**
   * @author Eike Stepper
   */
  protected static class AntMatcher extends PathMatcher
  {
    public AntMatcher(String path)
    {
      super(path);
    }

    @Override
    public boolean matches(String revisionPath)
    {
      return StringUtil.glob(path, revisionPath);
    }

    @Override
    public PatternStyle getParentPatternStyle()
    {
      return PatternStyle.ANT;
    }
  }

  /**
   * @author Eike Stepper
   */
  protected static class RegexMatcher implements Matcher
  {
    private final Pattern pattern;

    public RegexMatcher(String path)
    {
      try
      {
        pattern = Pattern.compile(path);
      }
      catch (PatternSyntaxException ex)
      {
        throw WrappedException.wrap(ex);
      }
    }

    @Override
    public boolean matches(String revisionPath)
    {
      return pattern.matcher(revisionPath).matches();
    }

    @Override
    public PatternStyle getParentPatternStyle()
    {
      return PatternStyle.REGEX;
    }

    @Override
    public String toString()
    {
      return getClass().getSimpleName() + "[" + pattern.pattern() + "]";
    }
  }

} // ResourceFilterImpl
