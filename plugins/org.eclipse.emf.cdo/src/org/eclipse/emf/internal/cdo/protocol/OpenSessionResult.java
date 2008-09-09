/***************************************************************************
 * Copyright (c) 2004 - 2008 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.emf.internal.cdo.protocol;

import org.eclipse.emf.cdo.common.id.CDOIDLibraryDescriptor;
import org.eclipse.emf.cdo.common.id.CDOIDMetaRange;
import org.eclipse.emf.cdo.common.model.CDOPackageInfo;
import org.eclipse.emf.cdo.common.model.CDOPackageURICompressor;

import org.eclipse.net4j.util.io.ExtendedDataInput;
import org.eclipse.net4j.util.io.ExtendedDataOutput;
import org.eclipse.net4j.util.io.StringCompressor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Eike Stepper
 */
public final class OpenSessionResult implements CDOPackageURICompressor
{
  private int sessionID;

  private String repositoryUUID;

  private CDOIDLibraryDescriptor libraryDescriptor;

  private List<CDOPackageInfo> packageInfos = new ArrayList<CDOPackageInfo>();

  private StringCompressor compressor = new StringCompressor(true);

  public OpenSessionResult(int sessionID, String repositoryUUID, CDOIDLibraryDescriptor libraryDescriptor)
  {
    this.sessionID = sessionID;
    this.repositoryUUID = repositoryUUID;
    this.libraryDescriptor = libraryDescriptor;
  }

  public int getSessionID()
  {
    return sessionID;
  }

  public String getRepositoryUUID()
  {
    return repositoryUUID;
  }

  public CDOIDLibraryDescriptor getLibraryDescriptor()
  {
    return libraryDescriptor;
  }

  public List<CDOPackageInfo> getPackageInfos()
  {
    return packageInfos;
  }

  void addPackageInfo(String packageURI, boolean dynamic, CDOIDMetaRange metaIDRange, String parentURI)
  {
    packageInfos.add(new CDOPackageInfo(packageURI, dynamic, metaIDRange, parentURI));
  }

  public StringCompressor getCompressor()
  {
    return compressor;
  }

  /**
   * @since 2.0
   */
  public void writePackageURI(ExtendedDataOutput out, String uri) throws IOException
  {
    compressor.write(out, uri);
  }

  /**
   * @since 2.0
   */
  public String readPackageURI(ExtendedDataInput in) throws IOException
  {
    return compressor.read(in);
  }
}
