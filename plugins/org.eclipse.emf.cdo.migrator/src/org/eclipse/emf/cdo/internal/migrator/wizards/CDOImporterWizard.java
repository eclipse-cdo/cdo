/*
 * Copyright (c) 2008, 2009, 2011, 2012, 2015, 2016, 2023 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.internal.migrator.wizards;

import org.eclipse.emf.cdo.internal.migrator.CDOImporter;
import org.eclipse.emf.cdo.internal.migrator.messages.Messages;

import org.eclipse.emf.converter.ModelConverter;
import org.eclipse.emf.importer.ui.contribution.base.ModelImporterDetailPage;
import org.eclipse.emf.importer.ui.contribution.base.ModelImporterPackagePage;
import org.eclipse.emf.importer.ui.contribution.base.ModelImporterWizard;

/**
 * @author Eike Stepper
 */
public class CDOImporterWizard extends ModelImporterWizard
{
  public CDOImporterWizard()
  {
  }

  @Override
  protected ModelConverter createModelConverter()
  {
    return new CDOImporter();
  }

  @Override
  public void addPages()
  {
    ModelImporterDetailPage detailPage = new ModelImporterDetailPage(getModelImporter(), Messages.getString("CDOImporterWizard_0")); //$NON-NLS-1$
    detailPage.setTitle(Messages.getString("CDOImporterWizard_1")); //$NON-NLS-1$
    detailPage.setDescription(detailPage.showGenModel() ? Messages.getString("CDOImporterWizard_2") //$NON-NLS-1$
        : Messages.getString("CDOImporterWizard_3")); //$NON-NLS-1$
    addPage(detailPage);

    ModelImporterPackagePage packagePage = new ModelImporterPackagePage(getModelImporter(), Messages.getString("CDOImporterWizard_4")); //$NON-NLS-1$
    packagePage.setShowReferencedGenModels(true);
    addPage(packagePage);
  }
}
