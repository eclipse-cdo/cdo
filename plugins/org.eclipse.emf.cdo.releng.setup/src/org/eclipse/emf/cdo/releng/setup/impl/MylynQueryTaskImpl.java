/*
 * Copyright (c) 2013, 2014 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.releng.setup.impl;

import org.eclipse.emf.cdo.releng.setup.MylynQueryTask;
import org.eclipse.emf.cdo.releng.setup.SetupPackage;
import org.eclipse.emf.cdo.releng.setup.SetupTaskContext;
import org.eclipse.emf.cdo.releng.setup.Trigger;

import org.eclipse.net4j.util.ObjectUtil;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.impl.ENotificationImpl;

import java.util.Set;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Mylyn Query Task</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.releng.setup.impl.MylynQueryTaskImpl#getConnectorKind <em>Connector Kind</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.releng.setup.impl.MylynQueryTaskImpl#getSummary <em>Summary</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.releng.setup.impl.MylynQueryTaskImpl#getRepositoryURL <em>Repository URL</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.releng.setup.impl.MylynQueryTaskImpl#getRelativeURL <em>Relative URL</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
@Deprecated
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
   * The default value of the '{@link #getRepositoryURL() <em>Repository URL</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getRepositoryURL()
   * @generated
   * @ordered
   */
  protected static final String REPOSITORY_URL_EDEFAULT = null;

  /**
   * The cached value of the '{@link #getRepositoryURL() <em>Repository URL</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getRepositoryURL()
   * @generated
   * @ordered
   */
  protected String repositoryURL = REPOSITORY_URL_EDEFAULT;

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
  public String getRepositoryURL()
  {
    return repositoryURL;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setRepositoryURL(String newRepositoryURL)
  {
    String oldRepositoryURL = repositoryURL;
    repositoryURL = newRepositoryURL;
    if (eNotificationRequired())
    {
      eNotify(new ENotificationImpl(this, Notification.SET, SetupPackage.MYLYN_QUERY_TASK__REPOSITORY_URL,
          oldRepositoryURL, repositoryURL));
    }
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
      eNotify(new ENotificationImpl(this, Notification.SET, SetupPackage.MYLYN_QUERY_TASK__RELATIVE_URL,
          oldRelativeURL, relativeURL));
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
    case SetupPackage.MYLYN_QUERY_TASK__REPOSITORY_URL:
      return getRepositoryURL();
    case SetupPackage.MYLYN_QUERY_TASK__RELATIVE_URL:
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
    case SetupPackage.MYLYN_QUERY_TASK__CONNECTOR_KIND:
      setConnectorKind((String)newValue);
      return;
    case SetupPackage.MYLYN_QUERY_TASK__SUMMARY:
      setSummary((String)newValue);
      return;
    case SetupPackage.MYLYN_QUERY_TASK__REPOSITORY_URL:
      setRepositoryURL((String)newValue);
      return;
    case SetupPackage.MYLYN_QUERY_TASK__RELATIVE_URL:
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
    case SetupPackage.MYLYN_QUERY_TASK__CONNECTOR_KIND:
      setConnectorKind(CONNECTOR_KIND_EDEFAULT);
      return;
    case SetupPackage.MYLYN_QUERY_TASK__SUMMARY:
      setSummary(SUMMARY_EDEFAULT);
      return;
    case SetupPackage.MYLYN_QUERY_TASK__REPOSITORY_URL:
      setRepositoryURL(REPOSITORY_URL_EDEFAULT);
      return;
    case SetupPackage.MYLYN_QUERY_TASK__RELATIVE_URL:
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
    case SetupPackage.MYLYN_QUERY_TASK__CONNECTOR_KIND:
      return CONNECTOR_KIND_EDEFAULT == null ? connectorKind != null : !CONNECTOR_KIND_EDEFAULT.equals(connectorKind);
    case SetupPackage.MYLYN_QUERY_TASK__SUMMARY:
      return SUMMARY_EDEFAULT == null ? summary != null : !SUMMARY_EDEFAULT.equals(summary);
    case SetupPackage.MYLYN_QUERY_TASK__REPOSITORY_URL:
      return REPOSITORY_URL_EDEFAULT == null ? repositoryURL != null : !REPOSITORY_URL_EDEFAULT.equals(repositoryURL);
    case SetupPackage.MYLYN_QUERY_TASK__RELATIVE_URL:
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
    result.append(" (connectorKind: ");
    result.append(connectorKind);
    result.append(", summary: ");
    result.append(summary);
    result.append(", repositoryURL: ");
    result.append(repositoryURL);
    result.append(", relativeURL: ");
    result.append(relativeURL);
    result.append(')');
    return result.toString();
  }

  @Override
  public Set<Trigger> getValidTriggers()
  {
    return Trigger.IDE_TRIGGERS;
  }

  public boolean isNeeded(SetupTaskContext context) throws Exception
  {
    String summary = getSummary();
    String repositoryURL = getRepositoryURL();
    String relativeURL = getRelativeURL();

    return MylynHelper.isNeeded(summary, repositoryURL, relativeURL);
  }

  public void perform(SetupTaskContext context) throws Exception
  {
    String connectorKind = getConnectorKind();
    String summary = getSummary();
    String repositoryURL = getRepositoryURL();
    String relativeURL = getRelativeURL();

    MylynHelper.perform(context, connectorKind, summary, repositoryURL, relativeURL);
  }

  /**
   * @author Eike Stepper
   */
  @SuppressWarnings("restriction")
  private static class MylynHelper
  {
    public static boolean isNeeded(String summary, String repositoryURL, String relativeURL) throws Exception
    {
      String queryURL = getQueryURL(repositoryURL, relativeURL);

      org.eclipse.mylyn.internal.tasks.core.TaskList taskList = //
      org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin.getTaskList();

      org.eclipse.mylyn.internal.tasks.core.RepositoryQuery query = lookupQuery(taskList, summary);
      return query == null || !ObjectUtil.equals(query.getUrl(), queryURL);
    }

    public static void perform(SetupTaskContext context, String connectorKind, String summary, String repositoryURL,
        String relativeURL) throws Exception
    {
      String queryURL = getQueryURL(repositoryURL, relativeURL);

      org.eclipse.mylyn.internal.tasks.core.TaskList taskList = //
      org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin.getTaskList();

      org.eclipse.mylyn.internal.tasks.core.RepositoryQuery query = lookupQuery(taskList, summary);
      if (query == null)
      {
        context.log("Adding " + connectorKind + " query " + summary + " = " + queryURL);
        String handle = taskList.getUniqueHandleIdentifier();
        query = new org.eclipse.mylyn.internal.tasks.core.RepositoryQuery(connectorKind, handle);
        query.setSummary(summary);
        query.setRepositoryUrl(repositoryURL);
        query.setUrl(queryURL);
        taskList.addQuery(query);
      }
      else
      {
        context.log("Changing " + connectorKind + " query " + summary + " = " + queryURL);
        query.setRepositoryUrl(repositoryURL);
        query.setUrl(queryURL);
      }

      // AbstractRepositoryConnector connector = TasksUi.getRepositoryManager().getRepositoryConnector(connectorKind);
      // if (connector != null)
      // {
      // org.eclipse.mylyn.internal.tasks.ui.util.TasksUiInternal.synchronizeQuery(connector, query, null, true);
      // }
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

    private static String getQueryURL(String repositoryURL, String relativeURL)
    {
      return repositoryURL + relativeURL;
    }
  }

} // MylynQueryTaskImpl
