package com.track.toy.copy;

public interface CopyCustom<T> {
    T copyCustom(T t);

    class DefaultCopyCustom implements CopyCustom<Object> {

        @Override
        public Object copyCustom(Object o) {
            return null;
        }
    }
}