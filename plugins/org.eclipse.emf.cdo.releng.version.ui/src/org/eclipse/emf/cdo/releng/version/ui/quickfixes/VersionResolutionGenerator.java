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

import org.eclipse.emf.cdo.releng.version.Markers;

import org.eclipse.core.resources.IMarker;
import org.eclipse.ui.IMarkerResolution;
import org.eclipse.ui.IMarkerResolutionGenerator2;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Eike Stepper
 */
public class VersionResolutionGenerator implements IMarkerResolutionGenerator2
{
  public VersionResolutionGenerator()
  {
  }

  public IMarkerResolution[] getResolutions(IMarker marker)
  {
    List<IMarkerResolution> resolutions = new ArrayList<IMarkerResolution>();

    String regEx = Markers.getQuickFixPattern(marker);
    if (regEx != null)
    {
      String replacement = Markers.getQuickFixReplacement(marker);
      resolutions.add(new ReplaceResolution(marker, replacement));
    }

    String ignoreOption = Markers.getQuickFixConfigureOption(marker);
    if (ignoreOption != null)
    {
      resolutions.add(new ConfigureResolution(marker, ignoreOption));
    }

    return resolutions.toArray(new IMarkerResolution[resolutions.size()]);
  }

  public boolean hasResolutions(IMarker marker)
  {
    if (Markers.getQuickFixPattern(marker) != null)
    {
      return true;
    }

    if (Markers.getQuickFixConfigureOption(marker) != null)
    {
      return true;
    }

    return false;
  }
}
