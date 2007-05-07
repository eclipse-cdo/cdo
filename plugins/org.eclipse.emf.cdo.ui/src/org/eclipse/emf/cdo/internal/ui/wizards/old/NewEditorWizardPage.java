package org.eclipse.emf.cdo.internal.ui.wizards.old;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.CDOSession;
import org.eclipse.emf.cdo.internal.ui.bundle.SharedIcons;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EPackage.Registry;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class NewEditorWizardPage extends WizardPage
{
  private CDOSession session;

  private Label sessionLabel;

  private Button loadResourceButton;

  private LoadResourceComposite loadComposite;

  private Button createResourceButton;

  private CreateResourceComposite createComposite;

  public NewEditorWizardPage()
  {
    super("wizardPage");
    setTitle("Multi-page Editor File");
    setDescription("This wizard creates a new file with *.mpe extension that can be opened by a multi-page editor.");
  }

  public CDOSession getSession()
  {
    return session;
  }

  public void setSession(CDOSession session)
  {
    this.session = session;
    setSessionLabel();
  }

  public void createControl(Composite parent)
  {
    Composite container = new Composite(parent, SWT.NULL);
    GridLayout layout = new GridLayout();
    container.setLayout(layout);
    layout.numColumns = 2;
    layout.verticalSpacing = 9;

    Label label = new Label(container, SWT.NULL);
    label.setText("Session:");

    sessionLabel = new Label(container, SWT.NULL);
    setSessionLabel();

    loadResourceButton = createRadioButton(container, "Load resource:");
    loadComposite = new LoadResourceComposite(container);

    createResourceButton = createRadioButton(container, "Create resource:");
    createComposite = new CreateResourceComposite(container);
    createComposite.getViewer().setInput(EPackage.Registry.INSTANCE);

    initialize();
    dialogChanged();
    setControl(container);
  }

  private Button createRadioButton(Composite container, String text)
  {
    Button button = new Button(container, SWT.RADIO);
    button.setText(text);
    button.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false));
    button.addSelectionListener(new SelectionAdapter()
    {
      public void widgetSelected(SelectionEvent e)
      {
        dialogChanged();
      }
    });

    return button;
  }

  private void setSessionLabel()
  {
    if (session != null && sessionLabel != null)
    {
      String repositoryName = session.getRepository().getName();
      String connectorDescription = session.getChannel().getConnector().getDescription();
      sessionLabel.setText(repositoryName + "@" + connectorDescription);
    }
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
    if (loadResourceButton.getSelection())
    {
      loadComposite.getText().setFocus();
    }
    else
    {
      createComposite.getText().setFocus();
    }
  }

  private void initialize()
  {
    if (false)
    {
      loadResourceButton.setSelection(true);
      // resourceList.setItems(descriptions);
      // resourceList.select(0);
    }
    else
    {
      createResourceButton.setSelection(true);
    }
  }

  private void dialogChanged()
  {
    loadComposite.setEnabled(loadResourceButton.getSelection());
    createComposite.setEnabled(createResourceButton.getSelection());

    if (loadComposite.isEnabled() && loadComposite.getViewer().getSelection().isEmpty())
    {
      updateStatus("Select an existing resource to be loaded from the list.");
      return;
    }

    if (createComposite.isEnabled())
    {
      if (createComposite.getText().getText().isEmpty())
      {
        updateStatus("Select an existing resource to be loaded from the list.");
        return;
      }

      if (createComposite.getViewer().getSelection().isEmpty())
      {
        updateStatus("Select an existing resource to be loaded from the list.");
        return;
      }
    }

    // if (connectorText.isEnabled() && connectorText.getText().length() == 0)
    // {
    // updateStatus("Enter a description to create a new connector.");
    // return;
    // }
    //
    // if (repositoryText.getText().length() == 0)
    // {
    // updateStatus("Enter the name of a remote repository.");
    // return;
    // }

    updateStatus(null);
  }

  private void updateStatus(String message)
  {
    setErrorMessage(message);
    setPageComplete(message == null);
  }

  /**
   * @author Eike Stepper
   */
  public static class TextViewerComposite extends Composite implements ITreeContentProvider
  {
    private Text text;

    private StructuredViewer viewer;

    private LabelProvider labelProvider = new LabelProvider()
    {
      public Image getImage(Object element)
      {
        return TextViewerComposite.this.getImage(element);
      }

      public String getText(Object element)
      {
        return TextViewerComposite.this.getText(element);
      }
    };

    public TextViewerComposite(Composite parent)
    {
      super(parent, SWT.NONE);
      setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
      GridLayout gridLayout = new GridLayout(1, false);
      gridLayout.marginWidth = 0;
      gridLayout.marginHeight = 0;
      gridLayout.verticalSpacing = 5;
      setLayout(gridLayout);

      text = createText();
      viewer = createViewer();
      init();
    }

    public Text getText()
    {
      return text;
    }

    public StructuredViewer getViewer()
    {
      return viewer;
    }

    @Override
    public void setEnabled(boolean enabled)
    {
      super.setEnabled(enabled);
      text.setEnabled(enabled);
      viewer.getControl().setEnabled(enabled);
    }

    protected Text createText()
    {
      Text text = new Text(this, SWT.BORDER);
      text.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
      return text;
    }

    protected StructuredViewer createViewer()
    {
      Table table = new Table(this, SWT.BORDER | SWT.SINGLE);
      table.setSize(300, 100);
      table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
      return new TableViewer(table);
    }

    protected void init()
    {
      viewer.setContentProvider(this);
      viewer.setLabelProvider(labelProvider);
    }

    public Object[] getChildren(Object parentElement)
    {
      return new Object[0];
    }

    public Object getParent(Object element)
    {
      return null;
    }

    public boolean hasChildren(Object element)
    {
      return false;
    }

    public Object[] getElements(Object inputElement)
    {
      return getChildren(inputElement);
    }

    public void inputChanged(Viewer viewer, Object oldInput, Object newInput)
    {
    }

    public Image getImage(Object element)
    {
      return null;
    }

    public String getText(Object element)
    {
      return element.toString();
    }
  }

  public static class LoadResourceComposite extends TextViewerComposite
  {
    public LoadResourceComposite(Composite parent)
    {
      super(parent);
    }
  }

  public static class CreateResourceComposite extends TextViewerComposite
  {
    private EPackage.Registry registry;

    private Object[] uris;

    public CreateResourceComposite(Composite parent)
    {
      super(parent);
    }

    protected StructuredViewer createViewer()
    {
      Tree tree = new Tree(this, SWT.BORDER | SWT.SINGLE);
      tree.setSize(300, 100);
      tree.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
      return new TreeViewer(tree);
    }

    @Override
    public void inputChanged(Viewer viewer, Object oldInput, Object newInput)
    {
      registry = (Registry)newInput;
      Set<String> keySet = registry.keySet();
      Iterator<String> it = keySet.iterator();
      uris = new Object[keySet.size()];
      for (int i = 0; i < uris.length; i++)
      {
        uris[i] = URI.createURI(it.next());
      }
    }

    @Override
    public boolean hasChildren(Object element)
    {
      return element instanceof URI;
    }

    @Override
    public Object[] getChildren(Object parentElement)
    {
      if (parentElement == registry)
      {
        return uris;
      }

      if (parentElement instanceof URI)
      {
        URI uri = (URI)parentElement;
        EPackage ePackage = registry.getEPackage(uri.toString());
        List result = new ArrayList();
        EList<EClassifier> classifiers = ePackage.getEClassifiers();
        for (EClassifier classifier : classifiers)
        {
          if (classifier instanceof EClass)
          {
            EClass eClass = (EClass)classifier;
            Class<?> instanceClass = eClass.getInstanceClass();
            if (CDOObject.class.isAssignableFrom(instanceClass))
            {
              result.add(eClass);
            }
          }
        }

        return result.toArray(new Object[result.size()]);
      }

      return super.getChildren(parentElement);
    }

    @Override
    public Object getParent(Object element)
    {
      if (element instanceof EClass)
      {
        EPackage ePackage = ((EClass)element).getEPackage();
        return URI.createURI(ePackage.getNsURI());
      }

      return registry;
    }

    @Override
    public String getText(Object element)
    {
      if (element instanceof URI)
      {
        return ((URI)element).toString();
      }

      if (element instanceof EClass)
      {
        return ((EClass)element).getName();
      }

      return super.getText(element);
    }

    @Override
    public Image getImage(Object element)
    {
      if (element instanceof URI)
      {
        return SharedIcons.getImage(SharedIcons.OBJ_EPACKAGE);
      }

      if (element instanceof EClass)
      {
        return SharedIcons.getImage(SharedIcons.OBJ_ECLASS);
      }

      return super.getImage(element);
    }
  }
}