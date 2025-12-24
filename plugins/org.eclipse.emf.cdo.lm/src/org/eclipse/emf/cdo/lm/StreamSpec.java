/*
 * Copyright (c) 2022, 2024 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.lm;

import org.eclipse.net4j.util.StringUtil;

/**
 * Encapsulates the {@link #getMajorVersion() major version}, the {@link #getMinorVersion() minor version},
 * and the {@link #getCodeName() code name} of a {@link Stream stream}.
 *
 * @author Eike Stepper
 * @noextend This class is not intended to be subclassed by clients.
 * @see Stream#getMajorVersion()
 * @see Stream#getMinorVersion()
 * @see Stream#getCodeName()
 */
public class StreamSpec
{
  protected int majorVersion;

  protected int minorVersion;

  protected String codeName;

  public StreamSpec(int majorVersion, int minorVersion, String codeName)
  {
    this.majorVersion = majorVersion;
    this.minorVersion = minorVersion;
    this.codeName = codeName;
  }

  public StreamSpec(int majorVersion, int minorVersion)
  {
    this(majorVersion, minorVersion, null);
  }

  public int getMajorVersion()
  {
    return majorVersion;
  }

  public int getMinorVersion()
  {
    return minorVersion;
  }

  public String getCodeName()
  {
    return codeName;
  }

  public Stream createStream()
  {
    return LMFactory.eINSTANCE.createStream(majorVersion, minorVersion, codeName);
  }

  @Override
  public String toString()
  {
    String label = majorVersion + "." + minorVersion;

    if (!StringUtil.isEmpty(codeName))
    {
      label += " " + codeName;
    }

    return label;
  }
}
