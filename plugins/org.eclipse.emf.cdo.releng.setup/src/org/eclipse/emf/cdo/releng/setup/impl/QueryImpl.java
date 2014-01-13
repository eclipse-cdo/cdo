/**
 */
package org.eclipse.emf.cdo.releng.setup.impl;

import org.eclipse.emf.cdo.releng.setup.MylynQueriesTask;
import org.eclipse.emf.cdo.releng.setup.Query;
import org.eclipse.emf.cdo.releng.setup.SetupPackage;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.util.EMap;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.MinimalEObjectImpl;
import org.eclipse.emf.ecore.util.EcoreEMap;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.util.InternalEList;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Query</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.releng.setup.impl.QueryImpl#getTask <em>Task</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.releng.setup.impl.QueryImpl#getSummary <em>Summary</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.releng.setup.impl.QueryImpl#getURL <em>URL</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.releng.setup.impl.QueryImpl#getAttributes <em>Attributes</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class QueryImpl extends MinimalEObjectImpl.Container implements Query
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

  /**
   * The default value of the '{@link #getURL() <em>URL</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getURL()
   * @generated
   * @ordered
   */
  protected static final String URL_EDEFAULT = null;

  /**
   * The cached value of the '{@link #getURL() <em>URL</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getURL()
   * @generated
   * @ordered
   */
  protected String uRL = URL_EDEFAULT;

  /**
   * The cached value of the '{@link #getAttributes() <em>Attributes</em>}' map.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getAttributes()
   * @generated
   * @ordered
   */
  protected EMap<String, String> attributes;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected QueryImpl()
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
    return SetupPackage.Literals.QUERY;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public MylynQueriesTask getTask()
  {
    if (eContainerFeatureID() != SetupPackage.QUERY__TASK)
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
    if (eContainerFeatureID() != SetupPackage.QUERY__TASK)
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
    msgs = eBasicSetContainer((InternalEObject)newTask, SetupPackage.QUERY__TASK, msgs);
    return msgs;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setTask(MylynQueriesTask newTask)
  {
    if (newTask != eInternalContainer() || eContainerFeatureID() != SetupPackage.QUERY__TASK && newTask != null)
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
      eNotify(new ENotificationImpl(this, Notification.SET, SetupPackage.QUERY__TASK, newTask, newTask));
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
      eNotify(new ENotificationImpl(this, Notification.SET, SetupPackage.QUERY__SUMMARY, oldSummary, summary));
    }
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public String getURL()
  {
    return uRL;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setURL(String newURL)
  {
    String oldURL = uRL;
    uRL = newURL;
    if (eNotificationRequired())
    {
      eNotify(new ENotificationImpl(this, Notification.SET, SetupPackage.QUERY__URL, oldURL, uRL));
    }
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EMap<String, String> getAttributes()
  {
    if (attributes == null)
    {
      attributes = new EcoreEMap<String, String>(SetupPackage.Literals.QUERY_ATTRIBUTE, QueryAttributeImpl.class, this,
          SetupPackage.QUERY__ATTRIBUTES);
    }
    return attributes;
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
    case SetupPackage.QUERY__TASK:
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
    case SetupPackage.QUERY__TASK:
      return basicSetTask(null, msgs);
    case SetupPackage.QUERY__ATTRIBUTES:
      return ((InternalEList<?>)getAttributes()).basicRemove(otherEnd, msgs);
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
    case SetupPackage.QUERY__TASK:
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
    case SetupPackage.QUERY__TASK:
      if (resolve)
      {
        return getTask();
      }
      return basicGetTask();
    case SetupPackage.QUERY__SUMMARY:
      return getSummary();
    case SetupPackage.QUERY__URL:
      return getURL();
    case SetupPackage.QUERY__ATTRIBUTES:
      if (coreType)
      {
        return getAttributes();
      }
      else
      {
        return getAttributes().map();
      }
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
    case SetupPackage.QUERY__TASK:
      setTask((MylynQueriesTask)newValue);
      return;
    case SetupPackage.QUERY__SUMMARY:
      setSummary((String)newValue);
      return;
    case SetupPackage.QUERY__URL:
      setURL((String)newValue);
      return;
    case SetupPackage.QUERY__ATTRIBUTES:
      ((EStructuralFeature.Setting)getAttributes()).set(newValue);
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
    case SetupPackage.QUERY__TASK:
      setTask((MylynQueriesTask)null);
      return;
    case SetupPackage.QUERY__SUMMARY:
      setSummary(SUMMARY_EDEFAULT);
      return;
    case SetupPackage.QUERY__URL:
      setURL(URL_EDEFAULT);
      return;
    case SetupPackage.QUERY__ATTRIBUTES:
      getAttributes().clear();
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
    case SetupPackage.QUERY__TASK:
      return basicGetTask() != null;
    case SetupPackage.QUERY__SUMMARY:
      return SUMMARY_EDEFAULT == null ? summary != null : !SUMMARY_EDEFAULT.equals(summary);
    case SetupPackage.QUERY__URL:
      return URL_EDEFAULT == null ? uRL != null : !URL_EDEFAULT.equals(uRL);
    case SetupPackage.QUERY__ATTRIBUTES:
      return attributes != null && !attributes.isEmpty();
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
    result.append(", uRL: ");
    result.append(uRL);
    result.append(')');
    return result.toString();
  }

} // QueryImpl
