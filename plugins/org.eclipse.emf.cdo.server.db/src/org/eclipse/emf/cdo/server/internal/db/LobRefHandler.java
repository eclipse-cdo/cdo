/*
 * Copyright (c) 2025 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.server.internal.db;

import org.eclipse.emf.cdo.common.lob.CDOLobInfo;

import org.eclipse.net4j.util.HexUtil;

/**
 * Handles a LOB reference consisting of an ID and a size.
 *
 * @author Eike Stepper
 */
@FunctionalInterface
public interface LobRefHandler<R>
{
  public static final char SEPARATOR = '-';

  public R handleLobRef(byte[] id, long size);

  public static String format(CDOLobInfo lobInfo)
  {
    byte[] id = lobInfo.getID();
    long size = lobInfo.getSize();
    return HexUtil.bytesToHex(id) + SEPARATOR + size;
  }

  public static <T> T parse(String lobRef, LobRefHandler<T> handler)
  {
    int pos = lobRef.indexOf(SEPARATOR);
    byte[] id = HexUtil.hexToBytes(lobRef.substring(0, pos));
    long size = Long.parseLong(lobRef.substring(pos + 1));
    return handler.handleLobRef(id, size);
  }
}
