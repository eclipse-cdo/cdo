/*
 * Copyright (c) 2024 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.lm.impl;

import org.eclipse.emf.cdo.lm.Baseline;
import org.eclipse.emf.cdo.lm.Stream;

/**
 * A mix-in interface for {@link Baseline baselines} to customize the sorting order within a {@link Stream stream}.
 *
 * @author Eike Stepper
 * @since 1.3
 * @see Stream#getContents()
 */
public interface ExtendedBaseline extends Baseline
{
  public int getSortPriority();
}
