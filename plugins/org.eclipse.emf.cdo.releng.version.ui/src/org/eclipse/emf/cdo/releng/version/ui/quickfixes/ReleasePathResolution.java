/*
 * Copyright (c) 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.releng.version.ui.quickfixes;

import org.eclipse.emf.cdo.releng.internal.version.VersionBuilderArguments;
import org.eclipse.emf.cdo.releng.version.Markers;
import org.eclipse.emf.cdo.releng.version.VersionUtil;
import org.eclipse.emf.cdo.releng.version.ui.Activator;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;

import java.util.Set;

/**
 * @author Eike Stepper
 */
public class ReleasePathResolution extends AbstractResolution
{
  public ReleasePathResolution(IMarker marker)
  {
    super(marker, "Add to ignored releases", Activator.CORRECTION_CONFIGURE_GIF);
  }

  @Override
  protected boolean isApplicable(IMarker marker)
  {
    return Markers.RELEASE_PATH_PROBLEM.equals(Markers.getProblemType(marker));
  }

  @Override
  public String getDescription()
  {
    IMarker marker = getMarker();
    IProject project = marker.getResource().getProject();
    VersionBuilderArguments arguments = new VersionBuilderArguments(project);

    return "Add '"
        + arguments.getReleasePath()
        + "' to the set of ignored releases in the Version Management preferences. To re-enable this release use the Preferences dialog and manage the set of ignored releases.";
  }

  @Override
  protected void apply(IMarker marker) throws Exception
  {
    IProject project = marker.getResource().getProject();
    VersionBuilderArguments arguments = new VersionBuilderArguments(project);
    final String releasePath = arguments.getReleasePath();

    Set<String> ignoredReleases = org.eclipse.emf.cdo.releng.internal.version.Activator.getIgnoredReleases();
    if (ignoredReleases.add(releasePath))
    {
      VersionUtil.cleanReleaseProjects(releasePath);
    }
  }
}
