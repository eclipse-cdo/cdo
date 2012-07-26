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

import org.eclipse.core.filebuffers.ITextFileBuffer;
import org.eclipse.core.filebuffers.ITextFileBufferManager;
import org.eclipse.core.filebuffers.LocationKind;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.text.IDocument;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.IMarkerResolution;
import org.eclipse.ui.IMarkerResolutionGenerator2;
import org.eclipse.ui.views.markers.WorkbenchMarkerResolution;

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

          IPath fullPath = file.getFullPath();
          ITextFileBufferManager.DEFAULT.connect(fullPath, LocationKind.IFILE, new NullProgressMonitor());
          ITextFileBuffer buffer = ITextFileBufferManager.DEFAULT.getTextFileBuffer(fullPath, LocationKind.IFILE);
          boolean wasDirty = buffer.isDirty();

          IDocument document = buffer.getDocument();
          String content = document.get();

          // InputStream contents = file.getContents();
          // BufferedReader reader = new BufferedReader(new InputStreamReader(contents, file.getCharset()));
          // CharArrayWriter caw = new CharArrayWriter();
          //
          // int c;
          // while ((c = reader.read()) != -1)
          // {
          // caw.write(c);
          // }
          //
          // String content = caw.toString();

          Pattern pattern = Pattern.compile(regEx, Pattern.MULTILINE | Pattern.DOTALL);
          Matcher matcher = pattern.matcher(content);
          if (matcher.find())
          {
            int start;
            int end;
            if (replacement != null)
            {
              start = matcher.start(1);
              end = matcher.end(1);
            }
            else
            {
              start = matcher.start();
              end = matcher.end();
              replacement = "";
            }

            document.replace(start, end - start, replacement);

            // file.setContents(new ByteArrayInputStream((before + replacement + after).getBytes(file.getCharset())),
            // true, true, null);

            if (!wasDirty && !buffer.isShared())
            {
              buffer.commit(new NullProgressMonitor(), true);
            }

            ITextFileBufferManager.DEFAULT.disconnect(fullPath, LocationKind.IFILE, new NullProgressMonitor());
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
