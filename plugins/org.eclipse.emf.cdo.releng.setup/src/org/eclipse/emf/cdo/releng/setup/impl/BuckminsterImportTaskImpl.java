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

import org.eclipse.emf.cdo.releng.setup.BuckminsterImportTask;
import org.eclipse.emf.cdo.releng.setup.SetupFactory;
import org.eclipse.emf.cdo.releng.setup.SetupPackage;
import org.eclipse.emf.cdo.releng.setup.SetupTaskContainer;
import org.eclipse.emf.cdo.releng.setup.SetupTaskContext;
import org.eclipse.emf.cdo.releng.setup.Trigger;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Buckminster Import Task</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.releng.setup.impl.BuckminsterImportTaskImpl#getMspec <em>Mspec</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class BuckminsterImportTaskImpl extends BasicMaterializationTaskImpl implements BuckminsterImportTask
{
  /**
   * The default value of the '{@link #getMspec() <em>Mspec</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getMspec()
   * @generated
   * @ordered
   */
  protected static final String MSPEC_EDEFAULT = null;

  /**
   * The cached value of the '{@link #getMspec() <em>Mspec</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getMspec()
   * @generated
   * @ordered
   */
  protected String mspec = MSPEC_EDEFAULT;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected BuckminsterImportTaskImpl()
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
    return SetupPackage.Literals.BUCKMINSTER_IMPORT_TASK;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public String getMspec()
  {
    return mspec;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setMspec(String newMspec)
  {
    String oldMspec = mspec;
    mspec = newMspec;
    if (eNotificationRequired())
    {
      eNotify(new ENotificationImpl(this, Notification.SET, SetupPackage.BUCKMINSTER_IMPORT_TASK__MSPEC, oldMspec,
          mspec));
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
    case SetupPackage.BUCKMINSTER_IMPORT_TASK__MSPEC:
      return getMspec();
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
    case SetupPackage.BUCKMINSTER_IMPORT_TASK__MSPEC:
      setMspec((String)newValue);
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
    case SetupPackage.BUCKMINSTER_IMPORT_TASK__MSPEC:
      setMspec(MSPEC_EDEFAULT);
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
    case SetupPackage.BUCKMINSTER_IMPORT_TASK__MSPEC:
      return MSPEC_EDEFAULT == null ? mspec != null : !MSPEC_EDEFAULT.equals(mspec);
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
    result.append(" (mspec: ");
    result.append(mspec);
    result.append(')');
    return result.toString();
  }

  @Override
  public Set<Trigger> getValidTriggers()
  {
    return Trigger.IDE_TRIGGERS;
  }

  @Override
  protected String getMspec(SetupTaskContext context)
  {
    return getMspec();
  }

  @Override
  public void collectSniffers(List<Sniffer> sniffers)
  {
    sniffers.add(new MSpecSniffer(this));
  }

  /**
   * @author Eike Stepper
   */
  private static class MSpecSniffer extends ResourceSniffer
  {
    public MSpecSniffer(EObject object)
    {
      super(object, "Creates tasks that import the MSpecs and CQueries found in this workspace.");
    }

    @Override
    public void retainDependencies(List<Sniffer> dependencies)
    {
      retainType(dependencies, SourcePathProvider.class);
    }

    @Override
    protected boolean filterResource(IResource resource)
    {
      return resource.getType() == IResource.FILE
          && ("mspec".equals(resource.getFileExtension()) || "cquery".equals(resource.getFileExtension()));
    }

    @Override
    protected void sniff(SetupTaskContainer container, List<Sniffer> dependencies, List<IResource> resources,
        IProgressMonitor monitor) throws Exception
        {
      Map<File, IPath> sourceFolders = getSourceFolders(dependencies);

      boolean first = true;
      for (IResource resource : resources)
      {
        BuckminsterImportTask task = SetupFactory.eINSTANCE.createBuckminsterImportTask();
        task.setMspec(getMspec(resource.getLocation(), sourceFolders));
        task.setTargetPlatform("${setup.branch.dir/tp}");
        container.getSetupTasks().add(task);

        if (first)
        {
          first = false;
        }
        else
        {
          task.setDisabled(true);
        }
      }
        }

    private String getMspec(IPath location, Map<File, IPath> sourcePaths)
    {
      IPath sourcePath = getSourcePath(location, sourcePaths);
      if (sourcePath != null)
      {
        return "${setup.branch.dir|uri}/" + sourcePath;
      }

      return location.toString();
    }

    private static IPath getSourcePath(IPath location, Map<File, IPath> sourcePaths)
    {
      for (Map.Entry<File, IPath> entry : sourcePaths.entrySet())
      {
        IPath relativePath = getRelativePath(location, new Path(entry.getKey().toString()));
        if (relativePath != null)
        {
          return entry.getValue().append(relativePath);
        }
      }

      return null;
    }

    private static IPath getRelativePath(IPath path, IPath base)
    {
      String[] pathSegments = path.segments();
      String[] baseSegments = base.segments();
      int n = getEqualSegmentCount(pathSegments, baseSegments);
      if (n > 0)
      {
        return path.removeFirstSegments(n).makeRelative();
      }

      return null;
    }

    private static int getEqualSegmentCount(String[] segments1, String[] segments2)
    {
      int min = Math.min(segments1.length, segments2.length);
      for (int i = 0; i < min; i++)
      {
        if (!segments1[i].equals(segments2[i]))
        {
          return i;
        }
      }

      return min;
    }
  }
} // BuckminsterImportTaskImpl
