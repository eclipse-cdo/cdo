package org.eclipse.emf.cdo.internal.ui.actions;

import org.eclipse.emf.cdo.internal.ui.editor.CDOEditor;

import org.eclipse.emf.internal.cdo.CDOStateMachine;
import org.eclipse.emf.internal.cdo.InternalCDOObject;

import org.eclipse.jface.viewers.IStructuredSelection;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author Eike Stepper
 */
public class ReloadObjectsAction extends EditingDomainAction
{
  public static final String ID = "reload-objects";

  private static final String TITLE = "Reload";

  private CDOEditor editor;

  private List<InternalCDOObject> objects = new ArrayList<InternalCDOObject>();

  public ReloadObjectsAction()
  {
    super(TITLE);
    setId(ID);
  }

  public CDOEditor getEditor()
  {
    return editor;
  }

  public void setEditor(CDOEditor editor)
  {
    this.editor = editor;
  }

  public void selectionChanged(IStructuredSelection selection)
  {
    objects.clear();
    if (selection != null)
    {
      for (Iterator<Object> it = selection.iterator(); it.hasNext();)
      {
        Object object = it.next();
        if (object instanceof InternalCDOObject)
        {
          objects.add((InternalCDOObject)object);
        }
      }
    }

    update();
  }

  @Override
  public void update()
  {
    setEnabled(!objects.isEmpty());
  }

  @Override
  protected void doRun() throws Exception
  {
    if (!objects.isEmpty())
    {
      InternalCDOObject[] array = objects.toArray(new InternalCDOObject[objects.size()]);
      CDOStateMachine.INSTANCE.reload(array);
      if (editor != null)
      {
        editor.refreshViewer(null);
      }
    }
  }
}