/**
 */
package org.eclipse.emf.cdo.security.impl;

import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.CDORevisionProvider;
import org.eclipse.emf.cdo.common.revision.CDORevisionUtil;
import org.eclipse.emf.cdo.security.ResourceCheck;
import org.eclipse.emf.cdo.security.SecurityPackage;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EStructuralFeature;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Resource Check</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.security.impl.ResourceCheckImpl#getPattern <em>Pattern</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class ResourceCheckImpl extends CheckImpl implements ResourceCheck
{
  private Pattern pattern;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected ResourceCheckImpl()
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
    return SecurityPackage.Literals.RESOURCE_CHECK;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public String getPattern()
  {
    return (String)eGet(SecurityPackage.Literals.RESOURCE_CHECK__PATTERN, true);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setPattern(String newPattern)
  {
    eSet(SecurityPackage.Literals.RESOURCE_CHECK__PATTERN, newPattern);
  }

  @Override
  public void eSet(EStructuralFeature eFeature, Object newValue)
  {
    super.eSet(eFeature, newValue);
    if (eFeature == SecurityPackage.Literals.RESOURCE_CHECK__PATTERN)
    {
      String value = (String)newValue;
      pattern = compilePattern(value);
    }
  }

  private Pattern compilePattern(String value)
  {
    if (value == null)
    {
      return null;
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

  public boolean isApplicable(CDORevision revision, CDORevisionProvider revisionProvider, CDOBranchPoint securityContext)
  {
    if (pattern == null)
    {
      return false;
    }

    if (revisionProvider == null)
    {
      return false;
    }

    String path = CDORevisionUtil.getResourceNodePath(revision, revisionProvider);

    Matcher matcher = pattern.matcher(path);
    return matcher.matches();
  }

} // ResourceCheckImpl
