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
import org.eclipse.emf.cdo.releng.setup.ComponentDefinition;
import org.eclipse.emf.cdo.releng.setup.ComponentExtension;
import org.eclipse.emf.cdo.releng.setup.ComponentType;
import org.eclipse.emf.cdo.releng.setup.util.ProjectProvider.Visitor;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.equinox.internal.p2.metadata.InstallableUnit;
import org.eclipse.equinox.p2.metadata.IInstallableUnit;
import org.eclipse.equinox.p2.metadata.IRequirement;
import org.eclipse.equinox.p2.metadata.MetadataFactory;
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

  public Map<IInstallableUnit, File> analyze(File folder, boolean locateNestedProjects, IProgressMonitor monitor)
  {
    Visitor<IInstallableUnit> visitor = new IUVisitor();
    return analyze(folder, locateNestedProjects, visitor, monitor);
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
      return super.visitComponentDefinition(componentDefinition, monitor);
    }

    @Override
    protected void visitComponentExtension(ComponentExtension componentExtension, IInstallableUnit host,
        IProgressMonitor monitor) throws Exception
    {
      super.visitComponentExtension(componentExtension, host, monitor);
    }

    @Override
    protected IInstallableUnit visitCSpec(Element rootElement, IProgressMonitor monitor) throws Exception
    {
      return super.visitCSpec(rootElement, monitor);
    }

    @Override
    protected void visitCSpex(Element rootElement, IInstallableUnit host, IProgressMonitor monitor) throws Exception
    {
      if (host instanceof InstallableUnit)
      {
        InstallableUnit iu = (InstallableUnit)host;

        final List<IRequirement> requirements = new ArrayList<IRequirement>(iu.getRequirements());
        new BuckminsterDependencyHandler()
        {
          @Override
          protected void handleDependency(String id, String type, String versionDesignator) throws Exception
          {
            try
            {
              ComponentType componentType = ComponentType.get(type);
              if (componentType != null)
              {
                String namespace = null;

                switch (componentType)
                {
                case ECLIPSE_FEATURE:
                  namespace = IInstallableUnit.NAMESPACE_IU_ID;
                  id += ".feature.group";
                  break;

                case OSGI_BUNDLE:
                  namespace = componentType.toString();
                  break;
                }

                if (namespace != null)
                {
                  IRequirement requirement = createRequirement(namespace, id, versionDesignator);
                  requirements.add(requirement);
                }
              }
            }
            catch (Exception ex)
            {
              Activator.log(ex);
            }
          }
        }.handleDependencies(rootElement, monitor);

        iu.setRequiredCapabilities(requirements.toArray(new IRequirement[requirements.size()]));
      }
    }

    private IRequirement createRequirement(String namespace, String id, String range)
    {
      VersionRange versionRange = range == null ? VersionRange.emptyRange : new VersionRange(range);
      return MetadataFactory.createRequirement(namespace, id, versionRange, null, false, true, true);
    }
  }
}
