/*
 * Copyright (c) 2010-2012, 2015, 2016 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Martin Fluegge - initial API and implementation
 */
package org.eclipse.emf.cdo.dawn.ui.wizards;

import org.eclipse.emf.cdo.dawn.preferences.PreferenceConstants;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.emf.common.util.URI;

/**
 * @author Martin Fluegge
 */
public class DawnCreateNewDiagramResourceWizardPage extends DawnCreateNewResourceWizardPage
{
  public DawnCreateNewDiagramResourceWizardPage(String fileExtension)
  {
    super(fileExtension);
  }

  public DawnCreateNewDiagramResourceWizardPage(String fileExtension, boolean showResources, CDOView view)
  {
    super(fileExtension, showResources, view);
  }

  @Override
  public URI getURI()
  {
    // TODO check why the dawn resource must be connected this way
    return URI
        .createURI("dawn://" + PreferenceConstants.getRepositoryName() + "/" + chooserComposite.getResourcePath() + "/" + chooserComposite.getResourceName());
  }
}
