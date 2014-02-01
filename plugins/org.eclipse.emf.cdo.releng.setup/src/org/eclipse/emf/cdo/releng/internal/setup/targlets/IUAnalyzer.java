/*
 * Copyright (c) 2004-2014 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.releng.internal.setup.targlets;

import org.eclipse.emf.cdo.releng.internal.setup.Activator;
import org.eclipse.emf.cdo.releng.internal.setup.targlets.IUGenerator.BundleIUGenerator;
import org.eclipse.emf.cdo.releng.internal.setup.targlets.IUGenerator.FeatureIUGenerator;
import org.eclipse.emf.cdo.releng.internal.setup.util.BasicProjectAnalyzer;
import org.eclipse.emf.cdo.releng.internal.setup.util.BasicProjectVisitor;
import org.eclipse.emf.cdo.releng.internal.setup.util.EMFUtil;
import org.eclipse.emf.cdo.releng.predicates.Predicate;
import org.eclipse.emf.cdo.releng.setup.ComponentDefinition;
import org.eclipse.emf.cdo.releng.setup.ComponentExtension;
import org.eclipse.emf.cdo.releng.setup.InstallableUnit;
import org.eclipse.emf.cdo.releng.setup.SetupFactory;
import org.eclipse.emf.cdo.releng.setup.util.ProjectProvider.Visitor;

import org.eclipse.net4j.util.XMLUtil;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.equinox.p2.metadata.IInstallableUnit;
import org.eclipse.equinox.p2.metadata.IRequirement;
import org.eclipse.equinox.p2.metadata.MetadataFactory;
import org.eclipse.equinox.p2.metadata.MetadataFactory.InstallableUnitDescription;
import org.eclipse.equinox.p2.metadata.Version;
import org.eclipse.equinox.p2.metadata.VersionRange;

import org.w3c.dom.Element;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Eike Stepper
 */
public class IUAnalyzer extends BasicProjectAnalyzer<IInstallableUnit>
{
  private final Set<String> ids = new HashSet<String>();

  public IUAnalyzer()
  {
  }

  public Set<String> getIDs()
  {
    return ids;
  }

  public Map<IInstallableUnit, File> analyze(File folder, EList<Predicate> predicates, boolean locateNestedProjects,
      IProgressMonitor monitor)
  {
    Visitor<IInstallableUnit> visitor = new IUVisitor();
    return analyze(folder, predicates, locateNestedProjects, visitor, monitor);
  }

  @Override
  protected IInstallableUnit filter(IInstallableUnit iu)
  {
    ids.add(iu.getId());
    return iu;
  }

  /**
   * @author Eike Stepper
   */
  public static class IUVisitor extends BasicProjectVisitor<IInstallableUnit>
  {
    @Override
    public IInstallableUnit visitPlugin(File manifestFile, IProgressMonitor monitor) throws Exception
    {
      File pluginFolder = manifestFile.getParentFile().getParentFile();
      return BundleIUGenerator.INSTANCE.generateIU(pluginFolder);
    }

    @Override
    public IInstallableUnit visitFeature(File featureFile, IProgressMonitor monitor) throws Exception
    {
      File featureFolder = featureFile.getParentFile();
      return FeatureIUGenerator.INSTANCE.generateIU(featureFolder);
    }

    @Override
    protected IInstallableUnit visitComponentDefinition(ComponentDefinition componentDefinition,
        IProgressMonitor monitor) throws Exception
    {
      InstallableUnitDescription description = new InstallableUnitDescription();
      description.setId(componentDefinition.getID());
      description.setVersion(componentDefinition.getVersion());

      IInstallableUnit iu = MetadataFactory.createInstallableUnit(description);
      visitComponentExtension(componentDefinition, iu, monitor);
      return iu;
    }

    @Override
    protected void visitComponentExtension(ComponentExtension componentExtension, IInstallableUnit host,
        IProgressMonitor monitor) throws Exception
    {
      // TODO It would be better to work with a new InstallableUnitDescription
      if (host instanceof org.eclipse.equinox.internal.p2.metadata.InstallableUnit)
      {
        org.eclipse.equinox.internal.p2.metadata.InstallableUnit iu = (org.eclipse.equinox.internal.p2.metadata.InstallableUnit)host;
        List<IRequirement> requirements = new ArrayList<IRequirement>(iu.getRequirements());

        for (InstallableUnit dependency : componentExtension.getDependencies())
        {
          String id = dependency.getID();
          VersionRange versionRange = dependency.getVersionRange();

          String namespace;
          if (id.endsWith(".feature.group"))
          {
            namespace = IInstallableUnit.NAMESPACE_IU_ID;
          }
          else
          {
            namespace = "osgi.bundle";
          }

          requirements.add(MetadataFactory.createRequirement(namespace, id, versionRange, null, false, true, true));
        }

        iu.setRequiredCapabilities(requirements.toArray(new IRequirement[requirements.size()]));
      }
    }

    @Override
    public IInstallableUnit visitCSpec(File cspecFile, IProgressMonitor monitor) throws Exception
    {
      File cdefFile = new File(cspecFile.getParentFile(), "component.def");
      if (cdefFile.exists())
      {
        return null;
      }

      Element rootElement = XMLUtil.loadRootElement(getDocumentBuilder(), cspecFile);
      String id = BuckminsterDependencyHandler.getP2ID(rootElement.getAttribute("name"),
          rootElement.getAttribute("componentType"));
      if (id == null)
      {
        return null;
      }

      ComponentDefinition componentDefinition = SetupFactory.eINSTANCE.createComponentDefinition();
      componentDefinition.setID(id);
      componentDefinition.setVersion(Version.create(rootElement.getAttribute("version")));

      handleBuckminsterDependencies(rootElement, componentDefinition, monitor);

      Resource resource = getResourceSet().createResource(URI.createFileURI(cdefFile.getAbsolutePath()));
      resource.getContents().add(componentDefinition);
      EMFUtil.saveEObject(componentDefinition);

      return visitComponentDefinition(componentDefinition, monitor);
    }

    @Override
    public void visitCSpex(File cspexFile, IInstallableUnit host, IProgressMonitor monitor) throws Exception
    {
      File cextFile = new File(cspexFile.getParentFile(), "component.ext");
      if (cextFile.exists())
      {
        return;
      }

      ComponentExtension componentExtension = SetupFactory.eINSTANCE.createComponentExtension();

      Element rootElement = XMLUtil.loadRootElement(getDocumentBuilder(), cspexFile);
      handleBuckminsterDependencies(rootElement, componentExtension, monitor);

      Resource resource = getResourceSet().createResource(URI.createFileURI(cextFile.getAbsolutePath()));
      resource.getContents().add(componentExtension);
      EMFUtil.saveEObject(componentExtension);

      visitComponentExtension(componentExtension, host, monitor);
    }

    private void handleBuckminsterDependencies(Element rootElement, final ComponentExtension componentExtension,
        IProgressMonitor monitor) throws Exception
    {
      new BuckminsterDependencyHandler()
      {
        @Override
        protected void handleDependency(String id, String versionDesignator) throws Exception
        {
          try
          {
            InstallableUnit dependency = SetupFactory.eINSTANCE.createInstallableUnit();
            dependency.setID(id);
            if (versionDesignator != null)
            {
              dependency.setVersionRange(new VersionRange(versionDesignator));
            }

            componentExtension.getDependencies().add(dependency);
          }
          catch (Exception ex)
          {
            Activator.log(ex);
          }
        }
      }.handleDependencies(rootElement, monitor);
    }
  }
}
