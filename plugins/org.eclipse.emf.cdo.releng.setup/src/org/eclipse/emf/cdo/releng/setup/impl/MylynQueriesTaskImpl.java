/**
 */
package org.eclipse.emf.cdo.releng.setup.impl;

import org.eclipse.emf.cdo.releng.setup.MylynQueriesTask;
import org.eclipse.emf.cdo.releng.setup.MylynQuery;
import org.eclipse.emf.cdo.releng.setup.SetupPackage;
import org.eclipse.emf.cdo.releng.setup.SetupTaskContext;
import org.eclipse.emf.cdo.releng.setup.Trigger;

import org.eclipse.net4j.util.ObjectUtil;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.util.EObjectContainmentWithInverseEList;
import org.eclipse.emf.ecore.util.InternalEList;

import org.eclipse.mylyn.internal.tasks.core.RepositoryQuery;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.internal.tasks.ui.util.TasksUiInternal;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.ui.TasksUi;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Mylyn Queries Task</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.releng.setup.impl.MylynQueriesTaskImpl#getConnectorKind <em>Connector Kind</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.releng.setup.impl.MylynQueriesTaskImpl#getRepositoryURL <em>Repository URL</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.releng.setup.impl.MylynQueriesTaskImpl#getQueries <em>Queries</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
@SuppressWarnings("restriction")
public class MylynQueriesTaskImpl extends SetupTaskImpl implements MylynQueriesTask
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
   * The cached value of the '{@link #getQueries() <em>Queries</em>}' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getQueries()
   * @generated
   * @ordered
   */
  protected EList<MylynQuery> queries;

  private transient MylynHelper mylynHelper;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected MylynQueriesTaskImpl()
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
    return SetupPackage.Literals.MYLYN_QUERIES_TASK;
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
      eNotify(new ENotificationImpl(this, Notification.SET, SetupPackage.MYLYN_QUERIES_TASK__CONNECTOR_KIND,
          oldConnectorKind, connectorKind));
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
      eNotify(new ENotificationImpl(this, Notification.SET, SetupPackage.MYLYN_QUERIES_TASK__REPOSITORY_URL,
          oldRepositoryURL, repositoryURL));
    }
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EList<MylynQuery> getQueries()
  {
    if (queries == null)
    {
      queries = new EObjectContainmentWithInverseEList.Resolving<MylynQuery>(MylynQuery.class, this,
          SetupPackage.MYLYN_QUERIES_TASK__QUERIES, SetupPackage.MYLYN_QUERY__TASK);
    }
    return queries;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @SuppressWarnings("unchecked")
  @Override
  public NotificationChain eInverseAdd(InternalEObject otherEnd, int featureID, NotificationChain msgs)
  {
    switch (featureID)
    {
    case SetupPackage.MYLYN_QUERIES_TASK__QUERIES:
      return ((InternalEList<InternalEObject>)(InternalEList<?>)getQueries()).basicAdd(otherEnd, msgs);
    }
    return super.eInverseAdd(otherEnd, featureID, msgs);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs)
  {
    switch (featureID)
    {
    case SetupPackage.MYLYN_QUERIES_TASK__QUERIES:
      return ((InternalEList<?>)getQueries()).basicRemove(otherEnd, msgs);
    }
    return super.eInverseRemove(otherEnd, featureID, msgs);
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
    case SetupPackage.MYLYN_QUERIES_TASK__CONNECTOR_KIND:
      return getConnectorKind();
    case SetupPackage.MYLYN_QUERIES_TASK__REPOSITORY_URL:
      return getRepositoryURL();
    case SetupPackage.MYLYN_QUERIES_TASK__QUERIES:
      return getQueries();
    }
    return super.eGet(featureID, resolve, coreType);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @SuppressWarnings("unchecked")
  @Override
  public void eSet(int featureID, Object newValue)
  {
    switch (featureID)
    {
    case SetupPackage.MYLYN_QUERIES_TASK__CONNECTOR_KIND:
      setConnectorKind((String)newValue);
      return;
    case SetupPackage.MYLYN_QUERIES_TASK__REPOSITORY_URL:
      setRepositoryURL((String)newValue);
      return;
    case SetupPackage.MYLYN_QUERIES_TASK__QUERIES:
      getQueries().clear();
      getQueries().addAll((Collection<? extends MylynQuery>)newValue);
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
    case SetupPackage.MYLYN_QUERIES_TASK__CONNECTOR_KIND:
      setConnectorKind(CONNECTOR_KIND_EDEFAULT);
      return;
    case SetupPackage.MYLYN_QUERIES_TASK__REPOSITORY_URL:
      setRepositoryURL(REPOSITORY_URL_EDEFAULT);
      return;
    case SetupPackage.MYLYN_QUERIES_TASK__QUERIES:
      getQueries().clear();
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
    case SetupPackage.MYLYN_QUERIES_TASK__CONNECTOR_KIND:
      return CONNECTOR_KIND_EDEFAULT == null ? connectorKind != null : !CONNECTOR_KIND_EDEFAULT.equals(connectorKind);
    case SetupPackage.MYLYN_QUERIES_TASK__REPOSITORY_URL:
      return REPOSITORY_URL_EDEFAULT == null ? repositoryURL != null : !REPOSITORY_URL_EDEFAULT.equals(repositoryURL);
    case SetupPackage.MYLYN_QUERIES_TASK__QUERIES:
      return queries != null && !queries.isEmpty();
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
    result.append(", repositoryURL: ");
    result.append(repositoryURL);
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
    mylynHelper = MylynHelperImpl.create();
    return mylynHelper.isNeeded(context, this);
  }

  public void perform(SetupTaskContext context) throws Exception
  {
    mylynHelper.perform(context, this);
  }

  /**
   * @author Eike Stepper
   */
  protected interface MylynHelper
  {
    public boolean isNeeded(SetupTaskContext context, MylynQueriesTask task) throws Exception;

    public void perform(SetupTaskContext context, MylynQueriesTask task) throws Exception;
  }

  /**
   * @author Eike Stepper
   */
  private static class MylynHelperImpl implements MylynHelper
  {
    private TaskRepository repository;

    private Map<MylynQuery, RepositoryQuery> repositoryQueries = new HashMap<MylynQuery, RepositoryQuery>();

    public boolean isNeeded(SetupTaskContext context, MylynQueriesTask task) throws Exception
    {
      String connectorKind = task.getConnectorKind();
      String repositoryURL = task.getRepositoryURL();
      repository = TasksUi.getRepositoryManager().getRepository(connectorKind, repositoryURL);

      for (MylynQuery query : task.getQueries())
      {
        context.checkCancelation();

        RepositoryQuery repositoryQuery = getRepositoryQuery(query);
        if (repositoryQuery == null || isQueryDifferent(query, repositoryQuery))
        {
          repositoryQueries.put(query, repositoryQuery);
        }
      }

      if (repository == null)
      {
        return true;
      }

      if (!repositoryQueries.isEmpty())
      {
        return true;
      }

      return false;
    }

    public void perform(SetupTaskContext context, MylynQueriesTask task) throws Exception
    {
      String connectorKind = task.getConnectorKind();
      String repositoryURL = task.getRepositoryURL();

      if (repository == null)
      {
        context.log("Adding " + connectorKind + " repository: " + repositoryURL);
        repository = new TaskRepository(connectorKind, repositoryURL);
        TasksUi.getRepositoryManager().addRepository(repository);
      }

      for (Map.Entry<MylynQuery, RepositoryQuery> entry : repositoryQueries.entrySet())
      {
        MylynQuery mylynQuery = entry.getKey();
        RepositoryQuery repositoryQuery = entry.getValue();

        String summary = mylynQuery.getSummary();

        if (repositoryQuery == null)
        {
          context.log("Adding " + connectorKind + " query: " + summary);
          String handle = TasksUiPlugin.getTaskList().getUniqueHandleIdentifier();
          repositoryQuery = new RepositoryQuery(connectorKind, handle);
          repositoryQuery.setSummary(summary);
          entry.setValue(repositoryQuery);

          repositoryQuery.setRepositoryUrl(repositoryURL);
          configureQuery(context, mylynQuery, repositoryQuery);

          TasksUiPlugin.getTaskList().addQuery(repositoryQuery);
        }
        else
        {
          context.log("Changing " + connectorKind + " query: " + summary);

          repositoryQuery.setRepositoryUrl(repositoryURL);
          configureQuery(context, mylynQuery, repositoryQuery);
        }
      }

      Set<RepositoryQuery> queries = new HashSet<RepositoryQuery>(repositoryQueries.values());
      TasksUiPlugin.getTaskList().notifyElementsChanged(queries);

      AbstractRepositoryConnector connector = TasksUi.getRepositoryManager().getRepositoryConnector(connectorKind);
      TasksUiInternal.synchronizeQueries(connector, repository, queries, null, true);
    }

    private RepositoryQuery getRepositoryQuery(MylynQuery mylynQuery) throws Exception
    {
      for (RepositoryQuery repositoryQuery : TasksUiPlugin.getTaskList().getQueries())
      {
        if (ObjectUtil.equals(repositoryQuery.getSummary(), mylynQuery.getSummary()))
        {
          return repositoryQuery;
        }
      }

      return null;
    }

    private boolean isQueryDifferent(MylynQuery mylynQuery, RepositoryQuery repositoryQuery) throws Exception
    {
      String url = mylynQuery.getURL();
      if (!ObjectUtil.equals(repositoryQuery.getUrl(), url))
      {
        return true;
      }

      Map<String, String> attributes = mylynQuery.getAttributes().map();
      if (!ObjectUtil.equals(repositoryQuery.getAttributes(), attributes))
      {
        return true;
      }

      return false;
    }

    private void configureQuery(SetupTaskContext context, MylynQuery mylynQuery, RepositoryQuery repositoryQuery)
    {
      String url = mylynQuery.getURL();
      if (!ObjectUtil.equals(url, repositoryQuery.getUrl()))
      {
        context.log("Setting query URL = " + url);
        repositoryQuery.setUrl(url);
      }

      Map<String, String> repositoryAttributes = repositoryQuery.getAttributes();
      Map<String, String> attributes = mylynQuery.getAttributes().map();

      for (Entry<String, String> entry : attributes.entrySet())
      {
        String key = entry.getKey();
        String value = entry.getValue();

        String repositoryValue = repositoryAttributes.get(key);
        if (!ObjectUtil.equals(value, repositoryValue))
        {
          context.log("Setting query attribute " + key + " = " + value);
          repositoryQuery.setAttribute(key, value);
        }
      }

      for (String key : new ArrayList<String>(repositoryAttributes.keySet()))
      {
        if (!attributes.containsKey(key))
        {
          context.log("Removing query attribute " + key);
          repositoryQuery.setAttribute(key, null);
        }
      }
    }

    public static MylynHelper create()
    {
      return new MylynHelperImpl();
    }
  }
} // MylynQueriesTaskImpl
