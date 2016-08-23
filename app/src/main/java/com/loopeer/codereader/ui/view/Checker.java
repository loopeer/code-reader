package com.loopeer.codereader.ui.view;

public abstract class Checker {
    public interface CheckObserver{
        void check(boolean b);
    }

    CheckObserver mCheckObserver;

    public Checker(CheckObserver checkObserver) {
        mCheckObserver = checkObserver;
    }

    abstract boolean isEnable();
}
