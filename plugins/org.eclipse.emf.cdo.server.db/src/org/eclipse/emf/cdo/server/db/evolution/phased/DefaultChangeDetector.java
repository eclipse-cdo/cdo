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
package org.eclipse.emf.cdo.server.db.evolution.phased;

import org.eclipse.emf.cdo.server.db.evolution.phased.Context.Model;
import org.eclipse.emf.cdo.server.db.evolution.phased.PhasedModelEvolutionSupport.Mode;

import java.util.Collection;

/**
 * Detects model changes between the stored models and the currently registered EPackages.
 * <p>
 * If no model changes are detected, the model evolution process is aborted.
 * If model changes are detected and the mode is set to {@link Mode#Prevent},
 * an exception is thrown.
 *
 * @author Eike Stepper
 * @since 4.14
 * @noreference This package is currently considered <i>provisional</i>.
 * @noimplement This package is currently considered <i>provisional</i>.
 * @noextend This package is currently considered <i>provisional</i>.
 */
public class DefaultChangeDetector extends BasicPhaseHandler
{
  /**
   * The factory type of the default change detector.
   */
  public static final String FACTORY_TYPE = "default-change-detector"; //$NON-NLS-1$

  /**
   * Creates a change detector.
   */
  public DefaultChangeDetector()
  {
  }

  /**
   * Detects model changes and registers the changed models in the context.
   */
  @Override
  public void execute(Context context) throws Exception
  {
    Collection<Model> models = context.getModels().values();
    models.forEach(model -> {
      Object changeInfo = getChangeInfo(model);
      if (changeInfo != null)
      {
        context.addChangeInfo(model, changeInfo);
      }
    });
  }

  protected Object getChangeInfo(Model model)
  {
    String oldXMI = model.getOldXMI();
    String newXMI = model.getNewXMI();

    if (newXMI != null && !oldXMI.equals(newXMI))
    {
      return Boolean.TRUE;
    }

    return null;
  }
}