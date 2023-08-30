/**
 */
package org.eclipse.emf.cdo.lm.security.impl;

import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.CDORevisionProvider;
import org.eclipse.emf.cdo.lm.security.LMFilter;
import org.eclipse.emf.cdo.lm.security.LMSecurityPackage;
import org.eclipse.emf.cdo.security.impl.PermissionFilterImpl;
import org.eclipse.emf.cdo.security.impl.PermissionImpl.CommitImpactContext;
import org.eclipse.emf.cdo.security.util.AuthorizationContext;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;

import org.eclipse.emf.ecore.EClass;

import java.util.Map;
import java.util.Objects;
import java.util.regex.Pattern;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>LM Filter</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.lm.security.impl.LMFilterImpl#isRegex <em>Regex</em>}</li>
 * </ul>
 *
 * @generated
 */
public abstract class LMFilterImpl extends PermissionFilterImpl implements LMFilter
{
  /**
   * The default value of the '{@link #isRegex() <em>Regex</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #isRegex()
   * @generated
   * @ordered
   */
  protected static final boolean REGEX_EDEFAULT = false;

  private transient Pattern pattern;

  private transient int lastVersion;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected LMFilterImpl()
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
    return LMSecurityPackage.Literals.LM_FILTER;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public boolean isRegex()
  {
    return (Boolean)eDynamicGet(LMSecurityPackage.LM_FILTER__REGEX, LMSecurityPackage.Literals.LM_FILTER__REGEX, true, true);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setRegex(boolean newRegex)
  {
    eDynamicSet(LMSecurityPackage.LM_FILTER__REGEX, LMSecurityPackage.Literals.LM_FILTER__REGEX, newRegex);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Object eGet(int featureID, boolean resolve, boolean coreType)
  {
    switch (featureID)
    {
    case LMSecurityPackage.LM_FILTER__REGEX:
      return isRegex();
    }
    return super.eGet(featureID, resolve, coreType);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void eSet(int featureID, Object newValue)
  {
    switch (featureID)
    {
    case LMSecurityPackage.LM_FILTER__REGEX:
      setRegex((Boolean)newValue);
      return;
    }
    super.eSet(featureID, newValue);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void eUnset(int featureID)
  {
    switch (featureID)
    {
    case LMSecurityPackage.LM_FILTER__REGEX:
      setRegex(REGEX_EDEFAULT);
      return;
    }
    super.eUnset(featureID);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public boolean eIsSet(int featureID)
  {
    switch (featureID)
    {
    case LMSecurityPackage.LM_FILTER__REGEX:
      return isRegex() != REGEX_EDEFAULT;
    }
    return super.eIsSet(featureID);
  }

  /**
   * @ADDED
   */
  @Override
  public boolean isImpacted(CommitImpactContext context)
  {
    return false;
  }

  /**
   * @ADDED
   */
  @Override
  public String format()
  {
    String operator = isRegex() ? " ~= " : " == ";
    return getComparisonKey() + operator + getComparisonValue();
  }

  /**
   * @ADDED
   */
  @Override
  protected final boolean filter(CDORevision revision, CDORevisionProvider revisionProvider, CDOBranchPoint securityContext, int level) throws Exception
  {
    Map<String, Object> authorizationContext = AuthorizationContext.get();
    if (authorizationContext == null)
    {
      return false;
    }

    String actualComparisonValue = (String)authorizationContext.get(getComparisonKey());
    String expectedComparisonValue = getComparisonValue();

    if (isRegex())
    {
      Pattern pattern = getPattern(expectedComparisonValue);
      return pattern.matcher(actualComparisonValue).matches();
    }

    return Objects.equals(actualComparisonValue, expectedComparisonValue);
  }

  /**
   * @ADDED
   */
  protected abstract String getComparisonKey();

  /**
   * @ADDED
   */
  protected abstract String getComparisonValue();

  /**
   * @ADDED
   */
  private Pattern getPattern(String expectedComparisonValue)
  {
    InternalCDORevision revision = cdoRevision();
    if (revision != null)
    {
      int currentVersion = revision.getVersion();
      if (currentVersion > lastVersion)
      {
        pattern = null;
        lastVersion = currentVersion;
      }

      if (pattern != null)
      {
        return pattern;
      }
    }

    pattern = Pattern.compile(expectedComparisonValue);
    return pattern;
  }
} // LMFilterImpl
