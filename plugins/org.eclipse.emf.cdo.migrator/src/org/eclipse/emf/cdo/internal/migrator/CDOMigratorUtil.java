/*
 * Copyright (c) 2010-2012, 2014, 2018, 2020, 2022 Eike Stepper (Loehne, Germany) and others.
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
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.common.util.WrappedException;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.plugin.EcorePlugin;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Eike Stepper
 */
public abstract class CDOMigratorUtil
{
  public static final String ROOT_EXTENDS_CLASS = "org.eclipse.emf.internal.cdo.CDOObjectImpl"; //$NON-NLS-1$

  public static final String ROOT_EXTENDS_INTERFACE = "org.eclipse.emf.cdo.CDOObject"; //$NON-NLS-1$

  public static final String PLUGIN_VARIABLE = "CDO=org.eclipse.emf.cdo"; //$NON-NLS-1$

  public static final String EDIT_PLUGIN_VARIABLE = "CDO_EDIT=org.eclipse.emf.cdo.edit"; //$NON-NLS-1$

  public static final String PROVIDER_ROOT_EXTENDS_CLASS = "org.eclipse.emf.cdo.edit.CDOItemProviderAdapter"; //$NON-NLS-1$

  private CDOMigratorUtil()
  {
  }

  public static List<GenModel> getGenModels(List<String> paths)
  {
    ResourceSet resourceSet = new ResourceSetImpl();
    resourceSet.getURIConverter().getURIMap().putAll(EcorePlugin.computePlatformURIMap(true));

    Resource.Factory factory = new XMIResourceFactoryImpl();
    resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("*", factory); //$NON-NLS-1$
    resourceSet.getResourceFactoryRegistry().getProtocolToFactoryMap().put("*", factory); //$NON-NLS-1$
    resourceSet.getResourceFactoryRegistry().getContentTypeToFactoryMap().put("*", factory); //$NON-NLS-1$

    List<URI> uris = new ArrayList<>();
    for (String path : paths)
    {
      URI uri = URI.createPlatformResourceURI(path, true);

      try
      {
        resourceSet.getResource(uri, true);
        uris.add(uri);
      }
      catch (Exception ex)
      {
        System.err.println("Ignoring " + uri + " (" + ex.getMessage() + ")");
      }
    }

    EcoreUtil.resolveAll(resourceSet);
    List<GenModel> genModels = new ArrayList<>();

    for (URI uri : uris)
    {
      Resource resource = resourceSet.getResource(uri, true);

      EList<EObject> contents = resource.getContents();
      if (!contents.isEmpty())
      {
        EObject object = contents.get(0);
        if (object instanceof GenModel)
        {
          genModels.add((GenModel)object);
          continue;
        }
      }

      System.err.println("Ignoring " + uri + " (Resource does not contain a GenModel)");
    }

    return genModels;
  }

  public static GenModel getGenModel(String path)
  {
    List<GenModel> genModels = getGenModels(Arrays.asList(path));
    return genModels.isEmpty() ? null : genModels.get(0);
  }

  public static String adjustGenModel(GenModel genModel)
  {
    return adjustGenModel(genModel, GenDelegationKind.REFLECTIVE_LITERAL);
  }

  public static String adjustGenModel(GenModel genModel, GenDelegationKind featureDelegation)
  {
    StringBuilder builder = new StringBuilder();

    if (genModel.getFeatureDelegation() != featureDelegation)
    {
      genModel.setFeatureDelegation(featureDelegation);
      builder.append(Messages.getString("CDOMigratorUtil.4")); //$NON-NLS-1$
      builder.append(" "); //$NON-NLS-1$
      builder.append(featureDelegation);
      builder.append("\n"); //$NON-NLS-1$
    }

    if (genModel.getBooleanFlagsField() != null)
    {
      genModel.setBooleanFlagsField(null);
      builder.append(Messages.getString("CDOMigratorUtil.1")); //$NON-NLS-1$
      builder.append(" null\n"); //$NON-NLS-1$
    }

    if (genModel.getBooleanFlagsReservedBits() != -1)
    {
      genModel.setBooleanFlagsReservedBits(-1);
      builder.append(Messages.getString("CDOMigratorUtil.2")); //$NON-NLS-1$
      builder.append(" -1\n"); //$NON-NLS-1$
    }

    if (genModel.isPackedEnums())
    {
      genModel.setPackedEnums(false);
      builder.append(Messages.getString("CDOMigratorUtil.3")); //$NON-NLS-1$
      builder.append(" false\n"); //$NON-NLS-1$
    }

    if (!ROOT_EXTENDS_CLASS.equals(genModel.getRootExtendsClass()))
    {
      genModel.setRootExtendsClass(ROOT_EXTENDS_CLASS);
      builder.append(Messages.getString("CDOMigratorUtil.6")); //$NON-NLS-1$
      builder.append(" "); //$NON-NLS-1$
      builder.append(ROOT_EXTENDS_CLASS);
      builder.append("\n"); //$NON-NLS-1$
    }

    if (!ROOT_EXTENDS_INTERFACE.equals(genModel.getRootExtendsInterface()))
    {
      genModel.setRootExtendsInterface(ROOT_EXTENDS_INTERFACE);
      builder.append(Messages.getString("CDOMigratorUtil.8")); //$NON-NLS-1$
      builder.append(" "); //$NON-NLS-1$
      builder.append(ROOT_EXTENDS_INTERFACE);
      builder.append("\n"); //$NON-NLS-1$
    }

    EList<String> pluginVariables = genModel.getModelPluginVariables();
    if (!pluginVariables.contains(PLUGIN_VARIABLE))
    {
      pluginVariables.add(PLUGIN_VARIABLE);
      builder.append(Messages.getString("CDOMigratorUtil.10")); //$NON-NLS-1$
      builder.append(" "); //$NON-NLS-1$
      builder.append(PLUGIN_VARIABLE);
      builder.append("\n"); //$NON-NLS-1$
    }

    EList<String> editPluginVariables = genModel.getEditPluginVariables();
    if (!editPluginVariables.contains(EDIT_PLUGIN_VARIABLE))
    {
      editPluginVariables.add(EDIT_PLUGIN_VARIABLE);
      builder.append(Messages.getString("CDOMigratorUtil.11")); //$NON-NLS-1$
      builder.append(" "); //$NON-NLS-1$
      builder.append(EDIT_PLUGIN_VARIABLE);
      builder.append("\n"); //$NON-NLS-1$
    }

    if (!PROVIDER_ROOT_EXTENDS_CLASS.equals(genModel.getProviderRootExtendsClass()))
    {
      genModel.setProviderRootExtendsClass(PROVIDER_ROOT_EXTENDS_CLASS);
      builder.append(Messages.getString("CDOMigratorUtil.9")); //$NON-NLS-1$
      builder.append(" "); //$NON-NLS-1$
      builder.append(PROVIDER_ROOT_EXTENDS_CLASS);
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
        builder.append(Messages.getString("CDOMigratorUtil.12") + "\n"); //$NON-NLS-1$ //$NON-NLS-2$
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
        builder.append(Messages.getString("CDOMigratorUtil.13") + "\n"); //$NON-NLS-1$ //$NON-NLS-2$
      }
      catch (CoreException ex)
      {
        throw new WrappedException(ex);
      }
    }

    return builder.length() == 0 ? null : builder.toString();
  }
}
