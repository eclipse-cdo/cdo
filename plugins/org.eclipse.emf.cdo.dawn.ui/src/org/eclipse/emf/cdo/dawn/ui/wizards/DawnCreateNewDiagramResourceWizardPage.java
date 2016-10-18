/*
 * Copyright (c) 2010-2012, 2015, 2016 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
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
