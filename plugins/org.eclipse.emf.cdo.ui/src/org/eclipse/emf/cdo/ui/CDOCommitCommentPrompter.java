/*
 * Copyright (c) 2021 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.ui;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.internal.ui.messages.Messages;
import org.eclipse.emf.cdo.transaction.CDOConflictResolver3;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.ui.shared.SharedIcons;

import org.eclipse.net4j.util.StringUtil;
import org.eclipse.net4j.util.ui.UIUtil;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import java.util.Set;

/**
 * @author Eike Stepper
 * @since 4.10
 */
public class CDOCommitCommentPrompter
{
  private final CDOConflictResolver3 impl = new CDOConflictResolver3()
  {
    @Override
    public CDOTransaction getTransaction()
    {
      return transaction;
    }

    @Override
    public void setTransaction(CDOTransaction tx)
    {
      transaction = tx;
      comment = StringUtil.EMPTY;
    }

    @Override
    public boolean preCommit()
    {
      CommentDialog dialog = createDialog(comment);

      int[] result = { Dialog.OK };
      UIUtil.syncExec(() -> result[0] = dialog.open());

      if (result[0] != Dialog.OK)
      {
        return false;
      }

      comment = dialog.getComment();
      transaction.setCommitComment(comment.length() == 0 ? null : comment.trim());
      return true;
    }

    @Override
    public void resolveConflicts(Set<CDOObject> conflicts)
    {
      // Do nothing.
    }
  };

  private CDOTransaction transaction;

  private String comment;

  public CDOCommitCommentPrompter(CDOTransaction transaction)
  {
    transaction.options().addConflictResolver(impl);
  }

  public final CDOTransaction getTransaction()
  {
    return transaction;
  }

  public void dispose()
  {
    transaction.options().removeConflictResolver(impl);
    transaction = null;
  }

  protected CommentDialog createDialog(String defaultComment)
  {
    return new CommentDialog(getParentShell(), defaultComment);
  }

  protected Shell getParentShell()
  {
    return UIUtil.getShell();
  }

  /**
   * @author Eike Stepper
   */
  public static class CommentDialog extends TitleAreaDialog
  {
    public static final String TITLE = Messages.getString("CDOCommitCommentPrompter.0"); //$NON-NLS-1$

    private String comment;

    public CommentDialog(Shell parentShell, String defaultComment)
    {
      super(parentShell);
      setShellStyle(getShellStyle() | SWT.APPLICATION_MODAL | SWT.MAX | SWT.TITLE | SWT.RESIZE);
      comment = defaultComment == null ? StringUtil.EMPTY : defaultComment;
    }

    public String getComment()
    {
      return comment;
    }

    @Override
    protected Control createDialogArea(Composite parent)
    {
      getShell().setText(TITLE);
      setTitle(TITLE);
      setTitleImage(SharedIcons.getImage(SharedIcons.WIZBAN_COMMIT));

      GridLayout layout = UIUtil.createGridLayout(1);
      layout.marginWidth = 10;
      layout.marginHeight = 10;

      Composite composite = new Composite((Composite)super.createDialogArea(parent), SWT.NONE);
      composite.setLayoutData(UIUtil.createGridData());
      composite.setLayout(layout);

      Text text = new Text(composite, SWT.MULTI | SWT.V_SCROLL | SWT.BORDER);
      text.setLayoutData(UIUtil.createGridData());
      text.setBackground(text.getDisplay().getSystemColor(SWT.COLOR_WHITE));
      text.setText(comment);
      text.selectAll();
      text.addModifyListener(e -> comment = text.getText());
      text.addTraverseListener(e -> {
        if (e.detail == SWT.TRAVERSE_RETURN && (e.stateMask & SWT.CTRL) != 0)
        {
          close();
          e.doit = true;
        }
      });

      return composite;
    }
  }
}
