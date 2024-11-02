package org.eclipse.emf.cdo.lm.reviews.ui.actions;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Shell;

public class Snippet183
{

  public static void main(String[] args)
  {
    Display display = new Display();
    Shell shell = new Shell(display);
    shell.setText("Snippet 183");
    Link link = new Link(shell, SWT.NONE);
    String text = "The SWT component is designed to provide <a>efficient</a>, <a>portable</a> <a href=\"native\">access to the user-interface facilities of the operating systems</a> on which it is implemented.";
    link.setText(text);
    Rectangle clientArea = shell.getClientArea();
    link.setBounds(clientArea.x, clientArea.y, 400, 400);
    link.addListener(SWT.Selection, event -> System.out.println("Selection: " + event.text));
    shell.pack();
    shell.open();
    while (!shell.isDisposed())
    {
      if (!display.readAndDispatch())
      {
        display.sleep();
      }
    }
    display.dispose();
  }
}
