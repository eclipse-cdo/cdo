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
package org.eclipse.emf.cdo.releng.version.digest;

import java.util.HashMap;

/**
 * @author Eike Stepper
 */
public class ReleaseDigest extends HashMap<String, byte[]>
{
  private static final long serialVersionUID = 1L;

  private String tag;

  public ReleaseDigest(String tag)
  {
    this.tag = tag;
  }

  public String getTag()
  {
    return tag;
  }

  public byte[] getProjectDigest(String projectName)
  {
    return get(projectName);
  }
}
