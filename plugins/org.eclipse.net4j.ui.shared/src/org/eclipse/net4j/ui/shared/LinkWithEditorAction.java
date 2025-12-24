/*
 * Copyright (c) 2022 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.ui.shared;

import org.eclipse.net4j.ui.internal.shared.messages.Messages;

import org.eclipse.jface.action.Action;

/**
 * @author Eike Stepper
 * @since 4.7
 */
public abstract class LinkWithEditorAction extends Action
{
  public LinkWithEditorAction()
  {
    super(Messages.getString("LinkWithEditorAction_name"), AS_CHECK_BOX);//$NON-NLS-1$
    setToolTipText(Messages.getString("LinkWithEditorAction_tooltip")); //$NON-NLS-1$
    setImageDescriptor(SharedIcons.getDescriptor(SharedIcons.ETOOL_LINK_WITH_EDITOR));
  }

  @Override
  public final void run()
  {
    linkWithEditorChanged(isChecked());
  }

  protected abstract void linkWithEditorChanged(boolean linkWithEditor);
}
