/***************************************************************************
 * Copyright (c) 2004 - 2007 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.emf.cdo.migrator;

import org.eclipse.emf.codegen.ecore.genmodel.GenDelegationKind;
import org.eclipse.emf.codegen.ecore.genmodel.GenModel;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.WrappedException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

/**
 * @author Eike Stepper
 */
public abstract class CDOMigrator
{
  public static final String ROOT_EXTENDS_CLASS = "org.eclipse.emf.internal.cdo.CDOObjectImpl";

  public static final String ROOT_EXTENDS_INTERFACE = "org.eclipse.emf.cdo.CDOObject";

  public static final String PLUGIN_VARIABLE = "CDO=org.eclipse.emf.cdo";

  public static final String CDO_MF_CONTENTS = "This is a marker file for bundles with CDO native models.\n";

  private CDOMigrator()
  {
  }

  public static void adjustGenModel(GenModel genModel, IProject project)
  {
    genModel.setFeatureDelegation(GenDelegationKind.REFLECTIVE_LITERAL);
    genModel.setRootExtendsClass(ROOT_EXTENDS_CLASS);
    genModel.setRootExtendsInterface(ROOT_EXTENDS_INTERFACE);

    EList<String> pluginVariables = genModel.getModelPluginVariables();
    if (!pluginVariables.contains(PLUGIN_VARIABLE))
    {
      pluginVariables.add(PLUGIN_VARIABLE);
    }

    IFolder folder = project.getFolder("META-INF");
    if (!folder.exists())
    {
      try
      {
        folder.create(true, true, new NullProgressMonitor());
      }
      catch (CoreException ex)
      {
        throw new WrappedException(ex);
      }
    }

    IFile file = folder.getFile("CDO.MF");
    if (!file.exists())
    {
      try
      {
        InputStream contents = new ByteArrayInputStream(CDO_MF_CONTENTS.getBytes());
        file.create(contents, true, new NullProgressMonitor());
      }
      catch (CoreException ex)
      {
        throw new WrappedException(ex);
      }
    }
  }
}