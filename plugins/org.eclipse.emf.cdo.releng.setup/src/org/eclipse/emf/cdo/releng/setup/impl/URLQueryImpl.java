/**
 */
package org.eclipse.emf.cdo.releng.setup.impl;

import org.eclipse.emf.cdo.releng.setup.MylynQuery;
import org.eclipse.emf.cdo.releng.setup.SetupPackage;
import org.eclipse.emf.cdo.releng.setup.SetupTaskContext;
import org.eclipse.emf.cdo.releng.setup.URLQuery;

import org.eclipse.net4j.util.ObjectUtil;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.impl.ENotificationImpl;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>URL Query</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.releng.setup.impl.URLQueryImpl#getRelativeURL <em>Relative URL</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class URLQueryImpl extends MylynQueryImpl implements URLQuery
{
  /**
   * The default value of the '{@link #getRelativeURL() <em>Relative URL</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getRelativeURL()
   * @generated
   * @ordered
   */
  protected static final String RELATIVE_URL_EDEFAULT = null;

  /**
   * The cached value of the '{@link #getRelativeURL() <em>Relative URL</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getRelativeURL()
   * @generated
   * @ordered
   */
  protected String relativeURL = RELATIVE_URL_EDEFAULT;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected URLQueryImpl()
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
    return SetupPackage.Literals.URL_QUERY;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public String getRelativeURL()
  {
    return relativeURL;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setRelativeURL(String newRelativeURL)
  {
    String oldRelativeURL = relativeURL;
    relativeURL = newRelativeURL;
    if (eNotificationRequired())
    {
      eNotify(new ENotificationImpl(this, Notification.SET, SetupPackage.URL_QUERY__RELATIVE_URL, oldRelativeURL,
          relativeURL));
    }
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
    case SetupPackage.URL_QUERY__RELATIVE_URL:
      return getRelativeURL();
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
    case SetupPackage.URL_QUERY__RELATIVE_URL:
      setRelativeURL((String)newValue);
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
    case SetupPackage.URL_QUERY__RELATIVE_URL:
      setRelativeURL(RELATIVE_URL_EDEFAULT);
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
    case SetupPackage.URL_QUERY__RELATIVE_URL:
      return RELATIVE_URL_EDEFAULT == null ? relativeURL != null : !RELATIVE_URL_EDEFAULT.equals(relativeURL);
    }
    return super.eIsSet(featureID);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public String toString()
  {
    if (eIsProxy())
    {
      return super.toString();
    }

    StringBuffer result = new StringBuffer(super.toString());
    result.append(" (relativeURL: ");
    result.append(relativeURL);
    result.append(')');
    return result.toString();
  }

  @Override
  protected MylynHelper createMylynHelper()
  {
    return URLMylynHelper.create();
  }

  /**
   * @author Eike Stepper
   */
  @SuppressWarnings("restriction")
  private static class URLMylynHelper extends AbstractMylynHelper
  {
    private String queryURL;

    @Override
    protected boolean isQueryDifferent(SetupTaskContext context, MylynQuery mylynQuery,
        org.eclipse.mylyn.internal.tasks.core.RepositoryQuery repositoryQuery)
    {
      String repositoryURL = mylynQuery.getTask().getRepositoryURL();
      String relativeURL = ((URLQuery)mylynQuery).getRelativeURL();

      queryURL = repositoryURL + relativeURL;
      return !ObjectUtil.equals(repositoryQuery.getUrl(), queryURL);
    }

    @Override
    protected void configureQuery(SetupTaskContext context, MylynQuery mylynQuery,
        org.eclipse.mylyn.internal.tasks.core.RepositoryQuery repositoryQuery)
    {
      context.log("Setting query URL = " + queryURL);
      repositoryQuery.setUrl(queryURL);
    }

    public static MylynHelper create()
    {
      return new URLMylynHelper();
    }
  }

} // URLQueryImpl
