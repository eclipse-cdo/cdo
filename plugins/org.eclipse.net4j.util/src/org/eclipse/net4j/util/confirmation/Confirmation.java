/*
 * Copyright (c) 2013, 2015 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Christian W. Damus (CEA LIST) - initial API and implementation
 */
package org.eclipse.net4j.util.confirmation;

/**
 * An enumeration of possible answers to a {@linkplain IConfirmationProvider request for confirmation}.
 *
 * @author Christian W. Damus (CEA LIST)
 * @since 3.4
 *
 * @see IConfirmationProvider
 */
public enum Confirmation
{
  // Order negative answers after positive answers
  OK, CANCEL, YES, NO;
}
