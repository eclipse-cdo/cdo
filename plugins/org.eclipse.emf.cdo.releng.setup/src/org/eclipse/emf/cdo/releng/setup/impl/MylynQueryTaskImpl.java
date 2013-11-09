/**
 */
package org.eclipse.emf.cdo.releng.setup.impl;

import org.eclipse.emf.cdo.releng.setup.MylynQueryTask;
import org.eclipse.emf.cdo.releng.setup.SetupPackage;
import org.eclipse.emf.cdo.releng.setup.SetupTaskContext;

import org.eclipse.net4j.util.ObjectUtil;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.impl.ENotificationImpl;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Mylyn Query Task</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.releng.setup.impl.MylynQueryTaskImpl#getConnectorKind <em>Connector Kind</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.releng.setup.impl.MylynQueryTaskImpl#getSummary <em>Summary</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.releng.setup.impl.MylynQueryTaskImpl#getUrl <em>Url</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class MylynQueryTaskImpl extends SetupTaskImpl implements MylynQueryTask
{
  /**
   * The default value of the '{@link #getConnectorKind() <em>Connector Kind</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getConnectorKind()
   * @generated
   * @ordered
   */
  protected static final String CONNECTOR_KIND_EDEFAULT = "bugzilla";

  /**
   * The cached value of the '{@link #getConnectorKind() <em>Connector Kind</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getConnectorKind()
   * @generated
   * @ordered
   */
  protected String connectorKind = CONNECTOR_KIND_EDEFAULT;

  /**
   * The default value of the '{@link #getSummary() <em>Summary</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getSummary()
   * @generated
   * @ordered
   */
  protected static final String SUMMARY_EDEFAULT = null;

  /**
   * The cached value of the '{@link #getSummary() <em>Summary</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getSummary()
   * @generated
   * @ordered
   */
  protected String summary = SUMMARY_EDEFAULT;

  /**
   * The default value of the '{@link #getUrl() <em>Url</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getUrl()
   * @generated
   * @ordered
   */
  protected static final String URL_EDEFAULT = null;

  /**
   * The cached value of the '{@link #getUrl() <em>Url</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getUrl()
   * @generated
   * @ordered
   */
  protected String url = URL_EDEFAULT;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected MylynQueryTaskImpl()
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
    return SetupPackage.Literals.MYLYN_QUERY_TASK;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public String getConnectorKind()
  {
    return connectorKind;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setConnectorKind(String newConnectorKind)
  {
    String oldConnectorKind = connectorKind;
    connectorKind = newConnectorKind;
    if (eNotificationRequired())
    {
      eNotify(new ENotificationImpl(this, Notification.SET, SetupPackage.MYLYN_QUERY_TASK__CONNECTOR_KIND,
          oldConnectorKind, connectorKind));
    }
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public String getSummary()
  {
    return summary;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setSummary(String newSummary)
  {
    String oldSummary = summary;
    summary = newSummary;
    if (eNotificationRequired())
    {
      eNotify(new ENotificationImpl(this, Notification.SET, SetupPackage.MYLYN_QUERY_TASK__SUMMARY, oldSummary, summary));
    }
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public String getUrl()
  {
    return url;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setUrl(String newUrl)
  {
    String oldUrl = url;
    url = newUrl;
    if (eNotificationRequired())
    {
      eNotify(new ENotificationImpl(this, Notification.SET, SetupPackage.MYLYN_QUERY_TASK__URL, oldUrl, url));
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
    case SetupPackage.MYLYN_QUERY_TASK__CONNECTOR_KIND:
      return getConnectorKind();
    case SetupPackage.MYLYN_QUERY_TASK__SUMMARY:
      return getSummary();
    case SetupPackage.MYLYN_QUERY_TASK__URL:
      return getUrl();
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
    case SetupPackage.MYLYN_QUERY_TASK__CONNECTOR_KIND:
      setConnectorKind((String)newValue);
      return;
    case SetupPackage.MYLYN_QUERY_TASK__SUMMARY:
      setSummary((String)newValue);
      return;
    case SetupPackage.MYLYN_QUERY_TASK__URL:
      setUrl((String)newValue);
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
    case SetupPackage.MYLYN_QUERY_TASK__CONNECTOR_KIND:
      setConnectorKind(CONNECTOR_KIND_EDEFAULT);
      return;
    case SetupPackage.MYLYN_QUERY_TASK__SUMMARY:
      setSummary(SUMMARY_EDEFAULT);
      return;
    case SetupPackage.MYLYN_QUERY_TASK__URL:
      setUrl(URL_EDEFAULT);
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
    case SetupPackage.MYLYN_QUERY_TASK__CONNECTOR_KIND:
      return CONNECTOR_KIND_EDEFAULT == null ? connectorKind != null : !CONNECTOR_KIND_EDEFAULT.equals(connectorKind);
    case SetupPackage.MYLYN_QUERY_TASK__SUMMARY:
      return SUMMARY_EDEFAULT == null ? summary != null : !SUMMARY_EDEFAULT.equals(summary);
    case SetupPackage.MYLYN_QUERY_TASK__URL:
      return URL_EDEFAULT == null ? url != null : !URL_EDEFAULT.equals(url);
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
    result.append(" (connectorKind: ");
    result.append(connectorKind);
    result.append(", summary: ");
    result.append(summary);
    result.append(", url: ");
    result.append(url);
    result.append(')');
    return result.toString();
  }

  public boolean isNeeded(SetupTaskContext context) throws Exception
  {
    return MylynHelper.isNeeded(context.expandString(getSummary()), context.expandString(getUrl()));
  }

  public void perform(SetupTaskContext context) throws Exception
  {
    MylynHelper.perform(context.expandString(getConnectorKind()), context.expandString(getSummary()),
        context.expandString(getUrl()));
  }

  /**
   * @author Eike Stepper
   */
  @SuppressWarnings("restriction")
  private static class MylynHelper
  {
    public static boolean isNeeded(String summary, String url) throws Exception
    {
      org.eclipse.mylyn.internal.tasks.core.TaskList taskList = //
      org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin.getTaskList();

      org.eclipse.mylyn.internal.tasks.core.RepositoryQuery query = lookupQuery(taskList, summary);
      return query == null || !ObjectUtil.equals(query.getUrl(), url);
    }

    public static void perform(String connectorKind, String summary, String url) throws Exception
    {
      org.eclipse.mylyn.internal.tasks.core.TaskList taskList = //
      org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin.getTaskList();

      org.eclipse.mylyn.internal.tasks.core.RepositoryQuery query = lookupQuery(taskList, summary);
      if (query == null)
      {
        String handle = taskList.getUniqueHandleIdentifier();
        query = new org.eclipse.mylyn.internal.tasks.core.RepositoryQuery(connectorKind, handle);
        query.setSummary(summary);
        query.setUrl(url);
        taskList.addQuery(query);
      }
      else
      {
        query.setUrl(url);
      }
    }

    private static org.eclipse.mylyn.internal.tasks.core.RepositoryQuery lookupQuery(
        org.eclipse.mylyn.internal.tasks.core.TaskList taskList, String summary) throws Exception
    {
      for (org.eclipse.mylyn.internal.tasks.core.RepositoryQuery query : taskList.getQueries())
      {
        if (ObjectUtil.equals(query.getSummary(), summary))
        {
          return query;
        }
      }

      return null;
    }
  }

} // MylynQueryTaskImpl
