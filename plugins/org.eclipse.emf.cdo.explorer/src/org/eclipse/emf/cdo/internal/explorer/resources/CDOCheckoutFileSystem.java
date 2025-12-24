/*
 * Copyright (c) 2021 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.internal.explorer.resources;

import org.eclipse.emf.cdo.explorer.CDOExplorerUtil;
import org.eclipse.emf.cdo.explorer.checkouts.CDOCheckout;
import org.eclipse.emf.cdo.internal.explorer.bundle.OM;
import org.eclipse.emf.cdo.util.CDOURIUtil;

import org.eclipse.net4j.util.WrappedException;
import org.eclipse.net4j.util.om.OMPlatform;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.filesystem.provider.FileSystem;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

/**
 * @author Eike Stepper
 */
public class CDOCheckoutFileSystem extends FileSystem
{
  public static final String SCHEME = CDOExplorerUtil.URI_SCHEME;

  public static final String PARAM_TIME_STAMPS = "timeStamps";

  public static final String TIME_STAMPS_SHALLOW = "shallow";

  public static final String TIME_STAMPS_DEEP = "deep";

  private static final boolean OMIT_CHECKOUT_FILE_SYSTEM = OMPlatform.INSTANCE.isProperty("org.eclipse.emf.cdo.explorer.omitCheckoutFileSystem");

  public CDOCheckoutFileSystem()
  {
  }

  @Override
  public IFileStore getStore(URI uri)
  {
    if (!OMIT_CHECKOUT_FILE_SYSTEM)
    {
      try
      {
        String scheme = uri.getScheme();
        if (SCHEME.equals(scheme))
        {
          String authority = uri.getAuthority();

          CDOCheckout checkout = CDOExplorerUtil.getCheckout(authority);
          if (checkout != null)
          {
            String path = uri.getPath();
            boolean shallowTimeStamp = isShallowTimeStamps(uri);

            return createFileStore(checkout, path, shallowTimeStamp);
          }
        }
      }
      catch (Exception ex)
      {
        OM.LOG.error(ex);
      }
    }

    return EFS.getNullFileSystem().getStore(uri);
  }

  protected CDOCheckoutFileStore createFileStore(CDOCheckout checkout, String path, boolean shallowTimeStamp)
  {
    return new CDOCheckoutFileStore(checkout, path, shallowTimeStamp);
  }

  private static boolean isShallowTimeStamps(URI uri)
  {
    Map<String, String> parameters = CDOURIUtil.getParameters(uri.getQuery());
    String parameter = parameters.get(PARAM_TIME_STAMPS);
    return TIME_STAMPS_SHALLOW.equalsIgnoreCase(parameter);
  }

  public static URI createURI(CDOCheckout checkout, String path, boolean shallowTimeStamp)
  {
    try
    {
      String authority = checkout.getID();
      String query = shallowTimeStamp ? PARAM_TIME_STAMPS + "=" + TIME_STAMPS_SHALLOW : null;

      return new URI(SCHEME, authority, path, query, null);
    }
    catch (URISyntaxException ex)
    {
      // Can't realistically happen.
      throw WrappedException.wrap(ex);
    }
  }
}
