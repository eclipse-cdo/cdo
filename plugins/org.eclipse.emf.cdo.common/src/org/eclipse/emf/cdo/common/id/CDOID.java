/**
 * Copyright (c) 2004 - 2009 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Simon McDuff - http://bugs.eclipse.org/226778
 *    Simon McDuff - http://bugs.eclipse.org/213402
 */
package org.eclipse.emf.cdo.common.id;

import java.io.Serializable;

/**
 * @author Eike Stepper
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface CDOID extends Serializable, Comparable<CDOID>
{
  public static final CDOID NULL = org.eclipse.emf.cdo.internal.common.id.CDOIDNullImpl.INSTANCE;

  public Type getType();

  public boolean isNull();

  public boolean isObject();

  public boolean isMeta();

  public boolean isTemporary();

  /**
   * @since 2.0
   */
  public boolean isExternal();

  /**
   * @since 2.0
   */
  public String toURIFragment();

  /**
   * @author Eike Stepper
   */
  public enum Type
  {
    NULL, //
    OBJECT,

    /**
     * @since 2.0
     */
    EXTERNAL_OBJECT,

    /**
     * @since 2.0
     */
    EXTERNAL_TEMP_OBJECT, //
    TEMP_OBJECT, //
    META, //
    TEMP_META
  }
}
