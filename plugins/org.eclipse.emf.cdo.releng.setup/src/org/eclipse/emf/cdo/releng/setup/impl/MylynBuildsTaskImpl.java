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

import org.eclipse.emf.cdo.releng.setup.BuildPlan;
import org.eclipse.emf.cdo.releng.setup.MylynBuildsTask;
import org.eclipse.emf.cdo.releng.setup.SetupPackage;
import org.eclipse.emf.cdo.releng.setup.SetupTaskContext;
import org.eclipse.emf.cdo.releng.setup.Trigger;
import org.eclipse.emf.cdo.releng.setup.util.UIUtil;

import org.eclipse.net4j.util.ObjectUtil;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.util.EObjectContainmentEList;
import org.eclipse.emf.ecore.util.InternalEList;

import org.eclipse.mylyn.builds.core.IBuildPlan;
import org.eclipse.mylyn.builds.core.IBuildServer;
import org.eclipse.mylyn.builds.internal.core.BuildFactory;
import org.eclipse.mylyn.builds.internal.core.BuildModel;
import org.eclipse.mylyn.builds.ui.BuildsUi;
import org.eclipse.mylyn.commons.repositories.core.RepositoryLocation;
import org.eclipse.mylyn.internal.builds.ui.BuildsUiInternal;
import org.eclipse.mylyn.internal.builds.ui.BuildsUiPlugin;
import org.eclipse.swt.widgets.Display;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Mylyn Builds Task</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.releng.setup.impl.MylynBuildsTaskImpl#getConnectorKind <em>Connector Kind</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.releng.setup.impl.MylynBuildsTaskImpl#getServerURL <em>Server URL</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.releng.setup.impl.MylynBuildsTaskImpl#getBuildPlans <em>Build Plans</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
@SuppressWarnings("restriction")
public class MylynBuildsTaskImpl extends SetupTaskImpl implements MylynBuildsTask
{
  /**
   * The default value of the '{@link #getConnectorKind() <em>Connector Kind</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getConnectorKind()
   * @generated
   * @ordered
   */
  protected static final String CONNECTOR_KIND_EDEFAULT = "org.eclipse.mylyn.hudson";

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
   * The default value of the '{@link #getServerURL() <em>Server URL</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getServerURL()
   * @generated
   * @ordered
   */
  protected static final String SERVER_URL_EDEFAULT = null;

  /**
   * The cached value of the '{@link #getServerURL() <em>Server URL</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getServerURL()
   * @generated
   * @ordered
   */
  protected String serverURL = SERVER_URL_EDEFAULT;

  /**
   * The cached value of the '{@link #getBuildPlans() <em>Build Plans</em>}' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getBuildPlans()
   * @generated
   * @ordered
   */
  protected EList<BuildPlan> buildPlans;

  private transient MylynHelper mylynHelper;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected MylynBuildsTaskImpl()
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
    return SetupPackage.Literals.MYLYN_BUILDS_TASK;
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
      eNotify(new ENotificationImpl(this, Notification.SET, SetupPackage.MYLYN_BUILDS_TASK__CONNECTOR_KIND,
          oldConnectorKind, connectorKind));
    }
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public String getServerURL()
  {
    return serverURL;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setServerURL(String newServerURL)
  {
    String oldServerURL = serverURL;
    serverURL = newServerURL;
    if (eNotificationRequired())
    {
      eNotify(new ENotificationImpl(this, Notification.SET, SetupPackage.MYLYN_BUILDS_TASK__SERVER_URL, oldServerURL,
          serverURL));
    }
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EList<BuildPlan> getBuildPlans()
  {
    if (buildPlans == null)
    {
      buildPlans = new EObjectContainmentEList.Resolving<BuildPlan>(BuildPlan.class, this,
          SetupPackage.MYLYN_BUILDS_TASK__BUILD_PLANS);
    }
    return buildPlans;
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
    case SetupPackage.MYLYN_BUILDS_TASK__BUILD_PLANS:
      return ((InternalEList<?>)getBuildPlans()).basicRemove(otherEnd, msgs);
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
    case SetupPackage.MYLYN_BUILDS_TASK__CONNECTOR_KIND:
      return getConnectorKind();
    case SetupPackage.MYLYN_BUILDS_TASK__SERVER_URL:
      return getServerURL();
    case SetupPackage.MYLYN_BUILDS_TASK__BUILD_PLANS:
      return getBuildPlans();
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
    case SetupPackage.MYLYN_BUILDS_TASK__CONNECTOR_KIND:
      setConnectorKind((String)newValue);
      return;
    case SetupPackage.MYLYN_BUILDS_TASK__SERVER_URL:
      setServerURL((String)newValue);
      return;
    case SetupPackage.MYLYN_BUILDS_TASK__BUILD_PLANS:
      getBuildPlans().clear();
      getBuildPlans().addAll((Collection<? extends BuildPlan>)newValue);
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
    case SetupPackage.MYLYN_BUILDS_TASK__CONNECTOR_KIND:
      setConnectorKind(CONNECTOR_KIND_EDEFAULT);
      return;
    case SetupPackage.MYLYN_BUILDS_TASK__SERVER_URL:
      setServerURL(SERVER_URL_EDEFAULT);
      return;
    case SetupPackage.MYLYN_BUILDS_TASK__BUILD_PLANS:
      getBuildPlans().clear();
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
    case SetupPackage.MYLYN_BUILDS_TASK__CONNECTOR_KIND:
      return CONNECTOR_KIND_EDEFAULT == null ? connectorKind != null : !CONNECTOR_KIND_EDEFAULT.equals(connectorKind);
    case SetupPackage.MYLYN_BUILDS_TASK__SERVER_URL:
      return SERVER_URL_EDEFAULT == null ? serverURL != null : !SERVER_URL_EDEFAULT.equals(serverURL);
    case SetupPackage.MYLYN_BUILDS_TASK__BUILD_PLANS:
      return buildPlans != null && !buildPlans.isEmpty();
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
    result.append(", serverURL: ");
    result.append(serverURL);
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
    public boolean isNeeded(SetupTaskContext context, MylynBuildsTask task) throws Exception;

    public void perform(SetupTaskContext context, MylynBuildsTask task) throws Exception;
  }

  /**
   * @author Eike Stepper
   */
  private static class MylynHelperImpl implements MylynHelper
  {
    private IBuildServer server;

    private Set<String> buildPlanNames = new HashSet<String>();

    public boolean isNeeded(SetupTaskContext context, MylynBuildsTask task) throws Exception
    {
      String serverURL = task.getServerURL();
      server = getServer(serverURL);

      for (BuildPlan buildPlan : task.getBuildPlans())
      {
        buildPlanNames.add(buildPlan.getName());
      }

      for (IBuildPlan buildPlan : BuildsUi.getModel().getPlans())
      {
        if (buildPlan.getServer() == server)
        {
          buildPlanNames.remove(buildPlan.getName());
        }
      }

      if (server == null)
      {
        return true;
      }

      if (!buildPlanNames.isEmpty())
      {
        return true;
      }

      return false;
    }

    public void perform(final SetupTaskContext context, final MylynBuildsTask task) throws Exception
    {
      UIUtil.getDisplay().syncExec(new Runnable()
      {
        public void run()
        {
          String connectorKind = task.getConnectorKind();
          String serverURL = task.getServerURL();

          if (server == null)
          {
            context.log("Adding " + connectorKind + " server: " + serverURL);

            server = BuildsUi.createServer(connectorKind);
            server.setLocation(new RepositoryLocation(serverURL));
            server.setUrl(serverURL);
            server.setName(serverURL);

            BuildsUiInternal.getModel().getServers().add(server);
          }

          for (String buildPlanName : buildPlanNames)
          {
            context.log("Adding " + connectorKind + " build plan: " + buildPlanName);

            IBuildPlan buildPlan = BuildFactory.eINSTANCE.createBuildPlan();
            buildPlan.setId(buildPlanName);
            buildPlan.setName(buildPlanName);
            buildPlan.setServer(server);
            buildPlan.setSelected(true);

            BuildsUiInternal.getModel().getPlans().add(buildPlan);
          }

          BuildsUiPlugin.getDefault().refreshBuilds();
        }
      });
    }

    private IBuildServer getServer(String serverURL)
    {
      for (IBuildServer server : BuildsUi.getModel().getServers())
      {
        if (ObjectUtil.equals(server.getUrl(), serverURL))
        {
          return server;
        }
      }

      return null;
    }

    public static MylynHelper create()
    {
      return new MylynHelperImpl();
    }
  }

  public static class MylynBuildHelper
  {
    public static void perform(SetupTaskContext context, final String location, final String name,
        final EList<BuildPlan> buildPlans) throws Exception
    {
      Display.getDefault().asyncExec(new Runnable()
      {
        public void run()
        {
          BuildModel model = BuildsUiInternal.getModel();
          List<IBuildServer> servers = model.getServers();
          IBuildServer server = BuildsUi.createServer("");
          if (!ObjectUtil.isEmpty(name))
          {
            server.setName(name);
          }
          server.setLocation(new RepositoryLocation(location));
          server.setUrl(location);
          server.getAttributes().put("id", location);
          server.getAttributes().put("url", location);
          servers.add(server);

          List<IBuildPlan> plans = model.getPlans();
          IBuildPlan plan;
          for (BuildPlan buildPlan : buildPlans)
          {
            plan = BuildFactory.eINSTANCE.createBuildPlan();
            plan.setId(buildPlan.getName());
            plan.setName(buildPlan.getName());
            plan.setServer(server);
            plan.setSelected(true);
            plans.add(plan);
          }
        }
      });
    }
  }

} // MylynBuildsTaskImpl
