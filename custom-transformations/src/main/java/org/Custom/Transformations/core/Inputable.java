package org.Custom.Transformations.core;

import java.nio.file.Path;

public interface Inputable {
    void load(String value);
    void load(Path path);
}
