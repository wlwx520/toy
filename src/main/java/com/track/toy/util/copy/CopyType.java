package com.track.toy.util.copy;

public enum CopyType {
    SOURCE(false), TARGET(true);

    private boolean flg;

    CopyType(boolean flg) {
        this.flg = flg;
    }

    public boolean flg() {
        return flg;
    }
}
