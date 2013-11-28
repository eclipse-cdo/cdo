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

import org.eclipse.emf.cdo.releng.setup.ResourceCopyTask;
import org.eclipse.emf.cdo.releng.setup.SetupPackage;
import org.eclipse.emf.cdo.releng.setup.SetupTaskContext;

import org.eclipse.net4j.util.io.IOUtil;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.resource.URIConverter;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Resource Copy Task</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.releng.setup.impl.ResourceCopyTaskImpl#getSourceURL <em>Source URL</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.releng.setup.impl.ResourceCopyTaskImpl#getTargetURL <em>Target URL</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class ResourceCopyTaskImpl extends SetupTaskImpl implements ResourceCopyTask
{
  /**
   * The default value of the '{@link #getSourceURL() <em>Source URL</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getSourceURL()
   * @generated
   * @ordered
   */
  protected static final String SOURCE_URL_EDEFAULT = null;

  /**
   * The cached value of the '{@link #getSourceURL() <em>Source URL</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getSourceURL()
   * @generated
   * @ordered
   */
  protected String sourceURL = SOURCE_URL_EDEFAULT;

  /**
   * The default value of the '{@link #getTargetURL() <em>Target URL</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getTargetURL()
   * @generated
   * @ordered
   */
  protected static final String TARGET_URL_EDEFAULT = null;

  /**
   * The cached value of the '{@link #getTargetURL() <em>Target URL</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getTargetURL()
   * @generated
   * @ordered
   */
  protected String targetURL = TARGET_URL_EDEFAULT;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected ResourceCopyTaskImpl()
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
    return SetupPackage.Literals.RESOURCE_COPY_TASK;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public String getSourceURL()
  {
    return sourceURL;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setSourceURL(String newSourceURL)
  {
    String oldSourceURL = sourceURL;
    sourceURL = newSourceURL;
    if (eNotificationRequired())
    {
      eNotify(new ENotificationImpl(this, Notification.SET, SetupPackage.RESOURCE_COPY_TASK__SOURCE_URL, oldSourceURL,
          sourceURL));
    }
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public String getTargetURL()
  {
    return targetURL;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setTargetURL(String newTargetURL)
  {
    String oldTargetURL = targetURL;
    targetURL = newTargetURL;
    if (eNotificationRequired())
    {
      eNotify(new ENotificationImpl(this, Notification.SET, SetupPackage.RESOURCE_COPY_TASK__TARGET_URL, oldTargetURL,
          targetURL));
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
    case SetupPackage.RESOURCE_COPY_TASK__SOURCE_URL:
      return getSourceURL();
    case SetupPackage.RESOURCE_COPY_TASK__TARGET_URL:
      return getTargetURL();
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
    case SetupPackage.RESOURCE_COPY_TASK__SOURCE_URL:
      setSourceURL((String)newValue);
      return;
    case SetupPackage.RESOURCE_COPY_TASK__TARGET_URL:
      setTargetURL((String)newValue);
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
    case SetupPackage.RESOURCE_COPY_TASK__SOURCE_URL:
      setSourceURL(SOURCE_URL_EDEFAULT);
      return;
    case SetupPackage.RESOURCE_COPY_TASK__TARGET_URL:
      setTargetURL(TARGET_URL_EDEFAULT);
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
    case SetupPackage.RESOURCE_COPY_TASK__SOURCE_URL:
      return SOURCE_URL_EDEFAULT == null ? sourceURL != null : !SOURCE_URL_EDEFAULT.equals(sourceURL);
    case SetupPackage.RESOURCE_COPY_TASK__TARGET_URL:
      return TARGET_URL_EDEFAULT == null ? targetURL != null : !TARGET_URL_EDEFAULT.equals(targetURL);
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
    result.append(" (sourceURL: ");
    result.append(sourceURL);
    result.append(", targetURL: ");
    result.append(targetURL);
    result.append(')');
    return result.toString();
  }

  public boolean isNeeded(SetupTaskContext context) throws Exception
  {
    URI sourceURI = createResolvedURI(getSourceURL());
    URI targetURI = createResolvedURI(getTargetURL());
    URIConverter uriConverter = context.getURIConverter();
    if (targetURI.hasTrailingPathSeparator())
    {
      if (sourceURI.hasTrailingPathSeparator())
      {
        // TODO
      }
      else if (uriConverter.exists(sourceURI, null))
      {
        return !uriConverter.exists(targetURI.appendSegment(sourceURI.lastSegment()), null);
      }
    }
    else if (uriConverter.exists(sourceURI, null))
    {
      return !uriConverter.exists(targetURI, null);
    }

    return false;
  }

  public void perform(SetupTaskContext context) throws Exception
  {
    URI sourceURI = createResolvedURI(getSourceURL());
    URI targetURI = createResolvedURI(getTargetURL());
    URIConverter uriConverter = context.getURIConverter();

    context.log("Copying " + uriConverter.normalize(sourceURI) + " to " + uriConverter.normalize(targetURI));

    if (targetURI.hasTrailingPathSeparator())
    {
      if (sourceURI.hasTrailingPathSeparator())
      {
        // TODO
      }
      else if (uriConverter.exists(sourceURI, null))
      {
        InputStream input = null;
        OutputStream output = null;

        try
        {
          input = uriConverter.createInputStream(sourceURI);
          output = uriConverter.createOutputStream(targetURI.appendSegment(sourceURI.lastSegment()), null);
          IOUtil.copy(input, output);
        }
        finally
        {
          IOUtil.closeSilent(input);
          IOUtil.closeSilent(output);
        }
      }
    }
    else if (uriConverter.exists(sourceURI, null))
    {
      InputStream input = null;
      OutputStream output = null;

      try
      {
        input = uriConverter.createInputStream(sourceURI);
        output = uriConverter.createOutputStream(targetURI, null);
        IOUtil.copy(input, output);
      }
      finally
      {
        IOUtil.closeSilent(input);
        IOUtil.closeSilent(output);
      }
    }
  }

} // ResourceCopyTaskImpl
