package org.Custom.Transformations.core;

import java.io.IOException;
import java.nio.file.Path;

public interface Outputable {
    void save(Path path) throws IOException;
    String getString();
}
