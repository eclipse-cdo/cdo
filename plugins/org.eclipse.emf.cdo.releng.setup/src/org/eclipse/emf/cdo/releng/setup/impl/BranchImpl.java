/*
 * Copyright (c) 2013 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.releng.setup.impl;

import org.eclipse.emf.cdo.releng.setup.ApiBaseline;
import org.eclipse.emf.cdo.releng.setup.Branch;
import org.eclipse.emf.cdo.releng.setup.GitClone;
import org.eclipse.emf.cdo.releng.setup.Project;
import org.eclipse.emf.cdo.releng.setup.SetupPackage;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.util.EObjectContainmentWithInverseEList;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.util.InternalEList;
import java.io.File;
import java.util.Collection;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Branch</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.releng.setup.impl.BranchImpl#getProject <em>Project</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.releng.setup.impl.BranchImpl#getName <em>Name</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.releng.setup.impl.BranchImpl#getGitClones <em>Git Clones</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.releng.setup.impl.BranchImpl#getApiBaseline <em>Api Baseline</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.releng.setup.impl.BranchImpl#getMspecFilePath <em>Mspec File Path</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.releng.setup.impl.BranchImpl#getCloneVariableName <em>Clone Variable Name</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class BranchImpl extends ToolInstallationImpl implements Branch
{
  /**
   * The default value of the '{@link #getName() <em>Name</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getName()
   * @generated
   * @ordered
   */
  protected static final String NAME_EDEFAULT = null;

  /**
   * The cached value of the '{@link #getName() <em>Name</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getName()
   * @generated
   * @ordered
   */
  protected String name = NAME_EDEFAULT;

  /**
   * The cached value of the '{@link #getGitClones() <em>Git Clones</em>}' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getGitClones()
   * @generated
   * @ordered
   */
  protected EList<GitClone> gitClones;

  /**
   * The cached value of the '{@link #getApiBaseline() <em>Api Baseline</em>}' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getApiBaseline()
   * @generated
   * @ordered
   */
  protected ApiBaseline apiBaseline;

  /**
   * The default value of the '{@link #getMspecFilePath() <em>Mspec File Path</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getMspecFilePath()
   * @generated
   * @ordered
   */
  protected static final String MSPEC_FILE_PATH_EDEFAULT = null;

  /**
   * The cached value of the '{@link #getMspecFilePath() <em>Mspec File Path</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getMspecFilePath()
   * @generated
   * @ordered
   */
  protected String mspecFilePath = MSPEC_FILE_PATH_EDEFAULT;

  /**
   * The default value of the '{@link #getCloneVariableName() <em>Clone Variable Name</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getCloneVariableName()
   * @generated
   * @ordered
   */
  protected static final String CLONE_VARIABLE_NAME_EDEFAULT = null;

  /**
   * The cached value of the '{@link #getCloneVariableName() <em>Clone Variable Name</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getCloneVariableName()
   * @generated
   * @ordered
   */
  protected String cloneVariableName = CLONE_VARIABLE_NAME_EDEFAULT;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected BranchImpl()
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
    return SetupPackage.Literals.BRANCH;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public Project getProject()
  {
    if (eContainerFeatureID() != SetupPackage.BRANCH__PROJECT)
      return null;
    return (Project)eInternalContainer();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public NotificationChain basicSetProject(Project newProject, NotificationChain msgs)
  {
    msgs = eBasicSetContainer((InternalEObject)newProject, SetupPackage.BRANCH__PROJECT, msgs);
    return msgs;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setProject(Project newProject)
  {
    if (newProject != eInternalContainer()
        || (eContainerFeatureID() != SetupPackage.BRANCH__PROJECT && newProject != null))
    {
      if (EcoreUtil.isAncestor(this, newProject))
        throw new IllegalArgumentException("Recursive containment not allowed for " + toString());
      NotificationChain msgs = null;
      if (eInternalContainer() != null)
        msgs = eBasicRemoveFromContainer(msgs);
      if (newProject != null)
        msgs = ((InternalEObject)newProject).eInverseAdd(this, SetupPackage.PROJECT__BRANCHES, Project.class, msgs);
      msgs = basicSetProject(newProject, msgs);
      if (msgs != null)
        msgs.dispatch();
    }
    else if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, SetupPackage.BRANCH__PROJECT, newProject, newProject));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public String getName()
  {
    return name;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setName(String newName)
  {
    String oldName = name;
    name = newName;
    if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, SetupPackage.BRANCH__NAME, oldName, name));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EList<GitClone> getGitClones()
  {
    if (gitClones == null)
    {
      gitClones = new EObjectContainmentWithInverseEList<GitClone>(GitClone.class, this,
          SetupPackage.BRANCH__GIT_CLONES, SetupPackage.GIT_CLONE__BRANCH);
    }
    return gitClones;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public ApiBaseline getApiBaseline()
  {
    if (apiBaseline != null && apiBaseline.eIsProxy())
    {
      InternalEObject oldApiBaseline = (InternalEObject)apiBaseline;
      apiBaseline = (ApiBaseline)eResolveProxy(oldApiBaseline);
      if (apiBaseline != oldApiBaseline)
      {
        if (eNotificationRequired())
          eNotify(new ENotificationImpl(this, Notification.RESOLVE, SetupPackage.BRANCH__API_BASELINE, oldApiBaseline,
              apiBaseline));
      }
    }
    return apiBaseline;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public ApiBaseline basicGetApiBaseline()
  {
    return apiBaseline;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setApiBaseline(ApiBaseline newApiBaseline)
  {
    ApiBaseline oldApiBaseline = apiBaseline;
    apiBaseline = newApiBaseline;
    if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, SetupPackage.BRANCH__API_BASELINE, oldApiBaseline,
          apiBaseline));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public String getMspecFilePath()
  {
    return mspecFilePath;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setMspecFilePath(String newMspecFilePath)
  {
    String oldMspecFilePath = mspecFilePath;
    mspecFilePath = newMspecFilePath;
    if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, SetupPackage.BRANCH__MSPEC_FILE_PATH, oldMspecFilePath,
          mspecFilePath));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public String getCloneVariableName()
  {
    return cloneVariableName;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setCloneVariableName(String newCloneVariableName)
  {
    String oldCloneVariableName = cloneVariableName;
    cloneVariableName = newCloneVariableName;
    if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, SetupPackage.BRANCH__CLONE_VARIABLE_NAME,
          oldCloneVariableName, cloneVariableName));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public boolean isInstalled(String installFolder)
  {
    Resource resource = eResource();
    if (resource != null)
    {
      ResourceSet resourceSet = resource.getResourceSet();
      if (resourceSet != null)
      {
        URI uri = getURI(installFolder);
        return resourceSet.getURIConverter().exists(uri, null);
      }
    }

    return false;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public URI getURI(String installFolder)
  {
    File folder = new File(installFolder);
    File projectFolder = new File(folder, getProject().getName().toLowerCase());
    File branchFolder = new File(projectFolder, getName().toLowerCase());
    File setupFile = new File(branchFolder, "setup.xmi");
    return URI.createFileURI(setupFile.getAbsolutePath());
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
    case SetupPackage.BRANCH__PROJECT:
      if (eInternalContainer() != null)
        msgs = eBasicRemoveFromContainer(msgs);
      return basicSetProject((Project)otherEnd, msgs);
    case SetupPackage.BRANCH__GIT_CLONES:
      return ((InternalEList<InternalEObject>)(InternalEList<?>)getGitClones()).basicAdd(otherEnd, msgs);
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
    case SetupPackage.BRANCH__PROJECT:
      return basicSetProject(null, msgs);
    case SetupPackage.BRANCH__GIT_CLONES:
      return ((InternalEList<?>)getGitClones()).basicRemove(otherEnd, msgs);
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
    case SetupPackage.BRANCH__PROJECT:
      return eInternalContainer().eInverseRemove(this, SetupPackage.PROJECT__BRANCHES, Project.class, msgs);
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
    case SetupPackage.BRANCH__PROJECT:
      return getProject();
    case SetupPackage.BRANCH__NAME:
      return getName();
    case SetupPackage.BRANCH__GIT_CLONES:
      return getGitClones();
    case SetupPackage.BRANCH__API_BASELINE:
      if (resolve)
        return getApiBaseline();
      return basicGetApiBaseline();
    case SetupPackage.BRANCH__MSPEC_FILE_PATH:
      return getMspecFilePath();
    case SetupPackage.BRANCH__CLONE_VARIABLE_NAME:
      return getCloneVariableName();
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
    case SetupPackage.BRANCH__PROJECT:
      setProject((Project)newValue);
      return;
    case SetupPackage.BRANCH__NAME:
      setName((String)newValue);
      return;
    case SetupPackage.BRANCH__GIT_CLONES:
      getGitClones().clear();
      getGitClones().addAll((Collection<? extends GitClone>)newValue);
      return;
    case SetupPackage.BRANCH__API_BASELINE:
      setApiBaseline((ApiBaseline)newValue);
      return;
    case SetupPackage.BRANCH__MSPEC_FILE_PATH:
      setMspecFilePath((String)newValue);
      return;
    case SetupPackage.BRANCH__CLONE_VARIABLE_NAME:
      setCloneVariableName((String)newValue);
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
    case SetupPackage.BRANCH__PROJECT:
      setProject((Project)null);
      return;
    case SetupPackage.BRANCH__NAME:
      setName(NAME_EDEFAULT);
      return;
    case SetupPackage.BRANCH__GIT_CLONES:
      getGitClones().clear();
      return;
    case SetupPackage.BRANCH__API_BASELINE:
      setApiBaseline((ApiBaseline)null);
      return;
    case SetupPackage.BRANCH__MSPEC_FILE_PATH:
      setMspecFilePath(MSPEC_FILE_PATH_EDEFAULT);
      return;
    case SetupPackage.BRANCH__CLONE_VARIABLE_NAME:
      setCloneVariableName(CLONE_VARIABLE_NAME_EDEFAULT);
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
    case SetupPackage.BRANCH__PROJECT:
      return getProject() != null;
    case SetupPackage.BRANCH__NAME:
      return NAME_EDEFAULT == null ? name != null : !NAME_EDEFAULT.equals(name);
    case SetupPackage.BRANCH__GIT_CLONES:
      return gitClones != null && !gitClones.isEmpty();
    case SetupPackage.BRANCH__API_BASELINE:
      return apiBaseline != null;
    case SetupPackage.BRANCH__MSPEC_FILE_PATH:
      return MSPEC_FILE_PATH_EDEFAULT == null ? mspecFilePath != null : !MSPEC_FILE_PATH_EDEFAULT.equals(mspecFilePath);
    case SetupPackage.BRANCH__CLONE_VARIABLE_NAME:
      return CLONE_VARIABLE_NAME_EDEFAULT == null ? cloneVariableName != null : !CLONE_VARIABLE_NAME_EDEFAULT
          .equals(cloneVariableName);
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
      return super.toString();

    StringBuffer result = new StringBuffer(super.toString());
    result.append(" (name: ");
    result.append(name);
    result.append(", mspecFilePath: ");
    result.append(mspecFilePath);
    result.append(", cloneVariableName: ");
    result.append(cloneVariableName);
    result.append(')');
    return result.toString();
  }

} // BranchImpl
