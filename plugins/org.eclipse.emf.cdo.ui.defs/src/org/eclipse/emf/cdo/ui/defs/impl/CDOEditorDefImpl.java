/*
 * Copyright (c) 2008-2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Andre Dietisheim - initial API and implementation
 *    Eike Stepper - maintenance
 */
package org.eclipse.emf.cdo.ui.defs.impl;

import org.eclipse.emf.cdo.defs.CDOViewDef;
import org.eclipse.emf.cdo.internal.ui.editor.CDOEditor;
import org.eclipse.emf.cdo.ui.CDOEditorUtil;
import org.eclipse.emf.cdo.ui.defs.CDOEditorDef;
import org.eclipse.emf.cdo.ui.defs.CDOUIDefsPackage;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.net4j.util.CheckUtil;
import org.eclipse.net4j.util.defs.DefException;
import org.eclipse.net4j.util.ui.UIUtil;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;

import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

/**
 * <!-- begin-user-doc --> An implementation of the model object ' <em><b>View Editor Def</b></em>'. <!-- end-user-doc
 * -->
 * <p>
 * The following features are implemented:
 * <ul>
 * <li>{@link org.eclipse.emf.cdo.ui.defs.impl.CDOEditorDefImpl#getCdoView <em>Cdo View</em>}</li>
 * <li>
 * {@link org.eclipse.emf.cdo.ui.defs.impl.CDOEditorDefImpl#getResourcePath <em>Resource Path</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class CDOEditorDefImpl extends EditorDefImpl implements CDOEditorDef
{

  /**
   * The cached value of the '{@link #getCdoView() <em>Cdo View</em>}' reference.
   * <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * @see #getCdoView()
   * @generated
   * @ordered
   */
  protected CDOViewDef cdoView;

  /**
   * The default value of the '{@link #getResourcePath() <em>Resource Path</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getResourcePath()
   * @generated
   * @ordered
   */
  protected static final String RESOURCE_PATH_EDEFAULT = null;

  /**
   * The cached value of the '{@link #getResourcePath() <em>Resource Path</em>}' attribute.
   * <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * @see #getResourcePath()
   * @generated
   * @ordered
   */
  protected String resourcePath = RESOURCE_PATH_EDEFAULT;

  protected IPartListener partListener = new IPartListener()
  {

    public void partActivated(IWorkbenchPart part)
    {
      // ignore
    }

    public void partBroughtToTop(IWorkbenchPart part)
    {
      // ignore
    }

    public void partClosed(IWorkbenchPart part)
    {
      Object instance = getInternalInstance();
      if (part == instance)
      {
        handleDeactivation(instance);
      }
    }

    public void partDeactivated(IWorkbenchPart part)
    {
      // ignore
    }

    public void partOpened(IWorkbenchPart part)
    {
      // ignore
    }
  };

  @Override
  public void unsetInstance()
  {
    handleDeactivation(getInternalInstance());
  }

  @Override
  protected void handleDeactivation(Object instance)
  {
    CheckUtil.checkState(instance, "the instance ist not created yet or already close!");
    closeEditor((CDOEditor)instance);
    super.handleDeactivation(instance);
  }

  @Override
  protected void unwireInstance(Object instance)
  {
    IWorkbenchWindow activeWorkbenchWindow = UIUtil.getActiveWorkbenchWindow();
    if (activeWorkbenchWindow != null)
    {
      activeWorkbenchWindow.getPartService().removePartListener(partListener);
    }
  }

  @Override
  protected void wireInstance(Object instance)
  {
    IWorkbenchWindow activeWorkbenchWindow = UIUtil.getActiveWorkbenchWindow();
    if (activeWorkbenchWindow != null)
    {
      activeWorkbenchWindow.getPartService().addPartListener(partListener);
    }
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  protected CDOEditorDefImpl()
  {
    super();
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  protected EClass eStaticClass()
  {
    return CDOUIDefsPackage.Literals.CDO_EDITOR_DEF;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  public CDOViewDef getCdoView()
  {
    if (cdoView != null && cdoView.eIsProxy())
    {
      InternalEObject oldCdoView = (InternalEObject)cdoView;
      cdoView = (CDOViewDef)eResolveProxy(oldCdoView);
      if (cdoView != oldCdoView)
      {
        if (eNotificationRequired())
        {
          eNotify(new ENotificationImpl(this, Notification.RESOLVE, CDOUIDefsPackage.CDO_EDITOR_DEF__CDO_VIEW,
              oldCdoView, cdoView));
        }
      }
    }
    return cdoView;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  public CDOViewDef basicGetCdoView()
  {
    return cdoView;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  public void setCdoView(CDOViewDef newCdoView)
  {
    CDOViewDef oldCdoView = cdoView;
    cdoView = newCdoView;
    if (eNotificationRequired())
    {
      eNotify(new ENotificationImpl(this, Notification.SET, CDOUIDefsPackage.CDO_EDITOR_DEF__CDO_VIEW, oldCdoView,
          cdoView));
    }
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  public String getResourcePath()
  {
    return resourcePath;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  public void setResourcePath(String newResourcePath)
  {
    String oldResourcePath = resourcePath;
    resourcePath = newResourcePath;
    if (eNotificationRequired())
    {
      eNotify(new ENotificationImpl(this, Notification.SET, CDOUIDefsPackage.CDO_EDITOR_DEF__RESOURCE_PATH,
          oldResourcePath, resourcePath));
    }
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Object eGet(int featureID, boolean resolve, boolean coreType)
  {
    switch (featureID)
    {
    case CDOUIDefsPackage.CDO_EDITOR_DEF__CDO_VIEW:
      if (resolve)
      {
        return getCdoView();
      }
      return basicGetCdoView();
    case CDOUIDefsPackage.CDO_EDITOR_DEF__RESOURCE_PATH:
      return getResourcePath();
    }
    return super.eGet(featureID, resolve, coreType);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void eSet(int featureID, Object newValue)
  {
    switch (featureID)
    {
    case CDOUIDefsPackage.CDO_EDITOR_DEF__CDO_VIEW:
      setCdoView((CDOViewDef)newValue);
      return;
    case CDOUIDefsPackage.CDO_EDITOR_DEF__RESOURCE_PATH:
      setResourcePath((String)newValue);
      return;
    }
    super.eSet(featureID, newValue);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void eUnset(int featureID)
  {
    switch (featureID)
    {
    case CDOUIDefsPackage.CDO_EDITOR_DEF__CDO_VIEW:
      setCdoView((CDOViewDef)null);
      return;
    case CDOUIDefsPackage.CDO_EDITOR_DEF__RESOURCE_PATH:
      setResourcePath(RESOURCE_PATH_EDEFAULT);
      return;
    }
    super.eUnset(featureID);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public boolean eIsSet(int featureID)
  {
    switch (featureID)
    {
    case CDOUIDefsPackage.CDO_EDITOR_DEF__CDO_VIEW:
      return cdoView != null;
    case CDOUIDefsPackage.CDO_EDITOR_DEF__RESOURCE_PATH:
      return RESOURCE_PATH_EDEFAULT == null ? resourcePath != null : !RESOURCE_PATH_EDEFAULT.equals(resourcePath);
    }
    return super.eIsSet(featureID);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
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
    result.append(" (resourcePath: ");
    result.append(resourcePath);
    result.append(')');
    return result.toString();
  }

  @Override
  protected Object createInstance()
  {
    return openCDOEditor(getResourcePath());
  }

  /**
   * Open a {@link CDOEditor} for a given resource path.
   *
   * @param resourcePath
   *          the resource path to open an editor for
   * @return the editor part that was opened, <tt>null</tt> if an exception occurred while opening it
   * @throws DefException
   *           if the Editor could not be opened
   */
  private IEditorPart openCDOEditor(String resourcePath)
  {
    // TODO Andre: Why not use CDOEditorUtil.openEditor()?
    IEditorInput input = CDOEditorUtil.createCDOEditorInput((CDOView)getCdoView().getInstance(), resourcePath, false);
    IWorkbenchWindow workbenchWindow = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
    CheckUtil.checkState(workbenchWindow != null && workbenchWindow.getActivePage() != null,
        "no active window or no active page present!");
    try
    {
      return workbenchWindow.getActivePage().openEditor(input, doGetEditorID());
    }
    catch (PartInitException e)
    {
      throw new DefException("the CDOEditor could not be opened!", e);
    }
  }

  /**
   * Gets the editor id. if the public property #editorID is set, this ID is returned. Otherwise the default
   * CDOEditor#EDITOR_ID is returned.
   *
   * @param editorID
   *          the editor id
   * @return the string
   */
  private String doGetEditorID()
  {
    String editorID = getEditorID();
    if (editorID == null || editorID.length() <= 0)
    {
      editorID = CDOEditorUtil.getEditorID();
    }
    return editorID;
  }

  private void closeEditor(IEditorPart editorPart)
  {
    IWorkbenchPage workbenchPage = UIUtil.getActiveWorkbenchPage();
    if (workbenchPage != null)
    {
      workbenchPage.closeEditor(editorPart, true);
    }
  }

  @Override
  protected void validateDefinition()
  {
    super.validateDefinition();
    CheckUtil.checkState(getResourcePath() != null && getResourcePath().length() > 0, "resource paths are not set!");
    CheckUtil.checkState(getCdoView() != null, "cdo view is not set!");
  }

} // ViewEditorDefImpl
