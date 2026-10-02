/*
 * Copyright (c) 2010-2012, 2025 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.spi.common;

import org.eclipse.emf.cdo.common.protocol.CDODataInput;

import org.eclipse.net4j.util.om.monitor.OMMonitor;

import java.io.IOException;

/**
 * Replication target that accepts the repository's raw replication stream. A raw context is used when replication data
 * can be transferred in the source store's native representation instead of replaying individual branch, commit, and
 * lock callbacks.
 *
 * @author Eike Stepper
 * @since 3.0
 */
public interface CDORawReplicationContext extends CDOReplicationInfo
{
  /**
   * @since 4.0
   */
  public void replicateRaw(CDODataInput in, OMMonitor monitor) throws IOException;
}
