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

import org.eclipse.emf.cdo.releng.version.ui.Activator;

import org.eclipse.core.resources.IMarker;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.views.markers.WorkbenchMarkerResolution;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Eike Stepper
 */
public abstract class AbstractResolution extends WorkbenchMarkerResolution
{
  private IMarker marker;

  private String label;

  private String imageKey;

  public AbstractResolution(IMarker marker, String label, String imageKey)
  {
    this.marker = marker;
    this.label = label;
    this.imageKey = imageKey;
  }

  public IMarker getMarker()
  {
    return marker;
  }

  public String getLabel()
  {
    return label;
  }

  public String getDescription()
  {
    return "";
  }

  public final Image getImage()
  {
    ImageRegistry imageRegistry = Activator.getPlugin().getImageRegistry();
    return imageRegistry.get(imageKey);
  }

  @Override
  public IMarker[] findOtherMarkers(IMarker[] markers)
  {
    List<IMarker> result = new ArrayList<IMarker>();
    for (IMarker marker : markers)
    {
      try
      {
        if (marker != this.marker && isApplicable(marker))
        {
          result.add(marker);
        }
      }
      catch (Exception ex)
      {
        Activator.log(ex);
      }
    }

    return result.toArray(new IMarker[result.size()]);
  }

  protected abstract boolean isApplicable(IMarker marker) throws Exception;

  public final void run(IMarker marker)
  {
    try
    {
      apply(marker);
    }
    catch (Exception ex)
    {
      Activator.log(ex);
    }
  }

  protected abstract void apply(IMarker marker) throws Exception;
}
