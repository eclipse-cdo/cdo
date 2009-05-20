/**
 * Copyright (c) 2004 - 2009 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.internal.migrator;

import org.eclipse.emf.cdo.internal.messages.Messages;

import org.eclipse.emf.codegen.ecore.genmodel.GenDelegationKind;
import org.eclipse.emf.codegen.ecore.genmodel.GenModel;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.WrappedException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

/**
 * @author Eike Stepper
 */
public abstract class CDOMigrator
{
  public static final String ROOT_EXTENDS_CLASS = "org.eclipse.emf.internal.cdo.CDOObjectImpl"; //$NON-NLS-1$

  public static final String ROOT_EXTENDS_INTERFACE = "org.eclipse.emf.cdo.CDOObject"; //$NON-NLS-1$

  public static final String PLUGIN_VARIABLE = "CDO=org.eclipse.emf.cdo"; //$NON-NLS-1$

  public static final String CDO_MF_CONTENTS = Messages.getString("CDOMigrator.3") + "\n"; //$NON-NLS-1$ //$NON-NLS-2$

  private CDOMigrator()
  {
  }

  public static String adjustGenModel(GenModel genModel, IProject project)
  {
    StringBuilder builder = new StringBuilder();

    if (genModel.getFeatureDelegation() != GenDelegationKind.REFLECTIVE_LITERAL)
    {
      genModel.setFeatureDelegation(GenDelegationKind.REFLECTIVE_LITERAL);
      builder.append(Messages.getString("CDOMigrator.4")); //$NON-NLS-1$
      builder.append(GenDelegationKind.REFLECTIVE_LITERAL);
      builder.append("\n"); //$NON-NLS-1$
    }

    if (!ROOT_EXTENDS_CLASS.equals(genModel.getRootExtendsClass()))
    {
      genModel.setRootExtendsClass(ROOT_EXTENDS_CLASS);
      builder.append(Messages.getString("CDOMigrator.6")); //$NON-NLS-1$
      builder.append(ROOT_EXTENDS_CLASS);
      builder.append("\n"); //$NON-NLS-1$
    }

    if (!ROOT_EXTENDS_INTERFACE.equals(genModel.getRootExtendsInterface()))
    {
      genModel.setRootExtendsInterface(ROOT_EXTENDS_INTERFACE);
      builder.append(Messages.getString("CDOMigrator.8")); //$NON-NLS-1$
      builder.append(ROOT_EXTENDS_INTERFACE);
      builder.append("\n"); //$NON-NLS-1$
    }

    EList<String> pluginVariables = genModel.getModelPluginVariables();
    if (!pluginVariables.contains(PLUGIN_VARIABLE))
    {
      pluginVariables.add(PLUGIN_VARIABLE);
      builder.append(Messages.getString("CDOMigrator.10")); //$NON-NLS-1$
      builder.append(PLUGIN_VARIABLE);
      builder.append("\n"); //$NON-NLS-1$
    }

    IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
    IFolder modelFolder = root.getFolder(new Path(genModel.getModelDirectory()));
    IProject modelProject = modelFolder.getProject();
    if (!modelProject.exists())
    {
      try
      {
        modelProject.create(new NullProgressMonitor());
        builder.append(Messages.getString("CDOMigrator.12") + "\n"); //$NON-NLS-1$ //$NON-NLS-2$
      }
      catch (CoreException ex)
      {
        throw new WrappedException(ex);
      }
    }

    if (!modelProject.isOpen())
    {
      try
      {
        modelProject.open(new NullProgressMonitor());
        builder.append(Messages.getString("CDOMigrator.13") + "\n"); //$NON-NLS-1$ //$NON-NLS-2$
      }
      catch (CoreException ex)
      {
        throw new WrappedException(ex);
      }
    }

    IFolder folder = modelProject.getFolder("META-INF"); //$NON-NLS-1$
    if (!folder.exists())
    {
      try
      {
        folder.create(true, true, new NullProgressMonitor());
        builder.append(Messages.getString("CDOMigrator.15") + "\n"); //$NON-NLS-1$ //$NON-NLS-2$
      }
      catch (CoreException ex)
      {
        throw new WrappedException(ex);
      }
    }

    IFile file = folder.getFile("CDO.MF"); //$NON-NLS-1$
    if (!file.exists())
    {
      try
      {
        InputStream contents = new ByteArrayInputStream(CDO_MF_CONTENTS.getBytes());
        file.create(contents, true, new NullProgressMonitor());
        builder.append(Messages.getString("CDOMigrator.17") + "\n"); //$NON-NLS-1$ //$NON-NLS-2$
      }
      catch (CoreException ex)
      {
        throw new WrappedException(ex);
      }
    }

    return builder.length() == 0 ? null : builder.toString();
  }
}
