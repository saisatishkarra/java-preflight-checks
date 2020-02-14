package com.dataguise.saas.source;

import com.dg.saas.orch.models.structures.Modules;

public interface SourceFactory {
    Source getSource(String modules);
}
