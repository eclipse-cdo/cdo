package org.eclipse.emf.cdo.internal.ui.wizards;

import org.eclipse.net4j.container.Container;
import org.eclipse.net4j.container.ContainerManager;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Text;

import java.util.Set;

public class NewSessionWizardPage extends WizardPage
{
  private Button existingConnectorButton;

  private Button newConnectorButton;

  private List connectorList;

  private Text connectorText;

  private Text repositoryText;

  private ISelection selection;

  public NewSessionWizardPage(ISelection selection)
  {
    super("wizardPage");
    setTitle("Multi-page Editor File");
    setDescription("This wizard creates a new file with *.mpe extension that can be opened by a multi-page editor.");
    this.selection = selection;
  }

  public void createControl(Composite parent)
  {
    Composite container = new Composite(parent, SWT.NULL);
    GridLayout layout = new GridLayout();
    container.setLayout(layout);
    layout.numColumns = 1;
    layout.verticalSpacing = 9;

    Group connectorGroup = new Group(container, SWT.None);
    connectorGroup.setText("Connector");
    connectorGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
    connectorGroup.setLayout(new GridLayout(2, false));

    existingConnectorButton = new Button(connectorGroup, SWT.RADIO);
    existingConnectorButton.setText("Existing connector:");
    existingConnectorButton.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false));
    existingConnectorButton.addSelectionListener(new SelectionAdapter()
    {
      public void widgetSelected(SelectionEvent e)
      {
        dialogChanged();
      }
    });

    connectorList = new List(connectorGroup, SWT.BORDER | SWT.SINGLE);
    connectorList.setSize(300, 100);
    connectorList.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

    newConnectorButton = new Button(connectorGroup, SWT.RADIO);
    newConnectorButton.setText("New connector:");
    newConnectorButton.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false));
    newConnectorButton.addSelectionListener(new SelectionAdapter()
    {
      public void widgetSelected(SelectionEvent e)
      {
        dialogChanged();
      }
    });

    connectorText = new Text(connectorGroup, SWT.BORDER);
    connectorText.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));

    Group repositoryGroup = new Group(container, SWT.None);
    repositoryGroup.setText("Repository");
    repositoryGroup.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
    repositoryGroup.setLayout(new GridLayout(2, false));

    Label label = new Label(repositoryGroup, SWT.NULL);
    label.setText("&Name:");

    repositoryText = new Text(repositoryGroup, SWT.BORDER);
    repositoryText.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));

    initialize();
    dialogChanged();
    setControl(container);
  }

  public void setVisible(boolean visible)
  {
    super.setVisible(visible);
    if (visible)
    {
      setFocus();
    }
  }

  private void setFocus()
  {
    if (existingConnectorButton.getSelection())
    {
      connectorList.setFocus();
    }
    else
    {
      connectorText.setFocus();
    }
  }

  private String[] getConnectorDescriptions()
  {
    Container container = ContainerManager.INSTANCE.getContainer();
    Set<String> keySet = container.getConnectorRegistry().keySet();
    return keySet.toArray(new String[keySet.size()]);
  }

  private void initialize()
  {
    String[] descriptions = getConnectorDescriptions();
    if (descriptions.length != 0)
    {
      existingConnectorButton.setSelection(true);
      connectorList.setItems(descriptions);
      connectorList.select(0);
    }
    else
    {
      existingConnectorButton.setEnabled(false);
      newConnectorButton.setSelection(true);
    }
  }

  private void dialogChanged()
  {
    connectorList.setEnabled(existingConnectorButton.getSelection());
    connectorText.setEnabled(newConnectorButton.getSelection());
    updateStatus(null);
  }

  private void updateStatus(String message)
  {
    setErrorMessage(message);
    setPageComplete(message == null);
  }

  public String getRepositoryName()
  {
    return repositoryText.getText();
  }

  public String getConnectorDescription()
  {
    if (existingConnectorButton.getSelection())
    {
      return connectorList.getSelection()[0];
    }

    return connectorText.getText();
  }
}