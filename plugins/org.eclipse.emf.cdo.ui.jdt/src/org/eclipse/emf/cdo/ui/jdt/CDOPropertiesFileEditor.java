/*
 * Copyright (c) 2020 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.ui.jdt;

import org.eclipse.emf.cdo.internal.ui.editor.CDOLobEditorInput;
import org.eclipse.emf.cdo.internal.ui.editor.CDOLobStorage;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.internal.ui.propertiesfileeditor.IPropertiesFilePartitions;
import org.eclipse.jdt.internal.ui.propertiesfileeditor.PropertiesFileDocumentSetupParticipant;
import org.eclipse.jdt.internal.ui.propertiesfileeditor.PropertiesFileEditor;
import org.eclipse.jdt.internal.ui.propertiesfileeditor.PropertiesFileEditorActionContributor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.editors.text.ForwardingDocumentProvider;

/**
 * @author Eike Stepper
 */
@SuppressWarnings("restriction")
public class CDOPropertiesFileEditor extends PropertiesFileEditor
{
  public static final String ID = "org.eclipse.emf.cdo.ui.jdt.CDOPropertiesFileEditor";

  public CDOPropertiesFileEditor()
  {
  }

  @Override
  protected void doSetInput(IEditorInput input) throws CoreException
  {
    if (input instanceof CDOLobEditorInput)
    {
      setDocumentProvider(new ForwardingDocumentProvider(IPropertiesFilePartitions.PROPERTIES_FILE_PARTITIONING, new PropertiesFileDocumentSetupParticipant(),
          CDOLobStorage.getInstance()));
    }

    super.doSetInput(input);
  }

  /**
   * @author Eike Stepper
   */
  public static final class ActionContributor extends PropertiesFileEditorActionContributor
  {
    public ActionContributor()
    {
    }
  }
}
