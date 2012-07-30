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
package org.eclipse.emf.cdo.releng.internal.version;

import org.eclipse.emf.cdo.releng.version.IElement;
import org.eclipse.emf.cdo.releng.version.IElementResolver;
import org.eclipse.emf.cdo.releng.version.IRelease;
import org.eclipse.emf.cdo.releng.version.IReleaseManager;
import org.eclipse.emf.cdo.releng.version.Markers;
import org.eclipse.emf.cdo.releng.version.VersionUtil;
import org.eclipse.emf.cdo.releng.version.VersionValidator;

import org.eclipse.core.resources.ICommand;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.osgi.service.resolver.BundleDescription;
import org.eclipse.osgi.service.resolver.BundleSpecification;
import org.eclipse.osgi.service.resolver.ExportPackageDescription;
import org.eclipse.osgi.service.resolver.ImportPackageSpecification;
import org.eclipse.osgi.service.resolver.VersionRange;
import org.eclipse.pde.core.IModel;
import org.eclipse.pde.core.plugin.IPluginBase;
import org.eclipse.pde.core.plugin.IPluginExtensionPoint;
import org.eclipse.pde.core.plugin.IPluginModelBase;
import org.eclipse.pde.core.plugin.PluginRegistry;

import org.osgi.framework.Version;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Eike Stepper
 */
public class VersionBuilder extends IncrementalProjectBuilder implements IElementResolver
{
  private static final Path DESCRIPTION_PATH = new Path(".project");

  private static final Path OPTIONS_PATH = new Path(".options");

  private static final Path MANIFEST_PATH = new Path("META-INF/MANIFEST.MF");

  private static final Path FEATURE_PATH = new Path("feature.xml");

  private static final String INTEGRATION_PROPERTY_KEY = "baseline.for.integration";

  private static final String DEVIATIONS_PROPERTY_KEY = "show.deviations";

  private static final Version ADDITION = new Version(Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE);

  private static final Version REMOVAL = new Version(Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE);

  private static final int NO_CHANGE = 0;

  private static final int MICRO_CHANGE = 1;

  private static final int MINOR_CHANGE = 2;

  private static final int MAJOR_CHANGE = 3;

  private static final Pattern DEBUG_OPTION_PATTERN = Pattern.compile("^( *)([^/ \\n\\r]+)/([^ =]+)( *=.*)$",
      Pattern.MULTILINE);

  private static final String NL = System.getProperty("line.separator");

  private IRelease release;

  private Boolean integration;

  private Boolean deviations;

  private Map<IElement, IElement> elementCache = new HashMap<IElement, IElement>();

  public VersionBuilder()
  {
  }

  public IElement resolveElement(IElement key)
  {
    try
    {
      IElement element = elementCache.get(key);
      if (element == null)
      {
        IModel componentModel = IReleaseManager.INSTANCE.getComponentModel(key);
        if (componentModel != null)
        {
          element = IReleaseManager.INSTANCE.createElement(componentModel, true);
          elementCache.put(element, element);
        }
      }

      return element;
    }
    catch (Exception ex)
    {
      Activator.log(ex);
      return null;
    }
  }

  @Override
  protected void clean(IProgressMonitor monitor) throws CoreException
  {
    IProject project = getProject();

    monitor.beginTask("", 1);
    monitor.subTask("Cleaning version validity problems of " + project.getName());

    try
    {
      Activator.clearBuildState(project);
      Markers.deleteAllMarkers(project);
    }
    finally
    {
      monitor.done();
    }
  }

  @Override
  protected final IProject[] build(int kind, @SuppressWarnings("rawtypes") Map args, IProgressMonitor monitor)
      throws CoreException
  {
    @SuppressWarnings("unchecked")
    VersionBuilderArguments arguments = new VersionBuilderArguments(args);
    List<IProject> buildDpependencies = new ArrayList<IProject>();
    VersionValidator validator = null;

    IProject project = getProject();
    IFile projectDescription = project.getFile(DESCRIPTION_PATH);

    BuildState buildState = Activator.getBuildState(project);
    byte[] releaseSpecDigest = buildState.getReleaseSpecDigest();
    final boolean fullBuild = releaseSpecDigest == null || kind == FULL_BUILD || kind == CLEAN_BUILD;
    IResourceDelta delta = fullBuild ? null : getDelta(project);

    monitor.beginTask("", 1);
    monitor.subTask("Checking version validity of " + project.getName());

    try
    {
      Markers.deleteAllMarkers(project);

      IModel componentModel = VersionUtil.getComponentModel(project);
      IPath componentModelPath = componentModel.getUnderlyingResource().getProjectRelativePath();
      boolean componentModelChanged = delta == null || delta.findMember(componentModelPath) != null;

      if (!arguments.isIgnoreMalformedVersions())
      {
        if (componentModelChanged)
        {
          if (checkMalformedVersions(componentModel))
          {
            return buildDpependencies.toArray(new IProject[buildDpependencies.size()]);
          }
        }
      }

      if (componentModel instanceof IPluginModelBase)
      {
        if (!arguments.isIgnoreSchemaBuilder())
        {
          if (delta == null || delta.findMember(DESCRIPTION_PATH) != null)
          {
            checkSchemaBuilder((IPluginModelBase)componentModel, projectDescription);
          }
        }

        if (!arguments.isIgnoreDebugOptions())
        {
          if (delta == null || delta.findMember(OPTIONS_PATH) != null)
          {
            checkDebugOptions((IPluginModelBase)componentModel);
          }
        }

        if (!arguments.isIgnoreMissingDependencyRanges())
        {
          if (componentModelChanged)
          {
            checkDependencyRanges((IPluginModelBase)componentModel);
          }
        }

        if (!arguments.isIgnoreMissingExportVersions())
        {
          if (componentModelChanged)
          {
            checkPackageExports((IPluginModelBase)componentModel);
          }
        }

        if (hasAPIToolsMarker((IPluginModelBase)componentModel))
        {
          return buildDpependencies.toArray(new IProject[buildDpependencies.size()]);
        }
      }

      /*
       * Determine release data to validate against
       */

      String releasePathArg = arguments.getReleasePath();
      if (releasePathArg == null)
      {
        String msg = "Path to release spec file is not configured";
        Markers.addMarker(projectDescription, msg, IMarker.SEVERITY_ERROR, "(" + VersionUtil.BUILDER_ID + ")");
        return buildDpependencies.toArray(new IProject[buildDpependencies.size()]);
      }

      IPath releasePath = new Path(releasePathArg);

      try
      {
        IFile releaseSpecFile = ResourcesPlugin.getWorkspace().getRoot().getFile(releasePath);
        buildDpependencies.add(releaseSpecFile.getProject());

        IRelease release;
        if (!releaseSpecFile.exists())
        {
          release = IReleaseManager.INSTANCE.createRelease(releaseSpecFile);
        }
        else
        {
          release = IReleaseManager.INSTANCE.getRelease(releaseSpecFile);
        }

        byte[] digest = VersionUtil.getSHA1(releaseSpecFile);
        if (releaseSpecDigest == null || !MessageDigest.isEqual(digest, releaseSpecDigest))
        {
          buildState.setReleaseSpecDigest(digest);
          delta = null;
        }

        this.release = release;
      }
      catch (Exception ex)
      {
        Activator.log(ex);
        String msg = "Problem with release spec: " + releasePath;
        Markers.addMarker(projectDescription, msg, IMarker.SEVERITY_ERROR,
            "(" + releasePath.toString().replace(".", "\\.") + ")");
        return buildDpependencies.toArray(new IProject[buildDpependencies.size()]);
      }

      /*
       * Determine if a validation is needed or if the version has already been increased properly
       */

      IElement element = IReleaseManager.INSTANCE.createElement(componentModel, true);
      elementCache.put(element, element);
      for (IElement child : element.getAllChildren(this))
      {
        IProject childProject = getProject(child);
        if (childProject != null)
        {
          buildDpependencies.add(childProject);
        }
      }

      IElement releaseElement = release.getElements().get(element);
      if (releaseElement == null)
      {
        if (VersionUtil.DEBUG)
        {
          System.out.println("Project has not been released: " + project.getName());
        }

        return buildDpependencies.toArray(new IProject[buildDpependencies.size()]);
      }

      IFile propertiesFile = VersionUtil.getFile(releasePath, "properties");
      long propertiesTimeStamp = propertiesFile.getLocalTimeStamp();
      if (buildState.getPropertiesTimeStamp() != propertiesTimeStamp)
      {
        if (initReleaseProperties(propertiesFile))
        {
          delta = null;
        }

        buildState.setDeviations(deviations);
        buildState.setIntegration(integration);
        buildState.setPropertiesTimeStamp(propertiesTimeStamp);
      }
      else
      {
        deviations = buildState.isDeviations();
        integration = buildState.isIntegration();
      }

      Version elementVersion = element.getVersion();
      Version releaseVersion = releaseElement.getVersion();
      Version nextImplementationVersion = getNextImplVersion(releaseVersion);

      int comparison = releaseVersion.compareTo(elementVersion);
      if (comparison != 0 && deviations)
      {
        addDeviationMarker(element, releaseVersion);
      }

      if (comparison < 0)
      {
        if (!nextImplementationVersion.equals(elementVersion))
        {
          if (elementVersion.getMajor() == nextImplementationVersion.getMajor()
              && elementVersion.getMinor() == nextImplementationVersion.getMinor())
          {
            addVersionMarker("Version should be " + nextImplementationVersion, nextImplementationVersion);
          }
        }

        if (element.getType() == IElement.Type.PLUGIN)
        {
          return buildDpependencies.toArray(new IProject[buildDpependencies.size()]);
        }
      }

      if (comparison > 0)
      {
        addVersionMarker("Version has been decreased after release " + releaseVersion, releaseVersion);
        return buildDpependencies.toArray(new IProject[buildDpependencies.size()]);
      }

      if (element.getType() == IElement.Type.FEATURE)
      {
        if (!arguments.isIgnoreFeatureContentRedundancy())
        {
          checkFeatureRedundancy(element);
        }

        if (!arguments.isIgnoreFeatureContentChanges())
        {
          List<Map.Entry<IElement, Version>> warnings = new ArrayList<Entry<IElement, Version>>();
          int change = checkFeatureContentChanges(componentModel, element, releaseElement, warnings);
          if (change != NO_CHANGE)
          {
            Version nextFeatureVersion = getNextFeatureVersion(releaseVersion, nextImplementationVersion, change);
            if (elementVersion.compareTo(nextFeatureVersion) < 0)
            {
              IMarker marker = addVersionMarker("Version must be increased to " + nextFeatureVersion
                  + " because the feature's references have changed", nextFeatureVersion);
              if (marker != null)
              {
                marker.setAttribute(Markers.QUICK_FIX_CONFIGURE_OPTION,
                    IVersionBuilderArguments.IGNORE_CONTENT_CHANGES_ARGUMENT);
              }

              for (Entry<IElement, Version> entry : warnings)
              {
                addIncludeMarker(entry.getKey(), entry.getValue());
              }
            }

            return buildDpependencies.toArray(new IProject[buildDpependencies.size()]);
          }

          if (!elementVersion.equals(releaseVersion))
          {
            return buildDpependencies.toArray(new IProject[buildDpependencies.size()]);
          }
        }
      }

      /*
       * Determine validator to use
       */

      String validatorClassName = arguments.getValidatorClassName();
      if (validatorClassName == null)
      {
        validatorClassName = IVersionBuilderArguments.DEFAULT_VALIDATOR_CLASS_NAME;
      }

      try
      {
        Class<?> c = Class.forName(validatorClassName, true, VersionBuilder.class.getClassLoader());
        validator = (VersionValidator)c.newInstance();

        if (VersionUtil.DEBUG)
        {
          System.out.println(validator.getClass().getName() + ": " + project.getName());
        }
      }
      catch (Exception ex)
      {
        String msg = ex.getLocalizedMessage() + ": " + validatorClassName;
        Markers.addMarker(projectDescription, msg, IMarker.SEVERITY_ERROR, ".*(" + validatorClassName + ").*");
        return buildDpependencies.toArray(new IProject[buildDpependencies.size()]);
      }

      /*
       * Do the validation
       */

      validator.updateBuildState(buildState, release, project, delta, componentModel, monitor);

      if (buildState.isChangedSinceRelease())
      {
        addVersionMarker("Version must be increased to " + nextImplementationVersion
            + " because the project's contents have changed", nextImplementationVersion);
      }
    }
    catch (Exception ex)
    {
      try
      {
        if (validator != null)
        {
          validator.abort(buildState, project, ex, monitor);
        }

        String msg = Activator.log(ex);
        Markers.addMarker(project, msg);
      }
      catch (Exception ignore)
      {
        Activator.log(ignore);
      }
    }
    finally
    {
      deviations = null;
      integration = null;
      release = null;
      elementCache.clear();
      monitor.done();
    }

    return buildDpependencies.toArray(new IProject[buildDpependencies.size()]);
  }

  private boolean hasAPIToolsMarker(IPluginModelBase pluginModel)
  {
    try
    {
      IResource manifest = pluginModel.getUnderlyingResource();
      for (IMarker marker : manifest.findMarkers(IMarker.PROBLEM, true, IResource.DEPTH_ZERO))
      {
        if (marker.getType().startsWith("org.eclipse.pde.api.tools"))
        {
          return true;
        }
      }
    }
    catch (CoreException ex)
    {
      Activator.log(ex);
    }

    return false;
  }

  private boolean initReleaseProperties(IFile propertiesFile) throws CoreException, IOException
  {
    if (propertiesFile.exists())
    {
      InputStream contents = null;

      try
      {
        contents = propertiesFile.getContents();

        Properties properties = new Properties();
        properties.load(contents);

        deviations = Boolean.valueOf(properties.getProperty(DEVIATIONS_PROPERTY_KEY, "false"));

        Boolean newValue = Boolean.valueOf(properties.getProperty(INTEGRATION_PROPERTY_KEY, "true"));
        if (!newValue.equals(integration))
        {
          integration = newValue;
          return true;
        }

        return false;
      }
      finally
      {
        VersionUtil.close(contents);
      }
    }

    deviations = false;
    integration = true;

    String contents = INTEGRATION_PROPERTY_KEY + " = " + integration + NL + DEVIATIONS_PROPERTY_KEY + " = "
        + deviations + NL;

    String charsetName = propertiesFile.getCharset();
    byte[] bytes = contents.getBytes(charsetName);

    ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
    propertiesFile.create(bais, true, new NullProgressMonitor());
    return true;
  }

  private Version getNextImplVersion(Version releaseVersion)
  {
    return new Version(releaseVersion.getMajor(), releaseVersion.getMinor(), releaseVersion.getMicro()
        + (integration ? 100 : 1));
  }

  private Version getNextFeatureVersion(Version releaseVersion, Version nextImplementationVersion, int change)
  {
    Version nextFeatureVersion = null;
    if (change == MAJOR_CHANGE)
    {
      nextFeatureVersion = new Version(releaseVersion.getMajor() + 1, 0, 0);
    }
    else if (change == MINOR_CHANGE)
    {
      nextFeatureVersion = new Version(releaseVersion.getMajor(), releaseVersion.getMinor() + 1, 0);
    }
    else if (change == MICRO_CHANGE)
    {
      nextFeatureVersion = nextImplementationVersion;
    }

    return nextFeatureVersion;
  }

  private int checkFeatureContentChanges(IModel componentModel, IElement element, IElement releasedElement,
      List<Entry<IElement, Version>> warnings)
  {
    int biggestChange = NO_CHANGE;
    Set<IElement> allChildren = element.getAllChildren(this);
    for (IElement child : allChildren)
    {
      int change = checkFeatureContentChanges(element, releasedElement, child, warnings);
      biggestChange = Math.max(biggestChange, change);
    }

    for (IElement releasedElementsChild : releasedElement.getAllChildren(release))
    {
      if (!allChildren.contains(releasedElementsChild))
      {
        addWarning(releasedElementsChild, REMOVAL, warnings);
        biggestChange = MAJOR_CHANGE; // REMOVAL
      }
    }

    return biggestChange;
  }

  private void checkFeatureRedundancy(IElement element)
  {
    int i = 0;
    List<IElement> children = element.getChildren();
    for (IElement pluginChild : children)
    {
      if (pluginChild.getType() == IElement.Type.PLUGIN)
      {
        for (IElement featureChild : children)
        {
          if (featureChild.getType() == IElement.Type.FEATURE)
          {
            featureChild = resolveElement(featureChild);
            if (featureChild != null)
            {
              Set<IElement> allChildren = featureChild.getAllChildren(this);
              if (allChildren.contains(pluginChild))
              {
                try
                {
                  addRedundancyMarker(pluginChild, featureChild);
                }
                catch (Exception ex)
                {
                  Activator.log(ex);
                }
              }
            }
          }
        }

        if (children.indexOf(pluginChild) != i)
        {
          addRedundancyMarker(pluginChild, null);
        }
      }

      ++i;
    }
  }

  private int checkFeatureContentChanges(IElement element, IElement releasedElement, IElement childElement,
      List<Entry<IElement, Version>> warnings)
  {
    if (childElement.isUnresolved())
    {
      return NO_CHANGE;
    }

    IElement releasedElementsChild = releasedElement.getChild(release, childElement);
    if (releasedElementsChild == null)
    {
      addWarning(childElement, ADDITION, warnings);
      return MINOR_CHANGE; // ADDITION
    }

    IElement childsReleasedElement = release.getElements().get(childElement);
    if (childsReleasedElement == null)
    {
      return NO_CHANGE;
    }

    Version releasedVersion = childsReleasedElement.getVersion();
    Version version = childElement.getVersion();
    if (version == null)
    {
      return NO_CHANGE;
    }

    if (version.getMajor() != releasedVersion.getMajor())
    {
      addWarning(childsReleasedElement, version, warnings);
      return MAJOR_CHANGE;
    }

    if (version.getMinor() != releasedVersion.getMinor())
    {
      addWarning(childsReleasedElement, version, warnings);
      return MINOR_CHANGE;
    }

    if (version.getMicro() != releasedVersion.getMicro())
    {
      addWarning(childsReleasedElement, version, warnings);
      return MICRO_CHANGE;
    }

    return NO_CHANGE;
  }

  private void addWarning(final IElement releasedElement, final Version version,
      List<Map.Entry<IElement, Version>> warnings)
  {
    warnings.add(new Map.Entry<IElement, Version>()
    {
      public IElement getKey()
      {
        return releasedElement;
      }

      public Version getValue()
      {
        return version;
      }

      public Version setValue(Version value)
      {
        throw new UnsupportedOperationException();
      }
    });
  }

  private IProject getProject(IElement element)
  {
    String name = element.getName();
    if (element.getType() == IElement.Type.PLUGIN)
    {
      return getPluginProject(name);
    }

    return getFeatureProject(name);
  }

  private IProject getPluginProject(String name)
  {
    IPluginModelBase pluginModel = PluginRegistry.findModel(name);
    if (pluginModel != null)
    {
      IResource resource = pluginModel.getUnderlyingResource();
      if (resource != null)
      {
        return resource.getProject();
      }
    }

    return null;
  }

  @SuppressWarnings("restriction")
  private IProject getFeatureProject(String name)
  {
    org.eclipse.pde.internal.core.ifeature.IFeatureModel[] featureModels = org.eclipse.pde.internal.core.PDECore
        .getDefault().getFeatureModelManager().getWorkspaceModels();

    for (org.eclipse.pde.internal.core.ifeature.IFeatureModel featureModel : featureModels)
    {
      IResource resource = featureModel.getUnderlyingResource();
      if (resource != null && featureModel.getFeature().getId().equals(name))
      {
        return resource.getProject();
      }
    }

    return null;
  }

  private boolean checkMalformedVersions(IModel componentModel)
  {
    IResource underlyingResource = componentModel.getUnderlyingResource();
    if (underlyingResource != null)
    {
      IProject project = underlyingResource.getProject();
      if (project.isAccessible())
      {
        IFile file = null;
        String regex = null;
        if (componentModel instanceof IPluginModelBase)
        {
          file = project.getFile(MANIFEST_PATH);
          regex = "Bundle-Version: *(\\d+(\\.\\d+(\\.\\d+(\\.[-_a-zA-Z0-9]+)?)?)?)";
        }
        else
        {
          file = project.getFile(FEATURE_PATH);
          regex = "feature.*?version\\s*=\\s*[\"'](\\d+(\\.\\d+(\\.\\d+(\\.[-_a-zA-Z0-9]+)?)?)?)";
        }

        if (file.exists())
        {
          try
          {
            String content = VersionUtil.getContent(file);
            Pattern pattern = Pattern.compile(regex, Pattern.MULTILINE | Pattern.DOTALL);
            Matcher matcher = pattern.matcher(content);
            if (matcher.find())
            {
              String version = matcher.group(1);
              if (matcher.groupCount() < 4 || !".qualifier".equals(matcher.group(4)))
              {
                Version expectedVersion = new Version(version);
                addMalformedVersionMarker(file, regex,
                    new Version(expectedVersion.getMajor(), expectedVersion.getMinor(), expectedVersion.getMicro(),
                        "qualifier"));
                return true;
              }
            }
          }
          catch (Exception ex)
          {
            Activator.log(ex);
          }
        }
      }
    }

    return false;
  }

  private void checkDependencyRanges(IPluginModelBase pluginModel) throws CoreException, IOException
  {
    BundleDescription description = pluginModel.getBundleDescription();
    if (description == null)
    {
      return;
    }

    for (BundleSpecification requiredBundle : description.getRequiredBundles())
    {
      VersionRange range = requiredBundle.getVersionRange();
      if (isUnspecified(getMaximum(range)))
      {
        addRequireMarker(requiredBundle.getName(), "dependency must specify a version range");
      }
      else
      {
        if (!range.getIncludeMinimum())
        {
          addRequireMarker(requiredBundle.getName(), "dependency range must include the minimum");
        }

        if (range.getIncludeMaximum())
        {
          addRequireMarker(requiredBundle.getName(), "dependency range must not include the maximum");
        }
      }
    }

    for (ImportPackageSpecification importPackage : description.getImportPackages())
    {
      VersionRange range = importPackage.getVersionRange();
      if (isUnspecified(getMaximum(range)))
      {
        addImportMarker(importPackage.getName(), "dependency must specify a version range");
      }
      else
      {
        if (!range.getIncludeMinimum())
        {
          addImportMarker(importPackage.getName(), "dependency range must include the minimum");
        }

        if (range.getIncludeMaximum())
        {
          addImportMarker(importPackage.getName(), "dependency range must not include the maximum");
        }
      }
    }
  }

  @SuppressWarnings("deprecation")
  private Version getMaximum(VersionRange range)
  {
    DeprecationUtil.someDeprecatedCode(); // Just make sure that this method refers to some deprecated code
    return range.getMaximum();
  }

  private boolean isUnspecified(Version version)
  {
    if (version.getMajor() != Integer.MAX_VALUE)
    {
      return false;
    }

    if (version.getMinor() != Integer.MAX_VALUE)
    {
      return false;
    }

    if (version.getMicro() != Integer.MAX_VALUE)
    {
      return false;
    }

    return true;
  }

  private void checkPackageExports(IPluginModelBase pluginModel) throws CoreException, IOException
  {
    BundleDescription description = pluginModel.getBundleDescription();
    String bundleName = description.getSymbolicName();
    Version bundleVersion = VersionUtil.normalize(description.getVersion());

    for (ExportPackageDescription packageExport : description.getExportPackages())
    {
      String packageName = packageExport.getName();
      if (isBundlePackage(packageName, bundleName))
      {
        Version packageVersion = packageExport.getVersion();
        if (packageVersion != null && !packageVersion.equals(Version.emptyVersion)
            && !packageVersion.equals(bundleVersion))
        {
          addExportMarker(packageName, bundleVersion);
        }
      }
    }
  }

  private void checkSchemaBuilder(IPluginModelBase pluginModel, IFile file) throws CoreException, IOException
  {
    IProjectDescription description = getProject().getDescription();

    IPluginBase pluginBase = pluginModel.getPluginBase();
    if (pluginBase != null)
    {
      IPluginExtensionPoint[] extensionPoints = pluginBase.getExtensionPoints();
      if (extensionPoints != null & extensionPoints.length != 0)
      {
        // Plugin has an extension point. Check that SchemaBuilder is configured.
        for (ICommand command : description.getBuildSpec())
        {
          if ("org.eclipse.pde.SchemaBuilder".equals(command.getBuilderName()))
          {
            return;
          }
        }

        String regex = "<buildCommand\\s*>\\s*<name>\\s*org.eclipse.pde.ManifestBuilder\\s*</name>.*?</buildCommand>(\\s*)";
        String msg = "Schema builder is missing";

        IMarker marker = Markers.addMarker(file, msg, IMarker.SEVERITY_WARNING, regex);
        marker.setAttribute(Markers.QUICK_FIX_PATTERN, regex);
        marker.setAttribute(Markers.QUICK_FIX_REPLACEMENT, NL + "\t\t<buildCommand>" + NL
            + "\t\t\t<name>org.eclipse.pde.SchemaBuilder</name>" + NL + "\t\t\t<arguments>" + NL + "\t\t\t</arguments>"
            + NL + "\t\t</buildCommand>" + NL + "\t\t");
        marker
            .setAttribute(Markers.QUICK_FIX_CONFIGURE_OPTION, IVersionBuilderArguments.IGNORE_SCHEMA_BUILDER_ARGUMENT);
        return;
      }
    }

    // Plugin has no extension point(s). Check that SchemaBuilder is not configured.
    for (ICommand command : description.getBuildSpec())
    {
      if ("org.eclipse.pde.SchemaBuilder".equals(command.getBuilderName()))
      {
        String regex = "(<buildCommand\\s*>\\s*<name>\\s*org.eclipse.pde.SchemaBuilder\\s*</name>.*?</buildCommand>)\\s*";
        String msg = "No schema builder is needed because no extension point is declared";

        IMarker marker = Markers.addMarker(file, msg, IMarker.SEVERITY_WARNING, regex);
        marker.setAttribute(Markers.QUICK_FIX_PATTERN, regex);
        marker
            .setAttribute(Markers.QUICK_FIX_CONFIGURE_OPTION, IVersionBuilderArguments.IGNORE_SCHEMA_BUILDER_ARGUMENT);
        break;
      }
    }
  }

  private void checkDebugOptions(IPluginModelBase pluginModel) throws CoreException, IOException
  {
    IFile file = getProject().getFile(OPTIONS_PATH);
    if (file.isAccessible())
    {
      String symbolicName = pluginModel.getBundleDescription().getSymbolicName();
      String content = VersionUtil.getContent(file);

      Matcher matcher = DEBUG_OPTION_PATTERN.matcher(content);
      while (matcher.find())
      {
        String pluginID = matcher.group(2);
        if (!symbolicName.equals(pluginID))
        {
          String prefix = matcher.group(1);
          String suffix = "/" + (matcher.group(3) + matcher.group(4)).replace(".", "\\.");
          pluginID = pluginID.replace(".", "\\.");

          String regex = prefix + "(" + pluginID + ")" + suffix;
          String msg = "Debug option should be '" + symbolicName + "/" + matcher.group(3) + "'";

          IMarker marker = Markers.addMarker(file, msg, IMarker.SEVERITY_ERROR, regex);
          marker.setAttribute(Markers.QUICK_FIX_PATTERN, regex);
          marker.setAttribute(Markers.QUICK_FIX_REPLACEMENT, symbolicName);
          marker.setAttribute(Markers.QUICK_FIX_CONFIGURE_OPTION,
              IVersionBuilderArguments.IGNORE_SCHEMA_BUILDER_ARGUMENT);
        }
      }
    }
  }

  private boolean isBundlePackage(String packageName, String bundleName)
  {
    if (packageName.startsWith(bundleName))
    {
      return true;
    }

    int lastDot = bundleName.lastIndexOf('.');
    if (lastDot != -1)
    {
      String bundleStart = bundleName.substring(0, lastDot);
      String bundleEnd = bundleName.substring(lastDot + 1);
      if (packageName.startsWith(bundleStart + ".internal." + bundleEnd))
      {
        return true;
      }

      if (packageName.startsWith(bundleStart + ".spi." + bundleEnd))
      {
        return true;
      }
    }

    return false;
  }

  private void addRequireMarker(String name, String message)
  {
    try
    {
      IFile file = getProject().getFile(MANIFEST_PATH);
      String regex = name.replaceAll("\\.", "\\\\.") + ";bundle-version=\"([^\\\"]*)\"";

      IMarker marker = Markers.addMarker(file, "'" + name + "' " + message, IMarker.SEVERITY_ERROR, regex);
      if (marker != null)
      {
        marker.setAttribute(Markers.QUICK_FIX_CONFIGURE_OPTION,
            IVersionBuilderArguments.IGNORE_DEPENDENCY_RANGES_ARGUMENT);
      }
    }
    catch (Exception ex)
    {
      Activator.log(ex);
    }
  }

  private void addImportMarker(String name, String message)
  {
    try
    {
      IFile file = getProject().getFile(MANIFEST_PATH);
      String regex = name.replaceAll("\\.", "\\\\.") + ";version=\"([^\\\"]*)\"";

      IMarker marker = Markers.addMarker(file, "'" + name + "' " + message, IMarker.SEVERITY_ERROR, regex);
      if (marker != null)
      {
        marker.setAttribute(Markers.QUICK_FIX_CONFIGURE_OPTION,
            IVersionBuilderArguments.IGNORE_DEPENDENCY_RANGES_ARGUMENT);
      }
    }
    catch (Exception ex)
    {
      Activator.log(ex);
    }
  }

  private void addExportMarker(String name, Version bundleVersion)
  {
    String versionString = bundleVersion.toString();

    try
    {
      IFile file = getProject().getFile(MANIFEST_PATH);
      String message = "Export of package '" + name + "' should have the version " + versionString;
      String regex = name.replaceAll("\\.", "\\\\.") + ";version=\"([0123456789\\.]*)\"";

      IMarker marker = Markers.addMarker(file, message, IMarker.SEVERITY_ERROR, regex);
      marker.setAttribute(Markers.QUICK_FIX_PATTERN, regex);
      marker.setAttribute(Markers.QUICK_FIX_REPLACEMENT, versionString);
      marker.setAttribute(Markers.QUICK_FIX_CONFIGURE_OPTION, IVersionBuilderArguments.IGNORE_EXPORT_VERSIONS_ARGUMENT);
    }
    catch (Exception ex)
    {
      Activator.log(ex);
    }
  }

  private void addMalformedVersionMarker(IFile file, String regex, Version version)
  {
    try
    {
      String versionString = version.toString();
      IMarker marker = Markers.addMarker(file, "The version should be of the form '" + versionString + "'",
          IMarker.SEVERITY_ERROR, regex);
      marker.setAttribute(Markers.QUICK_FIX_PATTERN, regex);
      marker.setAttribute(Markers.QUICK_FIX_REPLACEMENT, versionString);
      marker.setAttribute(Markers.QUICK_FIX_CONFIGURE_OPTION,
          IVersionBuilderArguments.IGNORE_MALFORMED_VERSIONS_ARGUMENT);
    }
    catch (Exception ex)
    {
      Activator.log(ex);
    }
  }

  private IMarker addDeviationMarker(IElement element, Version releasedVersion)
  {
    String type;
    if (element.getType() == IElement.Type.PLUGIN)
    {
      type = "Plug-in";
    }
    else
    {
      type = "Feature";
    }

    Version version = element.getVersion();
    String message = type + " '" + element.getName() + "' has been changed from " + releasedVersion + " to " + version;
    return addVersionMarker(message, version, IMarker.SEVERITY_INFO);
  }

  private IMarker addVersionMarker(String message, Version version)
  {
    return addVersionMarker(message, version, IMarker.SEVERITY_ERROR);
  }

  private IMarker addVersionMarker(String message, Version version, int severity)
  {
    try
    {
      String regex;
      IFile file = getProject().getFile(FEATURE_PATH);
      if (file.exists())
      {
        regex = "feature.*?version\\s*=\\s*[\"'](\\d+(\\.\\d+(\\.\\d+)?)?)";
      }
      else
      {
        file = getProject().getFile(MANIFEST_PATH);
        regex = "Bundle-Version: *(\\d+(\\.\\d+(\\.\\d+)?)?)";
      }

      IMarker marker = Markers.addMarker(file, message, severity, regex);
      if (severity != IMarker.SEVERITY_INFO)
      {
        marker.setAttribute(Markers.QUICK_FIX_PATTERN, regex);
        marker.setAttribute(Markers.QUICK_FIX_REPLACEMENT, version.toString());
      }

      return marker;
    }
    catch (Exception ex)
    {
      Activator.log(ex);
      return null;
    }
  }

  private void addRedundancyMarker(IElement pluginChild, IElement featureChild)
  {
    try
    {
      IFile file = getProject().getFile(FEATURE_PATH);
      String name = pluginChild.getName();
      String cause = featureChild != null ? "feature '" + featureChild.getName() + "' already includes it"
          : " because it occurs more than once in this feature";
      String msg = "Plug-in reference '" + name + "' is redundant because " + cause;

      IMarker marker = addFeatureChildMarker(file, "plugin", name, msg);
      marker.setAttribute(Markers.QUICK_FIX_CONFIGURE_OPTION,
          IVersionBuilderArguments.IGNORE_CONTENT_REDUNDANCY_ARGUMENT);
    }
    catch (Exception ex)
    {
      Activator.log(ex);
    }
  }

  private void addIncludeMarker(IElement releasedElement, Version version)
  {
    try
    {
      IFile file = getProject().getFile(FEATURE_PATH);
      String type;
      String tag;
      if (releasedElement.getType() == IElement.Type.PLUGIN)
      {
        type = "Plug-in";
        tag = "plugin";
      }
      else
      {
        type = "Feature";
        tag = "includes";
      }

      String name = releasedElement.getName();

      if (version == REMOVAL)
      {
        String msg = type + " reference '" + name + "' has been removed";
        Markers.addMarker(file, msg, IMarker.SEVERITY_WARNING);
      }
      else
      {
        String msg;
        if (version == ADDITION)
        {
          msg = type + " reference '" + name + "' has been added with " + releasedElement.getVersion();
        }
        else
        {
          msg = type + " reference '" + name + "' has been changed from " + releasedElement.getVersion() + " to "
              + version;
        }

        IMarker marker = addFeatureChildMarker(file, tag, name, msg);
        marker.setAttribute(Markers.QUICK_FIX_CONFIGURE_OPTION,
            IVersionBuilderArguments.IGNORE_CONTENT_CHANGES_ARGUMENT);
      }
    }
    catch (Exception ex)
    {
      Activator.log(ex);
    }
  }

  private IMarker addFeatureChildMarker(IFile file, String tag, String name, String msg) throws CoreException,
      IOException
  {
    String regex = "[ \\t\\x0B\\f]*<" + tag + "\\s+.*?id\\s*=\\s*[\"'](" + name.replace(".", "\\.")
        + ").*?/>([ \\t\\x0B\\f]*[\\n\\r])*";
    IMarker marker = Markers.addMarker(file, msg, IMarker.SEVERITY_WARNING, regex);
    marker.setAttribute(Markers.QUICK_FIX_PATTERN, regex);
    return marker;
  }
}
