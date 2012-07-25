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

import org.eclipse.core.resources.ICommand;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author Eike Stepper
 */
public class VersionBuilderArguments extends HashMap<String, String> implements IVersionBuilderArguments
{
  private static final long serialVersionUID = 1L;

  private static final String RELEASE_PATH_ARGUMENT = "release.path";

  private static final String VALIDATOR_CLASS_ARGUMENT = "validator.class";

  private static final String IGNORE_DEPENDENCY_RANGES_ARGUMENT = "ignore.missing.dependency.ranges";

  private static final String IGNORE_EXPORT_VERSIONS_ARGUMENT = "ignore.missing.export.versions";

  private static final String IGNORE_CONTENT_REDUNDANCY_ARGUMENT = "ignore.feature.content.redundancy";

  private static final String IGNORE_CONTENT_CHANGES_ARGUMENT = "ignore.feature.content.changes";

  public VersionBuilderArguments()
  {
  }

  public VersionBuilderArguments(int initialCapacity, float loadFactor)
  {
    super(initialCapacity, loadFactor);
  }

  public VersionBuilderArguments(int initialCapacity)
  {
    super(initialCapacity);
  }

  public VersionBuilderArguments(Map<? extends String, ? extends String> m)
  {
    super(m);
  }

  public VersionBuilderArguments(IProject project)
  {
    this(getArgumentsFromProject(project));
  }

  public String getReleasePath()
  {
    return get(RELEASE_PATH_ARGUMENT);
  }

  public String getValidatorClassName()
  {
    return get(VALIDATOR_CLASS_ARGUMENT);
  }

  public boolean isIgnoreMissingDependencyRanges()
  {
    return "true".equals(get(IGNORE_DEPENDENCY_RANGES_ARGUMENT));
  }

  public boolean isIgnoreMissingExportVersions()
  {
    return "true".equals(get(IGNORE_EXPORT_VERSIONS_ARGUMENT));
  }

  public boolean isIgnoreFeatureContentRedundancy()
  {
    return "true".equals(get(IGNORE_CONTENT_REDUNDANCY_ARGUMENT));
  }

  public boolean isIgnoreFeatureContentChanges()
  {
    return "true".equals(get(IGNORE_CONTENT_CHANGES_ARGUMENT));
  }

  public void setReleasePath(String value)
  {
    if (value != null)
    {
      put(RELEASE_PATH_ARGUMENT, value);
    }
    else
    {
      remove(RELEASE_PATH_ARGUMENT);
    }
  }

  public void setValidatorClassName(String value)
  {
    if (value != null)
    {
      put(VALIDATOR_CLASS_ARGUMENT, value);
    }
    else
    {
      remove(VALIDATOR_CLASS_ARGUMENT);
    }
  }

  public void setIgnoreMissingDependencyRanges(boolean value)
  {
    if (value)
    {
      put(IGNORE_DEPENDENCY_RANGES_ARGUMENT, Boolean.toString(true));
    }
    else
    {
      remove(IGNORE_DEPENDENCY_RANGES_ARGUMENT);
    }
  }

  public void setIgnoreMissingExportVersions(boolean value)
  {
    if (value)
    {
      put(IGNORE_EXPORT_VERSIONS_ARGUMENT, Boolean.toString(true));
    }
    else
    {
      remove(IGNORE_EXPORT_VERSIONS_ARGUMENT);
    }
  }

  public void setIgnoreFeatureContentRedundancy(boolean value)
  {
    if (value)
    {
      put(IGNORE_CONTENT_REDUNDANCY_ARGUMENT, Boolean.toString(true));
    }
    else
    {
      remove(IGNORE_CONTENT_REDUNDANCY_ARGUMENT);
    }
  }

  public void setIgnoreFeatureContentChanges(boolean value)
  {
    if (value)
    {
      put(IGNORE_CONTENT_CHANGES_ARGUMENT, Boolean.toString(true));
    }
    else
    {
      remove(IGNORE_CONTENT_CHANGES_ARGUMENT);
    }
  }

  public void applyTo(IProject project) throws CoreException
  {
    IProjectDescription description = project.getDescription();

    List<String> ids = getOtherNatures(description);
    ids.add(VersionNature.NATURE_ID);
    description.setNatureIds(ids.toArray(new String[ids.size()]));

    List<ICommand> commands = getOtherBuildCommands(description);
    commands.add(createBuildCommand(description));
    description.setBuildSpec(commands.toArray(new ICommand[commands.size()]));

    project.setDescription(description, new NullProgressMonitor());
  }

  private ICommand createBuildCommand(IProjectDescription description)
  {
    ICommand command = description.newCommand();
    command.setBuilderName(VersionBuilder.BUILDER_ID);
    command.setArguments(this);
    return command;
  }

  public static List<String> getOtherNatures(IProjectDescription description)
  {
    String[] natureIds = description.getNatureIds();
    List<String> ids = new ArrayList<String>(Arrays.asList(natureIds));
    ids.remove(VersionNature.NATURE_ID);
    return ids;
  }

  private static List<ICommand> getOtherBuildCommands(IProjectDescription description)
  {
    ICommand[] buildSpec = description.getBuildSpec();

    List<ICommand> commands = new ArrayList<ICommand>(Arrays.asList(buildSpec));
    for (Iterator<ICommand> it = commands.iterator(); it.hasNext();)
    {
      ICommand command = it.next();
      if (VersionBuilder.BUILDER_ID.equals(command.getBuilderName()))
      {
        it.remove();
        break;
      }
    }

    return commands;
  }

  private static Map<String, String> getArgumentsFromProject(IProject project)
  {
    try
    {
      IProjectDescription description = project.getDescription();
      for (ICommand command : description.getBuildSpec())
      {
        if (VersionBuilder.BUILDER_ID.equals(command.getBuilderName()))
        {
          return command.getArguments();
        }
      }
    }
    catch (CoreException ex)
    {
      Activator.log(ex);
    }

    return new HashMap<String, String>();
  }
}
