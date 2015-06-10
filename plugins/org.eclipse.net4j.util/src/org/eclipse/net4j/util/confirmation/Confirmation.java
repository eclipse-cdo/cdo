/*
 * Copyright (c) 2013, 2015 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
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
