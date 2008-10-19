/***************************************************************************
 * Copyright (c) 2004 - 2008 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Simon McDuff - initial API and implementation
 *    Eike Stepper - maintenance
 **************************************************************************/
package org.eclipse.emf.cdo.util;

import org.eclipse.emf.cdo.CDOSession;
import org.eclipse.emf.cdo.CDOView;
import org.eclipse.emf.cdo.common.CDOProtocolConstants;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDUtil;
import org.eclipse.emf.cdo.internal.common.id.CDOIDExternalImpl;

import org.eclipse.emf.common.util.URI;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

/**
 * @author Simon McDuff
 * @since 2.0
 */
public class CDOURIUtil
{
  public static final char SEGMENT_SEPARATOR_CHAR = '/';

  public static final String SEGMENT_SEPARATOR = new String(new char[] { SEGMENT_SEPARATOR_CHAR });

  public static void validateURI(URI uri) throws InvalidURIException
  {
    if (!CDOProtocolConstants.PROTOCOL_NAME.equals(uri.scheme()))
    {
      throw new InvalidURIException(uri);
    }

    if (!uri.isHierarchical())
    {
      throw new InvalidURIException(uri);
    }
  }

  public static String extractRepositoryUUID(URI uri)
  {
    try
    {
      validateURI(uri);
      if (!uri.hasAuthority())
      {
        throw new InvalidURIException(uri);
      }

      return uri.authority();
    }
    catch (InvalidURIException ex)
    {
      return null;
    }
  }

  public static String[] extractResourceFolderAndName(URI uri) throws InvalidURIException
  {
    String path = extractResourcePath(uri);
    int lastSeparator = path.lastIndexOf(SEGMENT_SEPARATOR_CHAR);
    if (lastSeparator == -1)
    {
      return new String[] { null, path };
    }

    String folder = path.substring(0, lastSeparator);
    String name = path.substring(lastSeparator + 1);
    return new String[] { folder, name };
  }

  public static String extractResourcePath(URI uri) throws InvalidURIException
  {
    validateURI(uri);
    String path = uri.path();
    if (path == null)
    {
      return SEGMENT_SEPARATOR;
    }

    return path;
  }

  /**
   * <p>
   * cdo://repositoryUUID/path
   * <p>
   * The path is added at the end of "cdo://repositoryUUID". If path doesn't start with '/', it will be added
   * automatically. <br>
   * e.g.: /resA or resA will give the same result -> cdo://repositoryUUID/resA <br>
   * authority = repositoryUUID <br>
   * path = /resA
   */
  public static URI createResourceURI(String repositoryUUID, String path)
  {
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append(CDOProtocolConstants.PROTOCOL_NAME);
    stringBuilder.append(":");

    if (repositoryUUID != null)
    {
      stringBuilder.append("//");
      stringBuilder.append(repositoryUUID);
    }

    if (!SEGMENT_SEPARATOR.equals(path))
    {
      if (path.charAt(0) != SEGMENT_SEPARATOR_CHAR)
      {
        stringBuilder.append(SEGMENT_SEPARATOR_CHAR);
      }

      stringBuilder.append(path);
    }

    return URI.createURI(stringBuilder.toString());
  }

  public static URI createResourceURI(CDOView cdoView, String path)
  {
    return createResourceURI(cdoView == null ? null : cdoView.getSession(), path);
  }

  public static URI createResourceURI(CDOSession cdoSession, String path)
  {
    return createResourceURI(cdoSession == null ? null : cdoSession.getRepositoryUUID(), path);
  }

  /**
   * Converting temporary CDOID to External CDOID <br>
   * e.g.: <br>
   * baseURI = cdo://2a57dfcf-8f97-4d39-8e17-9d99ae5c4b3c/resB#5/2<br>
   * newCDOID = OID2<br>
   * return = cdo://2a57dfcf-8f97-4d39-8e17-9d99ae5c4b3c/resB#1/2
   */
  public static CDOID convertExternalCDOID(URI baseURI, CDOID newCDOID)
  {
    StringBuilder builder = new StringBuilder();
    CDOIDUtil.write(builder, newCDOID);

    baseURI = baseURI.trimFragment().appendFragment(builder.toString());
    return new CDOIDExternalImpl(baseURI.toString());
  }

  public static List<String> analyzePath(URI uri)
  {
    String path = extractResourcePath(uri);
    return analyzePath(path);
  }

  public static List<String> analyzePath(String path)
  {
    List<String> segments = new ArrayList<String>();
    StringTokenizer tokenizer = new StringTokenizer(path, CDOURIUtil.SEGMENT_SEPARATOR);
    while (tokenizer.hasMoreTokens())
    {
      String name = tokenizer.nextToken();
      if (name != null)
      {
        segments.add(name);
      }
    }

    return segments;
  }
}
