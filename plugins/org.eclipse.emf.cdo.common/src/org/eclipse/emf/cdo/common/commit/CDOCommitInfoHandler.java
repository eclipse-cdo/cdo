/*
 * Copyright (c) 2010-2012, 2015, 2019, 2021 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.common.commit;

/**
 * A call-back interface that indicates the ability to <i>handle</i> {@link CDOCommitInfo commit infos} that are passed
 * from other entities.
 *
 * @author Eike Stepper
 * @since 3.0
 */
@FunctionalInterface
public interface CDOCommitInfoHandler
{
  public void handleCommitInfo(CDOCommitInfo commitInfo);
}
