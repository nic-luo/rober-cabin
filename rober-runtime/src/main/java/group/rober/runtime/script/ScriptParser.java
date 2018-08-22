package group.rober.runtime.script;

import group.rober.runtime.holder.ApplicationContextHolder;
import group.rober.runtime.kit.MapKit;
import group.rober.runtime.lang.RoberException;

import javax.script.Bindings;
import javax.script.ScriptEngine;
import javax.script.ScriptException;
import java.util.Map;

public class ScriptParser<T> {
    private ScriptEngine scriptEngine;

    public ScriptParser() {
    }

    public ScriptParser(ScriptEngine scriptEngine) {
        this.scriptEngine = scriptEngine;
    }

    public ScriptEngine getScriptEngine() {
        return scriptEngine;
    }

    public void setScriptEngine(ScriptEngine scriptEngine) {
        this.scriptEngine = scriptEngine;
    }

    public T parse(CharSequence expression, Map<String,Object> parameters) throws ScriptException {
        if(scriptEngine == null){
            scriptEngine = ApplicationContextHolder.getBean("scriptEngine",ScriptEngine.class);
        }

        if(scriptEngine == null){
            throw new RoberException("ScriptEngine对象不存在，请初始化springboot环境");
        }
        Bindings variables = scriptEngine.createBindings();
        variables.putAll(parameters);

        //执行
        Object result = scriptEngine.eval(expression.toString(),variables);
        return (T)result;
    }

    public T parse(CharSequence expression) throws ScriptException {
        return parse(expression, MapKit.newEmptyMap());
    }
}
