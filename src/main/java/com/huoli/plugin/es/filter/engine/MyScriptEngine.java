package com.huoli.plugin.es.filter.engine;

import com.huoli.plugin.es.filter.utils.Constants;
import com.huoli.plugin.es.filter.utils.ScriptConstants;
import org.elasticsearch.script.*;
import org.elasticsearch.search.lookup.SearchLookup;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;


public class MyScriptEngine implements ScriptEngine {
    private static String SCRIPT_SOURCE;

    @Override
    public String getType() {
        return Constants.LANG;
    }

    @Override
    public <T> T compile(String scriptName, String scriptSource, ScriptContext<T> context, Map<String, String> params) {
        if (!context.equals(FilterScript.CONTEXT)) {
            throw new IllegalArgumentException(getType() + " scripts cannot be used for context [" + context.name + "]");
        }
        MyScriptEngine.SCRIPT_SOURCE = scriptSource;
        // we use the script "source" as the script identifier
        FilterScript.Factory factory = new PureDfFactory();
        return context.factoryClazz.cast(factory);
    }

    @Override
    public void close() {
        // optionally close resources
    }

    @Override
    public Set<ScriptContext<?>> getSupportedContexts() {
        return Collections.singleton(FilterScript.CONTEXT);
    }

    private static class PureDfFactory implements FilterScript.Factory, ScriptFactory {
        @Override
        public boolean isResultDeterministic() {
            // PureDfLeafFactory only uses deterministic APIs, this
            // implies the results are cacheable.
            return true;
        }

        @Override
        public FilterScript.LeafFactory newFactory(Map<String, Object> params, SearchLookup lookup) {
            return new PureDfLeafFactory(params, lookup);
        }
    }

    private static class PureDfLeafFactory implements FilterScript.LeafFactory {

        private final Map<String, Object> params;
        private final SearchLookup lookup;

        public PureDfLeafFactory(Map<String, Object> params, SearchLookup lookup) {
            this.lookup = lookup;
            this.params = params;
        }

        public FilterScript newInstance(DocReader docReader) {
            Map<String, Object> ctx = new HashMap<>(docReader.docAsMap());

            return new FilterScript(ctx, lookup, docReader) {
                @Override
                public boolean execute() {

                    boolean flag = false;
                    switch (SCRIPT_SOURCE) {
                        case "pricerangelimit_v4":
                            flag = ScriptConstants.priceRangeLimit(ctx, params);
                            break;
                        default:
                            break;
                    }

                    return flag;
                }

                @Override
                public void setDocument(int doc) {
                    docReader.setDocument(doc);
                }
            };
        }
    }


}