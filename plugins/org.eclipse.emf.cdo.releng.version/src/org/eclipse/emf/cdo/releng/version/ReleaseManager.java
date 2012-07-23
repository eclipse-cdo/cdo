/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.releng.version;

import org.eclipse.emf.cdo.releng.version.Release.Element;
import org.eclipse.emf.cdo.releng.version.Release.Element.Type;

import org.eclipse.core.resources.ICommand;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.osgi.service.resolver.BundleDescription;
import org.eclipse.pde.core.IModel;
import org.eclipse.pde.core.plugin.IPluginModelBase;
import org.eclipse.pde.core.plugin.PluginRegistry;

import org.osgi.framework.Version;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.WeakHashMap;

/**
 * @author Eike Stepper
 */
public class ReleaseManager
{
  public static final ReleaseManager INSTANCE = new ReleaseManager();

  private Map<Release, Long> releases = new WeakHashMap<Release, Long>();

  private SAXParserFactory parserFactory;

  private ReleaseManager()
  {
  }

  private SAXParser getParser() throws ParserConfigurationException, SAXException
  {
    if (parserFactory == null)
    {
      parserFactory = SAXParserFactory.newInstance();
    }

    return parserFactory.newSAXParser();
  }

  public synchronized Release getRelease(IFile file) throws CoreException
  {
    try
    {
      for (Entry<Release, Long> entry : releases.entrySet())
      {
        Release release = entry.getKey();
        if (release.getFile().equals(file))
        {
          long timeStamp = entry.getValue();
          if (file.getLocalTimeStamp() == timeStamp)
          {
            return release;
          }

          releases.remove(release);
          break;
        }
      }

      if (!file.exists())
      {
        throw new FileNotFoundException(file.getFullPath().toString());
      }

      Release release = new Release(getParser(), file);
      releases.put(release, file.getLocalTimeStamp());
      return release;
    }
    catch (CoreException ex)
    {
      throw ex;
    }
    catch (Exception ex)
    {
      throw new CoreException(new Status(IStatus.ERROR, Activator.PLUGIN_ID, ex.getLocalizedMessage(), ex));
    }
  }

  public synchronized Release createRelease(IFile file) throws CoreException, IOException
  {
    Release release = new Release(file);
    String path = file.getFullPath().toString();

    Map<Element, Element> elements = release.getElements();
    for (IProject project : ResourcesPlugin.getWorkspace().getRoot().getProjects())
    {
      if (project.isOpen())
      {
        IProjectDescription desc = project.getDescription();
        ICommand[] commands = desc.getBuildSpec();

        for (int i = 0; i < commands.length; ++i)
        {
          if (commands[i].getBuilderName().equals(VersionBuilder.BUILDER_ID))
          {
            Map<String, String> arguments = commands[i].getArguments();
            if (arguments != null)
            {
              String releasePath = arguments.get(VersionBuilder.RELEASE_PATH_ARGUMENT);
              if (path.equals(releasePath))
              {
                IModel componentModel = VersionBuilder.getComponentModel(project);
                Element element = createElement(componentModel, true);
                elements.put(element, element);
              }
            }
          }
        }
      }
    }

    Set<Element> keySet = elements.keySet();
    for (Element element : keySet.toArray(new Element[keySet.size()]))
    {
      for (Element child : element.getChildren())
      {
        if (!elements.containsKey(child))
        {
          IModel childModel = getComponentModel(child);
          if (childModel != null)
          {
            Element topElement = createElement(childModel, true);
            elements.put(topElement, topElement);
          }
          else
          {
            elements.put(child, child);
          }
        }
      }
    }

    release.write();
    releases.put(release, file.getLocalTimeStamp());
    return release;
  }

  public Element createElement(IModel componentModel) throws CoreException
  {
    return createElement(componentModel, false);
  }

  private Element createElement(IModel componentModel, boolean withFeatureContent) throws CoreException
  {
    if (componentModel instanceof IPluginModelBase)
    {
      IPluginModelBase pluginModel = (IPluginModelBase)componentModel;
      BundleDescription description = pluginModel.getBundleDescription();

      String name = description.getSymbolicName();
      Version version = description.getVersion();
      return new Element(Type.PLUGIN, name, version);
    }

    return createFeatureElement(componentModel, withFeatureContent);
  }

  @SuppressWarnings("restriction")
  private Element createFeatureElement(IModel componentModel, boolean withContent)
  {
    org.eclipse.pde.internal.core.ifeature.IFeatureModel featureModel = (org.eclipse.pde.internal.core.ifeature.IFeatureModel)componentModel;
    org.eclipse.pde.internal.core.ifeature.IFeature feature = featureModel.getFeature();

    String name = feature.getId();
    Version version = new Version(feature.getVersion());
    Element element = new Element(Type.FEATURE, name, version);

    if (withContent)
    {
      for (org.eclipse.pde.internal.core.ifeature.IFeatureChild versionable : feature.getIncludedFeatures())
      {
        Element child = new Element(Element.Type.FEATURE, versionable.getId(), versionable.getVersion());
        element.getChildren().add(child);
      }

      for (org.eclipse.pde.internal.core.ifeature.IFeaturePlugin versionable : feature.getPlugins())
      {
        Element child = new Element(Element.Type.PLUGIN, versionable.getId(), versionable.getVersion());
        element.getChildren().add(child);
      }
    }

    return element;
  }

  @SuppressWarnings("restriction")
  public IModel getComponentModel(Element element)
  {
    String name = element.getName();
    if (element.getType() == Element.Type.PLUGIN)
    {
      IModel pluginModel = PluginRegistry.findModel(name);
      if (pluginModel != null)
      {
        return pluginModel;
      }
    }

    org.eclipse.pde.internal.core.FeatureModelManager manager = org.eclipse.pde.internal.core.PDECore.getDefault()
        .getFeatureModelManager();
    org.eclipse.pde.internal.core.ifeature.IFeatureModel[] featureModels = manager.getWorkspaceModels();

    org.eclipse.pde.internal.core.ifeature.IFeatureModel featureModel = getFeatureModel(name, featureModels);
    if (featureModel == null)
    {
      featureModels = manager.getExternalModels();
      featureModel = getFeatureModel(name, featureModels);
    }

    return featureModel;
  }

  @SuppressWarnings("restriction")
  private org.eclipse.pde.internal.core.ifeature.IFeatureModel getFeatureModel(String name,
      org.eclipse.pde.internal.core.ifeature.IFeatureModel[] featureModels)
  {
    Version highestVersion = null;
    org.eclipse.pde.internal.core.ifeature.IFeatureModel highestModel = null;

    for (org.eclipse.pde.internal.core.ifeature.IFeatureModel featureModel : featureModels)
    {
      org.eclipse.pde.internal.core.ifeature.IFeature feature = featureModel.getFeature();
      String id = feature.getId();
      if (id.equals(name))
      {
        Version newVersion = new Version(feature.getVersion());
        if (highestVersion == null || highestVersion.compareTo(newVersion) < 0)
        {
          highestVersion = newVersion;
          highestModel = featureModel;
        }
      }
    }

    if (highestModel == null)
    {
      return null;
    }

    return highestModel;
  }
}
