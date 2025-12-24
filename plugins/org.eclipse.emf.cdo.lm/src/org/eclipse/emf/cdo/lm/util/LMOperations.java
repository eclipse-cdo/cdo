/*
 * Copyright (c) 2025 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.lm.util;

/**
 * @author Eike Stepper
 * @since 1.4
 */
public interface LMOperations
{
  public String CREATE_MODULE = "org.eclipse.emf.cdo.lm.ui.CreateModule".intern();

  public String DELETE_MODULE = "org.eclipse.emf.cdo.lm.ui.DeleteModule".intern();

  public String CREATE_STREAM = "org.eclipse.emf.cdo.lm.ui.CreateStream".intern();

  public String CREATE_CHANGE = "org.eclipse.emf.cdo.lm.ui.CreateChange".intern();

  public String RENAME_CHANGE = "org.eclipse.emf.cdo.lm.ui.RenameChange".intern();

  public String DELETE_CHANGE = "org.eclipse.emf.cdo.lm.ui.DeleteChange".intern();

  public String CREATE_DELIVERY = "org.eclipse.emf.cdo.lm.ui.CreateDelivery".intern();

  public String CREATE_DROP = "org.eclipse.emf.cdo.lm.ui.CreateDrop".intern();

  public String ATTACH_FINGERPRINT = "org.eclipse.emf.cdo.lm.ui.AttachFingerprint".intern();
}
