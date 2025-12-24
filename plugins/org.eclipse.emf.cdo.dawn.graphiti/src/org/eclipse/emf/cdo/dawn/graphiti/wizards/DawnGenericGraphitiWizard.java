/*
 * Copyright (c) 2011, 2012, 2015 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Martin Fluegge - initial API and implementation
 */
package org.eclipse.emf.cdo.dawn.graphiti.wizards;

import org.eclipse.emf.cdo.dawn.ui.composites.CDOResourceNodeChooserComposite.ResourceChooserValidator;
import org.eclipse.emf.cdo.dawn.ui.wizards.DawnCreateNewDiagramResourceWizardPage;
import org.eclipse.emf.cdo.dawn.ui.wizards.DawnCreateNewResourceWizardPage;

import org.eclipse.emf.common.util.URI;

/**
 * @author Martin Fluegge
 */
public class DawnGenericGraphitiWizard extends DawnBasicGraphitiWizard
{
  private DawnGraphitiyDiagramTypeSelectionWizardPage dawnSelectModelPage;

  public DawnGenericGraphitiWizard()
  {
    super("", "graphiti");
  }

  @Override
  public void addPages()
  {
    dawnSelectModelPage = new DawnGraphitiyDiagramTypeSelectionWizardPage("title");
    addPage(dawnSelectModelPage);

    dawnDiagramModelFilePage = new DawnCreateNewDiagramResourceWizardPage(diagramExtension, false, view)
    {
      @Override
      public void setVisible(boolean visible)
      {
        if (visible)
        {
          URI uri = dawnDiagramModelFilePage.getURI();
          String fileName = uri.lastSegment();
          fileName = fileName.substring(0, fileName.length() - ("." + diagramExtension).length()); //$NON-NLS-1$
          fileName += "." + diagramExtension;
          dawnDomainModelFilePage.setResourceNamePrefix(fileName);
          dawnDomainModelFilePage.setResourcePath(dawnDiagramModelFilePage.getResourcePath());
        }
        super.setVisible(visible);
      }
    };

    dawnDiagramModelFilePage.setTitle("");
    dawnDiagramModelFilePage.setDescription("");
    dawnDiagramModelFilePage.setCreateAutomaticResourceName(true);
    addPage(dawnDiagramModelFilePage);

    dawnDomainModelFilePage = new DawnCreateNewResourceWizardPage("", true, view)
    {
      @Override
      public void setVisible(boolean visible)
      {
        if (visible)
        {
          String extension = dawnSelectModelPage.getText();
          URI uri = dawnDiagramModelFilePage.getURI();
          String fileName = uri.lastSegment();
          fileName = fileName.substring(0, fileName.length() - ("." + diagramExtension).length()); //$NON-NLS-1$
          fileName += "." + extension;
          dawnDomainModelFilePage.setResourceNamePrefix(fileName);
          dawnDomainModelFilePage.setResourcePath(dawnDiagramModelFilePage.getResourcePath());
        }
        super.setVisible(visible);
      }
    };
    dawnDomainModelFilePage.setTitle("");
    dawnDomainModelFilePage.setDescription("");

    // allows to connect to an existing resource
    dawnDomainModelFilePage.setResourceValidationType(ResourceChooserValidator.VALIDATION_WARN);
    addPage(dawnDomainModelFilePage);
  }

  @Override
  protected String geTypeId()
  {
    return dawnSelectModelPage.getText();
  }
}
