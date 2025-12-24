/*
 * Copyright (c) 2010-2012, 2014 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Simon McDuff - bug 201266
 *    Simon McDuff - bug 233314
 *    Simon McDuff - bug 247143
 */
package org.eclipse.emf.cdo.transaction;

/**
 * A marker interface for use with {@link CDOTransaction#addTransactionHandler(CDOTransactionHandlerBase)
 * CDOTransaction.addTransactionHandler()} and
 * {@link CDOTransaction#removeTransactionHandler(CDOTransactionHandlerBase) CDOTransaction.removeTransactionHandler()}.
 *
 * @author Eike Stepper
 * @since 4.0
 */
public interface CDOTransactionHandlerBase
{
}
