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
  public static final String MARKER_TYPE = "org.eclipse.emf.cdo.releng.version.VersionProblem"; //$NON-NLS-1$

  private static final String NL = System.getProperty("line.separator"); //$NON-NLS-1$

  private Markers()
  {
  }

  public static void addMarker(IResource resource, String message) throws CoreException
  {
    addMarker(resource, message, IMarker.SEVERITY_ERROR);
  }

  public static void addMarker(IResource resource, String message, int severity) throws CoreException
  {
    IMarker marker = resource.createMarker(MARKER_TYPE);
    marker.setAttribute(IMarker.MESSAGE, message);
    marker.setAttribute(IMarker.SEVERITY, severity);
  }

  public static void addMarker(IResource resource, String message, int severity, int lineNumber) throws CoreException
  {
    IMarker marker = resource.createMarker(MARKER_TYPE);
    marker.setAttribute(IMarker.MESSAGE, message);
    marker.setAttribute(IMarker.SEVERITY, severity);
    if (lineNumber == -1)
    {
      lineNumber = 1;
    }

    marker.setAttribute(IMarker.LINE_NUMBER, lineNumber);
  }

  public static void addMarker(IFile file, String message, int severity, int lineNumber, int charStart, int charEnd)
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
  }

  public static void addMarker(IFile file, String message, int severity, String regex) throws CoreException,
      IOException
  {
    Pattern pattern = Pattern.compile(regex);
    InputStream contents = null;

    try
    {
      contents = file.getContents();
      BufferedReader reader = new BufferedReader(new InputStreamReader(contents));

      String line;
      int lineNumber = 1;
      int charNumber = 0;
      while ((line = reader.readLine()) != null)
      {
        Matcher matcher = pattern.matcher(line);
        if (matcher.matches())
        {
          int startChar = charNumber + matcher.start(1);
          int endChar = charNumber + matcher.end(1);
          addMarker(file, message, severity, lineNumber, startChar, endChar);
          return;
        }

        lineNumber += 1;
        charNumber += line.length() + NL.length();
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

    addMarker(file, message, severity);
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
