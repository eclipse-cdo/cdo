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
package org.eclipse.emf.cdo.releng.version.ui;

import org.eclipse.emf.cdo.releng.version.Markers;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.IMarkerResolution;
import org.eclipse.ui.IMarkerResolutionGenerator2;
import org.eclipse.ui.views.markers.WorkbenchMarkerResolution;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.CharArrayWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Eike Stepper
 */
public class QuickFixer implements IMarkerResolutionGenerator2
{
  private static final IMarkerResolution[] NO_MARKER_RESOLUTIONS = {};

  public QuickFixer()
  {
  }

  public IMarkerResolution[] getResolutions(IMarker marker)
  {
    if (hasResolutions(marker))
    {
      IMarkerResolution[] markers = { new VersionMarkerResolution(marker) };
      return markers;
    }

    return NO_MARKER_RESOLUTIONS;
  }

  public boolean hasResolutions(IMarker marker)
  {
    return Markers.hasQuickFixes(marker);
  }

  /**
   * @author Eike Stepper
   */
  private static final class VersionMarkerResolution extends WorkbenchMarkerResolution
  {
    private IMarker marker;

    public VersionMarkerResolution(IMarker marker)
    {
      this.marker = marker;
    }

    public String getLabel()
    {
      return "Foo";
    }

    public String getDescription()
    {
      return "Foo Foo Foo";
    }

    public Image getImage()
    {
      return null;
    }

    public void run(IMarker marker)
    {
      try
      {
        String regEx = (String)marker.getAttribute(Markers.QUICK_FIX_PATTERN);
        String replacement = (String)marker.getAttribute(Markers.QUICK_FIX_REPLACEMENT);
        if (regEx != null)
        {
          IFile file = (IFile)marker.getResource();
          InputStream contents = file.getContents();
          BufferedReader reader = new BufferedReader(new InputStreamReader(contents, file.getCharset()));
          CharArrayWriter caw = new CharArrayWriter();

          int c;
          while ((c = reader.read()) != -1)
          {
            caw.write(c);
          }

          String string = caw.toString();
          Pattern pattern = Pattern.compile(regEx, Pattern.MULTILINE | Pattern.DOTALL);
          Matcher matcher = pattern.matcher(string);
          if (matcher.find())
          {
            String before;
            String after;
            if (replacement != null)
            {
              before = string.substring(0, matcher.start(1));
              after = string.substring(matcher.end(1));
            }
            else
            {
              before = string.substring(0, matcher.start());
              after = string.substring(matcher.end());
              replacement = "";
            }

            file.setContents(new ByteArrayInputStream((before + replacement + after).getBytes(file.getCharset())), true, true,
                null);
          }
        }
      }
      catch (Exception ex)
      {
        Activator.log(ex);
      }
    }

    @Override
    public IMarker[] findOtherMarkers(IMarker[] markers)
    {
      List<IMarker> result = new ArrayList<IMarker>();
      for (IMarker marker : markers)
      {
        if (marker != this.marker && Markers.hasQuickFixes(marker))
        {
          result.add(marker);
        }
      }

      return result.toArray(new IMarker[result.size()]);
    }
  }
}
