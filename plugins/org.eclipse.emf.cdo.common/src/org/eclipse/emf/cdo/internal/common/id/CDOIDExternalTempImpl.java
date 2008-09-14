/***************************************************************************
 * Copyright (c) 2004 - 2008 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Simon McDuff - initial API and implementation
 **************************************************************************/
package org.eclipse.emf.cdo.internal.common.id;

import org.eclipse.emf.cdo.common.id.CDOIDTemp;

/**
 * @author Simon McDuff
 */
public class CDOIDExternalTempImpl extends CDOIDExternalImpl implements CDOIDTemp
{
  private static final long serialVersionUID = 1L;

  public CDOIDExternalTempImpl(String uri)
  {
    super(uri);
  }

  @Override
  public Type getType()
  {
    return Type.EXTERNAL_TEMP_OBJECT;
  }
}
