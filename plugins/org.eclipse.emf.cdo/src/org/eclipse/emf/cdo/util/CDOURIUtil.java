/*
 * Copyright (c) 2008-2012, 2015, 2016, 2019, 2020 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Simon McDuff - initial API and implementation
 *    Eike Stepper - maintenance
 */
package org.eclipse.emf.cdo.util;

import org.eclipse.emf.cdo.common.CDOCommonRepository;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDUtil;
import org.eclipse.emf.cdo.common.protocol.CDOProtocolConstants;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.view.CDOView;
import org.eclipse.emf.cdo.view.CDOViewProvider;
import org.eclipse.emf.cdo.view.CDOViewProvider.CDOViewProvider2;
import org.eclipse.emf.cdo.view.CDOViewProviderRegistry;

import org.eclipse.emf.internal.cdo.view.PluginContainerViewProvider;

import org.eclipse.net4j.util.StringUtil;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.ResourceSet;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

/**
 * Various static methods that may help with CDO-specific {@link URI URIs}.
 * <p>
 * CDO URIs are in one of two different formats, either canonical or connection-aware. The canonical format is:
 *
 * <blockquote><b>cdo://</b> <i>RepositoryUUID</i> <b>/</b> <i>ResourcePath</i> [<b>?</b> <i>Param</i><b>=</b><i>Value</i>
 * (<b>&amp;</b> <i>Param</i><b>=</b><i>Value</i>)*]</blockquote>
 *
 * The non-terminals being:
 * <p>
 * <ul>
 * <li><i>RepositoryUUID</i>: the {@link CDOCommonRepository#getUUID() UUID} of the repository. By default it's generated when a repository is first started.
 * If the default format is not adequate the UUID value can be overridden in the repository setup with the <code>overrideUUID</code> property.
 * <li><i>ResourcePath</i>: the full path of the {@link CDOResource resource} within the repository, segments separated by slashes, no leading slash.
 * <li><i>Param</i>: one of the following
 * <ul>
 * <li><b>prefetch</b>: a boolean value. The value <b>true</b> attempts to load all objects contained by the resource in a single server-round trip and cache the results.
 * </ul>
 * </ul>
 *
 * URIs in the canonical form to resolve to {@link CDOResource resources} properly require the {@link ResourceSet resource set} to be
 * configured <i>externally</i> so that the connection to the correct repository can be established, for example:
 * <blockquote><code>session.openView(resourceSet);</code></blockquote>
 *
 * Note that resources preserve their original URI in the scope of the managing {@link CDOView view}, that is not necessarily in canonical format.
 * <p>
 * For a description of the connection-aware URI format refer to {@link CDOURIData}.
 *
 * @author Simon McDuff
 * @since 2.0
 */
public final class CDOURIUtil
{
  /**
   * @since 4.0
   */
  public static final String PROTOCOL_NAME = CDOProtocolConstants.PROTOCOL_NAME;

  public static final char SEGMENT_SEPARATOR_CHAR = '/';

  public static final String SEGMENT_SEPARATOR = new String(new char[] { SEGMENT_SEPARATOR_CHAR });

  static
  {
    CDOUtil.registerResourceFactory(null); // Ensure that the normal resource factory is registered
  }

  private CDOURIUtil()
  {
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
    CDOViewProvider[] viewProviders = CDOViewProviderRegistry.INSTANCE.getViewProviders(uri);
    if (viewProviders != null)
    {
      for (int i = 0; i < viewProviders.length; i++)
      {
        if (viewProviders[i] instanceof CDOViewProvider2)
        {
          CDOViewProvider2 viewProvider = (CDOViewProvider2)viewProviders[i];
          String path = viewProvider.getPath(uri);
          if (path != null && !StringUtil.isEmpty(path))
          {
            return sanitizePath(path);
          }
        }
      }
    }

    if (!PROTOCOL_NAME.equals(uri.scheme()))
    {
      CDOURIData data = new CDOURIData(uri);
      return sanitizePath(data.getResourcePath().toPortableString());
    }

    return sanitizePath(uri.path());
  }

  public static URI createResourceURI(CDOView view, String path)
  {
    return view == null ? null : view.createResourceURI(path);
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
    return CDOIDUtil.createExternal(baseURI.toString());
  }

  /**
   * @since 4.11
   */
  public static String sanitizePath(String path)
  {
    if (path == null || path.length() == 0)
    {
      return SEGMENT_SEPARATOR;
    }

    if (path.charAt(0) != SEGMENT_SEPARATOR_CHAR)
    {
      return SEGMENT_SEPARATOR + path;
    }

    return path;
  }

  private static List<String> analyzeSanitizedPath(String path)
  {
    List<String> segments = new ArrayList<>();
    StringTokenizer tokenizer = new StringTokenizer(path, SEGMENT_SEPARATOR);
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

  public static List<String> analyzePath(URI uri)
  {
    String path = extractResourcePath(uri);
    return analyzeSanitizedPath(path);
  }

  public static List<String> analyzePath(String path)
  {
    return analyzeSanitizedPath(sanitizePath(path));
  }

  /**
   * @since 4.0
   */
  public static Map<String, String> getParameters(String query)
  {
    Map<String, String> parameters = new LinkedHashMap<>();

    if (query != null)
    {
      StringTokenizer tokenizer = new StringTokenizer(query, "&"); //$NON-NLS-1$
      while (tokenizer.hasMoreTokens())
      {
        String parameter = tokenizer.nextToken();
        if (!StringUtil.isEmpty(parameter))
        {
          int pos = parameter.indexOf('=');
          if (pos == -1)
          {
            String key = parameter.trim();
            parameters.put(key, ""); //$NON-NLS-1$
          }
          else
          {
            String key = parameter.substring(0, pos).trim();
            String value = parameter.substring(pos + 1);
            parameters.put(key, value);
          }
        }
      }
    }

    return parameters;
  }

  /**
   * @since 4.12
   */
  public static String formatQuery(Map<String, String> parameters)
  {
    StringBuilder query = new StringBuilder();

    for (Map.Entry<String, String> entry : parameters.entrySet())
    {
      appendQueryParameter(query, entry.getKey(), entry.getValue());
    }

    if (query.length() == 0)
    {
      return null;
    }

    return query.toString();
  }

  /**
   * @since 4.12
   */
  public static void appendQueryParameter(StringBuilder query, String parameter, String value)
  {
    if (query.length() != 0)
    {
      query.append("&");
    }

    query.append(parameter);
    query.append("=");
    query.append(value);
  }

  /**
   * @since 4.12
   */
  public static URI appendResourcePath(URI uri, String path)
  {
    boolean pathIsEmpty = true;
    if (path != null)
    {
      StringTokenizer tokenizer = new StringTokenizer(path, SEGMENT_SEPARATOR);
      while (tokenizer.hasMoreTokens())
      {
        String segment = tokenizer.nextToken();
        uri = uri.appendSegment(segment);
        pathIsEmpty = false;
      }
    }

    if (pathIsEmpty)
    {
      uri = uri.appendSegment("");
    }

    return uri;
  }

  /**
   * @since 4.12
   */
  public static URI trimResourceInfos(URI uri)
  {
    String query = uri.query();
    if (query != null && query.length() != 0)
    {
      Map<String, String> parameters = getParameters(query);
      if (parameters.containsKey(CDOResource.PREFETCH_PARAMETER))
      {
        parameters.remove(CDOResource.PREFETCH_PARAMETER);
        query = formatQuery(parameters);
      }
    }

    return URI.createHierarchicalURI(uri.scheme(), uri.authority(), uri.device(), query, uri.fragment());
  }

  /**
   * @deprecated
   */
  @Deprecated
  public static void validateURI(URI uri) throws InvalidURIException
  {
    // if (!CDOProtocolConstants.PROTOCOL_NAME.equals(uri.scheme()))
    // {
    // throw new InvalidURIException(uri);
    // }
    //
    // if (!uri.isHierarchical())
    // {
    // throw new InvalidURIException(uri);
    // }
  }

  /**
   * @deprecated
   */
  @Deprecated
  public static String extractRepositoryUUID(URI uri)
  {
    return PluginContainerViewProvider.getRepositoryUUID(uri);
  }

  /**
   * <p>
   * cdo://repositoryUUID/path
   * <p>
   * The path is added at the end of "cdo://repositoryUUID". If path doesn't start with '/', it will be added
   * automatically. <br>
   * e.g.: /resA or resA will give the same result &rarr; cdo://repositoryUUID/resA <br>
   * authority = repositoryUUID <br>
   * path = /resA
   *
   * @deprecated This method is subject to removal in a future release.
   */
  @Deprecated
  public static URI createResourceURI(String repositoryUUID, String path)
  {
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append(PROTOCOL_NAME);
    stringBuilder.append(":"); //$NON-NLS-1$

    if (repositoryUUID != null)
    {
      stringBuilder.append("//"); //$NON-NLS-1$
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

  /**
   * @deprecated This method is subject to removal in a future release.
   */
  @Deprecated
  public static URI createResourceURI(CDOSession session, String path)
  {
    return createResourceURI(session == null ? null : session.getRepositoryInfo().getUUID(), path);
  }
}
