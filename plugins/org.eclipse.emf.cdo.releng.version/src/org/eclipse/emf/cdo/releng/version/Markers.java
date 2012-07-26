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

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;

import java.io.BufferedReader;
import java.io.CharArrayWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Eike Stepper
 */
public final class Markers
{
  public static final String MARKER_TYPE = "org.eclipse.emf.cdo.releng.version.VersionProblem";

  public static final String QUICK_FIX_PATTERN = "quickFixPattern";

  public static final String QUICK_FIX_REPLACEMENT = "quickReplacement";

  private static final Pattern NL_PATTERN = Pattern.compile("([\\n][\\r]?|[\\r][\\n]?)", Pattern.MULTILINE);

  private Markers()
  {
  }

  public static boolean hasQuickFixes(IMarker marker)
  {
    try
    {
      return marker.getAttribute(QUICK_FIX_PATTERN) != null;
    }
    catch (CoreException ex)
    {
      Activator.log(ex);
      return false;
    }
  }

  public static IMarker addMarker(IResource resource, String message) throws CoreException
  {
    return addMarker(resource, message, IMarker.SEVERITY_ERROR);
  }

  public static IMarker addMarker(IResource resource, String message, int severity) throws CoreException
  {
    IMarker marker = resource.createMarker(MARKER_TYPE);
    marker.setAttribute(IMarker.MESSAGE, message);
    marker.setAttribute(IMarker.SEVERITY, severity);
    return marker;
  }

  public static IMarker addMarker(IResource resource, String message, int severity, int lineNumber)
      throws CoreException
  {
    IMarker marker = resource.createMarker(MARKER_TYPE);
    marker.setAttribute(IMarker.MESSAGE, message);
    marker.setAttribute(IMarker.SEVERITY, severity);
    if (lineNumber == -1)
    {
      lineNumber = 1;
    }

    marker.setAttribute(IMarker.LINE_NUMBER, lineNumber);
    return marker;
  }

  public static IMarker addMarker(IFile file, String message, int severity, int lineNumber, int charStart, int charEnd)
      throws CoreException
  {
    if (lineNumber < 1)
    {
      lineNumber = 1;
    }

    IMarker marker = file.createMarker(MARKER_TYPE);
    marker.setAttribute(IMarker.MESSAGE, message);
    marker.setAttribute(IMarker.SEVERITY, severity);
    marker.setAttribute(IMarker.LINE_NUMBER, lineNumber);
    marker.setAttribute(IMarker.CHAR_START, charStart);
    marker.setAttribute(IMarker.CHAR_END, charEnd);
    return marker;
  }

  public static IMarker addMarker(IFile file, String message, int severity, String regex) throws CoreException,
      IOException
  {
    InputStream contents = null;

    try
    {
      contents = file.getContents();
      BufferedReader reader = new BufferedReader(new InputStreamReader(contents, file.getCharset()));
      CharArrayWriter caw = new CharArrayWriter();

      int c;
      while ((c = reader.read()) != -1)
      {
        caw.write(c);
      }

      String string = caw.toString();

      Pattern pattern = Pattern.compile(regex, Pattern.MULTILINE | Pattern.DOTALL);
      Matcher matcher = pattern.matcher(string);

      if (matcher.find())
      {
        int startChar = matcher.start(1);
        int endChar = matcher.end(1);

        matcher = NL_PATTERN.matcher(string);
        int line = 0;
        while (matcher.find())
        {
          if (matcher.start(1) > startChar)
          {
            break;
          }

          ++line;
        }

        return addMarker(file, message, severity, line, startChar, endChar);
      }
    }
    finally
    {
      if (contents != null)
      {
        try
        {
          contents.close();
        }
        catch (Exception ex)
        {
          Activator.log(ex);
        }
      }
    }

    return addMarker(file, message, severity);
  }

  public static void deleteMarkers(IResource resource) throws CoreException
  {
    resource.deleteMarkers(Markers.MARKER_TYPE, false, IResource.DEPTH_ZERO);
  }

  public static void deleteAllMarkers(IResource resource) throws CoreException
  {
    resource.deleteMarkers(Markers.MARKER_TYPE, false, IResource.DEPTH_INFINITE);
  }
}
