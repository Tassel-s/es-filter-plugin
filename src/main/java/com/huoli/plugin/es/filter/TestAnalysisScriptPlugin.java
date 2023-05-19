package com.huoli.plugin.es.filter;

import com.huoli.plugin.es.filter.engine.MyScriptEngine;
import lombok.extern.log4j.Log4j2;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.plugins.Plugin;
import org.elasticsearch.plugins.ScriptPlugin;
import org.elasticsearch.script.ScriptContext;
import org.elasticsearch.script.ScriptEngine;

import java.util.Collection;

@Log4j2
public class TestAnalysisScriptPlugin extends Plugin implements ScriptPlugin {

    @Override
    public ScriptEngine getScriptEngine(Settings settings, Collection<ScriptContext<?>> contexts) {
        // System.out.println(String.format("contexts: %s", Arrays.toString(contexts.toArray())));

        return new MyScriptEngine();
    }
}
