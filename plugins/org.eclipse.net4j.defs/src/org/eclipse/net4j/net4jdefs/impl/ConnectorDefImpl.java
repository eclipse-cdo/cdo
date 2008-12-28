/**
 * <copyright>
 * </copyright>
 *
 * $Id: ConnectorDefImpl.java,v 1.1 2008-12-28 18:07:28 estepper Exp $
 */
package org.eclipse.net4j.net4jdefs.impl;

import org.eclipse.net4j.ITransportConfig;
import org.eclipse.net4j.buffer.IBufferProvider;
import org.eclipse.net4j.connector.IConnector;
import org.eclipse.net4j.net4jdefs.BufferProviderDef;
import org.eclipse.net4j.net4jdefs.ClientProtocolFactoryDef;
import org.eclipse.net4j.net4jdefs.ConnectorDef;
import org.eclipse.net4j.net4jdefs.NegotiatorDef;
import org.eclipse.net4j.net4jdefs.Net4jDefsPackage;
import org.eclipse.net4j.net4jdefs.util.Net4jDefsUtil;
import org.eclipse.net4j.protocol.IProtocolProvider;
import org.eclipse.net4j.util.CheckUtil;
import org.eclipse.net4j.util.net4jutildefs.ExecutorServiceDef;
import org.eclipse.net4j.util.net4jutildefs.impl.DefImpl;
import org.eclipse.net4j.util.security.INegotiator;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.util.EObjectResolvingEList;
import org.eclipse.emf.ecore.util.InternalEList;

import org.eclipse.spi.net4j.Connector;

import java.util.Collection;
import java.util.concurrent.ExecutorService;

// TODO: Auto-generated Javadoc
/**
 * <!-- begin-user-doc --> An implementation of the model object ' <em><b>Connector Def</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 * <li>{@link org.eclipse.net4j.net4jdefs.impl.ConnectorDefImpl#getUserID <em>User ID</em>}</li>
 * <li>{@link org.eclipse.net4j.net4jdefs.impl.ConnectorDefImpl#getBufferProvider <em>Buffer Provider</em>}</li>
 * <li>{@link org.eclipse.net4j.net4jdefs.impl.ConnectorDefImpl#getExecutorService <em>Executor Service</em>}</li>
 * <li>{@link org.eclipse.net4j.net4jdefs.impl.ConnectorDefImpl#getNegotiator <em>Negotiator</em>}</li>
 * <li>{@link org.eclipse.net4j.net4jdefs.impl.ConnectorDefImpl#getClientProtocolProvider <em>Client Protocol Provider
 * </em>}</li>
 * </ul>
 * </p>
 * 
 * @generated
 */
public abstract class ConnectorDefImpl extends DefImpl implements ConnectorDef
{

  /**
   * The default value of the '{@link #getUserID() <em>User ID</em>}' attribute. <!-- begin-user-doc --> <!--
   * end-user-doc -->
   */
  protected static final String USER_ID_EDEFAULT = null;

  /**
   * The cached value of the '{@link #getUserID() <em>User ID</em>}' attribute. <!-- begin-user-doc --> <!--
   * end-user-doc -->
   */
  protected String userID = USER_ID_EDEFAULT;

  /**
   * This is true if the User ID attribute has been set. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  protected boolean userIDESet;

  /**
   * The cached value of the '{@link #getBufferProvider() <em>Buffer Provider</em>}' reference. <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * 
   * @see #getBufferProvider()
   * @generated
   * @ordered
   */
  protected BufferProviderDef bufferProvider;

  /**
   * The cached value of the '{@link #getExecutorService() <em>Executor Service</em>}' reference. <!-- begin-user-doc
   * --> <!-- end-user-doc -->
   * 
   * @see #getExecutorService()
   * @generated
   * @ordered
   */
  protected ExecutorServiceDef executorService;

  /**
   * The cached value of the '{@link #getNegotiator() <em>Negotiator</em>}' reference. <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * 
   * @see #getNegotiator()
   * @generated
   * @ordered
   */
  protected NegotiatorDef negotiator;

  /**
   * This is true if the Negotiator reference has been set. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  protected boolean negotiatorESet;

  /**
   * The cached value of the '{@link #getClientProtocolProvider() <em>Client Protocol Provider</em>}' reference list.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @see #getClientProtocolProvider()
   * @generated
   * @ordered
   */
  protected EList<ClientProtocolFactoryDef> clientProtocolProvider;

  /**
   * The cached value of the '{@link #getBufferProviderDef() <em>Buffer Provider Def</em>}' reference. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   */
  protected BufferProviderDef bufferProviderDef;

  /**
   * The cached value of the '{@link #getExecutorServiceDef() <em>Executor Service Def</em>}' reference. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   */
  protected ExecutorServiceDef executorServiceDef;

  /**
   * The cached value of the '{@link #getNegotiatorDef() <em>Negotiator Def</em>}' reference. <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   */
  protected NegotiatorDef negotiatorDef;

  /**
   * The cached value of the '{@link #getClientProtocolFactoryDef() <em>Client Protocol Factory Def</em>}' reference
   * list. <!-- begin-user-doc --> <!-- end-user-doc -->
   */
  protected EList<ClientProtocolFactoryDef> clientProtocolFactoryDef;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   * 
   * @generated
   */
  protected ConnectorDefImpl()
  {
    super();
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   * 
   * @return the e class
   * @generated
   */
  @Override
  protected EClass eStaticClass()
  {
    return Net4jDefsPackage.Literals.CONNECTOR_DEF;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   * 
   * @return the user id
   * @generated
   */
  public String getUserID()
  {
    return userID;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   * 
   * @param newUserID
   *          the new user id
   * @generated
   */
  public void setUserID(String newUserID)
  {
    String oldUserID = userID;
    userID = newUserID;
    boolean oldUserIDESet = userIDESet;
    userIDESet = true;
    if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, Net4jDefsPackage.CONNECTOR_DEF__USER_ID, oldUserID, userID,
          !oldUserIDESet));
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public void unsetUserID()
  {
    String oldUserID = userID;
    boolean oldUserIDESet = userIDESet;
    userID = USER_ID_EDEFAULT;
    userIDESet = false;
    if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.UNSET, Net4jDefsPackage.CONNECTOR_DEF__USER_ID, oldUserID,
          USER_ID_EDEFAULT, oldUserIDESet));
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public boolean isSetUserID()
  {
    return userIDESet;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public BufferProviderDef getBufferProvider()
  {
    if (bufferProvider != null && bufferProvider.eIsProxy())
    {
      InternalEObject oldBufferProvider = (InternalEObject)bufferProvider;
      bufferProvider = (BufferProviderDef)eResolveProxy(oldBufferProvider);
      if (bufferProvider != oldBufferProvider)
      {
        if (eNotificationRequired())
          eNotify(new ENotificationImpl(this, Notification.RESOLVE, Net4jDefsPackage.CONNECTOR_DEF__BUFFER_PROVIDER,
              oldBufferProvider, bufferProvider));
      }
    }
    return bufferProvider;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public BufferProviderDef basicGetBufferProvider()
  {
    return bufferProvider;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public void setBufferProvider(BufferProviderDef newBufferProvider)
  {
    BufferProviderDef oldBufferProvider = bufferProvider;
    bufferProvider = newBufferProvider;
    if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, Net4jDefsPackage.CONNECTOR_DEF__BUFFER_PROVIDER,
          oldBufferProvider, bufferProvider));
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public ExecutorServiceDef getExecutorService()
  {
    if (executorService != null && executorService.eIsProxy())
    {
      InternalEObject oldExecutorService = (InternalEObject)executorService;
      executorService = (ExecutorServiceDef)eResolveProxy(oldExecutorService);
      if (executorService != oldExecutorService)
      {
        if (eNotificationRequired())
          eNotify(new ENotificationImpl(this, Notification.RESOLVE, Net4jDefsPackage.CONNECTOR_DEF__EXECUTOR_SERVICE,
              oldExecutorService, executorService));
      }
    }
    return executorService;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public ExecutorServiceDef basicGetExecutorService()
  {
    return executorService;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public void setExecutorService(ExecutorServiceDef newExecutorService)
  {
    ExecutorServiceDef oldExecutorService = executorService;
    executorService = newExecutorService;
    if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, Net4jDefsPackage.CONNECTOR_DEF__EXECUTOR_SERVICE,
          oldExecutorService, executorService));
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public NegotiatorDef getNegotiator()
  {
    if (negotiator != null && negotiator.eIsProxy())
    {
      InternalEObject oldNegotiator = (InternalEObject)negotiator;
      negotiator = (NegotiatorDef)eResolveProxy(oldNegotiator);
      if (negotiator != oldNegotiator)
      {
        if (eNotificationRequired())
          eNotify(new ENotificationImpl(this, Notification.RESOLVE, Net4jDefsPackage.CONNECTOR_DEF__NEGOTIATOR,
              oldNegotiator, negotiator));
      }
    }
    return negotiator;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public NegotiatorDef basicGetNegotiator()
  {
    return negotiator;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public void setNegotiator(NegotiatorDef newNegotiator)
  {
    NegotiatorDef oldNegotiator = negotiator;
    negotiator = newNegotiator;
    boolean oldNegotiatorESet = negotiatorESet;
    negotiatorESet = true;
    if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, Net4jDefsPackage.CONNECTOR_DEF__NEGOTIATOR, oldNegotiator,
          negotiator, !oldNegotiatorESet));
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public void unsetNegotiator()
  {
    NegotiatorDef oldNegotiator = negotiator;
    boolean oldNegotiatorESet = negotiatorESet;
    negotiator = null;
    negotiatorESet = false;
    if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.UNSET, Net4jDefsPackage.CONNECTOR_DEF__NEGOTIATOR,
          oldNegotiator, null, oldNegotiatorESet));
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public boolean isSetNegotiator()
  {
    return negotiatorESet;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public EList<ClientProtocolFactoryDef> getClientProtocolProvider()
  {
    if (clientProtocolProvider == null)
    {
      clientProtocolProvider = new EObjectResolvingEList.Unsettable<ClientProtocolFactoryDef>(
          ClientProtocolFactoryDef.class, this, Net4jDefsPackage.CONNECTOR_DEF__CLIENT_PROTOCOL_PROVIDER);
    }
    return clientProtocolProvider;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public void unsetClientProtocolProvider()
  {
    if (clientProtocolProvider != null)
      ((InternalEList.Unsettable<?>)clientProtocolProvider).unset();
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public boolean isSetClientProtocolProvider()
  {
    return clientProtocolProvider != null && ((InternalEList.Unsettable<?>)clientProtocolProvider).isSet();
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   * 
   * @param featureID
   *          the feature id
   * @param resolve
   *          the resolve
   * @param coreType
   *          the core type
   * @return the object
   * @generated
   */
  @Override
  public Object eGet(int featureID, boolean resolve, boolean coreType)
  {
    switch (featureID)
    {
    case Net4jDefsPackage.CONNECTOR_DEF__USER_ID:
      return getUserID();
    case Net4jDefsPackage.CONNECTOR_DEF__BUFFER_PROVIDER:
      if (resolve)
        return getBufferProvider();
      return basicGetBufferProvider();
    case Net4jDefsPackage.CONNECTOR_DEF__EXECUTOR_SERVICE:
      if (resolve)
        return getExecutorService();
      return basicGetExecutorService();
    case Net4jDefsPackage.CONNECTOR_DEF__NEGOTIATOR:
      if (resolve)
        return getNegotiator();
      return basicGetNegotiator();
    case Net4jDefsPackage.CONNECTOR_DEF__CLIENT_PROTOCOL_PROVIDER:
      return getClientProtocolProvider();
    }
    return super.eGet(featureID, resolve, coreType);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   * 
   * @param featureID
   *          the feature id
   * @param newValue
   *          the new value
   * @generated
   */
  @SuppressWarnings("unchecked")
  @Override
  public void eSet(int featureID, Object newValue)
  {
    switch (featureID)
    {
    case Net4jDefsPackage.CONNECTOR_DEF__USER_ID:
      setUserID((String)newValue);
      return;
    case Net4jDefsPackage.CONNECTOR_DEF__BUFFER_PROVIDER:
      setBufferProvider((BufferProviderDef)newValue);
      return;
    case Net4jDefsPackage.CONNECTOR_DEF__EXECUTOR_SERVICE:
      setExecutorService((ExecutorServiceDef)newValue);
      return;
    case Net4jDefsPackage.CONNECTOR_DEF__NEGOTIATOR:
      setNegotiator((NegotiatorDef)newValue);
      return;
    case Net4jDefsPackage.CONNECTOR_DEF__CLIENT_PROTOCOL_PROVIDER:
      getClientProtocolProvider().clear();
      getClientProtocolProvider().addAll((Collection<? extends ClientProtocolFactoryDef>)newValue);
      return;
    }
    super.eSet(featureID, newValue);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   * 
   * @param featureID
   *          the feature id
   * @generated
   */
  @Override
  public void eUnset(int featureID)
  {
    switch (featureID)
    {
    case Net4jDefsPackage.CONNECTOR_DEF__USER_ID:
      unsetUserID();
      return;
    case Net4jDefsPackage.CONNECTOR_DEF__BUFFER_PROVIDER:
      setBufferProvider((BufferProviderDef)null);
      return;
    case Net4jDefsPackage.CONNECTOR_DEF__EXECUTOR_SERVICE:
      setExecutorService((ExecutorServiceDef)null);
      return;
    case Net4jDefsPackage.CONNECTOR_DEF__NEGOTIATOR:
      unsetNegotiator();
      return;
    case Net4jDefsPackage.CONNECTOR_DEF__CLIENT_PROTOCOL_PROVIDER:
      unsetClientProtocolProvider();
      return;
    }
    super.eUnset(featureID);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   * 
   * @param featureID
   *          the feature id
   * @return true, if e is set
   * @generated
   */
  @Override
  public boolean eIsSet(int featureID)
  {
    switch (featureID)
    {
    case Net4jDefsPackage.CONNECTOR_DEF__USER_ID:
      return isSetUserID();
    case Net4jDefsPackage.CONNECTOR_DEF__BUFFER_PROVIDER:
      return bufferProvider != null;
    case Net4jDefsPackage.CONNECTOR_DEF__EXECUTOR_SERVICE:
      return executorService != null;
    case Net4jDefsPackage.CONNECTOR_DEF__NEGOTIATOR:
      return isSetNegotiator();
    case Net4jDefsPackage.CONNECTOR_DEF__CLIENT_PROTOCOL_PROVIDER:
      return isSetClientProtocolProvider();
    }
    return super.eIsSet(featureID);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   * 
   * @return the string
   * @generated
   */
  @Override
  public String toString()
  {
    if (eIsProxy())
      return super.toString();

    StringBuffer result = new StringBuffer(super.toString());
    result.append(" (userID: ");
    if (userIDESet)
      result.append(userID);
    else
      result.append("<unset>");
    result.append(')');
    return result.toString();
  }

  /**
   * Configures a given {@link ITransportConfig}. It particularly sets
   * <ul>
   * <li>{@link IBufferProvider}</li>
   * <li>{@link ExecutorService}</li>
   * <li>{@link INegotiator}</li>
   * <li>{@link IProtocolProvider}</li>
   * </ul>
   * 
   * @param config
   *          the transport config
   */
  protected void configure(ITransportConfig config)
  {
    config.setBufferProvider((IBufferProvider)getBufferProvider().getInstance());
    config.setReceiveExecutor((ExecutorService)getExecutorService().getInstance());
    if (isSetClientProtocolProvider())
    {
      config.setProtocolProvider(Net4jDefsUtil.createFactoriesProtocolProvider(getClientProtocolProvider()));
    }
    if (isSetNegotiator())
    {
      config.setNegotiator((INegotiator)getNegotiator().getInstance());
    }
  }

  protected void configure(Connector connector)
  {
    if (isSetUserID())
    {
      connector.setUserID(getUserID());
    }
    configure(connector.getConfig());
  }

  @Override
  protected void validateDefinition()
  {
    CheckUtil.checkState(eIsSet(Net4jDefsPackage.CONNECTOR_DEF__BUFFER_PROVIDER), "buffer provider not set!");
    CheckUtil.checkState(eIsSet(Net4jDefsPackage.CONNECTOR_DEF__EXECUTOR_SERVICE), "executor service not set!"); //
    if (isSetClientProtocolProvider())
    {
      CheckUtil.checkState(getClientProtocolProvider().size() >= 1,
          "client protocol provider is set but has no protocol factories!");
    }

  }

  protected abstract IConnector createInstance();

} // ConnectorDefImpl
