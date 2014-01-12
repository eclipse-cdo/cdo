/**
 */
package org.eclipse.emf.cdo.releng.setup.impl;

import org.eclipse.emf.cdo.releng.setup.MylynQueriesTask;
import org.eclipse.emf.cdo.releng.setup.MylynQuery;
import org.eclipse.emf.cdo.releng.setup.SetupPackage;
import org.eclipse.emf.cdo.releng.setup.SetupTaskContext;

import org.eclipse.net4j.util.ObjectUtil;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.MinimalEObjectImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Mylyn Query</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.releng.setup.impl.MylynQueryImpl#getTask <em>Task</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.releng.setup.impl.MylynQueryImpl#getSummary <em>Summary</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public abstract class MylynQueryImpl extends MinimalEObjectImpl.Container implements MylynQuery
{
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

  private transient MylynHelper mylynHelper;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected MylynQueryImpl()
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
    return SetupPackage.Literals.MYLYN_QUERY;
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
      eNotify(new ENotificationImpl(this, Notification.SET, SetupPackage.MYLYN_QUERY__SUMMARY, oldSummary, summary));
    }
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public MylynQueriesTask getTask()
  {
    if (eContainerFeatureID() != SetupPackage.MYLYN_QUERY__TASK)
    {
      return null;
    }
    return (MylynQueriesTask)eContainer();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public MylynQueriesTask basicGetTask()
  {
    if (eContainerFeatureID() != SetupPackage.MYLYN_QUERY__TASK)
    {
      return null;
    }
    return (MylynQueriesTask)eInternalContainer();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public NotificationChain basicSetTask(MylynQueriesTask newTask, NotificationChain msgs)
  {
    msgs = eBasicSetContainer((InternalEObject)newTask, SetupPackage.MYLYN_QUERY__TASK, msgs);
    return msgs;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setTask(MylynQueriesTask newTask)
  {
    if (newTask != eInternalContainer() || eContainerFeatureID() != SetupPackage.MYLYN_QUERY__TASK && newTask != null)
    {
      if (EcoreUtil.isAncestor(this, newTask))
      {
        throw new IllegalArgumentException("Recursive containment not allowed for " + toString());
      }
      NotificationChain msgs = null;
      if (eInternalContainer() != null)
      {
        msgs = eBasicRemoveFromContainer(msgs);
      }
      if (newTask != null)
      {
        msgs = ((InternalEObject)newTask).eInverseAdd(this, SetupPackage.MYLYN_QUERIES_TASK__QUERIES,
            MylynQueriesTask.class, msgs);
      }
      msgs = basicSetTask(newTask, msgs);
      if (msgs != null)
      {
        msgs.dispatch();
      }
    }
    else if (eNotificationRequired())
    {
      eNotify(new ENotificationImpl(this, Notification.SET, SetupPackage.MYLYN_QUERY__TASK, newTask, newTask));
    }
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public NotificationChain eInverseAdd(InternalEObject otherEnd, int featureID, NotificationChain msgs)
  {
    switch (featureID)
    {
    case SetupPackage.MYLYN_QUERY__TASK:
      if (eInternalContainer() != null)
      {
        msgs = eBasicRemoveFromContainer(msgs);
      }
      return basicSetTask((MylynQueriesTask)otherEnd, msgs);
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
    case SetupPackage.MYLYN_QUERY__TASK:
      return basicSetTask(null, msgs);
    }
    return super.eInverseRemove(otherEnd, featureID, msgs);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public NotificationChain eBasicRemoveFromContainerFeature(NotificationChain msgs)
  {
    switch (eContainerFeatureID())
    {
    case SetupPackage.MYLYN_QUERY__TASK:
      return eInternalContainer().eInverseRemove(this, SetupPackage.MYLYN_QUERIES_TASK__QUERIES,
          MylynQueriesTask.class, msgs);
    }
    return super.eBasicRemoveFromContainerFeature(msgs);
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
    case SetupPackage.MYLYN_QUERY__TASK:
      if (resolve)
      {
        return getTask();
      }
      return basicGetTask();
    case SetupPackage.MYLYN_QUERY__SUMMARY:
      return getSummary();
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
    case SetupPackage.MYLYN_QUERY__TASK:
      setTask((MylynQueriesTask)newValue);
      return;
    case SetupPackage.MYLYN_QUERY__SUMMARY:
      setSummary((String)newValue);
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
    case SetupPackage.MYLYN_QUERY__TASK:
      setTask((MylynQueriesTask)null);
      return;
    case SetupPackage.MYLYN_QUERY__SUMMARY:
      setSummary(SUMMARY_EDEFAULT);
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
    case SetupPackage.MYLYN_QUERY__TASK:
      return basicGetTask() != null;
    case SetupPackage.MYLYN_QUERY__SUMMARY:
      return SUMMARY_EDEFAULT == null ? summary != null : !SUMMARY_EDEFAULT.equals(summary);
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
    result.append(" (summary: ");
    result.append(summary);
    result.append(')');
    return result.toString();
  }

  public boolean isNeeded(SetupTaskContext context, String connectorKind, String repositoryURL) throws Exception
  {
    mylynHelper = createMylynHelper();
    return mylynHelper.isNeeded(context, this);
  }

  public void perform(SetupTaskContext context, String connectorKind, String repositoryURL) throws Exception
  {
    mylynHelper.perform(context, this);
  }

  protected abstract MylynHelper createMylynHelper();

  /**
   * @author Eike Stepper
   */
  protected interface MylynHelper
  {
    public boolean isNeeded(SetupTaskContext context, MylynQuery mylynQuery) throws Exception;

    public void perform(SetupTaskContext context, MylynQuery mylynQuery) throws Exception;
  }

  /**
   * @author Eike Stepper
   */
  @SuppressWarnings("restriction")
  protected static abstract class AbstractMylynHelper implements MylynHelper
  {
    private org.eclipse.mylyn.internal.tasks.core.TaskList taskList;

    private org.eclipse.mylyn.internal.tasks.core.RepositoryQuery repositoryQuery;

    public boolean isNeeded(SetupTaskContext context, MylynQuery mylynQuery) throws Exception
    {
      String summary = mylynQuery.getSummary();

      taskList = org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin.getTaskList();

      repositoryQuery = lookupQuery(summary);
      return repositoryQuery == null || isQueryDifferent(context, mylynQuery, repositoryQuery);
    }

    public void perform(SetupTaskContext context, MylynQuery mylynQuery) throws Exception
    {
      String connectorKind = mylynQuery.getTask().getConnectorKind();
      String repositoryURL = mylynQuery.getTask().getRepositoryURL();
      String summary = mylynQuery.getSummary();

      if (repositoryQuery == null)
      {
        context.log("Adding " + connectorKind + " query " + summary);
        String handle = taskList.getUniqueHandleIdentifier();
        repositoryQuery = new org.eclipse.mylyn.internal.tasks.core.RepositoryQuery(connectorKind, handle);
        repositoryQuery.setSummary(summary);
        repositoryQuery.setRepositoryUrl(repositoryURL);
        configureQuery(context, mylynQuery, repositoryQuery);

        taskList.addQuery(repositoryQuery);
      }
      else
      {
        context.log("Changing " + connectorKind + " query " + summary);
        repositoryQuery.setRepositoryUrl(repositoryURL);
        configureQuery(context, mylynQuery, repositoryQuery);

        taskList.notifyElementChanged(repositoryQuery);
      }
    }

    protected abstract boolean isQueryDifferent(SetupTaskContext context, MylynQuery mylynQuery,
        org.eclipse.mylyn.internal.tasks.core.RepositoryQuery repositoryQuery);

    protected abstract void configureQuery(SetupTaskContext context, MylynQuery mylynQuery,
        org.eclipse.mylyn.internal.tasks.core.RepositoryQuery repositoryQuery);

    private org.eclipse.mylyn.internal.tasks.core.RepositoryQuery lookupQuery(String summary) throws Exception
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

} // MylynQueryImpl
