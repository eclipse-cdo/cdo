/*
 * Copyright (c) 2007, 2010-2012, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.internal.util.om.pref;

import org.eclipse.net4j.util.HexUtil;

/**
 * @author Eike Stepper
 */
public final class BytesPreference extends Preference<byte[]>
{
  public BytesPreference(Preferences preferences, String name, byte[] defaultValue)
  {
    super(preferences, name, defaultValue);
  }

  @Override
  protected String getString()
  {
    return HexUtil.bytesToHex(getValue());
  }

  @Override
  protected byte[] convert(String value)
  {
    return HexUtil.hexToBytes(value);
  }

  @Override
  public Type getType()
  {
    return Type.BYTES;
  }
}
