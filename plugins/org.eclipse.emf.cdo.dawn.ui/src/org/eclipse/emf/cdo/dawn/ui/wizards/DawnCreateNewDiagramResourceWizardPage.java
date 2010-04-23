/*******************************************************************************
 * Copyright (c) 2010 Martin Fluegge (Berlin, Germany).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Martin Fluegge - initial API and implementation
 ******************************************************************************/
package org.eclipse.emf.cdo.dawn.ui.wizards;

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

  public URI getURI()
  {
    return URI.createURI("dawn://repo1/" + resourcePathText.getText() + "/" + resourceText.getText());
  }
}
