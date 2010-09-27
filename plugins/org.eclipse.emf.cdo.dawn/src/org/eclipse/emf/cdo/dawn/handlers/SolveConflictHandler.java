package org.eclipse.emf.cdo.dawn.handlers;

import org.eclipse.emf.cdo.dawn.editors.IDawnEditor;
import org.eclipse.emf.cdo.dawn.helper.DawnEditorHelper;
import org.eclipse.emf.cdo.internal.dawn.bundle.OM;

import org.eclipse.net4j.util.om.trace.ContextTracer;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.IEditorPart;

public class SolveConflictHandler extends AbstractHandler
{
  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG, SolveConflictHandler.class);

  public Object execute(ExecutionEvent event) throws ExecutionException
  {
    IEditorPart activeEditor = DawnEditorHelper.getActiveEditor();

    if (TRACER.isEnabled())
    {
      TRACER.format("Start solving conflicts for {0}", activeEditor); //$NON-NLS-1$
    }

    if (activeEditor instanceof IDawnEditor)
    {
      MessageDialog dialog = new MessageDialog(DawnEditorHelper.getActiveShell(), "Conflict", null,
          "There are conflicts in your diagram. Would you like to rollback your current transaction?",
          MessageDialog.QUESTION, new String[] { "yes", "no", "Cancel" }, 1);

      switch (dialog.open())
      {
      case 0: // yes
        ((IDawnEditor)activeEditor).getDawnEditorSupport().rollback();
        break;
      case 1: // no
        break;
      default: // cancel
        break;
      }
    }
    return null;
  }
}
