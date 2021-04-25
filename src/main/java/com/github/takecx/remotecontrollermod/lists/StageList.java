package com.github.takecx.remotecontrollermod.lists;

import com.github.takecx.remotecontrollermod.stages.*;

import java.util.HashMap;


public class StageList {
    static public HashMap<String, BaseStage> Stages = new HashMap<String, BaseStage>(){
        {
            put("1_1",new Stage1_1("1_1",11,11,11));
            put("1_2",new Stage1_2("1_2",11,11,11));
            put("1_3",new Stage1_3("1_3",11,11,11));
            put("1_4",new Stage1_4("1_4",11,11,11));
        }
    };
}
