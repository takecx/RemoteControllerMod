package com.github.takecx.remotecontrollermod.lists;

import com.github.takecx.remotecontrollermod.stages.BaseStage;
import com.github.takecx.remotecontrollermod.stages.Stage1_1;

import java.util.HashMap;


public class StageList {
    static public HashMap<String, BaseStage> Stages = new HashMap<String, BaseStage>(){
        {
            put("1_1",new Stage1_1("1_1"));
        }
    };
}
