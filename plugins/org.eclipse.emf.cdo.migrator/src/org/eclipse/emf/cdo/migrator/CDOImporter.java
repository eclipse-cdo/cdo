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
import org.eclipse.emf.codegen.ecore.genmodel.GenPackage;
import org.eclipse.emf.common.util.BasicDiagnostic;
import org.eclipse.emf.common.util.BasicMonitor;
import org.eclipse.emf.common.util.Diagnostic;
import org.eclipse.emf.common.util.DiagnosticException;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.Monitor;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.common.util.UniqueEList;
import org.eclipse.emf.common.util.WrappedException;
import org.eclipse.emf.converter.ConverterPlugin;
import org.eclipse.emf.converter.util.ConverterUtil;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.util.Diagnostician;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.importer.ModelImporter;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.List;

public class CDOImporter extends ModelImporter
{
  public static final String IMPORTER_ID = "org.eclipse.emf.importer.cdo";

  public static final String PLUGIN_VARIABLE = "CDO=org.eclipse.emf.cdo";

  public static final String ROOT_EXTENDS_CLASS = "org.eclipse.emf.internal.cdo.CDOObjectImpl";

  public static final String ROOT_EXTENDS_INTERFACE = "org.eclipse.emf.cdo.CDOObject";

  public static final String CDO_MF_CONTENTS = "This is a marker file for bundles with CDO native models.\n";

  public CDOImporter()
  {
  }

  @Override
  public String getID()
  {
    return IMPORTER_ID;
  }

  @Override
  protected Diagnostic doComputeEPackages(Monitor monitor) throws Exception
  {
    Diagnostic diagnostic = Diagnostic.OK_INSTANCE;

    List<URI> locationURIs = getModelLocationURIs();
    if (locationURIs.isEmpty())
    {
      diagnostic = new BasicDiagnostic(Diagnostic.ERROR, "org.eclipse.emf.cdo.migrator", 0,
          "Specify a valid Ecore model and try loading again", null);
    }
    else
    {
      monitor.beginTask("", 2);
      monitor.subTask(MessageFormat.format("Loading {0}", locationURIs));

      ResourceSet ecoreResourceSet = createResourceSet();
      for (URI ecoreModelLocation : locationURIs)
      {
        ecoreResourceSet.getResource(ecoreModelLocation, true);
      }
      EcoreUtil.resolveAll(ecoreResourceSet);

      for (Resource resource : ecoreResourceSet.getResources())
      {
        getEPackages().addAll(
            EcoreUtil.<EPackage> getObjectsByType(resource.getContents(), EcorePackage.Literals.EPACKAGE));
      }

      BasicDiagnostic diagnosticChain = new BasicDiagnostic(ConverterPlugin.ID, ConverterUtil.ACTION_MESSAGE_NONE,
          "Problems were detected while validating and converting the Ecore models", null);
      for (EPackage ePackage : getEPackages())
      {
        Diagnostician.INSTANCE.validate(ePackage, diagnosticChain);
      }
      if (diagnosticChain.getSeverity() != Diagnostic.OK)
      {
        diagnostic = diagnosticChain;
      }
    }
    return diagnostic;
  }

  @Override
  public void addToResource(EPackage ePackage, ResourceSet resourceSet)
  {
    if (ePackage.eResource() != null && getGenModel().eResource() != null)
    {
      URI ePackageURI = ePackage.eResource().getURI();
      URI genModelURI = getGenModel().eResource().getURI();

      if (!ePackageURI.trimSegments(1).equals(genModelURI.trimSegments(1)))
      {
        ePackage.eResource().getContents().remove(ePackage);
      }
    }
    super.addToResource(ePackage, resourceSet);
  }

  @Override
  protected void adjustGenModel(Monitor monitor)
  {
    super.adjustGenModel(monitor);

    URI genModelURI = createFileURI(getGenModelPath().toString());
    for (URI uri : getModelLocationURIs())
    {
      GenModel genModel = getGenModel();
      genModel.getForeignModel().add(makeRelative(uri, genModelURI).toString());
      adjustGenModelCDO(genModel, monitor);
    }
  }

  protected void adjustGenModelCDO(GenModel genModel, Monitor monitor)
  {
    genModel.setFeatureDelegation(GenDelegationKind.REFLECTIVE_LITERAL);
    genModel.setRootExtendsClass(ROOT_EXTENDS_CLASS);
    genModel.setRootExtendsInterface(ROOT_EXTENDS_INTERFACE);

    EList<String> pluginVariables = genModel.getModelPluginVariables();
    if (!pluginVariables.contains(PLUGIN_VARIABLE))
    {
      pluginVariables.add(PLUGIN_VARIABLE);
    }

    String projectName = getModelProjectName();
    IProject project = getWorkspaceRoot().getProject(projectName);
    IFolder folder = project.getFolder("META-INF");
    if (!folder.exists())
    {
      try
      {
        folder.create(true, true, BasicMonitor.toIProgressMonitor(monitor));
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
        file.create(contents, true, BasicMonitor.toIProgressMonitor(monitor));
      }
      catch (CoreException ex)
      {
        throw new WrappedException(ex);
      }
    }
  }

  @Override
  protected void handleOriginalGenModel() throws DiagnosticException
  {
    URI genModelURI = getOriginalGenModel().eResource().getURI();
    StringBuffer text = new StringBuffer();
    for (String value : getOriginalGenModel().getForeignModel())
    {
      if (value.endsWith(".ecore") || value.endsWith(".emof"))
      {
        text.append(makeAbsolute(URI.createURI(value), genModelURI).toString());
        text.append(" ");
      }
    }

    if (text.length() == 0)
    {
      List<URI> locations = new UniqueEList<URI>();
      for (GenPackage genPackage : getOriginalGenModel().getGenPackages())
      {
        URI ecoreURI = genPackage.getEcorePackage().eResource().getURI();
        if (locations.add(ecoreURI))
        {
          text.append(makeAbsolute(URI.createURI(ecoreURI.toString()), genModelURI).toString());
          text.append(" ");
        }
      }
    }

    setModelLocation(text.toString().trim());
  }
}