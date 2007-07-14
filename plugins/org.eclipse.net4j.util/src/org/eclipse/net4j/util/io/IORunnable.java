package org.eclipse.net4j.util.io;

import java.io.Closeable;
import java.io.IOException;

/**
 * @author Eike Stepper
 */
public interface IORunnable<IO extends Closeable>
{
  public void run(IO io) throws IOException;
}