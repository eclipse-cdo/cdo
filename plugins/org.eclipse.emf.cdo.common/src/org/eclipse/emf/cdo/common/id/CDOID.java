/***************************************************************************
 * Copyright (c) 2004 - 2008 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Simon McDuff - https://bugs.eclipse.org/226778
 **************************************************************************/
package org.eclipse.emf.cdo.common.id;

import java.io.Serializable;

/**
 * @author Eike Stepper
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface CDOID extends Serializable
{
  public static final CDOID NULL = org.eclipse.emf.cdo.internal.common.id.CDOIDNullImpl.INSTANCE;

  public Type getType();

  public boolean isNull();

  public boolean isObject();

  public boolean isLegacy();

  public boolean isMeta();

  public boolean isTemporary();
  
  /**
   * @since 2.0
   */
  public String getCDOIDString();
  /**
   * @author Eike Stepper
   */
  public enum Type
  {
    NULL, OBJECT, LEGACY_OBJECT, TEMP_OBJECT, META, TEMP_META
  }
}
