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
package org.eclipse.emf.cdo.ui.internal.compare;

import org.eclipse.emf.cdo.common.lob.CDOClob;
import org.eclipse.emf.cdo.eresource.CDOBinaryResource;
import org.eclipse.emf.cdo.eresource.CDOFileResource;
import org.eclipse.emf.cdo.eresource.CDOTextResource;
import org.eclipse.emf.cdo.ui.internal.compare.bundle.OM;

import org.eclipse.net4j.util.io.ReaderInputStream;

import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.compare.Match;
import org.eclipse.emf.compare.rcp.ui.mergeviewer.IMergeViewer.MergeViewerSide;

import org.eclipse.compare.IStreamContentAccessor;
import org.eclipse.core.runtime.CoreException;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author Eike Stepper
 */
@SuppressWarnings("restriction")
public class CDOMatchAccessor //
    extends org.eclipse.emf.compare.rcp.ui.internal.contentmergeviewer.accessor.impl.MatchAccessor //
    implements IStreamContentAccessor, org.eclipse.emf.compare.rcp.ui.contentmergeviewer.accessor.legacy.IStreamContentAccessor
{
  private final CDOFileResource<?> file;

  public CDOMatchAccessor(AdapterFactory adapterFactory, Match match, CDOFileResource<?> file, MergeViewerSide side)
  {
    super(adapterFactory, match, side);
    this.file = file;
  }

  @Override
  public InputStream getContents() throws CoreException
  {
    try
    {
      if (file instanceof CDOTextResource)
      {
        CDOTextResource text = (CDOTextResource)file;
        CDOClob clob = text.getContents();

        return new ReaderInputStream(clob.getContents(), text.getEncoding());
      }

      CDOBinaryResource binary = (CDOBinaryResource)file;
      return binary.getContents().getContents();
    }
    catch (IOException ex)
    {
      OM.BUNDLE.coreException(ex);
      return null; // Can't happen.
    }
  }
}
