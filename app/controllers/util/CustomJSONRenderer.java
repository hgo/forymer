package controllers.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import javax.print.attribute.standard.Compression;

import play.Logger;
import play.classloading.enhancers.LocalvariablesNamesEnhancer.LocalVariablesNamesTracer;
import play.data.validation.Validation;
import play.exceptions.UnexpectedException;
import play.mvc.Http.Request;
import play.mvc.Http.Response;
import play.mvc.results.Result;
import play.templates.TemplateLoader;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class CustomJSONRenderer extends Result {

    private final String json;
    private final int status;
    
    public static class JsonBuilder{
        
//        AtomicInteger integer  = new AtomicInteger();
//        HashMap<String, Object> templatesMap = new HashMap<String, Object>();

//        public void buildAndRender(int statusCode,boolean isFailed){
//            renderCustomJson(isFailed, statusCode, templatesMap);
//        }

        public void buildAndRenderError(){
            renderCustomJson(true, 400, Validation.current().errorsMap());
        }
//        public JsonBuilder template(String templateName, Object... args) {
//            return template(null, templateName,args);
//        }
        
//        public JsonBuilder template(String templateName) {
//            return template(null, templateName);
//        }
        
        
//        public JsonBuilder string(Object... args) {
//        	HashMap<String, Object> map = new HashMap<String, Object>();
//        	if(args!=null){
//                for (Object o : args) {
//                    List<String> names = LocalVariablesNamesTracer.getAllLocalVariableNames(o);
//                    for (String s : names) {
//                        map.put(s, o);
//                    }
//                }    
//            }
//        	templatesMap.put(integer.getAndIncrement()+"", map);
//            return this;
//        }
            
//        public JsonBuilder template(String accessName,String templateName, Object... args) {
//            HashMap<String, Object> map = new HashMap<String, Object>();
//            if(args!=null){
//                for (Object o : args) {
//                    List<String> names = LocalVariablesNamesTracer.getAllLocalVariableNames(o);
//                    for (String s : names) {
//                        map.put(s, o);
//                    }
//                }    
//            }
//            if(accessName!=null && !accessName.isEmpty()){
//                templatesMap.put(accessName, Compression.compressHTML(TemplateLoader.load(templateName).render(map)));
//            }else{
//                templatesMap.put(integer.getAndIncrement()+"", Compression.compressHTML(TemplateLoader.load(templateName).render(map)));
//            }
//            return this;
//        }
        
//        public JsonBuilder template(String accessName,String templateName) {
//            return template(accessName, templateName, (Object)null);
//        }
    }
    
    public static JsonBuilder jsonBuilder(){
        return new JsonBuilder();
    }
    final static Gson gson = new GsonBuilder().setPrettyPrinting().create();
    
    private CustomJSONRenderer(boolean isF, int status,Map map) {
        HashMap<String, Object> resultMap = new HashMap<String, Object>();
        resultMap.put("isFailed", isF);
        resultMap.put("result", map);
        
        this.json = gson.toJson(resultMap);
        this.status = status;
        
    }

    private static void renderCustomJson(boolean isF, int status ,final Map map) {
        throw new CustomJSONRenderer(isF,status,map);
    }
    
    @Override
    public void apply(Request request, Response response) {
        try {
            response.status = this.status;
            String encoding = this.getEncoding();
            this.setContentTypeIfNotSet(response, "application/json; charset=" + encoding);
            Logger.info(this.json);
            response.out.write(this.json.getBytes(encoding));
        } catch (Exception e) {
            throw new UnexpectedException(e);
        }
    }

}
