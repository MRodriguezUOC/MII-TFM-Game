/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package edu.uoc.mii;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Application;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import edu.uoc.mii.utils.PlatformInput;

/**
 *
 * @author Marco Rodriguez
 */
public class HtmlPlatformInput implements PlatformInput {
    
    public void addStageTouchListener(Stage stage, TextField... fields){
        stage.addListener(new com.badlogic.gdx.scenes.scene2d.InputListener(){
            @Override
            public boolean touchDown(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y, int pointer, int button){
                boolean inside = false;
                for(TextField f : fields){
                    com.badlogic.gdx.math.Vector2 tmp = new com.badlogic.gdx.math.Vector2(x,y);
                    if(f.hit(tmp.x,tmp.y,true)!=null){
                        inside = true;
                        break;
                    }
                }
                if(!inside){
                    hide();
                }
                return false;
            }
        });
    }      

    @Override
    public void showInputFor(TextField field, boolean password, PlatformInput.InputCallback callback){
        if (Gdx.app.getType() != Application.ApplicationType.WebGL) {
            return;
        }
        com.badlogic.gdx.math.Vector2 tmp = new com.badlogic.gdx.math.Vector2(0,0);
        field.localToStageCoordinates(tmp);
        tmp = field.getStage().stageToScreenCoordinates(tmp);

        float x = tmp.x;
        float y = Gdx.graphics.getHeight() - tmp.y - field.getHeight();
        float w = field.getWidth();
        float h = field.getHeight();

        showNative(field.getText(), password, x, y, w, h, callback);
    }    
    
    @Override
    public void showInput(String defaultText, boolean password, InputCallback callback) {
        if (Gdx.app.getType() != Application.ApplicationType.WebGL) {
            return;
        }

        showNative(defaultText, password, 10, 40, 80, 40, callback);
    }
    
    @Override
    public void hide() {
        hideNative();
    }    
    
    private static native void hideNative() /*-{
        if ($wnd.__gdxInput) {
            try { $wnd.__gdxInput.blur(); } catch(e){}
            if ($wnd.__gdxInput.parentNode) $wnd.__gdxInput.parentNode.removeChild($wnd.__gdxInput);
            $wnd.__gdxInput = null;
        }
    }-*/;
        
    private static native void showNative(String value, boolean password, float x, float y, float w, float h, InputCallback callback) /*-{
        var input = document.createElement("input");
        $wnd.__gdxInput = input;

        input.type = password ? "password" : "text";
        input.value = value || "";
        input.setAttribute("dir","ltr");
        input.setAttribute("autocapitalize","off");
        input.setAttribute("autocorrect","off");
        input.setAttribute("autocomplete","off");
        input.setAttribute("spellcheck","false");
        if(!password) input.setAttribute("inputmode","email");

        input.style.position = "fixed";
        input.style.left = x + "px";
        input.style.top  = y + "px";
        input.style.width  = w + "px";
        input.style.height = h + "px";
        input.style.fontSize = Math.max(h*0.6,16) + "px";
        input.style.zIndex = 9999;
        input.style.border = "1px solid #333";
        input.style.borderRadius = "8px";
        input.style.backgroundColor = "#fff";
        input.style.color = "#000";

        document.body.appendChild(input);

        var isIOS = /iPad|iPhone|iPod/.test(navigator.userAgent) && !window.MSStream;

        if(isIOS){
            input.addEventListener("touchstart",function(e){ input.focus(); e.stopPropagation(); },{once:true});
            input.addEventListener("click",function(e){ input.focus(); e.stopPropagation(); },{once:true});
        }else{
            input.focus();
        }

        input.addEventListener("input", function(){
            callback.@edu.uoc.mii.utils.PlatformInput.InputCallback::onResult(Ljava/lang/String;)(input.value);
        });

        input.addEventListener("keydown", function(e){
            if(e.key==="Enter") input.blur();
        });

        input.addEventListener("blur", function(){
            if($wnd.__gdxInput!==input){ if(input.parentNode) input.parentNode.removeChild(input); return; }
            $wnd.__gdxInput=null;
            var val = input.value;
            if(input.parentNode) input.parentNode.removeChild(input);
            callback.@edu.uoc.mii.utils.PlatformInput.InputCallback::onResult(Ljava/lang/String;)(val);
        });

    }-*/;

}
