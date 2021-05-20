package com.github.takecx.remotecontrollermod.messages;

public abstract class MessageBase {
    abstract public boolean isMessageValid();
    abstract public String message2String();
}
