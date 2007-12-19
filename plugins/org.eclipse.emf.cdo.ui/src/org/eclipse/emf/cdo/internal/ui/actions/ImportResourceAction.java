package org.eclipse.emf.cdo.internal.ui.actions;

import org.eclipse.emf.cdo.CDOTransaction;
import org.eclipse.emf.cdo.CDOView;

import org.eclipse.emf.common.ui.dialogs.ResourceDialog;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbenchPage;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Eike Stepper
 */
public class ImportResourceAction extends ViewAction
{
  public static final String ID = "import-resource";

  private static final String TITLE = "Import Resource";

  private URI sourceURI;

  private String targetPath;

  public ImportResourceAction(IWorkbenchPage page, CDOView view)
  {
    super(page, TITLE + INTERACTIVE, "Import a CDO resource", null, view);
    setId(ID);
  }

  @Override
  protected void preRun() throws Exception
  {
    ImportResourceDialog dialog = new ImportResourceDialog(getShell(), TITLE, SWT.OPEN);
    if (dialog.open() == ImportResourceDialog.OK)
    {
      List<URI> uris = dialog.getURIs();
      if (uris.size() == 1)
      {
        sourceURI = uris.get(0);
        targetPath = dialog.getTargetPath();
      }
      else
      {
        MessageDialog.openError(getShell(), TITLE, "A single URI must be entered!");
        cancel();
      }
    }
    else
    {
      cancel();
    }
  }

  @Override
  protected void doRun() throws Exception
  {
    CDOTransaction transaction = getTransaction();
    Resource source = transaction.getResourceSet().getResource(sourceURI, true);
    Resource target = transaction.createResource(targetPath);

    List<EObject> contents = new ArrayList<EObject>(source.getContents());
    for (EObject root : contents)
    {
      target.getContents().add(root);
    }
  }

  /**
   * @author Eike Stepper
   */
  public static class ImportResourceDialog extends ResourceDialog
  {
    private String targetPath = "/";

    private Text targetText;

    public ImportResourceDialog(Shell parent, String title, int style)
    {
      super(parent, title, style);
    }

    public String getTargetPath()
    {
      return targetPath;
    }

    public void setTargetPath(String targetPath)
    {
      this.targetPath = targetPath;
    }

    @Override
    protected Control createDialogArea(Composite parent)
    {
      Composite composite = (Composite)super.createDialogArea(parent);

      Label separatorLabel1 = new Label(composite, SWT.SEPARATOR | SWT.HORIZONTAL);
      {
        FormData data = new FormData();
        data.top = new FormAttachment(uriField, (int)(1.5 * CONTROL_OFFSET));
        data.left = new FormAttachment(0, -CONTROL_OFFSET);
        data.right = new FormAttachment(100, CONTROL_OFFSET);
        separatorLabel1.setLayoutData(data);
      }

      Label label = new Label(composite, SWT.NONE);
      label.setText("Target path:");
      {
        FormData data = new FormData();
        data.top = new FormAttachment(separatorLabel1, CONTROL_OFFSET);
        data.left = new FormAttachment(0, CONTROL_OFFSET);
        data.right = new FormAttachment(100, -CONTROL_OFFSET);
        label.setLayoutData(data);
      }

      targetText = new Text(composite, SWT.BORDER);
      {
        FormData data = new FormData();
        data.top = new FormAttachment(label, CONTROL_OFFSET);
        data.left = new FormAttachment(0, CONTROL_OFFSET);
        data.right = new FormAttachment(100, -CONTROL_OFFSET);
        targetText.setLayoutData(data);
      }

      Label separatorLabel2 = new Label(composite, SWT.SEPARATOR | SWT.HORIZONTAL);
      {
        FormData data = new FormData();
        data.top = new FormAttachment(targetText, (int)(1.5 * CONTROL_OFFSET));
        data.left = new FormAttachment(0, -CONTROL_OFFSET);
        data.right = new FormAttachment(100, CONTROL_OFFSET);
        separatorLabel2.setLayoutData(data);
      }

      return composite;

      // Composite composite = UIUtil.createGridComposite(parent, 1);
      // Control source = super.createDialogArea(composite);
      // source.setLayoutData(UIUtil.createGridData(true, false));
      //
      // Composite line = UIUtil.createGridComposite(composite, 2);
      // new Label(line, SWT.NONE).setText("Enter target path:");
      //
      // targetText = new Text(line, SWT.BORDER);
      // targetText.setLayoutData(UIUtil.createGridData(true, false));
      // targetText.setText(targetPath);
      // targetText.addModifyListener(new ModifyListener()
      // {
      // public void modifyText(ModifyEvent e)
      // {
      // targetPath = targetText.getText();
      // }
      // });
      //
      // return source;
    }
  }
}