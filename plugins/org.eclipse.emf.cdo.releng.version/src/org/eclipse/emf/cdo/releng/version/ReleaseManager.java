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
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import java.io.FileNotFoundException;
import java.util.Map;
import java.util.Map.Entry;
import java.util.WeakHashMap;

/**
 * @author Eike Stepper
 */
public class ReleaseManager
{
  public static final ReleaseManager INSTANCE = new ReleaseManager();

  private Map<Release, Long> releases = new WeakHashMap<Release, Long>();

  private SAXParserFactory parserFactory;

  private ReleaseManager()
  {
  }

  private SAXParser getParser() throws ParserConfigurationException, SAXException
  {
    if (parserFactory == null)
    {
      parserFactory = SAXParserFactory.newInstance();
    }

    return parserFactory.newSAXParser();
  }

  public synchronized Release getRelease(IFile file) throws CoreException
  {
    try
    {
      for (Entry<Release, Long> entry : releases.entrySet())
      {
        Release release = entry.getKey();
        if (release.getFile().equals(file))
        {
          long timeStamp = entry.getValue();
          if (file.getLocalTimeStamp() == timeStamp)
          {
            return release;
          }

          releases.remove(release);
          break;
        }
      }

      if (!file.exists())
      {
        throw new FileNotFoundException(file.getFullPath().toString());
      }

      Release release = new Release(getParser(), file);
      releases.put(release, file.getLocalTimeStamp());
      return release;
    }
    catch (CoreException ex)
    {
      throw ex;
    }
    catch (Exception ex)
    {
      throw new CoreException(new Status(IStatus.ERROR, Activator.PLUGIN_ID, ex.getLocalizedMessage(), ex));
    }
  }
}
