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
package org.eclipse.emf.cdo.releng.version.ui.quickfixes;

import org.eclipse.emf.cdo.releng.internal.version.VersionBuilder;
import org.eclipse.emf.cdo.releng.internal.version.VersionBuilderArguments;
import org.eclipse.emf.cdo.releng.version.Markers;
import org.eclipse.emf.cdo.releng.version.VersionUtil;
import org.eclipse.emf.cdo.releng.version.ui.Activator;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.Path;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Properties;

/**
 * @author Eike Stepper
 */
public class RootProjectResolution extends AbstractResolution
{
  public RootProjectResolution(IMarker marker)
  {
    super(marker, "Mark as root project", Activator.CORRECTION_CONFIGURE_GIF);
  }

  @Override
  protected boolean isApplicable(IMarker marker)
  {
    return Markers.UNREFERENCED_ELEMENT_PROBLEM.equals(Markers.getProblemType(marker));
  }

  @Override
  public String getDescription()
  {
    IMarker marker = getMarker();
    IProject project = marker.getResource().getProject();
    VersionBuilderArguments arguments = new VersionBuilderArguments(project);
    IFile propertiesFile = VersionUtil.getFile(new Path(arguments.getReleasePath()), "properties");
    return "Add '" + project.getName() + "' to the " + VersionBuilder.ROOT_PROJECTS_KEY + " property of "
        + propertiesFile.getFullPath();
  }

  @Override
  protected void apply(IMarker marker) throws Exception
  {
    IProject project = marker.getResource().getProject();
    VersionBuilderArguments arguments = new VersionBuilderArguments(project);
    IFile propertiesFile = VersionUtil.getFile(new Path(arguments.getReleasePath()), "properties");
    if (propertiesFile.exists())
    {
      Properties properties = new Properties();
      InputStream contents = null;
      try
      {
        contents = propertiesFile.getContents();

        properties = new Properties();
        properties.load(contents);

        String oldValue = properties.getProperty(VersionBuilder.ROOT_PROJECTS_KEY, "");
        properties.setProperty(VersionBuilder.ROOT_PROJECTS_KEY,
            (oldValue + " " + project.getName().replace("\\", "\\\\").replace(" ", "\\ ")).trim());
      }
      finally
      {
        VersionUtil.close(contents);
      }

      ByteArrayOutputStream out = new ByteArrayOutputStream();
      properties.store(out, "");
      contents = new ByteArrayInputStream(out.toByteArray());
      propertiesFile.setContents(contents, true, true, null);
    }
  }
}
